package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsSCAPE;
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
    private Spinner spnScanAngleFirstView_;
    private CheckBox cbxScanFromCurrentPosition_;
    private CheckBox cbxScanNegativeDirection_;
    private CheckBox cbxReturnToOriginalPosition_;

    // light sheet scanner settings
    private Spinner spnSheetAxisFilterFreq_;
    private Spinner spnSliceAxisFilterFreq_;
    private Spinner spnLiveScanPeriod_;

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
        final DefaultAcquisitionSettingsSCAPE acqSettings =
                model_.acquisitions().settings();

        // check for devices to set up the tab
        isUsingPLogic_ = model_.devices().isUsingPLogic();
        isUsingScanSettings_ = model_.devices().isUsingStageScanning();

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
                acqSettings.scanSettings().scanAccelerationFactor(),
                0.1, 1000.0, 1.0);

        spnScanOvershootDist_ = Spinner.createIntegerSpinner(
                acqSettings.scanSettings().scanOvershootDistance(),
                0, 1000, 10);

        spnScanRetraceSpeed_ = Spinner.createDoubleSpinner(
                acqSettings.scanSettings().scanRetraceSpeed(),
                0.01, 99.0, 1.0);

        spnScanAngleFirstView_ = Spinner.createDoubleSpinner(
                acqSettings.scanSettings().scanAngleFirstView(),
                1.0, 89.0, 1.0);

        // Scan CheckBoxes
        cbxScanFromCurrentPosition_ = new CheckBox("Scan from current position instead of center",
                acqSettings.scanSettings().scanFromCurrentPosition());
        cbxScanNegativeDirection_ = new CheckBox("Scan negative direction",
                acqSettings.scanSettings().scanFromNegativeDirection());
        cbxReturnToOriginalPosition_ = new CheckBox("Return to original position after scan",
                acqSettings.scanSettings().scanReturnToOriginalPosition());

        final Panel pnlLightSheet = new Panel("Light Sheet Scanner");
        pnlLightSheet.setMigLayout(
                "",
                "[right]16[center]",
                "[]4[]"
        );

        final JLabel lblSheetAxisFilterFreq = new JLabel("Filter freq, sheet axis [kHz]:");
        final JLabel lblSliceAxisFilterFreq = new JLabel("Filter freq, slice axis [kHz]:");
        final JLabel lblLiveScanPeriod = new JLabel("Live scan period [ms]:");

        // TODO: increase max filter freq based on build (to 10.0)
        spnSheetAxisFilterFreq_ = Spinner.createDoubleSpinner(0.4, 0.1, 1.0, 0.1);
        spnSliceAxisFilterFreq_ = Spinner.createDoubleSpinner(0.4, 0.1, 1.0, 0.1);
        spnLiveScanPeriod_ = Spinner.createIntegerSpinner(20, 2, 10000, 100);

        // scan settings panel
        if (isUsingScanSettings_) {
            pnlScanSettings.add(lblScanAcceleration, "");
            pnlScanSettings.add(spnScanAcceleration_, "wrap");
            pnlScanSettings.add(lblScanOvershootDist, "");
            pnlScanSettings.add(spnScanOvershootDist_, "wrap");
            pnlScanSettings.add(lblScanRetraceSpeed, "");
            pnlScanSettings.add(spnScanRetraceSpeed_, "wrap");
            pnlScanSettings.add(lblScanAngleFirstView, "");
            pnlScanSettings.add(spnScanAngleFirstView_, "wrap");
            pnlScanSettings.add(cbxScanFromCurrentPosition_, "span 2, wrap");
            pnlScanSettings.add(cbxScanNegativeDirection_, "span 2, wrap");
            pnlScanSettings.add(cbxReturnToOriginalPosition_, "span 2, wrap");
        } else {
            pnlScanSettings.add(new JLabel("Stage scanning not supported by your firmware."), "");
        }

        // light sheet scanner settings panel
        if (isUsingPLogic_) {
            pnlLightSheet.add(lblSheetAxisFilterFreq, "");
            pnlLightSheet.add(spnSheetAxisFilterFreq_, "wrap");
            pnlLightSheet.add(lblSliceAxisFilterFreq, "");
            pnlLightSheet.add(spnSliceAxisFilterFreq_, "wrap");
        }
        pnlLightSheet.add(lblLiveScanPeriod, "");
        pnlLightSheet.add(spnLiveScanPeriod_, "");

        add(pnlScanSettings, "wrap");
        add(pnlLightSheet, "growx");
    }

    private void createEventHandlers() {

        // Scan Settings
        if (isUsingScanSettings_) {
            spnScanAcceleration_.registerListener(e ->
                    model_.acquisitions().settingsBuilder().scanSettingsBuilder()
                            .scanAccelerationFactor(spnScanAcceleration_.getDouble()));
            spnScanOvershootDist_.registerListener(e ->
                    model_.acquisitions().settingsBuilder().scanSettingsBuilder()
                            .scanOvershootDistance(spnScanOvershootDist_.getInt()));
            spnScanRetraceSpeed_.registerListener(e ->
                    model_.acquisitions().settingsBuilder().scanSettingsBuilder()
                            .scanRetraceSpeed(spnScanRetraceSpeed_.getDouble()));
            spnScanAngleFirstView_.registerListener(e ->
                    model_.acquisitions().settingsBuilder().scanSettingsBuilder()
                            .scanAngleFirstView(spnScanAngleFirstView_.getDouble()));

            cbxScanFromCurrentPosition_.registerListener(e ->
                    model_.acquisitions().settingsBuilder().scanSettingsBuilder()
                            .scanFromCurrentPosition(cbxScanFromCurrentPosition_.isSelected()));
            cbxScanNegativeDirection_.registerListener(e ->
                    model_.acquisitions().settingsBuilder().scanSettingsBuilder()
                            .scanFromNegativeDirection(cbxScanNegativeDirection_.isSelected()));
            cbxReturnToOriginalPosition_.registerListener(e ->
                    model_.acquisitions().settingsBuilder().scanSettingsBuilder()
                            .scanReturnToOriginalPosition(cbxReturnToOriginalPosition_.isSelected()));
        }

        // ASIScanner Filter Freq
        if (isUsingPLogic_) {
            final ASIScanner scanner = model_.devices()
                    .getDevice("IllumSlice");

            spnSheetAxisFilterFreq_.registerListener(
                    e -> scanner.setFilterFreqX(spnSheetAxisFilterFreq_.getDouble()));

            spnSliceAxisFilterFreq_.registerListener(
                    e -> scanner.setFilterFreqY(spnSliceAxisFilterFreq_.getDouble()));
        }

        spnLiveScanPeriod_.registerListener(
                e -> model_.acquisitions().settingsBuilder()
                        .liveScanPeriod(spnLiveScanPeriod_.getDouble()));

    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }
}
