package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import java.util.Objects;


/**
 * Select which camera is being used.
 */
public class CameraPanel extends Panel {

    private Button btnImagingPath_;
    private Button btnEpiPath_;
    private Button btnMultiPath_;
    private Button btnInvertedPath_;
    private Button btnLiveMode_;

    private LightSheetManagerModel model_;
    public CameraPanel(final LightSheetManagerModel model) {
        super("Cameras");
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        setMigLayout(
                "",
                "[]5[]",
                "[]5[]"
        );

        Button.setDefaultSize(80, 26);
        btnImagingPath_ = new Button("Imaging");
        btnMultiPath_ = new Button("Multi");
        btnEpiPath_ = new Button("Epi");
        btnInvertedPath_ = new Button("Inverted");

        Button.setDefaultSize(165, 26);
        btnLiveMode_ = new Button("Live", Icons.CAMERA);

        switch (geometryType) {
            case DISPIM:
                add(btnImagingPath_, "");
                add(btnMultiPath_, "wrap");
                add(btnEpiPath_, "");
                add(btnInvertedPath_, "wrap");
                add(btnLiveMode_, "span 2");
                break;
            case SCAPE:
                add(btnInvertedPath_, "wrap"); // TODO: change size to match live mode button
                add(btnLiveMode_, "");
                break;
            default:
                break;
        }
    }

    private void createEventHandlers() {
        btnImagingPath_.registerListener(e -> {

        });

        btnMultiPath_.registerListener(e -> {

        });

        btnEpiPath_.registerListener(e -> {

        });

        btnInvertedPath_.registerListener(e -> {

        });

        btnLiveMode_.registerListener(e -> {

        });
    }

}
