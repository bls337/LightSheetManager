package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;
import org.micromanager.lightsheetmanager.LightSheetManager;
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
    private Spinner spnNumSlices_;

    private final LightSheetManager model_;

    public VolumeSettingsPanel(final LightSheetManager model) {
        super("Volume Settings");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final GeometryType geometryType = model_.devices().adapter().geometry();

        final int numImagingPaths = model_.devices().adapter().numImagingPaths();

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
        final Label lblSliceStepSize = new Label("Slice step size [µm]:");

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
                volumeSettings.sliceStepSize(), 0.0, Double.MAX_VALUE, 0.1);
        spnNumSlices_ = Spinner.createIntegerSpinner(
                volumeSettings.slicesPerView(), 1, Integer.MAX_VALUE, 1);

        switch (geometryType) {
            case DISPIM:
                add(lblNumViews, "");
                add(cmbNumViews_, "wrap");
                add(lblFirstView, "");
                add(cmbFirstView_, "wrap");
                add(lblViewDelay, "");
                add(spnViewDelay_, "wrap");
                add(lblSlicesPerView, "");
                add(spnNumSlices_, "wrap");
                add(lblSliceStepSize, "");
                add(spnSliceStepSize_, "");
                break;
            case SCAPE:
                add(lblViewDelay, "");
                add(spnViewDelay_, "wrap");
                add(new Label("Number of slices:"), "");
                add(spnNumSlices_, "wrap");
                add(lblSliceStepSize, "");
                add(spnSliceStepSize_, "");
                break;
            default:
                break;
        }
    }

    private void createEventHandlers() {

        cmbNumViews_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().volumeSettingsBuilder()
                    .numViews(Integer.parseInt(cmbNumViews_.getSelected()));
        });

        cmbFirstView_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().volumeSettingsBuilder()
                    .firstView(Integer.parseInt(cmbFirstView_.getSelected()));
        });

        spnViewDelay_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().volumeSettingsBuilder()
                    .delayBeforeView(spnViewDelay_.getDouble());
            model_.acquisitions().updateDurationLabels();
        });

        spnNumSlices_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().volumeSettingsBuilder()
                    .slicesPerView(spnNumSlices_.getInt());
            model_.acquisitions().updateDurationLabels();
        });

        spnSliceStepSize_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().volumeSettingsBuilder()
                    .sliceStepSize(spnSliceStepSize_.getDouble());
        });
    }
}