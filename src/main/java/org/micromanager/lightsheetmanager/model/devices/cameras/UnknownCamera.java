package org.micromanager.lightsheetmanager.model.devices.cameras;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.data.CameraMode;

import java.awt.Rectangle;

/**
 * Stand-in for a camera whose device adapter the plugin does not have a class for.
 *
 * <p>Device setup still completes so the plugin opens and the rest of the configuration stays
 * usable, and the generic operations inherited from {@link CameraBase} (exposure, ROI) work
 * through the Core for any camera. Only the values that need vendor knowledge fail, and they
 * fail loudly: returning a zero for readout or reset time would flow straight into the
 * slice-timing math and produce a plausible-looking but wrong acquisition.
 */
public class UnknownCamera extends CameraBase {

    public UnknownCamera(final Studio studio, final String deviceName) {
        super(studio, deviceName);
    }

    private UnsupportedOperationException unsupported(final String what) {
        return new UnsupportedOperationException("Camera \"" + deviceName_
                + "\" uses a device adapter the plugin does not support, so " + what
                + " is unknown. Supported: AndorSDK3, HamamatsuHam, PCO_Camera, PVCAM, DemoCamera.");
    }

    @Override
    public void setBinning() {
        throw unsupported("binning");
    }

    @Override
    public int getBinning() {
        throw unsupported("binning");
    }

    @Override
    public Rectangle getResolution() {
        throw unsupported("sensor resolution");
    }

    @Override
    public double getRowReadoutTime() {
        throw unsupported("row readout time");
    }

    @Override
    public double getReadoutTime(final CameraMode cameraMode) {
        throw unsupported("readout time");
    }

    @Override
    public double getResetTime(final CameraMode cameraMode) {
        throw unsupported("reset time");
    }
}
