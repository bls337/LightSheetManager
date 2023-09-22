package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.gui.frames.XYZGridFrame;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import java.util.Objects;

public class MultiPositionPanel extends Panel {

    private Label lblPostMoveDelay_;
    private Spinner spnPostMoveDelay_;
    private Button btnOpenXYZGrid_;
    private Button btnEditPositionList_;

    private final XYZGridFrame xyzGridFrame_;
    private final LightSheetManagerModel model_;

    public MultiPositionPanel(final LightSheetManagerModel model, final CheckBox cbxUseMultiPositions) {
        super(cbxUseMultiPositions);
        model_ = Objects.requireNonNull(model);
        xyzGridFrame_ = new XYZGridFrame(model_);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final DefaultAcquisitionSettingsDISPIM acqSettings =
                model_.acquisitions().settings();

        Spinner.setDefaultSize(7);
        lblPostMoveDelay_ = new Label("Post-move delay [ms]:");
        spnPostMoveDelay_ = Spinner.createIntegerSpinner(
                acqSettings.postMoveDelay(), 0, Integer.MAX_VALUE, 100);
        btnEditPositionList_ = new Button("Edit Position List", 120, 24);
        btnOpenXYZGrid_ = new Button("XYZ Grid", 80, 24);

        add(btnEditPositionList_, "");
        add(btnOpenXYZGrid_, "wrap");
        add(lblPostMoveDelay_, "");
        add(spnPostMoveDelay_, "");
    }

    private void createEventHandlers() {
        final DefaultAcquisitionSettingsDISPIM.Builder asb_ =
                model_.acquisitions().settingsBuilder();

        btnOpenXYZGrid_.registerListener(e -> xyzGridFrame_.setVisible(true));
        btnEditPositionList_.registerListener(e -> model_.studio().app().showPositionList());

        spnPostMoveDelay_.registerListener(e -> {
            asb_.postMoveDelay(spnPostMoveDelay_.getInt());
            //System.out.println("getPostMoveDelay: " + model_.acquisitions().getAcquisitionSettings().getPostMoveDelay());
        });

    }

    @Override
    public void setEnabled(final boolean state) {
        lblPostMoveDelay_.setEnabled(state);
        spnPostMoveDelay_.setEnabled(state);
        btnEditPositionList_.setEnabled(state);
    }

}
