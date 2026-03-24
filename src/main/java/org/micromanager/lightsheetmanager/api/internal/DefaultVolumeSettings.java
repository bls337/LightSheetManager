package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.VolumeSettings;

import java.util.Objects;

public class DefaultVolumeSettings implements VolumeSettings {

    private final int firstView_;
    private final int numViews_;
    private final int slicesPerView_;
    private final double delayBeforeView_;
    private final double sliceStepSize_;
    private final double startPosition_;
    private final double centerPosition_;
    private final double endPosition_;

    private DefaultVolumeSettings(Builder builder) {
        firstView_ = builder.firstView_;
        numViews_ = builder.numViews_;
        slicesPerView_ = builder.slicesPerView_;
        delayBeforeView_ = builder.delayBeforeView_;
        sliceStepSize_ = builder.sliceStepSize_;
        startPosition_ = builder.startPosition_;
        centerPosition_ = builder.centerPosition_;
        endPosition_ = builder.endPosition_;
    }

    // Note: used by GSON library for deserialization
    private DefaultVolumeSettings() {
        this(new Builder());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(VolumeSettings settings) {
        Objects.requireNonNull(settings, "Cannot copy from null settings");
        return new Builder(settings);
    }

    @Override
    public Builder copyBuilder() {
        return new Builder(this);
    }

    @Override
    public int firstView() {
        return firstView_;
    }

    @Override
    public int numViews() {
        return numViews_;
    }

    @Override
    public int slicesPerView() {
        return slicesPerView_;
    }

    @Override
    public double delayBeforeView() {
        return delayBeforeView_;
    }

    @Override
    public double sliceStepSize() {
        return sliceStepSize_;
    }

    @Override
    public double startPosition() {
        return startPosition_;
    }

    @Override
    public double centerPosition() {
        return centerPosition_;
    }

    @Override
    public double endPosition() {
        return endPosition_;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DefaultVolumeSettings other = (DefaultVolumeSettings) obj;
        return firstView_ == other.firstView_ &&
                numViews_ == other.numViews_ &&
                slicesPerView_ == other.slicesPerView_ &&
                Double.compare(delayBeforeView_, other.delayBeforeView_) == 0 &&
                Double.compare(sliceStepSize_, other.sliceStepSize_) == 0 &&
                Double.compare(startPosition_, other.startPosition_) == 0 &&
                Double.compare(centerPosition_, other.centerPosition_) == 0 &&
                Double.compare(endPosition_, other.endPosition_) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstView_, numViews_, slicesPerView_, delayBeforeView_, sliceStepSize_,
                startPosition_, centerPosition_, endPosition_);
    }

    @Override
    public String toString() {
        return String.format(
                "%s[firstView=%s, numViews=%s, numSlices=%s, viewDelayMs=%s, stepSizeUm=%s, "
                        + "startPosition=%s, centerPosition=%s, endPosition=%s]",
                getClass().getSimpleName(),
                firstView_, numViews_, slicesPerView_, delayBeforeView_, sliceStepSize_,
                startPosition_, centerPosition_, endPosition_
        );
    }

    public static class Builder implements VolumeSettings.Builder {

        private int firstView_ = 1;
        private int numViews_ = 1;
        private int slicesPerView_ = 10;
        private double delayBeforeView_ = 50;
        private double sliceStepSize_ = 0.5;
        private double startPosition_ = 0.0;
        private double centerPosition_ = 0.0;
        private double endPosition_ = 0.0;

        private Builder() {
        }

        private Builder(final VolumeSettings settings) {
            firstView_ = settings.firstView();
            numViews_ = settings.numViews();
            slicesPerView_ = settings.slicesPerView();
            delayBeforeView_ = settings.delayBeforeView();
            sliceStepSize_ = settings.sliceStepSize();
            startPosition_ = settings.startPosition();
            centerPosition_ = settings.centerPosition();
            endPosition_ = settings.endPosition();
        }

        @Override
        public Builder numViews(final int numViews) {
            numViews_ = numViews;
            return this;
        }

        @Override
        public Builder firstView(final int firstView) {
            firstView_ = firstView;
            return this;
        }

        @Override
        public Builder delayBeforeView(final double viewDelayMs) {
            delayBeforeView_ = viewDelayMs;
            return this;
        }

        @Override
        public Builder slicesPerView(final int numSlices) {
            slicesPerView_ = numSlices;
            return this;
        }

        @Override
        public Builder sliceStepSize(final double stepSizeUm) {
            sliceStepSize_ = stepSizeUm;
            return this;
        }

        // TODO: what happens when stepSize is not evenly divided by range? maybe just remove?
        @Override
        public Builder volumeBounds(final double startPosition, final double endPosition, final double stepSizeUm) {
            startPosition_ = startPosition;
            endPosition_ = endPosition;
            sliceStepSize_ = stepSizeUm;
            centerPosition_ = (startPosition + endPosition) / 2.0;
            slicesPerView_ = (int)Math.floor((Math.abs(startPosition) + Math.abs(endPosition)) / stepSizeUm);
            return this;
        }

        @Override
        public Builder volumeBounds(final double startPosition, final double endPosition, final int numSlices) {
            startPosition_ = startPosition;
            endPosition_ = endPosition;
            slicesPerView_ = numSlices;
            centerPosition_ = (startPosition + endPosition) / 2.0;
            sliceStepSize_ = (Math.abs(startPosition) + Math.abs(endPosition)) / numSlices;
            return this;
        }

        @Override
        public Builder volumeBounds(final double centerPosition, final int numSlices, final double stepSizeUm) {
            final double halfDistance = (stepSizeUm * numSlices) / 2.0;
            centerPosition_ = centerPosition;
            sliceStepSize_ = stepSizeUm;
            slicesPerView_ = numSlices;
            startPosition_ = centerPosition - halfDistance;
            endPosition_ = centerPosition + halfDistance;
            return this;
        }

        @Override
        public DefaultVolumeSettings build() {
            return new DefaultVolumeSettings(this);
        }

    }

}
