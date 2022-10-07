package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceSettings;

public class DefaultSliceSettings implements SliceSettings {


    public static class Builder implements SliceSettings.Builder {

        double shutterWidth_ = 10.0;
        double shutterSpeedFactor_ = 1.0;
        double scanSettleTime_ = 100.0;
        double scanResetTime_ = 100.0;

        public Builder() {
        }

        public Builder(final double shutterWidth,
                       final double shutterSpeedFactor,
                       final double scanSettleTime,
                       final double scanResetTime) {
            shutterWidth_ = shutterWidth;
            shutterSpeedFactor_ = shutterSpeedFactor;
            scanSettleTime_ = scanSettleTime;
            scanResetTime_ = scanResetTime;
        }

        @Override
        public SliceSettings.Builder shutterWidth(double um) {
            shutterWidth_ = um;
            return this;
        }

        @Override
        public SliceSettings.Builder shutterSpeedFactor(double factor) {
            shutterSpeedFactor_ = factor;
            return this;
        }

        @Override
        public SliceSettings.Builder scanSettleTime(double ms) {
            scanSettleTime_ = ms;
            return this;
        }

        @Override
        public SliceSettings.Builder scanResetTime(double ms) {
            scanResetTime_ = ms;
            return this;
        }

        @Override
        public DefaultSliceSettings build() {
            return new DefaultSliceSettings(
                    shutterWidth_,
                    shutterSpeedFactor_,
                    scanSettleTime_,
                    scanResetTime_
            );
        }
    }

    final double shutterWidth_;
    final double shutterSpeedFactor_;
    final double scanSettleTime_;
    final double scanResetTime_;

    private DefaultSliceSettings(
            final double shutterWidth,
            final double shutterSpeedFactor,
            final double scanSettleTime,
            final double scanResetTime) {
        shutterWidth_ = shutterWidth;
        shutterSpeedFactor_ = shutterSpeedFactor;
        scanSettleTime_ = scanSettleTime;
        scanResetTime_ = scanResetTime;
    }

    @Override
    public SliceSettings.Builder copyBuilder() {
        return new Builder(
                shutterWidth_,
                shutterSpeedFactor_,
                scanSettleTime_,
                scanResetTime_
        );
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
    public double scanSettleTime() {
        return scanSettleTime_;
    }

    @Override
    public double scanResetTime() {
        return scanResetTime_;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[shutterWidth=%s, shutterSpeedFactor=%s, scanSettleTime=%s, scanResetTime=%s]",
                getClass().getSimpleName(),
                shutterWidth_, shutterSpeedFactor_, scanSettleTime_, scanResetTime_
        );
    }
}
