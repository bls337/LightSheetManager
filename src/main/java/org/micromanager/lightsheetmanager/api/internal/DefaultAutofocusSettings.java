package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AutofocusSettings;
import org.micromanager.lightsheetmanager.api.data.AutofocusMode;
import org.micromanager.lightsheetmanager.api.data.AutofocusType;

import java.util.Objects;

public class DefaultAutofocusSettings implements AutofocusSettings {

    private final boolean enabled_;
    private final int numImages_;
    private final double stepSizeUm_;
    private final boolean showImages_;
    private final boolean showGraph_;
    private final AutofocusMode mode_;
    private final AutofocusType scoringMethod_;
    private final String channel_;

    private DefaultAutofocusSettings(Builder builder) {
        enabled_ =  builder.enabled_;
        numImages_ = builder.numImages_;
        stepSizeUm_ = builder.stepSizeUm_;
        showImages_ = builder.showImages_;
        showGraph_ = builder.showGraph_;
        mode_ = builder.mode_;
        scoringMethod_ = builder.scoringMethod_;
        channel_ = builder.channel_;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AutofocusSettings settings) {
        Objects.requireNonNull(settings, "Cannot copy from null settings");
        return new Builder(settings);
    }

    @Override
    public Builder copyBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean enabled() {
        return enabled_;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DefaultAutofocusSettings other = (DefaultAutofocusSettings) obj;
        return enabled_ == other.enabled_ &&
                numImages_ == other.numImages_ &&
                Double.compare(stepSizeUm_, other.stepSizeUm_) == 0 &&
                showImages_ == other.showImages_ &&
                showGraph_ == other.showGraph_ &&
                mode_ == other.mode_ &&
                scoringMethod_ == other.scoringMethod_ &&
                channel_.equals(other.channel_);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled_, numImages_, stepSizeUm_,
                showImages_, showGraph_, mode_, scoringMethod_, channel_);
    }

    @Override
    public String toString() {
        return String.format("%s[enabled=%s, numImages=%s, stepSizeUm=%s, " +
                        "showImages=%s, showGraph=%s, mode=%s, scoringMethod=%s, channel=%s]",
                getClass().getSimpleName(),
                enabled_, numImages_, stepSizeUm_, showImages_, showGraph_, mode_, scoringMethod_, channel_);
    }

    public static class Builder implements AutofocusSettings.Builder {

        private boolean enabled_ = false;
        private int numImages_ = 10;
        private double stepSizeUm_ = 1.0;
        private boolean showImages_ = false;
        private boolean showGraph_ = false;
        private AutofocusMode mode_ = AutofocusMode.FIXED_PIEZO_SWEEP_SLICE;
        private AutofocusType scoringMethod_ = AutofocusType.VOLATH5;
        private String channel_ = "";

        private Builder() {
        }

        private Builder(final AutofocusSettings settings) {
            enabled_ = settings.enabled();
            numImages_ = settings.numImages();
            stepSizeUm_ = settings.stepSizeUm();
            showImages_ = settings.showImages();
            showGraph_ = settings.showGraph();
            mode_ = settings.mode();
            scoringMethod_ = settings.scoringMethod();
            channel_ = settings.channel();
        }

        @Override
        public Builder enabled(final boolean state) {
            enabled_ = state;
            return this;
        }

        /**
         * Sets the number of images to capture in the autofocus routine.
         *
         * @param numImages the number of images
         */
        @Override
        public Builder numImages(final int numImages) {
            numImages_ = numImages;
            return this;
        }

        /**
         * Sets the spacing between images in the autofocus routine.
         *
         * @param stepSize the step size in microns
         */
        @Override
        public Builder stepSizeUm(final double stepSize) {
            stepSizeUm_ = stepSize;
            return this;
        }

        /**
         * Set to {@code true} to show the images in the live view window.
         *
         * @param state {@code true} to show images
         */
        @Override
        public Builder showImages(boolean state) {
            showImages_ = state;
            return this;
        }

        /**
         * Set to {@code true} to show a graph of the data.
         *
         * @param state {@code true} to show the graph
         */
        @Override
        public Builder showGraph(boolean state) {
            showGraph_ = state;
            return this;
        }

        /**
         * Selects whether to fix the piezo or the sheet for an autofocus routine.
         *
         * @param mode the autofocus mode
         */
        @Override
        public Builder mode(final AutofocusMode mode) {
            mode_ = mode;
            return this;
        }

        /**
         * Sets the type of scoring algorithm to use when running autofocus.
         *
         * @param type the scoring algorithm
         */
        @Override
        public Builder scoringMethod(final AutofocusType type) {
            scoringMethod_ = type;
            return this;
        }

        /**
         * Set the channel to run the autofocus routine on.
         *
         * @param channel the channel to run autofocus on
         */
        @Override
        public Builder channel(final String channel) {
            channel_ = channel;
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

}
