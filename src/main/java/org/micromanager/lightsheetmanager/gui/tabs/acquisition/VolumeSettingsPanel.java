package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;

import java.util.ArrayList;
import java.util.Objects;

public class VolumeSettingsPanel extends Panel {

    private ComboBox cmbNumViews_;
    private ComboBox cmbFirstView_;

    private Spinner spnViewDelay_;
    private Spinner spnSliceStepSize_;
    private Spinner spnSlicesPerSide_;

    private LightSheetManagerModel model_;

    public VolumeSettingsPanel(final LightSheetManagerModel model) {
        super("Volume Settings");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        final int numImagingPaths = model_.devices()
                .getDeviceAdapter().getNumImagingPaths();

        final DefaultVolumeSettings volumeSettings = model_.acquisitions()
                .settings().volumeSettings();

        // create labels for combo boxes
        ArrayList<String> labels = new ArrayList<>(numImagingPaths);
        for (int i = 0; i < numImagingPaths; i++) {
            labels.add(String.valueOf(i+1));
        }
        final String[] lbls = labels.toArray(new String[0]);

        final Label lblNumViews = new Label("Number of views:");
        final Label lblFirstView = new Label("First view:");
        final Label lblViewDelay = new Label("Delay before view [ms]:");
        final Label lblSlicesPerView = new Label("Slices per view:");
        final Label lblSliceStepSize = new Label("Slice step size [\u00B5m]:");

        // if the number of sides has changed and the firstView or numViews is larger
        // than the number of sides, default to 1.
        int numViews = volumeSettings.numViews();
        int firstView = volumeSettings.firstView();
        if (numViews > labels.size()) {
            numViews = 1;
        }
        if (firstView > labels.size()) {
            firstView = 1;
        }

        cmbNumViews_ = new ComboBox(lbls, String.valueOf(numViews), 60, 20);
        cmbFirstView_ = new ComboBox(lbls, String.valueOf(firstView), 60, 20);

        spnViewDelay_ = Spinner.createDoubleSpinner(
                volumeSettings.delayBeforeView(), 0.0, Double.MAX_VALUE, 0.25);
        spnSliceStepSize_ = Spinner.createDoubleSpinner(
                volumeSettings.sliceStepSize(), 0.0, 100.0, 0.1);
        spnSlicesPerSide_ = Spinner.createIntegerSpinner(
                volumeSettings.slicesPerView(), 0, 100, 1);

        switch (geometryType) {
            case DISPIM:
                add(lblNumViews, "");
                add(cmbNumViews_, "wrap");
                add(lblFirstView, "");
                add(cmbFirstView_, "wrap");
                add(lblViewDelay, "");
                add(spnViewDelay_, "wrap");
                add(lblSlicesPerView, "");
                add(spnSlicesPerSide_, "wrap");
                add(lblSliceStepSize, "");
                add(spnSliceStepSize_, "");
                break;
            case SCAPE:
                add(lblViewDelay, "");
                add(spnViewDelay_, "wrap");
                add(new Label("Number of slices:"), "");
                add(spnSlicesPerSide_, "wrap");
                add(lblSliceStepSize, "");
                add(spnSliceStepSize_, "");
                break;
            default:
                break;
        }
    }

    private void createEventHandlers() {
        final DefaultVolumeSettings.Builder vsb = model_.acquisitions()
                .getAcquisitionSettingsBuilder().volumeSettingsBuilder();

        cmbNumViews_.registerListener(e -> {
            vsb.numViews(Integer.parseInt(cmbNumViews_.getSelected()));
        });

        cmbFirstView_.registerListener(e -> {
            vsb.firstView(Integer.parseInt(cmbFirstView_.getSelected()));
        });

        spnViewDelay_.registerListener(e -> {
            vsb.delayBeforeView(spnViewDelay_.getDouble());
        });

        spnSlicesPerSide_.registerListener(e -> {
            vsb.slicesPerVolume(spnSlicesPerSide_.getInt());
        });

        spnSliceStepSize_.registerListener(e -> {
            vsb.sliceStepSize(spnSliceStepSize_.getDouble());
        });
    }
}