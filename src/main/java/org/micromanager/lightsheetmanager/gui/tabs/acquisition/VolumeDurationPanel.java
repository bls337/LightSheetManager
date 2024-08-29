package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;

import java.util.Objects;

public class VolumeDurationPanel extends Panel {

    private Label lblSliceTime_;
    private Label lblVolumeTime_;
    private Label lblTotalTime_;

    private Label lblSliceTimeValue_;
    private Label lblVolumeTimeValue_;
    private Label lblTotalTimeValue_;

    private final LightSheetManager model_;

    public VolumeDurationPanel(final LightSheetManager model) {
        super("Durations");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        lblSliceTime_ = new Label("Slice");
        lblVolumeTime_ = new Label("Volume:");
        lblTotalTime_ = new Label("Total:");

        lblSliceTimeValue_ = new Label("0.0");
        lblVolumeTimeValue_ = new Label("0.0");
        lblTotalTimeValue_ = new Label("0.0");

        add(lblSliceTime_, "");
        add(lblSliceTimeValue_, "wrap");
        add(lblVolumeTime_, "");
        add(lblVolumeTimeValue_, "wrap");
        add(lblTotalTime_, "");
        add(lblTotalTimeValue_, "");
    }

    private void createEventHandlers() {

    }
}
