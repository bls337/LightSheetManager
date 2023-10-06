package org.micromanager.lightsheetmanager.model.acquisitions;

import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

public class AcquisitionEngineDISPIM extends AcquisitionEngine {

    public AcquisitionEngineDISPIM(final LightSheetManagerModel model) {
        super(model);
    }

    @Override
    boolean setup() {
        return true;
    }

    @Override
    boolean run() {
        runAcquisitionDISPIM();
        return true;
    }

    @Override
    boolean finish() {
        return true;
    }

}
