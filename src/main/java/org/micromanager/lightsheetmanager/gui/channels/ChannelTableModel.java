package org.micromanager.lightsheetmanager.gui.channels;

import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.channels.ChannelTableData;

import javax.swing.table.AbstractTableModel;
import java.util.Objects;

public class ChannelTableModel extends AbstractTableModel {

    /**Column names for the channels table. */
    private final String[] columnNames_ = {
            "Use",
            "Preset",
            "Offset"
    };

    private ChannelTableData tableData_;

    public ChannelTableModel(final ChannelTableData tableData) {
        tableData_ = Objects.requireNonNull(tableData);
    }

    @Override
    public int getRowCount() {
        return tableData_.getChannels().size();
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public int getColumnCount() {
        return columnNames_.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames_[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public Object getValueAt(int row, int col) {
        ChannelSpec channelSpec = tableData_.getChannels().get(row);
        switch (col) {
            case 0:
                return channelSpec.isUsed();
            case 1:
                return channelSpec.getName();
            case 2:
                return channelSpec.getOffset();
            default:
                return null; // FIXME: is this okay? raise exception?
        }
    }

    // TODO: is if (value instanceof Boolean) { needed?

    @Override
    public void setValueAt(Object value, int row, int col) {
        ChannelSpec channelSpec = tableData_.getChannels().get(row);
        switch (col) {
            case 0:
                if (value instanceof Boolean) {
                    channelSpec.setUsed((boolean) value);
                }
                break;
            case 1:
                if (value instanceof String) {
                    channelSpec.setName((String) value);
                }
                break;
            case 2:
                if (value instanceof Double) {
                    channelSpec.setOffset((double) value);
                }
                break;
            default:
                // FIXME: is this okay? raise exception?
                break;
        }
        fireTableCellUpdated(row, col);
    }

}
