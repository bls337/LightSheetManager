package org.micromanager.lightsheetmanager.model.autofocus;

import org.micromanager.AutofocusManager;
import org.micromanager.AutofocusPlugin;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.data.AutofocusMode;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsSCAPE;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPiezo;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;

import java.util.Objects;

public class AutofocusRunner {

    private AutofocusResult lastAutofocusResult_;

    private final Studio studio_;
    private final LightSheetManager model_;

    public AutofocusRunner(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);
        studio_ = model_.studio();
    }

    public void runAutofocus() {
        DefaultAcquisitionSettingsSCAPE acqSettings = model_.acquisitions().settings();

        // TODO: make this work for generic devices
        final ASIPiezo piezo = model_.devices().device("ImagingFocus");
        final ASIScanner scanner = model_.devices().device("IllumSlice");

        AutofocusManager afManager = studio_.getAutofocusManager();
        afManager.setAutofocusMethodByName("OughtaFocus");

//        System.out.println("AutofocusMethod: " + afManager.getAutofocusMethod());
//        List<String> autofocusMethods = afManager.getAllAutofocusMethods();
//        for (String method : autofocusMethods) {
//            System.out.println(method);
//        }

        AutofocusPlugin afDevice = afManager.getAutofocusMethod();
        if (afDevice == null) {
            studio_.logs().showError("could not get the autofocus plugin!");
            return; // early exit
        }

        final String scoringAlgorithmName = acqSettings.autofocusSettings()
                .scoringMethod().toString();

        // set scoring algorithm
        try {
            afDevice.setPropertyValue("Maximize", scoringAlgorithmName);
        } catch (Exception e) {
            studio_.logs().showError("could not set property on autofocus device!");
        }
        // make sure that the currently selected MM autofocus device uses the settings in its dialog
        afDevice.applySettings();

        // TODO: get roi

        // TODO: get fit function?
        //final AutofocusFitter.FunctionType function = AutofocusFitter.getFunctionTypeAsType();

        final AutofocusMode afMode = acqSettings.autofocusSettings().mode();
        final boolean isPiezoScan = afMode == AutofocusMode.FIXED_SLICE_SWEEP_PIEZO;

        studio_.logs().logDebugMessage("Autofocus getting ready using \""
                + scoringAlgorithmName + "\" algorithm, mode \"" + afMode + "\"");

        final int numImages = acqSettings.autofocusSettings().numImages();
        final double stepSize = acqSettings.autofocusSettings().stepSizeUm();
        //final double minimumRSquared = acqSettings.autofocusSettings().toleranceUm();

        // TODO: view 1?
        final double imagingCenter = acqSettings.sheetCalibration(1).imagingCenter();

        final boolean isBeamOff = !scanner.isBeamOn();
        scanner.setBeamOn(true);

        // TODO: pause position updates
    }

    public AutofocusResult getLastFocusResult() {
        return lastAutofocusResult_;
    }

}
