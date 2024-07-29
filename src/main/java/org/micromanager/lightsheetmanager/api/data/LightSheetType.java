package org.micromanager.lightsheetmanager.api.data;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum LightSheetType {
    UNKNOWN("Unknown"),
    STATIC("Static"),
    SCANNED("Scanned");

    private static final Map<String, LightSheetType> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    private final String label_;

    LightSheetType(final String label) {
        label_ = label;
    }

    @Override
    public String toString() {
        return label_;
    }

    public static LightSheetType fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, LightSheetType.UNKNOWN);
    }
}
