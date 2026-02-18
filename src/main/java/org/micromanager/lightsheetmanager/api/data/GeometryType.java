package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;

public enum GeometryType {
    UNKNOWN("Unknown"),
    DISPIM("diSPIM"),
    ISPIM("iSPIM"),
    OSPIM("oSPIM"),
    MESOSPIM("mesoSPIM"),
    SCAPE("SCAPE");

    private final String label_;

    GeometryType(final String label) {
        label_ = label;
    }

    @Override
    public String toString() {
        return label_;
    }

    public static GeometryType fromString(final String propertyValue) {
        if (propertyValue == null || propertyValue.isEmpty()) {
            return UNKNOWN;
        }
        return Arrays.stream(values())
                .filter(g -> g.label_.equalsIgnoreCase(propertyValue))
                .findFirst()
                .orElse(UNKNOWN);
    }

}
