package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.TimingSettings;

import java.util.Objects;

public class DefaultTimingSettings implements TimingSettings {

    private final int scansPerSlice_;
    private final double delayBeforeScanMs_;
    private final double scanDurationMs_;
    private final double delayBeforeLaserMs_;
    private final double laserTriggerDurationMs_;
    private final double delayBeforeCameraMs_;
    private final double cameraTriggerDurationMs_;
    private final double cameraExposureMs_;
    private final double sliceDurationMs_;
    private final boolean alternateScanDirection_;

    private DefaultTimingSettings(Builder builder) {
        scansPerSlice_ = builder.scansPerSlice_;
        delayBeforeScanMs_ = builder.delayBeforeScanMs_;
        scanDurationMs_ = builder.scanDurationMs_;
        delayBeforeLaserMs_ = builder.delayBeforeLaserMs_;
        laserTriggerDurationMs_ = builder.laserTriggerDurationMs_;
        delayBeforeCameraMs_ = builder.delayBeforeCameraMs_;
        cameraTriggerDurationMs_ = builder.cameraTriggerDurationMs_;
        cameraExposureMs_ = builder.cameraExposureMs_;
        sliceDurationMs_ = builder.sliceDurationMs();
        alternateScanDirection_ = builder.alternateScanDirection_;
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
        return delayBeforeScanMs_;
    }

    /**
     * Returns the number of one way beam scans per slice.
     *
     * @return the number of one way beam scans per slice
     */
    @Override
    public int scansPerSlice() {
        return scansPerSlice_;
    }

    /**
     * Returns the time in milliseconds of one beam scan sweep.
     *
     * @return the time in milliseconds of one beam scan sweep
     */
    @Override
    public double scanDurationMs() {
        return scanDurationMs_;
    }

    /**
     * Returns the delay time in milliseconds before the laser trigger.
     *
     * @return the delay time in milliseconds before the laser trigger
     */
    @Override
    public double delayBeforeLaserMs() {
        return delayBeforeLaserMs_;
    }

    /**
     * Returns the laser trigger duration in milliseconds.
     *
     * @return the laser trigger duration in milliseconds
     */
    @Override
    public double laserTriggerDurationMs() {
        return laserTriggerDurationMs_;
    }

    /**
     * Returns the delay time in milliseconds before the camera is triggered.
     *
     * @return the delay time in milliseconds before the camera is triggered
     */
    @Override
    public double delayBeforeCameraMs() {
        return delayBeforeCameraMs_;
    }

    /**
     * Returns the camera trigger duration in milliseconds.
     *
     * @return the camera trigger duration in milliseconds
     */
    @Override
    public double cameraTriggerDurationMs() {
        return cameraTriggerDurationMs_;
    }

    /**
     * Returns the duration in milliseconds that the camera shutter is open.
     *
     * @return the duration in milliseconds that the camera shutter is open
     */
    @Override
    public double cameraExposureMs() {
        return cameraExposureMs_;
    }

    /**
     * Returns the duration in milliseconds of each slice.
     *
     * @return the duration in milliseconds of each slice
     */
    @Override
    public double sliceDurationMs() {
        return sliceDurationMs_;
    }

    /**
     * Returns true if the scan direction is inverted.
     *
     * @return true if the scan direction is inverted
     */
    @Override
    public boolean useAlternateScanDirection() {
        return alternateScanDirection_;
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
        return scansPerSlice_ == other.scansPerSlice_ &&
                Double.compare(delayBeforeScanMs_, other.delayBeforeScanMs_) == 0 &&
                Double.compare(scanDurationMs_, other.scanDurationMs_) == 0 &&
                Double.compare(delayBeforeLaserMs_, other.delayBeforeLaserMs_) == 0 &&
                Double.compare(laserTriggerDurationMs_, other.laserTriggerDurationMs_) == 0 &&
                Double.compare(delayBeforeCameraMs_, other.delayBeforeCameraMs_) == 0 &&
                Double.compare(cameraTriggerDurationMs_, other.cameraTriggerDurationMs_) == 0 &&
                Double.compare(cameraExposureMs_, other.cameraExposureMs_) == 0 &&
                Double.compare(sliceDurationMs_, other.sliceDurationMs_) == 0 &&
                alternateScanDirection_ == other.alternateScanDirection_;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scansPerSlice_, delayBeforeScanMs_, scanDurationMs_, delayBeforeLaserMs_,
                laserTriggerDurationMs_, delayBeforeCameraMs_, cameraTriggerDurationMs_, cameraExposureMs_,
                sliceDurationMs_, alternateScanDirection_);
    }

    @Override
    public String toString() {
        return String.format(
                "%s[scansPerSlice=%s, delayBeforeScanMs=%s, scanDurationMs=%s,"
                        + " delayBeforeLaserMs=%s, laserTriggerDurationMs=%s,"
                        + " delayBeforeCameraMs=%s, cameraTriggerDurationMs=%s,"
                        + " cameraExposureMs=%s, sliceDurationMs=%s, alternateScanDirection=%s]",
                getClass().getSimpleName(),
                scansPerSlice_, delayBeforeScanMs_, scanDurationMs_, delayBeforeLaserMs_,
                laserTriggerDurationMs_, delayBeforeCameraMs_, cameraTriggerDurationMs_,
                cameraExposureMs_, sliceDurationMs_, alternateScanDirection_
        );
    }

    public static class Builder implements TimingSettings.Builder {

        private int scansPerSlice_ = 1;
        private double delayBeforeScanMs_ = 0.0;
        private double scanDurationMs_ = 10.0;
        private double delayBeforeLaserMs_ = 1.0;
        private double laserTriggerDurationMs_ = 1.0;
        private double delayBeforeCameraMs_ = 0.0;
        private double cameraTriggerDurationMs_ = 1.0;
        private double cameraExposureMs_ = 1.0;
        private boolean alternateScanDirection_ = false;

        private Builder() {
        }

        private Builder(final TimingSettings settings) {
            scansPerSlice_ = settings.scansPerSlice();
            delayBeforeScanMs_ = settings.delayBeforeScanMs();
            scanDurationMs_ = settings.scanDurationMs();
            delayBeforeLaserMs_ = settings.delayBeforeLaserMs();
            laserTriggerDurationMs_ = settings.laserTriggerDurationMs();
            delayBeforeCameraMs_ = settings.delayBeforeCameraMs();
            cameraTriggerDurationMs_ = settings.cameraTriggerDurationMs();
            cameraExposureMs_ = settings.cameraExposureMs();
            alternateScanDirection_ = settings.useAlternateScanDirection();
        }

        /**
         * Sets the delay time before scanning.
         *
         * @param delayMs the delay time in milliseconds
         */
        @Override
        public Builder delayBeforeScanMs(final double delayMs) {
            delayBeforeScanMs_ = delayMs;
            return this;
        }

        /**
         * Sets the number of one way beam scans per slice
         *
         * @param numScans the number of scans
         */
        @Override
        public Builder scansPerSlice(final int numScans) {
            scansPerSlice_ = numScans;
            return this;
        }

        /**
         * Sets the duration of a one way scan.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public Builder scanDurationMs(final double durationMs) {
            scanDurationMs_ = durationMs;
            return this;
        }

        /**
         * Sets the delay time before the laser trigger.
         *
         * @param delayMs the delay in milliseconds
         */
        @Override
        public Builder delayBeforeLaserMs(final double delayMs) {
            delayBeforeLaserMs_ = delayMs;
            return this;
        }

        /**
         * Sets the duration of the laser trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public Builder laserTriggerDurationMs(final double durationMs) {
            laserTriggerDurationMs_ = durationMs;
            return this;
        }

        /**
         * Sets the delay before the camera trigger is fired.
         *
         * @param delayMs the delay in milliseconds
         */
        @Override
        public Builder delayBeforeCameraMs(final double delayMs) {
            delayBeforeCameraMs_ = delayMs;
            return this;
        }

        /**
         * Sets the duration of the camera trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public Builder cameraTriggerDurationMs(final double durationMs) {
            cameraTriggerDurationMs_ = durationMs;
            return this;
        }

        /**
         * Sets the camera exposure time.
         *
         * @param exposureMs the exposure time in milliseconds
         */
        @Override
        public Builder cameraExposureMs(final double exposureMs) {
            cameraExposureMs_ = exposureMs;
            return this;
        }

        /**
         * Sets the scan direction.
         *
         * @param state true to invert the scan direction
         */
        @Override
        public Builder useAlternateScanDirection(final boolean state) {
            alternateScanDirection_ = state;
            return this;
        }

        /**
         * Computes the slice duration from the other timing settings.
         */
        @Override
        public double sliceDurationMs() {
            return Math.max(Math.max(
                            delayBeforeScanMs_ + (scanDurationMs_ * scansPerSlice_), // scan time
                            delayBeforeLaserMs_ + laserTriggerDurationMs_            // laser time
                    ),
                    delayBeforeCameraMs_ + cameraTriggerDurationMs_                  // camera time
            );
        }

        @Override
        public String toString() {
            return String.format("%s[scansPerSlice=%s, delayBeforeScanMs=%s, scanDurationMs=%s, " +
                            "delayBeforeLaserMs=%s, laserTriggerDurationMs=%s, delayBeforeCameraMs=%s, " +
                            "cameraTriggerDurationMs=%s, cameraExposureMs=%s, " +
                            "sliceDurationMs=%s, alternateScanDirection=%s]",
                    getClass().getSimpleName(),
                    scansPerSlice_, delayBeforeScanMs_, scanDurationMs_, delayBeforeLaserMs_, laserTriggerDurationMs_,
                    delayBeforeCameraMs_, cameraTriggerDurationMs_, cameraExposureMs_,
                    sliceDurationMs(), alternateScanDirection_
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
