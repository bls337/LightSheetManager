package org.micromanager.lightsheetmanager.model;

import mmcorej.org.json.JSONException;
import mmcorej.org.json.JSONObject;
import org.micromanager.UserProfile;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.propertymap.MutablePropertyMapView;

import java.util.Iterator;
import java.util.Objects;

public class UserSettings {

    private final String userName;
    private final MutablePropertyMapView settings;

    // This is the prefix String for saving the current acquisition settings
    // based on the microscope geometry type, "LSM_ACQ_SETTINGS_SCAPE" for example.
    private static final String SETTINGS_PREFIX_KEY = "LSM_ACQ_SETTINGS_";
    private static final String SETTINGS_NOT_FOUND = "Settings Not Found";

    // Note: increase this value based on the amount of nested json in the settings
    private static final int MAX_RECURSION_DEPTH_JSON = 4;

    private final LightSheetManagerModel model_;

    public UserSettings(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        // setup user profile
        UserProfile profile = model_.getStudio().getUserProfile();
        userName = profile.getProfileName();
        settings = profile.getSettings(UserSettings.class);
    }

    /**
     * Returns an object to save and retrieve settings.
     *
     * @return a reference to MutablePropertyMapView
     */
    public MutablePropertyMapView get() {
        return settings;
    }

    /**
     * Returns the name of the user profile.
     *
     * @return a String containing the name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Clears all user settings associated with this class name.
     */
    public void clear() {
        settings.clear();
    }

    /**
     * Load user settings.
     */
    public void load() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        // get json from settings based on microscope geometry type
        final String key = SETTINGS_PREFIX_KEY + geometryType;
        final String json = settings.getString(key, SETTINGS_NOT_FOUND);
        System.out.println("loaded json from "
                + SETTINGS_PREFIX_KEY + geometryType + ": " + json);

        // use default settings if settings data not found
        if (!json.equals(SETTINGS_NOT_FOUND)) {
            // validate user settings and create settings object
            JSONObject loadedJson = validateUserSettings(json);
            if (loadedJson != null) {
                // TODO: switch this based on microscope geometry type
                DefaultAcquisitionSettingsDISPIM acqSettings = DefaultAcquisitionSettingsDISPIM.fromJson(
                        loadedJson.toString(), DefaultAcquisitionSettingsDISPIM.class);
                model_.acquisitions().setAcquisitionSettings(acqSettings);
                //System.out.println("loadedJson: " + loadedJson);
            }
            //System.out.println("acqSettings: " + acqSettings);
        }
    }

    /**
     * Save user settings.
     */
    public void save() {
        // build settings before saving to make sure updates are saved
        model_.acquisitions().setAcquisitionSettings(
                model_.acquisitions().settingsBuilder().build());
        // settings key
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();
        final String key = SETTINGS_PREFIX_KEY + geometryType;
        // save in user settings
        settings.putString(key, model_.acquisitions().settings().toJson());
        System.out.println("saved json to " + key + ": "
                + model_.acquisitions().settings().toPrettyJson());
    }

    /**
     * Returns the JSONObject after checking if it matches the schema of the
     * default acquisition settings object. If it does not, then any new settings
     * found will be merged into the loaded settings as the default value.
     *
     * @param loadedSettings the settings loaded as a JSON String
     * @return the settings object or null if an error occurred
     */
    private JSONObject validateUserSettings(final String loadedSettings) {
        // get default settings from builder
        final String defaultSettings =
                new DefaultAcquisitionSettingsDISPIM.Builder().build().toJson();
        // validate json strings and count the number of keys
        int numLoadedKeys, numDefaultKeys;
        JSONObject loadedJson, defaultJson;
        try {
            loadedJson = new JSONObject(loadedSettings);
            defaultJson = new JSONObject(defaultSettings);
            numLoadedKeys = countKeysJson(loadedJson);
            numDefaultKeys = countKeysJson(defaultJson);
        } catch (JSONException e) {
            model_.studio().logs().showError("could not validate the JSON data.");
            return null;
        }
        // different number of keys => merge loaded settings with default settings
        if (numLoadedKeys != numDefaultKeys) {
            try {
                mergeSettingsJson(defaultJson, loadedJson);
            } catch (JSONException e) {
                model_.studio().logs().showError("could not merge new default settings into loaded settings.");
                return null;
            }
        }
        return loadedJson;
    }

    // Overloaded method to give mergeSettingsJson a default parameter.
    private void mergeSettingsJson(JSONObject defaultJson, JSONObject loadedJson) throws JSONException {
        mergeSettingsJson(defaultJson, loadedJson, 0);
    }

    private void mergeSettingsJson(JSONObject defaultJson, JSONObject loadedJson, final int level) throws JSONException {
        // bail out if settings data is nested too deep
        if (level > MAX_RECURSION_DEPTH_JSON) {
            model_.studio().logs().logMessage("UserSettings: recursion too deep, increase max level.");
            return; // early exit => recursion too deep
        }
        // for every key in the default settings, check to make sure the loaded settings has that key
        Iterator<String> keys = defaultJson.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object value = defaultJson.get(key);
            // if the loaded settings are missing the key then add it
            if (!loadedJson.has(key)) {
                loadedJson.put(key, value);
                model_.studio().logs().logMessage("UserSettings: Added key \"" + key + "\" to the loaded settings.");
            }
            // recursively call on sub-objects of type JSONObject
            if (value instanceof JSONObject) {
                JSONObject subDefaultJson = (JSONObject)value;
                JSONObject subLoadedJson = (JSONObject)loadedJson.get(key);
                if (subLoadedJson.length() != subDefaultJson.length()) {
                    mergeSettingsJson(subDefaultJson, subLoadedJson, level+1);
                    loadedJson.put(key, subLoadedJson);
                }
            }
        }
    }

    private int countKeysJson(final JSONObject obj) throws JSONException {
        int numKeys = obj.length();
        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object value = obj.get(key);
            if (value instanceof JSONObject) {
                numKeys += ((JSONObject)value).length();
            }
        }
        return numKeys;
    }

}
