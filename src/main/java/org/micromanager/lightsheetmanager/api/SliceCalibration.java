package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.internal.DefaultSliceCalibration;

/**
 * detection + illumination calibration
 * <p>
 * Using the diSPIM geometry this represents the piezo/slice calibration settings.
 */
public interface SliceCalibration {

    interface Builder {

        Builder slope(final double slope);

        Builder offset(final double offset);

        /**
         * Creates an immutable instance of DefaultSliceCalibration
         *
         * @return Immutable version of DefaultSliceCalibration
         */
        DefaultSliceCalibration build();
    }

    Builder copyBuilder();

    double slope();
    double offset();


}
