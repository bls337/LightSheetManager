package org.micromanager.lightsheetmanager.gui.tabs.setup;

import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPiezo;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIXYStage;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIZStage;
import org.micromanager.lightsheetmanager.model.devices.vendor.Joystick;

import javax.swing.JLabel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Control an ASI joystick, left wheel, and right wheel.
 */
public class JoystickPanel extends Panel {

    private ComboBox<String> cmbJoystick_;
    private ComboBox<String> cmbLeftWheel_;
    private ComboBox<String> cmbRightWheel_;

    private String previousJoystick_;
    private String previousLeftWheel_;
    private String previousRightWheel_;

    private Map<String, Runnable> methods_;

    private final LightSheetManager model_;

    public JoystickPanel(final LightSheetManager model) {
        super("Joystick");
        model_ = Objects.requireNonNull(model);
        if (model_.devices().isUsingPLogic()) {
            createMap();
        }
        createUserInterface();
        createEventHandlers();
        if (model_.devices().isUsingPLogic()) {
            initHardware();
        }
    }

    private void createMap() {
        final ASIXYStage xyStage = model_.devices().device("SampleXY");
        final ASIZStage zStage = model_.devices().device("SampleZ");
        final ASIZStage stage = model_.devices().device("IllumAngle");
        final ASIScanner scanner = model_.devices().device("IllumSlice");
        final ASIPiezo piezo = model_.devices().device("ImagingFocus");

        // set to the default values
        previousJoystick_ = model_.pluginSettings().joystickPanel().joystick();
        previousLeftWheel_ = model_.pluginSettings().joystickPanel().leftWheel();
        previousRightWheel_ = model_.pluginSettings().joystickPanel().rightWheel();

        methods_ = new HashMap<>();

        // joystick keys
        methods_.put("None", () -> { /* do nothing */ });
        methods_.put("Scanner", () -> {
            scanner.js().inputX(Joystick.Input.NONE);
            scanner.js().inputY(Joystick.Input.NONE);
        });
        methods_.put("XYStage", () -> xyStage.js().enabled(false));

        // wheel keys
        methods_.put("Imaging Piezo", () -> piezo.js().input(Joystick.Input.NONE));
        methods_.put("Imaging Slice", () -> {
            scanner.js().inputX(Joystick.Input.NONE);
            scanner.js().inputY(Joystick.Input.NONE);
        });
        methods_.put("Light Sheet Tilt", () -> stage.js().input(Joystick.Input.NONE));
        methods_.put("Sample Height", () -> zStage.js().input(Joystick.Input.NONE));
    }

    private void initHardware() {
        setJoystick(cmbJoystick_.getSelected());
        setWheel(cmbLeftWheel_.getSelected(), Joystick.Input.LEFT_WHEEL);
        setWheel(cmbRightWheel_.getSelected(), Joystick.Input.RIGHT_WHEEL);
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

        final String[] joystickLabels = {
                "None",
                "Scanner",
                "XYStage"
        };

        final String[] wheelLabels = {
                "None",
                "Imaging Piezo",
                "Imaging Slice",
                "Light Sheet Tilt",
                "Sample Height"
        };

        cmbJoystick_ = new ComboBox<>(joystickLabels, previousJoystick_, 100, 24);
        cmbLeftWheel_ = new ComboBox<>(wheelLabels, previousLeftWheel_, 100, 24);
        cmbRightWheel_ = new ComboBox<>(wheelLabels, previousRightWheel_, 100, 24);

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
            model_.pluginSettings().joystickPanel().joystick(selected);
            methods_.get(previousJoystick_).run(); // disable the previous device
            setJoystick(selected);
            previousJoystick_ = selected; // track the previous device to disable later
        });

        // select left wheel input
        cmbLeftWheel_.registerListener(() -> {
            final String selected = cmbLeftWheel_.getSelected();
            model_.pluginSettings().joystickPanel().leftWheel(selected);
            methods_.get(previousLeftWheel_).run(); // disable the previous device
            setWheel(selected, Joystick.Input.LEFT_WHEEL);
            previousLeftWheel_ = selected; // track the previous device to disable later
        });

        // select right wheel input
        cmbRightWheel_.registerListener(() -> {
            final String selected = cmbRightWheel_.getSelected();
            model_.pluginSettings().joystickPanel().rightWheel(selected);
            methods_.get(previousRightWheel_).run(); // disable the previous device
            setWheel(selected, Joystick.Input.RIGHT_WHEEL);
            previousRightWheel_ = selected; // track the previous device to disable later
        });
    }

    private void setJoystick(final String selected) {
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
    }

    private void setWheel(final String selected, final Joystick.Input target) {
        switch (selected) {
            case "Imaging Piezo":
                final ASIPiezo piezo  = model_.devices().device("ImagingFocus");
                piezo.js().input(target);
                break;
            case "Imaging Slice":
                final ASIScanner scanner = model_.devices().device("IllumSlice");
                scanner.js().inputY(target);
                break;
            case "Light Sheet Tilt":
                final ASIZStage stage = model_.devices().device("IllumAngle");
                stage.js().input(target);
                break;
            case "Sample Height":
                final ASIZStage zStage = model_.devices().device("SampleZ");
                zStage.js().input(target);
                break;
            default:
                break;
        }
    }

}
