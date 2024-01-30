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

    void setBinning();
    int getBinning();

    Rectangle getResolution();
    double getRowReadoutTime();
    double getReadoutTime(final CameraMode cameraMode);
    double getResetTime(final CameraMode cameraMode);
}
