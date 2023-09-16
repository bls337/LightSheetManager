package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JLabel;
import java.util.Objects;

/**
 * Activate lasers and light sheet.
 */
public class ExcitationPanel extends Panel {

    private CheckBox cbxBeamExc_;
    private CheckBox cbxSheetExc_;
    private CheckBox cbxBeamEpi_;
    private CheckBox cbxSheetEpi_;

    private final LightSheetManagerModel model_;

    public ExcitationPanel(final LightSheetManagerModel model) {
        super("Scanner");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        final JLabel lblExcitation = new JLabel("Excitation side:");
        final JLabel lblEpi = new JLabel("Epi side:");

        cbxBeamExc_ = new CheckBox("Beam", false);
        cbxSheetExc_ = new CheckBox("Sheet", false);
        cbxBeamEpi_ = new CheckBox("Beam", false);
        cbxSheetEpi_ = new CheckBox("Sheet", false);

        switch (geometryType) {
            case DISPIM:
                add(lblExcitation, "");
                add(cbxBeamExc_, "");
                add(cbxSheetExc_, "wrap");
                add(lblEpi, "");
                add(cbxBeamEpi_, "");
                add(cbxSheetEpi_, "");
                break;
            case SCAPE:
                add(lblExcitation, "");
                add(cbxBeamExc_, "");
                break;
            default:
                break;
        }
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
