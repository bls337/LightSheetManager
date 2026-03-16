package org.micromanager.lightsheetmanager.api;

// corresponds to slice settings panel on acquisition tab when in light sheet mode (virtual slit)
public interface SliceSettingsLightSheet {

    /**
     * Returns a builder initialized with the current settings.
     *
     * @return a builder to create a modified copy of these settings
     */
    SliceSettingsLightSheet.Builder copyBuilder();

    /**
     * Returns the shutter width in microns.
     *
     * @return the shutter width in microns
     */
    double shutterWidth();

    /**
     * Returns the shutter speed factor. (1 / speedFactor)
     *
     * @return the shutter speed factor
     */
    double shutterSpeedFactor();

    /**
     * Returns the scan settle time in milliseconds.
     *
     * @return the scan settle time in milliseconds
     */
    double scanSettleTime();

    /**
     * Returns the scan reset time in milliseconds.
     *
     * @return the scan reset time in milliseconds
     */
    double scanResetTime();

    interface Builder {
        /**
         * Sets the shutter width in microns.
         *
         * @param um the shutter width in microns
         * @return {@code this} builder
         */
        Builder shutterWidth(final double um);

        /**
         * Sets the shutter speed factor.
         *
         * @param factor the shutter speed factor
         * @return {@code this} builder
         */
        Builder shutterSpeedFactor(final double factor);

        /**
         * Sets the scan settle time in milliseconds.
         *
         * @param ms the scan settle time in milliseconds
         * @return {@code this} builder
         */
        Builder scanSettleTime(final double ms);

        /**
         * Sets the scan settle reset in milliseconds.
         *
         * @param ms the scan reset time in milliseconds
         * @return {@code this} builder
         */
        Builder scanResetTime(final double ms);

        /**
         * Creates a new {@link SliceSettingsLightSheet} instance based on the current configuration.
         *
         * @return a new immutable settings instance
         */
        SliceSettingsLightSheet build();
    }

}
