package org.micromanager.lightsheetmanager.model.devices;

import org.micromanager.Studio;

import java.awt.geom.Point2D;

// Covered by MM
// - moves

// Not covered by MM
// - speed

public class XYStage extends DeviceBase {

    public XYStage(final Studio studio, final String deviceName) {
        super(studio, deviceName);
    }

    public void setRelativeXYPosition(final int x, final int y) {
        try {
            core_.setRelativeXYPosition(deviceName_, x, y);
        } catch (Exception e) {
            studio_.logs().showError("could not set relative move for " + deviceName_);
        }
    }

    public void setXYPosition(final double x, final double y) {
        try {
            core_.setXYPosition(deviceName_, x, y);
        } catch (Exception e) {
            studio_.logs().showError("could not set move for " + deviceName_);
        }
    }

    public Point2D.Double getXYPosition() {
        try {
            return core_.getXYStagePosition(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("could not get the xy position for " + deviceName_);
            return new Point2D.Double(0.0, 0.0);
        }
    }

    public double getXPosition() {
        try {
            return core_.getXYStagePosition(deviceName_).x;
        } catch (Exception e) {
            studio_.logs().showError("could not get the x position for " + deviceName_);
            return 0.0;
        }
    }

    public double getYPosition() {
        try {
            return core_.getXYStagePosition(deviceName_).y;
        } catch (Exception e) {
            studio_.logs().showError("could not get the y position for " + deviceName_);
            return 0.0;
        }
    }
}
