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

public class ChannelTable extends JScrollPane {

    private JTable table_;
    private JComboBox<String> cmbPresets_;
    private ChannelTableData tableData_;
    private ChannelTableModel tableModel_;

    private final LightSheetManager model_;

    public ChannelTable(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);

        final String channelGroup = model_.acquisitions().settings().channelGroup();
        final ChannelSpec[] channels = model_.acquisitions().settings().channels();

        tableData_ = new ChannelTableData(channels, channelGroup);
        tableModel_ = new ChannelTableModel(tableData_);
        table_ = new JTable(tableModel_);

        // init presets combo box
        TableColumn column = table_.getColumnModel().getColumn(1);
        cmbPresets_ = new JComboBox<>();

        final String[] presets = getAllPresets(channelGroup);
        for (String preset : presets) {
            cmbPresets_.addItem(preset);
        }
        //cmbPresets.addItem("None");
        //cmbPresets_.setSelectedItem(presets[0]);
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
        this.tableData_ = data;
        tableModel_ = new ChannelTableModel(tableData_);
        table_ = new JTable(tableModel_);
    }

    public void refreshData() {
        tableModel_.fireTableDataChanged();
    }

    public ChannelTableData getData() {
        return tableData_;
    }

    public JTable getTable() {
        return table_;
    }

    public void updatePresetCombos(final String channelGroup) {
        final String[] presets = getAllPresets(channelGroup);
        cmbPresets_.removeAllItems();
        for (String preset : presets) {
            cmbPresets_.addItem(preset);
        }
        cmbPresets_.setSelectedItem(channelGroup);
    }

    // TODO: probably should be in the model
    private String[] getAllPresets(final String configGroup) {
        return model_.studio().core().getAvailableConfigs(configGroup).toArray();
    }

    // TODO: probably should be in the model
    public String[] getAvailableGroups() {
        StrVector groups;
        try {
            groups = model_.studio().core().getAllowedPropertyValues("Core", "ChannelGroup");
        } catch (Exception e) {
            model_.studio().logs().logError(e);
            return new String[0];
        }
        ArrayList<String> strGroups = new ArrayList<>();
        // strGroups.add("None");
        for (String group : groups) {
//            StrVector st = model_.studio().core().getAvailableConfigGroups();
//            for (String s : st)
//                System.out.println(s);
            // a channel group must have multiple presets to be detected
            if (model_.studio().core().getAvailableConfigs(group).size() > 1) {
                strGroups.add(group);
            }
        }
        return strGroups.toArray(new String[0]);
    }

    public void setHeaderRowColor(final boolean state) {
        table_.getTableHeader().setForeground(state ? Color.BLACK : Color.GRAY);
    }

}
