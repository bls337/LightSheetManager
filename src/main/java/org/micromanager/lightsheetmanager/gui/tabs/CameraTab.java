package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.data.CameraLibrary;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.cameras.CameraBase;

import java.awt.Font;
import java.util.Arrays;
import java.util.Objects;

public class CameraTab extends Panel implements ListeningPanel {

    private Button btnUnchangedROI_;
    private Button btnFullROI_;
    private Button btnHalfROI_;
    private Button btnQuarterROI_;
    private Button btnEigthROI_;
    private Button btnCustomROI_;
    private Button btnGetCurrentROI_;
    private ComboBox cmbCameraTriggerMode_;
    private ComboBox cmbPrimaryCamera_;
    private CheckBox cbxAcquireFromBothSides_;

    private final TabPanel tabPanel_;
    private final LightSheetManager model_;

    public CameraTab(final LightSheetManager model, final TabPanel tabPanel) {
        tabPanel_ = Objects.requireNonNull(tabPanel);
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final Label lblTitle = new Label("Camera Settings", Font.BOLD, 18);

        final Panel pnlROI = new Panel("Imaging ROI");
        final Panel pnlCameraTrigger = new Panel("Camera Trigger Mode");
        final Panel pnlPrimaryCamera = new Panel("First Camera");

        final Label lblXOffset = new Label("X Offset:");
        final Label lblYOffset = new Label("Y Offset:");
        final Label lblWidth = new Label("Width:");
        final Label lblHeight = new Label("Height:");

        btnUnchangedROI_ = new Button("Unchanged", 120, 30);
        btnFullROI_ = new Button("Full", 60, 30);
        btnHalfROI_ = new Button("1/2", 60, 30);
        btnQuarterROI_ = new Button("1/4", 60, 30);
        btnEigthROI_ = new Button("1/8", 60, 30);
        btnCustomROI_ = new Button("Custom", 120, 30);
        btnGetCurrentROI_ = new Button("Get Current ROI", 120, 30);

        // get the imaging camera library
        final CameraBase camera = model_.devices().firstImagingCamera();
        final CameraLibrary camLib = CameraLibrary.fromString(camera.getDeviceLibrary());

        cmbCameraTriggerMode_ = new ComboBox(CameraMode.getAvailableModes(camLib),
                model_.acquisitions().settings().cameraMode().toString());

        // validate that the logical device name exists
        final String[] cameraNames = model_.devices().imagingCameraNames();
        String primaryCamera =  model_.acquisitions().settings().primaryCamera();
        if (!Arrays.asList(cameraNames).contains(primaryCamera)) {
            model_.acquisitions().settingsBuilder().primaryCamera(cameraNames[0]);
            primaryCamera = cameraNames[0]; // use first device as default
            model_.studio().logs().logMessage(
                    "Logical device name " + primaryCamera + " not found, use " + cameraNames[0] + " instead.");
        }

        // simultaneous camera settings
        cmbPrimaryCamera_ = new ComboBox(cameraNames, primaryCamera);
        cbxAcquireFromBothSides_ = new CheckBox("Acquire from both sides simultaneously",
                model_.acquisitions().settings().isAcqFromBothSides());

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

        pnlCameraTrigger.add(cmbCameraTriggerMode_, "");
        if (model_.devices().adapter().numSimultaneousCameras() > 1) {
            pnlPrimaryCamera.add(cmbPrimaryCamera_, "wrap");
        }
        add(lblTitle, "wrap");
        add(pnlROI, "wrap");
        add(pnlCameraTrigger, "wrap");
        add(pnlPrimaryCamera, "wrap");
        if (model_.devices().adapter().numSimultaneousCameras() > 1) {
            add(cbxAcquireFromBothSides_, "");
        }
    }

    private void createEventHandlers() {

        // camera trigger mode
        cmbCameraTriggerMode_.registerListener(e -> {
            final CameraMode cameraMode = CameraMode.fromString(cmbCameraTriggerMode_.getSelected());
            model_.acquisitions().settingsBuilder().cameraMode(cameraMode);
            tabPanel_.getAcquisitionTab().getSliceSettingsPanel().switchUI(cameraMode);
            tabPanel_.swapSetupPathPanels(cameraMode);
        });

        // select primary camera
        cmbPrimaryCamera_.registerListener(e ->
                model_.acquisitions().settingsBuilder().primaryCamera(
                        cmbPrimaryCamera_.getSelected()));

        // use both cameras
        cbxAcquireFromBothSides_.registerListener(e ->
                model_.acquisitions().settingsBuilder().isAcqFromBothSides(
                        cbxAcquireFromBothSides_.isSelected()));

        //model_.studio().core().setROI();
    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }
}