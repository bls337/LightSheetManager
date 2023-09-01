package org.micromanager.lightsheetmanager.api.internal.scape;

import org.micromanager.lightsheetmanager.api.scape.SingleAxisSettings;
import org.micromanager.lightsheetmanager.model.devices.vendor.SingleAxis;

public class DefaultSingleAxisSettings implements SingleAxisSettings {

    public static class Builder implements SingleAxisSettings.Builder {

        private int period_ = 1;
        private double amplitude_ = 1.0;
        private SingleAxis.Pattern pattern_ = SingleAxis.Pattern.SINE;

        public Builder() {
        }

        private Builder(DefaultSingleAxisSettings singleAxisSettings) {
            period_ = singleAxisSettings.period_;
            amplitude_ = singleAxisSettings.amplitude_;
            pattern_ = singleAxisSettings.pattern_;
        }

        @Override
        public SingleAxisSettings.Builder amplitude(double amplitude) {
            amplitude_ = amplitude;
            return this;
        }

        @Override
        public SingleAxisSettings.Builder period(int period) {
            period_ = period;
            return this;
        }

        @Override
        public SingleAxisSettings.Builder pattern(SingleAxis.Pattern pattern) {
            pattern_ = pattern;
            return this;
        }

        @Override
        public DefaultSingleAxisSettings build() {
            return new DefaultSingleAxisSettings(this);
        }
    }

    private final int period_;
    private final double amplitude_;
    private final SingleAxis.Pattern pattern_;

    private DefaultSingleAxisSettings(Builder builder) {
        period_ = builder.period_;
        amplitude_ = builder.amplitude_;
        pattern_ = builder.pattern_;
    }

    @Override
    public int period() {
        return period_;
    }

    @Override
    public double amplitude() {
        return amplitude_;
    }

    @Override
    public SingleAxis.Pattern pattern() {
        return pattern_;
    }
}
