package org.micromanager.lightsheetmanager.api.data;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// To add a new AcquisitionMode:
// 1) Add a new constant to the enum.
// 2) Add the AcquisitionMode to the GeometryType in MODES_BY_GEOMETRY.

// Note: The UI display order is determined by the order in the MODES_BY_GEOMETRY lists.
// Note: Each text string must be unique to support reliable lookup in fromString().

/**
 * Acquisition modes for all microscope geometries.
 * <p>
 * Use the {@code modesByType} method to get valid modes based on {@link GeometryType}.
 */
public enum AcquisitionMode {
    NO_SCAN("No scan (fixed sheet)"),

    // Stage scan modes
    STAGE_SCAN("Stage scan"),
    STAGE_SCAN_INTERLEAVED("Stage scan interleaved"),
    STAGE_SCAN_UNIDIRECTIONAL("Stage scan unidirectional"),

    // SCAPE
    GALVO_SCAN("Galvo scan"),

    // diSPIM
    SLICE_SCAN_ONLY("Slice scan only"),
    PIEZO_SCAN_ONLY("Piezo scan only"),
    PIEZO_SLICE_SCAN("Synchronous piezo/slice scan");

    // Stage scan modes, conditionally included based on hardware availability
    private static final EnumSet<AcquisitionMode> STAGE_SCAN_MODES = EnumSet.of(
          STAGE_SCAN, STAGE_SCAN_INTERLEAVED, STAGE_SCAN_UNIDIRECTIONAL
    );

    // Maps GeometryType to a List of valid AcquisitionMode constants
    private static final Map<GeometryType, List<AcquisitionMode>> MODES_BY_GEOMETRY =
          new EnumMap<>(GeometryType.class);

    // Add valid AcquisitionModes to GeometryTypes.
    // The order of the constants determines the order in dropdown menus.
    static {
        MODES_BY_GEOMETRY.put(GeometryType.SCAPE, List.of(
              NO_SCAN, STAGE_SCAN, GALVO_SCAN
        ));

        MODES_BY_GEOMETRY.put(GeometryType.DISPIM, List.of(
              NO_SCAN, STAGE_SCAN, STAGE_SCAN_INTERLEAVED, STAGE_SCAN_UNIDIRECTIONAL,
              SLICE_SCAN_ONLY, PIEZO_SCAN_ONLY, PIEZO_SLICE_SCAN
        ));

        // Example: Add future geometry types here
        // MODES_BY_GEOMETRY.put(GeometryType.MESOSPIM, List.of(...));
    }

    // Display text used in the UI
    private final String text_;

    AcquisitionMode(final String text) {
        text_ = text;
    }

    @Override
    public String toString() {
        return text_;
    }

    /**
     * Returns {@code true} if the {@link AcquisitionMode} is a stage scan mode.
     *
     * @return {@code true} if the mode is stage scan mode
     */
    public boolean isStageScanMode() {
        return STAGE_SCAN_MODES.contains(this);
    }

    /**
     * Returns the available acquisition modes supported by a specific microscope geometry.
     * <p>
     * This list is filtered based on hardware capabilities: if {@code hasStageScanning}
     * is {@code false}, all stage-scan related modes are excluded.
     *
     * @param geometryType the {@link GeometryType} to query; if {@code null}, an empty list is returned
     * @param hasStageScanning {@code true} if stage scan hardware is available
     * @return an array of {@link AcquisitionMode} constants
     */
    public static AcquisitionMode[] modesByType(final GeometryType geometryType, final boolean hasStageScanning) {
        return Optional.ofNullable(geometryType)
                .map(MODES_BY_GEOMETRY::get) // returns null if geometryType is not in map
                .orElse(Collections.emptyList())
                .stream()
                .filter(mode -> hasStageScanning || !mode.isStageScanMode())
                .toArray(AcquisitionMode[]::new);
    }

}
