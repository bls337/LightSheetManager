package org.micromanager.lightsheetmanager.model;

import mmcorej.org.json.JSONException;
import mmcorej.org.json.JSONObject;
import org.micromanager.UserProfile;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsSCAPE;
import org.micromanager.propertymap.MutablePropertyMapView;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * Micro-Manager user settings for the current user.
 * <p>
 * This class is used to save and load settings using JSON strings.
 */
public class UserSettings {

    private final String userName_;
    private final MutablePropertyMapView settings_;

    // This is the prefix String for saving the current acquisition settings
    // based on the microscope geometry type, "LSM_ACQ_SETTINGS_SCAPE" for example.
    // Note: GeometryType will be converted to uppercase: "LSM_ACQ_SETTINGS_DISPIM".
    private static final String SETTINGS_KEY =  "LSM_PLUGIN_SETTINGS";
    private static final String SETTINGS_PREFIX_KEY = "LSM_ACQ_SETTINGS_";
    private static final String SETTINGS_NOT_FOUND = "Settings Not Found";

    // Note: increase this value based on the amount of nested json in the settings
    private static final int MAX_RECURSION_DEPTH_JSON = 4;

    private final LightSheetManagerModel model_;

    public UserSettings(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        // setup user profile
        final UserProfile profile = model_.getStudio().getUserProfile();
        settings_ = profile.getSettings(UserSettings.class);
        userName_ = profile.getProfileName();
    }

    /**
     * Returns an object to save and retrieve settings.
     *
     * @return a reference to MutablePropertyMapView
     */
    public MutablePropertyMapView get() {
        return settings_;
    }

    /**
     * Returns the name of the user profile.
     *
     * @return a {@code String} containing the name
     */
    public String getUserName() {
        return userName_;
    }

    /**
     * Clears all user settings associated with this profile name.
     */
    public void clear() {
        settings_.clear();
    }

    /**
     * Load user settings.
     */
    public void load() {
        // get json from settings based on microscope geometry type
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        final String key = SETTINGS_PREFIX_KEY + geometryType.toString().toUpperCase();
        final String json = settings_.getString(key, SETTINGS_NOT_FOUND);

        // use default settings if settings data not found in profile
        if (json.equals(SETTINGS_NOT_FOUND)) {
            model_.studio().logs().logDebugMessage(
                    "settings not found, using default settings for " + geometryType);
        } else {
            // validate user settings and create settings object
            final Optional<JSONObject> loadedJson = validateUserSettings(json);
            if (loadedJson.isPresent()) {
                // TODO: switch this based on microscope geometry type
                final DefaultAcquisitionSettingsSCAPE acqSettings = DefaultAcquisitionSettingsSCAPE.fromJson(
                        loadedJson.get().toString(), DefaultAcquisitionSettingsSCAPE.class);
                // update both the settings and builder
                model_.acquisitions().setAcquisitionSettingsAndBuilder(acqSettings);
                model_.studio().logs().logDebugMessage("loaded JSON from " + key + ": "
                        + model_.acquisitions().settings().toPrettyJson());
            }
        }

        // load plugin settings or default plugin settings
        final String jsonStr = settings_.getString(SETTINGS_KEY, SETTINGS_NOT_FOUND);
        if (jsonStr.equals(SETTINGS_NOT_FOUND)) {
            model_.studio().logs().logDebugMessage("settings not found, using default plugin settings.");
        } else {
            model_.pluginSettings(PluginSettings.fromJson(jsonStr));
            model_.studio().logs().logDebugMessage("loaded PluginSettings from " + SETTINGS_KEY + ": "
                    + model_.pluginSettings().toPrettyJson());
        }
    }

    /**
     * Save user settings.
     */
    public void save() {
        // make settings current before saving
        model_.acquisitions().updateAcquisitionSettings();

        // settings key based on geometry type
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();
        final String key = SETTINGS_PREFIX_KEY +
                geometryType.toString().toUpperCase();

        // save acquisition settings
        settings_.putString(key, model_.acquisitions().settings().toJson());
        model_.studio().logs().logDebugMessage("saved JSON to " + key + ": "
                + model_.acquisitions().settings().toPrettyJson());

        // save plugin settings
        settings_.putString(SETTINGS_KEY, model_.pluginSettings().toJson());
        model_.studio().logs().logDebugMessage("saved PluginSettings to " + SETTINGS_KEY + ": "
                + model_.pluginSettings().toPrettyJson());
    }

    // TODO: this can add new keys but doesn't delete renamed keys
    /**
     * Returns the {@code JSONObject} after checking if it matches the schema of the
     * default acquisition settings object. If it does not, then any new settings
     * found will be merged into the loaded settings as the default value.
     *
     * @param loadedSettings the settings loaded as a JSON String
     * @return the settings object or null if an error occurred
     */
    private Optional<JSONObject> validateUserSettings(final String loadedSettings) {
        // create default settings from builder
        final String defaultSettings =
                new DefaultAcquisitionSettingsSCAPE.Builder().build().toJson();
        // validate json strings and count the number of keys
        int numLoadedKeys;
        int numDefaultKeys;
        JSONObject loadedJson;
        JSONObject defaultJson;
        try {
            loadedJson = new JSONObject(loadedSettings);
            defaultJson = new JSONObject(defaultSettings);
            numLoadedKeys = countKeysJson(loadedJson);
            numDefaultKeys = countKeysJson(defaultJson);
        } catch (JSONException e) {
            model_.studio().logs().showError("could not validate the JSON data.");
            return Optional.empty();
        }
        // different number of keys => merge loaded settings with default settings
        if (numLoadedKeys != numDefaultKeys) {
            try {
                mergeSettingsJson(defaultJson, loadedJson);
            } catch (JSONException e) {
                model_.studio().logs().showError("could not merge new default settings into loaded settings.");
                return Optional.empty();
            }
        }
        return Optional.of(loadedJson);
    }

    // Overloaded method to give mergeSettingsJson a default parameter.
    private void mergeSettingsJson(JSONObject defaultJson, JSONObject loadedJson) throws JSONException {
        mergeSettingsJson(defaultJson, loadedJson, 0);
    }

    private void mergeSettingsJson(
            JSONObject defaultJson, JSONObject loadedJson, final int level) throws JSONException {

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
                model_.studio().logs().logMessage(
                        "UserSettings: Added key \"" + key + "\" to the loaded settings.");
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

    /**
     * Return the number of keys in the {@code JSONObject} object and
     * all internal {@code JSONObject} objects.
     *
     * @param obj the root {@code JSONObject} object
     * @return the number of keys in all objects
     * @throws JSONException error getting value from key from object
     */
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
