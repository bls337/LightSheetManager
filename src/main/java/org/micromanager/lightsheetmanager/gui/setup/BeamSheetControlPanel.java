package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Slider;
import org.micromanager.lightsheetmanager.gui.components.TextField;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JLabel;
import javax.swing.UIManager;
import java.util.Objects;

public class BeamSheetControlPanel extends Panel {

    // first panel
    private TextField txtSlope_;
    private TextField txtOffset_;
    private Button btnPlotProfile_;
    private JLabel lblSlopeUnits2_;

    // second panel
    private CheckBox cbxAutoSheetWidth_;
    private TextField txtSheetWidth_;
    private Button btnCenterOffset_;

    private Button btnSheetWidthMinus_;
    private Button btnSheetWidthPlus_;

    private Button btnSheetOffsetMinus_;
    private Button btnSheetOffsetPlus_;

    private Slider sldSheetWidth_;
    private Slider sldSheetOffset_;

    // layout panels
    private Panel pnlFirst_;  // virtual slit camera trigger mode active
    private Panel pnlSecond_; // all other camera trigger modes

    // SCAPE only
    private JLabel lblSlopeOffset_;
    private TextField txtSheetOffset_;

    private LightSheetManagerModel model_;

    public BeamSheetControlPanel(final LightSheetManagerModel model) {
        super("Light Sheet Synchronization");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        pnlFirst_ = new Panel();
        pnlSecond_ = new Panel();

        // first panel for virtual slit mode
        final JLabel lblSlope = new JLabel("Speed / slope:");
        final JLabel lblOffset = new JLabel("Start / offset:");
        final JLabel lblSlopeUnits = new JLabel("μ°/px");
        final JLabel lblOffsetUnits = new JLabel("m°");

        btnPlotProfile_ = new Button("Plot Profile", 100, 26);
        txtSlope_ = new TextField();
        txtOffset_ = new TextField();

        // second panel for other camera trigger modes
        final JLabel lblSheetWidth = new JLabel("Sheet width:");
        final JLabel lblSheetOffset = new JLabel("Sheet offset:");
        lblSlopeUnits2_ = new JLabel("μ°/px"); // TODO: reuse labels like this?

        cbxAutoSheetWidth_ = new CheckBox("Automatic", false);
        txtSheetWidth_ = new TextField();
        btnCenterOffset_ = new Button("Center", 100, 26);

        Button.setDefaultSize(40, 30);
        btnSheetWidthMinus_ = new Button("-");
        btnSheetWidthPlus_ = new Button("+");
        btnSheetOffsetMinus_ = new Button("-");
        btnSheetOffsetPlus_ = new Button("+");

        // TODO: set the ranges of these sliders to the micro-mirror's min and max deflection
        UIManager.put("Slider.focus", UIManager.get("Slider.background")); // remove highlight when clicked
        sldSheetWidth_ = new Slider(0, 8, 4);
        sldSheetOffset_ = new Slider(-1, 1, 0);

        // virtual slit trigger mode
        pnlFirst_.add(lblSlope, "");
        pnlFirst_.add(txtSlope_, "");
        pnlFirst_.add(lblSlopeUnits, "wrap");
        pnlFirst_.add(lblOffset, "");
        pnlFirst_.add(txtOffset_, "");
        pnlFirst_.add(lblOffsetUnits, "");
        pnlFirst_.add(btnPlotProfile_, "gapleft 100");

        // regular trigger modes
        switch (geometryType) {
            case DISPIM:
                pnlSecond_.add(lblSheetWidth, "");
                pnlSecond_.add(cbxAutoSheetWidth_, "");
                pnlSecond_.add(txtSheetWidth_, "");
                pnlSecond_.add(lblSlopeUnits2_, "");
                pnlSecond_.add(btnSheetWidthMinus_, "");
                pnlSecond_.add(btnSheetWidthPlus_, "");
                pnlSecond_.add(sldSheetWidth_, "wrap");
                pnlSecond_.add(lblSheetOffset, "span 3");
                pnlSecond_.add(btnCenterOffset_, "");
                pnlSecond_.add(btnSheetOffsetMinus_, "");
                pnlSecond_.add(btnSheetOffsetPlus_, "");
                pnlSecond_.add(sldSheetOffset_, "");
                break;
            case SCAPE:
                txtSheetOffset_ = new TextField(8);
                lblSlopeOffset_ = new JLabel("0.0 μm");
                pnlSecond_.add(lblSheetOffset, "");
                pnlSecond_.add(lblSlopeOffset_, "");
                pnlSecond_.add(txtSheetOffset_, "span 2, wrap");
                pnlSecond_.add(btnCenterOffset_, "");
                pnlSecond_.add(btnSheetOffsetMinus_, "split 2");
                pnlSecond_.add(btnSheetOffsetPlus_, "");
                pnlSecond_.add(sldSheetOffset_, "");
                break;
            default:
                break;
        }

        // add panel based on camera trigger mode
        final CameraModes cameraMode =
                model_.acquisitions().getAcquisitionSettingsBuilder().cameraMode();
        if (cameraMode == CameraModes.VIRTUAL_SLIT) {
            add(pnlFirst_, "");
        } else {
            add(pnlSecond_, "");
        }
    }

    private void createEventHandlers() {
        // first panel
        btnPlotProfile_.registerListener(e -> {
            System.out.println("do something here...");
        });

        // second panel
        cbxAutoSheetWidth_.registerListener(e -> {
            setEnabledSheetWidth(cbxAutoSheetWidth_.isSelected());
        });

        btnCenterOffset_.registerListener(e -> {
            System.out.println("center offset pressed");
        });

    }

    /**
     * This is called when the camera trigger mode changes and updates the controls accordingly.
     */
    public void swapPanels(final CameraModes cameraMode) {
        if (cameraMode != CameraModes.VIRTUAL_SLIT) {
            remove(pnlFirst_);
            add(pnlSecond_, "");
        } else {
            remove(pnlSecond_);
            add(pnlFirst_, "");
        }
        revalidate();
        repaint();
    }

    /**
     * Enable or disable the sheet width controls based on the "Automatic" checkbox.
     *
     * @param state true to enable the ui components
     */
    private void setEnabledSheetWidth(final boolean state) {
        txtSheetWidth_.setEnabled(state);
        lblSlopeUnits2_.setEnabled(state);
        btnSheetWidthMinus_.setEnabled(!state);
        btnSheetWidthPlus_.setEnabled(!state);
        sldSheetWidth_.setEnabled(!state);
    }

}
