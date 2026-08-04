package org.micromanager.lightsheetmanager.api;

public interface TimingSettings {

    /**
     * Returns a builder initialized with the current settings.
     *
     * @return a builder to create a modified copy of these settings
     */
    Builder copyBuilder();

    /**
     * Returns the delay time in milliseconds before the scan begins.
     *
     * @return the delay time in milliseconds
     */
    double delayBeforeScanMs();

    /**
     * Returns the number of one way beam scans per slice.
     *
     * @return the number of one way beam scans per slice
     */
    int scansPerSlice();

    /**
     * Returns the time in milliseconds of one beam scan sweep.
     *
     * @return the time in milliseconds of one beam scan sweep
     */
    double scanDurationMs();

    /**
     * Returns the delay time in milliseconds before the laser trigger.
     *
     * @return the delay time in milliseconds before the laser trigger
     */
    double delayBeforeLaserMs();

    /**
     * Returns the laser trigger duration in milliseconds.
     *
     * @return the laser trigger duration in milliseconds
     */
    double laserTriggerDurationMs();

    /**
     * Returns the delay time in milliseconds before the camera is triggered.
     *
     * @return the delay time in milliseconds before the camera is triggered
     */
    double delayBeforeCameraMs();

    /**
     * Returns the camera trigger duration in milliseconds.
     *
     * @return the camera trigger duration in milliseconds
     */
    double cameraTriggerDurationMs();

    /**
     * Returns the duration in milliseconds that the camera shutter is open.
     *
     * @return the duration in milliseconds that the camera shutter is open
     */
    double cameraExposureMs();

    /**
     * Returns the duration in milliseconds of each slice.
     *
     * @return the duration in milliseconds of each slice
     */
    double sliceDurationMs();

    /**
     * Returns true if the scan direction is inverted.
     *
     * @return true if the scan direction is inverted
     */
    boolean useAlternateScanDirection();

    interface Builder {

        /**
         * Sets the delay time before scanning.
         *
         * @param delayMs the delay time in milliseconds
         */
        Builder delayBeforeScanMs(final double delayMs);

        /**
         * Sets the number of one way beam scans per slice
         *
         * @param numLineScans the number of scans
         */
        Builder scansPerSlice(final int numLineScans);

        /**
         * Sets the duration of a one way scan.
         *
         * @param durationMs the duration in milliseconds
         */
        Builder scanDurationMs(final double durationMs);

        /**
         * Sets the delay time before the laser trigger.
         *
         * @param delayMs the delay in milliseconds
         */
        Builder delayBeforeLaserMs(final double delayMs);

        /**
         * Sets the duration of the laser trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        Builder laserTriggerDurationMs(final double durationMs);

        /**
         * Sets the delay before the camera trigger is fired.
         *
         * @param delayMs the delay in milliseconds
         */
        Builder delayBeforeCameraMs(final double delayMs);

        /**
         * Sets the duration of the camera trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        Builder cameraTriggerDurationMs(final double durationMs);

        /**
         * Sets the camera exposure time.
         *
         * @param exposureMs the exposure time in milliseconds
         */
        Builder cameraExposureMs(final double exposureMs);

        /**
         * Sets the scan direction.
         *
         * @param state true to invert the scan direction
         */
        Builder useAlternateScanDirection(final boolean state);

        /**
         * Computes the slice duration from the other timing settings.
         */
        double sliceDurationMs();

        /**
         * Creates an immutable instance of TimingSettings
         *
         * @return Immutable version of TimingSettings
         */
        TimingSettings build();

    }

}
