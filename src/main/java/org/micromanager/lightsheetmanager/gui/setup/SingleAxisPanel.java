package org.micromanager.lightsheetmanager.gui.setup;


import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.devices.vendor.SingleAxis;

import javax.swing.JLabel;
import java.util.Objects;

/**
 * A SCAPE panel
 */
public class SingleAxisPanel extends Panel {


    private ComboBox cbxPattern_;
    private Spinner spnAmplitude_;
    private Spinner spnPeriod_;

    private final LightSheetManagerModel model_;

    public SingleAxisPanel(final LightSheetManagerModel model) {
        super("SingleAxisY");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final JLabel lblPattern = new JLabel("Pattern:");
        final JLabel lblAmplitude = new JLabel("Amplitude");
        final JLabel lblPeriod = new JLabel("Period");

        final String[] patterns = SingleAxis.Pattern.toArray();
        cbxPattern_ = new ComboBox(patterns, patterns[0], 100, 24);

        spnAmplitude_ = Spinner.createDoubleSpinner(0.0, 1.0, 100.0, 1.0);
        spnPeriod_ = Spinner.createDoubleSpinner(0.0, 1.0, 100.0, 1.0);

        add(lblPattern, "");
        add(cbxPattern_, "wrap");
        add(lblAmplitude, "");
        add(spnAmplitude_, "wrap");
        add(lblPeriod, "");
        add(spnPeriod_, "");
    }

    private void createEventHandlers() {

        cbxPattern_.registerListener(e -> {

        });

        spnAmplitude_.registerListener(e -> {

        });

        spnPeriod_.registerListener(e -> {

        });

    }

}
