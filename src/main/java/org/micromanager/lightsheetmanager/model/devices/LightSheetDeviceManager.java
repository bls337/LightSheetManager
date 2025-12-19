package org.micromanager.lightsheetmanager.model.devices;

import mmcorej.DeviceType;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.data.LightSheetType;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The device adapter "LightSheetDeviceManager".
 *
 * <p>Contained in the DeviceManager object as "LightSheetDeviceManager".
 */
public class LightSheetDeviceManager extends DeviceBase {

    // used to indicate that the device is not set to any hardware
    public static final String UNDEFINED = "Undefined";

    // TODO: use this for validation
    // TODO: parse this to a more useful object for version comparisons?
    private String version_;

    // pre-init properties
    private GeometryType geometryType_;
    private LightSheetType lightSheetType_;
    private int numImagingPaths_;
    private int numIlluminationPaths_;
    private int numSimultaneousCameras_;

    public LightSheetDeviceManager(final Studio studio, final String deviceName) {
        super(studio, deviceName);
        loadPreInitProperties();
    }

    /**
     * Queries the pre-init properties from the device adapter and caches them.
     */
    private void loadPreInitProperties() {
        version_ = getProperty("Version");
        geometryType_ = GeometryType.fromString(getProperty("MicroscopeGeometry"));
        lightSheetType_ = LightSheetType.fromString(getProperty("LightSheetType"));
        // change defaults based on microscope geometry
        int defaultImaging = 1;
        int defaultIllumination = 1;
        if (geometryType_ == GeometryType.DISPIM) {
            defaultImaging = 2;
            defaultIllumination = 2;
        }
        numImagingPaths_ = parsePropertyInt("ImagingPaths", defaultImaging);
        numIlluminationPaths_ = parsePropertyInt("IlluminationPaths", defaultIllumination);
        numSimultaneousCameras_ = parsePropertyInt("SimultaneousCameras", 1);
    }

    /**
     * Safely parses a device property to an integer, returns the default value on failure.
     */
    private int parsePropertyInt(final String propertyName, final int defaultValue) {
        try {
            return Integer.parseInt(getProperty(propertyName));
        } catch (NumberFormatException e) {
            studio_.logs().logError("Error parsing " + propertyName
                    + " from the device adapter, use default value " + defaultValue);
            return defaultValue;
        }
    }

    private boolean isPositionDevice(final String deviceName) {
        final DeviceType deviceType = getDeviceType(deviceName);
        return deviceType == DeviceType.StageDevice
                || deviceType == DeviceType.XYStageDevice
                || deviceType == DeviceType.GalvoDevice;
    }

    private DeviceType getDeviceType(final String deviceName) {
        try {
            return core_.getDeviceType(deviceName);
        } catch (Exception e) {
            return DeviceType.UnknownType;
        }
    }

    public String version() {
        return version_;
    }

    public GeometryType geometry() {
        return geometryType_;
    }

    public LightSheetType lightSheetType() {
        return lightSheetType_;
    }

    public int numImagingPaths() {
        return numImagingPaths_;
    }

    public int numIlluminationPaths() {
        return numIlluminationPaths_;
    }

    public int numSimultaneousCameras() {
        return numSimultaneousCameras_;
    }

    /**
     * Returns a map of internal device names to hardware device names.
     *
     * @return a map of internal device names to hardware device names
     */
    public Map<String, String> deviceMap() {
        final String[] properties = getDevicePropertyNames();
        return Arrays.stream(properties)
                .filter(p -> !isPropertyPreInit(p) && !isPropertyReadOnly(p))
                .map(p -> Map.entry(p, getProperty(p)))
                .filter(e -> !e.getValue().equals(UNDEFINED))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Returns a map of internal device names to device types.
     *
     * @return a map of internal device names to device types
     */
    public Map<String, DeviceType> deviceTypeMap() {
        return deviceMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getDeviceType(e.getValue())));
    }

    /**
     * Returns an array of internal device names for all position devices.
     * <p>
     * A "Position Device" is a StageDevice, XYStageDevice, or GalvoDevice.
     *
     * @return an array of internal devices names for all position devices
     */
    public String[] positionDevices() {
        return deviceMap().entrySet().stream()
                .filter(entry -> isPositionDevice(entry.getValue()))
                .map(Map.Entry::getKey)
                .toArray(String[]::new);
    }

    /**
     * Return {@code true} if the device adapter has the deviceName property
     * set to a value other than the default: "Undefined".
     * <p>
     * Example deviceName properties: "SampleXY", "ImagingFocus".
     *
     * @param deviceName the name of the device in the device adapter
     * @return {@code true} if the device is not "Undefined".
     */
    public boolean hasDevice(final String deviceName) {
        return !getProperty(deviceName).equals(UNDEFINED);
    }

}
