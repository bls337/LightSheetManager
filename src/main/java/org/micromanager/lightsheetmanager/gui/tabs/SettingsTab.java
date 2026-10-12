package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.ScapeAcquisitionSettings;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;

import javax.swing.JLabel;
import java.util.Objects;

public class SettingsTab extends Panel implements ListeningPanel {

    // scan settings
    private Spinner spnScanAcceleration_;
    private Spinner spnScanOvershootDist_;
    private Spinner spnScanRetraceSpeed_;
    private Spinner spnScanFirstViewAngle_;
    private CheckBox cbxScanFromCurrentPosition_;
    private CheckBox cbxScanNegativeDirection_;
    private CheckBox cbxReturnToStart_;

    // light sheet scanner settings
    private Spinner spnSheetAxisFilterFreq_;
    private Spinner spnSliceAxisFilterFreq_;
    private Spinner spnLiveScanPeriod_;

    private Button btnCreateConfigGroup_;

    private CheckBox cbxAcquireFailQuietly_;

    // changes the ui setup
    private boolean isUsingPLogic_;
    private boolean isUsingScanSettings_;

    private final LightSheetManager model_;

    public SettingsTab(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final ScapeAcquisitionSettings settings = model_.acquisitions().settings();

        // check for devices to set up the tab
        isUsingPLogic_ = model_.devices().isUsingPLogic();
        isUsingScanSettings_ = model_.devices().hasStageScanning();

        btnCreateConfigGroup_ = new Button("Create Devices Configuration Group", 220, 30);
        btnCreateConfigGroup_.setToolTipText("Add each editable Light Sheet Manager device "
                + "adapter property missing from the \"System::Startup\" configuration group, "
                + "with the value Undefined. Existing entries are kept.");

        final JLabel lblGeometryType = new JLabel("Microscope Geometry: "
                + model_.devices().adapter().geometry());
        final JLabel lblLightSheetType = new JLabel("Light Sheet Type: "
                + model_.devices().adapter().lightSheetType());
        final JLabel lblNumImagingPaths = new JLabel("Imaging Paths: "
                + model_.devices().adapter().numImagingPaths());
        final JLabel lblNumIlluminationPaths = new JLabel("Illumination Paths: "
                + model_.devices().adapter().numIlluminationPaths());
        final JLabel lblNumSimultaneousCameras = new JLabel("Simultaneous Cameras: "
                + model_.devices().adapter().numSimultaneousCameras());

        final Panel pnlScanSettings = new Panel("Stage Scan Settings");
        pnlScanSettings.setMigLayout(
                "",
                "[]5[]",
                "[]5[]");

        final JLabel lblScanAcceleration = new JLabel("Relative acceleration time:");
        final JLabel lblScanOvershootDist = new JLabel("Scan overshoot distance [µm]:");
        final JLabel lblScanRetraceSpeed = new JLabel("Scan retrace speed [% of max]:");
        final JLabel lblScanAngleFirstView = new JLabel("Path A stage/objective angle [°]:");

        // Scan Spinners
        spnScanAcceleration_ = Spinner.createDoubleSpinner(
                settings.stageScan().accelerationFactor(),
                0.1, 1000.0, 1.0);

        spnScanOvershootDist_ = Spinner.createIntegerSpinner(
                settings.stageScan().overshootDistance(),
                0, 1000, 10);

        spnScanRetraceSpeed_ = Spinner.createDoubleSpinner(
                settings.stageScan().retraceSpeed(),
                0.01, 99.0, 1.0);

        spnScanFirstViewAngle_ = Spinner.createDoubleSpinner(
                settings.stageScan().firstViewAngle(),
                1.0, 89.0, 0.1);

        // Scan CheckBoxes
        cbxScanFromCurrentPosition_ = new CheckBox("Scan from current position instead of center",
                settings.stageScan().fromCurrentPosition());
        cbxScanNegativeDirection_ = new CheckBox("Scan negative direction",
                settings.stageScan().fromNegativeDirection());
        cbxReturnToStart_ = new CheckBox("Return to original position after scan",
                settings.stageScan().returnToStart());

        spnScanAcceleration_.setToolTipText(
                "Scales the stage acceleration time. Larger values ramp more gently.");
        // these describe the SCAPE stage scan, which the diSPIM engine does not match yet
        if (model_.devices().adapter().geometry() == GeometryType.SCAPE) {
            spnScanFirstViewAngle_.setToolTipText("Angle between the stage and the Path A "
                    + "objective. Stage scans use it to turn the slice step into stage travel.");
            cbxScanFromCurrentPosition_.setToolTipText(
                    "Start the scan at the position instead of centering the scan on it.");
            cbxScanNegativeDirection_.setToolTipText("Scan the stage toward decreasing X.");
            cbxReturnToStart_.setToolTipText("When a stage scan acquisition ends, move the "
                    + "stage back to where it was when the acquisition started.");
        }

        final Panel pnlLightSheet = new Panel("Light Sheet Scanner");
        pnlLightSheet.setMigLayout(
                "",
                "[right]16[center]",
                "[]4[]"
        );

        final JLabel lblSheetAxisFilterFreq = new JLabel("Filter freq, sheet axis [kHz]:");
        final JLabel lblSliceAxisFilterFreq = new JLabel("Filter freq, slice axis [kHz]:");
        final JLabel lblLiveScanPeriod = new JLabel("Live scan period [ms]:");

        // start the filter spinners from the scanner, because a run uses the scanner's values
        double maxFilterFreq = 1.0;
        double sheetAxisFilterFreq = 0.8;
        double sliceAxisFilterFreq = 0.4;
        double sheetAxis = 0.0;
        double sliceAxis = 0.0;
        final ASIScanner scanner = isUsingPLogic_ ? model_.devices().device("IllumSlice") : null;
        if (scanner != null) {
            // the galvo firmware builds can filter up to 10 kHz
            final String build = scanner.getFirmwareBuild();
            if (build.equals("GALVO_SPIM") || build.equals("GALVO_SPIM_SLAVE")) {
                maxFilterFreq = 10.0;
            }
            // a failed read returns 0, so keep the default
            sheetAxis = scanner.getFilterFreqX();
            if (sheetAxis > 0.0) {
                sheetAxisFilterFreq = sheetAxis;
            }
            sliceAxis = scanner.getFilterFreqY();
            if (sliceAxis > 0.0) {
                sliceAxisFilterFreq = sliceAxis;
            }
        }
        spnSheetAxisFilterFreq_ = Spinner.createDoubleSpinner(
                sheetAxisFilterFreq, 0.1, maxFilterFreq, 0.1);
        spnSliceAxisFilterFreq_ = Spinner.createDoubleSpinner(
                sliceAxisFilterFreq, 0.1, maxFilterFreq, 0.1);
        // the spinners clamp a value outside their range and show the default after a failed
        // read, so write what they show when the scanner holds something else
        if (scanner != null) {
            if (spnSheetAxisFilterFreq_.getDouble() != sheetAxis) {
                scanner.setFilterFreqX(spnSheetAxisFilterFreq_.getDouble());
            }
            if (spnSliceAxisFilterFreq_.getDouble() != sliceAxis) {
                scanner.setFilterFreqY(spnSliceAxisFilterFreq_.getDouble());
            }
            // described only here, where there is a scanner for the spinners to write
            spnSheetAxisFilterFreq_.setToolTipText("Bessel filter frequency of the sheet "
                    + "axis, written to the scanner at once. The computed slice timing "
                    + "allows for its delay, except in stage scans.");
            spnSliceAxisFilterFreq_.setToolTipText(
                    "Bessel filter frequency of the slice axis, written to the scanner at once.");
        }
        spnLiveScanPeriod_ = Spinner.createIntegerSpinner(20, 2, 10000, 100);

        add(lblGeometryType, "wrap");
        add(lblLightSheetType, "wrap");
        add(lblNumImagingPaths, "wrap");
        add(lblNumIlluminationPaths, "wrap");
        add(lblNumSimultaneousCameras, "wrap");

        // scan settings panel
        if (isUsingScanSettings_) {
            pnlScanSettings.add(lblScanAcceleration, "");
            pnlScanSettings.add(spnScanAcceleration_, "wrap");
            pnlScanSettings.add(lblScanOvershootDist, "");
            pnlScanSettings.add(spnScanOvershootDist_, "wrap");
            pnlScanSettings.add(lblScanRetraceSpeed, "");
            pnlScanSettings.add(spnScanRetraceSpeed_, "wrap");
        }

        // the grid slice step size is derived from the angle as well, so it stays
        // available even when the stage cannot scan
        pnlScanSettings.add(lblScanAngleFirstView, "");
        pnlScanSettings.add(spnScanFirstViewAngle_, "wrap");

        if (isUsingScanSettings_) {
            pnlScanSettings.add(cbxScanFromCurrentPosition_, "span 2, wrap");
            pnlScanSettings.add(cbxScanNegativeDirection_, "span 2, wrap");
            pnlScanSettings.add(cbxReturnToStart_, "span 2, wrap");
        } else {
            final String reason = model_.devices().hasDevice("SampleXY")
                    ? "Stage scanning not supported by your firmware."
                    : "Stage scanning needs SampleXY to be set.";
            pnlScanSettings.add(new JLabel(reason), "span 2");
        }

        // light sheet scanner settings panel
        if (isUsingPLogic_) {
            pnlLightSheet.add(lblSheetAxisFilterFreq, "");
            pnlLightSheet.add(spnSheetAxisFilterFreq_, "wrap");
            pnlLightSheet.add(lblSliceAxisFilterFreq, "");
            pnlLightSheet.add(spnSliceAxisFilterFreq_, "wrap");
        }

        // changes scan rate for live mode viewing
        if (model_.devices().adapter().geometry() == GeometryType.DISPIM) {
            pnlLightSheet.add(lblLiveScanPeriod, "");
            pnlLightSheet.add(spnLiveScanPeriod_, "");
        }

        cbxAcquireFailQuietly_ = new CheckBox("Acquisition failures are quiet",
                model_.pluginSettings().isAcquireFailQuietly());
        cbxAcquireFailQuietly_.setToolTipText("Write acquisition errors and warnings, "
                + "including a refused start, to the log instead of a dialog. Questions from "
                + "the acquisition take their default answer.");

        add(pnlScanSettings, "wrap");
        if (isUsingPLogic_) {
            add(pnlLightSheet, "growx, wrap");
        }

        add(cbxAcquireFailQuietly_, "wrap");
        add(btnCreateConfigGroup_, "");
    }

    private void createEventHandlers() {

        // Scan Settings
        if (isUsingScanSettings_) {
            spnScanAcceleration_.registerListener(
                    () -> model_.acquisitions().settingsBuilder().stageScanBuilder()
                            .accelerationFactor(spnScanAcceleration_.getDouble()));
            spnScanOvershootDist_.registerListener(
                    () -> model_.acquisitions().settingsBuilder().stageScanBuilder()
                            .overshootDistance(spnScanOvershootDist_.getInt()));
            spnScanRetraceSpeed_.registerListener(
                    () -> model_.acquisitions().settingsBuilder().stageScanBuilder()
                            .retraceSpeed(spnScanRetraceSpeed_.getDouble()));

            cbxScanFromCurrentPosition_.registerListener(
                    () -> model_.acquisitions().settingsBuilder().stageScanBuilder()
                            .fromCurrentPosition(cbxScanFromCurrentPosition_.isSelected()));
            cbxScanNegativeDirection_.registerListener(
                    () -> model_.acquisitions().settingsBuilder().stageScanBuilder()
                            .fromNegativeDirection(cbxScanNegativeDirection_.isSelected()));
            cbxReturnToStart_.registerListener(
                    () -> model_.acquisitions().settingsBuilder().stageScanBuilder()
                            .returnToStart(cbxReturnToStart_.isSelected()));
        }

        // registered outside the block above because the angle spinner is always shown
        spnScanFirstViewAngle_.registerListener(
                () -> model_.acquisitions().settingsBuilder().stageScanBuilder()
                        .firstViewAngle(spnScanFirstViewAngle_.getDouble()));

        // ASIScanner Filter Freq
        if (isUsingPLogic_) {
            final ASIScanner scanner = model_.devices().device("IllumSlice");

            spnSheetAxisFilterFreq_.registerListener(
                    () -> scanner.setFilterFreqX(spnSheetAxisFilterFreq_.getDouble()));

            spnSliceAxisFilterFreq_.registerListener(
                    () -> scanner.setFilterFreqY(spnSliceAxisFilterFreq_.getDouble()));
        }

        cbxAcquireFailQuietly_.registerListener(() -> model_.pluginSettings()
                .setAcquireFailQuietly(cbxAcquireFailQuietly_.isSelected()));

        btnCreateConfigGroup_.registerListener(() -> model_.devices().createConfigGroup());

        // TODO: make this work with diSPIM settings
//        spnLiveScanPeriod_.registerListener(
//                e -> model_.acquisitions().settingsBuilder()
//                        .liveScanPeriod(spnLiveScanPeriod_.getDouble()));

    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }
}
