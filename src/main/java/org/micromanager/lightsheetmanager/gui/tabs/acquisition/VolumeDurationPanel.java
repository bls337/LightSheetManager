package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;

import javax.swing.JLabel;
import java.util.Objects;

public class VolumeDurationPanel extends Panel {

    private Label lblSliceTime_;
    private Label lblVolumeTime_;
    private Label lblTotalTime_;

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
        lblSliceTime_ = new Label("Slice:");
        lblVolumeTime_ = new Label("Volume:");
        lblTotalTime_ = new Label("Total:");

        lblSliceTimeValue_ = new Label("0.000 ms");
        lblVolumeTimeValue_ = new Label("0.000 ms");
        lblTotalTimeValue_ = new Label("0.000 s");

        add(lblSliceTime_, "");
        add(lblSliceTimeValue_, "wrap");
        add(lblVolumeTime_, "");
        add(lblVolumeTimeValue_, "wrap");
        add(lblTotalTime_, "");
        add(lblTotalTimeValue_, "");
    }

    public void setDurationText(final String sliceTime, final String volumeTime, final String totalTime) {
        lblSliceTimeValue_.setText(sliceTime);
        lblVolumeTimeValue_.setText(volumeTime);
        lblTotalTimeValue_.setText(totalTime);
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
