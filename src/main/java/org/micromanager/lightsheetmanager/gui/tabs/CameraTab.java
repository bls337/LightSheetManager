package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.data.CameraData;
import org.micromanager.lightsheetmanager.api.data.CameraLibrary;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.gui.components.RadioButton;
import org.micromanager.lightsheetmanager.model.devices.cameras.CameraBase;

import javax.swing.JLabel;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
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
    private RadioButton radPrimaryCamera_;
    private CheckBox cbxUseSimultaneousCameras_;
    private List<CheckBox> cbxCameras_;
    private int selectedIndex_;

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
        final Panel pnlPrimaryCamera = new Panel("Simultaneous Cameras");

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

        // TODO: use optional here for camera?

        // get the imaging camera library
        CameraMode[] modes = {};
        final CameraBase camera = model_.devices().firstImagingCamera();
        if (camera != null) {
            final CameraLibrary camLib = CameraLibrary.fromString(camera.getDeviceLibrary());
            modes = CameraMode.modesByDeviceLibrary(camLib);
        }

        cmbCameraTriggerMode_ = new ComboBox<>(modes,
                model_.acquisitions().settings().cameraMode(), 140, 20);

        // validate that the logical device name exists
        final String[] cameraNames = model_.devices().imagingCameraNames();
        cbxCameras_ = new ArrayList<>(cameraNames.length);

        // the first element of the list is the primary camera
        String primaryCamera = "";
        final CameraData[] cameraOrder =  model_.acquisitions().settings().imagingCameraOrder();
        if (cameraOrder.length > 0) {
            primaryCamera = cameraOrder[0].name();
        } else {
            // TODO: what is a sensible default here? make sure cameraNames is always populated?
            // primaryCamera = cameraNames[0]; // default to first camera name
        }

        // simultaneous camera settings
        radPrimaryCamera_ = new RadioButton(cameraNames, primaryCamera, RadioButton.VERTICAL);
        cbxUseSimultaneousCameras_ = new CheckBox("Acquire from all active cameras simultaneously",
                model_.acquisitions().settings().isUsingSimultaneousCameras());

        // active check boxes
        final Panel pnlCheckboxes = new Panel();
        pnlCheckboxes.setMigLayout(
                "",
                "",
                "[]6[]"
        );

        // we already have the camera order array
        for (String cameraName : cameraNames) {
            final boolean isActive = CameraData.isCameraActive(cameraOrder, cameraName);
            final CheckBox checkBox = new CheckBox("Active", isActive);
            pnlCheckboxes.add(checkBox, "wrap");
            cbxCameras_.add(checkBox);
        }

        // we can only set this after we load the settings
        selectedIndex_ = radPrimaryCamera_.getSelectedIndex();
        // must not disable the primary camera
        cbxCameras_.get(selectedIndex_).setLocked(true);

        final Panel pnlCameraSelectionRow = new Panel();
        pnlCameraSelectionRow.add(new JLabel("Select Primary Camera"), "wrap");
        pnlCameraSelectionRow.add(radPrimaryCamera_, "");
        pnlCameraSelectionRow.add(pnlCheckboxes, "gaptop 2px");

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

        add(lblTitle, "wrap");
        if (model_.devices().adapter().numSimultaneousCameras() > 1) {
            pnlPrimaryCamera.add(pnlCameraSelectionRow, "wrap");
            pnlPrimaryCamera.add(cbxUseSimultaneousCameras_, "wrap");
            add(pnlROI, "growx");
            add(pnlPrimaryCamera, "wrap");
        } else {
            add(pnlROI, "wrap");
        }
        add(pnlCameraTrigger, "growx");
    }

    private void createEventHandlers() {

        // camera trigger mode
        cmbCameraTriggerMode_.registerListener(e -> {
            final CameraMode cameraMode = cmbCameraTriggerMode_.getSelected();
            model_.acquisitions().settingsBuilder().cameraMode(cameraMode);
            tabPanel_.getAcquisitionTab().getSliceSettingsPanel().switchUI(cameraMode);
            tabPanel_.swapSetupPathPanels(cameraMode);
        });

        // select primary camera
        radPrimaryCamera_.registerListener(e -> computeCameraOrder());

        // active camera check boxes
        for (CheckBox cbx : cbxCameras_) {
            cbx.registerListener(e -> computeCameraOrder());
        }

        // use all active cameras
        cbxUseSimultaneousCameras_.registerListener(e ->
                model_.acquisitions().settingsBuilder()
                        .useSimultaneousCameras(cbxUseSimultaneousCameras_.isSelected()));

        //model_.studio().core().setROI();
    }

    private boolean[] activeCameras() {
        boolean[] active = new boolean[cbxCameras_.size()];
        for (int i = 0; i < cbxCameras_.size(); i++) {
            active[i] = cbxCameras_.get(i).isSelected();
        }
        return active;
    }

    // Change the order of the imaging camera array based on user input.
    private void computeCameraOrder() {
        // cache the previous index
        final int lastSelectedIndex = selectedIndex_;

        // unlock the checkbox for the old primary camera
        cbxCameras_.get(lastSelectedIndex).setLocked(false);

        // lock the checkbox for the new primary camera
        final String selected = radPrimaryCamera_.getSelectedText();
        selectedIndex_ = radPrimaryCamera_.getSelectedIndex();
        cbxCameras_.get(selectedIndex_).setSelected(true, false);
        cbxCameras_.get(selectedIndex_).setLocked(true);

        // the first array index is the primary camera
        final String[] cameraNames = model_.devices().imagingCameraNames();
        final ArrayList<CameraData> cameraData = new ArrayList<>(cameraNames.length);
        cameraData.add(new CameraData(selected, true));

        // TODO(Brandon): uses simple ordering that works for 2 cameras,
        //   but needs additional work to support 4.

        // add cameras in a linear order for now
        final boolean[] active = activeCameras();
        for (int i = 0; i < cameraNames.length; i++) {
            if (!selected.equals(cameraNames[i])) {
                cameraData.add(new CameraData(cameraNames[i], active[i]));
            }
        }

        // update camera order
        model_.acquisitions().settingsBuilder()
                .imagingCameraOrder(cameraData.toArray(CameraData[]::new));
    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }
}
