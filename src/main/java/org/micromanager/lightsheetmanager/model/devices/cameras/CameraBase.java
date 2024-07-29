package org.micromanager.lightsheetmanager.model.devices.cameras;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.LightSheetCamera;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.model.devices.DeviceBase;

import java.awt.Rectangle;

// TODO: consider removing implements LightSheetCamera so no default methods for subclasses

/**
 * This is the base camera class.
 */
public class CameraBase extends DeviceBase implements LightSheetCamera {

    protected CameraMode mode_;

    public CameraBase(final Studio studio, final String deviceName) {
        super(studio, deviceName);
        mode_ = CameraMode.EDGE;
    }

    public void setExposure(final double milliseconds) {
        try {
            core_.setExposure(deviceName_, milliseconds);
        } catch (Exception e) {
            studio_.logs().logError("could not set camera exposure");
        }
    }

    public double getExposure() {
        double exposure = 0.0;
        try {
            exposure = core_.getExposure();
        } catch (Exception e) {
            studio_.logs().logError("could not get camera exposure");
        }
        return exposure;
    }

    // TODO: take binning into account
    public Rectangle getROI() {
        Rectangle roi = new Rectangle();
        try {
            roi = core_.getROI();
        } catch (Exception e) {
            studio_.logs().showError("could not get camera roi");
        }
        return roi;
    }

    public void setROI(final Rectangle roi) {
        final boolean isLiveModeOn = studio_.live().isLiveModeOn();
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (studio_.live().getDisplay() != null) {
                studio_.live().getDisplay().close();
            }
        }
        try {
            core_.setROI(deviceName_, roi.x, roi.y, roi.width, roi.height);
        } catch (Exception e) {
            studio_.logs().showError("could not set camera roi");
        }
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(true);
        }
    }

    public void setROI() {
        // TODO: set custom roi from camera tab :: store in model
    }

    public int roiVerticalOffset(Rectangle roi, Rectangle sensor) {
        return (roi.y + roi.height / 2) - (sensor.height / 2);
    }

    public int roiReadoutRowsSplitReadout(Rectangle roi, Rectangle sensor) {
        return Math.min(
                Math.abs(roiVerticalOffset(roi, sensor)) + roi.height / 2,  // if ROI overlaps sensor mid-line
                roi.height);                                                // if ROI does not overlap mid-line
    }

    // needed for subclasses

    @Override
    public void setTriggerMode(CameraMode cameraMode) {
        System.out.println("this!");
        mode_ = cameraMode;
    }

    @Override
    public CameraMode getTriggerMode() {
        return mode_;
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
        return new Rectangle();
    }

    @Override
    public double getRowReadoutTime() {
        return 0;
    }

    @Override
    public double getReadoutTime(CameraMode cameraMode) {
        return 0;
    }

    @Override
    public double getResetTime(CameraMode cameraMode) {
        return 0;
    }
}
