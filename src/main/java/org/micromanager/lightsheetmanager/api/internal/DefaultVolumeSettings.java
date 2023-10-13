package org.micromanager.lightsheetmanager.api.internal;


import org.micromanager.lightsheetmanager.api.VolumeSettings;

public class DefaultVolumeSettings implements VolumeSettings {

    public static class Builder implements VolumeSettings.Builder {
        private int firstView_ = 1;
        private int numViews_ = 1;
        private int slicesPerView_ = 10;
        private double delayBeforeView_ = 50;
        private double sliceStepSize_ = 0.5;
        private double startPosition_ = 0.0;
        private double centerPosition_ = 0.0;
        private double endPosition_ = 0.0;

        public Builder() {
        }

        /**
         * Create a builder with values populated from already existing DefaultVolumeSettings.
         *
         * @param volumeSettings the settings to copy
         */
        private Builder(DefaultVolumeSettings volumeSettings) {
            firstView_ = volumeSettings.firstView_;
            numViews_ = volumeSettings.numViews_;
            slicesPerView_ = volumeSettings.slicesPerView_;
            delayBeforeView_ = volumeSettings.delayBeforeView_;
            sliceStepSize_ = volumeSettings.sliceStepSize_;
            startPosition_ = volumeSettings.startPosition_;
            centerPosition_ = volumeSettings.centerPosition_;
            endPosition_ = volumeSettings.endPosition_;
        }

        /**
         * Sets the number of views to use during an acquisition.
         *
         * @param numViews the number of view
         */
        @Override
        public VolumeSettings.Builder numViews(final int numViews) {
            numViews_ = numViews;
            return this;
        }

        /**
         * Sets the imaging path to start the acquisition with.
         *
         * @param firstView the first view
         */
        @Override
        public VolumeSettings.Builder firstView(final int firstView) {
            firstView_ = firstView;
            return this;
        }

        /**
         * Sets the delay between switching imaging paths in an acquisition.
         *
         * @param viewDelayMs the delay in milliseconds
         */
        @Override
        public VolumeSettings.Builder delayBeforeView(final double viewDelayMs) {
            delayBeforeView_ = viewDelayMs;
            return this;
        }

        @Override
        public VolumeSettings.Builder slicesPerView(final int n) {
            slicesPerView_ = n;
            return this;
        }

        @Override
        public VolumeSettings.Builder sliceStepSize(final double um) {
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
        public VolumeSettings.Builder volumeBounds(final double startPosition, final double endPosition, final double stepSizeUm) {
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
        public VolumeSettings.Builder volumeBounds(final double startPosition, final double endPosition, final int numSlices) {
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
        public VolumeSettings.Builder volumeBounds(final double centerPosition, final int numSlices, final double stepSizeUm) {
            final double halfDistance = (stepSizeUm * numSlices) / 2.0;
            centerPosition_ = centerPosition;
            sliceStepSize_ = stepSizeUm;
            slicesPerView_ = numSlices;
            startPosition_ = centerPosition - halfDistance;
            endPosition_ = centerPosition + halfDistance;
            return this;
        }

        public int slicesPerVolume() {
            return slicesPerView_;
        }

        public int numViews() {
            return numViews_;
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

    public Builder copyBuilder() {
        return new Builder(this);
    }

    /**
     * Returns the imaging path that the acquisition starts on.
     *
     * @return the first view
     */
    public int firstView() {
        return firstView_;
    }

    /**
     * Return the number of views to use during an acquisition.
     *
     * @return the number of views
     */

    public int numViews() {
        return numViews_;
    }

    /**
     * Returns the number of slices per volume.
     *
     * @return the number of slices
     */
    public int slicesPerView() {
        return slicesPerView_;
    }

    /**
     * Return the delay in milliseconds before switching imaging paths in milliseconds.
     *
     * @return the delay in milliseconds
     */
    public double delayBeforeView() {
        return delayBeforeView_;
    }

    /**
     * Returns the step size in microns.
     *
     * @return the step size in microns
     */
    public double sliceStepSize() {
        return sliceStepSize_;
    }

    /**
     * Returns the start position of the volume.
     *
     * @return the start position
     */
    public double startPosition() {
        return startPosition_;
    }

    /**
     * Returns the center position of the volume.
     *
     * @return the center position
     */
    public double centerPosition() {
        return centerPosition_;
    }

    /**
     * Returns the end position of the volume.
     *
     * @return the end position
     */
    public double endPosition() {
        return endPosition_;
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

}
