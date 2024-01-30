package org.micromanager.lightsheetmanager.model.devices.vendor;

import org.micromanager.Studio;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASIPiezo extends ASITigerBase {

    private final SingleAxis singleAxis_;

    public ASIPiezo(final Studio studio, final String deviceName) {
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
            studio_.logs().showError("Could not move piezo!");
        }
    }

    public double getPosition() {
        try {
            return core_.getPosition(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("Could get the piezo position!");
            return 0.0;
        }
    }

    public void home() {
        try {
            core_.home(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("Could not move piezo to home!");
        }
    }

    public String getAxisLetter() {
        return getProperty(Properties.ReadOnly.AXIS_LETTER);
    }

    public double getStepSize() {
        return getPropertyFloat(Properties.ReadOnly.STEP_SIZE);
    }

    public double getTravelRange() {
        return getPropertyFloat(Properties.ReadOnly.PIEZO_TRAVEL_RANGE);
    }

    public void setAutoSleepDelay(final int minutes) {
        setPropertyInt(Properties.AUTO_SLEEP_DELAY, minutes);
    }

    public int getAutoSleepDelay() {
        return getPropertyInt(Properties.AUTO_SLEEP_DELAY);
    }

    public void setAxisPolarity(final AxisPolarity axisPolarity) {
        setProperty(Properties.AXIS_POLARITY, axisPolarity.toString());
    }

    public AxisPolarity getAxisPolarity() {
        return AxisPolarity.fromString(getProperty(Properties.AXIS_POLARITY));
    }

    public void setHomePosition(final double um) {
        setPropertyFloat(Properties.HOME_POSITION, um);
    }

    public double getHomePosition() {
        return getPropertyFloat(Properties.HOME_POSITION);
    }

    public void setLowerLimit(final double limit) {
        setPropertyFloat(Properties.LOWER_LIMIT, limit);
    }

    public double getLowerLimit() {
        return getPropertyFloat(Properties.LOWER_LIMIT);
    }

    public void setMaintainOneOvershoot(final double value) {
        setPropertyFloat(Properties.PIEZO_MAINTAIN_ONE_OVERSHOOT, value);
    }

    public int getMaintainOneOvershoot() {
        return getPropertyInt(Properties.PIEZO_MAINTAIN_ONE_OVERSHOOT);
    }

    public void setMaintainOneMaxTime(final double milliseconds) {
        setPropertyFloat(Properties.PIEZO_MAINTAIN_ONE_MAX_TIME, milliseconds);
    }

    public int getMaintainOneMaxTime() {
        return getPropertyInt(Properties.PIEZO_MAINTAIN_ONE_MAX_TIME);
    }

    public void setMotorOn(final boolean state) {
        setProperty(Properties.MOTOR_ENABLE, state ? Values.ON : Values.OFF);
    }

    public boolean isMotorOn() {
        return getProperty(Properties.MOTOR_ENABLE).equals(Values.ON);
    }

    public void setMaintainState(final MaintainState state) {
        setProperty(Properties.PIEZO_MAINTAIN_STATE, state.toString());
    }

    public MaintainState getMaintainState() {
        return MaintainState.fromString(getProperty(Properties.PIEZO_MAINTAIN_STATE));
    }

    public void setMode(final PiezoMode mode) {
        setProperty(Properties.PIEZO_MODE, mode.toString());
    }

    public PiezoMode getMode() {
        return PiezoMode.fromString(getProperty(Properties.PIEZO_MODE));
    }

    // TODO: no need for getter? check result and return boolean?
    public void runCalibration() {
        setProperty(Properties.RUN_PIEZO_CALIBRATION, Values.DO_IT);
    }

    public void setSPIMNumSlices(final int numSlices) {
        setPropertyInt(Properties.SPIM_NUM_SLICES, numSlices);
    }

    public int getSPIMNumSlices() {
        return getPropertyInt(Properties.SPIM_NUM_SLICES);
    }

    public void setSPIMState(final SPIMState state) {
        setProperty(Properties.SPIM_STATE, state.toString());
    }

    public SPIMState getSPIMState() {
        return SPIMState.fromString(getProperty(Properties.SPIM_STATE));
    }

    public void setHomeCurrentPosition() {
        setProperty(Properties.SET_HOME_CURRENT_POSITION, Values.DO_IT);
    }

    public void setUpperLimit(final double limit) {
        setPropertyFloat(Properties.UPPER_LIMIT, limit);
    }

    public double getUpperLimit() {
        return getPropertyFloat(Properties.UPPER_LIMIT);
    }

    public void setVectorMove(final double value) {
        setPropertyFloat(Properties.VECTOR_MOVE, value);
    }

    // TODO: use sequence / fast seq ( part of ring buffer?)
    public static class Properties {
        public static final String AUTO_SLEEP_DELAY = "AutoSleepDelay(min)";
        public static final String AXIS_POLARITY = "AxisPolarity";
        public static final String HOME_POSITION = "HomePosition(mm)";
        public static final String LOWER_LIMIT = "LowerLim(mm)";
        public static final String MOTOR_ENABLE = "MotorOnOff";
        public static final String PIEZO_MAINTAIN_ONE_MAX_TIME = "PiezoMaintainOneMaxTime(ms)";
        public static final String PIEZO_MAINTAIN_ONE_OVERSHOOT = "PiezoMaintainOneOvershoot(%)";
        public static final String PIEZO_MAINTAIN_STATE = "PiezoMaintainState";
        public static final String PIEZO_MODE = "PiezoMode";
        public static final String RUN_PIEZO_CALIBRATION = "RunPiezoCalibration";
        public static final String SPIM_NUM_SLICES = "SPIMNumSlices";
        public static final String SPIM_STATE = "SPIMState";
        public static final String SET_HOME_CURRENT_POSITION = "SetHomeToCurrentPosition";
        public static final String UPPER_LIMIT = "UpperLim(mm)";

        public static final String VECTOR_MOVE = "VectorMove-VE(mm/s)";

        public static class ReadOnly {
            public static final String AXIS_LETTER = "AxisLetter";
            public static final String PIEZO_TRAVEL_RANGE = "PiezoTravelRange(um)";
            public static final String STEP_SIZE = "StepSize(um)";
        }
    }

    public static class Values {
        public static final String NO = "No";
        public static final String YES = "Yes";
        public static final String ON = "On";
        public static final String OFF = "Off";

        public static final String DO_IT = "Do it";
    }

    public enum AxisPolarity {
        NEGATIVE_TOWARDS_SAMPLE("Negative towards sample"),
        POSITIVE_TOWARDS_SAMPLE("Positive towards sample");

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
            return stringToEnum.getOrDefault(symbol, AxisPolarity.NEGATIVE_TOWARDS_SAMPLE);
        }
    }

    public enum SPIMState {
        IDLE("Idle"),
        ARMED("Armed");

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

    public enum MaintainState {
        DEFAULT("0 - default"),
        OVERSHOOT("1 - overshoot algorithm");

        private final String text_;

        private static final Map<String, MaintainState> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        MaintainState(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static MaintainState fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, MaintainState.DEFAULT);
        }
    }

    public enum PiezoMode {
        INTERNAL_CLOSED_LOOP("0 - internal input closed-loop"),
        EXTERNAL_CLOSED_LOOP("1 - external input closed-loop"),
        INTERNAL_OPEN_LOOP("0 - internal input open-loop"),
        EXTERNAL_OPEN_LOOP("1 - external input open-loop");

        private final String text_;

        private static final Map<String, PiezoMode> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        PiezoMode(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static PiezoMode fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, PiezoMode.INTERNAL_CLOSED_LOOP);
        }
    }

}
