package org.micromanager.lightsheetmanager.gui.navigation;

import org.micromanager.lightsheetmanager.model.DeviceManager;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import mmcorej.CMMCore;
import mmcorej.DeviceType;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

// TODO: how to deal with 2 xy stages?
// TODO: place to set speed of xyz? (maybe not in this panel)
// TODO: better halt button

public class NavigationPanel extends Panel {

    private final Studio studio_;
    private final CMMCore core_;

    private boolean isPollingPositions;

    private Button btnRefreshNavPanel_;
    private Button btnHaltDevices_;
    private CheckBox cbxPollPositions_;

    private DeviceManager devices_;

    private PositionUpdater positionUpdater_;
    private ArrayList<ControlPanel> controlPanels_;

    private LightSheetManagerModel model_;

    public NavigationPanel(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        studio_ = model_.getStudio();
        devices_ = model_.devices();
        core_ = studio_.core();

        controlPanels_ = new ArrayList<>();
        isPollingPositions = true; // starts in polling mode
        positionUpdater_ = new PositionUpdater(this, isPollingPositions);
    }

    // TODO: make it so you can refresh the ui if you select a new device

    public void init() {

        Panel.setMigLayoutDefault(
                "", //""debug 1000",
                "[]5[]",
                "[]0[]"
        );

        //LightSheetDeviceManager lsm = devices_.getDeviceAdapter();

        btnHaltDevices_ = new Button("HALT", 120, 30);
        btnRefreshNavPanel_ = new Button("Refresh", 120, 30);
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

        System.out.println("img props: " + imagingProperties.size());
        System.out.println("ill props: " + illumProperties.size());

        int devicesFound = 0;
        for (String propertyName : deviceMap.keySet()) {
            final String deviceName = deviceMap.get(propertyName);
            if (deviceName.equals("Undefined")) {
                continue; // skip this property => device not set
            }

            // TODO: only add certain kinds of devices
            System.out.println(propertyName);
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
                    // does not contain digit: SCAPE
                    final int pathNum = 1;
                    ControlPanel.Axis axis = ControlPanel.Axis.NONE;
                    ControlPanel controlPanel = new ControlPanel(
                            studio_, propertyName, deviceName, deviceType, axis);
                    illumProperties.get(pathNum - 1).add(controlPanel);
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
            add(btnRefreshNavPanel_, "wrap");
            add(cbxPollPositions_, "");
            return;
        }

        miscProperties.sort(Comparator.comparing(ControlPanel::getPropertyName));

        /// ????
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

        createEventHandlers();
//        Panel infoPanel = new Panel(true);
//        infoPanel.add(new JLabel("Axis"), "");
//        infoPanel.add(new JLabel("Position"), "");
//        infoPanel.add(new JLabel("Relative Move"), "");
//        infoPanel.add(new JLabel("Absolute Move"), "");
        add(miscPanel, "wrap");
        add(btnHaltDevices_, "split 3");
        add(btnRefreshNavPanel_, "");
        add(cbxPollPositions_, "");
    }

    private void createEventHandlers() {
        btnRefreshNavPanel_.registerListener(e -> {
            removeAll();
            revalidate();
            repaint();
            init();
        });
        btnHaltDevices_.registerListener(e -> haltAllDevices());
        cbxPollPositions_.registerListener(e -> {
            isPollingPositions = !isPollingPositions;
            System.out.println("isPollingPositions: " + isPollingPositions);
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

    // TODO: what are axis devices? stages? piezos?
    private boolean isAxisDevice() {
        return false;
    }

    // TODO: start and stop timer instead?
    /**
     * Used by the PositionUpdater to update the displayed positions.
     */
    public void update() {
        if (isPollingPositions) {
            //System.out.println("updating!");
            for (ControlPanel panel : controlPanels_) {
                panel.updatePosition();
            }
        }
    }

    // TODO: should i use a thread here? timer fast enough?
    private void startPollingThread() {
        final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                return null;
            }
        };
        worker.execute();
    }

    // FIXME: what to do when ImageJ is closed with X button?
    public void stopTimer() {
        positionUpdater_.stopTimer();
    }

    // TODO: should this be outside of the gui?
    // TODO: keep deviceMap around in DeviceManager?
    public void haltAllDevices() {
        for (ControlPanel controlPanel : controlPanels_) {
            // only try to stop XY and 1D stages
            DeviceType deviceType = controlPanel.getDeviceType();
            if (deviceType == DeviceType.XYStageDevice || deviceType == DeviceType.StageDevice) {
                controlPanel.stop();
            }
        }
    }
}














//    public void init() {
//        Button btnSampleXPlus = new Button("UP");
//        Button btnSampleXMinus = new Button("UP");
//
//        final int numImagingPaths = devices_.getNumImagingPaths();
//
//        // xy stage
//        Panel sampleTranslationPanel = new Panel("Sample Translation", TitledBorder.LEFT);
//
//        Panel sampleXYPanel = new Panel("XY", TitledBorder.LEFT);
//        Panel sampleZPanel = new Panel("Z", TitledBorder.LEFT);
//        Panel incrementPanel = new Panel("Increment (\u00B5m)", TitledBorder.LEFT);
//
//        Button.setDefaultSize(60, 60);
//
//        Button btnSampleXYN = new Button(Icons.ARROW_UP);
//        Button btnSampleXYW  = new Button(Icons.ARROW_RIGHT);
//        Button btnSampleXYE  = new Button(Icons.ARROW_RIGHT);
//        Button btnSampleXYS  = new Button(Icons.ARROW_DOWN);
//        Button btnSampleXYZero = new Button("Zero");
//
//        Button btnSampleZUp  = new Button(Icons.ARROW_UP);
//        Button btnSampleZZero  = new Button("Zero");
//        Button btnSampleZDown  = new Button(Icons.ARROW_DOWN);
//
//        TextField txtIncrementX = new TextField();
//        TextField txtIncrementY = new TextField();
//        TextField txtIncrementZ = new TextField();
//        // TODO: better constructor
//        txtIncrementX.setText("100");
//        txtIncrementY.setText("100");
//        txtIncrementZ.setText("100");
//
//        sampleXYPanel.add(btnSampleXYN, "cell 1 0, wrap");
//        sampleXYPanel.add(btnSampleXYW, "");
//        sampleXYPanel.add(btnSampleXYZero, "");
//        sampleXYPanel.add(btnSampleXYE, "wrap");
//        sampleXYPanel.add(btnSampleXYS, "cell 1 2");
//
//        sampleZPanel.add(btnSampleZUp, "wrap");
//        sampleZPanel.add(btnSampleZZero, "wrap");
//        sampleZPanel.add(btnSampleZDown, "");
//
//        incrementPanel.add(new JLabel("X"));
//        incrementPanel.add(txtIncrementX, "");
//        incrementPanel.add(new JLabel("Y"));
//        incrementPanel.add(txtIncrementY, "");
//        incrementPanel.add(new JLabel("Z"));
//        incrementPanel.add(txtIncrementZ, "");
//        //incrementPanel.add(new JLabel("\u00B5m"));
//
//        sampleTranslationPanel.add(sampleXYPanel, "");
//        sampleTranslationPanel.add(sampleZPanel, "wrap");
//        sampleTranslationPanel.add(incrementPanel, "grow x");
//
//        add(sampleTranslationPanel, "");
//
//        // TODO: check for property with focus in the name?
//        // create z focus buttons for each imaging path
//        Button.setDefaultSize(80, 80);
//        for (int i = 1; i <= numImagingPaths; i++) {
//            final String title = numImagingPaths > 1 ? "Imaging Focus " + i : "Imaging Focus";
//            Panel focusPanel = new Panel(title, TitledBorder.CENTER);
//
//            Button btnFocusZUp = new Button(Icons.ARROW_UP);
//            //Button btnZeroZ = new Button("ZERO");
//            Button btnFocusZDown = new Button(Icons.ARROW_DOWN);
//
//            Spinner spnStepSize = Spinner.createDoubleSpinnner(
//                    100.0, 0.0, Double.MAX_VALUE, 100.0
//            );
//
//            // add ui components
//            focusPanel.add(btnFocusZUp, "center, wrap");
//            //focusPanel.add(btnZeroZ, "center, wrap");
//            focusPanel.add(btnFocusZDown, "center, wrap");
//            focusPanel.add(new JLabel("Increment (\u00B5m)"), "split 2");
//            focusPanel.add(spnStepSize, "");
//            if (i == numImagingPaths) {
//                add(focusPanel, "wrap");
//            } else {
//                add(focusPanel, "");
//            }
//        }
//
//        Panel scannerPanel = new Panel("Scanner", TitledBorder.LEFT);
//
//        Button.setDefaultSize(30, 30);
//        Button btnBeamPlus = new Button(Icons.ARROW_RIGHT);
//        Button btnBeamMinus = new Button(Icons.ARROW_RIGHT);
//        Button btnScannerPlus = new Button(Icons.ARROW_RIGHT);
//        Button btnScannerMinus = new Button(Icons.ARROW_RIGHT);
//
//        TextField txtIncrementBeam = new TextField(3);
//        TextField txtIncrementSlice = new TextField(3);
//        txtIncrementBeam.setText("0.2");
//        txtIncrementSlice.setText("0.2");
//
//        scannerPanel.add(new JLabel("Illumination Beam"), "");
//        scannerPanel.add(txtIncrementBeam, "");
//        scannerPanel.add(new JLabel("\u00B5m"), "");
//        scannerPanel.add(btnBeamMinus, "");
//        scannerPanel.add(btnBeamPlus, "wrap");
//        scannerPanel.add(new JLabel("Illumination Slice"), "");
//        scannerPanel.add(txtIncrementSlice, "");
//        scannerPanel.add(new JLabel("\u00B5m"), "");
//        scannerPanel.add(btnScannerMinus, "");
//        scannerPanel.add(btnScannerPlus, "");
//
//        add(scannerPanel, "");
//    }