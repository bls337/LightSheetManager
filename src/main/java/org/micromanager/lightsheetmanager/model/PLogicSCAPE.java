package org.micromanager.lightsheetmanager.model;

import mmcorej.CMMCore;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.model.devices.vendor.*;

import java.util.Objects;

public class PLogicSCAPE {

    private Studio studio_;
    private CMMCore core_;

    private DeviceManager devices_;

    private ASIScanner scanner_;
    private ASIPiezo piezo_;
    private ASIXYStage xyStage_;
    private ASIZStage zStage_;
    private ASIPLogic plcCamera_;
    private ASIPLogic plcLaser_;

    private final DefaultAcquisitionSettingsDISPIM.Builder asb_;
    private final LightSheetManagerModel model_;

    public PLogicSCAPE(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        studio_ = model_.studio();
        devices_ = model_.devices();
        core_ = studio_.core();

        asb_ = model_.acquisitions().settingsBuilder();

        // populate devices
        scanner_ = devices_.getDevice("IllumSlice");
        piezo_ = devices_.getDevice("ImagingFocus");
        plcCamera_ = devices_.getDevice("TriggerCamera");
        plcLaser_ = devices_.getDevice("TriggerLaser");
        xyStage_ = devices_.getDevice("SampleXY");
        zStage_ = devices_.getDevice("SampleZ");
    }

}
