package org.micromanager.lightsheetmanager.api.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: account for different naming on each geometry type (SLICE_SCAN_ONLY)

/**
 * Acquisition modes for diSPIM and SCAPE.
 */
public enum AcquisitionMode {
    NONE("None"),
    PIEZO_SLICE_SCAN("Synchronous piezo/slice scan"),
    NO_SCAN("No scan (fixed sheet)"),
    STAGE_SCAN("Stage scan"),
    STAGE_SCAN_INTERLEAVED("Stage scan interleaved"),
    STAGE_SCAN_UNIDIRECTIONAL("Stage scan unidirectional"),
    //SLICE_SCAN_ONLY("Slice scan only"), // for diSPIM
    SLICE_SCAN_ONLY("Galvo scan"), // for SCAPE
    PIEZO_SCAN_ONLY("Piezo scan only");

    private final String text_;

    private static final Map<String, AcquisitionMode> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    AcquisitionMode(final String text) {
        text_ = text;
    }

    @Override
    public String toString() {
        return text_;
    }

    public static AcquisitionMode fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, AcquisitionMode.NONE);
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(AcquisitionMode::toString)
                .toArray(String[]::new);
    }

    /**
     * Returns an array of valid acquisition modes as strings.
     *
     * @param isStageScanning {@code true} if stage scanning
     * @return an array of strings
     */
    public static String[] getValidKeys(final boolean isStageScanning) {
        final ArrayList<AcquisitionMode> keys = new ArrayList<>();
        keys.add(NO_SCAN);
        if (isStageScanning) {
            keys.add(STAGE_SCAN);
        }
        keys.add(SLICE_SCAN_ONLY);
        keys.add(PIEZO_SCAN_ONLY);
        return keys.stream()
                .map(AcquisitionMode::toString)
                .toArray(String[]::new);

    }
}
