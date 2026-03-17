package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;
import org.micromanager.lightsheetmanager.api.data.CameraData;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.internal.DefaultChannelSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultSheetCalibration;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceCalibration;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;

/**
 * Acquisition settings for SCAPE microscopes.
 */
public interface AcquisitionSettingsScape extends AcquisitionSettings {

    /**
     * Returns a builder initialized with the current settings.
     *
     * @return a builder to create a modified copy of these settings
     */
    //Builder copyBuilder();

    /**
     * Returns the immutable DefaultChannelSettings instance.
     *
     * @return immutable DefaultChannelSettings instance.
     */
    ChannelSettings channels();

    /**
     * Returns the immutable DefaultTimingSettings instance.
     *
     * @return immutable DefaultTimingSettings instance.
     */
    DefaultTimingSettings timing();

    /**
     * Returns the immutable DefaultVolumeSettings instance.
     *
     * @return immutable DefaultVolumeSettings instance.
     */
    DefaultVolumeSettings volume();

    /**
     * Returns the immutable DefaultSliceSettings instance.
     *
     * @return immutable DefaultSliceSettings instance.
     */
    DefaultSliceSettings slice();

    /**
     * Returns the immutable DefaultScanSettings instance.
     *
     * @return immutable DefaultScanSettings instance.
     */
    StageScanSettings stageScan();

    /**
     * Returns the immutable DefaultSheetCalibration instance.
     *
     * @return immutable DefaultSheetCalibration instance.
     */
    DefaultSheetCalibration sheetCalibration();

    /**
     * Returns the immutable DefaultSliceCalibration instance.
     *
     * @return immutable DefaultSliceCalibration instance.
     */
    DefaultSliceCalibration sliceCalibration();

    /**
     * Returns the acquisition mode.
     *
     * @return the acquisition mode.
     */
    AcquisitionMode acquisitionMode();

    /**
     * Returns the camera mode.
     *
     * @return the camera mode.
     */
    CameraMode cameraMode();

    /**
     * Returns the imaging camera order.
     *
     * @return the imaging camera order
     */
    CameraData[] imagingCameraOrder();

    /**
     * Returns true if using time points.
     *
     * @return true if using time points.
     */
    boolean isUsingTimePoints();

    /**
     * Returns true if using autofocus.
     *
     * @return true if using autofocus.
     */
    boolean isUsingAutofocus();

    /**
     * Returns true if using multiple positions.
     *
     * @return true if using multiple positions.
     */
    boolean isUsingMultiplePositions();

    /**
     * Returns true if using hardware time points.
     *
     * @return true if using hardware time points.
     */
    boolean isUsingHardwareTimePoints();

    /**
     * Returns true if using stage scanning.
     *
     * @return true if using stage scanning.
     */
    boolean isUsingStageScanning();

    /**
     * Returns true if using advanced timing settings.
     *
     * @return true if using advanced timing settings.
     */
    boolean isUsingAdvancedTiming();

    /**
     * Returns the number of time points.
     *
     * @return the number of time points.
     */
    int numTimePoints();

    /**
     * Returns the time point interval in seconds.
     *
     * @return the time point interval in seconds.
     */
    double timePointInterval();

    /**
     * Returns the post move delay in milliseconds.
     *
     * @return the post move delay in milliseconds.
     */
    int postMoveDelay();

    interface Builder<T extends AcquisitionSettings.Builder<T>> extends AcquisitionSettings.Builder<T> {

        /**
         * Sets the acquisition mode.
         *
         * @param mode the acquisition mode
         * @return {@code this} builder
         */
        T acquisitionMode(final AcquisitionMode mode);

        /**
         * Sets the camera mode.
         *
         * @param mode the camera mode.
         * @return {@code this} builder
         */
        T cameraMode(final CameraMode mode);

        /**
         * Sets the imaging camera order.
         *
         * @param order the imaging camera order
         * @return {@code this} builder
         */
        T imagingCameraOrder(final CameraData[] order);

        /**
         * Sets the acquisition to use time points.
         *
         * @param state true to use time points
         * @return {@code this} builder
         */
        T useTimePoints(final boolean state);

        /**
         * Sets the acquisition to use autofocus.
         *
         * @param state true to use autofocus
         * @return {@code this} builder
         */
        T useAutofocus(final boolean state);

        /**
         * Sets the acquisition to use multiple positions.
         *
         * @param state true to use multiple positions
         * @return {@code this} builder
         */
        T useMultiplePositions(final boolean state);

        /**
         * Sets the acquisition to use hardware time points.
         *
         * @param state true to use time points
         * @return {@code this} builder
         */
        T useHardwareTimePoints(final boolean state);

        /**
         * Sets the acquisition to use advanced timing settings.
         *
         * @param state true to use advanced timing settings
         * @return {@code this} builder
         */
        T useAdvancedTiming(final boolean state);

        /**
         * Sets the number of time points.
         *
         * @param numTimePoints the number of time points
         * @return {@code this} builder
         */
        T numTimePoints(final int numTimePoints);

        /**
         * Sets the time point interval between time points in seconds.
         *
         * @param timePointInterval the time point interval in seconds
         * @return {@code this} builder
         */
        T timePointInterval(final double timePointInterval);

        /**
         * Sets the delay after a move when using multiple positions.
         *
         * @param postMoveDelay the delay in milliseconds
         * @return {@code this} builder
         */
        T postMoveDelay(final int postMoveDelay);

        /**
         * Creates a new {@link AcquisitionSettingsScape} instance based on the current configuration.
         *
         * @return a new immutable settings instance
         */
        AcquisitionSettingsScape build();
    }

}
