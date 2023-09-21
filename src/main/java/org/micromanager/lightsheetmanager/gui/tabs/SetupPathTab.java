package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.setup.SetupPanel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import java.awt.Font;
import java.util.Objects;

public class SetupPathTab extends Panel {

    private int pathNum_;
    private SetupPanel setupPanel_;

    private LightSheetManagerModel model_;

    public SetupPathTab(final LightSheetManagerModel model, final int pathNum) {
        model_ = Objects.requireNonNull(model);
        pathNum_ = pathNum;
        createUserInterface();
    }

    private void createUserInterface() {
        final String title = "Setup Path " + pathNum_;
        final Label lblTitle = new Label(title, Font.BOLD, 16);

        setupPanel_ = new SetupPanel(model_, pathNum_);

        add(lblTitle, "wrap");
        add(setupPanel_, "wrap");
    }

    public void swapPanels(final CameraMode cameraMode) {
        setupPanel_.getBeamSheetPanel().swapPanels(cameraMode);
    }

    public int getPathNum() {
        return pathNum_;
    }

}
