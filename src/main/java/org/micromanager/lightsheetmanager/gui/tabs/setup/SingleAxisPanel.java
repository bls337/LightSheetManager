package org.micromanager.lightsheetmanager.gui.tabs.setup;


import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.devices.vendor.SingleAxis;

import javax.swing.JLabel;
import java.util.Objects;

/**
 * A SCAPE panel
 * This is only ever build in SCAPE mode...
 */
public class SingleAxisPanel extends Panel {


    private ComboBox cbxPattern_;
    private Spinner spnAmplitude_;
    private Spinner spnPeriod_;

    private final LightSheetManagerModel model_;

    public SingleAxisPanel(final LightSheetManagerModel model) {
        super("Single Axis Y");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {

        final ASIScanner scanner = model_.devices()
                .getDevice("IllumSlice");

        final JLabel lblPattern = new JLabel("Pattern:");
        final JLabel lblAmplitude = new JLabel("Amplitude");
        final JLabel lblPeriod = new JLabel("Period");


        final String[] patterns = SingleAxis.Pattern.toArray();

        String pattern = patterns[0];
        double amplitudeY = 0;
        int periodY = 0;
        if (scanner != null) {
            pattern = scanner.sa().getPatternY().toString();
            amplitudeY = scanner.sa().getAmplitudeY();
            periodY = scanner.sa().getPeriodY();
        }

        cbxPattern_ = new ComboBox(patterns, pattern, 100, 24);
        spnAmplitude_ = Spinner.createDoubleSpinner(amplitudeY, 0.0, 100.0, 1.0);
        spnPeriod_ = Spinner.createIntegerSpinner(periodY, 0, 100, 1);

        add(lblPattern, "");
        add(cbxPattern_, "wrap");
        add(lblAmplitude, "");
        add(spnAmplitude_, "wrap");
        add(lblPeriod, "");
        add(spnPeriod_, "");
    }

    private void createEventHandlers() {
            final ASIScanner galvo = model_.devices()
                    .getDevice("IllumSlice");

        cbxPattern_.registerListener(e -> {
            galvo.sa().setPatternY(SingleAxis.Pattern.fromString(cbxPattern_.getSelected()));
        });

        spnAmplitude_.registerListener(e -> {
            galvo.sa().setAmplitudeY(spnAmplitude_.getFloat());
        });

        spnPeriod_.registerListener(e -> {
            galvo.sa().setPeriodY(spnPeriod_.getInt());
        });

    }

}
