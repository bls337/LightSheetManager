package org.micromanager.lightsheetmanager.model.devices.cameras;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.LightSheetCamera;
import org.micromanager.lightsheetmanager.api.data.CameraMode;

import java.awt.Rectangle;

/**
 * Support for Teledyne Photometrics cameras.
 * <p>Devices Adapter: PVCAM
 * <p>Camera Support: Kinetix, Prime 95B, Prime
 */
public class PVCamera extends CameraBase implements LightSheetCamera {

    public PVCamera(Studio studio, String deviceName) {
        super(studio, deviceName);
    }

    @Override
    public void setTriggerMode(CameraMode cameraMode) {
        switch (cameraMode) {
            case EDGE:
            case PSEUDO_OVERLAP:
            case VIRTUAL_SLIT:
//                props_.setPropValue(devKey,
//                        Properties.Keys.TRIGGER_MODE,
//                        Properties.Values.EDGE_TRIGGER);
//                break;
//            case INTERNAL:
//                props_.setPropValue(devKey,
//                        Properties.Keys.TRIGGER_MODE,
//                        Properties.Values.INTERNAL_TRIGGER);
//                break;
            default:
                break;
        }
    }

    @Override
    public CameraMode getTriggerMode() {
        return null;
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
//        final int x = props_.getPropValueInteger(camKey, Properties.Keys.CAMERA_X_DIMENSION);
//        final int y = props_.getPropValueInteger(camKey, Properties.Keys.CAMERA_Y_DIMENSION);
//        return new Rectangle(0, 0, x, y);;
        return null;
    }

    @Override
    public double getRowReadoutTime() {
//        Rectangle roi = getCameraROI(camKey);
//        if (props_.getPropValueString(camKey, Properties.Keys.PVCAM_CHIPNAME).equals(Properties.Values.PRIME_95B_CHIPNAME)) {
//            float readoutTimeMs = (float) props_.getPropValueInteger(camKey, Properties.Keys.PVCAM_READOUT_TIME) / 1e6f;
//            return (readoutTimeMs / roi.height);
//        } else {
//            return 0.01;  // TODO get more accurate value
//        }
        return 0;
    }

    @Override
    public float getReadoutTime(CameraMode cameraMode) {
//        int endGlobalToTrig = props_.getPropValueInteger(camKey, Properties.Keys.PVCAM_PRE_TIME)
//                + 2 * props_.getPropValueInteger(camKey, Properties.Keys.PVCAM_READOUT_TIME);
//        // this factor of 2 is empirical 08-Jan-2021; I'm not sure why it's needed but that is the missing piece it seems
//        readoutTimeMs = (float) endGlobalToTrig / 1e6f;
        return 0;
    }

    @Override
    public float getResetTime(CameraMode cameraMode) {
        return 14.25f;  // strange number just to make it easy to find later; I think the original Prime needs to be added
    }
}
