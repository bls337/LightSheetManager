package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceSettingsLightSheet;

import java.util.Objects;

public class DefaultSliceSettingsLS implements SliceSettingsLightSheet {

    private final double scanResetTime_;
    private final double scanSettleTime_;
    private final double shutterWidth_;
    private final double shutterSpeedFactor_;

    private DefaultSliceSettingsLS(Builder builder) {
        scanResetTime_ = builder.scanResetTime_;
        scanSettleTime_ = builder.scanSettleTime_;
        shutterWidth_ = builder.shutterWidth_;
        shutterSpeedFactor_ = builder.shutterSpeedFactor_;
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
        return scanResetTime_;
    }

    @Override
    public double scanSettleTime() {
        return scanSettleTime_;
    }

    @Override
    public double shutterWidth() {
        return shutterWidth_;
    }

    @Override
    public double shutterSpeedFactor() {
        return shutterSpeedFactor_;
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
        return Double.compare(scanResetTime_, other.scanResetTime_) == 0 &&
                Double.compare(scanSettleTime_, other.scanSettleTime_) == 0 &&
                Double.compare(shutterWidth_, other.shutterWidth_) == 0 &&
                Double.compare(shutterSpeedFactor_, other.shutterSpeedFactor_) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scanResetTime_, scanSettleTime_, shutterWidth_, shutterSpeedFactor_);
    }

    @Override
    public String toString() {
        return String.format(
                "%s[scanResetTime=%s, scanSettleTime=%s, shutterWidth=%s, shutterSpeedFactor=%s]",
                getClass().getSimpleName(),
                scanResetTime_, scanSettleTime_, shutterWidth_, shutterSpeedFactor_
        );
    }

    public static class Builder implements SliceSettingsLightSheet.Builder {

        private double scanResetTime_ = 3.0;
        private double scanSettleTime_ = 1.0;
        private double shutterWidth_ = 5.0;
        private double shutterSpeedFactor_ = 1.0;

        private Builder() {
        }

        private Builder(final SliceSettingsLightSheet settings) {
            scanResetTime_ = settings.scanResetTime();
            scanSettleTime_ = settings.scanSettleTime();
            shutterWidth_ = settings.shutterWidth();
            shutterSpeedFactor_ = settings.shutterSpeedFactor();
        }

        @Override
        public Builder shutterWidth(final double um) {
            shutterWidth_ = um;
            return this;
        }

        @Override
        public Builder shutterSpeedFactor(final double factor) {
            shutterSpeedFactor_ = factor;
            return this;
        }

        @Override
        public Builder scanSettleTime(final double ms) {
            scanSettleTime_ = ms;
            return this;
        }

        @Override
        public Builder scanResetTime(final double ms) {
            scanResetTime_ = ms;
            return this;
        }

        @Override
        public DefaultSliceSettingsLS build() {
            return new DefaultSliceSettingsLS(this);
        }
    }

}
