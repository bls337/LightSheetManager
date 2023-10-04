package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettingsLS;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import java.util.Objects;

public class SliceSettingsPanel extends Panel {

    // regular panel
    private CheckBox cbxMinimizeSlicePeriod_;
    private Label lblSlicePeriod_;
    private Label lblSampleExposure_;
    private Spinner spnSlicePeriod_;
    private Spinner spnSampleExposure_;

    // virtual slit panel
    private Label lblScanResetTime_;
    private Label lblScanSettleTime_;
    private Label lblShutterWidth_;
    private Label lblShutterSpeed_;
    private Spinner spnScanResetTime_;
    private Spinner spnScanSettleTime_;
    private Spinner spnShutterWidth_;
    private Spinner spnShutterSpeed_;

    private final LightSheetManagerModel model_;

    public SliceSettingsPanel(final LightSheetManagerModel model) {
        super("Slice Settings");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final DefaultSliceSettingsLS sliceSettingsLS = model_.acquisitions()
                .settings().sliceSettingsLS();

        final DefaultSliceSettings sliceSettings = model_.acquisitions()
                .settings().sliceSettings();

        final boolean isSlicePeriodMinimized = sliceSettings.isSlicePeriodMinimized();

        // regular panel
        lblSlicePeriod_ = new Label("Slice period [ms]:");
        lblSampleExposure_ = new Label("Sample exposure [ms]:");
        cbxMinimizeSlicePeriod_ = new CheckBox(
                "Minimize slice period", 12, isSlicePeriodMinimized, CheckBox.RIGHT);
        spnSlicePeriod_ = Spinner.createDoubleSpinner(
                sliceSettings.slicePeriod(), 0.0, Double.MAX_VALUE, 0.25);
        spnSampleExposure_ = Spinner.createDoubleSpinner(
                sliceSettings.sampleExposure(), 0.0, Double.MAX_VALUE, 0.25);

        if (isSlicePeriodMinimized) {
            lblSlicePeriod_.setEnabled(false);
            spnSlicePeriod_.setEnabled(false);
        }

        // virtual slit panel
        lblScanResetTime_ = new Label("Scan Reset Time [ms]:");
        lblScanSettleTime_ = new Label("Scan Settle Time [ms]:");
        lblShutterWidth_ = new Label("Shutter Width [\u00B5s]:");
        lblShutterSpeed_ = new Label("1 / (shutter speed):");
        spnScanResetTime_ = Spinner.createDoubleSpinner(
                sliceSettingsLS.scanResetTime(), 1.0, 100.0, 0.25);
        spnScanSettleTime_ = Spinner.createDoubleSpinner(
                sliceSettingsLS.scanSettleTime(), 0.25, 100.0, 0.25);
        spnShutterWidth_ = Spinner.createDoubleSpinner(
                sliceSettingsLS.shutterWidth(),0.1, 100.0, 1.0);
        spnShutterSpeed_ = Spinner.createDoubleSpinner(
                sliceSettingsLS.shutterSpeedFactor(), 1.0, 10.0, 1.0);

        // create the ui based on the camera trigger mode
        switchUI(model_.acquisitions().settings().cameraMode());
    }

    /**
     * Setup event handlers for the regular and virtual slit camera trigger mode versions of the ui.
     */
    private void createEventHandlers() {

        // regular panel
        cbxMinimizeSlicePeriod_.registerListener(e -> {
            final boolean selected = !cbxMinimizeSlicePeriod_.isSelected();
            lblSlicePeriod_.setEnabled(selected);
            spnSlicePeriod_.setEnabled(selected);
            model_.acquisitions().settingsBuilder()
                    .sliceSettingsBuilder().minimizeSlicePeriod(!selected);
            //System.out.println("isSlicePeriodMinimized: " + acqSettings.getSliceSettings().isSlicePeriodMinimized());
        });

        spnSlicePeriod_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .sliceSettingsBuilder().slicePeriod(spnSlicePeriod_.getDouble());
            //System.out.println("slicePeriod: " + acqSettings.getSliceSettings().slicePeriod());
        });

        spnSampleExposure_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .sliceSettingsBuilder().sampleExposure(spnSampleExposure_.getDouble());
            //System.out.println("sampleExposure: " + acqSettings.getSliceSettings().sampleExposure());
        });

        // virtual slit panel
        spnScanResetTime_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .sliceSettingsLSBuilder().scanResetTime(spnScanResetTime_.getDouble());
            //System.out.println("scanResetTime: " + acqSettings.getSliceSettingsLS().scanResetTime());
        });

        spnScanSettleTime_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .sliceSettingsLSBuilder().scanSettleTime(spnScanSettleTime_.getDouble());
            //System.out.println("scanSettleTime: " + acqSettings.getSliceSettingsLS().scanSettleTime());
        });

        spnShutterWidth_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .sliceSettingsLSBuilder().shutterWidth(spnShutterWidth_.getDouble());
            //System.out.println("shutterWidth: " + acqSettings.getSliceSettingsLS().shutterWidth());
        });

        spnShutterSpeed_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .sliceSettingsLSBuilder().shutterSpeedFactor(spnShutterSpeed_.getDouble());
            //System.out.println("shutterSpeedFactor: " + acqSettings.getSliceSettingsLS().shutterSpeedFactor());
        });
    }

    /**
     * Switches the displayed ui based on the camera trigger mode.
     *
     * @param cameraMode the current camera trigger mode
     */
    public void switchUI(final CameraMode cameraMode) {
        removeAll();
        if (cameraMode != CameraMode.VIRTUAL_SLIT) {
            add(cbxMinimizeSlicePeriod_, "wrap");
            add(lblSlicePeriod_, "");
            add(spnSlicePeriod_, "wrap");
            add(lblSampleExposure_, "");
            add(spnSampleExposure_, "wrap");
        } else {
            add(lblScanResetTime_, "");
            add(spnScanResetTime_, "wrap");
            add(lblScanSettleTime_, "");
            add(spnScanSettleTime_, "wrap");
            add(lblShutterWidth_, "");
            add(spnShutterWidth_, "wrap");
            add(lblShutterSpeed_, "");
            add(spnShutterSpeed_, "wrap");
        }
        revalidate();
        repaint();
    }

}
