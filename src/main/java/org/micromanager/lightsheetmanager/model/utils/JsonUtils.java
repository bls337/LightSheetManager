package org.micromanager.lightsheetmanager.model.utils;

import mmcorej.org.json.JSONException;
import mmcorej.org.json.JSONObject;

import java.util.Objects;

public final class JsonUtils {

    /** This class should not be instantiated. */
    private JsonUtils() {
        throw new AssertionError("Utility class; do not instantiate.");
    }

    public static void putJson(JSONObject json, final String property, final Object value) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(property);
        Objects.requireNonNull(value);
        try {
            json.put(property, value);
        } catch (JSONException e) {
            //studio_.logs().logError(e.getMessage());
        }
    }

    public static String getString(JSONObject json, final String key) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(key);
        try {
            return json.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }
//    public JSONObject createJSONObject(final String text) {
//        try {
//            JSONObject json = new JSONObject(text);
//            return json;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
}
