package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceSettings;

public class DefaultSliceSettings implements SliceSettings {

    public static class Builder implements SliceSettings.Builder {

        private double slicePeriod_ = 10.0;
        private double sampleExposure_ = 1.0;
        private boolean minimizeSlicePeriod_ = false;

        public Builder() {
        }

        private Builder(final DefaultSliceSettings sliceSettings) {
            slicePeriod_ = sliceSettings.slicePeriod();
            sampleExposure_ = sliceSettings.sampleExposure();
            minimizeSlicePeriod_ = sliceSettings.isSlicePeriodMinimized();
        }

        @Override
        public SliceSettings.Builder slicePeriod(double slicePeriodMs) {
            slicePeriod_ = slicePeriodMs;
            return this;
        }

        @Override
        public SliceSettings.Builder sampleExposure(double exposureMs) {
            sampleExposure_ = exposureMs;
            return this;
        }

        @Override
        public SliceSettings.Builder minimizeSlicePeriod(boolean state) {
            minimizeSlicePeriod_ = state;
            return this;
        }

        @Override
        public DefaultSliceSettings build() {
            return new DefaultSliceSettings(this);
        }
    }

    private final double slicePeriod_;
    private final double sampleExposure_;
    private final boolean minimizeSlicePeriod_;

    private DefaultSliceSettings(Builder builder) {
        slicePeriod_ = builder.slicePeriod_;
        sampleExposure_ = builder.sampleExposure_;
        minimizeSlicePeriod_ = builder.minimizeSlicePeriod_;
    }

    @Override
    public DefaultSliceSettings.Builder copyBuilder() {
        return new Builder(this);
    }

    @Override
    public double slicePeriod() {
        return slicePeriod_;
    }

    @Override
    public double sampleExposure() {
        return sampleExposure_;
    }

    @Override
    public boolean isSlicePeriodMinimized() {
        return minimizeSlicePeriod_;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[slicePeriod_=%s, sampleExposure_=%s, minimizeSlicePeriod_=%s]",
                getClass().getSimpleName(),
                slicePeriod_, sampleExposure_, minimizeSlicePeriod_
        );
    }
}
