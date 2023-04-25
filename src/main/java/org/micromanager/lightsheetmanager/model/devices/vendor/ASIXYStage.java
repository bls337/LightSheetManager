package org.micromanager.lightsheetmanager.model.devices.vendor;

import org.micromanager.Studio;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASIXYStage extends ASITigerBase {

    public ASIXYStage(final Studio studio, final String deviceName) {
        super(studio, deviceName);
    }

    public void setRelativeXYPosition(final double x, final double y) {
        try {
            core_.setRelativeXYPosition(deviceName_, x, y);
        } catch (Exception e) {
            studio_.logs().showError("could not set relative move for " + deviceName_);
        }
    }

    public void setXYPosition(final double x, final double y) {
        try {
            core_.setXYPosition(deviceName_, x, y);
        } catch (Exception e) {
            studio_.logs().showError("could not set relative move for " + deviceName_);
        }
    }

    public Point2D.Double getXYPosition() {
        try {
            return core_.getXYStagePosition(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("could not get the position for " + deviceName_);
            return new Point2D.Double(0.0, 0.0);
        }
    }

    public float getMaxSpeedX() {
        return getPropertyFloat(Properties.MAX_SPEED_X);
    }

    public float getMaxSpeedY() {
        return getPropertyFloat(Properties.MAX_SPEED_Y);
    }

    public void setSpeedX(final float speed) {
        setPropertyFloat(Properties.SPEED_X_MM, speed);
    }

    public float getSpeedX() {
        return getPropertyFloat(Properties.SPEED_X_MM);
    }

    public void setSpeedY(final float speed) {
        setPropertyFloat(Properties.SPEED_Y_MM, speed);
    }

    public float getSpeedY() {
        return getPropertyFloat(Properties.SPEED_Y_MM);
    }

    public float getSpeedXUm() {
        return getPropertyFloat(Properties.SPEED_X_UM);
    }

    public float getSpeedYUm() {
        return getPropertyFloat(Properties.SPEED_Y_UM);
    }

    public void setAccelerationX(final float value) {
        setPropertyFloat(Properties.ACCELERATION_X, value);
    }

    public float getAccelerationX() {
        return getPropertyFloat(Properties.ACCELERATION_X);
    }

    public void setAccelerationY(final float value) {
        setPropertyFloat(Properties.ACCELERATION_Y, value);
    }

    public float getAccelerationY() {
        return getPropertyFloat(Properties.ACCELERATION_Y);
    }

    public void setScanNumLines(final int numLines) {
        setPropertyInt(Properties.SCAN_NUM_LINES, numLines);
    }

    public int getScanNumLines() {
        return getPropertyInt(Properties.SCAN_NUM_LINES);
    }

    public void setAxisPolarityX(final AxisPolarity polarity) {
        setProperty(Properties.AXIS_POLARITY_X, polarity.toString());
    }

    public AxisPolarity getAxisPolarityX() {
        return AxisPolarity.fromString(getProperty(Properties.AXIS_POLARITY_X));
    }

    public void setAxisPolarityY(final AxisPolarity polarity) {
        setProperty(Properties.AXIS_POLARITY_Y, polarity.toString());
    }

    public AxisPolarity getAxisPolarityY() {
        return AxisPolarity.fromString(getProperty(Properties.AXIS_POLARITY_Y));
    }

    public void setScanPattern(final ScanPattern pattern) {
        setProperty(Properties.SCAN_PATTERN, pattern.toString());
    }

    public ScanPattern getScanPattern() {
        return ScanPattern.fromString(getProperty(Properties.SCAN_PATTERN));
    }

    public void setFastAxisStart(final float position) {
        setPropertyFloat(Properties.SCAN_FAST_AXIS_START_POSITION, position);
    }

    public float getFastAxisStart() {
        return getPropertyFloat(Properties.SCAN_FAST_AXIS_START_POSITION);
    }

    public void setFastAxisStop(final float position) {
        setPropertyFloat(Properties.SCAN_FAST_AXIS_STOP_POSITION, position);
    }

    public float getFastAxisStop() {
        return getPropertyFloat(Properties.SCAN_FAST_AXIS_STOP_POSITION);
    }

    public void setSlowAxisStart(final float position) {
        setPropertyFloat(Properties.SCAN_SLOW_AXIS_START_POSITION, position);
    }

    public float getSlowAxisStart() {
        return getPropertyFloat(Properties.SCAN_SLOW_AXIS_START_POSITION);
    }

    public void setSlowAxisStop(final float position) {
        setPropertyFloat(Properties.SCAN_SLOW_AXIS_STOP_POSITION, position);
    }

    public float getSlowAxisStop() {
        return getPropertyFloat(Properties.SCAN_SLOW_AXIS_STOP_POSITION);
    }

    public void setScanState(final ScanState state) {
        setProperty(Properties.SCAN_STATE, state.toString());
    }

    public ScanState getScanState() {
        return ScanState.fromString(getProperty(Properties.SCAN_STATE));
    }

    public static class Properties {
        public static final String AXIS_POLARITY_X = "AxisPolarityX";
        public static final String AXIS_POLARITY_Y = "AxisPolarityY";
        public static final String ACCELERATION_X = "AccelerationX-AC(ms)";
        public static final String ACCELERATION_Y = "AccelerationY-AC(ms)";
        public static final String MAX_SPEED_X = "MotorSpeedMaximumX(mm/s)";
        public static final String MAX_SPEED_Y = "MotorSpeedMaximumY(mm/s)";
        public static final String SPEED_X_MM = "MotorSpeedX-S(mm/s)";
        public static final String SPEED_Y_MM = "MotorSpeedY-S(mm/s)";
        public static final String SPEED_X_UM ="MotorSpeedX(um/s)";
        public static final String SPEED_Y_UM ="MotorSpeedY(um/s)";

        public static final String SCAN_NUM_LINES = "ScanNumLines";
        public static final String SCAN_PATTERN = "ScanPattern";
        public static final String SCAN_STATE = "ScanState";

        public static final String SCAN_FAST_AXIS_START_POSITION = "ScanFastAxisStartPosition(mm)";
        public static final String SCAN_FAST_AXIS_STOP_POSITION = "ScanFastAxisStopPosition(mm)";
        public static final String SCAN_SLOW_AXIS_START_POSITION = "ScanSlowAxisStartPosition(mm)";
        public static final String SCAN_SLOW_AXIS_STOP_POSITION = "ScanSlowAxisStopPosition(mm)";

    }

    public static class Values {
        public static final String YES = "Yes";
        public static final String NO = "No";
    }

    public enum ScanPattern {
        RASTER("Raster"),
        SERPENTINE("Serpentine");

        private final String text_;

        private static final Map<String, ScanPattern> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        ScanPattern(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static ScanPattern fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, ScanPattern.RASTER);
        }
    }

    // TODO: check this to make sure it correct
    public enum AxisPolarity {
        NORMAL("Normal"),
        REVERSED("Reversed");

        private final String text_;

        private static final Map<String, AxisPolarity> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        AxisPolarity(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static AxisPolarity fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, AxisPolarity.NORMAL);
        }
    }

    public enum ScanState {
        IDLE("Idle"),
        RUNNING("Running");

        private final String text_;

        private static final Map<String, ScanState> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        ScanState(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static ScanState fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, ScanState.IDLE);
        }
    }

}
