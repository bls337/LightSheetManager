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

    public static class Properties {
        public static final String BINNING = "Binning";
        public static final String X_RESOLUTION = "OnCameraCCDXSize";
        public static final String Y_RESOLUTION = "OnCameraCCDYSize";
        public static final String READOUT_TIME = "ReadoutTime";
    }

    public DemoCamera(Studio studio, String deviceName) {
        super(studio, deviceName);
    }

    @Override
    public void setBinning() {
        //setProperty(Properties.BINNING); // TODO: need to modify LightSheetCamera interface to take param
    }

    @Override
    public int getBinning() {
        final String binning = getProperty(Properties.BINNING);
        final int factor = Integer.parseInt(binning.substring(0, 1));
        if (factor < 1) {
            studio_.logs().showError("Was not able to get camera binning factor");
            return 1;
        }
        return Integer.parseInt(binning.substring(0, 1));
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
    public float getResetTime(CameraMode cameraMode) {
        return 0;
    }

    @Override
    public float getReadoutTime(CameraMode cameraMode) {
        return getPropertyFloat(Properties.READOUT_TIME);
    }
}
