package org.micromanager.lightsheetmanager.model.devices.vendor;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.model.devices.DeviceBase;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Joystick extends DeviceBase {

    public Joystick(final Studio studio, final String deviceName) {
        super(studio, deviceName);
    }

    public void enabled(final boolean state) {
        setProperty(Properties.ENABLED, state ? Values.YES : Values.NO);
    }

    public boolean enabled() {
        return getProperty(Properties.ENABLED).equals(Values.YES);
    }

    public void input(final Input input) {
        setProperty(Properties.INPUT, input.toString());
    }

    public Input input() {
        return Input.fromString(getProperty(Properties.INPUT));
    }

    public void inputX(final Input input) {
        setProperty(Properties.INPUT_X, input.toString());
    }

    public Input inputX() {
        return Input.fromString(getProperty(Properties.INPUT_X));
    }

    public void inputY(final Input input) {
        setProperty(Properties.INPUT_Y, input.toString());
    }

    public Input inputY() {
        return Input.fromString(getProperty(Properties.INPUT_Y));
    }

    public void fastSpeed(final double speed) {
        setProperty(Properties.FAST_SPEED, String.valueOf(speed));
    }

    public double fastSpeed() {
        return Double.parseDouble(getProperty(Properties.FAST_SPEED));
    }

    public void slowSpeed(final double speed) {
        setProperty(Properties.SLOW_SPEED, String.valueOf(speed));
    }

    public double slowSpeed() {
        return Double.parseDouble(getProperty(Properties.SLOW_SPEED));
    }

    public void reverse(final boolean state) {
        setProperty(Properties.REVERSE, state ? Values.YES : Values.NO);
    }

    public boolean reverse() {
        return getProperty(Properties.REVERSE).equals(Values.YES);
    }

    public void rotate(final boolean state) {
        setProperty(Properties.ROTATE, state ? Values.YES : Values.NO);
    }

    public boolean rotate() {
        return getProperty(Properties.ROTATE).equals(Values.YES);
    }

    public static class Properties {
        public static final String ENABLED = "JoystickEnabled";
        public static final String INPUT = "JoystickInput";
        public static final String INPUT_X = "JoystickInputX";
        public static final String INPUT_Y = "JoystickInputY";
        public static final String REVERSE = "JoystickReverse";
        public static final String ROTATE = "JoystickRotate";
        public static final String FAST_SPEED = "JoystickFastSpeed";
        public static final String SLOW_SPEED = "JoystickSlowSpeed";
    }

    public static class Values {
        public static final String YES = "Yes";
        public static final String NO = "No";
    }

    public enum Input {
        NONE("0 - none"),
        JOYSTICK_X("2 - joystick X"),
        JOYSTICK_Y("3 - joystick Y"),
        RIGHT_WHEEL("22 - right wheel"),
        LEFT_WHEEL("23 - left wheel");

        private final String text_;

        private static final Map<String, Input> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        Input(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static Input fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, Input.NONE);
        }
    }
}
