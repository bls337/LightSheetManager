package org.micromanager.lightsheetmanager.model.devices.cameras;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.data.CameraMode;

import java.awt.Rectangle;

/**
 * Support for the DemoCamera device adapter.
 * <p>Note: this is for demo configurations.
 * <p>Device Adapter: DemoCamera
 * <p>Camera Support: DCam
 */
public class DemoCamera extends CameraBase {

    public static class Properties {
        public static final String X_RESOLUTION = "OnCameraCCDXSize";
        public static final String Y_RESOLUTION = "OnCameraCCDYSize";
        public static final String READOUT_TIME = "ReadoutTime";
    }

    public DemoCamera(Studio studio, String deviceName) {
        super(studio, deviceName);
    }

    @Override
    protected void applyTriggerMode(final CameraMode cameraMode) {
        // do nothing - no camera trigger modes - always internal - log for debug convenience
        studio_.logs().logMessage(
                "setTriggerMode(" + cameraMode + ") called but the DemoCamera is always in internal mode.");
    }

    @Override
    public Rectangle getResolution() {
        final int x = getPropertyInt(Properties.X_RESOLUTION);
        final int y = getPropertyInt(Properties.Y_RESOLUTION);
        return new Rectangle(0, 0, x, y);
    }

    @Override
    public double getRowReadoutTime() {
        return 0;
    }

    @Override
    public double getResetTime(CameraMode cameraMode) {
        return 0;
    }

    @Override
    public double getReadoutTime(CameraMode cameraMode) {
        return getPropertyFloat(Properties.READOUT_TIME);
    }
}
