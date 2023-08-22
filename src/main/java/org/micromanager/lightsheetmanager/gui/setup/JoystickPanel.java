package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JLabel;
import java.util.Objects;

// TODO: find a better way to organize vendor specific panels/ui elements
public class JoystickPanel extends Panel {

    private ComboBox cmbJoystick_;
    private ComboBox cmbLeftWheel_;
    private ComboBox cmbRightWheel_;

    private LightSheetManagerModel model_;

    public JoystickPanel(final LightSheetManagerModel model) {
        super("Joystick");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final JLabel lblJoystick = new JLabel("JoysticK:");
        final JLabel lblLeftWheel = new JLabel("Left Wheel:");
        final JLabel lblRightWheel = new JLabel("Right Wheel:");

        setMigLayout(
                "",
                "[]5[]",
                "[]5[]"
        );

        // TODO: enum for combo values
        String[] labels = {"Imaging Piezo", "XYStage", "Imaging Slice"};

        ComboBox.setDefaultSize(100, 20);
        cmbJoystick_ = new ComboBox(labels, "XYStage");
        cmbLeftWheel_ = new ComboBox(labels, "Imaging Piezo");
        cmbRightWheel_ = new ComboBox(labels, "Imaging Slice");

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
