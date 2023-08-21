package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;

import javax.swing.JLabel;

/**
 * Activate lasers and light sheet.
 */
public class ExcitationPanel extends Panel {

    private CheckBox cbxBeamExc_;
    private CheckBox cbxSheetExc_;
    private CheckBox cbxBeamEpi_;
    private CheckBox cbxSheetEpi_;

    public ExcitationPanel() {
        super("Scanner");
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final JLabel lblExcitation = new JLabel("Excitation side:");
        final JLabel lblEpi = new JLabel("Epi side:");

        cbxBeamExc_ = new CheckBox("Beam", false);
        cbxSheetExc_ = new CheckBox("Sheet", false);
        cbxBeamEpi_ = new CheckBox("Beam", false);
        cbxSheetEpi_ = new CheckBox("Sheet", false);

        add(lblExcitation, "");
        add(cbxBeamExc_, "");
        add(cbxSheetExc_, "wrap");
        add(lblEpi, "");
        add(cbxBeamEpi_, "");
        add(cbxSheetEpi_, "");
    }

    private void createEventHandlers() {
        cbxBeamExc_.registerListener(e -> {

        });

        cbxSheetExc_.registerListener(e -> {

        });

        cbxBeamEpi_.registerListener(e -> {

        });

        cbxSheetEpi_.registerListener(e -> {

        });
    }
}
