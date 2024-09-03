package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.gui.frames.XYZGridFrame;
import org.micromanager.lightsheetmanager.LightSheetManager;

import java.util.Objects;

public class MultiPositionPanel extends Panel {

    private Label lblPostMoveDelay_;
    private Spinner spnPostMoveDelay_;
    private Button btnOpenXYZGrid_;
    private Button btnEditPositionList_;

    private final XYZGridFrame xyzGridFrame_;
    private final LightSheetManager model_;

    public MultiPositionPanel(final LightSheetManager model, final CheckBox cbxUseMultiPositions) {
        super(cbxUseMultiPositions);
        model_ = Objects.requireNonNull(model);
        xyzGridFrame_ = new XYZGridFrame(model_);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {

        // post move delay
        lblPostMoveDelay_ = new Label("Post-move delay [ms]:");
        Spinner.setDefaultSize(8);
        spnPostMoveDelay_ = Spinner.createIntegerSpinner(
                model_.acquisitions().settings().postMoveDelay(),
                0, Integer.MAX_VALUE, 100);

        // XYZ grid
        btnEditPositionList_ = new Button("Edit Position List...", 130, 24);
        btnOpenXYZGrid_ = new Button("XYZ Grid...", 90, 24);

        add(btnEditPositionList_, "");
        add(btnOpenXYZGrid_, "wrap");
        add(lblPostMoveDelay_, "");
        add(spnPostMoveDelay_, "");
    }

    private void createEventHandlers() {

        // open XYZ grid
        btnOpenXYZGrid_.registerListener(e -> {
            if (model_.devices().hasDevice("SampleXY")
                    && model_.devices().hasDevice("SampleZ")) {
                xyzGridFrame_.setVisible(true);
            } else {
                model_.studio().logs().showError(
                        "SampleXY and SampleZ must not be \"Undefined\" to use the XYZ grid.");
            }
        });

        // open position list
        btnEditPositionList_.registerListener(e -> model_.studio().app().showPositionList());

        spnPostMoveDelay_.registerListener(e -> model_.acquisitions()
                .settingsBuilder().postMoveDelay(spnPostMoveDelay_.getInt()));

    }

    public void setPanelEnabled(final boolean state) {
        lblPostMoveDelay_.setEnabled(state);
        spnPostMoveDelay_.setEnabled(state);
        btnEditPositionList_.setEnabled(state);
    }

    public XYZGridFrame getXYZGridFrame() {
        return xyzGridFrame_;
    }
}
