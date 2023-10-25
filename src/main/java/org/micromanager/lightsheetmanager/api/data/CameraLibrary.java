package org.micromanager.lightsheetmanager.api.data;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This enum contains a list of device adapters that the plugin supports.
 */
public enum CameraLibrary {
    UNKNOWN("Unknown"),
    DEMOCAMERA("DemoCamera"),
    HAMAMATSU("HamamatsuHam"),
    PCOCAMERA("PCO_Camera"),
    ANDORSDK3("AndorSDK3"),
    PVCAM("PVCAM");

    private final String text_;

    private static final Map<String, CameraLibrary> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    CameraLibrary(final String text) {
        text_ = text;
    }

    @Override
    public String toString() {
        return text_;
    }

    public static CameraLibrary fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, CameraLibrary.UNKNOWN);
    }
}