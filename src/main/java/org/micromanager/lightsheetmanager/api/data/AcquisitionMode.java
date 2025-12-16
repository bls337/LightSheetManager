package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: account for different naming on each geometry type (SLICE_SCAN_ONLY)

/**
 * Acquisition modes for all microscope geometry types.
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

    // Maps GeometryType to a set of valid AcquisitionModes
    private static final Map<GeometryType, List<AcquisitionMode>> MODES_BY_GEOMETRY_TYPE = Map.of(
          GeometryType.SCAPE, List.of(NO_SCAN, STAGE_SCAN, SLICE_SCAN_ONLY),
          GeometryType.DISPIM, List.of(NO_SCAN, STAGE_SCAN, SLICE_SCAN_ONLY) // TODO: add all valid modes
    );

    private static final List<AcquisitionMode> STAGE_SCAN_MODES = List.of(
          STAGE_SCAN, STAGE_SCAN_INTERLEAVED, STAGE_SCAN_UNIDIRECTIONAL
    );

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
    public static String[] getValidKeys(final GeometryType geometry, final boolean isStageScanning) {
        final List<AcquisitionMode> baseModes = MODES_BY_GEOMETRY_TYPE.getOrDefault(geometry, List.of());
        return baseModes.stream()
                .filter(mode -> isStageScanning || !STAGE_SCAN_MODES.contains(mode))
                .map(AcquisitionMode::toString)
                .toArray(String[]::new);
    }
}
