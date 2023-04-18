package org.micromanager.lightsheetmanager.model;

import mmcorej.org.json.JSONException;
import mmcorej.org.json.JSONObject;
import org.micromanager.Studio;
import org.micromanager.UserProfile;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.propertymap.MutablePropertyMapView;

import java.util.Iterator;
import java.util.Objects;

public class UserSettings {

    private final String userName;
    private final UserProfile profile;
    private final MutablePropertyMapView settings;

    // JSON key for LightSheetDeviceManager selected device save settings
    private static final String DEVICES_KEY = "Devices";
    private static final String SETTINGS_NOT_FOUND = "Settings Not Found";

    private final LightSheetManagerModel model_;

    public UserSettings(final Studio studio, final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        Objects.requireNonNull(studio);
        // setup user profile
        profile = studio.getUserProfile();
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
        String json = settings.getString(DEVICES_KEY, SETTINGS_NOT_FOUND);
        try {
            JSONObject jsonObj = new JSONObject(json);
            //jsonObj.remove("volumeSettings_");
            JSONObject volumeSettings = jsonObj.getJSONObject("volumeSettings_");
            volumeSettings.remove("numViews_");
            jsonObj.put("volumeSettings_", volumeSettings);
            json = jsonObj.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        System.out.println("loaded json: " + json);
        // use default settings if settings data not found
        if (!json.equals(SETTINGS_NOT_FOUND)) {
            // validate user settings and create settings object
            JSONObject loadedJson = validateUserSettings(json);
            if (loadedJson != null) {
                DefaultAcquisitionSettingsDISPIM acqSettings =
                        DefaultAcquisitionSettingsDISPIM.fromJson(loadedJson.toString());
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
                model_.acquisitions().getAcquisitionSettingsBuilder().build());
        // save in user settings
        settings.putString(DEVICES_KEY,
                model_.acquisitions().getAcquisitionSettings().toJson());
        System.out.println("saved json: " + model_.acquisitions().getAcquisitionSettings().toPrettyJson());
    }

    /**
     * Returns the JSONObject after checking if it matches the schema of the
     * default acquisition settings object. If it does not, then any new settings
     * found in will be merged into the loaded settings as the default value.
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

    private void mergeSettingsJson(JSONObject defaultJson, JSONObject loadedJson) throws JSONException {
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
                    mergeSettingsJson(subDefaultJson, subLoadedJson);
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
