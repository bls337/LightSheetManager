package org.micromanager.lightsheetmanager.model.devices.vendor;

import org.micromanager.Studio;

public class ASIZStage extends ASITigerBase {

    private final Joystick joystick_;

    public ASIZStage(final Studio studio, final String deviceName) {
        super(studio, deviceName);
        joystick_ = new Joystick(studio, deviceName);
    }

    public Joystick js() {
        return joystick_;
    }

    public void setPosition(final double position) {
        try {
            core_.setPosition(deviceName_, position);
        } catch (Exception e) {
            studio_.logs().showError("could not set position for " + deviceName_);
        }
    }

    public void setRelativePosition(final double position) {
        try {
            core_.setRelativePosition(deviceName_, position);
        } catch (Exception e) {
            studio_.logs().showError("could not set relative position for " + deviceName_);
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

    public void setTTLInputMode(final String mode) {
        setProperty("TTLInputMode", mode);
    }

    public String getTTLInputMode() {
        return getProperty("TTLInputMode");
    }

    public static class Properties {

    }

//    public static enum TTLInputMode {
//
//    }
}
