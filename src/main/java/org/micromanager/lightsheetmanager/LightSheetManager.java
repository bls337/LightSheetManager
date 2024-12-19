package org.micromanager.lightsheetmanager;

import mmcorej.CMMCore;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.LightSheetManagerAPI;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.model.DeviceManager;
import org.micromanager.lightsheetmanager.model.PluginSettings;
import org.micromanager.lightsheetmanager.model.UserSettings;
import org.micromanager.lightsheetmanager.model.acquisitions.AcquisitionEngine;
import org.micromanager.lightsheetmanager.model.acquisitions.AcquisitionEngineDISPIM;
import org.micromanager.lightsheetmanager.model.acquisitions.AcquisitionEngineSCAPE;

import java.util.Objects;

/**
 * This is the container for all the data needed to operate a microscope with light sheet manager.
 */
public class LightSheetManager implements LightSheetManagerAPI {

    private final Studio studio_;
    private final CMMCore core_;

    private String errorText_;

    private PluginSettings pluginSettings_;

    private final UserSettings userSettings_;
    private final DeviceManager deviceManager_;

    private AcquisitionEngine acqEngine_;
    //private final AcquisitionTableData acqTableData_;

    public LightSheetManager(final Studio studio) {
        studio_ = Objects.requireNonNull(studio);
        core_ = studio_.core();

        pluginSettings_ = new PluginSettings();
        userSettings_ = new UserSettings(this);

        deviceManager_ = new DeviceManager(studio_, this);
        //acqTableData_ = new AcquisitionTableData();

        // set during setup if there is an error
        errorText_ = "no errors";
    }

    /**
     * Returns true when the model is loaded correctly.
     *
     * @return true if the model loads with no errors
     */
    public boolean setup() {

        // first we check to see if the device adapter is present
        if (!deviceManager_.hasDeviceAdapter()) {
            return false;
        }

        // setup devices
        deviceManager_.setup();

        // create different acq engine based on microscope geometry
        final GeometryType geometryType = deviceManager_
                .getDeviceAdapter().getMicroscopeGeometry();
        switch (geometryType) {
            case SCAPE:
                acqEngine_ = new AcquisitionEngineSCAPE(this);
                break;
            case DISPIM:
                acqEngine_ = new AcquisitionEngineDISPIM(this);
                break;
            default:
                studio_.logs().logError(
                        "setup error, AcquisitionEngine not implemented for " + geometryType);
                return false; // early exit => error
        }

        // load settings
        userSettings_.load();

        // if we made it here then everything loaded correctly
        return true;
    }

    /**
     * This sets the text to be displayed in the error ui when an error occurs during setup.
     *
     * @param text the error message
     */
    public void setErrorText(final String text) {
        errorText_ = text;
    }

    public String getErrorText() {
        return errorText_;
    }

    public UserSettings userSettings() {
        return userSettings_;
    }

    public PluginSettings pluginSettings() {
        return pluginSettings_;
    }

    public void pluginSettings(final PluginSettings pluginSettings) {
        pluginSettings_ = Objects.requireNonNull(pluginSettings);
    }

    public CMMCore getCore() {
        return core_;
    }

    public CMMCore core() {
        return core_;
    }

    public Studio getStudio() {
        return studio_;
    }

    public Studio studio() {
        return studio_;
    }

    public AcquisitionEngine getAcquisitionEngine() {
        return acqEngine_;
    }

    @Override
    public AcquisitionEngine acquisitions() {
        return acqEngine_;
    }

    public DeviceManager getDeviceManager() {
        return deviceManager_;
    }

    @Override
    public DeviceManager devices() {
        return deviceManager_;
    }

}