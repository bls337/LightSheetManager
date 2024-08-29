package org.micromanager.lightsheetmanager.gui.tabs.setup;

import com.google.common.eventbus.Subscribe;
import org.micromanager.events.LiveModeEvent;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.ToggleButton;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.cameras.CameraBase;

import java.util.Objects;


/**
 * Select which camera is being used.
 */
public class CameraPanel extends Panel {

    private boolean isPreviewPressed = false;
    private boolean isLivePressed = false;

    private Button btnImagingPath_;
    private Button btnEpiPath_;
    private Button btnMultiPath_;

    private ToggleButton btnInvertedPath_;
    private ToggleButton btnLiveMode_;

    private final LightSheetManager model_;

    public CameraPanel(final LightSheetManager model) {
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

        // register micro-manager events
        model_.studio().events().registerForEvents(this);

        Button.setDefaultSize(80, 26);
        btnImagingPath_ = new Button("Imaging");
        btnMultiPath_ = new Button("Multi");
        btnEpiPath_ = new Button("Epi");

        ToggleButton.setDefaultSize(165, 26);
        btnInvertedPath_ = new ToggleButton(
                "Preview", "Stop Preview",
                Icons.CAMERA, Icons.CANCEL
        );
        btnLiveMode_ = new ToggleButton(
                "Live", "Stop Live",
                Icons.CAMERA, Icons.CANCEL
        );

        switch (geometryType) {
            case DISPIM:
                add(btnImagingPath_, "");
                add(btnMultiPath_, "wrap");
                add(btnEpiPath_, "");
                add(btnInvertedPath_, "wrap");
                add(btnLiveMode_, "span 2");
                break;
            case SCAPE:
                //btnInvertedPath_.setAbsoluteSize(165, 26);
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
                //btnInvertedPath_.setText("Preview");
                btnInvertedPath_.registerListener(e -> {
                    closeLiveModeWindow();
                    final CameraBase camera = model_.devices().getDevice("PreviewCamera");
                    if (camera != null) {
                        try {
                            model_.studio().core().setCameraDevice(camera.getDeviceName());
                            camera.setTriggerMode(CameraMode.INTERNAL);
                        } catch (Exception ex) {
                            model_.studio().logs().showError("could not set camera to " + camera.getDeviceName());
                            return; // early exit
                        }
                        isPreviewPressed = true;
                        model_.studio().live().setLiveModeOn(true);
                    } else {
                        btnInvertedPath_.setState(false);
                        model_.studio().logs().showError(
                                "No device for \"PreviewCamera\" set in the device adapter.");
                    }
                });

                // live mode
                btnLiveMode_.registerListener(e -> {
                    closeLiveModeWindow();
                    final CameraBase camera = model_.devices().getDevice("ImagingCamera");
                    if (camera != null) {
                        try {
                            model_.studio().core().setCameraDevice(camera.getDeviceName());
                            camera.setTriggerMode(CameraMode.INTERNAL);
                        } catch (Exception ex) {
                            model_.studio().logs().showError("could not set camera to " + camera.getDeviceName());
                            return; // early exit
                        }
                        isLivePressed = true;
                        model_.studio().live().setLiveModeOn(true);
                    } else {
                        model_.studio().logs().showError(
                                "No device for \"ImagingCamera\" set in the device adapter.");
                    }
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

    @Subscribe
    public void liveModeListener(LiveModeEvent event) {
        if (!model_.studio().live().isLiveModeOn()) {
            if (isPreviewPressed) {
                isPreviewPressed = false;
                btnInvertedPath_.setState(false);
            }
            if (isLivePressed) {
                isLivePressed = false;
                btnLiveMode_.setState(false);;
            }
        }
    }

}
