package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AcquisitionSettingsDispim;
import org.micromanager.lightsheetmanager.api.ChannelSettings;
import org.micromanager.lightsheetmanager.api.StageScanSettings;
import org.micromanager.lightsheetmanager.api.VolumeSettings;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;

import java.util.Arrays;
import java.util.Objects;

public class DispimAcquisitionSettings extends BaseAcquisitionSettings implements AcquisitionSettingsDispim {

    private final ChannelSettings channels_;
    private final DefaultTimingSettings timing_;
    private final VolumeSettings volume_;
    private final DefaultSliceSettings slice_;
    private final DefaultSliceSettingsLS sliceLS_;
    private final StageScanSettings stageScan_;
    private final DefaultSheetCalibration[] sheetCalibrations_;
    private final DefaultSliceCalibration[] sliceCalibrations_;

    private final AcquisitionMode acquisitionMode_;
    private final CameraMode cameraMode_;

    private final boolean useTimePoints_;
    private final boolean useMultiplePositions_;
    private final boolean useHardwareTimePoints_;
    private final boolean useAdvancedTiming_;

    private final int numTimePoints_;
    private final int timePointInterval_;
    private final int postMoveDelay_;

    private final double liveScanPeriod_;

    private DispimAcquisitionSettings(Builder builder) {
        super(builder);
        channels_ = builder.channelBuilder().build();
        timing_ = builder.timingBuilder().build();
        volume_ = builder.volumeBuilder().build();
        slice_ = builder.sliceBuilder().build();
        sliceLS_ = builder.sliceLSBuilder().build();
        stageScan_ = builder.stageScanBuilder().build();
        sheetCalibrations_ = new DefaultSheetCalibration[2];
        sliceCalibrations_ = new DefaultSliceCalibration[2]; // TODO: populate with numViews instead of magic number
        for (int i = 0; i < 2; i++) {
            sheetCalibrations_[i] = builder.shcb_[i].build();
            sliceCalibrations_[i] = builder.slcb_[i].build();
        }
        acquisitionMode_ = builder.acquisitionMode_;
        cameraMode_ = builder.cameraMode_;
        useTimePoints_ = builder.useTimePoints_;
        useMultiplePositions_ = builder.useMultiplePositions_;
        useHardwareTimePoints_ = builder.useHardwareTimePoints_;
        useAdvancedTiming_ = builder.useAdvancedTiming_;
        numTimePoints_ = builder.numTimePoints_;
        timePointInterval_ = builder.timePointInterval_;
        postMoveDelay_ = builder.postMoveDelay_;
        liveScanPeriod_= builder.liveScanPeriod_;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DispimAcquisitionSettings settings) {
        Objects.requireNonNull(settings, "Cannot copy from null settings");
        return new Builder(settings);
    }

//    @Override
//    public DefaultAcquisitionSettingsDISPIM.Builder copyBuilder() {
//        return new Builder(this);
//        );
//    }

    @Override
    public ChannelSettings channels() {
        return channels_;
    }

    @Override
    public DefaultTimingSettings timing() {
        return timing_;
    }

    @Override
    public VolumeSettings volume() {
        return volume_;
    }

    @Override
    public DefaultSliceSettings slice() {
        return slice_;
    }

    @Override
    public DefaultSliceSettingsLS sliceLS() {
        return sliceLS_;
    }

    @Override
    public StageScanSettings stageScan() {
        return stageScan_;
    }

    @Override
    public DefaultSheetCalibration sheetCalibration(final int view) {
        return sheetCalibrations_[view-1];
    }

    @Override
    public DefaultSliceCalibration sliceCalibration(final int view) {
        return sliceCalibrations_[view-1];
    }

    @Override
    public AcquisitionMode acquisitionMode() {
        return acquisitionMode_;
    }

    @Override
    public CameraMode cameraMode() {
        return cameraMode_;
    }

    @Override
    public boolean isUsingTimePoints() {
        return useTimePoints_;
    }

    @Override
    public boolean isUsingMultiplePositions() {
        return useMultiplePositions_;
    }

    @Override
    public boolean isUsingHardwareTimePoints() {
        return useHardwareTimePoints_;
    }

    @Override
    public boolean isUsingAdvancedTiming() {
        return useAdvancedTiming_;
    }

    @Override
    public int numTimePoints() {
        return numTimePoints_;
    }

    @Override
    public int timePointInterval() {
        return timePointInterval_;
    }

    @Override
    public int postMoveDelay() {
        return postMoveDelay_;
    }

    @Override
    public double liveScanPeriod() {
        return liveScanPeriod_;
    }

    // TODO: finish this
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DispimAcquisitionSettings other = (DispimAcquisitionSettings) obj;
        return Objects.equals(channels_, other.channels_) &&
                Objects.equals(timing_, other.timing_) &&
                Objects.equals(volume_, other.volume_) &&
                Objects.equals(slice_, other.slice_) &&
                Objects.equals(sliceLS_, other.sliceLS_) &&
                Objects.equals(stageScan_, other.stageScan_) &&
                // Objects.equals(sheetCalibration_, other.sheetCalibration_) &&
                // Objects.equals(sliceCalibration_, other.sliceCalibration_) &&
                acquisitionMode_ == other.acquisitionMode_ &&
                cameraMode_ == other.cameraMode_ &&
                // Arrays.equals(imagingCameraOrder_, other.imagingCameraOrder_) &&
                useTimePoints_ == other.useTimePoints_ &&
                useMultiplePositions_ == other.useMultiplePositions_ &&
                useHardwareTimePoints_ == other.useHardwareTimePoints_ &&
                useAdvancedTiming_ == other.useAdvancedTiming_ &&
                numTimePoints_ == other.numTimePoints_ &&
                Double.compare(other.timePointInterval_, timePointInterval_) == 0 &&
                postMoveDelay_ == other.postMoveDelay_;
    }

    // TODO: finish this
    @Override
    public int hashCode() {
        return Objects.hash(
                channels_,
                timing_,
                volume_,
                slice_,
                sliceLS_,
                stageScan_,
                // sheetCalibration_,
                // sliceCalibration_,
                acquisitionMode_,
                cameraMode_,
                //Arrays.hashCode(imagingCameraOrder_),
                useTimePoints_,
                useMultiplePositions_,
                useHardwareTimePoints_,
                useAdvancedTiming_,
                numTimePoints_,
                timePointInterval_,
                postMoveDelay_
        );
    }

    // TODO: finish this, and maybe use pretty printing? or just rely on JSON conversion?
    @Override
    public String toString() {
        return String.format("%s[channels=%s, timing=%s, volume=%s, slice=%s, sliceLS=%s, stageScan=%s]",
                getClass().getSimpleName(), channels_, timing_, volume_, slice_, sliceLS_, stageScan_);
    }

    public static class Builder
            extends BaseAcquisitionSettings.Builder<Builder>
            implements AcquisitionSettingsDispim.Builder<Builder> {

        private ChannelSettings.Builder channelBuilder_ = DefaultChannelSettings.builder();
        private DefaultTimingSettings.Builder timingBuilder_ = DefaultTimingSettings.builder();
        private VolumeSettings.Builder volumeBuilder_ = DefaultVolumeSettings.builder();
        private DefaultSliceSettings.Builder sliceBuilder_ = DefaultSliceSettings.builder();
        private DefaultSliceSettingsLS.Builder ssbLS_ = DefaultSliceSettingsLS.builder(); // maybe this should be LightSheetSliceSettings? replace ssb_?
        private StageScanSettings.Builder stageScanBuilder_ = DefaultStageScanSettings.builder();
        private DefaultSheetCalibration.Builder[] shcb_ = new DefaultSheetCalibration.Builder[2];
        private DefaultSliceCalibration.Builder[] slcb_ = new DefaultSliceCalibration.Builder[2];

        private AcquisitionMode acquisitionMode_ = AcquisitionMode.NO_SCAN;
        private CameraMode cameraMode_ = CameraMode.EDGE;

        private boolean useTimePoints_ = false;
        private boolean useMultiplePositions_ = false;
        private boolean useHardwareTimePoints_ = false;
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
            channelBuilder_ = settings.channels().copyBuilder();
            timingBuilder_ = settings.timing().copyBuilder();
            volumeBuilder_ = settings.volume().copyBuilder();
            sliceBuilder_ = settings.slice().copyBuilder();
            ssbLS_ = settings.sliceLS_.copyBuilder();
            stageScanBuilder_ = settings.stageScan().copyBuilder();
            for (int i = 0; i < 2; i++) {
                slcb_[i] = settings.sliceCalibrations_[i].copyBuilder();
                shcb_[i] = settings.sheetCalibrations_[i].copyBuilder();
            }
            acquisitionMode_ = settings.acquisitionMode_;
            cameraMode_ = settings.cameraMode_;
            useTimePoints_ = settings.useTimePoints_;
            useMultiplePositions_ = settings.useMultiplePositions_;
            useHardwareTimePoints_ = settings.useHardwareTimePoints_;
            useAdvancedTiming_ =  settings.useAdvancedTiming_;
            numTimePoints_ = settings.numTimePoints_;
            timePointInterval_ = settings.timePointInterval_;
            postMoveDelay_ = settings.postMoveDelay_;
            liveScanPeriod_ = settings.liveScanPeriod_;
        }

        @Override
        public Builder acquisitionMode(final AcquisitionMode mode) {
            acquisitionMode_ = mode;
            return this;
        }

        @Override
        public Builder cameraMode(final CameraMode mode) {
            cameraMode_ = mode;
            return this;
        }

        @Override
        public Builder useTimePoints(final boolean state) {
            useTimePoints_ = state;
            return this;
        }

        @Override
        public Builder useMultiplePositions(final boolean state) {
            useMultiplePositions_ = state;
            return this;
        }

        @Override
        public Builder useHardwareTimePoints(final boolean state) {
            useHardwareTimePoints_ = state;
            return this;
        }

        @Override
        public Builder useAdvancedTiming(final boolean state) {
            useAdvancedTiming_ = state;
            return this;
        }

        @Override
        public Builder numTimePoints(final int numTimePoints) {
            numTimePoints_ = numTimePoints;
            return this;
        }

        @Override
        public Builder timePointInterval(final int timePointInterval) {
            timePointInterval_ = timePointInterval;
            return this;
        }

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
        public ChannelSettings.Builder channelBuilder() {
            return channelBuilder_;
        }

        public DefaultTimingSettings.Builder timingBuilder() {
            return timingBuilder_;
        }

        public VolumeSettings.Builder volumeBuilder() {
            return volumeBuilder_;
        }

        public DefaultSliceSettings.Builder sliceBuilder() {
            return sliceBuilder_;
        }

        public DefaultSliceSettingsLS.Builder sliceLSBuilder() {
            return ssbLS_;
        }

        public StageScanSettings.Builder stageScanBuilder() {
            return stageScanBuilder_;
        }

        public DefaultSheetCalibration.Builder sheetCalibrationBuilder(final int view) {
            return shcb_[view-1];
        }

        public DefaultSliceCalibration.Builder sliceCalibrationBuilder(final int view) {
            return slcb_[view-1];
        }

        public void timingBuilder(DefaultTimingSettings.Builder builder) {
            timingBuilder_ = builder;
        }

        public void volumeBuilder(DefaultVolumeSettings.Builder builder) {
            volumeBuilder_ = builder;
        }

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
            return String.format("[timingBuilder_=%s]", timingBuilder_);
        }

    }

}
