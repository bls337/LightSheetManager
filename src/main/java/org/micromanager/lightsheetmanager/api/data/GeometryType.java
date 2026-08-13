package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;

public enum GeometryType {
    UNKNOWN("Unknown", 45.0),
    DISPIM("diSPIM", 45.0),
    ISPIM("iSPIM", 45.0),
    OSPIM("oSPIM", 60.0),
    MESOSPIM("mesoSPIM", 45.0),
    SCAPE("SCAPE", 50.0);

    private final String label_;
    private final double firstViewAngle_;

    GeometryType(final String label, final double firstViewAngle) {
        label_ = label;
        firstViewAngle_ = firstViewAngle;
    }

    @Override
    public String toString() {
        return label_;
    }

    /**
     * Returns the nominal angle in degrees between the objective and the stage.
     *
     * <p>Each geometry is built to a different angle, so this is only a starting point
     * for a new install. Every build is aligned by hand, and the angle a user measures
     * on their own microscope is the one that belongs in the settings.
     *
     * @return the angle in degrees
     */
    public double defaultFirstViewAngle() {
        return firstViewAngle_;
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
