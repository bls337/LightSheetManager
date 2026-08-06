package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.StageScanSettings;

import java.util.Objects;

public class DefaultStageScanSettings implements StageScanSettings {

    private final boolean enabled;
    private final double accelerationFactor;
    private final int overshootDistance;
    private final double retraceSpeed;
    private final double firstViewAngle;
    private final boolean returnToStart;
    private final boolean fromCurrentPosition;
    private final boolean fromNegativeDirection;

    private DefaultStageScanSettings(Builder builder) {
        enabled = builder.enabled;
        accelerationFactor = builder.accelerationFactor;
        overshootDistance = builder.overshootDistance;
        retraceSpeed = builder.retraceSpeed;
        firstViewAngle = builder.firstViewAngle;
        returnToStart = builder.returnToStart;
        fromCurrentPosition = builder.fromCurrentPosition;
        fromNegativeDirection = builder.fromNegativeDirection;
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
        return enabled;
    }

    @Override
    public double accelerationFactor() {
        return accelerationFactor;
    }

    @Override
    public int overshootDistance() {
        return overshootDistance;
    }

    @Override
    public double retraceSpeed() {
        return retraceSpeed;
    }

    @Override
    public double firstViewAngle() {
        return firstViewAngle;
    }

    @Override
    public boolean returnToStart() {
        return returnToStart;
    }

    @Override
    public boolean fromCurrentPosition() {
        return fromCurrentPosition;
    }

    @Override
    public boolean fromNegativeDirection() {
        return fromNegativeDirection;
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
        return enabled == other.enabled &&
                Double.compare(other.accelerationFactor, accelerationFactor) == 0 &&
                overshootDistance == other.overshootDistance &&
                Double.compare(other.retraceSpeed, retraceSpeed) == 0 &&
                Double.compare(other.firstViewAngle, firstViewAngle) == 0 &&
                returnToStart == other.returnToStart &&
                fromCurrentPosition == other.fromCurrentPosition &&
                fromNegativeDirection == other.fromNegativeDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                enabled,
                accelerationFactor,
                overshootDistance,
                retraceSpeed,
                firstViewAngle,
                returnToStart,
                fromCurrentPosition,
                fromNegativeDirection
        );
    }

    @Override
    public String toString() {
        return String.format(
                "%s[enabled=%s, accelerationFactor=%s, overshootDistance=%s, retraceSpeed=%s, firstViewAngle=%s, " +
                        "returnToStart=%s, fromCurrentPosition=%s, fromNegativeDirection=%s]",
                getClass().getSimpleName(),
                enabled, accelerationFactor, overshootDistance, retraceSpeed, firstViewAngle,
                returnToStart, fromCurrentPosition, fromNegativeDirection
        );
    }

    public static class Builder implements StageScanSettings.Builder {

        private boolean enabled = false;
        private double accelerationFactor = 1.0;
        private int overshootDistance = 0;
        private double retraceSpeed =  67.0;
        private double firstViewAngle = 45.0;
        private boolean returnToStart = false;
        private boolean fromCurrentPosition = false;
        private boolean fromNegativeDirection = false;

        private Builder() {
        }

        private Builder(StageScanSettings settings) {
            enabled = settings.enabled();
            accelerationFactor = settings.accelerationFactor();
            overshootDistance = settings.overshootDistance();
            retraceSpeed = settings.retraceSpeed();
            firstViewAngle = settings.firstViewAngle();
            returnToStart = settings.returnToStart();
            fromCurrentPosition = settings.fromCurrentPosition();
            fromNegativeDirection = settings.fromNegativeDirection();
        }

        @Override
        public Builder enabled(final boolean state) {
            enabled = state;
            return this;
        }

        @Override
        public Builder accelerationFactor(final double factor) {
            accelerationFactor = factor;
            return this;
        }

        @Override
        public Builder overshootDistance(final int distance) {
            overshootDistance = distance;
            return this;
        }

        @Override
        public Builder retraceSpeed(final double speed) {
            retraceSpeed = speed;
            return this;
        }

        @Override
        public Builder firstViewAngle(final double angle) {
            firstViewAngle = angle;
            return this;
        }

        @Override
        public Builder returnToStart(final boolean state) {
            returnToStart = state;
            return this;
        }

        @Override
        public Builder fromCurrentPosition(final boolean state) {
            fromCurrentPosition = state;
            return this;
        }

        @Override
        public Builder fromNegativeDirection(final boolean state) {
            fromNegativeDirection = state;
            return this;
        }

        @Override
        public DefaultStageScanSettings build() {
            return new DefaultStageScanSettings(this);
        }

    }

}
