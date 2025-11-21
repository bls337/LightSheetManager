package org.micromanager.lightsheetmanager.gui.tabs.channels;

import mmcorej.StrVector;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.channels.ChannelTableData;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This is the JTable ui element for channel settings.
 */
public class ChannelTable extends JScrollPane {

    private JTable table_;
    private final JComboBox<String> cmbPresets_;

    private ChannelTableData tableData_;
    private ChannelTableModel tableModel_;

    private final LightSheetManager model_;

    public ChannelTable(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);

        // set the channel group first to get the correct channel array
        final String channelGroup = model_.acquisitions().settings().channelSettings().channelGroup();
        final ChannelSpec[] channels = model_.acquisitions().settings().channelSettings().channels();

        tableData_ = new ChannelTableData(channelGroup, channels);
        tableModel_ = new ChannelTableModel(tableData_);
        table_ = new JTable(tableModel_);

        // init presets combo box
        cmbPresets_ = new JComboBox<>();
        updatePresetComboBoxes(channelGroup);

        // set the editor
        TableColumn column = table_.getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(cmbPresets_));

        // cancel JTable edits when focus is lost to prevent errors
        table_.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // only select a single row at a time
        table_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // disable drag to reorder columns
        table_.getTableHeader().setReorderingAllowed(false);

        // display the JTable in the JScrollPane
        setViewportView(table_);
    }

    /**
     * Load table data from JSON.
     *
     * @param data the channel data
     */
    public void setTableData(final ChannelTableData data) {
        tableData_ = data;
        tableModel_ = new ChannelTableModel(tableData_);
        table_ = new JTable(tableModel_);
    }

    public void refreshData() {
        tableModel_.fireTableDataChanged();
    }

    public ChannelTableData getData() {
        return tableData_;
    }

    public ChannelTableModel getTableModel() {
        return tableModel_;
    }

    public JTable getTable() {
        return table_;
    }

   /**
    * Update the preset combo box with the available configurations
    * for this channel group.
    *
    * @param channelGroup the channel group
    */
    public void updatePresetComboBoxes(final String channelGroup) {
        final String[] presets = getChannelGroupPresets(channelGroup);
        cmbPresets_.removeAllItems();
        for (String preset : presets) {
            cmbPresets_.addItem(preset);
        }
        cmbPresets_.setSelectedItem(channelGroup);
    }

    // TODO: probably should be in the model
    private String[] getChannelGroupPresets(final String configGroup) {
        return model_.studio().core().getAvailableConfigs(configGroup).toArray();
    }

    // TODO: probably should be in the model
    public String[] getChannelGroups() {
        // get all channel groups
        StrVector channelGroups;
        try {
            channelGroups = model_.studio().core()
                  .getAllowedPropertyValues("Core", "ChannelGroup");
        } catch (Exception e) {
            model_.studio().logs().logError(e);
            return new String[0];
        }
        // filter channel groups
        ArrayList<String> groups = new ArrayList<>();
        for (String group : channelGroups) {
            // a channel group must have multiple presets to be detected
            if (model_.studio().core().getAvailableConfigs(group).size() > 1) {
                groups.add(group);
            }
//            StrVector st = model_.studio().core().getAvailableConfigGroups();
//            for (String s : st)
//                System.out.println(s);
        }
        return groups.toArray(String[]::new);
    }

    public void setHeaderRowColor(final boolean state) {
        table_.getTableHeader().setForeground(state ? Color.BLACK : Color.GRAY);
    }

}
