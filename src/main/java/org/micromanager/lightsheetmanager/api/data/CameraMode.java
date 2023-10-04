package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Camera trigger modes.
 *
 * <p>All modes should use hardware triggering.
 */
public enum CameraMode {
    EDGE("Edge Trigger"),
    OVERLAP("Overlap/Synchronous"),
    LEVEL("Level Trigger"),
    PSEUDO_OVERLAP("Pseudo Overlap"),
    VIRTUAL_SLIT("Virtual Slit");

    private final String text;

    private static final Map<String, CameraMode> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    CameraMode(final String text) {
        this.text = text;
    }

    public static CameraMode fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, CameraMode.INTERNAL);
    }

    public static CameraMode getByIndex(final int index) {
        return values()[index];
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(CameraMode::toString)
                .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return text;
    }

}
