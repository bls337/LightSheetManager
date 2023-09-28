package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.tabs.navigation.NavigationPanel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import java.util.Objects;

public class NavigationTab extends Panel {

    private final NavigationPanel navigationPanel_;

    private final LightSheetManagerModel model_;

    public NavigationTab(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        navigationPanel_ = new NavigationPanel(model_);
        add(navigationPanel_, "");
    }

    public NavigationPanel getNavigationPanel() {
        return navigationPanel_;
    }

}
