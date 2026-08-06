package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.TimingSettings;

import java.util.Objects;

public class DefaultTimingSettings implements TimingSettings {

    private final int scansPerSlice;
    private final double delayBeforeScanMs;
    private final double scanDurationMs;
    private final double delayBeforeLaserMs;
    private final double laserTriggerDurationMs;
    private final double delayBeforeCameraMs;
    private final double cameraTriggerDurationMs;
    private final double cameraExposureMs;
    private final double sliceDurationMs;
    private final boolean alternateScanDirection;

    private DefaultTimingSettings(Builder builder) {
        scansPerSlice = builder.scansPerSlice;
        delayBeforeScanMs = builder.delayBeforeScanMs;
        scanDurationMs = builder.scanDurationMs;
        delayBeforeLaserMs = builder.delayBeforeLaserMs;
        laserTriggerDurationMs = builder.laserTriggerDurationMs;
        delayBeforeCameraMs = builder.delayBeforeCameraMs;
        cameraTriggerDurationMs = builder.cameraTriggerDurationMs;
        cameraExposureMs = builder.cameraExposureMs;
        sliceDurationMs = builder.sliceDurationMs();
        alternateScanDirection = builder.alternateScanDirection;
    }

    // Note: used by GSON library for deserialization
    private DefaultTimingSettings() {
        this(new Builder());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(TimingSettings settings) {
        Objects.requireNonNull(settings, "Cannot copy from null settings");
        return new Builder(settings);
    }

    /**
     * Creates a Builder populated with settings of this TimingSettings instance.
     *
     * @return TimingSettings.Builder pre-populated with settings of this instance.
     */
    @Override
    public DefaultTimingSettings.Builder copyBuilder() {
        return new Builder(this);
    }

    /**
     * Return the delay time in milliseconds before the scan begins.
     *
     * @return the delay time in milliseconds
     */
    @Override
    public double delayBeforeScanMs() {
        return delayBeforeScanMs;
    }

    /**
     * Returns the number of one way beam scans per slice.
     *
     * @return the number of one way beam scans per slice
     */
    @Override
    public int scansPerSlice() {
        return scansPerSlice;
    }

    /**
     * Returns the time in milliseconds of one beam scan sweep.
     *
     * @return the time in milliseconds of one beam scan sweep
     */
    @Override
    public double scanDurationMs() {
        return scanDurationMs;
    }

    /**
     * Returns the delay time in milliseconds before the laser trigger.
     *
     * @return the delay time in milliseconds before the laser trigger
     */
    @Override
    public double delayBeforeLaserMs() {
        return delayBeforeLaserMs;
    }

    /**
     * Returns the laser trigger duration in milliseconds.
     *
     * @return the laser trigger duration in milliseconds
     */
    @Override
    public double laserTriggerDurationMs() {
        return laserTriggerDurationMs;
    }

    /**
     * Returns the delay time in milliseconds before the camera is triggered.
     *
     * @return the delay time in milliseconds before the camera is triggered
     */
    @Override
    public double delayBeforeCameraMs() {
        return delayBeforeCameraMs;
    }

    /**
     * Returns the camera trigger duration in milliseconds.
     *
     * @return the camera trigger duration in milliseconds
     */
    @Override
    public double cameraTriggerDurationMs() {
        return cameraTriggerDurationMs;
    }

    /**
     * Returns the duration in milliseconds that the camera shutter is open.
     *
     * @return the duration in milliseconds that the camera shutter is open
     */
    @Override
    public double cameraExposureMs() {
        return cameraExposureMs;
    }

    /**
     * Returns the duration in milliseconds of each slice.
     *
     * @return the duration in milliseconds of each slice
     */
    @Override
    public double sliceDurationMs() {
        return sliceDurationMs;
    }

    /**
     * Returns true if the scan direction is inverted.
     *
     * @return true if the scan direction is inverted
     */
    @Override
    public boolean useAlternateScanDirection() {
        return alternateScanDirection;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DefaultTimingSettings other = (DefaultTimingSettings) obj;
        return scansPerSlice == other.scansPerSlice &&
                Double.compare(delayBeforeScanMs, other.delayBeforeScanMs) == 0 &&
                Double.compare(scanDurationMs, other.scanDurationMs) == 0 &&
                Double.compare(delayBeforeLaserMs, other.delayBeforeLaserMs) == 0 &&
                Double.compare(laserTriggerDurationMs, other.laserTriggerDurationMs) == 0 &&
                Double.compare(delayBeforeCameraMs, other.delayBeforeCameraMs) == 0 &&
                Double.compare(cameraTriggerDurationMs, other.cameraTriggerDurationMs) == 0 &&
                Double.compare(cameraExposureMs, other.cameraExposureMs) == 0 &&
                Double.compare(sliceDurationMs, other.sliceDurationMs) == 0 &&
                alternateScanDirection == other.alternateScanDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scansPerSlice, delayBeforeScanMs, scanDurationMs, delayBeforeLaserMs,
                laserTriggerDurationMs, delayBeforeCameraMs, cameraTriggerDurationMs, cameraExposureMs,
                sliceDurationMs, alternateScanDirection);
    }

    @Override
    public String toString() {
        return String.format(
                "%s[scansPerSlice=%s, delayBeforeScanMs=%s, scanDurationMs=%s,"
                        + " delayBeforeLaserMs=%s, laserTriggerDurationMs=%s,"
                        + " delayBeforeCameraMs=%s, cameraTriggerDurationMs=%s,"
                        + " cameraExposureMs=%s, sliceDurationMs=%s, alternateScanDirection=%s]",
                getClass().getSimpleName(),
                scansPerSlice, delayBeforeScanMs, scanDurationMs, delayBeforeLaserMs,
                laserTriggerDurationMs, delayBeforeCameraMs, cameraTriggerDurationMs,
                cameraExposureMs, sliceDurationMs, alternateScanDirection
        );
    }

    public static class Builder implements TimingSettings.Builder {

        private int scansPerSlice = 1;
        private double delayBeforeScanMs = 0.0;
        private double scanDurationMs = 10.0;
        private double delayBeforeLaserMs = 1.0;
        private double laserTriggerDurationMs = 1.0;
        private double delayBeforeCameraMs = 0.0;
        private double cameraTriggerDurationMs = 1.0;
        private double cameraExposureMs = 1.0;
        private boolean alternateScanDirection = false;

        private Builder() {
        }

        private Builder(final TimingSettings settings) {
            scansPerSlice = settings.scansPerSlice();
            delayBeforeScanMs = settings.delayBeforeScanMs();
            scanDurationMs = settings.scanDurationMs();
            delayBeforeLaserMs = settings.delayBeforeLaserMs();
            laserTriggerDurationMs = settings.laserTriggerDurationMs();
            delayBeforeCameraMs = settings.delayBeforeCameraMs();
            cameraTriggerDurationMs = settings.cameraTriggerDurationMs();
            cameraExposureMs = settings.cameraExposureMs();
            alternateScanDirection = settings.useAlternateScanDirection();
        }

        /**
         * Sets the delay time before scanning.
         *
         * @param delayMs the delay time in milliseconds
         */
        @Override
        public Builder delayBeforeScanMs(final double delayMs) {
            delayBeforeScanMs = delayMs;
            return this;
        }

        /**
         * Sets the number of one way beam scans per slice
         *
         * @param numScans the number of scans
         */
        @Override
        public Builder scansPerSlice(final int numScans) {
            scansPerSlice = numScans;
            return this;
        }

        /**
         * Sets the duration of a one way scan.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public Builder scanDurationMs(final double durationMs) {
            scanDurationMs = durationMs;
            return this;
        }

        /**
         * Sets the delay time before the laser trigger.
         *
         * @param delayMs the delay in milliseconds
         */
        @Override
        public Builder delayBeforeLaserMs(final double delayMs) {
            delayBeforeLaserMs = delayMs;
            return this;
        }

        /**
         * Sets the duration of the laser trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public Builder laserTriggerDurationMs(final double durationMs) {
            laserTriggerDurationMs = durationMs;
            return this;
        }

        /**
         * Sets the delay before the camera trigger is fired.
         *
         * @param delayMs the delay in milliseconds
         */
        @Override
        public Builder delayBeforeCameraMs(final double delayMs) {
            delayBeforeCameraMs = delayMs;
            return this;
        }

        /**
         * Sets the duration of the camera trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public Builder cameraTriggerDurationMs(final double durationMs) {
            cameraTriggerDurationMs = durationMs;
            return this;
        }

        /**
         * Sets the camera exposure time.
         *
         * @param exposureMs the exposure time in milliseconds
         */
        @Override
        public Builder cameraExposureMs(final double exposureMs) {
            cameraExposureMs = exposureMs;
            return this;
        }

        /**
         * Sets the scan direction.
         *
         * @param state true to invert the scan direction
         */
        @Override
        public Builder useAlternateScanDirection(final boolean state) {
            alternateScanDirection = state;
            return this;
        }

        /**
         * Computes the slice duration from the other timing settings.
         */
        @Override
        public double sliceDurationMs() {
            return Math.max(Math.max(
                            delayBeforeScanMs + (scanDurationMs * scansPerSlice), // scan time
                            delayBeforeLaserMs + laserTriggerDurationMs            // laser time
                    ),
                    delayBeforeCameraMs + cameraTriggerDurationMs                  // camera time
            );
        }

        @Override
        public String toString() {
            return String.format("%s[scansPerSlice=%s, delayBeforeScanMs=%s, scanDurationMs=%s, " +
                            "delayBeforeLaserMs=%s, laserTriggerDurationMs=%s, delayBeforeCameraMs=%s, " +
                            "cameraTriggerDurationMs=%s, cameraExposureMs=%s, " +
                            "sliceDurationMs=%s, alternateScanDirection=%s]",
                    getClass().getSimpleName(),
                    scansPerSlice, delayBeforeScanMs, scanDurationMs, delayBeforeLaserMs, laserTriggerDurationMs,
                    delayBeforeCameraMs, cameraTriggerDurationMs, cameraExposureMs,
                    sliceDurationMs(), alternateScanDirection
            );
        }

        /**
         * Creates an immutable instance of TimingSettings
         *
         * @return Immutable version of TimingSettings
         */
        @Override
        public DefaultTimingSettings build() {
            return new DefaultTimingSettings(this);
        }

    }

}
