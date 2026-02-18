package org.micromanager.lightsheetmanager.model.utils;

// TODO: make this generic for diSPIM/SCOPE or have separate static classes
public final class GeometryUtils {

    /** This class should not be instantiated. */
    private GeometryUtils() {
        throw new AssertionError("Utility class; do not instantiate.");
    }

    /***
     * Compute how far we need to shift each image for deskew relative to Z-step size (orthogonal to image) based on user-specified angle
     * e.g. with diSPIM, angle is 45 degrees so factor is 1.0, for oSPIM the factor is tan(60 degrees) = sqrt(3), etc.
     * if pathA is false then we compute based on Path B angle (assumed to be 90 degrees minus one specified for Path A)
     * @param pathA true if using Path A
     * @return factor, e.g. 1.0 for 45 degrees, sqrt(3) for 60 degrees, etc.
     */
    public static double getStageGeometricShiftFactor(double angle, final boolean pathA) {
        //double angle = 1.0; // TODO: props_.getPropValueFloat(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_STAGESCAN_ANGLE_PATHA);
        if (angle < 1) {  // case when property not defined
            angle = 45.0;
            //angle = ASIdiSPIM.oSPIM ? 60.0 : 45.0;
        }
        if (!pathA) {
            angle = 90.0 - angle;
        }
        return Math.tan(angle/180.0*Math.PI);
    }

    /***
     * Compute fractional size when viewed from above for overview image based on user-specified angle
     * e.g. with diSPIM, angle is 45 degrees so factor is cos(45 degrees) = 1/sqrt(2), for oSPIM would be cos(60 degrees) = 0.5, etc.
     * if pathA is false then we compute based on Path B angle (assumed to be 90 degrees minus one specified for Path A)
     * @param pathA true if using Path A
     * @return factor, e.g. 1/sqrt(2) for 45 degrees, 0.5 for 60 degrees, etc.
     */
    public static double getStageTopViewCompressFactor(double angle, final boolean pathA) {
        //double angle = 1.0; // TODO: props_.getPropValueFloat(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_STAGESCAN_ANGLE_PATHA);
        if (angle < 1) {  // case when property not defined
            angle = 45.0;
            //angle = ASIdiSPIM.oSPIM ? 60.0 : 45.0;
        }
        if (!pathA) {
            angle = 90.0 - angle;
        }
        return Math.cos(angle/180.0*Math.PI);
    }

    /***
     * Compute how far we need to move the stage relative to the Z-step size (orthogonal to image) based on user-specified angle
     * e.g. with diSPIM, angle is 45 degrees so go 1/cos(45deg) = 1.41x faster, with oSPIM, angle is 60 degrees so go 1/cos(60deg) = 2x faster
     * if pathA is false then we compute based on Path B angle (assumed to be 90 degrees minus one specified for Path A)
     * @param pathA true if using Path A
     * @return factor, e.g. 1.41 for 45 degrees, 2 for 60 degrees, etc.
     */
    public static double getStageGeometricSpeedFactor(final double angle, final boolean pathA) {
        return 1/getStageTopViewCompressFactor(angle, pathA);
    }
}
