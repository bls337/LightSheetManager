package org.micromanager.lightsheetmanager.api.data;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: use hashmap to get different defaults for different microscope geometries? CameraMode.EDGE vs CameraMode.OVERLAP

/**
 * Camera trigger modes.
 */
public enum CameraMode {
    INTERNAL("Internal"),
    EDGE("Edge Trigger"),
    OVERLAP("Overlap/Synchronous"),
    LEVEL("Level Trigger"),
    PSEUDO_OVERLAP("Pseudo Overlap"),
    VIRTUAL_SLIT("Virtual Slit");

    private final String text_;

    private static final Map<String, CameraMode> STRING_TO_ENUM =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(Object::toString, e -> e));

    CameraMode(final String text) {
        text_ = text;
    }

    public static CameraMode fromString(final String symbol) {
        return STRING_TO_ENUM.getOrDefault(symbol, CameraMode.EDGE);
    }

    /**
     * Does camera support overlap/synchronous mode?
     *
     * @param camLib the camera device adapter
     * @return {@code true} if the camera supports the mode
     */
    public static boolean hasOverlapTrigger(final CameraLibrary camLib) {
        return camLib == CameraLibrary.HAMAMATSU ||
                camLib == CameraLibrary.ANDORSDK3 ||
                camLib == CameraLibrary.DEMOCAMERA;
    }

    /**
     * Does camera support pseudo overlap/synchronous mode?
     * Both PCO and Photometrics 95B do (PCO panda seems to be exception but can't easily account for that)
     *
     * @param camLib the camera device adapter
     * @return {@code true} if the camera supports the mode
     */
    private static boolean hasPseudoOverlapTrigger(final CameraLibrary camLib) {
        return camLib == CameraLibrary.PCOCAMERA || camLib == CameraLibrary.PVCAM;
    }

    private static boolean hasLevelTrigger(final CameraLibrary camLib) {
        return camLib == CameraLibrary.HAMAMATSU ||
                camLib == CameraLibrary.ANDORSDK3 ||
                camLib == CameraLibrary.PCOCAMERA;
    }

    /**
     * Does camera support light sheet mode?
     *
     * @param camLib the camera device adapter
     * @return {@code true} if the camera supports the mode
     */
    private static boolean hasLightSheetTrigger(final CameraLibrary camLib) {
        return camLib == CameraLibrary.HAMAMATSU ||
                camLib == CameraLibrary.ANDORSDK3 ||
                camLib == CameraLibrary.PVCAM || // not sure about this
                camLib == CameraLibrary.DEMOCAMERA;  // for testing only
    }

    public static boolean isCameraValid(final CameraLibrary camLib) {
        return camLib != CameraLibrary.UNKNOWN;
    }

    public static CameraMode[] modesByDeviceLibrary(final CameraLibrary cameraLibrary) {
        ArrayList<CameraMode> modes = new ArrayList<>();
        if (isCameraValid(cameraLibrary)) {
            modes.add(CameraMode.EDGE);
            if (hasLevelTrigger(cameraLibrary)) {
                modes.add(CameraMode.LEVEL);
            }
            if (hasOverlapTrigger(cameraLibrary)) {
                modes.add(CameraMode.OVERLAP);
            }
            if (hasPseudoOverlapTrigger(cameraLibrary)) {
                modes.add(CameraMode.PSEUDO_OVERLAP);
            }
            if (hasLightSheetTrigger(cameraLibrary)) {
                modes.add(CameraMode.VIRTUAL_SLIT);
            }
        }
        return modes.toArray(CameraMode[]::new);
    }

    @Override
    public String toString() {
        return text_;
    }

}
