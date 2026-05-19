package org.micromanager.lightsheetmanager;

import mmcorej.CMMCore;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.LightSheetManagerApi;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.model.DeviceManager;
import org.micromanager.lightsheetmanager.model.PluginSettings;
import org.micromanager.lightsheetmanager.model.acquisitions.LightSheetEventAdapter;
import org.micromanager.lightsheetmanager.model.positions.PositionUpdater;
import org.micromanager.lightsheetmanager.model.UserSettings;
import org.micromanager.lightsheetmanager.model.acquisitions.AcquisitionEngine;
import org.micromanager.lightsheetmanager.model.acquisitions.AcquisitionEngineDispim;
import org.micromanager.lightsheetmanager.model.acquisitions.AcquisitionEngineScape;

import java.util.Objects;

/**
 * This is the container for all the data needed to operate a microscope with light sheet manager.
 */
public class LightSheetManager implements LightSheetManagerApi, AutoCloseable {

    private final Studio studio_;
    private final CMMCore core_;

    private String errorText_;

    private PluginSettings pluginSettings_;

    private final UserSettings userSettings_;
    private final DeviceManager deviceManager_;
    private final PositionUpdater positionUpdater_;

    private AcquisitionEngine acqEngine_;
    //private final AcquisitionTableData acqTableData_;

    public LightSheetManager(final Studio studio) {
        studio_ = Objects.requireNonNull(studio,
                "Micro-Manager Studio context cannot be null!");
        core_ = studio_.core();

        pluginSettings_ = new PluginSettings();
        userSettings_ = new UserSettings(this);

        deviceManager_ = new DeviceManager(studio_, this);
        positionUpdater_ = new PositionUpdater(this);

        // set during setup if there is an error
        errorText_ = "";
    }

    /**
     * Returns true when the model is loaded correctly.
     *
     * @return true if the model loads with no errors
     */
    public boolean setup() {

        // first we check to see if the device adapter is present
        if (!deviceManager_.hasDeviceAdapter()) {
            final String message = "Could not find the Light Sheet Manager " +
                    "device adapter in the hardware configuration.";
            studio_.logs().logError(message);
            errorText_ = message;
            return false;
        }

        // setup devices
        deviceManager_.setup();
        positionUpdater_.setup();

        // create different acq engine based on microscope geometry
        final GeometryType geometryType = deviceManager_.adapter().geometry();
        switch (geometryType) {
            case SCAPE:
                acqEngine_ = new AcquisitionEngineScape(this);
                break;
            case DISPIM:
                acqEngine_ = new AcquisitionEngineDispim(this);
                break;
            default:
                final String message = "AcquisitionEngine not implemented for " + geometryType;
                studio_.logs().logError(message);
                errorText_ = message;
                return false; // early exit => show error ui
        }

        // load settings
        userSettings_.load();

        // validate settings
        if (!devices().validateCameras()) {
            return false; // early exit => show error ui
        }

        // TODO: put this somewhere better, need to put this value into LightSheetEventAdapter for now
        LightSheetEventAdapter.isUsingMultipleCameras =
              deviceManager_.adapter().numSimultaneousCameras() > 1;

        // if we made it here then everything loaded correctly
        return true;
    }

    /**
     * Save the settings and stop polling, should be called before exiting.
     */
    @Override
    public void close() {

        // disable position polling
        try {
            if (positionUpdater_ != null) {
                if (positionUpdater_.isPolling()) {
                    positionUpdater_.stopPolling();
                    if (studio_ != null) {
                        studio_.logs().logMessage("Polling stopped.");
                    }
                }
            }
        } catch (Exception e) {
            // Log the error but don't rethrow, so we can still try to save settings!
            if (studio_ != null) {
                studio_.logs().logError(e, "Failed to stop position updater polling during close.");
            }
        }

        // save settings
        try {
            if (userSettings_ != null) {
                userSettings_.save();
                if (studio_ != null) {
                    studio_.logs().logMessage("User settings saved.");
                }
            }
        } catch (Exception e) {
            if (studio_ != null) {
                studio_.logs().logError(e, "Failed to save user settings during close.");
            }
        }

        if (studio_ != null) {
            studio_.logs().logMessage("Light Sheet Manager Shutdown");
        }
    }

    /**
     * Sets the text in the error ui when an error occurs during setup.
     *
     * @param text the error message
     */
    public void setupErrorMessage(final String text) {
        errorText_ = text;
    }

    /**
     * Returns the error message from the setup method, it will be empty
     * in the case of no errors detected.
     *
     * @return the error message
     */
    public String setupErrorMessage() {
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

    public CMMCore core() {
        return core_;
    }

    public Studio studio() {
        return studio_;
    }

    @Override
    public AcquisitionEngine acquisitions() {
        return acqEngine_;
    }

    @Override
    public DeviceManager devices() {
        return deviceManager_;
    }

    public PositionUpdater positions() {
        return positionUpdater_;
    }

}
