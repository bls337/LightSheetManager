package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type of scoring algorithm for the general autofocus settings.
 */
public enum AutofocusType {
    NONE("None"),
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

    private static final Map<String, AutofocusType> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    AutofocusType(final String name) {
        name_ = name;
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(AutofocusType::toString)
                .toArray(String[]::new);
    }

    public static AutofocusType fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, AutofocusType.NONE);
    }

    @Override
    public String toString() {
        return name_;
    }

}