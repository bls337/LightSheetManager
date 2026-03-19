package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.StageScanSettings;

import java.util.Objects;

public class DefaultStageScanSettings implements StageScanSettings {

    private final boolean enabled_;
    private final double accelerationFactor_;
    private final int overshootDistance_;
    private final double retraceSpeed_;
    private final double firstViewAngle_;
    private final boolean returnToStart_;
    private final boolean fromCurrentPosition_;
    private final boolean fromNegativeDirection_;

    private DefaultStageScanSettings(Builder builder) {
        enabled_ = builder.enabled_;
        accelerationFactor_ = builder.accelerationFactor_;
        overshootDistance_ = builder.overshootDistance_;
        retraceSpeed_ = builder.retraceSpeed_;
        firstViewAngle_ = builder.firstViewAngle_;
        returnToStart_ = builder.returnToStart_;
        fromCurrentPosition_ = builder.fromCurrentPosition_;
        fromNegativeDirection_ = builder.fromNegativeDirection_;
    }

    // Note: used by GSON library for deserialization
    private DefaultStageScanSettings() {
        this(new Builder());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(StageScanSettings settings) {
        Objects.requireNonNull(settings, "Cannot copy from null settings");
        return new Builder(settings);
    }

    @Override
    public StageScanSettings.Builder copyBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean enabled() {
        return enabled_;
    }

    @Override
    public double accelerationFactor() {
        return accelerationFactor_;
    }

    @Override
    public int overshootDistance() {
        return overshootDistance_;
    }

    @Override
    public double retraceSpeed() {
        return retraceSpeed_;
    }

    @Override
    public double firstViewAngle() {
        return firstViewAngle_;
    }

    @Override
    public boolean returnToStart() {
        return returnToStart_;
    }

    @Override
    public boolean fromCurrentPosition() {
        return fromCurrentPosition_;
    }

    @Override
    public boolean fromNegativeDirection() {
        return fromNegativeDirection_;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DefaultStageScanSettings other = (DefaultStageScanSettings) obj;
        return enabled_ == other.enabled_ &&
                Double.compare(other.accelerationFactor_, accelerationFactor_) == 0 &&
                overshootDistance_ == other.overshootDistance_ &&
                Double.compare(other.retraceSpeed_, retraceSpeed_) == 0 &&
                Double.compare(other.firstViewAngle_, firstViewAngle_) == 0 &&
                returnToStart_ == other.returnToStart_ &&
                fromCurrentPosition_ == other.fromCurrentPosition_ &&
                fromNegativeDirection_ == other.fromNegativeDirection_;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                enabled_,
                accelerationFactor_,
                overshootDistance_,
                retraceSpeed_,
                firstViewAngle_,
                returnToStart_,
                fromCurrentPosition_,
                fromNegativeDirection_
        );
    }

    @Override
    public String toString() {
        return String.format(
                "%s[enabled=%s, accelerationFactor=%s, overshootDistance=%s, retraceSpeed=%s, firstViewAngle=%s, " +
                        "returnToStart=%s, fromCurrentPosition=%s, fromNegativeDirection=%s]",
                getClass().getSimpleName(),
                enabled_, accelerationFactor_, overshootDistance_, retraceSpeed_, firstViewAngle_,
                returnToStart_, fromCurrentPosition_, fromNegativeDirection_
        );
    }

    public static class Builder implements StageScanSettings.Builder {

        private boolean enabled_ = false;
        private double accelerationFactor_ = 1.0;
        private int overshootDistance_ = 0;
        private double retraceSpeed_ =  67.0;
        private double firstViewAngle_ = 45.0;
        private boolean returnToStart_ = false;
        private boolean fromCurrentPosition_ = false;
        private boolean fromNegativeDirection_ = false;

        private Builder() {
        }

        private Builder(StageScanSettings settings) {
            enabled_ = settings.enabled();
            accelerationFactor_ = settings.accelerationFactor();
            overshootDistance_ = settings.overshootDistance();
            retraceSpeed_ = settings.retraceSpeed();
            firstViewAngle_ = settings.firstViewAngle();
            returnToStart_ = settings.returnToStart();
            fromCurrentPosition_ = settings.fromCurrentPosition();
            fromNegativeDirection_ = settings.fromNegativeDirection();
        }

        @Override
        public Builder enabled(final boolean state) {
            enabled_ = state;
            return this;
        }

        @Override
        public Builder accelerationFactor(final double factor) {
            accelerationFactor_ = factor;
            return this;
        }

        @Override
        public Builder overshootDistance(final int distance) {
            overshootDistance_ = distance;
            return this;
        }

        @Override
        public Builder retraceSpeed(final double speed) {
            retraceSpeed_ = speed;
            return this;
        }

        @Override
        public Builder firstViewAngle(final double angle) {
            firstViewAngle_ = angle;
            return this;
        }

        @Override
        public Builder returnToStart(final boolean state) {
            returnToStart_ = state;
            return this;
        }

        @Override
        public Builder fromCurrentPosition(final boolean state) {
            fromCurrentPosition_ = state;
            return this;
        }

        @Override
        public Builder fromNegativeDirection(final boolean state) {
            fromNegativeDirection_ = state;
            return this;
        }

        @Override
        public DefaultStageScanSettings build() {
            return new DefaultStageScanSettings(this);
        }

    }

}
