package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceSettingsLightSheet;

import java.util.Objects;

public class DefaultSliceSettingsLS implements SliceSettingsLightSheet {

    private final double scanResetTime;
    private final double scanSettleTime;
    private final double shutterWidth;
    private final double shutterSpeedFactor;

    private DefaultSliceSettingsLS(Builder builder) {
        scanResetTime = builder.scanResetTime;
        scanSettleTime = builder.scanSettleTime;
        shutterWidth = builder.shutterWidth;
        shutterSpeedFactor = builder.shutterSpeedFactor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SliceSettingsLightSheet settings) {
        Objects.requireNonNull(settings, "Cannot copy from null settings");
        return new Builder(settings);
    }

    @Override
    public Builder copyBuilder() {
        return new Builder(this);
    }

    @Override
    public double scanResetTime() {
        return scanResetTime;
    }

    @Override
    public double scanSettleTime() {
        return scanSettleTime;
    }

    @Override
    public double shutterWidth() {
        return shutterWidth;
    }

    @Override
    public double shutterSpeedFactor() {
        return shutterSpeedFactor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DefaultSliceSettingsLS other = (DefaultSliceSettingsLS) obj;
        return Double.compare(scanResetTime, other.scanResetTime) == 0 &&
                Double.compare(scanSettleTime, other.scanSettleTime) == 0 &&
                Double.compare(shutterWidth, other.shutterWidth) == 0 &&
                Double.compare(shutterSpeedFactor, other.shutterSpeedFactor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scanResetTime, scanSettleTime, shutterWidth, shutterSpeedFactor);
    }

    @Override
    public String toString() {
        return String.format(
                "%s[scanResetTime=%s, scanSettleTime=%s, shutterWidth=%s, shutterSpeedFactor=%s]",
                getClass().getSimpleName(),
                scanResetTime, scanSettleTime, shutterWidth, shutterSpeedFactor
        );
    }

    public static class Builder implements SliceSettingsLightSheet.Builder {

        private double scanResetTime = 3.0;
        private double scanSettleTime = 1.0;
        private double shutterWidth = 5.0;
        private double shutterSpeedFactor = 1.0;

        private Builder() {
        }

        private Builder(final SliceSettingsLightSheet settings) {
            scanResetTime = settings.scanResetTime();
            scanSettleTime = settings.scanSettleTime();
            shutterWidth = settings.shutterWidth();
            shutterSpeedFactor = settings.shutterSpeedFactor();
        }

        @Override
        public Builder shutterWidth(final double um) {
            shutterWidth = um;
            return this;
        }

        @Override
        public Builder shutterSpeedFactor(final double factor) {
            shutterSpeedFactor = factor;
            return this;
        }

        @Override
        public Builder scanSettleTime(final double ms) {
            scanSettleTime = ms;
            return this;
        }

        @Override
        public Builder scanResetTime(final double ms) {
            scanResetTime = ms;
            return this;
        }

        @Override
        public DefaultSliceSettingsLS build() {
            return new DefaultSliceSettingsLS(this);
        }
    }

}
