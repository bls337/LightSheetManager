package org.micromanager.lightsheetmanager.api.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: use hashmap to get different defaults for different microscope geometries? CameraMode.EDGE vs CameraMode.OVERLAP

/**
 * Camera trigger modes.
 *
 * <p>All modes should use hardware triggering.
 */
public enum CameraMode {
    INTERNAL("Internal"),
    EDGE("Edge Trigger"),
    OVERLAP("Overlap/Synchronous"),
    LEVEL("Level Trigger"),
    PSEUDO_OVERLAP("Pseudo Overlap"),
    VIRTUAL_SLIT("Virtual Slit");

    private final String text_;

    private static final Map<String, CameraMode> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    CameraMode(final String text) {
        text_ = text;
    }

    public static CameraMode fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, CameraMode.EDGE);
    }

    /**
     * @return an array of Strings containing all possible camera trigger modes.
     */
    public static String[] toArray() {
        return Arrays.stream(values())
                .map(CameraMode::toString)
                .toArray(String[]::new);
    }

    /**
     * Does camera support overlap/synchronous mode?
     *
     * @param camLib the camera device adapter
     * @return
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
     * @return
     */
    private static boolean hasPseudoOverlapTrigger(CameraLibrary camLib) {
        return camLib == CameraLibrary.PCOCAMERA || camLib == CameraLibrary.PVCAM;
    }

    private static boolean hasLevelTrigger(CameraLibrary camLib) {
        return camLib == CameraLibrary.HAMAMATSU ||
                camLib == CameraLibrary.ANDORSDK3 ||
                camLib == CameraLibrary.PCOCAMERA;
    }

    /**
     * Does camera support light sheet mode?
     *
     * @param camLib the camera device adapter
     * @return
     */
    private static boolean hasLightSheetTrigger(CameraLibrary camLib) {
        return camLib == CameraLibrary.HAMAMATSU ||
                camLib == CameraLibrary.ANDORSDK3 ||
                camLib == CameraLibrary.PVCAM || // not sure about this
                camLib == CameraLibrary.DEMOCAMERA;  // for testing only
    }

    public static boolean isCameraValid(CameraLibrary camLib) {
        return camLib != CameraLibrary.UNKNOWN;
    }

    public static String[] getAvailableModes(CameraLibrary camLib) {
        ArrayList<CameraMode> modes = new ArrayList<>();
        if (isCameraValid(camLib)) {
            modes.add(CameraMode.EDGE);
            if (hasLevelTrigger(camLib)) {
                modes.add(CameraMode.LEVEL);
            }
            if (hasOverlapTrigger(camLib)) {
                modes.add(CameraMode.OVERLAP);
            }
            if (hasPseudoOverlapTrigger(camLib)) {
                modes.add(CameraMode.PSEUDO_OVERLAP);
            }
            if (hasLightSheetTrigger(camLib)) {
                modes.add(CameraMode.VIRTUAL_SLIT);
            }
        }
        return modes.stream()
                .map(CameraMode::toString)
                .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return text_;
    }

}
