package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.api.internal.DefaultScanSettings;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JLabel;
import java.util.Objects;

public class SettingsTab extends Panel {

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

    private LightSheetManagerModel model_;

    public SettingsTab(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final DefaultAcquisitionSettingsDISPIM acqSettings =
                model_.acquisitions().getAcquisitionSettings();

        final Panel pnlScanSettings = new Panel("Stage Scan Settings");
        pnlScanSettings.setMigLayout(
                "",
                "[]5[]",
                "[]5[]");

        final JLabel lblScanAcceleration = new JLabel("Relative acceleration time:");
        final JLabel lblScanOvershootDist = new JLabel("Scan overshoot distance [" + "\u00B5"+ "m]:");
        final JLabel lblScanRetraceSpeed = new JLabel("Scan retrace speed [% of max]:");
        final JLabel lblScanAngleFirstView = new JLabel("Path A stage/objective angle [\u00B0]:");

        // Spinners
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

        // CheckBoxes
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
        pnlScanSettings.add(lblScanAcceleration, "");
        pnlScanSettings.add(spnScanAcceleration_, "wrap");
        pnlScanSettings.add(lblScanOvershootDist, "");
        pnlScanSettings.add(spnScanOvershootDist_, "wrap");
        pnlScanSettings.add(lblScanRetraceSpeed, "");
        pnlScanSettings.add(spnScanRetraceSpeed_, "wrap");
        pnlScanSettings.add(lblScanAngleFirstView, "");
        pnlScanSettings.add(spnScanAngleFirstView_, "wrap");
        pnlScanSettings.add(cbxScanFromCurrentPosition_, "wrap");
        pnlScanSettings.add(cbxScanNegativeDirection_, "wrap");
        pnlScanSettings.add(cbxReturnToOriginalPosition_, "wrap");

        // light sheet scanner settings panel
        pnlLightSheet.add(lblSheetAxisFilterFreq, "");
        pnlLightSheet.add(spnSheetAxisFilterFreq_, "wrap");
        pnlLightSheet.add(lblSliceAxisFilterFreq, "");
        pnlLightSheet.add(spnSliceAxisFilterFreq_, "wrap");
        pnlLightSheet.add(lblLiveScanPeriod, "");
        pnlLightSheet.add(spnLiveScanPeriod_, "");

        add(pnlLightSheet, "growx, wrap");
        //add(pnlScanSettings, "");
    }

    private void createEventHandlers() {
        final DefaultScanSettings.Builder scsb = model_.acquisitions()
                .getAcquisitionSettingsBuilder().scanSettingsBuilder();

        // Scan Settings
        spnScanAcceleration_.registerListener(e ->
                scsb.scanAccelerationFactor(spnScanAcceleration_.getDouble()));
        spnScanOvershootDist_.registerListener(e ->
                scsb.scanOvershootDistance(spnScanOvershootDist_.getInt()));
        spnScanRetraceSpeed_.registerListener(e ->
                scsb.scanRetraceSpeed(spnScanRetraceSpeed_.getDouble()));
        spnScanAngleFirstView_.registerListener(e ->
                scsb.scanAngleFirstView(spnScanAngleFirstView_.getDouble()));

        cbxScanFromCurrentPosition_.registerListener(e ->
                scsb.scanFromCurrentPosition(cbxScanFromCurrentPosition_.isSelected()));
        cbxScanNegativeDirection_.registerListener(e ->
                scsb.scanFromNegativeDirection(cbxScanNegativeDirection_.isSelected()));
        cbxReturnToOriginalPosition_.registerListener(e ->
                scsb.scanReturnToOriginalPosition(cbxReturnToOriginalPosition_.isSelected()));

        // TODO: set these settings
        // Light Sheet Scanner
        spnSheetAxisFilterFreq_.registerListener(e -> {

        });

        spnSliceAxisFilterFreq_.registerListener(e -> {

        });

        spnLiveScanPeriod_.registerListener(e -> {

        });

    }
}
