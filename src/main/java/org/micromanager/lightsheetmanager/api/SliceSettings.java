package org.micromanager.lightsheetmanager.api;

public interface SliceSettings {

    interface Builder {
        /**
         * Sets the slice period in milliseconds.
         *
         * @param slicePeriodMs the slice period in milliseconds
         */
        Builder slicePeriod(final double slicePeriodMs);

        /**
         * Sets the sample exposure time in milliseconds.
         *
         * @param exposureMs the exposure time in milliseconds
         */
        Builder sampleExposure(final double exposureMs);

        /**
         * Sets the slice period automatically.
         *
         * @param state true to minimize the slice period
         */
        Builder minimizeSlicePeriod(final boolean state);

        /**
         * Creates an immutable instance of SliceSettings
         *
         * @return Immutable version of SliceSettings
         */
        SliceSettings build();
    }

    Builder copyBuilder();

    /**
     * Returns the slice period in milliseconds.
     *
     * @return the slice period in milliseconds
     */
    double slicePeriod();

    /**
     * Returns the sample exposure time in milliseconds.
     *
     * @return the exposure time in milliseconds
     */
    double sampleExposure();

    /**
     * Returns true if the slice period is minimized.
     *
     * @return true if slice period is minimized
     */
    boolean isSlicePeriodMinimized();
}
