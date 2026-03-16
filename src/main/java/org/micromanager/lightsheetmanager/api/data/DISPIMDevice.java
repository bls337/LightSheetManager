package org.micromanager.lightsheetmanager.api.data;

// TODO: error handling when out of range (side > numViews)

public class DispimDevice {

    public static final String UNDEFINED = "Undefined";
    public static final String INVERTED_CAMERA = "InvertedCamera";
    public static final String INVERTED_FOCUS = "InvertedFocus";
    public static final String INVERTED_SHUTTER = "InvertedShutter";
    public static final String SAMPLE_XY = "SampleXY";
    public static final String SAMPLE_Z = "SampleZ";
    public static final String TRIGGER_CAMERA = "TriggerCamera";
    public static final String TRIGGER_LASER = "TriggerLaser";

    public static String getImagingCamera(final int view) {
        return "Imaging" + view + "Camera";
    }

    public static String getImagingFocus(final int view) {
        return "Imaging" + view + "Focus";
    }

    public static String getIllumBeam(final int view) {
        return "Illum" + view + "Beam";
    }

    public static String getIllumSlice(final int view) {
        return "Illum" + view + "Slice";
    }
}
