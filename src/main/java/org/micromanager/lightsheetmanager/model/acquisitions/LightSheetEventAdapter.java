package org.micromanager.lightsheetmanager.model.acquisitions;

import org.micromanager.MultiStagePosition;
import org.micromanager.PositionList;
import org.micromanager.acqj.internal.Engine;
import org.micromanager.acqj.main.AcqEngMetadata;
import org.micromanager.acqj.main.AcquisitionEvent;
import org.micromanager.acqj.util.AcquisitionEventIterator;
import org.micromanager.lightsheetmanager.api.AcquisitionSettings;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Adapts {@code LightSheetManager} settings into {@code AcqEngJ} instructions.
 * <p>
 * This class translates {@link AcquisitionSettings} into lazy sequences
 * ({@link Iterator}s) of {@link AcquisitionEvent}s, based on the current
 * {@code LightSheetManager} configuration.
 */
public final class LightSheetEventAdapter {

    // These strings are MM's, not ours: AcqEngJ keys event coordinates on them and the datastore and
    // viewer size/display dimensions by them, so the VALUES cannot be changed; an axis MM does not
    // know is dropped by TIFF storage and never displayed.
    // CAMERA_AXIS is deliberately AcqEngJ's "channel" axis: LSM packs the combined
    // (channelIndex * numCameras + cameraIndex) slot into it, which is why the name here says camera
    // while the value says channel. Note setChannelName() writes this same axis.
    public static final String TIME_AXIS = "time";
    public static final String POSITION_AXIS = "position";
    public static final String CAMERA_AXIS = "channel";

    public static boolean isUsingMultipleCameras = false;

    /**
     * This class should not be instantiated.
     */
    private LightSheetEventAdapter() {
        throw new AssertionError("Utility class; do not instantiate.");
    }

    public static Iterator<AcquisitionEvent> createTimelapseMultiChannelVolumeAcqEvents(
            AcquisitionEvent baseEvent, AcquisitionSettings settings,
            String[] cameraDeviceNames,
            Function<AcquisitionEvent, AcquisitionEvent> eventMonitor) {

        if (settings.numTimePoints() <= 1) {
            throw new RuntimeException("timelapse selected but only one timepoint");
        }
        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> timelapse =
                timelapse(settings.numTimePoints(), settings.timePointInterval());

        if (settings.channels().count() == 1) {
            throw new RuntimeException("Expected multiple channels but only one found");
        }

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> channels =
                channels(settings.channels().used());

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack =
                zStack(0, settings.volume().slicesPerView());

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> cameras = cameras(cameraDeviceNames);

        ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions = new ArrayList<>();
        acqFunctions.add(timelapse);
        acqFunctions.add(channels);
        acqFunctions.add(cameras);
        acqFunctions.add(zStack);
        return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
    }

    public static Iterator<AcquisitionEvent> createTimelapseVolumeAcqEvents(
            AcquisitionEvent baseEvent, AcquisitionSettings settings,
            String[] cameraDeviceNames,
            Function<AcquisitionEvent, AcquisitionEvent> eventMonitor) {

        if (settings.numTimePoints() <= 1) {
            throw new RuntimeException("timelapse selected but only one timepoint");
        }
        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> timelapse =
                timelapse(settings.numTimePoints(), null);

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> cameras = cameras(cameraDeviceNames);

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack =
                zStack(0, settings.volume().slicesPerView());

        ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions = new ArrayList<>();
        acqFunctions.add(timelapse);
        acqFunctions.add(cameras);
        acqFunctions.add(zStack);
        return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
    }

    /**
     *
     * @param interleaved true: do we want to do every channel at each z slice before moving to
     *                    the next z slice
     *                    false: do an entire volume in one channel, then the next one
     */
    public static Iterator<AcquisitionEvent> createMultiChannelVolumeAcqEvents(
            AcquisitionEvent baseEvent, AcquisitionSettings settings,
            String[] cameraDeviceNames,
            Function<AcquisitionEvent, AcquisitionEvent> eventMonitor, boolean interleaved) {

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> channels =
                channels(settings.channels().used());

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack =
                zStack(0, settings.volume().slicesPerView());

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> cameras = cameras(cameraDeviceNames);

        ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions = new ArrayList<>();
        if (interleaved) {
            acqFunctions.add(cameras);
            acqFunctions.add(zStack);
            acqFunctions.add(channels);
        } else {
            acqFunctions.add(channels);
            acqFunctions.add(cameras);
            acqFunctions.add(zStack);
        }
        return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
    }

    public static Iterator<AcquisitionEvent> createChannelAcqEvents(
            AcquisitionEvent baseEvent, AcquisitionSettings settings,
            String[] cameraDeviceNames,
            Function<AcquisitionEvent, AcquisitionEvent> eventMonitor) {

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> channels =
                channels(settings.channels().used());

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> cameras = cameras(cameraDeviceNames);

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack =
                zStack(0, settings.volume().slicesPerView());

        ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions = new ArrayList<>();
        acqFunctions.add(channels);
        acqFunctions.add(cameras);
        acqFunctions.add(zStack);
        return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
    }

    public static Iterator<AcquisitionEvent> createAcqEvents(
            AcquisitionEvent baseEvent, AcquisitionSettings settings,
            String[] cameraDeviceNames,
            Function<AcquisitionEvent, AcquisitionEvent> eventMonitor) {

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> cameras = cameras(cameraDeviceNames);

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack =
                zStack(0, settings.volume().slicesPerView());

        ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions = new ArrayList<>();
        acqFunctions.add(cameras);
        acqFunctions.add(zStack);
        return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
    }

    /**
     * Build events for ONE software channel's volume, as a self-contained iterator.
     * <p>
     * {@code run()} calls this once per used channel, so each channel is submitted as
     * its own event iterator. AcqEngJ appends a SequenceEnd flush at the end of every
     * submitted iterator, guaranteeing exactly one camera sequence + one controller fire per
     * channel-volume regardless of whether the channel presets are identical, distinct, or
     * property-sequenceable. Composing all channels into a single iterator (the old
     * {@link #createChannelAcqEvents}) instead lets AcqEngJ merge identical-preset channels into one
     * sequence, firing the controller once and collapsing the channel dimension.
     *
     * @param channelIndex zero-based index of this channel among the used channels
     * @param channel      the channel to acquire
     */
    public static Iterator<AcquisitionEvent> createSingleChannelVolumeAcqEvents(
            AcquisitionEvent baseEvent, AcquisitionSettings settings,
            String[] cameraDeviceNames,
            Function<AcquisitionEvent, AcquisitionEvent> eventMonitor,
            int channelIndex, ChannelSpec channel) {

        // Bake this one channel's config into the base event (what channels().next() does per channel).
        baseEvent.setConfigGroup(channel.getGroup());
        baseEvent.setConfigPreset(channel.getName());
        baseEvent.setChannelName(Integer.toString(channelIndex));

        // Channel z-offset: mirror channels(), apply the offset to the current stage/z position.
        double zPos;
        if (baseEvent.getZPosition() == null) {
            try {
                zPos = Engine.getCore().getPosition() + channel.getOffset();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            zPos = baseEvent.getZPosition() + channel.getOffset();
        }
        baseEvent.setZ(baseEvent.getZIndex(), zPos);

        // cameras() would now derive the same base from CAMERA_AXIS (setChannelName above put the
        // channel index there); passing it explicitly keeps this path independent of that coupling.
        final int channelAxisBase = isUsingMultipleCameras
                ? channelIndex * cameraDeviceNames.length : channelIndex;

        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> cameras =
                cameras(cameraDeviceNames, channelAxisBase);
        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack =
                zStack(0, settings.volume().slicesPerView());

        ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions = new ArrayList<>();
        acqFunctions.add(cameras);
        acqFunctions.add(zStack);
        return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
    }

    /**
     * Build events for one volume whose channels the controller switches slice by slice.
     * <p>
     * <b>Slice-by-slice switching only.</b> Volume-by-volume switching changes channel once per
     * volume and needs the opposite axis order; see {@link #createChannelPerVolumeAcqEvents}. Using
     * this factory for it would not fail, since the event count still matches; the images would
     * simply be filed against the wrong coordinates.
     * <p>
     * The controller emits every channel within a single armed run, so the event stream has to
     * describe one sequence rather than one per channel. Two things follow from that.
     * <p>
     * The events carry no per-channel config preset. A preset that differs between consecutive events
     * makes AcqEngJ break the merge and start one more camera sequence than the controller was armed
     * to deliver, so the extra sequence waits for frames that never arrive. Presets also move whatever
     * devices they name, such as filter wheels, which cannot follow a controller switching channels
     * at slice rate.
     * <p>
     * The channel axis is innermost because the controller interleaves channels within a slice, and
     * AcqEngJ assigns each image to the first event matching that camera in sequence order, so the
     * event order per camera has to match the order the camera delivers frames.
     * <p>
     * A single channel is the exception. Hardware channel switching is not set up at all for one
     * channel, so that channel's own preset and offset apply, as they do on the software path.
     * Baking them into the base event leaves every event carrying the same preset, which cannot
     * split the merge, since a sequence only breaks where consecutive presets differ.
     *
     * @param usedChannels the channels the controller was programmed for
     */
    public static Iterator<AcquisitionEvent> createChannelPerSliceAcqEvents(
            AcquisitionEvent baseEvent, AcquisitionSettings settings,
            String[] cameraDeviceNames, ChannelSpec[] usedChannels,
            Function<AcquisitionEvent, AcquisitionEvent> eventMonitor) {

        if (usedChannels.length == 1) {
            final ChannelSpec channel = usedChannels[0];
            baseEvent.setConfigGroup(channel.getGroup());
            baseEvent.setConfigPreset(channel.getName());

            // Channel z-offset: mirror channels(), apply the offset to the current stage/z position.
            double zPos;
            if (baseEvent.getZPosition() == null) {
                try {
                    zPos = Engine.getCore().getPosition() + channel.getOffset();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                zPos = baseEvent.getZPosition() + channel.getOffset();
            }
            baseEvent.setZ(baseEvent.getZIndex(), zPos);
        }

        // Base 0 so cameras() leaves the plain camera index on the axis; channelAxis() runs
        // innermost and folds it into the combined slot.
        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> cameras =
                cameras(cameraDeviceNames, 0);
        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack =
                zStack(0, settings.volume().slicesPerView());
        Function<AcquisitionEvent, Iterator<AcquisitionEvent>> channels =
                channelAxis(usedChannels.length, cameraDeviceNames.length);

        ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions = new ArrayList<>();
        acqFunctions.add(cameras);
        acqFunctions.add(zStack);
        acqFunctions.add(channels);
        return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
    }

    /**
     * Build events for a volume whose channels the controller switches volume by volume.
     * <p>
     * <b>Not implemented.</b> Present so the gap is visible next to
     * {@link #createChannelPerSliceAcqEvents} rather than being rediscovered.
     * <p>
     * This mode switches channel once per volume instead of once per slice, so the controller
     * delivers every slice of one channel before it starts the next channel. The channel axis
     * therefore has to sit <b>outside</b> the z axis, the opposite of the slice-by-slice factory,
     * and the combined axis slot cannot be derived the way {@link #channelAxis} derives it, since
     * the cameras fan out below the channels rather than above them. Getting the order wrong does
     * not fail: the event count still matches, so the images are simply filed against the wrong
     * coordinates.
     * <p>
     * A single view geometry cannot use this at all, because the controller clocks its channel
     * counter from the view select signal, which never alternates with one view. The two view
     * geometry is what this is for, and its event submission is currently disabled, so nothing
     * calls this yet. Implement it against two view hardware rather than from this description.
     */
    public static Iterator<AcquisitionEvent> createChannelPerVolumeAcqEvents(
            AcquisitionEvent baseEvent, AcquisitionSettings settings,
            String[] cameraDeviceNames, ChannelSpec[] usedChannels,
            Function<AcquisitionEvent, AcquisitionEvent> eventMonitor) {
        throw new UnsupportedOperationException(
                "volume-by-volume hardware channel switching is not implemented; "
                        + "it needs the channel axis outside the z axis, unlike "
                        + "createChannelPerSliceAcqEvents");
    }

    /**
     * Fan an event out over controller-switched channels, writing only the {@link #CAMERA_AXIS}
     * coordinate.
     * <p>
     * Deliberately sets no config group or preset, and no channel z-offset: the controller owns
     * channel switching here, and the offset stays wherever the user's own preset left it.
     * Runs innermost, after {@link #cameras}, so the value already on the axis is the camera index;
     * it is replaced with the combined slot {@code channelIndex * numCameras + cameraIndex} that
     * sizes and names the datastore.
     *
     * @param numChannels the number of channels the controller was programmed for
     * @param numCameras  the number of cameras this event fans out over
     */
    public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> channelAxis(
            int numChannels, int numCameras) {
        return (AcquisitionEvent event) -> new Iterator<>() {

            private int channelIndex_ = 0;

            @Override
            public boolean hasNext() {
                return channelIndex_ < numChannels;
            }

            @Override
            public AcquisitionEvent next() {
                AcquisitionEvent channelEvent = event.copy();

                Object position = event.getAxisPosition(CAMERA_AXIS);
                int cameraIndex = 0;
                if (position != null) {
                    try {
                        cameraIndex = Integer.parseInt(position.toString());
                    } catch (NumberFormatException e) {
                        // ignore => number already assigned
                    }
                }

                channelEvent.setAxisPosition(CAMERA_AXIS, channelIndex_ * numCameras + cameraIndex);
                channelIndex_++;
                return channelEvent;
            }
        };
    }

    /**
     * Fan an event out over the cameras, deriving the channel-axis base from the event.
     * <p>
     * Used by the {@link #channels}-composed factories.
     */
    public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> cameras(String[] cameraDeviceNames) {
        return cameras(cameraDeviceNames, null);
    }

    /**
     * Fan an event out over the cameras, assigning each one a {@link #CAMERA_AXIS} coordinate.
     * <p>
     * <b>{@code CAMERA_AXIS} is AcqEngJ's {@code "channel"} axis</b> ({@code AcqEngMetadata.CHANNEL_AXIS}):
     * {@code AcquisitionEvent.setChannelName(s)} compiles to {@code setAxisPosition("channel", s)}. So the
     * channel index and the camera index share one axis, and the coordinate written here is the combined
     * slot {@code channelIndex * numCameras + cameraIndex} that {@code addMMSummaryMetadata} names and
     * sizes the datastore for. Do not treat a value already present on that axis as a camera base unless
     * you put it there: on the {@code channels()}-composed paths it is the raw channel index written by
     * {@code setChannelName}.
     *
     * @param channelAxisBase the channel-axis base for this fan-out, or {@code null} to derive it from
     *                        the event: the channel index {@code channels()} left on the axis, times
     *                        the camera count when using multiple cameras
     */
    public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> cameras(
            String[] cameraDeviceNames, Integer channelAxisBase) {
        return (AcquisitionEvent event) -> new Iterator<>() {

            private int cameraIndex_ = 0;
            private final String[] cameraDeviceNames_ = cameraDeviceNames;

            @Override
            public boolean hasNext() {
                return cameraIndex_ < cameraDeviceNames_.length;
            }

            @Override
            public AcquisitionEvent next() {
                AcquisitionEvent cameraEvent = event.copy();
                cameraEvent.setCameraDeviceName(cameraDeviceNames_[cameraIndex_]);

                final int baseIndex;
                if (channelAxisBase != null) {
                    // Caller-supplied base (createSingleChannelVolumeAcqEvents, per-channel path).
                    baseIndex = channelAxisBase;
                } else {
                    // The base is the channel index channels() wrote onto this axis via
                    // setChannelName (absent => 0 when channels are disabled). Read it off the event
                    // rather than from shared state: run() submits one iterator per position per
                    // timepoint eagerly, and AcqEngJ consumes them lazily and interleaved.
                    Object position = event.getAxisPosition(CAMERA_AXIS);
                    int parsed = 0;
                    if (position != null) {
                        try {
                            parsed = Integer.parseInt(position.toString());
                        } catch (NumberFormatException e) {
                            // ignore => number already assigned
                        }
                    }
                    baseIndex = isUsingMultipleCameras
                            ? parsed * cameraDeviceNames_.length : parsed;
                }

                cameraEvent.setAxisPosition(CAMERA_AXIS, baseIndex + cameraIndex_);
                cameraIndex_++;
                return cameraEvent;
            }
        };
    }

    public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack(
            int startSliceIndex, int stopSliceIndex) {
        return (AcquisitionEvent event) -> new Iterator<>() {

            private int zIndex_ = startSliceIndex;

            @Override
            public boolean hasNext() {
                return zIndex_ < stopSliceIndex;
            }

            @Override
            public AcquisitionEvent next() {
                AcquisitionEvent sliceEvent = event.copy();
                sliceEvent.setAxisPosition(AcqEngMetadata.Z_AXIS, zIndex_);
                // System.out.println("Final Event Axes: " + sliceEvent.getAxesAsJSONString());
                // The tiger controller handles Z axis, so no need to add the actual Z position
                zIndex_++;
                return sliceEvent;
            }
        };
    }

    public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> timelapse(
            int numTimePoints, Double intervalMs) {
        return (AcquisitionEvent event) -> new Iterator<>() {

            int frameIndex_ = 0;

            @Override
            public boolean hasNext() {
                return frameIndex_ == 0 || frameIndex_ < numTimePoints;
            }

            @Override
            public AcquisitionEvent next() {
                AcquisitionEvent timePointEvent = event.copy();
                if (intervalMs != null) {
                    timePointEvent.setMinimumStartTime((long) (intervalMs * frameIndex_));
                }
                timePointEvent.setTimeIndex(frameIndex_);
                frameIndex_++;
                return timePointEvent;
            }
        };
    }

    /**
     * Make an iterator for events for each active channel
     *
     * @param channelList the list of channels to iterate over
     * @return
     */
    public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> channels(
            ChannelSpec[] channelList) {
        return (AcquisitionEvent event) -> new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < channelList.length;
            }

            @Override
            public AcquisitionEvent next() {
                AcquisitionEvent channelEvent = event.copy();
                channelEvent.setConfigGroup(channelList[index].getGroup());
                channelEvent.setConfigPreset(channelList[index].getName());
                channelEvent.setChannelName(Integer.toString(index));

                double zPos;
                if (channelEvent.getZPosition() == null) {
                    try {
                        zPos = Engine.getCore().getPosition() + channelList[index].getOffset();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    zPos = channelEvent.getZPosition() + channelList[index].getOffset();
                }
                channelEvent.setZ(channelEvent.getZIndex(), zPos);

                // TODO: do channels have different exposures?
//               channelEvent.setExposure(channelList.get(index).exposure());
                index++;
                return channelEvent;
            }
        };
    }

    /**
     * Iterate over an arbitrary list of positions. Adds in position indices to
     * the axes that assume the order in the list provided correspond to the
     * desired indices
     *
     * @param positionList the list of positions or iterate over
     * @return
     */
    public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> positions(
            PositionList positionList) {
        return (AcquisitionEvent event) -> new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < positionList.getNumberOfPositions();
            }

            @Override
            public AcquisitionEvent next() {
                //System.out.println("called! " + index);
                AcquisitionEvent posEvent = event.copy();
                MultiStagePosition msp = positionList.getPosition(index);
                if (msp != null) {
                    posEvent.setX(msp.getX());
                    posEvent.setY(msp.getY());
                }
                posEvent.setAxisPosition(POSITION_AXIS, index);

                index++;
                return posEvent;
            }
        };
    }

//   /**
//    * Iterate over an arbitrary list of positions. Adds in position indices to
//    * the axes that assume the order in the list provided correspond to the
//    * desired indices
//    *
//    * @param positionList
//    * @return
//    */
//   public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> positions(
//           PositionList positionList) {
//      return (AcquisitionEvent event) -> {
//         Stream.Builder<AcquisitionEvent> builder = Stream.builder();
//         if (positionList == null) {
//            builder.accept(event);
//         } else {
//            for (int index = 0; index < positionList.getNumberOfPositions(); index++) {
//               AcquisitionEvent posEvent = event.copy();
//               MultiStagePosition msp = positionList.getPosition(index);
//               posEvent.setX(msp.getX());
//               posEvent.setY(msp.getY());
//               posEvent.setAxisPosition(POSITION_AXIS, index);
//               builder.accept(posEvent);
//            }
//         }
//         return builder.build().iterator();
//      };
//   }

}
