package org.micromanager.lightsheetmanager.gui.tabs.channels;

import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.channels.ChannelTableData;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import java.util.Objects;

public class ChannelTable extends JScrollPane {

    private JTable table_;
    private ChannelTableData tableData_;
    private ChannelTableModel tableModel_;

    private LightSheetManagerModel model_;

    public ChannelTable(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);

        final ChannelSpec[] channels = model_.acquisitions().settings().channels();

        tableData_ = new ChannelTableData(channels);
        tableModel_ = new ChannelTableModel(tableData_);
        table_ = new JTable(tableModel_);

        // init presets combo box
        TableColumn column = table_.getColumnModel().getColumn(1);
        JComboBox<String> cmbPresets = new JComboBox<>();
        cmbPresets.addItem("None");
        cmbPresets.setSelectedIndex(0);
        column.setCellEditor(new DefaultCellEditor(cmbPresets));

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

}
