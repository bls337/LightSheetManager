package org.micromanager.lightsheetmanager.gui.components;

import org.micromanager.lightsheetmanager.api.AcquisitionSettings;

@FunctionalInterface
public interface SettingsListener {
    void onSettingsChanged(final AcquisitionSettings settings);
}
