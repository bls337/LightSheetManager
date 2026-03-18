package org.micromanager.lightsheetmanager.api;

public interface SliceSettings {

    /**
     * Returns a builder initialized with the current settings.
     *
     * @return a builder to create a modified copy of these settings
     */
    Builder copyBuilder();

    /**
     * Returns the slice period in milliseconds.
     *
     * @return the slice period in milliseconds
     */
    double period();

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
    boolean periodMinimized();

    interface Builder {

        /**
         * Sets the slice period in milliseconds.
         *
         * @param slicePeriodMs the slice period in milliseconds
         */
        Builder period(final double slicePeriodMs);

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
        Builder periodMinimized(final boolean state);

        /**
         * Creates an immutable instance of SliceSettings
         *
         * @return Immutable version of SliceSettings
         */
        SliceSettings build();

    }

}
