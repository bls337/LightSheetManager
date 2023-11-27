package org.micromanager.lightsheetmanager;

import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.gui.LightSheetManagerFrame;
import org.micromanager.lightsheetmanager.gui.utils.WindowUtils;
import org.micromanager.MenuPlugin;
import org.micromanager.Studio;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

@Plugin(type = MenuPlugin.class)
public class LightSheetManagerPlugin implements MenuPlugin, SciJavaPlugin {
    public static final String copyright = "Applied Scientific Instrumentation (ASI), 2022-2023";
    public static final String description = "A plugin to control various types of light sheet microscopes.";
    public static final String menuName = "Light Sheet Manager";
    public static final String version = "0.3.5";

    private Studio studio_;
    private LightSheetManagerFrame frame_;
    private LightSheetManagerModel model_;

    @Override
    public void setContext(final Studio studio) {
        this.studio_ = studio;
    }

    @Override
    public String getSubMenu() {
        return "Beta"; // TODO: Change to "Device Control" when out of the Beta stage.
    }

    @Override
    public void onPluginSelected() {
        // only one instance of the plugin can be open
        if (WindowUtils.isOpen(frame_)) {
            WindowUtils.close(frame_);
        }

        // TODO: capture all errors like this?
        try {
            model_ = new LightSheetManagerModel(studio_);
            final boolean isLoaded = model_.setup();
            frame_ = new LightSheetManagerFrame(model_, isLoaded);
            if (isLoaded) {
                model_.acquisitions().setFrame(frame_); // TODO: remove later
            }
            frame_.setVisible(true);
            frame_.toFront();
        } catch (Exception e) {
            if (studio_ != null) {
                studio_.logs().showError(e);
            }
        }
    }

    @Override
    public String getName() {
        return menuName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getCopyright() {
        return copyright;
    }

    @Override
    public String getHelpText() {
        return description;
    }
}
