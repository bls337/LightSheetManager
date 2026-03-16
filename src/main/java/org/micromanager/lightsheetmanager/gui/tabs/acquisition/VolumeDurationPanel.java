package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;

import javax.swing.JLabel;
import java.util.Objects;

public class VolumeDurationPanel extends Panel {

    private Label lblSliceTimeValue_;
    private Label lblVolumeTimeValue_;
    private Label lblTotalTimeValue_;

    public VolumeDurationPanel(final LightSheetManager model) {
        super("Durations");
        Objects.requireNonNull(model)
              .acquisitions().setVolumeDurationPanel(this);
        createUserInterface();
    }

    private void createUserInterface() {
        // prevent panel from moving when values change
        setAbsoluteSize(120, 95);

        lblSliceTimeValue_ = new Label("0.0 ms");
        lblVolumeTimeValue_ = new Label("0.0 ms");
        lblTotalTimeValue_ = new Label("0.0 s");

        add(new Label("Slice:"), "");
        add(lblSliceTimeValue_, "wrap");
        add(new Label("Volume:"), "");
        add(lblVolumeTimeValue_, "wrap");
        add(new Label("Total:"), "");
        add(lblTotalTimeValue_, "");
    }

    public JLabel getSliceDurationLabel() {
        return lblSliceTimeValue_;
    }

    public JLabel getVolumeDurationLabel() {
        return lblVolumeTimeValue_;
    }

    public JLabel getTotalDurationLabel() {
        return lblTotalTimeValue_;
    }

}
