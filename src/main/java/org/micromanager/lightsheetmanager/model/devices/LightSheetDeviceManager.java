package org.micromanager.lightsheetmanager.model.devices;

import mmcorej.DeviceType;
import mmcorej.StrVector;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.data.LightSheetType;
import org.micromanager.lightsheetmanager.model.utils.jplus.PredicateUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * This class is for interacting with the LightSheetDeviceManager device adapter.
 */
public class LightSheetDeviceManager extends DeviceBase {


    public static final String UNDEFINED = "Undefined";

    // TODO: put this here?
    // private String deviceAdapterVersion_;

    // pre-init properties
    private GeometryType geometryType_;
    private LightSheetType lightSheetType_;
    private int numImagingPaths_;
    private int numIlluminationPaths_;
    private int numSimultaneousCameras_;

    private static final String deviceName_ = "LightSheetDeviceManager";

    public LightSheetDeviceManager(final Studio studio) {
        super(studio, deviceName_);
    }

    // TODO: safely use Integer.parseInt
    /**
     * Gets the pre-init properties from LightSheetDeviceManager and caches them. Call this method before
     * using the getters for pre-init properties.
     */
    public void getPreInitProperties() {
        // convert pre-init property strings to ints
        numImagingPaths_ = Integer.parseInt(getProperty("ImagingPaths"));
        numIlluminationPaths_ = Integer.parseInt(getProperty("IlluminationPaths"));
        numSimultaneousCameras_ = Integer.parseInt(getProperty("SimultaneousCameras"));
        // convert pre-init property strings to enum constants
        geometryType_ = GeometryType.fromString(getProperty("MicroscopeGeometry"));
        lightSheetType_ = LightSheetType.fromString(getProperty("LightSheetType"));
        //System.out.println("geometryType_: " + geometryType_);
    }

    // TODO: is this needed?
    private int tryIntParse(final String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public GeometryType getMicroscopeGeometry() {
        return geometryType_;
    }

    public LightSheetType getLightSheetType() {
        return lightSheetType_;
    }

    public int getNumImagingPaths() {
        return numImagingPaths_;
    }

    public int getNumIlluminationPaths() {
        return numIlluminationPaths_;
    }

    public int getNumSimultaneousCameras() {
        return numSimultaneousCameras_;
    }

    private boolean isPropertyUndefined(final String propertyName) {
        return getProperty(propertyName).equals(UNDEFINED);
    }

    // check for ASI diSPIM hardware triggering device
    public boolean isUsingPLogic() {
        boolean result = false;
        if (getMicroscopeGeometry().equals(GeometryType.DISPIM)) {
            final boolean isLaserPLogic = getProperty("TriggerLaser").contains("PLogic");
            final boolean isCameraPLogic = getProperty("TriggerCamera").contains("PLogic");
            if (isLaserPLogic && !isCameraPLogic || !isLaserPLogic && isCameraPLogic) {
                studio_.logs().showError("PLogic must be set as both the camera and laser trigger.");
            }
            if (isLaserPLogic && isCameraPLogic) {
                result = true;
            }
        }
        return result;
    }

    public Map<String, String> getDeviceMap() {
        final String[] properties = getDevicePropertyNames();
        return Arrays.stream(properties)
                .filter(PredicateUtils.not(this::isPropertyPreInit))
                .filter(PredicateUtils.not(this::isPropertyReadOnly))
                .collect(Collectors.toMap(p -> p, this::getProperty));
    }

    public Map<String, DeviceType> getDeviceTypeMap() {
        final Map<String, String> deviceMap = getDeviceMap();
        return deviceMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getDeviceType(e.getValue())));
    }

}
