package org.micromanager.lightsheetmanager.model.devices;

import org.micromanager.Studio;

public class Stage extends DeviceBase {

    public Stage(final Studio studio, final String deviceName) {
        super(studio, deviceName);
    }

    public void setPosition(final double position) {
        try {
            core_.setPosition(deviceName_, position);
        } catch (Exception e) {
            studio_.logs().showError("could not set position for " + deviceName_);
        }
    }

    public double getPosition() {
        try {
            return core_.getPosition(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("could not get position for " + deviceName_);
            return 0.0;
        }
    }

}