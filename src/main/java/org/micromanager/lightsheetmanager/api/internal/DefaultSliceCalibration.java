package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceCalibration;

import java.util.Objects;

public class DefaultSliceCalibration implements SliceCalibration {

    private final double slope;
    private final double offset;

    private DefaultSliceCalibration(Builder builder) {
        slope = builder.slope;
        offset = builder.offset;
    }

    // Note: used by GSON library for deserialization
    private DefaultSliceCalibration() {
        this(new Builder());
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
        return slope;
    }

    @Override
    public double offset() {
        return offset;
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
        return Double.compare(slope, other.slope) == 0 &&
                Double.compare(offset, other.offset) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slope, offset);
    }

    @Override
    public String toString() {
        return String.format("%s[slope=%s, offset=%s]",
                getClass().getSimpleName(), slope, offset);
    }

    public static class Builder implements SliceCalibration.Builder {

        private double slope = 0.0;
        private double offset = 0.0;

        private Builder() {
        }

        private Builder(final SliceCalibration settings) {
            slope = settings.slope();
            offset = settings.offset();
        }

        @Override
        public Builder slope(final double slope) {
            this.slope = slope;
            return this;
        }

        @Override
        public Builder offset(final double offset) {
            this.offset = offset;
            return this;
        }

        @Override
        public DefaultSliceCalibration build() {
            return new DefaultSliceCalibration(this);
        }

    }

}
