package org.micromanager.lightsheetmanager.model;

import org.micromanager.AutofocusManager;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;

import java.util.List;
import java.util.Objects;

public class AutofocusRunner {

    private AutofocusResult lastAutofocusResult_;

    private final Studio studio_;
    private final LightSheetManagerModel model_;

    public AutofocusRunner(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        studio_ = model_.studio();

    }

    public void runAutofocus() {
        DefaultAcquisitionSettingsDISPIM acqSettings = model_.getAcquisitionEngine().getAcquisitionSettings();

        AutofocusManager afManager = studio_.getAutofocusManager();
        List<String> autofocusMethods = afManager.getAllAutofocusMethods();
        for (String method : autofocusMethods) {
            System.out.println(method);
        }
    }

    public AutofocusResult getLastFocusResult() {
        return lastAutofocusResult_;
    }

}
