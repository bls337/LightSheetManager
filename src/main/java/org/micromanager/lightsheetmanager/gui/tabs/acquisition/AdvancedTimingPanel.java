package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.LightSheetManager;

import java.util.Objects;

public class AdvancedTimingPanel extends Panel {

    private Spinner spnDelayBeforeScan_;
    private Spinner spnDelayBeforeLaser_;
    private Spinner spnDelayBeforeCamera_;
    private Spinner spnLaserTriggerDuration_;
    private Spinner spnCameraTriggerDuration_;
    private Spinner spnScanDuration_;
    private Spinner spnScansPerSlice_;
    private Spinner spnCameraExposure_;

    private CheckBox cbxAlternateScanDirection_;

    private final LightSheetManager model_;

    public AdvancedTimingPanel(final LightSheetManager model) {
        super("Advanced Timing Settings");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        setMigLayout(
            "insets 10 10 10 10",
            "",
            "[]5[]"
        );

        final Label lblDelayBeforeScan = new Label("Delay Before Scan [ms]: ");
        final Label lblDelayBeforeLaser = new Label("Delay Before Laser [ms]: ");
        final Label lblDelayBeforeCamera = new Label("Delay Before Camera [ms]: ");
        final Label lblLaserTriggerDuration = new Label("Laser Trigger Duration [ms]: ");
        final Label lblCameraTriggerDuration = new Label("Camera Trigger Duration [ms]: ");
        final Label lblScanDuration = new Label("Scan Duration [ms]: ");
        final Label lblScansPerSlice = new Label("Scans Per Slice: ");
        final Label lblCameraExposure = new Label("Camera Exposure [ms]: ");

        final DefaultTimingSettings timingSettings = model_.acquisitions()
                .settings().timingSettings();

        spnDelayBeforeScan_ = Spinner.createDoubleSpinner(
                timingSettings.delayBeforeScan(), 0.0, 10000.0, 0.25);
        spnDelayBeforeLaser_ = Spinner.createDoubleSpinner(
                timingSettings.delayBeforeLaser(), 0.0, 10000.0, 0.25);
        spnDelayBeforeCamera_ = Spinner.createDoubleSpinner(
                timingSettings.delayBeforeCamera(), 0.0, 10000.0, 0.25);
        spnLaserTriggerDuration_ = Spinner.createDoubleSpinner(
                timingSettings.laserTriggerDuration(), 0.0, 10000.0, 0.25);
        spnCameraTriggerDuration_ = Spinner.createDoubleSpinner(
                timingSettings.cameraTriggerDuration(), 0.0, 1000.0, 0.25);
        spnScanDuration_ = Spinner.createDoubleSpinner(
                timingSettings.scanDuration(), 0.0, 10000.0, 0.25);
        spnScansPerSlice_ = Spinner.createIntegerSpinner(
                timingSettings.scansPerSlice(), 1, 1000, 1);
        spnCameraExposure_ = Spinner.createDoubleSpinner(
                timingSettings.cameraExposure(), 0.0, 1000.0, 0.25);

        cbxAlternateScanDirection_ = new CheckBox(
                "Alternate scan direction", false, CheckBox.LEFT);

        add(lblDelayBeforeScan, "");
        add(spnDelayBeforeScan_, "wrap");
        add(lblScansPerSlice, "");
        add(spnScansPerSlice_, "wrap");
        add(lblScanDuration, "");
        add(spnScanDuration_, "wrap");
        add(lblDelayBeforeLaser, "");
        add(spnDelayBeforeLaser_, "wrap");
        add(lblLaserTriggerDuration, "");
        add(spnLaserTriggerDuration_, "wrap");
        add(lblDelayBeforeCamera, "");
        add(spnDelayBeforeCamera_, "wrap");
        add(lblCameraTriggerDuration, "");
        add(spnCameraTriggerDuration_, "wrap");
        add(lblCameraExposure, "");
        add(spnCameraExposure_, "wrap");
        add(cbxAlternateScanDirection_, "");
    }

    private void createEventHandlers() {

        spnDelayBeforeScan_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .timingSettingsBuilder().delayBeforeScan(spnDelayBeforeScan_.getDouble());
        });

        spnScansPerSlice_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .timingSettingsBuilder().scansPerSlice(spnScansPerSlice_.getInt());
        });

        spnScanDuration_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .timingSettingsBuilder().scanDuration(spnScanDuration_.getDouble());
        });

        spnDelayBeforeLaser_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .timingSettingsBuilder().delayBeforeLaser(spnDelayBeforeLaser_.getDouble());
        });

        spnLaserTriggerDuration_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .timingSettingsBuilder().laserTriggerDuration(spnLaserTriggerDuration_.getDouble());
        });

        spnDelayBeforeCamera_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .timingSettingsBuilder().delayBeforeCamera(spnDelayBeforeCamera_.getDouble());
        });

        spnCameraTriggerDuration_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .timingSettingsBuilder().cameraTriggerDuration(spnCameraTriggerDuration_.getDouble());
        });

        spnCameraExposure_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .timingSettingsBuilder().cameraExposure(spnCameraExposure_.getDouble());
        });

        cbxAlternateScanDirection_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .timingSettingsBuilder().useAlternateScanDirection(cbxAlternateScanDirection_.isSelected());
        });
    }

    /**
     * Updates the spinner values from the timing settings builder.
     */
    public void updateSpinners() {
        final DefaultTimingSettings timingSettings = model_.acquisitions()
                .settingsBuilder().timingSettingsBuilder().build();
        spnDelayBeforeScan_.setDouble(timingSettings.delayBeforeScan());
        spnScansPerSlice_.setInt(timingSettings.scansPerSlice());
        spnScanDuration_.setDouble(timingSettings.scanDuration());
        spnDelayBeforeLaser_.setDouble(timingSettings.delayBeforeLaser());
        spnLaserTriggerDuration_.setDouble(timingSettings.laserTriggerDuration());
        spnDelayBeforeCamera_.setDouble(timingSettings.delayBeforeCamera());
        spnCameraTriggerDuration_.setDouble(timingSettings.cameraTriggerDuration());
        spnCameraExposure_.setDouble(timingSettings.cameraExposure());
        cbxAlternateScanDirection_.setSelected(timingSettings.useAlternateScanDirection());
    }
}
