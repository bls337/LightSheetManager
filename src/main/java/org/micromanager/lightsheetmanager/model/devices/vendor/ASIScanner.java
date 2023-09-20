package org.micromanager.lightsheetmanager.model.devices.vendor;

import org.micromanager.Studio;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: single axis commands and joystick

/**
 * ASI Scanner Device
 *
 */
public class ASIScanner extends ASITigerBase {

    private SingleAxis singleAxis_;

    public ASIScanner(Studio studio, final String deviceName) {
        super(studio, deviceName);
        singleAxis_ = new SingleAxis(studio, deviceName);
    }

    public SingleAxis sa() {
        return singleAxis_;
    }

    public void setPosition(final double position) {
        try {
            core_.setPosition(deviceName_, position);
        } catch (Exception e) {
            studio_.logs().showError("Could not move scanner!");
        }
    }

    public double getPosition() {
        try {
            return core_.getPosition(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("Could get the scanner position!");
            return 0.0;
        }
    }

    public void setAttenuateX(final float value) {
        setPropertyFloat(Properties.ATTENUATE_X, value);
    }

    public float getAttenuateX() {
        return getPropertyFloat(Properties.ATTENUATE_X);
    }

    public void setAttenuateY(final float value) {
        setPropertyFloat(Properties.ATTENUATE_Y, value);
    }

    public float getAttenuateY() {
        return getPropertyFloat(Properties.ATTENUATE_Y);
    }

    public String getAxisLetterX() {
        return getProperty(Properties.ReadOnly.AXIS_LETTER_X);
    }

    public String getAxisLetterY() {
        return getProperty(Properties.ReadOnly.AXIS_LETTER_Y);
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

    public void setBeamOn(final boolean state) {
        setProperty(Properties.BEAM_ENABLED, state ? Values.YES : Values.NO);
    }

    public boolean isBeamOn() {
        return getProperty(Properties.BEAM_ENABLED).equals(Values.YES);
    }

    public void setFilterFreqX(final float kHz) {
        setPropertyFloat(Properties.FILTER_FREQ_X, kHz);
    }

    public float getFilterFreqX() {
        return getPropertyFloat(Properties.FILTER_FREQ_X);
    }

    public void setFilterFreqY(final float kHz) {
        setPropertyFloat(Properties.FILTER_FREQ_Y, kHz);
    }

    public float getFilterFreqY() {
        return getPropertyFloat(Properties.FILTER_FREQ_Y);
    }

    public void setInputMode(final InputMode mode) {
        setProperty(Properties.INPUT_MODE, mode.toString());
    }

    public InputMode getInputMode() {
        return InputMode.fromString(getProperty(Properties.INPUT_MODE));
    }

    public void setLaserOutputMode(final LaserOutputMode mode) {
        setProperty(Properties.LASER_OUTPUT_MODE, mode.toString());
    }

    public LaserOutputMode getLaserOutputMode() {
        return LaserOutputMode.fromString(getProperty(Properties.LASER_OUTPUT_MODE));
    }

    public void setLaserSwitchTime(final float milliseconds) {
        setPropertyFloat(Properties.LASER_SWITCH_TIME, milliseconds);
    }

    public float getLaserSwitchTime() {
        return getPropertyFloat(Properties.LASER_SWITCH_TIME);
    }

    public void setMinDeflectionX(final float value) {
        setPropertyFloat(Properties.MIN_DEFLECTION_X, value);
    }

    public float getMinDeflectionX() {
        return getPropertyFloat(Properties.MIN_DEFLECTION_X);
    }

    public void setMinDeflectionY(final float value) {
        setPropertyFloat(Properties.MIN_DEFLECTION_Y, value);
    }

    public float getMinDeflectionY() {
        return getPropertyFloat(Properties.MIN_DEFLECTION_Y);
    }

    public void setMaxDeflectionX(final float value) {
        setPropertyFloat(Properties.MAX_DEFLECTION_X, value);
    }

    public float getMaxDeflectionX() {
        return getPropertyFloat(Properties.MAX_DEFLECTION_X);
    }

    public void setMaxDeflectionY(final float value) {
        setPropertyFloat(Properties.MAX_DEFLECTION_Y, value);
    }

    public float getMaxDeflectionY() {
        return getPropertyFloat(Properties.MAX_DEFLECTION_Y);
    }

    public void setSPIMAlternateDirections(final boolean state) {
        setProperty(Properties.SPIM_ALTERNATE_DIRECTIONS_ENABLE, state ? Values.YES : Values.NO);
    }

    public boolean isSPIMAlternateDirectionsOn() {
        return getProperty(Properties.SPIM_ALTERNATE_DIRECTIONS_ENABLE).equals(Values.YES);
    }

    public void setSPIMCameraDuration(final float milliseconds) {
        setPropertyFloat(Properties.SPIM_CAMERA_DURATION, milliseconds);
    }

    public float getSPIMCameraDuration() {
        return getPropertyFloat(Properties.SPIM_CAMERA_DURATION);
    }

    public void setSPIMDelayBeforeCamera(final float milliseconds) {
        setPropertyFloat(Properties.SPIM_DELAY_BEFORE_CAMERA, milliseconds);
    }

    public float getSPIMDelayBeforeCamera() {
        return getPropertyFloat(Properties.SPIM_DELAY_BEFORE_CAMERA);
    }

    public void setSPIMDelayBeforeLaser(final float milliseconds) {
        setPropertyFloat(Properties.SPIM_DELAY_BEFORE_LASER, milliseconds);
    }

    public float getSPIMDelayBeforeLaser() {
        return getPropertyFloat(Properties.SPIM_DELAY_BEFORE_LASER);
    }

    public void setSPIMDelayBeforeRepeat(final float milliseconds) {
        setPropertyFloat(Properties.SPIM_DELAY_BEFORE_REPEAT, milliseconds);
    }

    public float getSPIMDelayBeforeRepeat() {
        return getPropertyFloat(Properties.SPIM_DELAY_BEFORE_REPEAT);
    }

    public void setSPIMDelayBeforeScan(final float milliseconds) {
        setPropertyFloat(Properties.SPIM_DELAY_BEFORE_SCAN, milliseconds);
    }

    public float getSPIMDelayBeforeScan() {
        return getPropertyFloat(Properties.SPIM_DELAY_BEFORE_SCAN);
    }

    public void setSPIMDelayBeforeSide(final float milliseconds) {
        setPropertyFloat(Properties.SPIM_DELAY_BEFORE_SIDE, milliseconds);
    }

    public float getSPIMDelayBeforeSide() {
        return getPropertyFloat(Properties.SPIM_DELAY_BEFORE_SIDE);
    }

    public void setSPIMFirstSide(final SPIMSide view) {
        setProperty(Properties.SPIM_FIRST_SIDE, view.toString());
    }

    public SPIMSide getSPIMFirstSide() {
        return SPIMSide.fromString(getProperty(Properties.SPIM_FIRST_SIDE));
    }

    public void setSPIMInterleaveSides(final boolean state) {
        setProperty(Properties.SPIM_INTERLEAVE_SIDES_ENABLE, state ? Values.YES : Values.NO);
    }

    public boolean isSPIMInterleaveSidesOn() {
        return getProperty(Properties.SPIM_INTERLEAVE_SIDES_ENABLE).equals(Values.YES);
    }

    public void setSPIMLaserDuration(final float milliseconds) {
        setPropertyFloat(Properties.SPIM_LASER_DURATION, milliseconds);
    }

    public float getSPIMLaserDuration() {
        return getPropertyFloat(Properties.SPIM_LASER_DURATION);
    }

    public void setSPIMModeByte(final int value) {
        setPropertyInt(Properties.SPIM_MODE_BYTE, value);
    }

    public int getSPIMModeByte() {
        return getPropertyInt(Properties.SPIM_MODE_BYTE);
    }

    public void setSPIMNumRepeats(final int value) {
        setPropertyInt(Properties.SPIM_NUM_REPEATS, value);
    }

    public int getSPIMNumRepeats() {
        return getPropertyInt(Properties.SPIM_NUM_REPEATS);
    }

    public void setSPIMNumScansPerSlice(final int value) {
        setPropertyInt(Properties.SPIM_NUM_SCANS_PER_SLICE, value);
    }

    public int getSPIMNumScansPerSlice() {
        return getPropertyInt(Properties.SPIM_NUM_SCANS_PER_SLICE);
    }

    public void setSPIMNumSides(final int value) {
        setPropertyInt(Properties.SPIM_NUM_SIDES, value);
    }

    public int getSPIMNumSides() {
        return getPropertyInt(Properties.SPIM_NUM_SIDES);
    }

    public void setSPIMNumSlices(final int value) {
        setPropertyInt(Properties.SPIM_NUM_SLICES, value);
    }

    public int getSPIMNumSlices() {
        return getPropertyInt(Properties.SPIM_NUM_SLICES);
    }

    public void setSPIMNumSlicesPerPiezo(final int value) {
        setPropertyInt(Properties.SPIM_NUM_SLICES_PER_PIEZO, value);
    }

    public int getSPIMNumSlicesPerPiezo() {
        return getPropertyInt(Properties.SPIM_NUM_SLICES_PER_PIEZO);
    }

    public void setSPIMPiezoHomeDisable(final boolean state) {
        setProperty(Properties.SPIM_PIEZO_HOME_DISABLE, state ? Values.YES : Values.NO);
    }

    public boolean getSPIMPiezoHomeDisable() {
        return getProperty(Properties.SPIM_PIEZO_HOME_DISABLE).equals(Values.YES);
    }

    public void setSPIMScanDuration(final float milliseconds) {
        setPropertyFloat(Properties.SPIM_SCAN_DURATION, milliseconds);
    }

    public float getSPIMScanDuration() {
        return getPropertyFloat(Properties.SPIM_SCAN_DURATION);
    }

    public void setSPIMScannerHomeDisable(final boolean state) {
        setProperty(Properties.SPIM_SCANNER_HOME_DISABLE, state ? Values.YES : Values.NO);
    }

    public boolean getSPIMScannerHomeDisable() {
        return getProperty(Properties.SPIM_SCANNER_HOME_DISABLE).equals(Values.YES);
    }

    public void setSPIMState(final SPIMState state) {
        setProperty(Properties.SPIM_STATE, state.toString());
    }

    public SPIMState getSPIMState() {
        return SPIMState.fromString(getProperty(Properties.SPIM_STATE));
    }

    public int getTravelRange() {
        return getPropertyInt(Properties.ReadOnly.SCANNER_TRAVEL_RANGE);
    }

    // TODO: getVectorMove X and Y?

    public void setVectorMoveX(final float value) {
        setPropertyFloat(Properties.VECTOR_MOVE_X, value);
    }

    public void setVectorMoveY(final float value) {
        setPropertyFloat(Properties.VECTOR_MOVE_Y, value);
    }

    public static class Properties {
        public static final String ATTENUATE_X = "AttenuateX(0..1)";
        public static final String ATTENUATE_Y = "AttenuateY(0..1)";
        public static final String AXIS_POLARITY_X = "AxisPolarityX";
        public static final String AXIS_POLARITY_Y = "AxisPolarityY";
        public static final String BEAM_ENABLED = "BeamEnabled";
        public static final String FILTER_FREQ_X = "FilterFreqX(kHz)";
        public static final String FILTER_FREQ_Y = "FilterFreqY(kHz)";
        public static final String INPUT_MODE = "InputMode";
        public static final String LASER_OUTPUT_MODE = "LaserOutputMode";
        public static final String LASER_SWITCH_TIME = "LaserSwitchTime(ms)";
        public static final String MAX_DEFLECTION_X = "MaxDeflectionX(deg)";
        public static final String MAX_DEFLECTION_Y = "MaxDeflectionY(deg)";
        public static final String MIN_DEFLECTION_X = "MinDeflectionX(deg)";
        public static final String MIN_DEFLECTION_Y = "MinDeflectionY(deg)";
        public static final String SPIM_ALTERNATE_DIRECTIONS_ENABLE = "SPIMAlternateDirectionsEnable";
        public static final String SPIM_CAMERA_DURATION = "SPIMCameraDuration(ms)";
        public static final String SPIM_DELAY_BEFORE_CAMERA = "SPIMDelayBeforeCamera(ms)";
        public static final String SPIM_DELAY_BEFORE_LASER = "SPIMDelayBeforeLaser(ms)";
        public static final String SPIM_DELAY_BEFORE_REPEAT = "SPIMDelayBeforeRepeat(ms)";
        public static final String SPIM_DELAY_BEFORE_SCAN = "SPIMDelayBeforeScan(ms)";
        public static final String SPIM_DELAY_BEFORE_SIDE = "SPIMDelayBeforeSide(ms)";
        public static final String SPIM_FIRST_SIDE = "SPIMFirstSide";
        public static final String SPIM_INTERLEAVE_SIDES_ENABLE = "SPIMInterleaveSidesEnable";
        public static final String SPIM_LASER_DURATION = "SPIMLaserDuration(ms)";
        public static final String SPIM_MODE_BYTE = "SPIMModeByte";
        public static final String SPIM_NUM_REPEATS = "SPIMNumRepeats";
        public static final String SPIM_NUM_SCANS_PER_SLICE = "SPIMNumScansPerSlice";
        public static final String SPIM_NUM_SIDES = "SPIMNumSides";
        public static final String SPIM_NUM_SLICES = "SPIMNumSlices";
        public static final String SPIM_NUM_SLICES_PER_PIEZO = "SPIMNumSlicesPerPiezo";
        public static final String SPIM_PIEZO_HOME_DISABLE = "SPIMPiezoHomeDisable";
        public static final String SPIM_SCAN_DURATION = "SPIMScanDuration(ms)";
        public static final String SPIM_SCANNER_HOME_DISABLE = "SPIMScannerHomeDisable";
        public static final String SPIM_STATE = "SPIMState";
        public static final String VECTOR_MOVE_X = "VectorMoveX-VE(mm/s)";
        public static final String VECTOR_MOVE_Y = "VectorMoveY-VE(mm/s)";

        public static class ReadOnly {
            public static final String AXIS_LETTER_X = "AxisLetterX";
            public static final String AXIS_LETTER_Y = "AxisLetterY";
            public static final String SCANNER_TRAVEL_RANGE = "ScannerTravelRange(deg)";
        }
    }

    public static class Values {
        public static final String YES = "Yes";
        public static final String NO = "No";
    }

    public enum SPIMState {
        IDLE("Idle"),
        ARMED("Armed"),
        RUNNING("Running");

        private final String text_;

        private static final Map<String, SPIMState> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        SPIMState(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static SPIMState fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, SPIMState.IDLE);
        }
    }

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

    public enum InputMode {
        INTERNAL("internal input"),
        EXTERNAL("external input");
        private final String text_;

        private static final Map<String, InputMode> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        InputMode(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static InputMode fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, InputMode.INTERNAL);
        }
    }

    public enum LaserOutputMode {
        FAST_CIRCLES("fast circles"),
        INDIVIDUAL_SHUTTER("individual shutters"),
        SHUTTER_PLUS_SIDE("fast circles"),
        SIDE_PLUS_SIDE("side + side");
        private final String text_;

        private static final Map<String, LaserOutputMode> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        LaserOutputMode(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static LaserOutputMode fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, LaserOutputMode.SHUTTER_PLUS_SIDE);
        }
    }

    public enum SPIMSide {
        A("A"),
        B("B");
        private final String text_;

        private static final Map<String, SPIMSide> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        SPIMSide(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static SPIMSide fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, SPIMSide.A);
        }
    }
}
