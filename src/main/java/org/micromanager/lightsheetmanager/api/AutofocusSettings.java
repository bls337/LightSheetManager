package org.micromanager.lightsheetmanager.api;


import org.micromanager.lightsheetmanager.api.data.AutofocusMode;
import org.micromanager.lightsheetmanager.api.data.AutofocusType;

/**
 * Autofocus settings.
 *
 */
public interface AutofocusSettings {

    interface Builder {

        /**
         * Sets the number of images to capture in the autofocus routine.
         *
         * @param numImages the number of images
         */
        Builder numImages(final int numImages);

        /**
         * Sets the spacing between images in the autofocus routine.
         *
         * @param stepSize the step size in microns
         */
        Builder stepSizeUm(final double stepSize);

//        /**
//         * Sets the tolerance in microns for the autofocus algorithm.
//         *
//         * @param value the tolerance in microns
//         */
//        Builder toleranceUm(final double value);

        /**
         * Set to {@code true} to show the images in the live view window.
         *
         * @param state {@code true} to show images
         */
        Builder showImages(final boolean state);

        /**
         * Set to {@code true} to show a graph of the data.
         *
         * @param state {@code true} to show the graph
         */
        Builder showGraph(final boolean state);

        /**
         * Selects whether to fix the piezo or the sheet for an autofocus routine.
         *
         * @param mode the autofocus mode
         */
        Builder mode(final AutofocusMode mode);

        /**
         * Sets the type of scoring algorithm to use when running autofocus.
         *
         * @param type the scoring algorithm
         */
        Builder scoringMethod(final AutofocusType type);

        /**
         * Set the channel to run the autofocus routine on.
         *
         * @param channel the channel to run autofocus on
         */
        Builder channel(final String channel);

        // TODO: maybe denote that these are related to acquisitions

//        /**
//         * Run autofocus every time we move to the next channel during an acquisition.
//         *
//         * @param state true to enable autofocus every stage pass
//         */
//        Builder useEveryStagePass(final boolean state);
//
//        /**
//         * Run an autofocus routine before starting the acquisition.
//         *
//         * @param state true or false
//         */
//        Builder useBeforeAcquisition(final boolean state);


//        Builder maxOffset(final double maxOffset); // +/- um
//
//        Builder autoUpdateOffset(final boolean state);
//
//        Builder autoUpdateMaxOffset(final double um);

        /**
         * Creates an immutable instance of AutofocusSettings
         *
         * @return Immutable version of AutofocusSettings
         */
        AutofocusSettings build();
    }

   /**
    * Creates a Builder populated with settings of this AutofocusSettings instance.
    *
    * @return AutofocusSettings.Builder pre-populated with settings of this instance.
    */
    Builder copyBuilder();

    /**
     * Returns the number of images used in for autofocus routine.
     *
     * @return the number of images
     */
    int numImages();

    /**
     * Returns the step size between images in microns.
     *
     * @return the step size in microns
     */
    double stepSizeUm();

//    /**
//     * Returns the coefficient of determination used in the autofocus routine.
//     *
//     * @return the coefficient of determination
//     */
//    double toleranceUm();

    /**
     * Returns the autofocus mode being used.
     *
     * @return the autofocus mode
     */
    AutofocusMode mode();

    /**
     * Returns the type of scoring algorithm used for autofocus.
     *
     * @return the type of scoring algorithm
     */
    AutofocusType scoringMethod();

    /**
     * Returns the channel autofocus is being run on.
     *
     * @return the autofocus channel
     */
    String channel();

//    /**
//     * Returns true if autofocus is run every stage pass.
//     *
//     * @return true if autofocus is run every stage pass
//     */
//    boolean useEveryStagePass();
//
//    /**
//     * Returns true if we run an autofocus routine before starting an acquisition.
//     *
//     * @return true if enabled
//     */
//    boolean useBeforeAcquisition();

   /**
    * What is this?
    *
    * @return
    */
//    double maxOffset(); // used during acquisitions
//
//    boolean autoUpdateOffset();
//
//    double autoUpdateMaxOffset();

}
