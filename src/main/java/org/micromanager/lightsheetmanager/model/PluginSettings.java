package org.micromanager.lightsheetmanager.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Plugin settings that are not a part of the acquisition settings.
 */
public class PluginSettings {

    private boolean isPollingPositions_ = true;

    private final XYZGrid xyzGrid_ = new XYZGrid();

    public XYZGrid xyzGrid() {
        return xyzGrid_;
    }

    public void setPollingPositions(final boolean state) {
        isPollingPositions_ = state;
    }

    public boolean isPollingPositions() {
        return isPollingPositions_;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String toPrettyJson() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static PluginSettings fromJson(final String json) {
        return new Gson().fromJson(json, PluginSettings.class);
    }

}
