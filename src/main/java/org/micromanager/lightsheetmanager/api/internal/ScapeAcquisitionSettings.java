package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AcquisitionSettingsScape;
import org.micromanager.lightsheetmanager.api.ChannelSettings;
import org.micromanager.lightsheetmanager.api.StageScanSettings;
import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;
import org.micromanager.lightsheetmanager.api.data.CameraData;
import org.micromanager.lightsheetmanager.api.data.CameraMode;

public class ScapeAcquisitionSettings extends BaseAcquisitionSettings implements AcquisitionSettingsScape {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseAcquisitionSettings.Builder<Builder> implements AcquisitionSettingsScape.Builder<Builder> {

        private ChannelSettings.Builder channelBuilder_ = DefaultChannelSettings.builder();
        private DefaultTimingSettings.Builder timingBuilder_ = DefaultTimingSettings.builder();
        private DefaultVolumeSettings.Builder volumeBuilder_ = DefaultVolumeSettings.builder();
        private DefaultSliceSettings.Builder sliceBuilder_ = DefaultSliceSettings.builder();
        private StageScanSettings.Builder stageScanBuilder_ = DefaultStageScanSettings.builder();
        private DefaultSheetCalibration.Builder sheetCalibBuilder_ = DefaultSheetCalibration.builder();
        private DefaultSliceCalibration.Builder sliceCalibBuilder_ = DefaultSliceCalibration.builder();

        private AcquisitionMode acquisitionMode_ = AcquisitionMode.NO_SCAN;

        private CameraMode cameraMode_ = CameraMode.EDGE;
        private CameraData[] imagingCameraOrder_ = {};

        private boolean useTimePoints_ = false;
        private boolean useAutofocus_ = false;
        private boolean useMultiplePositions_ = false;
        private boolean useHardwareTimePoints_ = false;
        private boolean useStageScanning_ = false;
        private boolean useAdvancedTiming_ = false;

        private int numTimePoints_ = 1;
        private double timePointInterval_ = 0.0;
        private int postMoveDelay_ = 0;

        private Builder() {
        }

        public Builder(final ScapeAcquisitionSettings settings) {
            super(settings);
            channelBuilder_ = settings.channels().copyBuilder();
            timingBuilder_ = settings.timing().copyBuilder();
            volumeBuilder_ = settings.volume().copyBuilder();
            sliceBuilder_ = settings.slice().copyBuilder();
            stageScanBuilder_ = settings.stageScan().copyBuilder();
            sheetCalibBuilder_ = settings.sheetCalibration().copyBuilder();
            sliceCalibBuilder_ = settings.sliceCalibration().copyBuilder();
            acquisitionMode_ = settings.acquisitionMode();
            cameraMode_ = settings.cameraMode();
            imagingCameraOrder_ = settings.imagingCameraOrder();
            useTimePoints_ = settings.isUsingTimePoints();
            useMultiplePositions_ = settings.isUsingMultiplePositions();
            useHardwareTimePoints_ = settings.isUsingHardwareTimePoints();
            useStageScanning_ = settings.isUsingStageScanning();
            useAdvancedTiming_ =  settings.isUsingAdvancedTiming();
            numTimePoints_ = settings.numTimePoints();
            timePointInterval_ = settings.timePointInterval();
            postMoveDelay_ = settings.postMoveDelay();
        }

        /**
         * Set the mode for the acquisition.
         * <p>
         * If the mode is a stage scanning mode,
         * set internal stage scanning flag.
         *
         * @param mode the acquisition mode
         */
        @Override
        public Builder acquisitionMode(final AcquisitionMode mode) {
            acquisitionMode_ = mode;
            useStageScanning_ = mode == AcquisitionMode.STAGE_SCAN
                    || mode == AcquisitionMode.STAGE_SCAN_INTERLEAVED
                    || mode == AcquisitionMode.STAGE_SCAN_UNIDIRECTIONAL;
            return this;
        }

        /**
         * Sets the camera mode.
         *
         * @param mode the camera mode.
         */
        @Override
        public Builder cameraMode(final CameraMode mode) {
            cameraMode_ = mode;
            return this;
        }

        /**
         * Sets the imaging camera order.
         *
         * @param order the imaging camera order
         */
        @Override
        public Builder imagingCameraOrder(final CameraData[] order) {
            imagingCameraOrder_ = order;
            return this;
        }

        /**
         * Sets the acquisition to use time points.
         *
         * @param state true to use time points.
         */
        @Override
        public Builder useTimePoints(final boolean state) {
            useTimePoints_ = state;
            return this;
        }

        /**
         * Sets the acquisition to use multiple positions.
         *
         * @param state true to use multiple positions.
         */
        @Override
        public Builder useMultiplePositions(final boolean state) {
            useMultiplePositions_ = state;
            return this;
        }

        /**
         * Sets the acquisition to use hardware time points.
         *
         * @param state true to use time points.
         */
        @Override
        public Builder useHardwareTimePoints(final boolean state) {
            useHardwareTimePoints_ = state;
            return this;
        }

        /**
         * Sets the acquisition to use advanced timing settings.
         *
         * @param state true to use advanced timing settings
         */
        @Override
        public Builder useAdvancedTiming(final boolean state) {
            useAdvancedTiming_ = state;
            return this;
        }

        /**
         * Sets the number of time points.
         *
         * @param numTimePoints the number of time points
         */
        @Override
        public Builder numTimePoints(final int numTimePoints) {
            numTimePoints_ = numTimePoints;
            return this;
        }

        /**
         * Sets the time point interval between time points in seconds.
         *
         * @param timePointInterval the time point interval in seconds.
         */
        @Override
        public Builder timePointInterval(final double timePointInterval) {
            timePointInterval_ = timePointInterval;
            return this;
        }

        /**
         * Sets the delay after a move when using multiple positions.
         *
         * @param postMoveDelay the delay in milliseconds.
         */
        @Override
        public Builder postMoveDelay(final int postMoveDelay) {
            postMoveDelay_ = postMoveDelay;
            return this;
        }

        // getters for sub-builders
        public ChannelSettings.Builder channelBuilder() {
            return channelBuilder_;
        }

        public DefaultTimingSettings.Builder timingBuilder() {
            return timingBuilder_;
        }

        public DefaultVolumeSettings.Builder volumeBuilder() {
            return volumeBuilder_;
        }

        public DefaultSliceSettings.Builder sliceBuilder() {
            return sliceBuilder_;
        }

        public StageScanSettings.Builder stageScanBuilder() {
            return stageScanBuilder_;
        }

        public DefaultSheetCalibration.Builder sheetCalibrationBuilder() {
            return sheetCalibBuilder_;
        }

        public DefaultSliceCalibration.Builder sliceCalibrationBuilder() {
            return sliceCalibBuilder_;
        }

        public void timingBuilder(DefaultTimingSettings.Builder builder) {
            timingBuilder_ = builder;
        }

        public void volumeBuilder(DefaultVolumeSettings.Builder builder) {
            volumeBuilder_ = builder;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public ScapeAcquisitionSettings build() {
            return new ScapeAcquisitionSettings(this);
        }

        // TODO: finish toString with rest of properties
        @Override
        public String toString() {
            return String.format("[tsb_=%s]", timingBuilder_);
        }

    }

    private final ChannelSettings channels_;
    private final DefaultTimingSettings timing_;
    private final DefaultVolumeSettings volume_;
    private final DefaultSliceSettings slice_;
    private final StageScanSettings stageScan_;
    private final DefaultSheetCalibration sheetCalibration_;
    private final DefaultSliceCalibration sliceCalibration_;

    private final AcquisitionMode acquisitionMode_;

    private final CameraMode cameraMode_;
    private final CameraData[] imagingCameraOrder_;

    private final boolean useTimePoints_;
    private final boolean useMultiplePositions_;
    private final boolean useHardwareTimePoints_;
    private final boolean useStageScanning_;
    private final boolean useAdvancedTiming_;

    private final int numTimePoints_;
    private final double timePointInterval_;
    private final int postMoveDelay_;

    private ScapeAcquisitionSettings(Builder builder) {
        super(builder);
        channels_ = builder.channelBuilder().build();
        timing_ = builder.timingBuilder_.build();
        volume_ = builder.volumeBuilder_.build();
        slice_ = builder.sliceBuilder_.build();
        stageScan_ = builder.stageScanBuilder().build();
        sheetCalibration_ = builder.sheetCalibBuilder_.build();
        sliceCalibration_ = builder.sliceCalibBuilder_.build();
        acquisitionMode_ = builder.acquisitionMode_;
        cameraMode_ = builder.cameraMode_;
        imagingCameraOrder_ = builder.imagingCameraOrder_;
        useTimePoints_ = builder.useTimePoints_;
        useStageScanning_ = builder.useStageScanning_;
        useMultiplePositions_ = builder.useMultiplePositions_;
        useHardwareTimePoints_ = builder.useHardwareTimePoints_;
        useAdvancedTiming_ = builder.useAdvancedTiming_;
        numTimePoints_ = builder.numTimePoints_;
        timePointInterval_ = builder.timePointInterval_;
        postMoveDelay_ = builder.postMoveDelay_;
    }

//    /**
//     * Creates a Builder populated with settings of this DefaultAcquisitionSettingsSCAPE instance.
//     *
//     * @return DefaultAcquisitionSettingsSCAPE.Builder pre-populated with settings of this instance.
//     */
//    @Override
//    public DefaultAcquisitionSettingsSCAPE.Builder copyBuilder() {
//        return new Builder(this);
//    }

    /**
     * Returns the immutable DefaultChannelSettings instance.
     *
     * @return immutable DefaultChannelSettings instance.
     */
    @Override
    public ChannelSettings channels() {
        return channels_;
    }

    /**
     * Returns the immutable DefaultTimingSettings instance.
     *
     * @return immutable DefaultTimingSettings instance.
     */
    @Override
    public DefaultTimingSettings timing() {
        return timing_;
    }

    /**
     * Returns the immutable DefaultVolumeSettings instance.
     *
     * @return immutable DefaultVolumeSettings instance.
     */
    @Override
    public DefaultVolumeSettings volume() {
        return volume_;
    }

    /**
     * Returns the immutable DefaultSliceSettings instance.
     *
     * @return immutable DefaultSliceSettings instance.
     */
    @Override
    public DefaultSliceSettings slice() {
        return slice_;
    }

    /**
     * Returns the immutable DefaultStageScanSettings instance.
     *
     * @return immutable DefaultStageScanSettings instance.
     */
    @Override
    public StageScanSettings stageScan() {
        return stageScan_;
    }

    /**
     * Returns the immutable DefaultSheetCalibration instance.
     *
     * @return immutable DefaultSheetCalibration instance.
     */
    @Override
    public DefaultSheetCalibration sheetCalibration() {
        return sheetCalibration_;
    }

    /**
     * Returns an immutable DefaultSliceCalibration instance.
     * <p>
     * Views start at index 1.
     *
     * @return immutable DefaultSliceCalibration instance.
     */
    @Override
    public DefaultSliceCalibration sliceCalibration() {
        return sliceCalibration_;
    }

    /**
     * Returns the acquisition mode.
     *
     * @return the acquisition mode.
     */
    @Override
    public AcquisitionMode acquisitionMode() {
        return acquisitionMode_;
    }

    /**
     * Returns the camera mode.
     *
     * @return the camera mode.
     */
    @Override
    public CameraMode cameraMode() {
        return cameraMode_;
    }

    /**
     * Returns the imaging camera order.
     *
     * @return the imaging camera order
     */
    @Override
    public CameraData[] imagingCameraOrder() {
        return imagingCameraOrder_;
    }

    /**
     * Returns true if using time points.
     *
     * @return true if using time points.
     */
    @Override
    public boolean isUsingTimePoints() {
        return useTimePoints_;
    }

    /**
     * Returns true if using multiple positions.
     *
     * @return true if using multiple positions.
     */
    @Override
    public boolean isUsingMultiplePositions() {
        return useMultiplePositions_;
    }

    /**
     * Returns true if using hardware time points.
     *
     * @return true if using hardware time points.
     */
    @Override
    public boolean isUsingHardwareTimePoints() {
        return useHardwareTimePoints_;
    }

    /**
     * Returns true if using stage scanning.
     *
     * @return true if using stage scanning.
     */
    @Override
    public boolean isUsingStageScanning() {
        return useStageScanning_;
    }

    /**
     * Returns true if using advanced timing settings.
     *
     * @return true if using advanced timing settings.
     */
    @Override
    public boolean isUsingAdvancedTiming() {
        return useAdvancedTiming_;
    }

    /**
     * Returns the number of time points.
     *
     * @return the number of time points.
     */
    @Override
    public int numTimePoints() {
        return numTimePoints_;
    }

    /**
     * Returns the time point interval in seconds.
     *
     * @return the time point interval in seconds.
     */
    @Override
    public double timePointInterval() {
        return timePointInterval_;
    }

    /**
     * Returns the post move delay in milliseconds.
     *
     * @return the post move delay in milliseconds.
     */
    @Override
    public int postMoveDelay() {
        return postMoveDelay_;
    }

    // TODO: finish this, and maybe use pretty printing? or just rely on JSON conversion?
    @Override
    public String toString() {
        return String.format("[timingSettings_=%s]", timing_);
    }

//    public String toJson() {
//        return new Gson().toJson(this);
//    }

//    public String toPrettyJson() {
//        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        return gson.toJson(this);
//    }

//    public static DefaultAcquisitionSettingsDISPIM fromJson(final String json) {
//        return new Gson().fromJson(json, DefaultAcquisitionSettingsDISPIM.class);
//    }
}
