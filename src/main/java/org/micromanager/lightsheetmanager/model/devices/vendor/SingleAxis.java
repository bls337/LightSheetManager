package org.micromanager.lightsheetmanager.model.devices.vendor;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.model.devices.DeviceBase;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SingleAxis extends DeviceBase {

    public SingleAxis(Studio studio, String deviceName) {
        super(studio, deviceName);
    }

    public void setModeX(final Mode mode) {
        setProperty(Properties.MODE_X, mode.toString());
    }

    public void setModeY(final Mode mode) {
        setProperty(Properties.MODE_Y, mode.toString());
    }

    public Mode getModeX() {
        return Mode.fromString(getProperty(Properties.MODE_X));
    }

    public Mode getModeY() {
        return Mode.fromString(getProperty(Properties.MODE_Y));
    }

    public void setPeriodX(final int milliseconds) {
        setPropertyInt(Properties.PERIOD_X, milliseconds);
    }

    public int getPeriodX() {
        return getPropertyInt(Properties.PERIOD_X);
    }

    public void setPeriodY(final int milliseconds) {
        setPropertyInt(Properties.PERIOD_Y, milliseconds);
    }

    public int getPeriodY() {
        return getPropertyInt(Properties.PERIOD_Y);
    }

    public void setAmplitude(final double um) {
        setPropertyFloat(Properties.AMPLITUDE, um);
    }
    public double getAmplitude() {
        return getPropertyFloat(Properties.AMPLITUDE);
    }

    public void setAmplitudeX(final double degrees) {
        setPropertyFloat(Properties.AMPLITUDE_X, degrees);
    }

    public double getAmplitudeX() {
        return getPropertyFloat(Properties.AMPLITUDE_X);
    }

    public void setAmplitudeY(final double degrees) {
        setPropertyFloat(Properties.AMPLITUDE_Y, degrees);
    }

    public double getAmplitudeY() {
        return getPropertyFloat(Properties.AMPLITUDE_Y);
    }

    public void setOffset(final double um) {
        setPropertyFloat(Properties.OFFSET, um);
    }

    public double getOffset() {
        return getPropertyFloat(Properties.OFFSET);
    }

    public void setOffsetX(final double degrees) {
        setPropertyFloat(Properties.OFFSET_X, degrees);
    }

    public double getOffsetX() {
        return getPropertyFloat(Properties.OFFSET_X);
    }

    public void setOffsetY(final double degrees) {
        setPropertyFloat(Properties.OFFSET_Y, degrees);
    }

    public double getOffsetY() {
        return getPropertyFloat(Properties.OFFSET_Y);
    }

    public void setPatternByteX(final int modeByte) {
        setPropertyInt(Properties.PATTERN_BYTE_X, modeByte);
    }

    public int getPatternByteX() {
        return getPropertyInt(Properties.PATTERN_BYTE_X);
    }

    public void setPatternByteY(final int modeByte) {
        setPropertyInt(Properties.PATTERN_BYTE_Y, modeByte);
    }

    public int getPatternByteY() {
        return getPropertyInt(Properties.PATTERN_BYTE_Y);
    }

    public void setPatternX(final Pattern pattern) {
        setProperty(Properties.PATTERN_X, pattern.toString());
    }

    public Pattern getPatternX() {
        return Pattern.fromString(getProperty(Properties.PATTERN_X));
    }

    public void setPatternY(final Pattern pattern) {
        setProperty(Properties.PATTERN_Y, pattern.toString());
    }

    public Pattern getPatternY() {
        return Pattern.fromString(getProperty(Properties.PATTERN_Y));
    }

    public static class Properties {
        public static final String MODE = "SingleAxisMode";
        public static final String AMPLITUDE = "SingleAxisAmplitude(um)";
        public static final String OFFSET = "SingleAxisOffset(um)";

        public static final String MODE_X = "SingleAxisXMode";
        public static final String MODE_Y = "SingleAxisYMode";
        public static final String PERIOD_X = "SingleAxisXPeriod(ms)";
        public static final String PERIOD_Y = "SingleAxisYPeriod(ms)";
        public static final String PATTERN_X = "SingleAxisXPattern";
        public static final String PATTERN_Y = "SingleAxisYPattern";
        public static final String PATTERN_BYTE_X = "SingleAxisXPatternByte";
        public static final String PATTERN_BYTE_Y = "SingleAxisYPatternByte";
        public static final String AMPLITUDE_X = "SingleAxisXAmplitude(deg)";
        public static final String AMPLITUDE_Y = "SingleAxisYAmplitude(deg)";
        public static final String OFFSET_X = "SingleAxisXOffset(deg)";
        public static final String OFFSET_Y = "SingleAxisYOffset(deg)";
    }

    public static class Values {

    }

    public enum Mode {
        DISABLED("0 - Disabled"),
        ENABLED("1 - Enabled"),
        ARMED_FOR_TTL("3 - Armed for TTL trigger"),
        ENABLED_AXES_SYNCED("4 - Enabled with axes synced");

        private final String text_;

        private static final Map<String, Mode> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        Mode(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static Mode fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, Mode.DISABLED);
        }
    }

    public enum Pattern {
        RAMP("0 - Ramp"),
        TRIANGLE("1 - Triangle"),
        SQUARE("2 - Square"),
        SINE("3 - Sine");

        private final String text_;

        private static final Map<String, Pattern> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        Pattern(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static Pattern fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, Pattern.RAMP);
        }

        public static String[] toArray() {
            return Arrays.stream(values())
                    .map(Pattern::toString)
                    .toArray(String[]::new);
        }
    }


    public enum ClockSource {
        EXTERNAL("external clock"),
        INTERNAL("internal 4kHz clock");

        private final String text_;

        private static final Map<String, ClockSource> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        ClockSource(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static ClockSource fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, ClockSource.INTERNAL);
        }
    }


    public enum ClockPolarity {
        NEGATIVE("negative edge"),
        POSITIVE("positive edge");

        private final String text_;

        private static final Map<String, ClockPolarity> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        ClockPolarity(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static ClockPolarity fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, ClockPolarity.NEGATIVE);
        }
    }

    public enum SingleAxisTTLPolarity {
        HIGH("active high"),
        LOW("active low");

        private final String text_;

        private static final Map<String, SingleAxisTTLPolarity> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        SingleAxisTTLPolarity(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static SingleAxisTTLPolarity fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, SingleAxisTTLPolarity.LOW);
        }
    }
}
