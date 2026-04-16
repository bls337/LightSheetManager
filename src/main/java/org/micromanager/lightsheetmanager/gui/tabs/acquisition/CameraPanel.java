package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.api.AcquisitionSettings;
import org.micromanager.lightsheetmanager.api.data.CameraData;
import org.micromanager.lightsheetmanager.api.data.CameraLibrary;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.internal.ScapeAcquisitionSettings;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.RadioButton;
import org.micromanager.lightsheetmanager.gui.components.SettingsListener;
import org.micromanager.lightsheetmanager.model.devices.cameras.CameraBase;

import javax.swing.JLabel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CameraPanel extends Panel implements SettingsListener {

    private ComboBox<CameraMode> cmbCameraMode_;
    private RadioButton radPrimaryCamera_;
    private List<CheckBox> cbxCameras_;
    private int selectedIndex_;

    private final LightSheetManager model_;

    public CameraPanel(final LightSheetManager model) {
        super("Simultaneous Cameras");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
        model.userSettings().addChangeListener(this);
    }

    private void createUserInterface() {
        setMigLayout(
                "",
                "[]5[]",
                "[]5[]"
        );

        // TODO: use optional here for camera?

        // get the imaging camera library
        CameraMode[] modes = {};
        final CameraBase camera = model_.devices().firstImagingCamera();
        if (camera != null) {
            final CameraLibrary camLib = CameraLibrary.fromString(camera.getDeviceLibrary());
            modes = CameraMode.modesByDeviceLibrary(camLib);
        }

        cmbCameraMode_ = new ComboBox<>(modes,
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
        if (selectedIndex_ != -1) {
            cbxCameras_.get(selectedIndex_).setLocked(true);
        }

        final Panel pnlCameraSelectionRow = new Panel();
        pnlCameraSelectionRow.add(new JLabel("Select Primary Camera:"), "wrap");
        pnlCameraSelectionRow.add(radPrimaryCamera_, "");
        pnlCameraSelectionRow.add(pnlCheckboxes, "gaptop 2px");

        add(pnlCameraSelectionRow, "wrap");
        add(new JLabel("Camera Trigger Mode:"), "wrap");
        add(cmbCameraMode_, "wrap");
    }

    private void createEventHandlers() {
        // camera trigger mode
        cmbCameraMode_.registerListener(() -> {
            final CameraMode cameraMode = cmbCameraMode_.getSelected();
            model_.acquisitions().settingsBuilder().cameraMode(cameraMode);
        });

        // select primary camera
        radPrimaryCamera_.registerListener(this::computeCameraOrder);

        // active camera check boxes
        for (CheckBox cbx : cbxCameras_) {
            cbx.registerListener(this::computeCameraOrder);
        }
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
    public void onSettingsChanged(final AcquisitionSettings settings) {
        // TODO: add remaining props
        if (settings instanceof ScapeAcquisitionSettings) {
            var settingsScape = (ScapeAcquisitionSettings) settings;
            cmbCameraMode_.setSelectedItem(settingsScape.cameraMode());
            // set the primary camera
//            final CameraData[] cameraOrder = settingsScape.imagingCameraOrder();
//            if (cameraOrder.length > 0) {
//                final String primaryCamera = cameraOrder[0].name();
//                radPrimaryCamera_.setSelected(primaryCamera, true);
//            }
        }
    }
}
