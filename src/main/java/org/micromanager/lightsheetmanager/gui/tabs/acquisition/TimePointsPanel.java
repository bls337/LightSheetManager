package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import java.util.Objects;

public class TimePointsPanel extends Panel {

    private Label lblNumTimePoints_;
    private Label lblTimePointInterval_;
    private Spinner spnNumTimePoints_;
    private Spinner spnTimePointInterval_;

    private final LightSheetManagerModel model_;

    public TimePointsPanel(final LightSheetManagerModel model, final CheckBox cbxUseTimePoints) {
        super(cbxUseTimePoints);
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final DefaultAcquisitionSettingsDISPIM acqSettings =
                model_.acquisitions().settings();

        setMigLayout(
                "",
                "[]5[]",
                "[]5[]"
        );

        lblNumTimePoints_ = new Label("Number:");
        lblTimePointInterval_ = new Label("Interval [s]:");
        spnNumTimePoints_ = Spinner.createIntegerSpinner(
                acqSettings.numTimePoints(), 1, Integer.MAX_VALUE,1);
        spnTimePointInterval_ = Spinner.createIntegerSpinner(
                acqSettings.timePointInterval(), 0, Integer.MAX_VALUE, 1);

        add(lblNumTimePoints_, "");
        add(spnNumTimePoints_, "wrap");
        add(lblTimePointInterval_, "");
        add(spnTimePointInterval_, "");
    }

    // TODO: update duration labels
    private void createEventHandlers() {

        spnNumTimePoints_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().numTimePoints(spnNumTimePoints_.getInt());
            //updateDurationLabels();
            //System.out.println("getNumTimePoints: " + model_.acquisitions().getAcquisitionSettings().getNumTimePoints());
        });

        spnTimePointInterval_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().timePointInterval(spnTimePointInterval_.getInt());
            //updateDurationLabels();
            //System.out.println("getTimePointInterval: " + model_.acquisitions().getAcquisitionSettings().getTimePointInterval());
        });
    }

    @Override
    public void setEnabled(final boolean state) {
        lblNumTimePoints_.setEnabled(state);
        lblTimePointInterval_.setEnabled(state);
        spnNumTimePoints_.setEnabled(state);
        spnTimePointInterval_.setEnabled(state);
    }

}
