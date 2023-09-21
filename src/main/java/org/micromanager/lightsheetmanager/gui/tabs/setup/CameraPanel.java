package org.micromanager.lightsheetmanager.gui.tabs.setup;

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

    private final LightSheetManagerModel model_;
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
                btnInvertedPath_.setAbsoluteSize(165, 26);
                add(btnInvertedPath_, "wrap");
                add(btnLiveMode_, "");
                break;
            default:
                break;
        }
    }

    private void createEventHandlers() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        switch (geometryType) {
            case DISPIM:
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
                break;
            case SCAPE:
                btnInvertedPath_.registerListener(e -> {
                    // TODO: make this work, needs Device Adapter pull request and name for camera...
                    closeLiveModeWindow();
                    final String cameraName = model_.devices()
                            .getDevice("InvertedCamera").getDeviceName();
                    try {
                        model_.studio().core().setCameraDevice(cameraName);
                    } catch (Exception ex) {
                        model_.studio().logs().showError("could not set camera to " + cameraName);
                    }
                    model_.studio().live().setLiveModeOn(true);
                });

                btnLiveMode_.registerListener(e -> {
                    closeLiveModeWindow();
                    final String cameraName = model_.devices()
                            .getDevice("ImagingCamera").getDeviceName();
                    try {
                        model_.studio().core().setCameraDevice(cameraName);
                    } catch (Exception ex) {
                        model_.studio().logs().showError("could not set camera to " + cameraName);
                    }
                    model_.studio().live().setLiveModeOn(true);
                });
                break;
            default:
                break;
        }
    }

    /**
     * Closes the Live Mode window if it exists.
     */
    private void closeLiveModeWindow() {
        final boolean isLiveModeOn = model_.studio().live().isLiveModeOn();
        if (isLiveModeOn) {
            model_.studio().live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (model_.studio().live().getDisplay() != null) {
                model_.studio().live().getDisplay().close();
            }
        }
    }

}
