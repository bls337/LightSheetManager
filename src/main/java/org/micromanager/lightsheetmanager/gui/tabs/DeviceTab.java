package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.model.DeviceManager;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JLabel;
import java.util.Objects;

/**
 *
 */
public class DeviceTab extends Panel {

    private Button btnCreateConfigGroup_;

    private DeviceManager devices_;
    private LightSheetManagerModel model_;

    public DeviceTab(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        devices_ = model_.devices();
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {

        setMigLayout(
            "",
            "[]0[]",
            "[]0[]"
        );

        btnCreateConfigGroup_ = new Button("Create System Configuration Group", 220, 30);

        final GeometryType geometryType = devices_.getDeviceAdapter().getMicroscopeGeometry();
        final int numImagingPaths = devices_.getDeviceAdapter().getNumImagingPaths();

        final JLabel lblGeometryType = new JLabel("Microscope Geometry: " + geometryType);
        final JLabel lblLightSheetType = new JLabel("Light Sheet Type: " + devices_.getDeviceAdapter().getLightSheetType());
        final JLabel lblNumImagingPaths = new JLabel("Imaging Paths: " + numImagingPaths);
        final JLabel lblNumIlluminationPaths = new JLabel("Illumination Paths: " + devices_.getDeviceAdapter().getNumIlluminationPaths());
        final JLabel lblNumSimultaneousCameras = new JLabel("Simultaneous Cameras: " + devices_.getDeviceAdapter().getNumSimultaneousCameras());
        //final JLabel lblDeviceAdapterVersion = new JLabel("Device Adapter Version: " + devices_.getVersionNumber());

        add(lblGeometryType, "wrap");
        add(lblLightSheetType, "wrap");
        add(lblNumImagingPaths, "wrap");
        add(lblNumIlluminationPaths, "wrap");
        add(lblNumSimultaneousCameras, "wrap");
        add(btnCreateConfigGroup_, "gaptop 100");
    }

    private void createEventHandlers() {
        btnCreateConfigGroup_.registerListener(e ->
                devices_.createConfigGroup());
    }

}
