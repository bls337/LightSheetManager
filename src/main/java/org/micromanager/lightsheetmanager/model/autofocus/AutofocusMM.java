package org.micromanager.lightsheetmanager.model.autofocus;

import org.micromanager.AutofocusManager;
import org.micromanager.AutofocusPlugin;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.api.internal.DefaultAutofocusSettings;

import java.util.Objects;

/**
 * Use Micro-Manager's built in autofocus method "OughtaFocus".
 */
public class AutofocusMM {

    private static final String AF_METHOD = "OughtaFocus";

    private final LightSheetManager model_;

    public AutofocusMM(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);
    }

    public void run() {
        // get the current autofocus settings
//        final DefaultAutofocusSettings afSettings = model_
//                .acquisitions().settings().autofocusSettings();

        final DefaultAutofocusSettings afSettings = model_
                .acquisitions().settingsBuilder().autofocusSettingsBuilder().build();

        // save the original plugin settings
        final AutofocusManager manager = model_.studio().getAutofocusManager();
        final AutofocusPlugin originalPlugin = manager.getAutofocusMethod();
        String name = originalPlugin.getName();
        System.out.println(name);

        // switch to OughtaFocus plugin
        manager.setAutofocusMethodByName(AF_METHOD);
        final AutofocusPlugin plugin = manager.getAutofocusMethod();
        String name2 = plugin.getName();
        System.out.println(name2);

        // TODO: switch based on AutofocusMode?
        final String deviceName = model_.devices()
                .getDevice("SampleZ").getDeviceName();

        final String deviceName2 = model_.devices()
                .getDevice("IllumSlice").getDeviceName();

        final String deviceName3 = model_.devices()
                .getDevice("IllumFocus").getDeviceName();

        // TODO: is this correct?
        final double exposure = model_.acquisitions()
                .settingsBuilder().timingSettingsBuilder().build()
                .cameraExposure();

        // convert numImages and stepSizeUm to OughtaFocus plugin
        // "SearchRange_um" and "Tolerance_um" properties
        // Note: "Tolerance_um" is used as step size
        final double searchRangeUm = afSettings.numImages() * afSettings.stepSizeUm();
        final double toleranceUm = afSettings.stepSizeUm();
        try {
            plugin.setPropertyValue("OptimizerStrategy", "Z-Stack");
            plugin.setPropertyValue("FocusDrive", deviceName);
            plugin.setPropertyValue("SearchRange_um", Double.toString(searchRangeUm));
            plugin.setPropertyValue("Tolerance_um", Double.toString(toleranceUm));
            plugin.setPropertyValue("CropFactor", "1"); // TODO: what to do here?
            plugin.setPropertyValue("Exposure", "15"); // TODO: get from settings
            plugin.setPropertyValue("FFTLowerCutoff(%)", "2.5");
            plugin.setPropertyValue("FFTUpperCutoff(%)", "14");
            plugin.setPropertyValue("ShowImages", afSettings.showImages() ? "Yes" : "No");
            plugin.setPropertyValue("ShowGraph", afSettings.showGraph() ? "Yes" : "No");
            plugin.setPropertyValue("Maximize", afSettings.scoringMethod().toString());
            plugin.setPropertyValue("Channel", afSettings.channel());
        } catch (Exception e) {
            model_.studio().logs().showError("Error setting " + AF_METHOD + " property!");
        }

        try {
            final double score = plugin.fullFocus();
            System.out.println("score: " + score);
        } catch (Exception e) {
            model_.studio().logs().logError("error running fullFocus() " + e.getMessage());
            return; // early exit => could not run fullFocus()
        }

        // restore original plugin settings
        manager.setAutofocusMethod(originalPlugin);
        String name3 = manager.getAutofocusMethod().getName();
        System.out.println(name3);
    }

    public void setPropertyValue(final AutofocusPlugin plugin, final String key, final String value) {
        try {
            plugin.setPropertyValue(key, value);
        } catch (Exception e) {
            model_.studio().logs().showError("error setting " + AF_METHOD + " property " + key + " to " + value);
        }
    }

//    static class Properties {
//        public static final String OPTIMIZER_STRATEGY = "OptimizerStrategy";
//        public static final String FOCUS_DRIVE = "FocusDrive";
//        public static final String SEARCH_RANGE_UM = "SearchRange_um";
//        public static final String TOLERANCE_UM = "Tolerance_um";
//        public static final String CROP_FACTOR = "CropFactor";
//        public static final String EXPOSURE = "Exposure";
//        public static final String FFT_LOWER_CUTOFF = "FFTLowerCutoff(%)";
//        public static final String FFT_UPPER_CUTOFF = "FFTUpperCutoff(%)";
//    }
//
//    static class Values {
//        public static final String ZSTACK = "Z-Stack";
//    }

}
