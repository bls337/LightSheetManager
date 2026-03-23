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

    /**
     * Returns the imaging path that the acquisition starts on.
     *
     * @return the first view
     */
    @Override
    public int firstView() {
        return firstView_;
    }

    /**
     * Return the number of views to use during an acquisition.
     *
     * @return the number of views
     */
    @Override
    public int numViews() {
        return numViews_;
    }

    /**
     * Returns the number of slices per volume.
     *
     * @return the number of slices
     */
    @Override
    public int slicesPerView() {
        return slicesPerView_;
    }

    /**
     * Return the delay in milliseconds before switching imaging paths in milliseconds.
     *
     * @return the delay in milliseconds
     */
    @Override
    public double delayBeforeView() {
        return delayBeforeView_;
    }

    /**
     * Returns the step size in microns.
     *
     * @return the step size in microns
     */
    @Override
    public double sliceStepSize() {
        return sliceStepSize_;
    }

    /**
     * Returns the start position of the volume.
     *
     * @return the start position
     */
    @Override
    public double startPosition() {
        return startPosition_;
    }

    /**
     * Returns the center position of the volume.
     *
     * @return the center position
     */
    @Override
    public double centerPosition() {
        return centerPosition_;
    }

    /**
     * Returns the end position of the volume.
     *
     * @return the end position
     */
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

        /**
         * Create a builder with values populated from already existing DefaultVolumeSettings.
         *
         * @param settings the settings to copy
         */
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

        /**
         * Sets the number of views to use during an acquisition.
         *
         * @param numViews the number of view
         */
        @Override
        public Builder numViews(final int numViews) {
            numViews_ = numViews;
            return this;
        }

        /**
         * Sets the imaging path to start the acquisition with.
         *
         * @param firstView the first view
         */
        @Override
        public Builder firstView(final int firstView) {
            firstView_ = firstView;
            return this;
        }

        /**
         * Sets the delay between switching imaging paths in an acquisition.
         *
         * @param viewDelayMs the delay in milliseconds
         */
        @Override
        public Builder delayBeforeView(final double viewDelayMs) {
            delayBeforeView_ = viewDelayMs;
            return this;
        }

        @Override
        public Builder slicesPerView(final int n) {
            slicesPerView_ = n;
            return this;
        }

        @Override
        public Builder sliceStepSize(final double um) {
            sliceStepSize_ = um;
            return this;
        }

        // TODO: what happens when stepSize is not evenly divided by range? maybe just remove?
        /**
         * Sets the volume bounds, automatically computing numSlices and centerPosition.
         *
         * @param startPosition the start position
         * @param endPosition the end position
         * @param stepSizeUm the step size in micron
         */
        @Override
        public Builder volumeBounds(final double startPosition, final double endPosition, final double stepSizeUm) {
            startPosition_ = startPosition;
            endPosition_ = endPosition;
            sliceStepSize_ = stepSizeUm;
            centerPosition_ = (startPosition + endPosition) / 2.0;
            slicesPerView_ = (int)Math.floor((Math.abs(startPosition) + Math.abs(endPosition)) / stepSizeUm);
            return this;
        }

        /**
         * Sets the volume bounds, automatically computing stepSizeUm and centerPosition.
         *
         * @param startPosition the start position
         * @param endPosition the end position
         * @param numSlices the number of slices
         */
        @Override
        public Builder volumeBounds(final double startPosition, final double endPosition, final int numSlices) {
            startPosition_ = startPosition;
            endPosition_ = endPosition;
            slicesPerView_ = numSlices;
            centerPosition_ = (startPosition + endPosition) / 2.0;
            sliceStepSize_ = (Math.abs(startPosition) + Math.abs(endPosition)) / numSlices;
            return this;
        }

        /**
         * Sets the volume bounds, automatically computing startPosition and endPosition.
         *
         * @param centerPosition the center position
         * @param numSlices the number of slices
         * @param stepSizeUm the step size in microns
         */
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

        /**
         * Creates an immutable instance of VolumeSettings
         *
         * @return Immutable version of VolumeSettings
         */
        @Override
        public DefaultVolumeSettings build() {
            return new DefaultVolumeSettings(this);
        }
    }

}
