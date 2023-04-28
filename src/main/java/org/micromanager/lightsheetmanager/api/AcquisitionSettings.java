package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.model.DataStorage;

/**
 * Base acquisition settings for all microscopes.
 */
public interface AcquisitionSettings {

    interface Builder<T extends Builder<T>>  {

        /**
         * Sets the save directory.
         *
         * @param directory the directory
         */
        T saveDirectory(final String directory);

        /**
         * Sets the folder name.
         *
         * @param name the name of the folder
         */
        T saveNamePrefix(final String name);

        /**
         * Sets the plugin to save images during an acquisition.
         *
         * @param state true to save images during an acquisition
         */
        T saveImagesDuringAcquisition(final boolean state);

        /**
         * Sets the acquisition to demo mode.
         *
         * @param state true if in demo mode
         */
        T demoMode(final boolean state);

        /**
         * Sets the save mode for the acquisition.
         *
         * @param saveMode the save mode
         */
        T saveMode(final DataStorage.SaveMode saveMode);

        T self();

        /**
         * Creates an immutable instance of DefaultAcquisitionSettings
         *
         * @return Immutable version of DefaultAcquisitionSettings
         */
        AcquisitionSettings build();
    }

    // TODO: impl
    /**
     * Creates a Builder populated with settings of this AcquisitionSettings instance.
     *
     * @return AcquisitionSettings.Builder pre-populated with settings of this instance.
     */
    //Builder copyBuilder();

    /**
     * Returns the save name prefix.
     *
     * @return the save name prefix.
     */
    String saveNamePrefix();

    /**
     * Returns the save directory.
     *
     * @return the save directory.
     */
    String saveDirectory();

    /**
     * Returns true if saving images during an acquisition.
     *
     * @return true if saving images during an acquisition.
     */
    boolean isSavingImagesDuringAcquisition();

    /**
     * Returns true if using demo mode.
     *
     * @return true if using demo mode
     */
    boolean demoMode();

    /**
     * Returns the save mode of the acquisition.
     *
     * @return the save mode of the acquisition.
     */
    DataStorage.SaveMode saveMode();
}
