package org.micromanager.lightsheetmanager.model.utils;

import mmcorej.org.json.JSONException;
import mmcorej.org.json.JSONObject;

import java.util.Iterator;
import java.util.function.Consumer;

public final class JsonUtils {

    /** This class should not be instantiated. */
    private JsonUtils() {
        throw new AssertionError("Utility class; do not instantiate.");
    }

    /**
     * Adds each key of the defaults that the loaded object lacks, with its default value. Nested
     * objects are completed the same way, and a loaded value that is not an object is replaced by
     * the default.
     *
     * @param defaults the object holding every key, with its default value
     * @param loaded the object to complete, changed in place
     * @param maxDepth the deepest level of nested objects to complete
     * @param log receives one line for each key added or replaced, and one if the nesting goes past
     *            maxDepth
     * @throws JSONException if a value cannot be read or written
     */
    public static void addMissingKeys(final JSONObject defaults, final JSONObject loaded,
            final int maxDepth, final Consumer<String> log) throws JSONException {
        addMissingKeys(defaults, loaded, maxDepth, log, 0);
    }

    private static void addMissingKeys(final JSONObject defaults, final JSONObject loaded,
            final int maxDepth, final Consumer<String> log, final int level) throws JSONException {

        // bail out if the data is nested too deep
        if (level > maxDepth) {
            log.accept("recursion too deep, increase max level.");
            return; // early exit => recursion too deep
        }

        // for every key in the defaults, check to make sure the loaded object has that key
        final Iterator<String> keys = defaults.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object value = defaults.get(key);
            // if the loaded object is missing the key then add it
            if (!loaded.has(key)) {
                loaded.put(key, value);
                log.accept("Added key \"" + key + "\" to the loaded settings.");
            }
            // recursively call on sub-objects of type JSONObject
            if (value instanceof JSONObject) {
                final JSONObject subDefaults = (JSONObject) value;
                final Object loadedValue = loaded.get(key);
                if (loadedValue instanceof JSONObject) {
                    addMissingKeys(subDefaults, (JSONObject) loadedValue, maxDepth, log, level + 1);
                } else {
                    // a value that is not an object, null included, cannot be completed
                    loaded.put(key, subDefaults);
                    log.accept("Replaced key \"" + key
                            + "\" with its default, the loaded value was not an object.");
                }
            }
        }
    }

}
