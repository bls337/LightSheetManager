package org.micromanager.lightsheetmanager.gui.tabs.setup;

import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIXYStage;
import org.micromanager.lightsheetmanager.model.devices.vendor.Joystick;

import javax.swing.JLabel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Control an ASI joystick.
 */
public class JoystickPanel extends Panel {

    private ComboBox<String> cmbJoystick_;
    private ComboBox<String> cmbLeftWheel_;
    private ComboBox<String> cmbRightWheel_;

    private String previousDevice_;
    private Map<String, Runnable> methods_;

    private final LightSheetManager model_;

    public JoystickPanel(final LightSheetManager model) {
        super("Joystick");
        model_ = Objects.requireNonNull(model);
        createMap();
        createUserInterface();
        createEventHandlers();
    }

    private void createMap() {
        final ASIXYStage xyStage = model_.devices().device("SampleXY");
        final ASIScanner scanner = model_.devices().device("IllumSlice");

        previousDevice_ = "None"; // set to the default value
        methods_ = new HashMap<>();

        // create map, these methods are called when switching js input
        methods_.put("None", () -> {
            // do nothing
        });
        methods_.put("Scanner", () -> {
            scanner.js().inputX(Joystick.Input.NONE);
            scanner.js().inputY(Joystick.Input.NONE);
        });
        methods_.put("XYStage", () -> xyStage.js().enabled(false));
    }

    private void createUserInterface() {
        final JLabel lblJoystick = new JLabel("Joystick:");
        final JLabel lblLeftWheel = new JLabel("Left Wheel:");
        final JLabel lblRightWheel = new JLabel("Right Wheel:");

        setMigLayout(
                "",
                "[]5[]",
                "[]5[]"
        );

        final String[] joystickLabels = {"None", "Scanner", "XYStage"};
        final String[] wheelLabels = {"None", "Imaging Piezo", "Imaging Slice", "Light Sheet Tilt", "Sample Height"};

        cmbJoystick_ = new ComboBox<>(joystickLabels, "None", 100, 24);
        cmbLeftWheel_ = new ComboBox<>(wheelLabels, "None", 100, 24);
        cmbRightWheel_ = new ComboBox<>(wheelLabels, "None", 100, 24);

        add(lblJoystick, "");
        add(cmbJoystick_, "wrap");
        add(lblLeftWheel, "");
        add(cmbLeftWheel_, "wrap");
        add(lblRightWheel, "");
        add(cmbRightWheel_, "");
    }

    private void createEventHandlers() {

        // select joystick input
        cmbJoystick_.registerListener(() -> {
            final String selected = cmbJoystick_.getSelected();
            // disable the previous device
            methods_.get(previousDevice_).run();
            // enable the selected device
            switch (selected) {
                case "None":
                    return; // early exit => do nothing
                case "Scanner":
                    final ASIScanner scanner = model_.devices().device("IllumSlice");
                    scanner.js().inputX(Joystick.Input.JOYSTICK_X);
                    scanner.js().inputY(Joystick.Input.JOYSTICK_Y);
                    break;
                case "XYStage":
                    final ASIXYStage xyStage = model_.devices().device("SampleXY");
                    xyStage.js().enabled(true);
                    break;
                default:
                    break;
            }
            // track the previous device to disable later
            previousDevice_ = selected;
        });

        // select left wheel input
        cmbLeftWheel_.registerListener(() -> {
            final String selected = cmbLeftWheel_.getSelected();
            switch (selected) {
                case "Imaging Piezo":
                    break;
                case "Imaging Slice":
                    break;
                case "Light Sheet Tilt":
                    break;
                case "Sample Height":
                    break;
                default:
                    break;
            }
        });

        // select right wheel input
        cmbRightWheel_.registerListener(() -> {
            final String selected = cmbRightWheel_.getSelected();
            switch (selected) {
                case "Imaging Piezo":
                    break;
                case "Imaging Slice":
                    break;
                case "Light Sheet Tilt":
                    break;
                case "Sample Height":
                    break;
                default:
                    break;
            }
        });

    }
}
