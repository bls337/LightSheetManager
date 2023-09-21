package org.micromanager.lightsheetmanager.gui.frames;

import net.miginfocom.swing.MigLayout;
import org.micromanager.lightsheetmanager.gui.tabs.navigation.NavigationPanel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.internal.utils.WindowPositioning;

import javax.swing.JFrame;

public class NavigationFrame extends JFrame {

    private NavigationPanel navigationPanel_;

    public NavigationFrame(final LightSheetManagerModel model) {
        WindowPositioning.setUpBoundsMemory(this, this.getClass(), this.getClass().getSimpleName());
        setLayout(new MigLayout("", "", ""));
        navigationPanel_ = new NavigationPanel(model);
        add(navigationPanel_, "");
    }

    public void init() {
        navigationPanel_.createUserInterface();
    }

    public void stopTimer() {
        navigationPanel_.stopPolling();
    }
}
