package org.micromanager.lightsheetmanager.model;

import mmcorej.org.json.JSONException;
import mmcorej.org.json.JSONObject;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.data.ChannelMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

// TODO: add better validation and error handling

/**
 * An adapter that translates Micro-Manager 1.4 acquisition settings from
 * the SCOPE plugin to Light Sheet Manager settings for Micro-Manager 2.0.
 */
public class SettingsAdapter {

    private final LightSheetManager model_;

    // maps old settings to conversion methods
    private final Map<String, Consumer<Object>> registry = new HashMap<>();

    private static final Map<String, AcquisitionMode> ACQUISITION_MODE_MAP = Map.of(
            "NO_SCAN", AcquisitionMode.NO_SCAN,
            "STAGE_SCAN", AcquisitionMode.STAGE_SCAN,
            "SLICE_SCAN_ONLY", AcquisitionMode.GALVO_SCAN
    );

    private static final Map<String, ChannelMode> CHANNEL_MODE_MAP = Map.of(
            "VOLUME", ChannelMode.VOLUME,
            "VOLUME_HW", ChannelMode.VOLUME_HW,
            "SLICE_HW", ChannelMode.SLICE_HW
    );

    private static final Map<String, CameraMode> CAMERA_MODE_MAP = Map.of(
            "INTERNAL", CameraMode.INTERNAL,
            "EDGE", CameraMode.EDGE,
            "OVERLAP", CameraMode.OVERLAP,
            "LEVEL", CameraMode.LEVEL,
            "PSEUDO_OVERLAP", CameraMode.PSEUDO_OVERLAP,
            "LIGHT_SHEET", CameraMode.VIRTUAL_SLIT
    );

    public SettingsAdapter(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);

        registry.put("spimMode",
                value -> model_.acquisitions().settingsBuilder()
                        .acquisitionMode(convertAcquisitionMode((String) value)));

        registry.put("isStageScanning",
                value -> model_.acquisitions().settingsBuilder()
                        .stageScanBuilder().enabled((boolean) value));

        // isStageStepping - not used, no longer support this kind of stage

        registry.put("useTimepoints",
                value -> model_.acquisitions().settingsBuilder()
                        .useTimePoints((boolean) value));

        registry.put("numTimepoints",
                value -> model_.acquisitions().settingsBuilder()
                        .numTimePoints((int) value));

        registry.put("timepointInterval",
                value -> model_.acquisitions().settingsBuilder()
                        .timePointInterval((double) value));

        registry.put("useMultiPositions",
                value -> model_.acquisitions().settingsBuilder()
                        .useMultiplePositions((boolean) value));

        registry.put("useChannels",
                value -> model_.acquisitions().settingsBuilder()
                        .channelBuilder().enabled((boolean) value));

        registry.put("channelMode",
                value -> model_.acquisitions().settingsBuilder()
                        .channelBuilder().mode(convertChannelMode((String) value)));

        // numChannels - not used, computed by the plugin based on the size of the array

        // TODO: error handling, what happens if the group does not exist, etc?
//        registry.put("channels", value -> {
//            if (value instanceof JSONArray) {
//                var channels = new ArrayList<ChannelSpec>();
//                final JSONArray channelsJson = (JSONArray) value;
//                for (int i = 0; i < channelsJson.length(); i++) {
//                    try {
//                        final JSONObject channelObj = channelsJson.getJSONObject(i);
//
//                        final boolean use = channelObj.getBoolean("useChannel_");
//                        final String group = channelObj.getString("group_");
//                        final String config = channelObj.getString("config_");
//                        final double offset = channelObj.getDouble("offset_");
//
//                        channels.add(new ChannelSpec(use, group, config, offset));
//
//                    } catch (JSONException e) {
//                        model_.studio().logs().logError("Error parsing channel at index " + i);
//                    }
//                }
//
//                // add channels to
//                model_.acquisitions().settingsBuilder()
//                        .channelBuilder().data(channels.toArray(new ChannelSpec[0]));
//            }
//        });

        registry.put("channelGroup",
                value -> model_.acquisitions().settingsBuilder()
                        .channelBuilder().group((String) value));

        registry.put("useAutofocus",
                value -> model_.acquisitions().settingsBuilder()
                        .autofocusBuilder().enabled((boolean) value));

        // useMovementCorrection - not used, old planar correction feature
        // acquireBothCamerasSimultaneously - not used, uses camera order to specify which cameras are active
        // numSides - not used, SCAPE microscopes only have a single view
        // firstSideIsA - not used, SCAPE microscopes only have a single view

        registry.put("delayBeforeSide",
                value -> model_.acquisitions().settingsBuilder()
                        .volumeBuilder().delayBeforeView((double) value));

        registry.put("numSlices",
                value -> model_.acquisitions().settingsBuilder()
                        .volumeBuilder().slicesPerView((int) value));

        registry.put("stepSizeUm",
                value -> model_.acquisitions().settingsBuilder()
                        .volumeBuilder().sliceStepSize((double) value));

        registry.put("minimizeSlicePeriod",
                value -> model_.acquisitions().settingsBuilder()
                        .sliceBuilder().periodMinimized((boolean) value));

        registry.put("desiredSlicePeriod",
                value -> model_.acquisitions().settingsBuilder()
                        .sliceBuilder().period((double) value));

        registry.put("desiredLightExposure",
                value -> model_.acquisitions().settingsBuilder()
                        .sliceBuilder().sampleExposure((double) value));

        // centerAtCurrentZ - not used

        registry.put("sliceTiming", value -> {
            final JSONObject timing = (JSONObject) value;
            final Iterator<String> keys = timing.keys();

            while (keys.hasNext()) {
                final String key = keys.next();
                try {
                    final Object val = timing.get(key);
                    switch (key) {
                        case "scanDelay":
                            model_.acquisitions().settingsBuilder()
                                    .timingBuilder().delayBeforeScan((double) val);
                            break;
                        case "scanNum":
                            model_.acquisitions().settingsBuilder()
                                    .timingBuilder().scansPerSlice((int) val);
                            break;
                        case "scanPeriod":
                            model_.acquisitions().settingsBuilder()
                                    .timingBuilder().scanDuration((double) val);
                            break;
                        case "laserDelay":
                            model_.acquisitions().settingsBuilder()
                                    .timingBuilder().delayBeforeLaser((double) val);
                            break;
                        case "laserDuration":
                            model_.acquisitions().settingsBuilder()
                                    .timingBuilder().laserTriggerDuration((double) val);
                            break;
                        case "cameraDelay":
                            model_.acquisitions().settingsBuilder()
                                    .timingBuilder().delayBeforeCamera((double) val);
                            break;
                        case "cameraDuration":
                            model_.acquisitions().settingsBuilder()
                                    .timingBuilder().cameraTriggerDuration((double) val);
                            break;
                        case "cameraExposure":
                            model_.acquisitions().settingsBuilder()
                                    .timingBuilder().cameraExposure((double) val);
                            break;
                        default:
                            break; // skip key
                    }
                } catch (JSONException e) {
                    model_.studio().logs().showError(
                            "Error parsing the timing settings!");
                }
            }
        });

        registry.put("cameraMode",
                value -> model_.acquisitions().settingsBuilder()
                        .cameraMode(convertCameraMode((String) value)));

        // hardwareTimepoints - computed by the acquisition setup
        // separateTimepoints - not used
        // usePathPresets - not used

        registry.put("useAdvancedSliceTiming",
                value -> model_.acquisitions().settingsBuilder()
                        .useAdvancedTiming((boolean) value));

        // numSimultCameras - this is set as pre-init property in the LSM device adapter

        registry.put("saveDirectoryRoot",
                value -> model_.acquisitions().settingsBuilder()
                        .saveDirectory((String) value));

        registry.put("saveNamePrefix",
                value -> model_.acquisitions().settingsBuilder()
                        .saveNamePrefix((String) value));

        // durationSliceMs - compute after we load the settings
        // durationVolumeMs - compute after we load the settings
        // durationTotalSec - compute after we load the settings
        // pluginVersion - not used
    }

    public void convert(final String json) {
        // convert to JSON object
        JSONObject obj;
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            model_.studio().logs().showError(e,
                    "Failed to convert old settings to JSON!");
            return; // early exit => no json to convert!
        }

        // update settings from the old file
        final Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            if (registry.containsKey(key)) {
                try {
                    final Object value = obj.get(key);
                    registry.get(key).accept(value);
                } catch (Exception e) {
                    model_.studio().logs().logError(
                            "Could not migrate key: " + key + " - " + e.getMessage());
                }
            }
        }

        // build the settings from builder and update the ui
        model_.acquisitions().updateAcquisitionSettings();
        model_.userSettings().notifyListeners(model_.acquisitions().settings());
    }

    private AcquisitionMode convertAcquisitionMode(final String mode) {
        return ACQUISITION_MODE_MAP.getOrDefault(mode, AcquisitionMode.NO_SCAN);
    }

    private ChannelMode convertChannelMode(final String mode) {
        return CHANNEL_MODE_MAP.getOrDefault(mode, ChannelMode.VOLUME);
    }

    private CameraMode convertCameraMode(final String mode) {
        return CAMERA_MODE_MAP.getOrDefault(mode, CameraMode.EDGE);
    }

}
