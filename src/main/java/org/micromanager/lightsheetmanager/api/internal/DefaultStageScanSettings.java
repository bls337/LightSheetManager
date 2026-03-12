package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.StageScanSettings;

public class DefaultStageScanSettings implements StageScanSettings {

    public static class Builder implements StageScanSettings.Builder {

        private double scanAccelerationFactor_ = 1;
        private int scanOvershootDistance_ = 0;
        private double scanRetraceSpeed_ =  67.0;
        private double scanAngleFirstView_ = 45.0;
        private boolean scanReturnToOriginalPosition_ = false;
        private boolean scanFromCurrentPosition_ = false;
        private boolean scanFromNegativeDirection_ = false;

        public Builder() {
        }

        private Builder(DefaultStageScanSettings scanSettings) {
            scanAccelerationFactor_ = scanSettings.scanAccelerationFactor_;
            scanOvershootDistance_ = scanSettings.scanOvershootDistance_;
            scanRetraceSpeed_ = scanSettings.scanRetraceSpeed_;
            scanAngleFirstView_ = scanSettings.scanAngleFirstView_;
            scanReturnToOriginalPosition_ = scanSettings.scanReturnToOriginalPosition_;
            scanFromCurrentPosition_ = scanSettings.scanFromCurrentPosition_;
            scanFromNegativeDirection_ = scanSettings.scanFromNegativeDirection_;
        }

        @Override
        public StageScanSettings.Builder scanAccelerationFactor(final double factor) {
            scanAccelerationFactor_ = factor;
            return this;
        }

        @Override
        public StageScanSettings.Builder scanOvershootDistance(final int distance) {
            scanOvershootDistance_ = distance;
            return this;
        }

        @Override
        public StageScanSettings.Builder scanRetraceSpeed(final double speed) {
            scanRetraceSpeed_ = speed;
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

    private final double scanAccelerationFactor_;
    private final int scanOvershootDistance_;
    private final double scanRetraceSpeed_;
    private final double scanAngleFirstView_;
    private final boolean scanReturnToOriginalPosition_;
    private final boolean scanFromCurrentPosition_;
    private final boolean scanFromNegativeDirection_;

    private DefaultStageScanSettings(Builder builder) {
        scanAccelerationFactor_ = builder.scanAccelerationFactor_;
        scanOvershootDistance_ = builder.scanOvershootDistance_;
        scanRetraceSpeed_ = builder.scanRetraceSpeed_;
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
    public double scanAccelerationFactor() {
        return scanAccelerationFactor_;
    }

    @Override
    public int scanOvershootDistance() {
        return scanOvershootDistance_;
    }

    @Override
    public double scanRetraceSpeed() {
        return scanRetraceSpeed_;
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
                scanAccelerationFactor_, scanOvershootDistance_, scanRetraceSpeed_, scanAngleFirstView_
        );
    }

}
