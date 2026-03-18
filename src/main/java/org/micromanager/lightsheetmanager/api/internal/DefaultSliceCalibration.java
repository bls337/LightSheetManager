package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceCalibration;

import java.util.Objects;

public class DefaultSliceCalibration implements SliceCalibration {

    private final double slope_;
    private final double offset_;

    private DefaultSliceCalibration(Builder builder) {
        slope_ = builder.slope_;
        offset_ = builder.offset_;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SliceCalibration settings) {
        Objects.requireNonNull(settings, "Cannot copy from null settings");
        return new Builder(settings);
    }

    @Override
    public Builder copyBuilder() {
        return new Builder(this);
    }

    @Override
    public double slope() {
        return slope_;
    }

    @Override
    public double offset() {
        return offset_;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DefaultSliceCalibration other = (DefaultSliceCalibration) obj;
        return Double.compare(slope_, other.slope_) == 0 &&
                Double.compare(offset_, other.offset_) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slope_, offset_);
    }

    @Override
    public String toString() {
        return String.format("%s[sliceSlope=%s, sliceOffset=%s]",
                getClass().getSimpleName(), slope_, offset_);
    }

    public static class Builder implements SliceCalibration.Builder {
        private double slope_ = 0.0;
        private double offset_ = 0.0;

        private Builder() {
        }

        private Builder(final SliceCalibration settings) {
            slope_ = settings.slope();
            offset_ = settings.offset();
        }

        @Override
        public Builder slope(final double slope) {
            slope_ = slope;
            return this;
        }

        @Override
        public Builder offset(final double offset) {
            offset_ = offset;
            return this;
        }

        @Override
        public DefaultSliceCalibration build() {
            return new DefaultSliceCalibration(this);
        }

    }

}
