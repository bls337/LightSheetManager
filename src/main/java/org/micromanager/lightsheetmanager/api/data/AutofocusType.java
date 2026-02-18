package org.micromanager.lightsheetmanager.api.data;

/**
 * The type of scoring algorithm for the general autofocus settings.
 */
public enum AutofocusType {
    EDGES("Edges"),
    STD_DEV("StdDev"),
    MEAN("Mean"),
    NORMALIZED_VARIANCE("NormalizedVariance"),
    SHARP_EDGES("Sharp Edges"),
    REDONDO("Redondo"),
    VOLATH("Volath"),
    VOLATH5("Volath5"),
    MEDIAN_EDGES("MedianEdges"),
    FFT_BANDPASS("FFTBandpass"),
    TENENGRAD("Tenengrad");

    private final String name_;

    AutofocusType(final String name) {
        name_ = name;
    }

    @Override
    public String toString() {
        return name_;
    }

}