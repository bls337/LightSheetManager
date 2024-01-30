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

    public static class Models {
        public static final String PRIME = "CIS2020F";
        public static final String PRIME_95B = "GS144BSI";
        public static final String PRIME_BSI_EXPRESS = "GS2020";
        public static final String KINETIX = "TMP-Kinetix";
    }

    public static class Properties {
        public static final String BINNING = "Binning";
        public static final String CHIP_NAME = "ChipName";
        public static final String TRIGGER_MODE = "TriggerMode";

        public static final String X_RESOLUTION = "X-dimension";
        public static final String Y_RESOLUTION = "Y-dimension";

        public static final String READOUT_TIME = "Timing-ReadoutTimeNs";
        public static final String PRE_TRIGGER_TIME = "Timing-PreTriggerDelayNs";
        public static final String POST_TRIGGER_TIME = "Timing-PostTriggerDelayNs";

    }

    public static class Values {
        public final static String INTERNAL_TRIGGER = "Internal Trigger";
        public final static String EDGE_TRIGGER = "Edge Trigger";
    }

    public PVCamera(Studio studio, String deviceName) {
        super(studio, deviceName);
    }

    @Override
    public void setTriggerMode(CameraMode cameraMode) {
        switch (cameraMode) {
            case EDGE:
            case PSEUDO_OVERLAP:
            case VIRTUAL_SLIT:
                setProperty(Properties.TRIGGER_MODE, Values.EDGE_TRIGGER);
                break;
            case INTERNAL:
                setProperty(Properties.TRIGGER_MODE, Values.INTERNAL_TRIGGER);
                break;
            default:
                break;
        }
    }

    @Override
    public CameraMode getTriggerMode() {
        return CameraMode.fromString(getProperty(Properties.TRIGGER_MODE));
    }

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
        final int x = getPropertyInt(Properties.X_RESOLUTION);
        final int y = getPropertyInt(Properties.Y_RESOLUTION);
        return new Rectangle(0, 0, x, y);
    }

    @Override
    public double getRowReadoutTime() {
        Rectangle roi = getROI();
        if (hasProperty(Properties.READOUT_TIME)) {
            final double readoutTimeMs = getPropertyFloat(Properties.READOUT_TIME) / 1e6;
            return (readoutTimeMs / roi.height);
        } else {
            return 0.01;  // TODO get more accurate value
        }
    }

    @Override
    public double getReadoutTime(CameraMode cameraMode) {
        double readoutTimeMs = 10.0;
        switch (cameraMode) {
            case OVERLAP:
                readoutTimeMs = 0.0;
                break;
            case PSEUDO_OVERLAP:
                if (isKinetix() || isPrime95B()) {
                    final double preTime = getPropertyFloat(Properties.PRE_TRIGGER_TIME);
                    readoutTimeMs = preTime / 1e6;
                    // for safety we make sure to wait at least a quarter millisecond to trigger
                    //   (may have hidden assumptions in other code about at least one tic wait)
                    if (readoutTimeMs < 0.249) {
                        readoutTimeMs = 0.25;
                    }
                } else { // original Prime
                    readoutTimeMs = 0.25;
                }
                break;
            case VIRTUAL_SLIT:
                readoutTimeMs = getPropertyFloat(Properties.READOUT_TIME) / 1e6;
                break;
            case EDGE: // fall through to next case
            case LEVEL:
                final double readoutTime = getPropertyFloat(Properties.READOUT_TIME);
                final double endGlobalToTrig = getPropertyFloat(Properties.PRE_TRIGGER_TIME) + 2 * readoutTime;
                // this factor of 2 is empirical 08-Jan-2021; I'm not sure why it's needed but that is the missing piece it seems
                readoutTimeMs = endGlobalToTrig / 1e6f;
                break;
            default:
                break;
        }
        return readoutTimeMs;
    }

    @Override
    public double getResetTime(CameraMode cameraMode) {
        double resetTimeMs;
        if (cameraMode == CameraMode.VIRTUAL_SLIT) {
            resetTimeMs = 0.0;
        } else {
            // TODO(Jon): Confirm that the Kinetix camera is like the Prime 95B

            // Photometrics Prime 95B is very different from other cameras so handle it as special case
            if (isKinetix() || isPrime95B()) {
                final double trigToGlobal = getPropertyFloat(Properties.POST_TRIGGER_TIME)
                        + getPropertyFloat(Properties.READOUT_TIME);
                // it appears as of end-May 2017 that the clearing time is actually rolled into the post-trigger
                //    time despite Photometrics documentation to the contrary
                resetTimeMs = trigToGlobal / 1e6;
            } else {
                resetTimeMs = 14.25;  // strange number just to make it easy to find later; I think the original Prime needs to be added
            }
        }
        return resetTimeMs;
    }

    private boolean isPrime95B() {
        return getProperty(Properties.CHIP_NAME).equals(Models.PRIME_95B);
    }

    private boolean isKinetix() {
        return getProperty(Properties.CHIP_NAME).equals(Models.KINETIX);
    }
}
