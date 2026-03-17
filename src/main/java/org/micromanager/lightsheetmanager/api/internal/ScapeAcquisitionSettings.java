package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AcquisitionSettingsScape;
import org.micromanager.lightsheetmanager.api.StageScanSettings;
import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;
import org.micromanager.lightsheetmanager.api.data.CameraData;
import org.micromanager.lightsheetmanager.api.data.CameraMode;

public class ScapeAcquisitionSettings extends BaseAcquisitionSettings implements AcquisitionSettingsScape {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseAcquisitionSettings.Builder<Builder> implements AcquisitionSettingsScape.Builder<Builder> {

        private DefaultChannelSettings.Builder channelBuilder_ = DefaultChannelSettings.builder();
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
            channelBuilder_ = settings.channelSettings_.copyBuilder();
            timingBuilder_ = settings.timingSettings_.copyBuilder();
            volumeBuilder_ = settings.volumeSettings_.copyBuilder();
            sliceBuilder_ = settings.sliceSettings_.copyBuilder();
            stageScanBuilder_ = settings.stageScan().copyBuilder();
            sheetCalibBuilder_ = settings.sheetCalibrations_.copyBuilder();
            sliceCalibBuilder_ = settings.sliceCalibrations_.copyBuilder();
            acquisitionMode_ = settings.acquisitionMode_;
            cameraMode_ = settings.cameraMode_;
            imagingCameraOrder_ = settings.imagingCameraOrder_;
            useTimePoints_ = settings.useTimePoints_;
            useAutofocus_ = settings.useAutofocus_;
            useMultiplePositions_ = settings.useMultiplePositions_;
            useHardwareTimePoints_ = settings.useHardwareTimePoints_;
            useStageScanning_ = settings.useStageScanning_;
            useAdvancedTiming_ =  settings.useAdvancedTiming_;
            numTimePoints_ = settings.numTimePoints_;
            timePointInterval_ = settings.timePointInterval_;
            postMoveDelay_ = settings.postMoveDelay_;
        }

        /**
         * Set the mode for the acquisition.
         * <p>
         * If the mode is a stage scanning mode,
         * set internal stage scanning flag.
         *
         * @param acqMode the acquisition mode
         */
        @Override
        public Builder acquisitionMode(final AcquisitionMode acqMode) {
            acquisitionMode_ = acqMode;
            useStageScanning_ = acqMode == AcquisitionMode.STAGE_SCAN
                    || acqMode == AcquisitionMode.STAGE_SCAN_INTERLEAVED
                    || acqMode == AcquisitionMode.STAGE_SCAN_UNIDIRECTIONAL;
            return this;
        }

        /**
         * Sets the camera mode.
         *
         * @param cameraMode the camera mode.
         */
        @Override
        public Builder cameraMode(final CameraMode cameraMode) {
            cameraMode_ = cameraMode;
            return this;
        }

        /**
         * Sets the imaging camera order.
         *
         * @param cameraOrder the imaging camera order
         */
        @Override
        public Builder imagingCameraOrder(final CameraData[] cameraOrder) {
            imagingCameraOrder_ = cameraOrder;
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
         * Sets the acquisition to use autofocus.
         *
         * @param state true to use autofocus.
         */
        @Override
        public Builder useAutofocus(final boolean state) {
            useAutofocus_ = state;
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
        public DefaultChannelSettings.Builder channelBuilder() {
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

        public DefaultSheetCalibration.Builder sheetCalibrationBuilder(final int view) {
            return sheetCalibBuilder_;
        }

        public DefaultSliceCalibration.Builder sliceCalibrationBuilder(final int view) {
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

    private final DefaultChannelSettings channelSettings_;
    private final DefaultTimingSettings timingSettings_;
    private final DefaultVolumeSettings volumeSettings_;
    private final DefaultSliceSettings sliceSettings_;
    private final StageScanSettings stageScan_;
    private final DefaultSheetCalibration sheetCalibrations_;
    private final DefaultSliceCalibration sliceCalibrations_;

    private final AcquisitionMode acquisitionMode_;

    private final CameraMode cameraMode_;
    private final CameraData[] imagingCameraOrder_;

    private final boolean useTimePoints_;
    private final boolean useAutofocus_;
    private final boolean useMultiplePositions_;
    private final boolean useHardwareTimePoints_;
    private final boolean useStageScanning_;
    private final boolean useAdvancedTiming_;

    private final int numTimePoints_;
    private final double timePointInterval_;
    private final int postMoveDelay_;

    private ScapeAcquisitionSettings(Builder builder) {
        super(builder);
        channelSettings_ = builder.channelBuilder_.build();
        timingSettings_ = builder.timingBuilder_.build();
        volumeSettings_ = builder.volumeBuilder_.build();
        sliceSettings_ = builder.sliceBuilder_.build();
        stageScan_ = builder.stageScanBuilder().build();
        sheetCalibrations_ = builder.sheetCalibBuilder_.build();
        sliceCalibrations_ = builder.sliceCalibBuilder_.build();
        acquisitionMode_ = builder.acquisitionMode_;
        cameraMode_ = builder.cameraMode_;
        imagingCameraOrder_ = builder.imagingCameraOrder_;
        useTimePoints_ = builder.useTimePoints_;
        useAutofocus_ = builder.useAutofocus_;
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
    public DefaultChannelSettings channels() {
        return channelSettings_;
    }

    /**
     * Returns the immutable DefaultTimingSettings instance.
     *
     * @return immutable DefaultTimingSettings instance.
     */
    @Override
    public DefaultTimingSettings timing() {
        return timingSettings_;
    }

    /**
     * Returns the immutable DefaultVolumeSettings instance.
     *
     * @return immutable DefaultVolumeSettings instance.
     */
    @Override
    public DefaultVolumeSettings volume() {
        return volumeSettings_;
    }

    /**
     * Returns the immutable DefaultSliceSettings instance.
     *
     * @return immutable DefaultSliceSettings instance.
     */
    @Override
    public DefaultSliceSettings slice() {
        return sliceSettings_;
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
    public DefaultSheetCalibration sheetCalibration(final int view) {
        return sheetCalibrations_;
    }

    /**
     * Returns an immutable DefaultSliceCalibration instance.
     * <p>
     * Views start at index 1.
     *
     * @return immutable DefaultSliceCalibration instance.
     */
    @Override
    public DefaultSliceCalibration sliceCalibration(final int view) {
        return sliceCalibrations_;
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
     * Returns true if using autofocus.
     *
     * @return true if using autofocus.
     */
    @Override
    public boolean isUsingAutofocus() {
        return useAutofocus_;
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
        return String.format("[timingSettings_=%s]", timingSettings_);
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
