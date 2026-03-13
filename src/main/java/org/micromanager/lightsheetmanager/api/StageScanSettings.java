package org.micromanager.lightsheetmanager.api;

/**
 * This interface implements settings for ASI stages with the scan module.
 */
public interface StageScanSettings {

    interface Builder {

        Builder accelerationFactor(final double factor);

        Builder overshootDistance(final int distance);

        Builder retraceSpeed(final double speed);

        Builder scanAngleFirstView(final double angle);

        Builder scanReturnToOriginalPosition(final boolean state);

        Builder scanFromCurrentPosition(final boolean state);

        Builder scanFromNegativeDirection(final boolean state);

        /**
         * Creates an immutable instance of ScanSettings
         *
         * @return Immutable version of ScanSettings
         */
        StageScanSettings build();
    }

    Builder copyBuilder();

    double accelerationFactor();

    int overshootDistance();

    double retraceSpeed();

    double scanAngleFirstView();

    boolean scanReturnToOriginalPosition();

    boolean  scanFromCurrentPosition();

    boolean scanFromNegativeDirection();
}
