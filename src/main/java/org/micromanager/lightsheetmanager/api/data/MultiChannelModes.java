package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MultiChannelModes {
    NONE("None"),
    VOLUME("Every Volume"),
    VOLUME_HW("Every Volume (PLogic)"),
    SLICE_HW("Every Slice (PLogic)");

    private final String text_;

    private static final Map<String, MultiChannelModes> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    MultiChannelModes(final String text) {
        text_ = text;
    }

    @Override
    public String toString() {
        return text_;
    }

    public static MultiChannelModes fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, MultiChannelModes.NONE);
    }

    public static MultiChannelModes getByIndex(final int index) {
        return values()[index];
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(MultiChannelModes::toString)
                .toArray(String[]::new);
    }

}
