package org.micromanager.lightsheetmanager.gui.components;

/**
 * This interface implements methods to be called when the
 * user switches tabs in the UI.
 */
public interface ListeningPanel {

    /**
     * This method is called when switching to a new tab.
     */
    void selected();

    /**
     * This method is called when navigating away from the tab.
     */
    void unselected();

}
