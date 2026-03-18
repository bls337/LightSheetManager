package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceSettings;

import java.util.Objects;

public class DefaultSliceSettings implements SliceSettings {

    private final double period_;
    private final double sampleExposure_;
    private final boolean periodMinimized_;

    private DefaultSliceSettings(Builder builder) {
        period_ = builder.period_;
        sampleExposure_ = builder.sampleExposure_;
        periodMinimized_ = builder.periodMinimized_;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SliceSettings settings) {
        Objects.requireNonNull(settings, "Cannot copy from null settings");
        return new Builder(settings);
    }

    @Override
    public Builder copyBuilder() {
        return new Builder(this);
    }

    @Override
    public double period() {
        return period_;
    }

    @Override
    public double sampleExposure() {
        return sampleExposure_;
    }

    @Override
    public boolean periodMinimized() {
        return periodMinimized_;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DefaultSliceSettings other = (DefaultSliceSettings) obj;
        return Double.compare(period_, other.period_) == 0 &&
                Double.compare(sampleExposure_, other.sampleExposure_) == 0 &&
                periodMinimized_ == other.periodMinimized_;
    }

    @Override
    public int hashCode() {
        return Objects.hash(period_, sampleExposure_, periodMinimized_);
    }

    @Override
    public String toString() {
        return String.format(
                "%s[period=%s, sampleExposure=%s, periodMinimized=%s]",
                getClass().getSimpleName(),
                period_, sampleExposure_, periodMinimized_
        );
    }

    public static class Builder implements SliceSettings.Builder {

        private double period_ = 10.0;
        private double sampleExposure_ = 1.0;
        private boolean periodMinimized_ = false;

        private Builder() {
        }

        private Builder(final SliceSettings settings) {
            period_ = settings.period();
            sampleExposure_ = settings.sampleExposure();
            periodMinimized_ = settings.periodMinimized();
        }

        @Override
        public Builder period(double periodMs) {
            period_ = periodMs;
            return this;
        }

        @Override
        public Builder sampleExposure(double exposureMs) {
            sampleExposure_ = exposureMs;
            return this;
        }

        @Override
        public Builder periodMinimized(boolean state) {
            periodMinimized_ = state;
            return this;
        }

        @Override
        public DefaultSliceSettings build() {
            return new DefaultSliceSettings(this);
        }

    }

}
