package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;

// TODO: replicate getValidModeKeys from 1.4 plugin to populate acq mode combo box

/**
 * Acquisition modes for diSPIM.
 */
public enum AcquisitionModes {
    NONE("None"),

    PIEZO_SLICE_SCAN("Synchronous piezo/slice scan"),
    NO_SCAN("No Scan (Fixed Sheet)"),
    STAGE_SCAN("Stage Scan"),
    STAGE_SCAN_INTERLEAVED("Stage Scan Interleaved"),
    STAGE_SCAN_UNIDIRECTIONAL("Stage Scan Unidirectional"),
    SLICE_SCAN_ONLY("Slice scan only"),
    PIEZO_SCAN_ONLY("Piezo scan only");

    private final String text_;

    AcquisitionModes(final String text) {
        text_ = text;
    }

    @Override
    public String toString() {
        return text_;
    }

    public static AcquisitionModes getByIndex(final int index) {
        return values()[index];
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(AcquisitionModes::toString)
                .toArray(String[]::new);
    }

}
