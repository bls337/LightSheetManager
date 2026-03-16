package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AcquisitionSettingsDispim;
import org.micromanager.lightsheetmanager.api.StageScanSettings;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;

public class DispimAcquisitionSettings extends BaseAcquisitionSettings implements AcquisitionSettingsDispim {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseAcquisitionSettings.Builder<Builder> implements AcquisitionSettingsDispim.Builder<Builder> {

        private DefaultChannelSettings.Builder csb_ = DefaultChannelSettings.builder();
        private DefaultTimingSettings.Builder tsb_ = DefaultTimingSettings.builder();
        private DefaultVolumeSettings.Builder vsb_ = DefaultVolumeSettings.builder();
        private DefaultSliceSettings.Builder ssb_ = DefaultSliceSettings.builder();
        private DefaultSliceSettingsLS.Builder ssbLS_ = DefaultSliceSettingsLS.builder(); // maybe this should be LightSheetSliceSettings? replace ssb_?
        private StageScanSettings.Builder scsb_ = DefaultStageScanSettings.builder();
        private DefaultSheetCalibration.Builder[] shcb_ = new DefaultSheetCalibration.Builder[2];
        private DefaultSliceCalibration.Builder[] slcb_ = new DefaultSliceCalibration.Builder[2];

        private AcquisitionMode acquisitionMode_ = AcquisitionMode.NO_SCAN;
        private CameraMode cameraMode_ = CameraMode.EDGE;

        private boolean useTimePoints_ = false;
        private boolean useAutofocus_ = false;
        private boolean useMultiplePositions_ = false;
        private boolean useHardwareTimePoints_ = false;
        private boolean useStageScanning_ = false;
        private boolean useAdvancedTiming_ = false;

        private int numTimePoints_ = 1;
        private int timePointInterval_ = 0;
        private int postMoveDelay_ = 0;

        private double liveScanPeriod_ = 20.0; // TODO: this could go in user settings since it has to do with the live view

        private Builder() {
            for (int i = 0; i < 2; i++) {
                shcb_[i] = DefaultSheetCalibration.builder();
                slcb_[i] = DefaultSliceCalibration.builder();
            }
        }

        public Builder(final DispimAcquisitionSettings settings) {
            super(settings);
            csb_ = settings.channelSettings_.copyBuilder();
            tsb_ = settings.timingSettings_.copyBuilder();
            vsb_ = settings.volumeSettings_.copyBuilder();
            ssb_ = settings.sliceSettings_.copyBuilder();
            ssbLS_ = settings.sliceSettingsLS_.copyBuilder();
            scsb_ = settings.stageScan().copyBuilder();
            for (int i = 0; i < 2; i++) {
                slcb_[i] = settings.sliceCalibrations_[i].copyBuilder();
                shcb_[i] = settings.sheetCalibrations_[i].copyBuilder();
            }
            acquisitionMode_ = settings.acquisitionMode_;
            cameraMode_ = settings.cameraMode_;
            useTimePoints_ = settings.useTimePoints_;
            useAutofocus_ = settings.useAutofocus_;
            useMultiplePositions_ = settings.useMultiplePositions_;
            useHardwareTimePoints_ = settings.useHardwareTimePoints_;
            useStageScanning_ = settings.useStageScanning_;
            useAdvancedTiming_ =  settings.useAdvancedTiming_;
            numTimePoints_ = settings.numTimePoints_;
            timePointInterval_ = settings.timePointInterval_;
            postMoveDelay_ = settings.postMoveDelay_;
            liveScanPeriod_ = settings.liveScanPeriod_;
        }

        /**
         * Sets the acquisition mode.
         *
         * @param acqMode the acquisition mode
         */
        @Override
        public Builder acquisitionMode(final AcquisitionMode acqMode) {
            acquisitionMode_ = acqMode;
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
         * Sets the acquisition to use stage scanning.
         *
         * @param state true to use stage scanning.
         */
        @Override
        public Builder useStageScanning(final boolean state) {
            useStageScanning_ = state;
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
        public Builder timePointInterval(final int timePointInterval) {
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

        @Override
        public Builder liveScanPeriod(double liveScanPeriod) {
            liveScanPeriod_ = liveScanPeriod;
            return this;
        }

        // getters for sub-builders
        public DefaultChannelSettings.Builder channelBuilder() {
            return csb_;
        }

        public DefaultTimingSettings.Builder timingSettingsBuilder() {
            return tsb_;
        }

        public DefaultVolumeSettings.Builder volumeSettingsBuilder() {
            return vsb_;
        }

        public DefaultSliceSettings.Builder sliceSettingsBuilder() {
            return ssb_;
        }

        public DefaultSliceSettingsLS.Builder sliceSettingsLSBuilder() {
            return ssbLS_;
        }

        public StageScanSettings.Builder scanSettingsBuilder() {
            return scsb_;
        }

        public DefaultSheetCalibration.Builder sheetCalibrationBuilder(final int view) {
            return shcb_[view-1];
        }

        public DefaultSliceCalibration.Builder sliceCalibrationBuilder(final int view) {
            return slcb_[view-1];
        }

        public void timingSettingsBuilder(DefaultTimingSettings.Builder tsb) {
            tsb_ = tsb;
        }

        public void volumeSettingsBuilder(DefaultVolumeSettings.Builder vsb) {
            vsb_ = vsb;
        }

        /**
         * Creates an immutable instance of DefaultAcquisitionSettingsDISPIM
         *
         * @return Immutable version of DefaultAcquisitionSettingsDISPIM
         */
        @Override
        public DispimAcquisitionSettings build() {
            return new DispimAcquisitionSettings(this);
        }

        @Override
        public Builder self() {
            return this;
        }

        // TODO: finish toString with rest of properties
        @Override
        public String toString() {
            return String.format("[tsb_=%s]", tsb_);
        }

    }

    private final DefaultChannelSettings channelSettings_;
    private final DefaultTimingSettings timingSettings_;
    private final DefaultVolumeSettings volumeSettings_;
    private final DefaultSliceSettingsLS sliceSettingsLS_;
    private final DefaultSliceSettings sliceSettings_;
    private final StageScanSettings stageScan_;
    private final DefaultSheetCalibration[] sheetCalibrations_;
    private final DefaultSliceCalibration[] sliceCalibrations_;

    private final AcquisitionMode acquisitionMode_;
    private final CameraMode cameraMode_;

    private final boolean useTimePoints_;
    private final boolean useAutofocus_;
    private final boolean useMultiplePositions_;
    private final boolean useHardwareTimePoints_;
    private final boolean useStageScanning_;
    private final boolean useAdvancedTiming_;

    private final int numTimePoints_;
    private final int timePointInterval_;
    private final int postMoveDelay_;

    private final double liveScanPeriod_;

    private DispimAcquisitionSettings(Builder builder) {
        super(builder);
        channelSettings_ = builder.csb_.build();
        timingSettings_ = builder.tsb_.build();
        volumeSettings_ = builder.vsb_.build();
        sliceSettings_ = builder.ssb_.build();
        sliceSettingsLS_ = builder.ssbLS_.build();
        stageScan_ = builder.scsb_.build();
        sheetCalibrations_ = new DefaultSheetCalibration[2];
        sliceCalibrations_ = new DefaultSliceCalibration[2]; // TODO: populate with numViews instead of magic number
        for (int i = 0; i < 2; i++) {
            sheetCalibrations_[i] = builder.shcb_[i].build();
            sliceCalibrations_[i] = builder.slcb_[i].build();
        }
        acquisitionMode_ = builder.acquisitionMode_;
        cameraMode_ = builder.cameraMode_;
        useTimePoints_ = builder.useTimePoints_;
        useAutofocus_ = builder.useAutofocus_;
        useStageScanning_ = builder.useStageScanning_;
        useMultiplePositions_ = builder.useMultiplePositions_;
        useHardwareTimePoints_ = builder.useHardwareTimePoints_;
        useAdvancedTiming_ = builder.useAdvancedTiming_;
        numTimePoints_ = builder.numTimePoints_;
        timePointInterval_ = builder.timePointInterval_;
        postMoveDelay_ = builder.postMoveDelay_;
        liveScanPeriod_= builder.liveScanPeriod_;
    }

//    /**
//     * Creates a Builder populated with settings of this DefaultAcquisitionSettingsDISPIM instance.
//     *
//     * @return DefaultAcquisitionSettingsDISPIM.Builder pre-populated with settings of this instance.
//     */
//    @Override
//    public DefaultAcquisitionSettingsDISPIM.Builder copyBuilder() {
//        return new Builder(this);
//        );
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
     * Returns the immutable DefaultSliceSettingsLS instance.
     *
     * @return immutable DefaultSliceSettingsLS instance.
     */
    @Override
    public DefaultSliceSettingsLS sliceLS() {
        return sliceSettingsLS_;
    }

    /**
     * Returns the immutable DefaultScanSettings instance.
     *
     * @return immutable DefaultScanSettings instance.
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
        return sheetCalibrations_[view-1];
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
        return sliceCalibrations_[view-1];
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
    public int timePointInterval() {
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

    @Override
    public double liveScanPeriod() {
        return liveScanPeriod_;
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
