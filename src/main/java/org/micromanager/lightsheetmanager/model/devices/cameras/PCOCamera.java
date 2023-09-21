package org.micromanager.lightsheetmanager.model.devices.cameras;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.LightSheetCamera;
import org.micromanager.lightsheetmanager.api.data.CameraMode;

import java.awt.Rectangle;

public class PCOCamera extends CameraBase implements LightSheetCamera {

    public static class Models {
        public static final String EDGE55 =" 5.5";
        public static final String PANDA = "panda";
    }

    public static class Properties {
        public static final String CAMERA_TYPE = "CameraType";
        public static final String BINNING = "Binning";
        public static final String TRIGGER_MODE = "Triggermode";
        public static final String LINE_TIME = "Line Time [us]";
        public static final String PIXEL_RATE = "PixelRate";
    }

    public static class Values {
        public static final String INTERNAL = "Internal";
        public static final String EXTERNAL = "External";
        public static final String LEVEL = "External Exp. Ctrl.";
        public static final String SLOW_SCAN = "slow scan";
    }

    public PCOCamera(Studio studio, String deviceName) {
        super(studio, deviceName);
    }

    @Override
    public void setTriggerMode(CameraMode cameraMode) {
        mode_ = cameraMode;
        switch (cameraMode) {
            case EDGE:
            case PSEUDO_OVERLAP:
                setProperty(Properties.TRIGGER_MODE, Values.EXTERNAL);
                break;
            case LEVEL:
                setProperty(Properties.TRIGGER_MODE, Values.LEVEL);
                break;
            case INTERNAL:
                setProperty(Properties.TRIGGER_MODE, Values.INTERNAL);
                break;
            default:
                break;
        }
    }

    // TODO: impl
    @Override
    public void setBinning() {

    }

    @Override
    public int getBinning() {
        final String propVal = getProperty(Properties.BINNING);
//        if (factor < 1) {
//            // TODO: MyDialogUtils.showError("Was not able to get camera binning factor");
//            return 1;
//        }
        return Integer.parseInt(propVal.substring(0, 1));
    }

    @Override
    public Rectangle getResolution() {
        int x = 0;
        int y = 0;
        if (isEdge55()) {
            x = 2560;
            y = 2160;
        } else { // 4.2
            x = 2060;
            y = 2048;
        }
        return new Rectangle(0, 0, x, y);
    }

    @Override
    public double getRowReadoutTime() {
        if (hasProperty(Properties.LINE_TIME)) {
            return Double.parseDouble(getProperty(Properties.LINE_TIME))/1000.0;
        } else {
            if (isPanda()) {
                // documentation not clear but suggests line time < 13ms
                // empirically observed 12.5us + 0.0056us*ROI height (centered), e.g. 24ms at full frame in one set of measurements
                // another set of measurements it seemed approximately linear in log(height)
                // for now just use pessimistic but safe 24us
                // TODO get more accurate value
                return 0.024;
            } else {
                if (isEdge55()) {
                    if (isSlowReadout()) {
                        return 0.02752;
                    } else {
                        return 0.00917;
                    }
                } else {  // 4.2
                    if (isSlowReadout()) {
                        return 0.0276;
                    } else {
                        return 0.00965;
                    }
                }
            }
        }
    }

    @Override
    public float getReadoutTime(CameraMode cameraMode) {
        float readoutTimeMs = 10.0f;
        switch (cameraMode) {
            case VIRTUAL_SLIT:
                Rectangle roi = getROI();
                final double rowReadoutTime = getRowReadoutTime();
                int speedFactor = 1; // props_.getPropValueInteger(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_LS_SHUTTER_SPEED);
//                if (speedFactor < 1) {
//                    speedFactor = 1;
//                }
                readoutTimeMs = (float) rowReadoutTime * roi.height * speedFactor;
                break;
            case EDGE:
            case LEVEL:
                double rowReadoutTime2 = getRowReadoutTime();
                int numReadoutRows;

                Rectangle roi2 = getROI();
                Rectangle sensorSize = getResolution();

                numReadoutRows = roiReadoutRowsSplitReadout(roi2, sensorSize);
                readoutTimeMs = ((float) (numReadoutRows * rowReadoutTime2));;
                break;
            case OVERLAP:
                readoutTimeMs = 0.0f;
                break;
            case PSEUDO_OVERLAP:
                readoutTimeMs = 0.25f;
                break;
            default:
                break;
        }
        return readoutTimeMs;
    }

    @Override
    public float getResetTime(CameraMode cameraMode) {
        float resetTimeMs = 10.0f;
        final double rowReadoutTime = getRowReadoutTime();
        final float camReadoutTime = getReadoutTime(CameraMode.EDGE);

        int numRowsOverhead = 1;
        resetTimeMs = camReadoutTime + (float) (numRowsOverhead * rowReadoutTime);
        return resetTimeMs;
    }

    private boolean isEdge55() {
        return getProperty(Properties.CAMERA_TYPE).contains(Models.EDGE55);
    }

    private boolean isPanda() {
        return getProperty(Properties.CAMERA_TYPE).contains(Models.PANDA);
    }

    private boolean isSlowReadout() {
        return getProperty(Properties.PIXEL_RATE).equals(Values.SLOW_SCAN);
    }
}
