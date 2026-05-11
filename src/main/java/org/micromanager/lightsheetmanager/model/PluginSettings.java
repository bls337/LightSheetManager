package org.micromanager.lightsheetmanager.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Plugin settings that are not a part of the acquisition settings.
 */
public class PluginSettings {

    private boolean isPollingPositions_ = true;

    private final JoystickData joystick_ = new JoystickData();

    private final XYZGrid xyzGrid_ = new XYZGrid();

    public JoystickData joystickPanel() {
        return joystick_;
    }

    public XYZGrid xyzGrid() {
        return xyzGrid_;
    }

    public void setPollingPositions(final boolean state) {
        isPollingPositions_ = state;
    }

    public boolean isPollingPositions() {
        return isPollingPositions_;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String toPrettyJson() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static PluginSettings fromJson(final String json) {
        return new Gson().fromJson(json, PluginSettings.class);
    }

    public static class JoystickData {

        private String joystick_;
        private String leftWheel_;
        private String rightWheel_;

        JoystickData() {
            joystick_ = "None";
            leftWheel_ = "None";
            rightWheel_ = "None";
        }

        public String joystick() {
            return joystick_;
        }

        public String leftWheel() {
            return leftWheel_;
        }

        public String rightWheel() {
            return rightWheel_;
        }

        public void joystick(final String joystick) {
            joystick_ = joystick;
        }

        public void leftWheel(final String leftWheel) {
            leftWheel_ = leftWheel;
        }

        public void rightWheel(final String rightWheel) {
            rightWheel_ = rightWheel;
        }
    }
}
