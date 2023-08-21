package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceCalibration;

public class DefaultSliceCalibration implements SliceCalibration {

    public static class Builder implements SliceCalibration.Builder {

        private double sliceSlope_ = 0.0;
        private double sliceOffset_ = 0.0;

        public Builder() {
        }

        private Builder(final SliceCalibration sliceCalibration) {
            sliceSlope_ = sliceCalibration.sliceSlope();
            sliceOffset_ = sliceCalibration.sliceOffset();
        }

        @Override
        public SliceCalibration.Builder sliceSlope(final double slope) {
            sliceSlope_ = slope;
            System.out.println(sliceSlope_);
            return this;
        }

        @Override
        public SliceCalibration.Builder sliceOffset(final double offset) {
            sliceOffset_ = offset;
            return this;
        }

        @Override
        public DefaultSliceCalibration build() {
            return new DefaultSliceCalibration(this);
        }
    }

    private final double sliceSlope_;
    private final double sliceOffset_;

    private DefaultSliceCalibration(Builder builder) {
        sliceSlope_ = builder.sliceSlope_;
        sliceOffset_ = builder.sliceOffset_;
    }

    @Override
    public DefaultSliceCalibration.Builder copyBuilder() {
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

}
