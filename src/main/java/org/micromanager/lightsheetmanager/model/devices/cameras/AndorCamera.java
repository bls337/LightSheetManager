package org.micromanager.lightsheetmanager.model.devices.cameras;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.LightSheetCamera;
import org.micromanager.lightsheetmanager.api.data.CameraMode;

import java.awt.Rectangle;

/**
 * Support for Andor cameras.
 * <p>Devices Adapter: AndorSDK3
 * <p>Camera Support: Andor Zyla 4.2, Andor Zyla 5.5
 */
public class AndorCamera extends CameraBase implements LightSheetCamera {

    public static class Models {
        public static final String ZYLA55 = "Zyla 5.5";
        public static final String ZYLA42 = "Zyla 4.2";
    }

    public static class Properties {
        public static final String CAMERA_NAME = "CameraName";
        public static final String BINNING = "Binning";
        public static final String TRIGGER_MODE = "TriggerMode";
        public static final String SENSOR_READOUT_MODE = "LightScanPlus-SensorReadoutMode";
        public static final String SCAN_SPEED_CONTROL_ENABLE = "LightScanPlus-ScanSpeedControlEnable";
        public static final String EXPOSED_PIXEL_HEIGHT = "LightScanPlus-ExposedPixelHeight";
        public static final String LINE_SCAN_SPEED = "LightScanPlus-LineScanSpeed [lines/sec]";
        public static final String OVERLAP = "Overlap";
        public static final String PIXEL_READOUT_RATE = "PixelReadoutRate";

    }

    public static class Values {

        public static final String INTERNAL = "Internal (Recommended for fast acquisitions)";
        public static final String EXTERNAL = "External";
        public static final String BOTTOM_UP_SIMULTANEOUS = "Bottom Up Simultaneous";
        public static final String BOTTOM_UP_SEQUENTIAL = "Bottom Up Sequential";
        public static final String CENTER_OUT_SIMULTANEOUS = "Centre Out Simultaneous";
        public static final String LEVEL = "External Exposure";
        public static final String RATE_200 = "200";
        public static final String RATE_216 = "216";

        public static final String OFF = "Off";
        public static final String ON = "On";
    }

    public AndorCamera(Studio studio, String deviceName) {
        super(studio, deviceName);
    }

    @Override
    public void setTriggerMode(CameraMode cameraMode) {
        mode_ = cameraMode;
        // work-around a bug in SDK3 device adapter, can't switch from light sheet mode
        //  to "normal" center out simultaneous but works if we always go through the in-between mode
        if (hasProperty(Properties.SENSOR_READOUT_MODE)) {
            setProperty(Properties.SENSOR_READOUT_MODE, Values.BOTTOM_UP_SIMULTANEOUS);
            setProperty(Properties.SENSOR_READOUT_MODE,
                    (cameraMode == CameraMode.VIRTUAL_SLIT) ? Values.BOTTOM_UP_SEQUENTIAL : Values.CENTER_OUT_SIMULTANEOUS);
        }
        switch (cameraMode) {
            case VIRTUAL_SLIT:
                setProperty(Properties.TRIGGER_MODE, Values.EXTERNAL);
                setProperty(Properties.OVERLAP, Values.OFF);

                // Note: the following applies to single view mode, the issue is not present in dual view mode:
                // only shutter speeds < 4 work with the 200 Mhz slow scan mode, shutter speeds >= 5 will miss 1 image

                float maxLineScanSpeed = 104166.6667f;
                if (isSlowReadout()) {
                    maxLineScanSpeed = 41666.6667f;
                }

                // FIXME: need plugin properties
//                final float shutterSpeed = props_.getPropValueFloat(
//                        Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_LS_SHUTTER_SPEED);
//
//                final float shutterWidth = props_.getPropValueFloat(
//                        Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_LS_SHUTTER_WIDTH);

                // this property must be set to "On" to change LineScanSpeed
                setProperty(Properties.SCAN_SPEED_CONTROL_ENABLE, Values.ON);

                // TODO: should this be here?
                // default to the same pixelSize as getTimingFromPeriodAndLightExposure
                float pixelSize = (float) core_.getPixelSizeUm();
                if (pixelSize < 1e-6) {
                    pixelSize = 0.1625f;
                }

                // these properties must be set in the correct order:
                // light sheet speed => exposed pixel height
                // this will set the correct exposure automatically
                //setProperty("LightScanPlus-LineScanSpeed [lines/sec]", String.valueOf(maxLineScanSpeed/shutterSpeed)); // TODO: had to wrap with String.valueOf
                //setProperty("LightScanPlus-ExposedPixelHeight", String.valueOf((int)(shutterWidth/pixelSize)));

                break;
            case EDGE:
                setProperty(Properties.TRIGGER_MODE, Values.EXTERNAL);
                setProperty(Properties.OVERLAP, Values.OFF);
                break;
            case LEVEL:
                setProperty(Properties.TRIGGER_MODE, Values.LEVEL);
                setProperty(Properties.OVERLAP, Values.OFF);
                break;
            case OVERLAP:
                setProperty(Properties.TRIGGER_MODE, Values.LEVEL);
                setProperty(Properties.OVERLAP, Values.ON);
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
        final String binning = getProperty(PCOCamera.Properties.BINNING);
        final int factor = Integer.parseInt(binning.substring(0, 1));
        if (factor < 1) {
            studio_.logs().showError("Was not able to get camera binning factor");
            return 1;
        }
        return Integer.parseInt(binning.substring(0, 1));
    }

    @Override
    public Rectangle getResolution() {
        int x;
        int y;
        if (isZyla55()) { // 5.5
            x = 2560;
            y = 2160;
        } else { // 4.2
            x = 2048;
            y = 2048;
        }
        return new Rectangle(0, 0, x, y);
    }

    @Override
    public double getRowReadoutTime() {
        if (isZyla55()) {
            if (isSlowReadout()) {
                return (2624 * 2 / 206.54e3);
            } else {
                return (2624 * 2 / 568e3);
            }
        } else {  // 4.2
            if (isSlowReadout()) {
                return (2592 * 2 / 216e3);
            } else {
                return (2592 * 2 / 540e3);
            }
        }
    }

    @Override
    public float getReadoutTime(CameraMode cameraMode) {
        float readoutTimeMs = 10.0f;
        System.out.println("getReadoutTime: mode: " + cameraMode);
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
                readoutTimeMs = ((float) (numReadoutRows * rowReadoutTime2));
                break;
            case OVERLAP:
                readoutTimeMs = 0.0f;
                break;
            default:
                break;
        }
        return readoutTimeMs;
    }

    @Override
    public float getResetTime(CameraMode cameraMode) {
        final double rowReadoutTime = getRowReadoutTime();
        final float cameraReadoutTime = getReadoutTime(cameraMode);
        int numRowsOverhead = 1; // TODO: change? or keep for explanatory purposes
        return cameraReadoutTime + (float) (numRowsOverhead * rowReadoutTime);
    }

    // Note: at some point the Andor Zyla 4.2 camera changed it's PixelReadoutRate property value
    // for the slow scan speed to start with "200 MHz" instead of "216 MHz".
    private boolean isSlowReadout() {
        if (isZyla55()) {
            return getProperty(Properties.PIXEL_READOUT_RATE).startsWith(Values.RATE_200);
        } else {
            final String pixelReadoutRate = getProperty(Properties.PIXEL_READOUT_RATE).substring(0, 3);
            return pixelReadoutRate.equals(Values.RATE_200) || pixelReadoutRate.equals(Values.RATE_216);
        }
    }

    private boolean isZyla55() {
        return getProperty(Properties.CAMERA_NAME).startsWith(Models.ZYLA55);
    }
}
