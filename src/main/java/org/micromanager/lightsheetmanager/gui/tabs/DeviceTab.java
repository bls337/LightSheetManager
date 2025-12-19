package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.model.DeviceManager;
import org.micromanager.lightsheetmanager.LightSheetManager;

import javax.swing.JLabel;
import java.util.Objects;

/**
 *
 */
public class DeviceTab extends Panel implements ListeningPanel {

    private Button btnCreateConfigGroup_;

    private DeviceManager devices_;
    private LightSheetManager model_;

    public DeviceTab(final LightSheetManager model) {
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

        btnCreateConfigGroup_ = new Button("Create Devices Configuration Group", 220, 30);

        final JLabel lblGeometryType = new JLabel("Microscope Geometry: "
                + devices_.adapter().geometry());
        final JLabel lblLightSheetType = new JLabel("Light Sheet Type: "
                + devices_.adapter().lightSheetType());
        final JLabel lblNumImagingPaths = new JLabel("Imaging Paths: "
                + devices_.adapter().numImagingPaths());
        final JLabel lblNumIlluminationPaths = new JLabel("Illumination Paths: "
                + devices_.adapter().numIlluminationPaths());
        final JLabel lblNumSimultaneousCameras = new JLabel("Simultaneous Cameras: "
                + devices_.adapter().numSimultaneousCameras());

        btnCreateConfigGroup_.setToolTipText("Creates or updates the \"LightSheetManager::Devices\" " +
                "configuration group with all editable properties from the Light Sheet Manager device adapter.");

        add(lblGeometryType, "wrap");
        add(lblLightSheetType, "wrap");
        add(lblNumImagingPaths, "wrap");
        add(lblNumIlluminationPaths, "wrap");
        add(lblNumSimultaneousCameras, "wrap");
        add(btnCreateConfigGroup_, "gaptop 100");
    }

    private void createEventHandlers() {
        btnCreateConfigGroup_.registerListener(e -> {
            devices_.createConfigGroup();
        });
    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }
}
