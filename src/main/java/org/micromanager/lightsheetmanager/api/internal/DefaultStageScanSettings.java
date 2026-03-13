package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.StageScanSettings;

public class DefaultStageScanSettings implements StageScanSettings {

    public static class Builder implements StageScanSettings.Builder {

        private double accelerationFactor_ = 1;
        private int overshootDistance_ = 0;
        private double retraceSpeed_ =  67.0;
        private double scanAngleFirstView_ = 45.0;
        private boolean scanReturnToOriginalPosition_ = false;
        private boolean scanFromCurrentPosition_ = false;
        private boolean scanFromNegativeDirection_ = false;

        public Builder() {
        }

        private Builder(DefaultStageScanSettings settings) {
            accelerationFactor_ = settings.accelerationFactor_;
            overshootDistance_ = settings.overshootDistance_;
            retraceSpeed_ = settings.retraceSpeed_;
            scanAngleFirstView_ = settings.scanAngleFirstView_;
            scanReturnToOriginalPosition_ = settings.scanReturnToOriginalPosition_;
            scanFromCurrentPosition_ = settings.scanFromCurrentPosition_;
            scanFromNegativeDirection_ = settings.scanFromNegativeDirection_;
        }

        @Override
        public StageScanSettings.Builder accelerationFactor(final double factor) {
            accelerationFactor_ = factor;
            return this;
        }

        @Override
        public StageScanSettings.Builder overshootDistance(final int distance) {
            overshootDistance_ = distance;
            return this;
        }

        @Override
        public StageScanSettings.Builder retraceSpeed(final double speed) {
            retraceSpeed_ = speed;
            return this;
        }

        @Override
        public StageScanSettings.Builder scanAngleFirstView(final double angle) {
            scanAngleFirstView_ = angle;
            return this;
        }

        @Override
        public StageScanSettings.Builder scanReturnToOriginalPosition(final boolean state) {
            scanReturnToOriginalPosition_ = state;
            return this;
        }

        @Override
        public StageScanSettings.Builder scanFromCurrentPosition(final boolean state) {
            scanFromCurrentPosition_ = state;
            return this;
        }

        @Override
        public StageScanSettings.Builder scanFromNegativeDirection(final boolean state) {
            scanFromNegativeDirection_ = state;
            return this;
        }

        @Override
        public DefaultStageScanSettings build() {
            return new DefaultStageScanSettings(this);
        }

    }

    private final double accelerationFactor_;
    private final int overshootDistance_;
    private final double retraceSpeed_;
    private final double scanAngleFirstView_;
    private final boolean scanReturnToOriginalPosition_;
    private final boolean scanFromCurrentPosition_;
    private final boolean scanFromNegativeDirection_;

    private DefaultStageScanSettings(Builder builder) {
        accelerationFactor_ = builder.accelerationFactor_;
        overshootDistance_ = builder.overshootDistance_;
        retraceSpeed_ = builder.retraceSpeed_;
        scanAngleFirstView_ = builder.scanAngleFirstView_;
        scanReturnToOriginalPosition_ = builder.scanReturnToOriginalPosition_;
        scanFromCurrentPosition_ = builder.scanFromCurrentPosition_;
        scanFromNegativeDirection_ = builder.scanFromNegativeDirection_;
    }

    @Override
    public DefaultStageScanSettings.Builder copyBuilder() {
        return new DefaultStageScanSettings.Builder(this);
    }

    @Override
    public double accelerationFactor() {
        return accelerationFactor_;
    }

    @Override
    public int overshootDistance() {
        return overshootDistance_;
    }

    /**
     * Return the scan retrace speed percent.
     *
     * @return the scan retrace speed
     */
    @Override
    public double retraceSpeed() {
        return retraceSpeed_;
    }

    @Override
    public double scanAngleFirstView() {
        return scanAngleFirstView_;
    }

    @Override
    public boolean scanReturnToOriginalPosition() {
        return scanReturnToOriginalPosition_;
    }

    @Override
    public boolean scanFromCurrentPosition() {
        return scanFromCurrentPosition_;
    }

    @Override
    public boolean scanFromNegativeDirection() {
        return scanFromNegativeDirection_;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[scanAccelerationFactor=%s, scanOvershootDistance=%s, scanRetraceSpeed=%s, scanAngleFirstView=%s]",
                getClass().getSimpleName(),
                accelerationFactor_, overshootDistance_, retraceSpeed_, scanAngleFirstView_
        );
    }

}
