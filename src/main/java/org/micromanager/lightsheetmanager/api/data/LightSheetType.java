package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;

public enum LightSheetType {
    STATIC("Static"),
    SCANNED("Scanned");

    private final String label_;

    LightSheetType(final String label) {
        label_ = label;
    }

    @Override
    public String toString() {
        return label_;
    }

    public static LightSheetType fromString(final String propertyValue) {
        if (propertyValue == null || propertyValue.isEmpty()) {
            return STATIC;
        }
        return Arrays.stream(values())
                .filter(g -> g.label_.equalsIgnoreCase(propertyValue))
                .findFirst()
                .orElse(STATIC);
    }

}
