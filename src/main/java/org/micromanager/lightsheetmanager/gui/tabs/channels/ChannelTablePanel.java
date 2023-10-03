package org.micromanager.lightsheetmanager.gui.tabs.channels;

import mmcorej.StrVector;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.api.data.MultiChannelMode;

import javax.swing.JLabel;
import java.util.ArrayList;
import java.util.Objects;

public class ChannelTablePanel extends Panel {

    private JLabel lblChannelGroup_;
    private JLabel lblChangeChannel_;

    private Button btnAddChannel_;
    private Button btnRemoveChannel_;
    private Button btnRefresh_;

    private ComboBox cmbChannelGroup_;
    private ComboBox cmbChannelMode_;

    private ChannelTable table_;

    private final LightSheetManagerModel model_;

    public ChannelTablePanel(final LightSheetManagerModel model, final CheckBox checkBox) {
        super(checkBox);
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        lblChannelGroup_ = new JLabel("Channel group:");
        lblChangeChannel_ = new JLabel("Change channel:");

        table_ = new ChannelTable(model_);

        Button.setDefaultSize(72, 24);
        btnAddChannel_ = new Button("Add");
        btnRemoveChannel_ = new Button("Remove");
        btnRefresh_ = new Button("Refresh");

        btnAddChannel_.setToolTipText("Add a new channel to the table.");
        btnRemoveChannel_.setToolTipText("Remove the currently selected channel from the table.");
        btnRefresh_.setToolTipText("Refresh the channel panel with the latest configuration groups settings.");

        final String[] groupLabels = getAvailableGroups();
        cmbChannelGroup_ = new ComboBox(groupLabels, groupLabels[0]);

        cmbChannelMode_ = new ComboBox(MultiChannelMode.toArray(),
                model_.acquisitions().settings().channelMode().toString());

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
        final DefaultAcquisitionSettingsDISPIM.Builder asb = model_.acquisitions().settingsBuilder();

        btnAddChannel_.registerListener(e -> {
            table_.getData().addEmptyChannel();
//            revalidate(); // updates JScrollBar when adding elements
            table_.refreshData();
//            table_.repaint();
//            repaint();
            System.out.println("add channel");
            table_.getData().printChannelData();
            asb.channels(table_.getData().getChannelArray());
        });

        btnRemoveChannel_.registerListener(e -> {
            final int row = table_.getTable().getSelectedRow();
            if (row != -1) {
                table_.getData().removeChannel(row);
                asb.channels(table_.getData().getChannelArray());
                table_.refreshData();
                System.out.println("remove row index: " + row);
            }
        });

        btnRefresh_.registerListener(e -> {
            final Object currentLabel = cmbChannelGroup_.getSelectedItem();
            final String[] groupLabels = getAvailableGroups();
            cmbChannelGroup_.removeAllItems();
            for (String label : groupLabels){
                cmbChannelGroup_.addItem(label);
                //System.out.println(label);
                if (label.equals(currentLabel)) {
                    cmbChannelGroup_.setSelectedItem(currentLabel);
                }
            }
            cmbChannelGroup_.updateUI();
        });

        cmbChannelMode_.registerListener(e -> {
            final int index = cmbChannelMode_.getSelectedIndex();
            asb.channelMode(MultiChannelMode.getByIndex(index));
            //System.out.println("getChannelMode: " + model_.acquisitions().getAcquisitionSettings().getChannelMode());
        });

        cmbChannelGroup_.registerListener(e -> {
            asb.channelGroup(cmbChannelGroup_.getSelected());
            //System.out.println("getChannelGroup: " + model_.acquisitions().getAcquisitionSettings().getChannelGroup());
        });
    }

    // TODO: probably should be in the model not gui
    private String[] getAvailableGroups() {
        StrVector groups;
        try {
            groups = model_.studio().core().getAllowedPropertyValues("Core", "ChannelGroup");
        } catch (Exception e) {
            model_.studio().logs().logError(e);
            return new String[0];
        }
        ArrayList<String> strGroups = new ArrayList<>();
        strGroups.add("None");
        for (String group : groups) {
            if (model_.studio().core().getAvailableConfigs(group).size() > 1) {
                strGroups.add(group);
            }
        }
        return strGroups.toArray(new String[0]);
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
    }

}
