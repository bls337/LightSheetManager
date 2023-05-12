package org.micromanager.lightsheetmanager.api.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.micromanager.lightsheetmanager.api.AcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;

public class DefaultAcquisitionSettingsDISPIM extends DefaultAcquisitionSettings implements AcquisitionSettingsDISPIM {

    public static class Builder extends DefaultAcquisitionSettings.Builder<Builder> implements AcquisitionSettingsDISPIM.Builder<Builder> {
        private DefaultTimingSettings.Builder tsb_ = new DefaultTimingSettings.Builder();
        private DefaultVolumeSettings.Builder vsb_ = new DefaultVolumeSettings.Builder();
        private DefaultSliceSettings.Builder ssb_ = new DefaultSliceSettings.Builder();
        private DefaultSliceSettingsLS.Builder ssbLS_ = new DefaultSliceSettingsLS.Builder();
        private DefaultScanSettings.Builder scsb_ = new DefaultScanSettings.Builder();
        private DefaultSheetCalibration.Builder shcb_ = new DefaultSheetCalibration.Builder();
        private DefaultSliceCalibration.Builder slcb_ = new DefaultSliceCalibration.Builder();

        private AcquisitionModes acquisitionMode_ = AcquisitionModes.NONE;
        private MultiChannelModes channelMode_ = MultiChannelModes.NONE;
        private CameraModes cameraMode_ = CameraModes.INTERNAL;

        private boolean useChannels_ = false;
        private boolean useTimePoints_ = false;
        private boolean useAutofocus_ = false;
        private boolean useMultiplePositions_ = false;
        private boolean useHardwareTimePoints_ = false;
        private boolean useStageScanning_ = false;
        private boolean useAdvancedTiming_ = false;

        private int numTimePoints_ = 1;
        private int timePointInterval_ = 0;
        private int postMoveDelay_ = 0;

        private int numChannels_ = 0;
        private String channelGroup_ = "";
        private ChannelSpec[] channels_ = new ChannelSpec[]{};

        public Builder() {
        }

        public Builder(final DefaultAcquisitionSettingsDISPIM acqSettings) {
            super(acqSettings);
            tsb_ = acqSettings.timingSettings_.copyBuilder();
            vsb_ = acqSettings.volumeSettings_.copyBuilder();
            ssb_ = acqSettings.sliceSettings_.copyBuilder();
            ssbLS_ = acqSettings.sliceSettingsLS_.copyBuilder();
            scsb_ = acqSettings.scanSettings_.copyBuilder();
            acquisitionMode_ = acqSettings.acquisitionMode_;
            channelMode_ = acqSettings.channelMode_;
            cameraMode_ = acqSettings.cameraMode_;
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
            numChannels_ = acqSettings.numChannels_;
            channelGroup_ = acqSettings.channelGroup_;
            channels_ = acqSettings.channels_;
        }

        /**
         * Sets the acquisition mode.
         *
         * @param acqMode the acquisition mode
         */
        @Override
        public Builder acquisitionMode(final AcquisitionModes acqMode) {
            acquisitionMode_ = acqMode;
            return this;
        }

        /**
         * Sets the channel mode.
         *
         * @param channelMode the channel mode.
         */
        @Override
        public Builder channelMode(final MultiChannelModes channelMode) {
            channelMode_ = channelMode;
            return this;
        }

        /**
         * Sets the camera mode.
         *
         * @param cameraMode the camera mode.
         */
        @Override
        public Builder cameraMode(final CameraModes cameraMode) {
            cameraMode_ = cameraMode;
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

        /**
         * Sets the number of channels.
         *
         * @param numChannels the number of channels.
         */
        @Override
        public Builder numChannels(final int numChannels) {
            numChannels_ = numChannels;
            return this;
        }

        /**
         * Sets the channel group.
         *
         * @param channelGroup the channel group.
         */
        @Override
        public Builder channelGroup(final String channelGroup) {
            channelGroup_ = channelGroup;
            return this;
        }

        /**
         * Sets the channels array.
         *
         * @param channels the channel array
         */
        @Override
        public Builder channels(final ChannelSpec[] channels) {
            channels_ = channels;
            return this;
        }

        // getters for sub-builders
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

        public DefaultSheetCalibration.Builder sheetCalibrationBuilder() {
            return shcb_;
        }

        public DefaultSliceCalibration.Builder sliceCalibrationBuilder() {
            return slcb_;
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


        // getters for builder

        public AcquisitionModes acquisitionMode() {
            return acquisitionMode_;
        }

        public CameraModes cameraMode() {
            return cameraMode_;
        }

        public boolean isUsingAdvancedTiming() {
            return useAdvancedTiming_;
        }

        public int numChannels() {
            return numChannels_;
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
        public DefaultAcquisitionSettingsDISPIM build() {
            return new DefaultAcquisitionSettingsDISPIM(this);
        }

        @Override
        public Builder self() {
            return this;
        }

    }

    private final DefaultTimingSettings timingSettings_;
    private final DefaultVolumeSettings volumeSettings_;
    private final DefaultSliceSettingsLS sliceSettingsLS_;
    private final DefaultSliceSettings sliceSettings_;
    private final DefaultScanSettings scanSettings_;
    private final DefaultSheetCalibration sheetCalibration_;
    private final DefaultSliceCalibration sliceCalibration_;
    private final AcquisitionModes acquisitionMode_;
    private final MultiChannelModes channelMode_;
    private final CameraModes cameraMode_;

    private final boolean useChannels_;
    private final boolean useTimePoints_;
    private final boolean useAutofocus_;
    private final boolean useMultiplePositions_;
    private final boolean useHardwareTimePoints_;
    private final boolean useStageScanning_;
    private final boolean useAdvancedTiming_;

    private final int numTimePoints_;
    private final int timePointInterval_;
    private final int postMoveDelay_;

    private final int numChannels_;
    private final String channelGroup_;
    private final ChannelSpec[] channels_;

    private DefaultAcquisitionSettingsDISPIM(Builder builder) {
        super(builder);
        timingSettings_ = builder.tsb_.build();
        volumeSettings_ = builder.vsb_.build();
        sliceSettings_ = builder.ssb_.build();
        sliceSettingsLS_ = builder.ssbLS_.build();
        scanSettings_ = builder.scsb_.build();
        sheetCalibration_ = builder.shcb_.build();
        sliceCalibration_ = builder.slcb_.build();
        acquisitionMode_ = builder.acquisitionMode_;
        channelMode_ = builder.channelMode_;
        cameraMode_ = builder.cameraMode_;
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
        numChannels_ = builder.numChannels_;
        channelGroup_ = builder.channelGroup_;
        channels_ = builder.channels_;
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
//                postMoveDelay_,
//                numChannels_,
//                channelGroup_,
//                channels_
//        );
//    }

//    @Override
//    public AcquisitionSettingsDISPIM.Builder copyBuilder() {
//        return null;
//    }

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
    public DefaultSheetCalibration sheetCalibration() {
        return sheetCalibration_;
    }

    /**
     * Returns the immutable DefaultSliceCalibration instance.
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
    public AcquisitionModes acquisitionMode() {
        return acquisitionMode_;
    }

    /**
     * Returns the channel mode.
     *
     * @return the channel mode.
     */
    @Override
    public MultiChannelModes channelMode() {
        return channelMode_;
    }

    /**
     * Returns the camera mode.
     *
     * @return the camera mode.
     */
    @Override
    public CameraModes cameraMode() {
        return cameraMode_;
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

    /**
     * Returns the number of channels.
     *
     * @return the number of channels.
     */
    @Override
    public int numChannels() {
        return numChannels_;
    }

    /**
     * Returns the channel group.
     *
     * @return the channel group.
     */
    @Override
    public String channelGroup() {
        return channelGroup_;
    }

    /**
     * Returns the channels as an array.
     *
     * @return the channels as an array.
     */
    @Override
    public ChannelSpec[] channels() {
        return channels_;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String toPrettyJson() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static DefaultAcquisitionSettingsDISPIM fromJson(final String json) {
        return new Gson().fromJson(json, DefaultAcquisitionSettingsDISPIM.class);
    }
}