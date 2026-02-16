package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AcquisitionSettingsSCAPE;
import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;
import org.micromanager.lightsheetmanager.api.data.CameraData;
import org.micromanager.lightsheetmanager.api.data.CameraMode;

public class DefaultAcquisitionSettingsSCAPE extends DefaultAcquisitionSettings implements AcquisitionSettingsSCAPE {

    public static class Builder extends DefaultAcquisitionSettings.Builder<Builder> implements AcquisitionSettingsSCAPE.Builder<Builder> {

        private DefaultChannelSettings.Builder csb_ = new DefaultChannelSettings.Builder();
        private DefaultTimingSettings.Builder tsb_ = new DefaultTimingSettings.Builder();
        private DefaultVolumeSettings.Builder vsb_ = new DefaultVolumeSettings.Builder();
        private DefaultSliceSettings.Builder ssb_ = new DefaultSliceSettings.Builder();
        private DefaultSliceSettingsLS.Builder ssbLS_ = new DefaultSliceSettingsLS.Builder();
        private DefaultScanSettings.Builder scsb_ = new DefaultScanSettings.Builder();
        private DefaultSheetCalibration.Builder[] shcb_ = new DefaultSheetCalibration.Builder[1];
        private DefaultSliceCalibration.Builder[] slcb_ = new DefaultSliceCalibration.Builder[1];
        private AcquisitionMode acquisitionMode_ = AcquisitionMode.NO_SCAN;

        private CameraMode cameraMode_ = CameraMode.EDGE;
        private CameraData[] imagingCameraOrder_ = {};
        private boolean useSimultaneousCameras_ = true;

        private boolean useChannels_ = false;
        private boolean useTimePoints_ = false;
        private boolean useAutofocus_ = false;
        private boolean useMultiplePositions_ = false;
        private boolean useHardwareTimePoints_ = false;
        private boolean useStageScanning_ = false;
        private boolean useAdvancedTiming_ = false;

        private int numTimePoints_ = 1;
        private double timePointInterval_ = 0.0;
        private int postMoveDelay_ = 0;
        private double liveScanPeriod_ = 20.0;

        public Builder() {
            for (int i = 0; i < 1; i++) {
                shcb_[i] = new DefaultSheetCalibration.Builder();
                slcb_[i] = new DefaultSliceCalibration.Builder();
            }
        }

        public Builder(final DefaultAcquisitionSettingsSCAPE acqSettings) {
            super(acqSettings);
            csb_ = acqSettings.channelSettings_.copyBuilder();
            tsb_ = acqSettings.timingSettings_.copyBuilder();
            vsb_ = acqSettings.volumeSettings_.copyBuilder();
            ssb_ = acqSettings.sliceSettings_.copyBuilder();
            ssbLS_ = acqSettings.sliceSettingsLS_.copyBuilder();
            scsb_ = acqSettings.scanSettings_.copyBuilder();
            for (int i = 0; i < 1; i++) {
                slcb_[i] = acqSettings.sliceCalibrations_[i].copyBuilder();
                shcb_[i] = acqSettings.sheetCalibrations_[i].copyBuilder();
            }
            acquisitionMode_ = acqSettings.acquisitionMode_;
            cameraMode_ = acqSettings.cameraMode_;
            imagingCameraOrder_ = acqSettings.imagingCameraOrder_;
            useSimultaneousCameras_ = acqSettings.useSimultaneousCameras_;
            useChannels_ = acqSettings.useChannels_;
            useTimePoints_ = acqSettings.useTimePoints_;
            useAutofocus_ = acqSettings.useAutofocus_;
            useMultiplePositions_ = acqSettings.useMultiplePositions_;
            useHardwareTimePoints_ = acqSettings.useHardwareTimePoints_;
            useStageScanning_ = acqSettings.useStageScanning_;
            useAdvancedTiming_ =  acqSettings.useAdvancedTiming_;
            numTimePoints_ = acqSettings.numTimePoints_;
            timePointInterval_ = acqSettings.timePointInterval_;
            postMoveDelay_ = acqSettings.postMoveDelay_;
            liveScanPeriod_ = acqSettings.liveScanPeriod_;
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
         * Sets the acquisition to acquire from multiple simultaneous cameras.
         *
         * @param state true if acquiring from both sides
         */
        @Override
        public Builder useSimultaneousCameras(final boolean state) {
            useSimultaneousCameras_ = state;
            return this;
        }

        /**
         * Sets the acquisition to use channels.
         *
         * @param state true to use channels.
         */
        @Override
        public Builder useChannels(final boolean state) {
            useChannels_ = state;
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

        @Override
        public Builder liveScanPeriod(double liveScanPeriod) {
            liveScanPeriod_ = liveScanPeriod;
            return this;
        }

        // getters for sub-builders
        public DefaultChannelSettings.Builder channelSettingsBuilder() {
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

        public DefaultScanSettings.Builder scanSettingsBuilder() {
            return scsb_;
        }

        public DefaultSheetCalibration.Builder sheetCalibrationBuilder(final int view) {
            return shcb_[view-1];
        }

        public DefaultSliceCalibration.Builder sliceCalibrationBuilder(final int view) {
            return slcb_[view-1];
        }

        public DefaultTimingSettings.Builder tsb() {
            return tsb_;
        }

        public DefaultVolumeSettings.Builder vsb() {
            return vsb_;
        }

        public DefaultSliceSettings.Builder ssb() {
            return ssb_;
        }

        public void timingSettingsBuilder(DefaultTimingSettings.Builder tsb) {
            tsb_ = tsb;
        }

        public void volumeSettingsBuilder(DefaultVolumeSettings.Builder vsb) {
            vsb_ = vsb;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public DefaultAcquisitionSettingsSCAPE build() {
            return new DefaultAcquisitionSettingsSCAPE(this);
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
    private final DefaultScanSettings scanSettings_;
    private final DefaultSheetCalibration[] sheetCalibrations_;
    private final DefaultSliceCalibration[] sliceCalibrations_;

    private final AcquisitionMode acquisitionMode_;

    private final CameraMode cameraMode_;
    private final CameraData[] imagingCameraOrder_;
    private final boolean useSimultaneousCameras_;

    private final boolean useChannels_;
    private final boolean useTimePoints_;
    private final boolean useAutofocus_;
    private final boolean useMultiplePositions_;
    private final boolean useHardwareTimePoints_;
    private final boolean useStageScanning_;
    private final boolean useAdvancedTiming_;

    private final int numTimePoints_;
    private final double timePointInterval_;
    private final int postMoveDelay_;
    private final double liveScanPeriod_;

    private DefaultAcquisitionSettingsSCAPE(Builder builder) {
        super(builder);
        channelSettings_ = builder.csb_.build();
        timingSettings_ = builder.tsb_.build();
        volumeSettings_ = builder.vsb_.build();
        sliceSettings_ = builder.ssb_.build();
        sliceSettingsLS_ = builder.ssbLS_.build();
        scanSettings_ = builder.scsb_.build();
        sheetCalibrations_ = new DefaultSheetCalibration[1];
        sliceCalibrations_ = new DefaultSliceCalibration[1]; // TODO: use this object directly
        for (int i = 0; i < 1; i ++) {
            sheetCalibrations_[i] = builder.shcb_[i].build();
            sliceCalibrations_[i] = builder.slcb_[i].build();
        }
        acquisitionMode_ = builder.acquisitionMode_;
        cameraMode_ = builder.cameraMode_;
        imagingCameraOrder_ = builder.imagingCameraOrder_;
        useSimultaneousCameras_ = builder.useSimultaneousCameras_;
        useChannels_ = builder.useChannels_;
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
//        return new Builder(
//                timingSettings_.copyBuilder(),
//                volumeSettings_.copyBuilder(),
//                sliceSettings_.copyBuilder(),
//                sliceSettingsLS_.copyBuilder(),
//                acquisitionMode_,
//                channelMode_,
//                cameraMode_,
//                useChannels_,
//                useTimePoints_,
//                useAutofocus_,
//                useStageScanning_,
//                useMultiplePositions_,
//                useHardwareTimePoints_,
//                useAdvancedTiming_,
//                numTimePoints_,
//                timePointInterval_,
//                postMoveDelay_
//        );
//    }

    /**
     * Returns the immutable DefaultChannelSettings instance.
     *
     * @return immutable DefaultChannelSettings instance.
     */
    @Override
    public DefaultChannelSettings channelSettings() {
        return channelSettings_;
    }

    /**
     * Returns the immutable DefaultTimingSettings instance.
     *
     * @return immutable DefaultTimingSettings instance.
     */
    @Override
    public DefaultTimingSettings timingSettings() {
        return timingSettings_;
    }

    /**
     * Returns the immutable DefaultVolumeSettings instance.
     *
     * @return immutable DefaultVolumeSettings instance.
     */
    @Override
    public DefaultVolumeSettings volumeSettings() {
        return volumeSettings_;
    }

    /**
     * Returns the immutable DefaultSliceSettings instance.
     *
     * @return immutable DefaultSliceSettings instance.
     */
    @Override
    public DefaultSliceSettings sliceSettings() {
        return sliceSettings_;
    }

    /**
     * Returns the immutable DefaultSliceSettingsLS instance.
     *
     * @return immutable DefaultSliceSettingsLS instance.
     */
    @Override
    public DefaultSliceSettingsLS sliceSettingsLS() {
        return sliceSettingsLS_;
    }

    /**
     * Returns the immutable DefaultScanSettings instance.
     *
     * @return immutable DefaultScanSettings instance.
     */
    @Override
    public DefaultScanSettings scanSettings() {
        return scanSettings_;
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
     * Returns the imaging camera order.
     *
     * @return the imaging camera order
     */
    @Override
    public CameraData[] imagingCameraOrder() {
        return imagingCameraOrder_;
    }

    /**
     * Returns true if acquiring from all active imaging cameras on a single view.
     *
     * @return true if acquiring from all active imaging cameras on a single view
     */
    @Override
    public boolean isUsingSimultaneousCameras() {
        return useSimultaneousCameras_;
    }

    /**
     * Returns true if using channels.
     *
     * @return true if using channels.
     */
    @Override
    public boolean isUsingChannels() {
        return useChannels_;
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
