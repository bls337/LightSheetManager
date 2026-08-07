package org.micromanager.lightsheetmanager.model.acquisitions;

import org.micromanager.acqj.main.AcquisitionEvent;
import org.micromanager.lightsheetmanager.api.AcquisitionSettings;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Accounts for the images overlap camera mode delivers that the dataset does not want.
 * <p>
 * Under hardware time points the controller runs every time point from one start trigger, so the
 * cameras are armed once for the whole burst and every frame in it arrives on the same sequence.
 * In overlap mode that burst carries one unwanted image per channel per time point, sitting at the
 * end of each volume where the controller took the extra slice position. Those images are real and
 * have to be pulled off the camera; they simply do not belong in the dataset.
 * <p>
 * AcqEngJ arms each camera for however many events it merged and pops one event per frame, so the
 * event stream has to describe the frames the camera <b>delivers</b>, not the frames the dataset
 * <b>keeps</b>. Describing only the kept frames leaves the surplus images filed in the following
 * time point's first slots, shifts everything after them, and loses as many real frames off the end
 * as were displaced. Nothing errors, because the totals still match.
 * <p>
 * So the stream carries an event for every delivered frame, and the ones standing in for surplus
 * images are marked. Marking them is what lets the image be discarded after the event has been
 * consumed, which is the only order that keeps the remaining events lined up with the remaining
 * frames.
 * <p>
 * The burst's very last trigger is the exception: a frame reads out on the trigger after the one
 * that exposed it, and the last trigger has no following one, so that frame never arrives at all.
 * Its event is dropped rather than marked, since an event with no frame behind it leaves the camera
 * armed and waiting forever.
 */
public final class SurplusFrames {

    /**
     * This class should not be instantiated.
     */
    private SurplusFrames() {
        throw new AssertionError("Utility class; do not instantiate.");
    }

    /**
     * Resize an event stream from the images wanted to the frames the camera delivers.
     * <p>
     * Returns the stream unchanged where trigger count and image count already agree, which is
     * every camera mode but overlap.
     *
     * @param events   the composed event stream, in delivery order
     * @param settings the settings the controller was programmed from
     */
    public static Iterator<AcquisitionEvent> wrap(
            final Iterator<AcquisitionEvent> events, final AcquisitionSettings settings) {

        if (!CameraTriggers.hasSurplusFrames(settings)) {
            return events;
        }

        return new Iterator<>() {

            private AcquisitionEvent nextEvent_ = null;
            private boolean primed_ = false;

            private void prime() {
                if (primed_) {
                    return;
                }
                primed_ = true;
                while (events.hasNext()) {
                    final AcquisitionEvent candidate = events.next();
                    // The engine tolerates a null event and skips it, so this has to as well:
                    // a composed axis with nothing in it yields one.
                    if (candidate == null) {
                        continue;
                    }
                    if (!isNeverDelivered(settings, candidate)) {
                        nextEvent_ = candidate;
                        return;
                    }
                }
                nextEvent_ = null;
            }

            @Override
            public boolean hasNext() {
                prime();
                return nextEvent_ != null;
            }

            @Override
            public AcquisitionEvent next() {
                prime();
                if (nextEvent_ == null) {
                    throw new NoSuchElementException();
                }
                final AcquisitionEvent event = nextEvent_;
                primed_ = false;
                nextEvent_ = null;
                return isSurplus(settings, event) ? markSurplus(event) : event;
            }
        };
    }

    /**
     * Whether this event stands in for an image the controller delivers but the dataset does not
     * want.
     */
    public static boolean isSurplus(
            final AcquisitionSettings settings, final AcquisitionEvent event) {
        final Integer sliceIndex = event.getZIndex();
        return sliceIndex != null && CameraTriggers.isSurplusSlice(settings, sliceIndex);
    }

    /**
     * Whether this event stands in for the burst's last trigger, which reads out no frame.
     * <p>
     * That is the last channel of the surplus slice of the last time point, and it is that on every
     * camera, because the cameras are triggered together.
     */
    public static boolean isNeverDelivered(
            final AcquisitionSettings settings, final AcquisitionEvent event) {

        final Integer timeIndex = event.getTIndex();
        final Integer sliceIndex = event.getZIndex();
        if (timeIndex == null || sliceIndex == null) {
            return false;
        }
        if (timeIndex != settings.numTimePoints() - 1) {
            return false;
        }
        if (sliceIndex != CameraTriggers.slicesPerVolume(settings) - 1) {
            return false;
        }
        final int channelsPerSlice = CameraTriggers.channelsPerSlice(settings);
        return channelWithinCamera(event, channelsPerSlice) == channelsPerSlice - 1;
    }

    /**
     * Recover the channel index from the combined channel and camera slot the event carries.
     * <p>
     * Channel varies fastest in that slot, so the channel is what survives the remainder.
     * <p>
     * Both failures throw rather than falling back to zero. A fallback looks harmless and is not:
     * zero only matches the last channel when there is one channel, so with more than one it makes
     * the caller decide the event is delivered, the event whose frame never arrives is kept, and the
     * camera is armed for one more frame than the controller produces. That is exactly the wait that
     * never ends. Throwing costs an acquisition; returning zero costs an unrecoverable hang.
     */
    private static int channelWithinCamera(final AcquisitionEvent event, final int channelsPerSlice) {
        final Object slot = event.getAxisPosition(LightSheetEventAdapter.CAMERA_AXIS);
        if (slot == null) {
            throw new IllegalStateException("event carries no " + LightSheetEventAdapter.CAMERA_AXIS
                    + " coordinate, so the trigger that returns no frame cannot be identified");
        }
        try {
            return Integer.parseInt(slot.toString()) % channelsPerSlice;
        } catch (NumberFormatException e) {
            throw new IllegalStateException("expected a numeric "
                    + LightSheetEventAdapter.CAMERA_AXIS + " coordinate, found \"" + slot + "\"", e);
        }
    }

    /**
     * Mark an event so the image it consumes is discarded instead of filed.
     * <p>
     * The engine drops the image after popping this event and before stamping it, which is the only
     * order that leaves the remaining events lined up with the remaining frames. The event still
     * consumes its frame; only the filing is skipped.
     */
    private static AcquisitionEvent markSurplus(final AcquisitionEvent event) {
        event.setDiscardImage(true);
        return event;
    }
}
