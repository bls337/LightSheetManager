package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.tabs.navigation.NavigationPanel;
import org.micromanager.lightsheetmanager.LightSheetManager;

import java.util.Objects;

public class NavigationTab extends Panel implements ListeningPanel {

    private final NavigationPanel navigationPanel_;

    private final LightSheetManager model_;

    public NavigationTab(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);
        navigationPanel_ = new NavigationPanel(model_);
        add(navigationPanel_, "");
    }

    public NavigationPanel getNavigationPanel() {
        return navigationPanel_;
    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }
}
