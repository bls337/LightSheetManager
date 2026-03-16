package org.micromanager.lightsheetmanager.api;

/**
 * This is an example to reference for creating a new microscope geometry interface.
 */
public interface AcquisitionSettingsExample extends AcquisitionSettings {

    interface Builder<T extends AcquisitionSettings.Builder<T>> extends AcquisitionSettings.Builder<T> {

        /**
         * Creates a new {@link AcquisitionSettingsExample} instance based on the current configuration.
         *
         * @return a new immutable settings instance
         */
        AcquisitionSettingsExample build();
    }
}
