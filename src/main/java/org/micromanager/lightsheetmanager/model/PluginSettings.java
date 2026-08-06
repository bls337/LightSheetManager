package org.micromanager.lightsheetmanager.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Plugin settings that are not a part of the acquisition settings.
 */
public class PluginSettings {

    private boolean isPollingPositions = true;

    // volatile: written from the EDT (Settings tab) and read from the acquisition thread
    private volatile boolean acquireFailQuietly = false;

    private final JoystickData joystick = new JoystickData();

    private final XYZGrid xyzGrid = new XYZGrid();

    public JoystickData joystickPanel() {
        return joystick;
    }

    public XYZGrid xyzGrid() {
        return xyzGrid;
    }

    public void setPollingPositions(final boolean state) {
        isPollingPositions = state;
    }

    public boolean isPollingPositions() {
        return isPollingPositions;
    }

    public void setAcquireFailQuietly(final boolean state) {
        acquireFailQuietly = state;
    }

    public boolean isAcquireFailQuietly() {
        return acquireFailQuietly;
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

        private String joystick;
        private String leftWheel;
        private String rightWheel;

        JoystickData() {
            joystick = "None";
            leftWheel = "None";
            rightWheel = "None";
        }

        public String joystick() {
            return joystick;
        }

        public String leftWheel() {
            return leftWheel;
        }

        public String rightWheel() {
            return rightWheel;
        }

        public void joystick(final String joystick) {
            this.joystick = joystick;
        }

        public void leftWheel(final String leftWheel) {
            this.leftWheel = leftWheel;
        }

        public void rightWheel(final String rightWheel) {
            this.rightWheel = rightWheel;
        }
    }
}
