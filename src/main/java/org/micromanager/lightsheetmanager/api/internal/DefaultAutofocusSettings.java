package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AutofocusSettings;
import org.micromanager.lightsheetmanager.api.data.AutofocusMode;
import org.micromanager.lightsheetmanager.api.data.AutofocusType;

public class DefaultAutofocusSettings implements AutofocusSettings {

    public static class Builder implements AutofocusSettings.Builder {

        private int numImages_ = 10;
        private double stepSizeUm_ = 1.0;
        //private double toleranceUm_ = 1.0;
        private boolean showImages_ = false;
        private boolean showGraph_ = false;
        private AutofocusMode mode_ = AutofocusMode.FIXED_PIEZO_SWEEP_SLICE;
        private AutofocusType scoringMethod_ = AutofocusType.VOLATH5;
        private String channel_ = "";
//        private boolean useEveryStagePass_ = false;
//        private boolean useBeforeAcquisition_ = false;
//        private double maxOffset_ = 1.0;
//        private boolean autoUpdateOffset_ = true;
//        private double autoUpdateMaxOffset_ = 5.0;

        public Builder() {
        }

        private Builder(final DefaultAutofocusSettings autofocusSettings) {
            numImages_ = autofocusSettings.numImages_;
            stepSizeUm_ = autofocusSettings.stepSizeUm_;
            //toleranceUm_ = autofocusSettings.toleranceUm_;
            showImages_ = autofocusSettings.showImages_;
            showGraph_ = autofocusSettings.showGraph_;
            mode_ = autofocusSettings.mode_;
            scoringMethod_ = autofocusSettings.scoringMethod_;
            channel_ = autofocusSettings.channel_;
            //useEveryStagePass_ = autofocusSettings.useEveryStagePass_;
            //useBeforeAcquisition_ = autofocusSettings.useBeforeAcquisition_;
//            maxOffset_ = autoUpdateMaxOffset_;
//            autoUpdateOffset_ = autofocusSettings.autoUpdateOffset_;
//            autoUpdateMaxOffset_ = autofocusSettings.autoUpdateMaxOffset_;
        }

        /**
         * Sets the number of images to capture in the autofocus routine.
         *
         * @param numImages the number of images
         */
        @Override
        public AutofocusSettings.Builder numImages(int numImages) {
            numImages_ = numImages;
            return this;
        }

        /**
         * Sets the spacing between images in the autofocus routine.
         *
         * @param stepSize the step size in microns
         */
        @Override
        public AutofocusSettings.Builder stepSizeUm(final double stepSize) {
            stepSizeUm_ = stepSize;
            return this;
        }

//        /**
//         * Sets the tolerance in microns for the autofocus algorithm.
//         *
//         * @param value the tolerance in microns
//         */
//        @Override
//        public AutofocusSettings.Builder toleranceUm(final double value) {
//            toleranceUm_ = value;
//            return this;
//        }

        /**
         * Set to {@code true} to show the images in the live view window.
         *
         * @param state {@code true} to show images
         */
        @Override
        public AutofocusSettings.Builder showImages(boolean state) {
            showImages_ = state;
            return this;
        }

        /**
         * Set to {@code true} to show a graph of the data.
         *
         * @param state {@code true} to show the graph
         */
        @Override
        public AutofocusSettings.Builder showGraph(boolean state) {
            showGraph_ = state;
            return this;
        }

        /**
         * Selects whether to fix the piezo or the sheet for an autofocus routine.
         *
         * @param mode the autofocus mode
         */
        @Override
        public AutofocusSettings.Builder mode(final AutofocusMode mode) {
            mode_ = mode;
            return this;
        }

        /**
         * Sets the type of scoring algorithm to use when running autofocus.
         *
         * @param type the scoring algorithm
         */
        @Override
        public AutofocusSettings.Builder scoringMethod(final AutofocusType type) {
            scoringMethod_ = type;
            return this;
        }

        /**
         * Set the channel to run the autofocus routine on.
         *
         * @param channel the channel to run autofocus on
         */
        @Override
        public AutofocusSettings.Builder channel(final String channel) {
            channel_ = channel;
            return this;
        }

//        /**
//         * Run autofocus every time we move to the next channel during an acquisition.
//         *
//         * @param state true to enable autofocus every stage pass
//         */
//        @Override
//        public AutofocusSettings.Builder useEveryStagePass(final boolean state) {
//            useEveryStagePass_ = state;
//            return this;
//        }
//
//        /**
//         * Run an autofocus routine before starting the acquisition.
//         *
//         * @param state true or false
//         */
//        @Override
//        public AutofocusSettings.Builder useBeforeAcquisition(final boolean state) {
//            useBeforeAcquisition_ = state;
//            return this;
//        }

//        @Override
//        public AutofocusSettings.Builder maxOffset(final double maxOffset) {
//            maxOffset_ = maxOffset;
//            return this;
//        }
//
//        @Override
//        public AutofocusSettings.Builder autoUpdateOffset(final boolean state) {
//            autoUpdateOffset_ = state;
//            return this;
//        }
//
//        @Override
//        public AutofocusSettings.Builder autoUpdateMaxOffset(final double um) {
//            autoUpdateMaxOffset_ = um;
//            return this;
//        }

        /**
         * Creates an immutable instance of AutofocusSettings
         *
         * @return Immutable version of AutofocusSettings
         */
        @Override
        public DefaultAutofocusSettings build() {
            return new DefaultAutofocusSettings(this);
        }
    }


    private final int numImages_;
    private final double stepSizeUm_;
//    private final double toleranceUm_;
    private final boolean showImages_;
    private final boolean showGraph_;
    private final AutofocusMode mode_;
    private final AutofocusType scoringMethod_;
    private final String channel_;
//    private final boolean useEveryStagePass_;
//    private final boolean useBeforeAcquisition_;
//    private final double maxOffset_;
//    private final boolean autoUpdateOffset_;
//    private final double autoUpdateMaxOffset_;

    private DefaultAutofocusSettings(Builder builder) {
        numImages_ = builder.numImages_;
        stepSizeUm_ = builder.stepSizeUm_;
       // toleranceUm_ = builder.toleranceUm_;
        showImages_ = builder.showImages_;
        showGraph_ = builder.showGraph_;
        mode_ = builder.mode_;
        scoringMethod_ = builder.scoringMethod_;
        channel_ = builder.channel_;
//        useEveryStagePass_ = builder.useEveryStagePass_;
//        useBeforeAcquisition_ = builder.useBeforeAcquisition_;
//        maxOffset_ = builder.maxOffset_;
//        autoUpdateOffset_ = builder.autoUpdateOffset_;
//        autoUpdateMaxOffset_ = builder.autoUpdateMaxOffset_;
    }

    public Builder copyBuilder() {
        return new Builder(this);
    }

    /**
     * Returns the number of images used for autofocus routine.
     *
     * @return the number of images
     */
    @Override
    public int numImages() {
        return numImages_;
    }

    /**
     * Returns the step size between images in microns.
     *
     * @return the step size in microns
     */
    @Override
    public double stepSizeUm() {
        return stepSizeUm_;
    }

    /**
     * Returns the autofocus mode being used.
     *
     * @return the autofocus mode
     */
    @Override
    public AutofocusMode mode() {
        return mode_;
    }

//    /**
//     * Returns the tolerance in microns used in the autofocus routine.
//     *
//     * @return the coefficient of determination
//     */
//    @Override
//    public double toleranceUm() {
//        return toleranceUm_;
//    }

    /**
     * Returns {@code true} if showing images in the live view window.
     *
     * @return {@code true} if displaying images
     */
    public boolean showImages() {
        return showImages_;
    }

    /**
     * Returns {@code true} if the graph will be displayed after the autofocus routine.
     *
     * @return {@code true} if displaying the graph
     */
    public boolean showGraph() {
        return showGraph_;
    }

    /**
     * Returns the type of scoring algorithm used for autofocus.
     *
     * @return the type of scoring algorithm
     */
    @Override
    public AutofocusType scoringMethod() {
        return scoringMethod_;
    }


    /**
     * Returns the channel autofocus is being run on.
     *
     * @return the autofocus channel
     */
    @Override
    public String channel() {
        return channel_;
    }

//    /**
//     * Returns true if autofocus is run every stage pass.
//     *
//     * @return true if autofocus is run every stage pass
//     */
//    @Override
//    public boolean useEveryStagePass() {
//        return useEveryStagePass_;
//    }
//
//    /**
//     * Returns true if we run an autofocus routine before starting an acquisition.
//     *
//     * @return true if enabled
//     */
//    @Override
//    public boolean useBeforeAcquisition() {
//        return useBeforeAcquisition_;
//    }

//    /**
//     * What is this?
//     *
//     * @return
//     */
//    @Override
//    public double maxOffset() {
//        return maxOffset_;
//    }
//
//    @Override
//    public boolean autoUpdateOffset() {
//        return autoUpdateOffset_;
//    }
//
//    @Override
//    public double autoUpdateMaxOffset() {
//        return autoUpdateMaxOffset_;
//    }

}
