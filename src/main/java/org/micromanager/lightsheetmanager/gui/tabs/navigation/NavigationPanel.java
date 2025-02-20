package org.micromanager.lightsheetmanager.gui.tabs.navigation;

import org.micromanager.lightsheetmanager.model.DeviceManager;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import mmcorej.DeviceType;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.LightSheetDeviceManager;

import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

// TODO: how to deal with 2 xy stages?
// TODO: place to set speed of xyz? (maybe not in this panel)
// TODO: better halt button
// TODO: make it so you can refresh the ui if you select a new device

public class NavigationPanel extends Panel {

    private Button btnHaltDevices_;
    private Button btnRefreshPanel_;
    private CheckBox cbxPollPositions_;

    private final DeviceManager devices_;
    private final ArrayList<ControlPanel> controlPanels_;

    private final LightSheetManager model_;

    public NavigationPanel(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);
        devices_ = model_.devices();

        controlPanels_ = new ArrayList<>();

        createUserInterface();
        createEventHandlers();
    }

    /**
     * Create the control panels for each device.
     */
    private void createUserInterface() {

        setMigLayout(
                "", // "debug 1000"
                "[]5[]",
                "[]5[]"
        );

        btnHaltDevices_ = new Button("HALT", 120, 30);
        btnRefreshPanel_ = new Button("Refresh", 120, 30);
        cbxPollPositions_ = new CheckBox("Poll Positions",
                model_.pluginSettings().isPollingPositions());

        // Property prefixes
        final String illum = "Illum";
        final String imaging = "Imaging";

        // use pre-init property settings and Microscope Geometry data from Device Adapter
        final LightSheetDeviceManager deviceAdapter = devices_.getDeviceAdapter();
        final int numImagingPaths = deviceAdapter.getNumImagingPaths();
        final int numIllumPaths = deviceAdapter.getNumIlluminationPaths();
        final Map<String, String> deviceMap = deviceAdapter.getDeviceMap();
        final Map<String, DeviceType> deviceTypeMap = deviceAdapter.getDeviceTypeMap();

        // init ControlPanel arrays
        final ArrayList<ControlPanel> stageProperties = new ArrayList<>();
        final ArrayList<ArrayList<ControlPanel>> imagingProperties = new ArrayList<>(numImagingPaths);
        final ArrayList<ArrayList<ControlPanel>> illumProperties = new ArrayList<>(numIllumPaths);

        for (int i = 0; i < numImagingPaths; i++) {
            imagingProperties.add(new ArrayList<>());
        }
        for (int i = 0; i < numIllumPaths; i++) {
            illumProperties.add(new ArrayList<>());
        }

        // create a ControlPanel for each device
        int devicesFound = 0;
        for (String propertyName : deviceMap.keySet()) {

            final String deviceName = deviceMap.get(propertyName);
            if (deviceName.equals("Undefined")) {
                continue; // skip this property => device not set
            }

            // TODO: only add certain kinds of devices
            //System.out.println(propertyName);
            final DeviceType deviceType = deviceTypeMap.get(propertyName);
            if (deviceType == DeviceType.CameraDevice) {
                continue; // don't add cameras to axis list
            }

            //System.out.println(property);
            if (propertyName.startsWith(illum)) {
                // check if String contains digit TODO: or switch based on microscope geometry?
                boolean containsDigit = containsDigit(propertyName);
                if (containsDigit) {
                    // DISPIM
                    System.out.println("propertyName: " + propertyName);
                    final int pathNum = Character.getNumericValue(propertyName.charAt(illum.length()));
                    System.out.println("pathNum: " + pathNum);
                    // galvo devices
                    ControlPanel.Axis axis = ControlPanel.Axis.NONE;
                    if (propertyName.endsWith("Beam")) {
                        axis = ControlPanel.Axis.X;
                    } else if (propertyName.endsWith("Slice")) {
                        axis = ControlPanel.Axis.Y;
                    }
                    ControlPanel controlPanel = new ControlPanel(
                            model_, propertyName, deviceName, deviceType, axis, ControlPanel.Units.MICRONS);
                    illumProperties.get(pathNum - 1).add(controlPanel);
                    System.out.println(propertyName + " " + pathNum + " added to illum properties.");
                } else {
                    // does not contain digit: SCAPE geometry etc
                    final int pathNum = 1;
                    if (deviceType == DeviceType.GalvoDevice) {
                        ControlPanel controlPanelX = new ControlPanel(
                              model_, propertyName, deviceName, deviceType, ControlPanel.Axis.X, ControlPanel.Units.DEGREES);
                        ControlPanel controlPanelY = new ControlPanel(
                              model_, propertyName, deviceName, deviceType, ControlPanel.Axis.Y, ControlPanel.Units.DEGREES);
                        // TODO: check for ASI hardware when settings units
                        illumProperties.get(pathNum - 1).add(controlPanelX);
                        illumProperties.get(pathNum - 1).add(controlPanelY);
                    }
                    System.out.println(propertyName + " " + pathNum + " added to illum properties.");
                }
            } else if (propertyName.startsWith(imaging)) {
                boolean containsDigit = containsDigit(propertyName);
                final int pathNum;
                if (containsDigit) {
                    pathNum = Character.getNumericValue(propertyName.charAt(imaging.length())); // TODO: make fn?
                } else {
                    pathNum = 1;
                }
                ControlPanel controlPanel = new ControlPanel(
                      model_, propertyName, deviceName, deviceType, ControlPanel.Axis.NONE, ControlPanel.Units.MICRONS);
                imagingProperties.get(pathNum - 1).add(controlPanel);
                System.out.println(propertyName + " " + pathNum + " added to imaging properties.");
            } else {
                // propertyName doesn't start with "Illum" or "Imaging"
                if (deviceType == DeviceType.XYStageDevice) {
                    ControlPanel controlPanelX = new ControlPanel(
                          model_, propertyName, deviceName, deviceType, ControlPanel.Axis.X, ControlPanel.Units.MICRONS);
                    ControlPanel controlPanelY = new ControlPanel(
                          model_, propertyName, deviceName, deviceType, ControlPanel.Axis.Y, ControlPanel.Units.MICRONS);
                    stageProperties.add(controlPanelX);
                    stageProperties.add(controlPanelY);

                    System.out.println(propertyName + " added to misc properties");
                } else if (deviceType == DeviceType.StageDevice) {
                    ControlPanel controlPanel = new ControlPanel(
                          model_, propertyName, deviceName, deviceType, ControlPanel.Axis.NONE, ControlPanel.Units.MICRONS);
                    stageProperties.add(controlPanel);

                    System.out.println(propertyName + " added to misc properties");
                } else {
                    // TODO: added this so we don't add PLC card here, hopefully nothing else breaks
                    System.out.println(propertyName + " NOT added to misc properties");
                }
            }
            devicesFound++;
        }

        // no devices found
        if (devicesFound == 0) {
            add(new JLabel("No devices found or the device adapter properties are not set."), "wrap");
            add(btnHaltDevices_, "wrap");
            //add(btnRefreshPanel_, "wrap"); // TODO: add refresh option
            add(cbxPollPositions_, "");
            return; // early exit => no devices to add
        }

        // sort all ControlPanel groups by property name
        stageProperties.sort(Comparator.comparing(ControlPanel::getPropertyName));
        illumProperties.get(0).sort(Comparator.comparing(ControlPanel::getPropertyName));
        imagingProperties.get(0).sort(Comparator.comparing(ControlPanel::getPropertyName));

        // add ControlPanels to ui
        final Panel sampleAxesPanel = new Panel("Sample Axes", TitledBorder.LEFT);
        for (ControlPanel controlPanel : stageProperties) {
            sampleAxesPanel.add(controlPanel, "wrap");
            controlPanels_.add(controlPanel);
        }

        if (!stageProperties.isEmpty()) {
            add(sampleAxesPanel, "wrap");
        }

        int i = 1;
        for (ArrayList<ControlPanel> list : imagingProperties) {
            if (!list.isEmpty()) {
                Panel imagingPanel = new Panel("Imaging Path " + i, TitledBorder.LEFT);
                for (ControlPanel controlPanel : list) {
                    imagingPanel.add(controlPanel, "wrap");
                    controlPanels_.add(controlPanel);
                }
                add(imagingPanel, "wrap, growx");
            }
            i++;
        }

        int ii = 1;
        for (ArrayList<ControlPanel> list : illumProperties) {
            if (!list.isEmpty()) {
                Panel illumPanel = new Panel("Illumination Path " + ii, TitledBorder.LEFT);
                for (ControlPanel controlPanel : list) {
                    illumPanel.add(controlPanel, "wrap");
                    controlPanels_.add(controlPanel);
                }
                add(illumPanel, "wrap, growx");
            }
            ii++;
        }

        add(btnHaltDevices_, "wrap");
        //add(btnRefreshPanel_, "");
        add(cbxPollPositions_, "");
    }

    private void createEventHandlers() {

        // refresh devices and ui
        btnRefreshPanel_.registerListener(e -> {
            removeAll();
            createUserInterface();
            createEventHandlers();
            revalidate();
            repaint();
            if (cbxPollPositions_.isSelected()) {
                model_.positions().startPolling();
            }
        });

        // halt stage devices
        btnHaltDevices_.registerListener(e -> model_.devices().haltDevices());

        // position polling
        cbxPollPositions_.registerListener(e -> {
            final boolean isSelected = cbxPollPositions_.isSelected();
            model_.pluginSettings().setPollingPositions(isSelected);
            if (isSelected) {
                model_.positions().startPolling();
            } else {
                model_.positions().stopPolling();
            }
            //System.out.println("poll positions isSelected: " + cbxPollPositions_.isSelected());
        });

    }

    /**
     * Returns true if the string contains a digit.
     *
     * @param str the string to search
     * @return true if the string contains a digit.
     */
    private boolean containsDigit(final String str) {
        boolean containsDigit = false;
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                containsDigit = true;
            }
        }
        return containsDigit;
    }

}
