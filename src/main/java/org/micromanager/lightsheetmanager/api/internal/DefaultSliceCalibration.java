package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceCalibration;

import java.util.Objects;

public class DefaultSliceCalibration implements SliceCalibration {

    private final double sliceSlope_;
    private final double sliceOffset_;

    private DefaultSliceCalibration(Builder builder) {
        sliceSlope_ = builder.sliceSlope_;
        sliceOffset_ = builder.sliceOffset_;
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
    public double sliceSlope() {
        return sliceSlope_;
    }

    @Override
    public double sliceOffset() {
        return sliceOffset_;
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
        return Double.compare(sliceSlope_, other.sliceSlope_) == 0 &&
                Double.compare(sliceOffset_, other.sliceOffset_) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sliceSlope_, sliceOffset_);
    }

    @Override
    public String toString() {
        return String.format("%s[sliceSlope=%s, sliceOffset=%s]",
                getClass().getSimpleName(), sliceSlope_, sliceOffset_);
    }

    public static class Builder implements SliceCalibration.Builder {

        private double sliceSlope_ = 0.0;
        private double sliceOffset_ = 0.0;

        private Builder() {
        }

        private Builder(final SliceCalibration settings) {
            sliceSlope_ = settings.sliceSlope();
            sliceOffset_ = settings.sliceOffset();
        }

        @Override
        public Builder sliceSlope(final double slope) {
            sliceSlope_ = slope;
            return this;
        }

        @Override
        public Builder sliceOffset(final double offset) {
            sliceOffset_ = offset;
            return this;
        }

        @Override
        public DefaultSliceCalibration build() {
            return new DefaultSliceCalibration(this);
        }

    }

}
