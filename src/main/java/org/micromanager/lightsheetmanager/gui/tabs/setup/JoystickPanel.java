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

        // TODO: enum for combo values
        String[] labels = {"Imaging Piezo", "XYStage", "Imaging Slice"};

        cmbJoystick_ = new ComboBox<>(labels, "XYStage", 100, 20);
        cmbLeftWheel_ = new ComboBox<>(labels, "Imaging Piezo", 100, 20);
        cmbRightWheel_ = new ComboBox<>(labels, "Imaging Slice", 100, 20);

        add(lblJoystick, "");
        add(cmbJoystick_, "wrap");
        add(lblLeftWheel, "");
        add(cmbLeftWheel_, "wrap");
        add(lblRightWheel, "");
        add(cmbRightWheel_, "");
    }

    private void createEventHandlers() {

        cmbJoystick_.registerListener(e -> {

        });

        cmbLeftWheel_.registerListener(e -> {

        });

        cmbRightWheel_.registerListener(e -> {

        });

    }
}
