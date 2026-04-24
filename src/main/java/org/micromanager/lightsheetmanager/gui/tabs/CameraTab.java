package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.cameras.CameraBase;

import java.awt.Font;
import java.util.Objects;

public class CameraTab extends Panel implements ListeningPanel {

    private Button btnUnchangedROI_;
    private Button btnFullROI_;
    private Button btnHalfROI_;
    private Button btnQuarterROI_;
    private Button btnEigthROI_;
    private Button btnCustomROI_;
    private Button btnGetCurrentROI_;

    private ComboBox<CameraMode> cmbCameraTriggerMode_;

    private final LightSheetManager model_;

    public CameraTab(final LightSheetManager model) {;
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final Label lblTitle = new Label("Camera Settings", Font.BOLD, 18);

        final Panel pnlROI = new Panel("Imaging ROI");

        final Label lblXOffset = new Label("X Offset:");
        final Label lblYOffset = new Label("Y Offset:");
        final Label lblWidth = new Label("Width:");
        final Label lblHeight = new Label("Height:");

        btnUnchangedROI_ = new Button("Unchanged", 140, 30);
        btnFullROI_ = new Button("Full", 70, 30);
        btnHalfROI_ = new Button("1/2", 70, 30);
        btnQuarterROI_ = new Button("1/4", 70, 30);
        btnEigthROI_ = new Button("1/8", 70, 30);
        btnCustomROI_ = new Button("Custom", 140, 30);
        btnGetCurrentROI_ = new Button("Get Current ROI", 140, 30);

        pnlROI.add(btnUnchangedROI_, "span 2, wrap");
        pnlROI.add(btnFullROI_, "");
        pnlROI.add(btnHalfROI_, "wrap");
        pnlROI.add(btnQuarterROI_, "");
        pnlROI.add(btnEigthROI_, "wrap");
        pnlROI.add(btnCustomROI_, "span 2, wrap");
        pnlROI.add(lblXOffset, "wrap");
        pnlROI.add(lblYOffset, "wrap");
        pnlROI.add(lblWidth, "wrap");
        pnlROI.add(lblHeight, "wrap");
        pnlROI.add(btnGetCurrentROI_, "span 2");


        add(lblTitle, "wrap");
        add(pnlROI, "wrap");
    }

    private void createEventHandlers() {
        btnUnchangedROI_.registerListener(() -> {
            final CameraBase[] cameras = model_.devices().imagingCameras();
            for (CameraBase camera : cameras) {
                camera.setROI();
            }
        });
    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }
}
