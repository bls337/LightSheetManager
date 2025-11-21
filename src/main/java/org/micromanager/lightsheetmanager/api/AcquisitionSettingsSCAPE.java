package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.internal.DefaultScanSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultSheetCalibration;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceCalibration;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettingsLS;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;

/**
 * Acquisition settings for SCAPE microscope geometries.
 */
public interface AcquisitionSettingsSCAPE extends AcquisitionSettings {

    interface Builder<T extends AcquisitionSettings.Builder<T>> extends AcquisitionSettings.Builder<T> {

        /**
         * Sets the acquisition mode.
         *
         * @param acqMode the acquisition mode
         */
        T acquisitionMode(final AcquisitionMode acqMode);

        /**
         * Sets the camera mode.
         *
         * @param cameraMode the camera mode.
         */
        T cameraMode(final CameraMode cameraMode);

        /**
         * Sets the acquisition to use channels.
         *
         * @param state true to use channels.
         */
        T useChannels(final boolean state);

        /**
         * Sets the acquisition to use time points.
         *
         * @param state true to use time points.
         */
        T useTimePoints(final boolean state);

        /**
         * Sets the acquisition to use autofocus.
         *
         * @param state true to use autofocus.
         */
        T useAutofocus(final boolean state);

        /**
         * Sets the acquisition to use multiple positions.
         *
         * @param state true to use multiple positions.
         */
        T useMultiplePositions(final boolean state);

        /**
         * Sets the acquisition to use hardware time points.
         *
         * @param state true to use time points.
         */
        T useHardwareTimePoints(final boolean state);

        /**
         * Sets the acquisition to use advanced timing settings.
         *
         * @param state true to use advanced timing settings
         */
        T useAdvancedTiming(final boolean state);

        /**
         * Sets the number of time points.
         *
         * @param numTimePoints the number of time points
         */
        T numTimePoints(final int numTimePoints);

        /**
         * Sets the time point interval between time points in seconds.
         *
         * @param timePointInterval the time point interval in seconds.
         */
        T timePointInterval(final double timePointInterval);

        /**
         * Sets the delay after a move when using multiple positions.
         *
         * @param postMoveDelay the delay in milliseconds.
         */
        T postMoveDelay(final int postMoveDelay);

        /**
         * Sets the live scan period.
         *
         * @param liveScanPeriod the channel array
         */
        T liveScanPeriod(final double liveScanPeriod);

        /**
         * Creates an immutable instance of AcquisitionSettingsSCAPE
         *
         * @return Immutable version of AcquisitionSettingsSCAPE
         */
        AcquisitionSettingsSCAPE build();
    }

    /**
     * Creates a Builder populated with settings of this AcquisitionSettingsDISPIM instance.
     *
     * @return AcquisitionSettingsDISPIM.Builder pre-populated with settings of this instance.
     */
    //Builder copyBuilder();

    /**
     * Returns the immutable DefaultTimingSettings instance.
     *
     * @return immutable DefaultTimingSettings instance.
     */
    DefaultTimingSettings timingSettings();

    /**
     * Returns the immutable DefaultVolumeSettings instance.
     *
     * @return immutable DefaultVolumeSettings instance.
     */
    DefaultVolumeSettings volumeSettings();

    /**
     * Returns the immutable DefaultSliceSettings instance.
     *
     * @return immutable DefaultSliceSettings instance.
     */
    DefaultSliceSettings sliceSettings();

    /**
     * Returns the immutable DefaultSliceSettingsLS instance.
     *
     * @return immutable DefaultSliceSettingsLS instance.
     */
    DefaultSliceSettingsLS sliceSettingsLS();

    /**
     * Returns the immutable DefaultScanSettings instance.
     *
     * @return immutable DefaultScanSettings instance.
     */
    DefaultScanSettings scanSettings();

    /**
     * Returns the immutable DefaultSheetCalibration instance.
     *
     * @return immutable DefaultSheetCalibration instance.
     */
    DefaultSheetCalibration sheetCalibration(final int view);

    /**
     * Returns the immutable DefaultSliceCalibration instance.
     *
     * @return immutable DefaultSliceCalibration instance.
     */
    DefaultSliceCalibration sliceCalibration(final int view);

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
     * Returns true if using channels.
     *
     * @return true if using channels.
     */
    boolean isUsingChannels();

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

    double liveScanPeriod();
}
