package org.micromanager.lightsheetmanager.model.devices;

import mmcorej.CMMCore;
import mmcorej.DeviceType;
import mmcorej.StrVector;
import org.micromanager.Studio;

import java.util.Arrays;
import java.util.Objects;

public class DeviceBase {

    protected final Studio studio_;
    protected final CMMCore core_;

    protected String deviceName_;

    public DeviceBase(final Studio studio) {
        studio_ = Objects.requireNonNull(studio);
        core_ = studio_.core();
        deviceName_ = "Unnamed";
    }

    public DeviceBase(final Studio studio, final String deviceName) {
        studio_ = Objects.requireNonNull(studio);
        core_ = studio_.core();
        deviceName_ = deviceName;
    }

    public void setDeviceName(final String deviceName) {
        deviceName_ = deviceName;
    }

    public String getDeviceName() {
        return deviceName_;
    }

    public boolean hasProperty(final String propertyName) {
        boolean result = false;
        try {
            result = core_.hasProperty(deviceName_, propertyName);
        } catch (Exception e) {
            studio_.logs().showError("Error: could not determine if " + propertyName + " of "
                    + deviceName_ + " exists: " + e.getMessage());
        }
        return result;
    }

    public String getProperty(final String propertyName) {
        String result = "";
        try {
            result = core_.getProperty(deviceName_, propertyName);
        } catch (Exception e) {
            studio_.logs().logError("Could not get the \"" + propertyName + "\" property of "
                    + deviceName_ + ": " + e.getMessage());
        }
        return result;
    }

    public void setProperty(final String propertyName, final String propertyValue) {
        try {
            core_.setProperty(deviceName_, propertyName, propertyValue);
        } catch (Exception e) {
            studio_.logs().logError("Could not set the \"" + propertyName + "\" property of "
                    + deviceName_ + " to " + propertyValue + ": " + e.getMessage());
        }
    }

    public int getPropertyInt(final String propertyName) {
        int result = 0;
        try {
            result = Integer.parseInt(core_.getProperty(deviceName_, propertyName));
        } catch (Exception e) {
            studio_.logs().logError("Could not get the \"" + propertyName + "\" property of "
                    + deviceName_ + " as an integer: " + e.getMessage());
        }
        return result;
    }

    public void setPropertyInt(final String propertyName, final int propertyValue) {
        try {
            core_.setProperty(deviceName_, propertyName, propertyValue);
        } catch (Exception e) {
            studio_.logs().logError("Could not set the \"" + propertyName + "\" property of "
                    + deviceName_ + " to " + propertyValue + ": " + e.getMessage());
        }
    }

    // Note: this refers to the Micro-Manager FloatProperty, not the Java float type.
    // See: the FloatProperty class in mmCoreAndDevices/MMDevice/Property.h
    public double getPropertyFloat(final String propertyName) {
        double result = 0.0;
        try {
            result = Double.parseDouble(core_.getProperty(deviceName_, propertyName));
        } catch (Exception e) {
            studio_.logs().logError("Could not get the \"" + propertyName + "\" property of "
                    + deviceName_ + " as a double: " + e.getMessage());
        }
        return result;
    }

    public void setPropertyFloat(final String propertyName, final double propertyValue) {
        try {
            core_.setProperty(deviceName_, propertyName, propertyValue);
        } catch (Exception e) {
            studio_.logs().logError("Could not set the \"" + propertyName + "\" property of "
                    + deviceName_ + " to " + propertyValue + ": " + e.getMessage());
        }
    }

    public double getPropertyUpperLimit(final String propertyName) {
        try {
            if (core_.hasPropertyLimits(deviceName_, propertyName)) {
                return core_.getPropertyUpperLimit(deviceName_, propertyName);
            }
        } catch (Exception e) {
            studio_.logs().logDebugMessage("Property " + propertyName + " has no upper limit or is non-numeric.");
        }
        return Double.MAX_VALUE;
    }

    public double getPropertyLowerLimit(final String propertyName) {
        try {
            if (core_.hasPropertyLimits(deviceName_, propertyName)) {
                return core_.getPropertyLowerLimit(deviceName_, propertyName);
            }
        } catch (Exception e) {
            studio_.logs().logDebugMessage("Property " + propertyName + " has no lower limit or is non-numeric.");
        }
        return -Double.MAX_VALUE; // Double.MIN_VALUE is a value close to 0 so use -Double.MAX_VALUE
    }

    public boolean isPropertyPreInit(final String propertyName) {
        boolean result = false;
        try {
            result = core_.isPropertyPreInit(deviceName_, propertyName);
        } catch (Exception e) {
            studio_.logs().logError("Could not tell whether the \"" + propertyName
                    + "\" property of " + deviceName_ + " is pre-init: " + e.getMessage());
        }
        return result;
    }

    public boolean isPropertyReadOnly(final String propertyName) {
        boolean result = false;
        try {
            result = core_.isPropertyReadOnly(deviceName_, propertyName);
        } catch (Exception e) {
            studio_.logs().logError("Could not tell whether the \"" + propertyName
                    + "\" property of " + deviceName_ + " is read-only: " + e.getMessage());
        }
        return result;
    }

    public DeviceType getDeviceType() {
        try {
            return core_.getDeviceType(deviceName_);
        } catch (Exception e) {
            return DeviceType.UnknownType;
        }
    }

    public String getDeviceLibrary() {
        try {
            return core_.getDeviceLibrary(deviceName_);
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public String[] getDevicePropertyNames() {
        StrVector devices = new StrVector();
        try {
            devices = core_.getDevicePropertyNames(deviceName_);
        } catch (Exception e) {
            studio_.logs().logError("Could not get the property names of " + deviceName_ + ": "
                    + e.getMessage());
        }
        return devices.toArray();
    }

    public String[] getAllowedPropertyValues(final String propertyName) {
        try {
            return core_.getAllowedPropertyValues(deviceName_, propertyName).toArray();
        } catch (Exception e) {
            studio_.logs().logError("Could not get the allowed values of the \"" + propertyName
                    + "\" property of " + deviceName_ + ": " + e.getMessage());
            return new String[0];
        }
    }

    public String[] getEditableProperties(final String[] properties) {
        return Arrays.stream(properties)
                .filter(p -> !isPropertyPreInit(p))
                .filter(p -> !isPropertyReadOnly(p))
                .toArray(String[]::new);
    }

    public void halt() {
       try {
          core_.stop(deviceName_);
       } catch (Exception e) {
          studio_.logs().logError("Error halting device " + deviceName_ + ": " + e.getMessage());
       }
    }

    public void waitForDevice() {
        try {
            core_.waitForDevice(deviceName_);
        } catch (Exception e) {
            studio_.logs().logError("Error waiting for device " + deviceName_ + ": "
                    + e.getMessage());
        }
    }

}
