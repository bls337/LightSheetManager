package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.LightSheetManagerFrame;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.DeviceManager;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TabbedPane;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The tabbed pane to select between different settings panels.
 */
public class TabPanel extends Panel {

    private NavigationTab navigationTab_;
    private AcquisitionTab acquisitionTab_;
    private AutofocusTab autofocusTab_;
    private CameraTab cameraTab_;
    private SettingsTab settingsTab_;
    private ArrayList<SetupPathTab> setupPathTabs_;

    private final TabbedPane tabbedPane_;

    private final DeviceManager devices_;

    private final LightSheetManager model_;
    private final LightSheetManagerFrame frame_;

    public TabPanel(final LightSheetManager model,
                    final LightSheetManagerFrame frame,
                    final int width, final int height) {
        model_ = Objects.requireNonNull(model);
        frame_ = Objects.requireNonNull(frame);

        devices_ = model_.devices();

        setupPathTabs_ = new ArrayList<>(2);
        tabbedPane_ = new TabbedPane(width, height);

        createUserInterface();
    }

    /**
     * Create the ui.
     */
    private void createUserInterface() {
        // create tabs
        navigationTab_ = new NavigationTab(model_);
        acquisitionTab_ = new AcquisitionTab(model_, frame_);
        autofocusTab_ = new AutofocusTab(model_);
        cameraTab_ = new CameraTab(model_);
        settingsTab_ = new SettingsTab(model_);

        // add tabs to the pane
        String tabName = "Navigation";
        int index = tabbedPane_.getTabCount();
        tabbedPane_.addTab(tabName, navigationTab_);
        tabbedPane_.setTabComponentAt(index, createTabTitle(tabName));

        // create a setup path tab for each imaging path
        final int numImagingPaths = devices_.adapter().numImagingPaths();
        for (int i = 0; i < numImagingPaths; i++) {
            final SetupPathTab setupPathTab = new SetupPathTab(model_, i + 1);
            if (numImagingPaths > 1) {
                tabName = "Setup Path " + (i + 1);
            } else {
                tabName = "Setup Path";
            }
            index = tabbedPane_.getTabCount();
            tabbedPane_.add(tabName, setupPathTab);
            tabbedPane_.setTabComponentAt(index, createTabTitle(tabName));
            setupPathTabs_.add(setupPathTab);
        }

        tabName = "Acquisition";
        index = tabbedPane_.getTabCount();
        tabbedPane_.addTab(tabName, acquisitionTab_);
        tabbedPane_.setTabComponentAt(index, createTabTitle(tabName));

        tabName = "Autofocus";
        index = tabbedPane_.getTabCount();
        tabbedPane_.addTab(tabName, autofocusTab_);
        tabbedPane_.setTabComponentAt(index, createTabTitle(tabName));

        tabName = "Cameras";
        index = tabbedPane_.getTabCount();
        tabbedPane_.addTab(tabName, cameraTab_);
        tabbedPane_.setTabComponentAt(index, createTabTitle(tabName));

        tabName = "Settings";
        index = tabbedPane_.getTabCount();
        tabbedPane_.addTab(tabName, settingsTab_);
        tabbedPane_.setTabComponentAt(index, createTabTitle(tabName));

        // set acquisition tab to default
        final int tabIndex = findTabNameIndex("Acquisition");
        if (tabIndex != -1) {
            tabbedPane_.setSelectedIndex(tabIndex);
        } else {
            tabbedPane_.setSelectedIndex(0); // Navigation Tab
        }
        tabbedPane_.setSelectedIndex(numImagingPaths + 1);

        // add ui elements to the panel
        add(tabbedPane_, "growx, growy");
    }

    /**
     * Returns the index of the tab or -1 if not found.
     *
     * @param tabName the name of the tab
     * @return the index or -1 if not found
     */
    private int findTabNameIndex(final String tabName) {
        for (int i = 0; i < tabbedPane_.getTabCount(); i++) {
            final String title = tabbedPane_.getTitleAt(i);
            if (title.equals(tabName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a styled {@link JLabel} for tab titles.
     *
     * @param title the title text
     * @return a styled component
     */
    private JLabel createTabTitle(final String title) {
        final JLabel label = new JLabel(title);

        // font style
        final Font currentFont = label.getFont();
        label.setFont(new Font(currentFont.getName(), Font.BOLD, 14));
        label.setForeground(Color.BLACK);

        // set the margins
        label.setBorder(new EmptyBorder(8, 10, 5, 10));

        return label;
    }

    /**
     * Swaps the control panel on the setup tabs based on the camera trigger mode.
     *
     * @param cameraMode the selected camera trigger mode
     */
    public void swapSetupPathPanels(final CameraMode cameraMode) {
        for (SetupPathTab setupPathTab : setupPathTabs_) {
            setupPathTab.swapPanels(cameraMode);
        }
    }

    public AcquisitionTab getAcquisitionTab() {
        return acquisitionTab_;
    }

    public SetupPathTab getSetupPathTab(final int view) {
        return setupPathTabs_.get(view-1);
    }

}
