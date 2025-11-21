package org.micromanager.lightsheetmanager.gui.tabs.channels;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.api.data.MultiChannelMode;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

import javax.swing.JLabel;
import java.util.Objects;

/**
 * This panel contains the ChannelTable and controls.
 */
public class ChannelTablePanel extends Panel {

    private JLabel lblChannelGroup_;
    private JLabel lblChangeChannel_;

    private Button btnAddChannel_;
    private Button btnRemoveChannel_;
    private Button btnRefresh_;

    private ComboBox cmbChannelGroup_;
    private ComboBox cmbChannelMode_;

    private final ChannelTable table_;
    private final LightSheetManager model_;

    public ChannelTablePanel(final LightSheetManager model, final CheckBox checkBox) {
        super(checkBox);
        model_ = Objects.requireNonNull(model);
        table_ = new ChannelTable(model_);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        lblChannelGroup_ = new JLabel("Channel group:");
        lblChangeChannel_ = new JLabel("Change channel:");

        btnAddChannel_ = new Button("Add", 74, 24);
        btnRemoveChannel_ = new Button("Remove", 74, 24);
        btnRefresh_ = new Button("Refresh", 74, 24);

        btnAddChannel_.setToolTipText("Add a new channel to the table.");
        btnRemoveChannel_.setToolTipText("Remove the currently selected channel from the table.");
        btnRefresh_.setToolTipText("Refresh the channel panel with the latest configuration groups settings.");

        final String[] groupLabels = table_.getChannelGroups();
        cmbChannelGroup_ = new ComboBox(groupLabels,
                model_.acquisitions().settings().channelSettings().channelGroup(),
                120, 22);

        cmbChannelMode_ = new ComboBox(MultiChannelMode.toArray(),
                model_.acquisitions().settings().channelSettings().channelMode().toString(),
                120, 22);

        add(lblChannelGroup_, "split 2");
        add(cmbChannelGroup_, "wrap");
        add(table_, "wrap");
        add(btnAddChannel_, "split 3");
        add(btnRemoveChannel_, "");
        add(btnRefresh_, "wrap");
        add(lblChangeChannel_, "split 2");
        add(cmbChannelMode_, "");
    }

    private void createEventHandlers() {

        // select channel group
        cmbChannelGroup_.registerListener(e -> {
            final String channelGroup = cmbChannelGroup_.getSelected();
            table_.updatePresetComboBoxes(channelGroup);
            // set the channel group to use when we get the channels
            model_.acquisitions().settingsBuilder().channelSettingsBuilder().channelGroup(channelGroup);
            model_.acquisitions().updateAcquisitionSettings();
            // update the table data model and refresh ui
            table_.getData().setChannels(channelGroup, model_.acquisitions().settings().channelSettings().channels());
            table_.getData().setChannelGroup(channelGroup);
            table_.refreshData();
        });

        // add channel
        btnAddChannel_.registerListener(e -> {
            table_.getTableModel().addEmptyChannel();
            final ChannelSpec[] channels = table_.getData().getChannels();
            model_.acquisitions().settingsBuilder().channelSettingsBuilder().channels(channels);
            //System.out.println("add channel");
            //table_.getData().printChannelData();
        });

        // remove channel
        btnRemoveChannel_.registerListener(e -> {
            final int row = table_.getTable().getSelectedRow();
            if (row != -1) { // is any row selected?
                table_.getTableModel().removeChannel(row);
                final ChannelSpec[] channels = table_.getData().getChannels();
                model_.acquisitions().settingsBuilder().channelSettingsBuilder().channels(channels);
                //System.out.println("remove row index: " + row);
            }
        });

        // refresh channel table
        btnRefresh_.registerListener(e -> {
            final String channelGroup = model_.acquisitions().settings().channelSettings().channelGroup();
            final String[] groups = table_.getChannelGroups();
            cmbChannelGroup_.removeAllItems();
            for (String group : groups) {
                cmbChannelGroup_.addItem(group);
                if (group.equals(channelGroup)) {
                    // the currently selected channel group still exists
                    cmbChannelGroup_.setSelectedItem(channelGroup);
                }
            }
            table_.updatePresetComboBoxes(channelGroup);
            cmbChannelGroup_.updateUI();
        });

        // select channel mode
        cmbChannelMode_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().channelSettingsBuilder()
                  .channelMode(MultiChannelMode.getByIndex(cmbChannelMode_.getSelectedIndex()));
        });

    }

    /**
     * Enable or disable items in the channel table panel.
     *
     * @param state enabled or disabled
     */
    public void setItemsEnabled(final boolean state) {
        lblChannelGroup_.setEnabled(state);
        cmbChannelGroup_.setEnabled(state);
        btnAddChannel_.setEnabled(state);
        btnRemoveChannel_.setEnabled(state);
        lblChangeChannel_.setEnabled(state);
        cmbChannelMode_.setEnabled(state);
        table_.setEnabled(state);
        table_.getTable().setEnabled(state);
        table_.setHeaderRowColor(state);
    }

}
