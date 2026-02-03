package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MultiChannelMode {
    VOLUME("Every Volume"),
    VOLUME_HW("Every Volume (PLogic)"),
    SLICE_HW("Every Slice (PLogic)");

    private final String text_;

    private static final Map<String, MultiChannelMode> STRING_TO_ENUM =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    MultiChannelMode(final String text) {
        text_ = text;
    }

    @Override
    public String toString() {
        return text_;
    }

    public static MultiChannelMode getByIndex(final int index) {
        return values()[index];
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(MultiChannelMode::toString)
                .toArray(String[]::new);
    }

    public static Optional<MultiChannelMode> fromString(final String str) {
        return Optional.ofNullable(str).map(STRING_TO_ENUM::get);
    }

}
