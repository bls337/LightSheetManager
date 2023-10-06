package org.micromanager.lightsheetmanager.api;

/**
 * This is an example to reference for creating a new microscope geometry interface.
 */
public interface AcquisitionSettingsEXAMPLE extends AcquisitionSettings {

    interface Builder<T extends AcquisitionSettings.Builder<T>> extends AcquisitionSettings.Builder<T> {

        /**
         * Creates an immutable instance of AcquisitionSettingsEXAMPLE
         *
         * @return Immutable version of AcquisitionSettingsEXAMPLE
         */
        AcquisitionSettingsEXAMPLE build();
    }
}
