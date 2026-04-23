package org.micromanager.lightsheetmanager.gui.tabs.setup;

import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;

import javax.swing.JLabel;
import java.util.Objects;

// TODO: find a better way to organize vendor specific panels/ui elements
public class JoystickPanel extends Panel {

    private ComboBox<String> cmbJoystick_;
    private ComboBox<String> cmbLeftWheel_;
    private ComboBox<String> cmbRightWheel_;

    private LightSheetManager model_;

    public JoystickPanel(final LightSheetManager model) {
        super("Joystick");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
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

        String[] joystickLabels = {"None", "Scanner", "XYStage"};
        String[] wheelLabels = {"None", "Imaging Piezo", "Imaging Slice", "Light Sheet Tilt", "Sample Height"};

        cmbJoystick_ = new ComboBox<>(joystickLabels, "XYStage", 100, 24);
        cmbLeftWheel_ = new ComboBox<>(wheelLabels, "Imaging Piezo", 100, 24);
        cmbRightWheel_ = new ComboBox<>(wheelLabels, "Imaging Slice", 100, 24);

        add(lblJoystick, "");
        add(cmbJoystick_, "wrap");
        add(lblLeftWheel, "");
        add(cmbLeftWheel_, "wrap");
        add(lblRightWheel, "");
        add(cmbRightWheel_, "");
    }

    private void createEventHandlers() {

        cmbJoystick_.registerListener(() -> {

        });

        cmbLeftWheel_.registerListener(() -> {

        });

        cmbRightWheel_.registerListener(() -> {

        });

    }
}
