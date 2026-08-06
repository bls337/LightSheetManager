package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.VolumeSettings;

import java.util.Objects;

public class DefaultVolumeSettings implements VolumeSettings {

    private final int firstView;
    private final int numViews;
    private final int slicesPerView;
    private final double delayBeforeView;
    private final double sliceStepSize;
    private final double startPosition;
    private final double centerPosition;
    private final double endPosition;

    private DefaultVolumeSettings(Builder builder) {
        firstView = builder.firstView;
        numViews = builder.numViews;
        slicesPerView = builder.slicesPerView;
        delayBeforeView = builder.delayBeforeView;
        sliceStepSize = builder.sliceStepSize;
        startPosition = builder.startPosition;
        centerPosition = builder.centerPosition;
        endPosition = builder.endPosition;
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
        return firstView;
    }

    @Override
    public int numViews() {
        return numViews;
    }

    @Override
    public int slicesPerView() {
        return slicesPerView;
    }

    @Override
    public double delayBeforeView() {
        return delayBeforeView;
    }

    @Override
    public double sliceStepSize() {
        return sliceStepSize;
    }

    @Override
    public double startPosition() {
        return startPosition;
    }

    @Override
    public double centerPosition() {
        return centerPosition;
    }

    @Override
    public double endPosition() {
        return endPosition;
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
        return firstView == other.firstView &&
                numViews == other.numViews &&
                slicesPerView == other.slicesPerView &&
                Double.compare(delayBeforeView, other.delayBeforeView) == 0 &&
                Double.compare(sliceStepSize, other.sliceStepSize) == 0 &&
                Double.compare(startPosition, other.startPosition) == 0 &&
                Double.compare(centerPosition, other.centerPosition) == 0 &&
                Double.compare(endPosition, other.endPosition) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstView, numViews, slicesPerView, delayBeforeView, sliceStepSize,
                startPosition, centerPosition, endPosition);
    }

    @Override
    public String toString() {
        return String.format(
                "%s[firstView=%s, numViews=%s, numSlices=%s, viewDelayMs=%s, stepSizeUm=%s, "
                        + "startPosition=%s, centerPosition=%s, endPosition=%s]",
                getClass().getSimpleName(),
                firstView, numViews, slicesPerView, delayBeforeView, sliceStepSize,
                startPosition, centerPosition, endPosition
        );
    }

    public static class Builder implements VolumeSettings.Builder {

        private int firstView = 1;
        private int numViews = 1;
        private int slicesPerView = 10;
        private double delayBeforeView = 50;
        private double sliceStepSize = 0.5;
        private double startPosition = 0.0;
        private double centerPosition = 0.0;
        private double endPosition = 0.0;

        private Builder() {
        }

        private Builder(final VolumeSettings settings) {
            firstView = settings.firstView();
            numViews = settings.numViews();
            slicesPerView = settings.slicesPerView();
            delayBeforeView = settings.delayBeforeView();
            sliceStepSize = settings.sliceStepSize();
            startPosition = settings.startPosition();
            centerPosition = settings.centerPosition();
            endPosition = settings.endPosition();
        }

        @Override
        public Builder numViews(final int numViews) {
            this.numViews = numViews;
            return this;
        }

        @Override
        public Builder firstView(final int firstView) {
            this.firstView = firstView;
            return this;
        }

        @Override
        public Builder delayBeforeView(final double viewDelayMs) {
            delayBeforeView = viewDelayMs;
            return this;
        }

        @Override
        public Builder slicesPerView(final int numSlices) {
            slicesPerView = numSlices;
            return this;
        }

        @Override
        public Builder sliceStepSize(final double stepSizeUm) {
            sliceStepSize = stepSizeUm;
            return this;
        }

        // TODO: what happens when stepSize is not evenly divided by range? maybe just remove?
        @Override
        public Builder volumeBounds(final double startPosition, final double endPosition, final double stepSizeUm) {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            sliceStepSize = stepSizeUm;
            centerPosition = (startPosition + endPosition) / 2.0;
            slicesPerView = (int)Math.floor((Math.abs(startPosition) + Math.abs(endPosition)) / stepSizeUm);
            return this;
        }

        @Override
        public Builder volumeBounds(final double startPosition, final double endPosition, final int numSlices) {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            slicesPerView = numSlices;
            centerPosition = (startPosition + endPosition) / 2.0;
            sliceStepSize = (Math.abs(startPosition) + Math.abs(endPosition)) / numSlices;
            return this;
        }

        @Override
        public Builder volumeBounds(final double centerPosition, final int numSlices, final double stepSizeUm) {
            final double halfDistance = (stepSizeUm * numSlices) / 2.0;
            this.centerPosition = centerPosition;
            sliceStepSize = stepSizeUm;
            slicesPerView = numSlices;
            startPosition = centerPosition - halfDistance;
            endPosition = centerPosition + halfDistance;
            return this;
        }

        @Override
        public DefaultVolumeSettings build() {
            return new DefaultVolumeSettings(this);
        }

    }

}
