package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceSettings;

import java.util.Objects;

public class DefaultSliceSettings implements SliceSettings {

    private final double period;
    private final double sampleExposure;
    private final boolean periodMinimized;

    private DefaultSliceSettings(Builder builder) {
        period = builder.period;
        sampleExposure = builder.sampleExposure;
        periodMinimized = builder.periodMinimized;
    }

    // Note: used by GSON library for deserialization
    private DefaultSliceSettings() {
        this(new Builder());
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
        return period;
    }

    @Override
    public double sampleExposure() {
        return sampleExposure;
    }

    @Override
    public boolean periodMinimized() {
        return periodMinimized;
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
        return Double.compare(period, other.period) == 0 &&
                Double.compare(sampleExposure, other.sampleExposure) == 0 &&
                periodMinimized == other.periodMinimized;
    }

    @Override
    public int hashCode() {
        return Objects.hash(period, sampleExposure, periodMinimized);
    }

    @Override
    public String toString() {
        return String.format(
                "%s[period=%s, sampleExposure=%s, periodMinimized=%s]",
                getClass().getSimpleName(),
                period, sampleExposure, periodMinimized
        );
    }

    public static class Builder implements SliceSettings.Builder {

        private double period = 10.0;
        private double sampleExposure = 1.0;
        private boolean periodMinimized = false;

        private Builder() {
        }

        private Builder(final SliceSettings settings) {
            period = settings.period();
            sampleExposure = settings.sampleExposure();
            periodMinimized = settings.periodMinimized();
        }

        @Override
        public Builder period(double periodMs) {
            period = periodMs;
            return this;
        }

        @Override
        public Builder sampleExposure(double exposureMs) {
            sampleExposure = exposureMs;
            return this;
        }

        @Override
        public Builder periodMinimized(boolean state) {
            periodMinimized = state;
            return this;
        }

        @Override
        public DefaultSliceSettings build() {
            return new DefaultSliceSettings(this);
        }

    }

}
