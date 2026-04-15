package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.internal.utils.FileDialogs;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.LightSheetManagerFrame;
import org.micromanager.lightsheetmanager.api.AcquisitionSettings;
import org.micromanager.lightsheetmanager.api.internal.ScapeAcquisitionSettings;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.SettingsListener;
import org.micromanager.lightsheetmanager.gui.components.TextField;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.model.DataStorage;
import org.micromanager.lightsheetmanager.model.utils.FileUtils;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SavePanel extends Panel implements SettingsListener {

    private TextField txtSaveDirectory_;
    private TextField txtSaveFileName_;

    private Button btnBrowse_;
    private Button btnOpen_;

    private ComboBox<DataStorage.SaveMode> cbxSaveMode_;
    private CheckBox cbxSaveWhileAcquiring_;

    private Button btnSaveSettings_;
    private Button btnLoadSettings_;

    private final FileDialogs.FileType directorySelect_;
    private final FileDialogs.FileType jsonFileSave_;
    private final FileDialogs.FileType jsonFileLoad_;

    private final LightSheetManager model_;
    private final LightSheetManagerFrame frame_;

    public SavePanel(final LightSheetManager model, final LightSheetManagerFrame frame) {
        super("Save Settings");
        model_ = Objects.requireNonNull(model);
        frame_ = Objects.requireNonNull(frame);

        directorySelect_ = new FileDialogs.FileType(
                "SAVE_DIRECTORY",
                "All Directories",
                "",
                false,
                ""
        );

        jsonFileSave_ = new FileDialogs.FileType(
                "SAVE_FILE",
                "JSON Files",
                "acq_settings.json",
                false,
                "json"
        );

        jsonFileLoad_ = new FileDialogs.FileType(
                "OPEN_FILE",
                "JSON Files",
                "acq_settings.json",
                false,
                "json"
        );

        createUserInterface();
        createEventHandlers();

        model.userSettings().addChangeListener(this);
    }

    public void createUserInterface() {
        setMigLayout(
                "",
                "",
                "[]5[]"
        );

        final ScapeAcquisitionSettings acqSettings = model_.acquisitions().settings();

        final JLabel lblSaveDirectory = new JLabel("Directory:");
        final JLabel lblSaveFileName = new JLabel("File Name:");
        final JLabel lblSaveMode = new JLabel("Save Mode:");

        txtSaveDirectory_ = new TextField();
        txtSaveDirectory_.setEditable(false);
        txtSaveDirectory_.setColumns(18);
        txtSaveDirectory_.setForeground(Color.BLACK);
        txtSaveDirectory_.setText(acqSettings.saveDirectory());

        txtSaveFileName_ = new TextField();
        txtSaveFileName_.setColumns(18);
        txtSaveFileName_.setForeground(Color.WHITE);
        txtSaveFileName_.setText(acqSettings.saveNamePrefix());

        btnBrowse_ = new Button("...", 26, 20);
        btnOpen_ = new Button(Icons.FOLDER, 26, 20);

        cbxSaveMode_ = new ComboBox<>(DataStorage.SaveMode.values(),
                acqSettings.saveMode(), 110, 20);

        cbxSaveWhileAcquiring_ = new CheckBox("Save images during acquisition",
                acqSettings.isSavingImagesDuringAcquisition());

        btnSaveSettings_ = new Button("Save", 60, 20);
        btnLoadSettings_ = new Button("Load", 60, 20);

        add(lblSaveDirectory, "");
        add(txtSaveDirectory_, "");
        add(btnBrowse_, "");
        add(btnOpen_, "wrap");
        add(lblSaveFileName, "");
        add(txtSaveFileName_, "wrap");
        add(lblSaveMode, "");
        add(cbxSaveMode_, "split 2, wrap");
        add(cbxSaveWhileAcquiring_, "span 2, wrap");
        add(new JLabel("Acq Settings:"), "");
        add(btnSaveSettings_, "split 2");
        add(btnLoadSettings_, "");
    }

    public void createEventHandlers() {
        btnBrowse_.registerListener(() -> {
            final File result = FileDialogs.openDir(frame_,
                    "Please select the directory to save images to...",
                    directorySelect_
            );
            if (result != null) {
                model_.acquisitions().settingsBuilder().saveDirectory(result.toString());
                txtSaveDirectory_.setText(result.toString());
            }
        });

        // use the text field so we don't need to update settings
        btnOpen_.registerListener(
                () -> openDirectory(txtSaveDirectory_.getText()));

        cbxSaveWhileAcquiring_.registerListener(
                () -> model_.acquisitions().settingsBuilder()
                        .saveImagesDuringAcquisition(cbxSaveWhileAcquiring_.isSelected()));

        txtSaveFileName_.registerListener(
                () -> model_.acquisitions().settingsBuilder()
                        .saveNamePrefix(txtSaveFileName_.getText()));

        cbxSaveMode_.registerListener(
                () -> model_.acquisitions().settingsBuilder()
                        .saveMode(cbxSaveMode_.getSelected()));

        btnSaveSettings_.registerListener(() -> {
            final File file = FileDialogs.save(frame_,
                    "Save the acquisition settings to JSON...", jsonFileSave_
            );
            if (file != null) {
                // update settings and save to file
                model_.acquisitions().updateAcquisitionSettings();
                FileUtils.writeStringToFile(file.toString(), model_.acquisitions().settings().toPrettyJson());
                model_.studio().logs().logMessage("Acquisition settings saved to: " + file);
            }
        });

        btnLoadSettings_.registerListener(() -> {
            final File file = FileDialogs.openFile(frame_,
                    "Load the acquisition settings from JSON...", jsonFileLoad_
            );
            if (file != null) {
                // TODO: prompt to overwrite settings
                final String json = FileUtils.readFileToString(file.toString());
                model_.userSettings().loadFromJson(json, true);
                model_.studio().logs().logMessage("Acquisition settings loaded from: " + file);
            }
        });

    }

    // Opens the file explorer to the save directory
    private void openDirectory(final String path) {
        final File directory = new File(path);
        if (directory.exists()) {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(directory);
                } catch (IOException e) {
                    model_.studio().logs().logError(
                            "Could not open the save directory.");
                }
            } else {
                model_.studio().logs().logError(
                        "Desktop is not supported on this platform.");
            }
        } else {
            model_.studio().logs().logError("Directory does not exist.");
        }
    }

    @Override
    public void onSettingsChanged(final AcquisitionSettings settings) {
        txtSaveDirectory_.setText(settings.saveDirectory());
        txtSaveFileName_.setText(settings.saveNamePrefix());
    }
}
