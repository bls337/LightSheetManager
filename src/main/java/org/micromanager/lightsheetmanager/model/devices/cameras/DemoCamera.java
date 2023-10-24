package org.micromanager.lightsheetmanager.model.devices.cameras;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.LightSheetCamera;
import org.micromanager.lightsheetmanager.api.data.CameraMode;

import java.awt.Rectangle;

/**
 * Support for the DemoCamera in DHub.
 * <p>Note: this is for demo configurations.
 * <p>Devica Adapter: DHub
 * <p>Camera Support: DemoCamera
 */
public class DemoCamera extends CameraBase implements LightSheetCamera {

    public DemoCamera(Studio studio, String deviceName) {
        super(studio, deviceName);
    }

    @Override
    public void setBinning() {

    }

    @Override
    public int getBinning() {
        return 0;
    }

    @Override
    public Rectangle getResolution() {
        return null;
    }

    @Override
    public double getRowReadoutTime() {
        return 0;
    }

    @Override
    public float getResetTime(CameraMode cameraMode) {
        return 0;
    }

    @Override
    public float getReadoutTime(CameraMode cameraMode) {
        return 0;
    }
}
