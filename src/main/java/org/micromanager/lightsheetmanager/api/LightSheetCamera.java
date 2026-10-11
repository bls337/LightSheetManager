package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.data.CameraMode;

import java.awt.Rectangle;

/**
 * There should be a concrete implementation for each camera device adapter
 * that implements these methods to make them compatible with the plugin.
 */
public interface LightSheetCamera {

    void setTriggerMode(final CameraMode cameraMode);
    CameraMode getTriggerMode();

    /**
     * Sets the binning factor, such as 2 for 2x2.
     *
     * @return true if the camera reports the factor afterwards
     */
    boolean setBinning(final int factor);

    /** Returns the binning factor the camera reports. */
    int getBinning();

    /** Returns the binning factors the camera offers, ascending. */
    int[] getAllowedBinnings();

    Rectangle getResolution();
    double getRowReadoutTime();
    double getReadoutTime(final CameraMode cameraMode);
    double getResetTime(final CameraMode cameraMode);
}
