package org.micromanager.lightsheetmanager.gui.tabs.setup;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;

import javax.swing.JLabel;
import java.util.Objects;

/**
 * Activate lasers and light sheet.
 */
public class ExcitationPanel extends Panel implements ListeningPanel {

    private CheckBox cbxBeamExc_;
    private CheckBox cbxSheetExc_;
    private CheckBox cbxBeamEpi_;
    private CheckBox cbxSheetEpi_;

    private boolean isUsingPLogic_;

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

        isUsingPLogic_ = model_.devices().isUsingPLogic();

        final JLabel lblExcitation = new JLabel("Excitation side:");
        final JLabel lblEpi = new JLabel("Epi side:");

        cbxBeamExc_ = new CheckBox("Beam", false);
        cbxSheetExc_ = new CheckBox("Sheet", false);
        cbxBeamEpi_ = new CheckBox("Beam", false);
        cbxSheetEpi_ = new CheckBox("Sheet", false);

        if (isUsingPLogic_) {
            final ASIScanner scanner = model_.devices().getDevice("IllumSlice");
            cbxBeamExc_.setSelected(scanner.isBeamOn());
        }

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

        if (isUsingPLogic_) {
            final ASIScanner scanner = model_.devices().getDevice("IllumSlice");

            cbxBeamExc_.registerListener(e -> {
                scanner.setBeamOn(cbxBeamExc_.isSelected());
                System.out.println("set beam on: " + cbxBeamExc_.isSelected());
            });
        }

        cbxSheetExc_.registerListener(e -> {

        });

        cbxBeamEpi_.registerListener(e -> {

        });

        cbxSheetEpi_.registerListener(e -> {

        });
    }

    // TODO: only handles SCAPE for now
    @Override
    public void selected() {
        final boolean isBeamOn = cbxBeamExc_.isSelected();

        if (isUsingPLogic_) {
            final ASIScanner scanner =
                    model_.devices().getDevice("IllumSlice");

            if (scanner != null && isBeamOn && !scanner.isBeamOn()) {
                scanner.setBeamOn(true);
            }
        }
    }

    @Override
    public void unselected() {

    }
}
