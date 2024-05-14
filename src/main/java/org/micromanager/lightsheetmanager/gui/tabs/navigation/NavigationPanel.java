package org.micromanager.lightsheetmanager.gui.tabs.navigation;

import org.micromanager.lightsheetmanager.model.DeviceManager;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import mmcorej.DeviceType;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.*;
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

    private final Studio studio_;

    private Button btnHaltDevices_;
    private Button btnRefreshPanel_;
    private CheckBox cbxPollPositions_;

    private DeviceManager devices_;

    private PositionUpdater positionUpdater_;
    private ArrayList<ControlPanel> controlPanels_;

    private final LightSheetManagerModel model_;

    public NavigationPanel(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        studio_ = model_.studio();
        devices_ = model_.devices();

        controlPanels_ = new ArrayList<>();

        createUserInterface();
        createEventHandlers();
        positionUpdater_ = new PositionUpdater(this);
    }

    public void createUserInterface() {

        Panel.setMigLayoutDefault(
                "", //""debug 1000",
                "[]5[]",
                "[]0[]"
        );

        btnHaltDevices_ = new Button("HALT", 120, 30);
        btnRefreshPanel_ = new Button("Refresh", 120, 30);
        cbxPollPositions_ = new CheckBox("Poll Positions", true);

        final int numImagingPaths = devices_.getDeviceAdapter().getNumImagingPaths();
        final int numIlluminationPaths = devices_.getDeviceAdapter().getNumIlluminationPaths();

        final Map<String, String> deviceMap = devices_.getDeviceAdapter().getDeviceMap();
        final Map<String, DeviceType> deviceTypeMap = devices_.getDeviceAdapter().getDeviceTypeMap();

        final String illum = "Illum";
        final String imaging = "Imaging";

        //ControlPanel[][] panels = new ControlPanel[2][2];

        ArrayList<ArrayList<ControlPanel>> imagingProperties = new ArrayList<>(numImagingPaths);
        ArrayList<ArrayList<ControlPanel>> illumProperties = new ArrayList<>(numIlluminationPaths);
        ArrayList<ControlPanel> miscProperties = new ArrayList<>();

        for (int i = 0; i < numImagingPaths; i++) {
            imagingProperties.add(new ArrayList<>());
        }
        for (int i = 0; i < numIlluminationPaths; i++) {
            illumProperties.add(new ArrayList<>());
        }

        //System.out.println("img props: " + imagingProperties.size());
        //System.out.println("ill props: " + illumProperties.size());

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

            //ControlPanel controlPanel = new ControlPanel(studio_, propertyName, deviceName, deviceType, ControlPanel.Axis.X);

            //System.out.println(property);
            if (propertyName.startsWith(illum)) {
                // check if String contains digit (TODO: or switch based on microscope geometry?)
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
                            studio_, propertyName, deviceName, deviceType, axis);
                    illumProperties.get(pathNum - 1).add(controlPanel);
                    System.out.println(propertyName + " " + pathNum + " added to illum properties.");
                } else {
                    // does not contain digit: SCAPE geometry etc
                    final int pathNum = 1;
                    if (deviceType == DeviceType.GalvoDevice) {
                        ControlPanel controlPanelX = new ControlPanel(
                                studio_, propertyName + ": X Axis", deviceName, deviceType, ControlPanel.Axis.X);
                        ControlPanel controlPanelY = new ControlPanel(
                                studio_, propertyName + ": Y Axis", deviceName, deviceType, ControlPanel.Axis.Y);
                        // TODO: check for ASI hardware when settings units
                        controlPanelX.setUnits("°");
                        controlPanelY.setUnits("°");
                        illumProperties.get(pathNum - 1).add(controlPanelX);
                        illumProperties.get(pathNum - 1).add(controlPanelY);
                    }
                    System.out.println(propertyName + " " + pathNum + " added to illum properties.");
                }
            } else if (propertyName.startsWith(imaging)) {
                boolean containsDigit = containsDigit(propertyName);
                final int pathNum;
                if (containsDigit) {
                    pathNum = Character.getNumericValue(propertyName.charAt(imaging.length()));
                } else {
                    pathNum = 1;
                }
                ControlPanel controlPanel = new ControlPanel(
                        studio_, propertyName, deviceName, deviceType, ControlPanel.Axis.NONE);
                imagingProperties.get(pathNum - 1).add(controlPanel);
                System.out.println(propertyName + " " + pathNum + " added to imaging properties.");
            } else {
                // propertyName doesn't start with "Illum" or "Imaging"
                if (deviceType == DeviceType.XYStageDevice) {
                    ControlPanel controlPanelX = new ControlPanel(
                            studio_, propertyName + ": X Axis", deviceName, deviceType, ControlPanel.Axis.X);
                    ControlPanel controlPanelY = new ControlPanel(
                            studio_, propertyName + ": Y Axis", deviceName, deviceType, ControlPanel.Axis.Y);
                    miscProperties.add(controlPanelX);
                    miscProperties.add(controlPanelY);

                    System.out.println(propertyName + " added to misc properties");
                } else if (deviceType == DeviceType.StageDevice) {
                    ControlPanel controlPanel = new ControlPanel(
                            studio_, propertyName, deviceName, deviceType, ControlPanel.Axis.NONE);
                    miscProperties.add(controlPanel);

                    System.out.println(propertyName + " added to misc properties");
                } else {
                    // TODO: added this so we don't add PLC card here, hopefully nothing else breaks
                    System.out.println(propertyName + " NOT added to misc properties");
                }
            }
            devicesFound++;
        }

        if (devicesFound == 0) {
            add(new JLabel("No devices or device adapter properties are not set."), "wrap");
            add(btnHaltDevices_, "wrap");
            //add(btnRefreshPanel_, "wrap");
            add(cbxPollPositions_, "");
            return;
        }

        miscProperties.sort(Comparator.comparing(ControlPanel::getPropertyName));

        illumProperties.get(0).sort(Comparator.comparing(ControlPanel::getPropertyName));
        imagingProperties.get(0).sort(Comparator.comparing(ControlPanel::getPropertyName));

        //ControlPanel control = new ControlPanel(studio_,"SampleXY", deviceMap.get("SampleXY"), DeviceType.XYStageDevice);

        int i = 1;
        for (ArrayList<ControlPanel> list : imagingProperties) {
            if (!list.isEmpty()) {
                Panel imagingPanel = new Panel("Imaging Path " + i, TitledBorder.LEFT);
                for (ControlPanel controlPanel : list) {
                    imagingPanel.add(controlPanel, "wrap");
                    controlPanels_.add(controlPanel);
                }
                add(imagingPanel, "wrap");
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

        Panel miscPanel = new Panel("Additional Axes", TitledBorder.LEFT);
        for (ControlPanel controlPanel : miscProperties) {
            miscPanel.add(controlPanel, "wrap");
            controlPanels_.add(controlPanel);
        }

        add(miscPanel, "wrap");
        add(btnHaltDevices_, "split 3");
        //add(btnRefreshPanel_, "");
        add(cbxPollPositions_, "");
    }

    private void createEventHandlers() {

        btnRefreshPanel_.registerListener(e -> {
            System.out.println("refresh pressed");
            removeAll();
            createUserInterface();
            createEventHandlers();
            if (cbxPollPositions_.isSelected()) {
                positionUpdater_.startPolling();
            }
            revalidate();
            repaint();
        });

        btnHaltDevices_.registerListener(e -> haltAllDevices());

        cbxPollPositions_.registerListener(e -> {
            if (cbxPollPositions_.isSelected()) {
                positionUpdater_.startPolling();
            } else {
                positionUpdater_.stopPolling();
            }
            System.out.println("poll positions isSelected: " + cbxPollPositions_.isSelected());
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

    /**
     * Used by the PositionUpdater to update the displayed positions.
     */
    public void updatePositions() {
        //System.out.println("isEDT: " + SwingUtilities.isEventDispatchThread());
        //System.out.println("updating!");
        for (ControlPanel panel : controlPanels_) {
            panel.updatePosition();
        }
    }

    public void startPolling() {
        positionUpdater_.startPolling();
    }

    public void stopPolling() {
        positionUpdater_.stopPolling();
    }

    public boolean isPolling() {
        return positionUpdater_.isPolling();
    }

    public PositionUpdater getPositionUpdater() {
        return positionUpdater_;
    }

    public void haltAllDevices() {
        //System.out.println("Halt pressed!");
        for (ControlPanel controlPanel : controlPanels_) {
            // only try to stop XY and 1D stages
            DeviceType deviceType = controlPanel.getDeviceType();
            if (deviceType == DeviceType.XYStageDevice || deviceType == DeviceType.StageDevice) {
                controlPanel.stop();
            }
        }
    }
}
