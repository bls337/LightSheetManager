package org.micromanager.lightsheetmanager.model.acquisitions;

import org.micromanager.lightsheetmanager.api.AcquisitionSettings;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.data.ChannelMode;

/**
 * How many camera triggers the controller emits, and how many of those come back as images.
 * <p>
 * Two places need these numbers and they have to agree: the controller is programmed with them,
 * and the acquisition event stream is sized from them. AcqEngJ arms each camera for however many
 * events it merged, so a trigger the controller emits that no event expects leaves the camera
 * armed for a frame nobody claims, and an event with no trigger behind it waits forever.
 * Computing the numbers here rather than at either call site is what keeps the two in step.
 * <p>
 * Overlap camera mode is where the two counts come apart. The camera reads one frame out while
 * the next exposes, so getting N images out of N slice positions needs an N+1st trigger. That
 * extra trigger does return an image; the image is simply not wanted. It arrives once per
 * channel rather than once per volume, because the controller is told a slice count and a
 * channel count and multiplies them.
 */
public final class CameraTriggers {

    /**
     * This class should not be instantiated.
     */
    private CameraTriggers() {
        throw new AssertionError("Utility class; do not instantiate.");
    }

    /**
     * The slice count the controller is programmed with, which is one more than the number of
     * images wanted in overlap mode.
     */
    public static int slicesPerVolume(final AcquisitionSettings settings) {
        if (settings.cameraMode() == CameraMode.OVERLAP) {
            return settings.volume().slicesPerView() + 1;
        }
        return settings.volume().slicesPerView();
    }

    /**
     * How many triggers the controller emits per slice position.
     * <p>
     * Slice by slice channel switching moves the channel within a slice, so the controller fires
     * once per channel at each position. Every other channel arrangement fires once.
     */
    public static int channelsPerSlice(final AcquisitionSettings settings) {
        if (settings.channels().enabled() && settings.channels().mode() == ChannelMode.SLICE_HW) {
            return settings.channels().count();
        }
        return 1;
    }

    /**
     * Whether the controller emits more triggers than there are images wanted.
     * <p>
     * Only overlap mode does. The other camera modes return exactly one image per trigger, so
     * the trigger count and the image count are the same number and nothing has to be discarded.
     */
    public static boolean hasSurplusFrames(final AcquisitionSettings settings) {
        return settings.cameraMode() == CameraMode.OVERLAP;
    }

    /**
     * Whether a slice index addresses the surplus position rather than a wanted image.
     * <p>
     * The surplus position is the one past the last wanted slice, so it sits at the end of each
     * volume and carries one image per channel.
     */
    public static boolean isSurplusSlice(final AcquisitionSettings settings, final int sliceIndex) {
        return sliceIndex >= settings.volume().slicesPerView();
    }

}
