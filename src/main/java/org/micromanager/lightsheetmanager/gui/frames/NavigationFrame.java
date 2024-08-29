package org.micromanager.lightsheetmanager.gui.frames;

import net.miginfocom.swing.MigLayout;
import org.micromanager.lightsheetmanager.gui.tabs.navigation.NavigationPanel;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.internal.utils.WindowPositioning;

import javax.swing.JFrame;

public class NavigationFrame extends JFrame {

    private NavigationPanel navigationPanel_;

    public NavigationFrame(final LightSheetManager model) {
        WindowPositioning.setUpBoundsMemory(this, this.getClass(), this.getClass().getSimpleName());
        setLayout(new MigLayout("", "", ""));
        navigationPanel_ = new NavigationPanel(model);
        add(navigationPanel_, "");
    }

    // TODO: this would need ui and events
    public void init() {
        //navigationPanel_.createUserInterface();
    }

}
