package org.micromanager.lightsheetmanager.model.devices.cameras;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.LightSheetCamera;
import org.micromanager.lightsheetmanager.api.data.CameraMode;

import java.awt.Rectangle;

/**
 * Support for Hamamatsu cameras.
 * <p>Devices Adapter: HamamatsuHam
 * <p>Camera Support: ORCA-Flash4, ORCA-Fusion, ORCA-Fusion BT
 */
public class HamamatsuCamera extends CameraBase implements LightSheetCamera {
    public static class Models {
        public static final String FUSION_BT = "C15440";
        public static final String FUSION = "C14440";
        public static final String FLASH4 = "C11440";
    }

    public static class Properties {
        public static final String CAMERA_NAME = "CameraName";
        public static final String CAMERA_BUS = "Camera Bus";
        public static final String TRIGGER_SOURCE = "TRIGGER SOURCE";
        public static final String TRIGGER_ACTIVE = "TRIGGER ACTIVE";
        public static final String SENSOR_MODE = "SENSOR MODE";
        public static final String INTERNAL_LINE_INTERVAL = "INTERNAL LINE INTERVAL";
        public static final String SCAN_MODE = "ScanMode";
        public static final String READOUT_TIME = "ReadoutTime";
        public static final String BINNING = "Binning";
    }

    public static class Values {
        public static final String INTERNAL = "INTERNAL";
        public static final String EXTERNAL = "EXTERNAL";
        public static final String EDGE = "EDGE";
        public static final String LEVEL = "LEVEL";
        public static final String SYNCREADOUT = "SYNCREADOUT";
        public static final String PROGRESSIVE = "PROGRESSIVE";
        public static final String AREA = "AREA";
        public static final String USB3 = "USB3";
        public static final String SCAN_MODE_1 = "1";
        public static final String SCAN_MODE_2 = "2";
        public static final String SCAN_MODE_3 = "3";

    }

    private final boolean isFusion_;

    public HamamatsuCamera(final Studio studio, final String deviceName) {
        super(studio, deviceName);
        isFusion_ = isFusion();
    }

    @Override
    public void setTriggerMode(final CameraMode cameraMode) {
        mode_ = cameraMode;
        //setProperty(Properties.TRIGGER_SOURCE, (cameraMode == CameraMode.INTERNAL) ? Values.INTERNAL : Values.EXTERNAL);
        setProperty(Properties.TRIGGER_SOURCE, Values.EXTERNAL);
        setProperty(Properties.SENSOR_MODE, (cameraMode == CameraMode.VIRTUAL_SLIT) ? Values.PROGRESSIVE : Values.AREA);
        switch (cameraMode) {
            case VIRTUAL_SLIT:
                setProperty(Properties.TRIGGER_ACTIVE, Values.EDGE);
                // FIXME: need this plugin property
                final double rowTime = getRowReadoutTime(); // * props_.getPropValueFloat(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_LS_SHUTTER_SPEED); // "LightSheetSpeedFactor"

                // get the lower limit from the camera (0.0098 for Flash4)
                double lowerLimit = 0.0;
                try {
                    lowerLimit = core_.getPropertyLowerLimit(deviceName_, Properties.INTERNAL_LINE_INTERVAL);
                } catch (Exception ignore) {
                    // ignore => will set the HAMAMATSU_LINE_INTERVAL property
                    // to the rowTime without considering the lower limit
                }
                setProperty(Properties.INTERNAL_LINE_INTERVAL, String.valueOf((float)Math.max(rowTime, lowerLimit)));
                //props_.setPropValue(devKey, Properties.Keys.HAMAMATSU_LINE_INTERVAL, (float)Math.max(rowTime, lowerLimit));
                break;
            case EDGE:
                setProperty(Properties.TRIGGER_ACTIVE, Values.EDGE);
                break;
            case LEVEL:
                setProperty(Properties.TRIGGER_ACTIVE, Values.LEVEL);
                break;
            case OVERLAP:
                setProperty(Properties.TRIGGER_ACTIVE, Values.SYNCREADOUT);
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
        int x;
        int y;
        if (isFusion_) {
            x = 2304;
            y = 2304;
        } else { // Flash4
            x = 2048;
            y = 2048;
        }
        return new Rectangle(0, 0, x, y);
    }

    @Override
    public double getRowReadoutTime() {
       if (isFusion_) {
           final String mode = getProperty(Properties.SCAN_MODE);
            if (mode.equals(Values.SCAN_MODE_3)) {
                return (11.22/2304);
            } else if (mode.equals(Values.SCAN_MODE_2)) {
                return (42.99/2304);
            } else {
                return (184.4/2304);
            }
        } else {  // Flash4
            if (isSlowReadout()) {
                return (2592/266e3 * (10.0/3));
            } else {
                return (2592/266e3);
            }
        }
    }

    @Override
    public float getReadoutTime(final CameraMode cameraMode) {
        float readoutTimeMs = 10.0f;
        switch (cameraMode) {
            case VIRTUAL_SLIT:
                if (getProperty(Properties.CAMERA_BUS).equals(Values.USB3)) {
                    readoutTimeMs = 10000;  // absurdly large, light sheet mode over USB3 isn't supported by Flash4, but we are set up to decide available modes by device library and not a property
                } else {
                    Rectangle roi = getROI();
                    readoutTimeMs = (float)(getRowReadoutTime() * roi.height);
                }
                break;
            case EDGE:
            case LEVEL:
                double rowReadoutTime = getRowReadoutTime();
                int numReadoutRows;

                Rectangle roi = getROI();
                Rectangle sensorSize = getResolution();

                if (getProperty(Properties.CAMERA_BUS).equals(Values.USB3)) {
                    // trust the device adapter's calculation for USB3
                    readoutTimeMs = Float.parseFloat(getProperty(Properties.READOUT_TIME)) * 1000.0f;
                } else {
                    if (isFusion_) { // Fusion
                        numReadoutRows = roi.height;
                    } else {
                        // Camera Link interface, original implementation
                        // device adapter provides readout time rounded to nearest 0.1ms; we calculate it ourselves instead
                        // note that Flash4's ROI is always set in increments of 4 pixels
                        if (getProperty(Properties.SENSOR_MODE).equals(Values.PROGRESSIVE)) {
                            numReadoutRows = roi.height;
                        } else {
                            numReadoutRows = roiReadoutRowsSplitReadout(roi, sensorSize);
                        }
                    }
                    readoutTimeMs = ((float) (numReadoutRows * rowReadoutTime));
                }
                break;
            case OVERLAP:
                readoutTimeMs = 0.0f;
                break;
            default:
                break;
        }
//        ReportingUtils.logDebugMessage("camera readout time computed as " + readoutTimeMs +
//                " for camera " + devices_.getMMDevice(camKey));
        return readoutTimeMs;
    }

    @Override
    public float getResetTime(final CameraMode cameraMode) {
        if (cameraMode == CameraMode.VIRTUAL_SLIT) {
            return 0.0f;
        } else {
            final double rowReadoutTime = getRowReadoutTime();
            final float camReadoutTime = getReadoutTime(CameraMode.EDGE);

            // don't know if this is different for Fusion; leave it all the same for time being
            // global reset mode not yet exposed in Micro-manager
            // it will be 17+1 rows of overhead but nothing else
            int numRowsOverhead;
            if (getProperty(Properties.TRIGGER_ACTIVE).equals(Values.SYNCREADOUT)) {
                numRowsOverhead = 18; // overhead of 17 rows plus jitter of 1 row
            } else { // for EDGE and LEVEL trigger modes
                numRowsOverhead = 10; // overhead of 9 rows plus jitter of 1 row
            }
            final float resetTimeMs = camReadoutTime + (float) (numRowsOverhead * rowReadoutTime);
//        ReportingUtils.logDebugMessage("camera reset time computed as " + resetTimeMs +
//                " for camera " + devices_.getMMDevice(camKey));
            return resetTimeMs; // assume 10ms readout if not otherwise possible to calculate
        }
    }

    /**
     * @return true if the camera is in the slow sensor readout mode.
     */
    private boolean isSlowReadout() {
        if (isFusion_) {
            return !getProperty(Properties.SCAN_MODE).equals(Values.SCAN_MODE_3);
        } else {
            return getProperty(Properties.SCAN_MODE).equals(Values.SCAN_MODE_1);
        }
    }

    /**
     * @return true if the camera is an ORCA-Fusion or ORCA-Fusion BT.
     */
    public boolean isFusion() {
        return getProperty(Properties.CAMERA_NAME).startsWith(Models.FUSION)
                || getProperty(Properties.CAMERA_NAME).startsWith(Models.FUSION_BT);
    }

    /**
     * @return true if the camera is an ORCA-Flash4.
     */
    public boolean isFlash4() {
        return getProperty(Properties.CAMERA_NAME).startsWith(Models.FLASH4);
    }
}
