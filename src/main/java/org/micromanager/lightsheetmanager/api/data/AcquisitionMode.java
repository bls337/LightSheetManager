package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;

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

    AcquisitionMode(final String text) {
        text_ = text;
    }

    @Override
    public String toString() {
        return text_;
    }

    public static AcquisitionMode getByIndex(final int index) {
        return values()[index];
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(AcquisitionMode::toString)
                .toArray(String[]::new);
    }

    // TODO: check if stage scanning exists
    public static String[] getValidKeys() {
        final AcquisitionMode[] keys = new AcquisitionMode[] {
                NO_SCAN, STAGE_SCAN, SLICE_SCAN_ONLY, PIEZO_SCAN_ONLY
        };
        return Arrays.stream(keys)
                .map(AcquisitionMode::toString)
                .toArray(String[]::new);

    }
}
