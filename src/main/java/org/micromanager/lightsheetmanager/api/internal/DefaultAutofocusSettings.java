package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AutofocusSettings;
import org.micromanager.lightsheetmanager.api.data.AutofocusFit;
import org.micromanager.lightsheetmanager.api.data.AutofocusMode;
import org.micromanager.lightsheetmanager.api.data.AutofocusType;

public class DefaultAutofocusSettings implements AutofocusSettings {

    public static class Builder implements AutofocusSettings.Builder {
        private int numImages_ = 4;
        private double stepSize_ = 0.001;
        private AutofocusMode mode_ = AutofocusMode.FIXED_PIEZO_SWEEP_SLICE;
        private AutofocusType scoring_ = AutofocusType.VOLATH;
        private AutofocusFit fit_ = AutofocusFit.GAUSSIAN;
        private double r2_ = 1.0;
        private int timePointInterval_ = 1;
        private boolean useEveryStagePass_ = false;
        private boolean useBeforeAcquisition_ = false;
        private String channel_ = "";
        private double maxOffset_ = 1.0;
        private boolean autoUpdateOffset_ = true;
        private double autoUpdateMaxOffset_ = 5.0;

        public Builder() {
        }

        private Builder(final DefaultAutofocusSettings autofocusSettings) {
            numImages_ = autofocusSettings.numImages_;
            stepSize_ = autofocusSettings.stepSize_;
            mode_ = autofocusSettings.mode_;
            scoring_ = autofocusSettings.scoring_;
            fit_ = autofocusSettings.fit_;
            r2_ = autofocusSettings.r2_;
            timePointInterval_ = autofocusSettings.timePointInterval_;
            useEveryStagePass_ = autofocusSettings.useEveryStagePass_;
            useBeforeAcquisition_ = autofocusSettings.useBeforeAcquisition_;
            channel_ = autofocusSettings.channel_;
            maxOffset_ = autoUpdateMaxOffset_;
            autoUpdateOffset_ = autofocusSettings.autoUpdateOffset_;
            autoUpdateMaxOffset_ = autofocusSettings.autoUpdateMaxOffset_;
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
        public AutofocusSettings.Builder stepSize(final double stepSize) {
            stepSize_ = stepSize;
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
        public AutofocusSettings.Builder scoringAlgorithm(final AutofocusType type) {
            scoring_ = type;
            return this;
        }

        /**
         * Sets the type of curve fitting algorithm to use with autofocus..
         *
         * @param type the curve fitting algorithm
         */
        @Override
        public AutofocusSettings.Builder fit(final AutofocusFit type) {
            fit_ = type;
            return this;
        }

        /**
         * Sets the coefficient of determination for the autofocus algorithm.
         *
         * @param value the coefficient of determination
         */
        @Override
        public AutofocusSettings.Builder r2(final double value) {
            r2_ = value;
            return this;
        }

        /**
         * Sets the amount of time in seconds to delay before capturing the next time point.
         *
         * @param timePointInterval the interval in seconds
         */
        @Override
        public AutofocusSettings.Builder timePointInterval(final int timePointInterval) {
            timePointInterval_ = timePointInterval;
            return this;
        }

        /**
         * Run autofocus every time we move to the next channel during an acquisition.
         *
         * @param state true to enable autofocus every stage pass
         */
        @Override
        public AutofocusSettings.Builder useEveryStagePass(final boolean state) {
            useEveryStagePass_ = state;
            return this;
        }

        /**
         * Run an autofocus routine before starting the acquisition.
         *
         * @param state true or false
         */
        @Override
        public AutofocusSettings.Builder useBeforeAcquisition(final boolean state) {
            useBeforeAcquisition_ = state;
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

        @Override
        public AutofocusSettings.Builder maxOffset(final double maxOffset) {
            maxOffset_ = maxOffset;
            return this;
        }

        @Override
        public AutofocusSettings.Builder autoUpdateOffset(final boolean state) {
            autoUpdateOffset_ = state;
            return this;
        }

        @Override
        public AutofocusSettings.Builder autoUpdateMaxOffset(final double um) {
            autoUpdateMaxOffset_ = um;
            return this;
        }

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
    private final double stepSize_;
    private final AutofocusMode mode_;
    private final AutofocusType scoring_;
    private final AutofocusFit fit_;
    private final double r2_;
    private final int timePointInterval_;
    private final boolean useEveryStagePass_;
    private final boolean useBeforeAcquisition_;
    private final String channel_;
    private final double maxOffset_;
    private final boolean autoUpdateOffset_;
    private final double autoUpdateMaxOffset_;

    private DefaultAutofocusSettings(Builder builder) {
        numImages_ = builder.numImages_;
        stepSize_ = builder.stepSize_;
        mode_ = builder.mode_;
        scoring_ = builder.scoring_;
        fit_ = builder.fit_;
        r2_ = builder.r2_;
        timePointInterval_ = builder.timePointInterval_;
        useEveryStagePass_ = builder.useEveryStagePass_;
        useBeforeAcquisition_ = builder.useBeforeAcquisition_;
        channel_ = builder.channel_;
        maxOffset_ = builder.maxOffset_;
        autoUpdateOffset_ = builder.autoUpdateOffset_;
        autoUpdateMaxOffset_ = builder.autoUpdateMaxOffset_;
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
    public double stepSize() {
        return stepSize_;
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

    /**
     * Returns the type of scoring algorithm used for autofocus.
     *
     * @return the type of scoring algorithm
     */
    @Override
    public AutofocusType scoringAlgorithm() {
        return scoring_;
    }

    @Override
    public AutofocusFit fit() {
        return fit_;
    }

    /**
     * Returns the coefficient of determination used in the autofocus routine.
     *
     * @return the coefficient of determination
     */
    @Override
    public double r2() {
        return r2_;
    }

    /**
     * Returns the number of images between autofocus routines.
     *
     * @return the number of images between autofocus routines
     */
    @Override
    public int timePointInterval() {
        return timePointInterval_;
    }

    /**
     * Returns true if autofocus is run every stage pass.
     *
     * @return true if autofocus is run every stage pass
     */
    @Override
    public boolean useEveryStagePass() {
        return useEveryStagePass_;
    }

    /**
     * Returns true if we run an autofocus routine before starting an acquisition.
     *
     * @return true if enabled
     */
    @Override
    public boolean useBeforeAcquisition() {
        return useBeforeAcquisition_;
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

    /**
     * What is this?
     *
     * @return
     */
    @Override
    public double maxOffset() {
        return maxOffset_;
    }

    @Override
    public boolean autoUpdateOffset() {
        return autoUpdateOffset_;
    }

    @Override
    public double autoUpdateMaxOffset() {
        return autoUpdateMaxOffset_;
    }

}
