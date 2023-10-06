package org.micromanager.lightsheetmanager.model.acquisitions;

import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

public class AcquisitionEngineSCAPE extends AcquisitionEngine {

    public AcquisitionEngineSCAPE(final LightSheetManagerModel model) {
        super(model);
    }

    @Override
    boolean setup() {
        return true;
    }

    @Override
    boolean run() {
        runAcquisitionSCAPE();
        return true;
    }

    @Override
    boolean finish() {
        return true;
    }
}
