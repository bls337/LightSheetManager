package org.micromanager.lightsheetmanager.api;

// TODO: some way of specifying the view order for systems with up to 8 cameras

/**
 * Volume settings for microscope geometries that support volumetric imaging.
 */
public interface VolumeSettings {

    interface Builder {

        /**
         * Sets the number of views to use during an acquisition.
         *
         * @param numViews the number of views
         */
        Builder numViews(final int numViews);

        /**
         * Sets the imaging path to start the acquisition with.
         *
         * @param firstView the first view
         */
        Builder firstView(final int firstView);

        /**
         * Sets the delay between switching imaging paths in milliseconds.
         *
         * @param viewDelayMs the delay in milliseconds
         */
        Builder delayBeforeView(final double viewDelayMs);

        Builder slicesPerView(final int n);

        Builder sliceStepSize(final double um);

        /**
         * Sets the volume bounds, automatically computing numSlices and centerPosition.
         *
         * @param startPosition the start position
         * @param endPosition the end position
         * @param stepSizeUm the step size in micron
         */
        Builder volumeBounds(final double startPosition, final double endPosition, final double stepSizeUm);

        /**
         * Sets the volume bounds, automatically computing stepSizeUm and centerPosition.
         *
         * @param startPosition the start position
         * @param endPosition the end position
         * @param numSlices the number of slices
         */
        Builder volumeBounds(final double startPosition, final double endPosition, final int numSlices);

        /**
         * Sets the volume bounds, automatically computing startPosition and endPosition.
         *
         * @param centerPosition the center position
         * @param numSlices the number of slices
         * @param stepSizeUm the step size in microns
         */
        Builder volumeBounds(final double centerPosition, final int numSlices, final double stepSizeUm);

        /**
         * Creates an immutable instance of VolumeSettings
         *
         * @return Immutable version of VolumeSettings
         */
        VolumeSettings build();
    }

    /**
     * Creates a Builder populated with settings of this VolumeSettings instance.
     *
     * @return VolumeSettings.Builder pre-populated with settings of this instance.
     */
    Builder copyBuilder();

    /**
     * Return the number of views to use during an acquisition.
     *
     * @return the number of views
     */
    int numViews();

    /**
     * Returns the number of slices per volume.
     *
     * @return the number of slices
     */
    int slicesPerView();

    /**
     * Returns the imaging path that the acquisition starts on.
     *
     * @return the first view
     */
    int firstView();

    /**
     * Return the delay in milliseconds before switching imaging paths in an acquisitions.
     *
     * @return the delay in milliseconds
     */
    double delayBeforeView();

    /**
     * Returns the step size in microns.
     *
     * @return the step size in microns
     */
    double sliceStepSize();

    /**
     * Returns the start position of the volume.
     *
     * @return the start position
     */
    double startPosition();

    /**
     * Returns the center position of the volume.
     *
     * @return the center position
     */
    double centerPosition();

    /**
     * Returns the end position of the volume.
     *
     * @return the end position
     */
    double endPosition();

}
