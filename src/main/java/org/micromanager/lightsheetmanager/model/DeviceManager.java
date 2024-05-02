package org.micromanager.lightsheetmanager.model;

import mmcorej.Configuration;
import mmcorej.StrVector;
import org.micromanager.lightsheetmanager.api.data.CameraLibrary;
import mmcorej.CMMCore;
import mmcorej.DeviceType;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.gui.utils.DialogUtils;
import org.micromanager.lightsheetmanager.model.devices.DeviceBase;
import org.micromanager.lightsheetmanager.model.devices.Galvo;
import org.micromanager.lightsheetmanager.model.devices.LightSheetDeviceManager;
import org.micromanager.lightsheetmanager.model.devices.NIDAQ;
import org.micromanager.lightsheetmanager.model.devices.Stage;
import org.micromanager.lightsheetmanager.model.devices.XYStage;
import org.micromanager.lightsheetmanager.model.devices.cameras.AndorCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.CameraBase;
import org.micromanager.lightsheetmanager.model.devices.cameras.DemoCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.HamamatsuCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.PCOCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.PVCamera;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPLogic;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPiezo;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIXYStage;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIZStage;

import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility for extracting information from LightSheetDeviceManager.
 *
 * <p>This class maps device strings to device objects.
 */
public class DeviceManager {

    private final Studio studio_;
    private final CMMCore core_;

    private Map<String, DeviceBase> deviceMap_;
    private Map<String, DeviceBase> devicesAdded_;

    private LightSheetManagerModel model_;

    private static String deviceAdapterName_;
    public static final String LSM_DEVICE_LIBRARY = "LightSheetManager";

    public DeviceManager(final Studio studio, final LightSheetManagerModel model) {
        studio_ = Objects.requireNonNull(studio);
        model_ = Objects.requireNonNull(model);
        core_ = studio_.core();

        // set by hasDeviceAdapter
        deviceAdapterName_ = "";

        // TODO: ConcurrentHashMap or HashMap?
        deviceMap_ = new ConcurrentHashMap<>();
        devicesAdded_ = new HashMap<>();
    }

    /**
     * Creates the device map from the device adapter properties.
     * <p>
     * Properties that are set to "Undefined" are ignored.
     */
    public void setup() {
        studio_.logs().logMessage("DeviceManager: [Begin Setup]");

        // make sure this is empty
        devicesAdded_.clear();

        // always add an entry for the device adapter
        LightSheetDeviceManager lsm = new LightSheetDeviceManager(studio_, deviceAdapterName_);
        lsm.getPreInitProperties();
        deviceMap_.put("LightSheetDeviceManager", lsm);

        // keep track of devices we have already added to the map
        // used when multiple properties are mapped to the same device
        //HashMap<String, DeviceBase> devicesAdded = new HashMap<>();

        String[] props = lsm.getDevicePropertyNames();
        String[] properties = lsm.getEditableProperties(props);

        for (String propertyName : properties) {
            final String deviceName = lsm.getProperty(propertyName);

            // skip properties that don't have a device assigned
            if (deviceName.equals(LightSheetDeviceManager.UNDEFINED)) {
                continue;
            }

            final DeviceType deviceType = getDeviceType(deviceName);
            final String deviceLibrary = getDeviceLibrary(deviceName);

            System.out.println(propertyName + " " + deviceType);

            // skip properties with no known DeviceType
            if (deviceType == DeviceType.UnknownType) {
                continue;
            }

            // object was already created so grab a reference to it
            if (devicesAdded_.containsKey(deviceName)) {
                deviceMap_.put(propertyName, devicesAdded_.get(deviceName));
                final String className = devicesAdded_.get(deviceName).getClass().getSimpleName();
                studio_.logs().logMessage("DeviceManager: " + propertyName + " set to "
                        + className + "(" + deviceName + ") (reused)");
                continue;
            }

            // add device objects to the device map
            if (deviceType == DeviceType.XYStageDevice) {
                if (deviceLibrary.equals("ASITiger")) {
                    ASIXYStage xyStage = new ASIXYStage(studio_, deviceName);
                    addDevice(propertyName, deviceName, xyStage);
                } else {
                    // generic XY stage device
                    XYStage xyStage = new XYStage(studio_, deviceName);
                    addDevice(propertyName, deviceName, xyStage);
                }
            } else if (deviceType == DeviceType.StageDevice) {
                if (deviceLibrary.equals("ASITiger")) {
                    if (deviceName.contains("Piezo")) {
                        ASIPiezo piezo = new ASIPiezo(studio_, deviceName);
                        addDevice(propertyName, deviceName, piezo);
                    }
                    if (deviceName.contains("ZStage")) {
                        ASIZStage zStage = new ASIZStage(studio_, deviceName);
                        addDevice(propertyName, deviceName, zStage);
                    }
                } else {
                    // generic stage device
                    Stage stage = new Stage(studio_, deviceName);
                    addDevice(propertyName, deviceName, stage);
                }
            } else if (deviceType == DeviceType.GalvoDevice) {
                if (deviceLibrary.equals("ASITiger")) {
                    ASIScanner scanner = new ASIScanner(studio_, deviceName);
                    addDevice(propertyName, deviceName, scanner);
                } else {
                    // use generic galvo device
                    Galvo galvo = new Galvo(studio_);
                    addDevice(propertyName, deviceName, galvo);
                }
            } else if (deviceType == DeviceType.ShutterDevice) {
                // Check if ASI PLogic or NIDAQ board is present
                if (deviceLibrary.equals("ASITiger")) {
                    ASIPLogic plc = new ASIPLogic(studio_, deviceName);
                    addDevice(propertyName, deviceName, plc);
                } else if (deviceLibrary.equals("NIDAQ")) {
                    NIDAQ nidaq = new NIDAQ(studio_, deviceName);
                    addDevice(propertyName, deviceName, nidaq);
                }
            } else if (deviceType == DeviceType.CameraDevice) {
                createCameraDevice(propertyName, deviceName,
                        CameraLibrary.fromString(deviceLibrary));
            }
            //deviceMap_.put(propertyName, "");
        }
        System.out.println("----------------");

        // we don't need this array anymore
        devicesAdded_.clear();

        studio_.logs().logMessage("DeviceManager: [End Setup]");
    }

    private void addDevice(final String propertyName, final String deviceName, final DeviceBase device) {
        deviceMap_.put(propertyName, device);
        devicesAdded_.put(deviceName, device);
        studio_.logs().logMessage("DeviceManager: " + propertyName + " set to "
                + device.getClass().getSimpleName() + "(" + deviceName + ")");
    }

    private void createCameraDevice(final String propertyName, final String deviceName, CameraLibrary cameraLibrary) {
        switch (cameraLibrary) {
            case ANDORSDK3:
                AndorCamera andorCamera = new AndorCamera(studio_, deviceName);
                addDevice(propertyName, deviceName, andorCamera);
                break;
            case HAMAMATSU:
                HamamatsuCamera hamaCamera = new HamamatsuCamera(studio_, deviceName);
                addDevice(propertyName, deviceName, hamaCamera);
                break;
            case PCOCAMERA:
                PCOCamera pcoCamera = new PCOCamera(studio_, deviceName);
                addDevice(propertyName, deviceName, pcoCamera);
                break;
            case PVCAM:
                PVCamera pvCamera = new PVCamera(studio_, deviceName);
                addDevice(propertyName, deviceName, pvCamera);
                break;
            case DEMOCAMERA:
                DemoCamera demoCamera = new DemoCamera(studio_, deviceName);
                addDevice(propertyName, deviceName, demoCamera);
                break;
            default:
                CameraBase camera = new CameraBase(studio_, deviceName);
                addDevice(propertyName, deviceName, camera);
                studio_.logs().logError(
                        "Camera device library \"" + cameraLibrary + "\" not supported, using basic camera.");
                break;
        }
    }

    private void createShutterDevice() {

    }

    private void createXYStageDevice() {

    }

    // Note: clients should use var when we support Java 11
//    public DeviceBase getDevice(final String deviceName) {
//        return deviceMap_.get(deviceName);
//    }

    /**
     * Returns the device given by {@code deviceName} as type {@code T}.
     * The caller is responsible for assigning the returned
     * value to the correct type.
     * <P><P>
     * Typesafe: The client can only cast the return value to a subclass
     * of DeviceBase, avoiding the ClassCastException at compile time.
     *
     * @param deviceName the device name
     * @return the device as a subclass of DeviceBase
     * @param <T> the generic type to cast the result to
     */
    @SuppressWarnings("unchecked")
    public <T extends DeviceBase> T getDevice(final String deviceName) {
        //System.out.println(deviceName + " " + deviceMap_.get(deviceName).getDeviceName());
        return (T) deviceMap_.get(deviceName);
    }

    public DeviceBase getImagingCamera() {
        return deviceMap_.get("ImagingCamera");
    }

    public DeviceBase getImagingCamera(final int side) {
        return deviceMap_.get("Imaging" + side + "Camera");
    }

    public DeviceBase getImagingCamera(final int side, final int num) {
        return deviceMap_.get("Imaging" + side + "Camera" + num);
    }

    public LightSheetDeviceManager getDeviceAdapter() {
        return (LightSheetDeviceManager)deviceMap_.get("LightSheetDeviceManager");
    }

    public String getDeviceLibrary(final String deviceName) {
        String result = "";
        try {
            result = core_.getDeviceLibrary(deviceName);
        } catch (Exception e) {
            studio_.logs().logError(e.getMessage());
        }
        return result;
    }

    private DeviceType getDeviceType(final String deviceName) {
        try {
            return core_.getDeviceType(deviceName);
        } catch (Exception e) {
            return DeviceType.UnknownType;
        }
    }

    public String[] getLoadedDevices() {
        StrVector loadedDevices = new StrVector();
        try {
            loadedDevices = core_.getLoadedDevices();
        } catch (Exception e) {
            studio_.logs().logError(e.getMessage());
        }
        return loadedDevices.toArray();
    }

    /**
     * Returns true if the hardware configuration has the LightSheetManager device adapter. The user can
     * change the device name of the adapter, but not the device library so that's what we detect. Also,
     * the name of the device adapter is cached for later usage. This also set the error text on the model
     * when an error is encountered, this is used in the error user interface.
     *
     * @return true if the hardware configuration has the device adapter
     */
    public boolean hasDeviceAdapter() {
        int count = 0;
        final String[] devices = getLoadedDevices();
        for (String device : devices) {
            try {
                final String deviceLibrary = core_.getDeviceLibrary(device);
                if (deviceLibrary.equals(LSM_DEVICE_LIBRARY)) {
                    deviceAdapterName_ = device;
                    count++;
                    if (count > 1) {
                        model_.setErrorText("You have multiple instances of the LightSheetManager " +
                                "device adapter in your hardware configuration.");
                        break; // exit loop because this a failure condition
                    }
                }
            } catch (Exception e) {
                studio_.logs().logError("could not get the device " +
                        "library for the device \"" + device + "\".");
            }
        }
        // no device adapters found
        if (count == 0) {
            model_.setErrorText("Please add the LightSheetManager device adapter to your " +
                    "hardware configuration to use this plugin.");
        }
        return count == 1;
    }

    // check for ASI hardware triggering device
    public boolean isUsingPLogic() {
        // make sure that we have devices set
        if (deviceMap_.get("TriggerLaser") == null && deviceMap_.get("TriggerCamera") == null) {
            return false;
        }
        // check if both device names contain "PLogic"
        boolean result = false;
        final boolean isLaserPLogic = deviceMap_.get("TriggerLaser").getDeviceName().contains("PLogic");
        final boolean isCameraPLogic = deviceMap_.get("TriggerCamera").getDeviceName().contains("PLogic");
        if (isLaserPLogic && !isCameraPLogic || !isLaserPLogic && isCameraPLogic) {
            studio_.logs().showError("PLogic must be set as both the camera and laser trigger.");
        }
        if (isLaserPLogic && isCameraPLogic) {
            result = true;
        }
        return result;
    }

    /**
     * Creates a configuration group named "System" that includes all device properties
     * the Light Sheet Manager device adapter.
     */
    public void createConfigGroup() {
        final String groupName = "System";
        final String configName = "Startup";

        // create group
        if (!core_.isGroupDefined(groupName)) {
            try {
                core_.defineConfigGroup(groupName);
            } catch (Exception e) {
                studio_.logs().logError("could not create the \"" + groupName + "\" configuration group.");
                return; // early exit
            }
            // create config
            if (!core_.isConfigDefined(groupName, configName)) {
                try {
                    core_.defineConfig(groupName, configName);
                } catch (Exception e) {
                    studio_.logs().logError("could not create the \"" + configName + "\" configuration preset.");
                    return; // early exit
                }
            }
        }

        ArrayList<String> updatedProperties = updateConfig(groupName, configName);

        if (updatedProperties.isEmpty()) {
            studio_.logs().showMessage("All device adapter properties are present in the "
                    + groupName + "::" + configName + " configuration group.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String property : updatedProperties) {
                sb.append(property).append("\n");
            }
            studio_.logs().showMessage("Added properties to the "
                    + groupName + "::" + configName + " configuration group: \n" + sb);
        }
    }


    /**
     * Returns a list of new properties added to the configuration group.
     *
     * @param groupName the group name to check
     * @param configName the configuration group to check
     * @return an ArrayList of new properties
     */
    private ArrayList<String> updateConfig(final String groupName, final String configName) {
        ArrayList<String> newProperties = new ArrayList<>();
        final String[] props = getDeviceAdapter().getDevicePropertyNames();
        final String[] properties = getDeviceAdapter().getEditableProperties(props);

        Configuration config;
        try {
            config = core_.getConfigData(groupName, configName);
        } catch (Exception e) {
            studio_.logs().showError("could not get configuration data!");
            return newProperties; // early exit => could not get config data to compare
        }

        for (String propertyName : properties) {
            if (!config.isPropertyIncluded(deviceAdapterName_, propertyName)) {
                try {
                    core_.defineConfig(groupName, configName,
                            deviceAdapterName_, propertyName, LightSheetDeviceManager.UNDEFINED);
                    newProperties.add(propertyName);
                } catch (Exception e) {
                    studio_.logs().logError("Could not create the \"" + propertyName
                            + "\" property for the \"" + groupName + "\" configuration group.");
                }
            }
        }

        // update MM ui
        if (!newProperties.isEmpty()) {
            studio_.getApplication().refreshGUI();
        }
        return newProperties;
    }

    // TODO: adapt for diSPIM and multiple cameras
    /**
     * Check user settings and ask to change settings with dialogs.
     */
    public void checkDevices(final JFrame frame) {

        final String cameraKey = "ImagingCamera";
        CameraBase cameraDevice = getDevice(cameraKey);
        CameraLibrary cameraLib = CameraLibrary.UNKNOWN;
        if (cameraDevice != null) {
            cameraLib = CameraLibrary.fromString(cameraDevice.getDeviceLibrary());
        }

        switch (cameraLib) {
            case HAMAMATSU:
                // Flash4, Fusion, etc
                HamamatsuCamera camera = getDevice(cameraKey);
                if (camera.getTriggerPolarity().equals(HamamatsuCamera.Values.NEGATIVE)) {
                    final int result = DialogUtils.showYesNoDialog(frame, "Hamamatsu Camera",
                            "The trigger polarity should be set to POSITIVE. Set it now?");
                    if (result == 0) {
                        camera.setTriggerPolarity(HamamatsuCamera.Values.POSITIVE);
                    }
                }
                break;
            case PVCAM:
                // Kinetix, etc
                break;
            default:
                break;
        }
    }

}
