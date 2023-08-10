package org.micromanager.lightsheetmanager.api.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.micromanager.lightsheetmanager.api.AcquisitionSettings;
import org.micromanager.lightsheetmanager.model.DataStorage;

// TODO: make public instead of abstract? use this in demo mode?

/**
 * Base acquisition settings for all microscopes.
 */
public abstract class DefaultAcquisitionSettings implements AcquisitionSettings {

    public abstract static class Builder<T extends Builder<T>> implements AcquisitionSettings.Builder<T> {

        private String saveDirectory_ = "";
        private String saveNamePrefix_ = "";
        private boolean saveDuringAcq_ = false;
        private boolean demoMode_ = false;
        private DataStorage.SaveMode saveMode_ = DataStorage.SaveMode.NDTIFF;

        private DefaultAutofocusSettings.Builder afsb_ = new DefaultAutofocusSettings.Builder();

        public Builder() {
        }

        public Builder(final DefaultAcquisitionSettings acqSettings) {
            saveDirectory_ = acqSettings.saveDirectory_;
            saveNamePrefix_ = acqSettings.saveNamePrefix_;
            saveDuringAcq_ = acqSettings.saveDuringAcq_;
            demoMode_ = acqSettings.demoMode_;
            saveMode_ = acqSettings.saveMode_;
        }

        /**
         * Sets the save directory.
         *
         * @param directory the directory
         */
        @Override
        public T saveDirectory(final String directory) {
            saveDirectory_ = directory;
            return self();
        }

        /**
         * Sets the folder name.
         *
         * @param name the name of the folder
         */
        @Override
        public T saveNamePrefix(final String name) {
            saveNamePrefix_ = name;
            return self();
        }

        /**
         * Sets the folder name.
         *
         * @param state the name of the folder
         */
        @Override
        public T saveImagesDuringAcquisition(final boolean state) {
            saveDuringAcq_ = state;
            return self();
        }

        /**
         * Sets the acquisition to demo mode.
         *
         * @param state true if in demo mode
         */
        @Override
        public T demoMode(final boolean state) {
            demoMode_ = state;
            return self();
        }

        /**
         * Sets the data saving mode.
         *
         * @param saveMode the save mode
         */
        @Override
        public T saveMode(final DataStorage.SaveMode saveMode) {
            saveMode_ = saveMode;
            return self();
        }

        @Override
        public DefaultAutofocusSettings.Builder autofocusSettingsBuilder() {
            return afsb_;
        }

        /**
         * Creates an immutable instance of DefaultAcquisitionSettings
         *
         * @return Immutable version of DefaultAcquisitionSettings
         */
        //@Override
        //public abstract AcquisitionSettings build();

        //public abstract T self();
    }

    /**
     * Creates a Builder populated with settings of this AcquisitionSettings instance.
     *
     * @return AcquisitionSettings.Builder pre-populated with settings of this instance.
     */
//    @Override
//    public AcquisitionSettings.Builder copyBuilder() {
//        return new DefaultAcquisitionSettings.Builder(
//                saveDirectory_, saveNamePrefix_, demoMode_
//        );
//    }

    private final String saveNamePrefix_;
    private final String saveDirectory_;
    private final boolean saveDuringAcq_;
    private final boolean demoMode_;
    private final DataStorage.SaveMode saveMode_;

    private final DefaultAutofocusSettings autofocusSettings_;

//    public DefaultAcquisitionSettings() {
//        saveNamePrefix_ = "";
//        saveDirectory_ = "";
//        demoMode_ = false;
//    }

    protected DefaultAcquisitionSettings(Builder<?> builder) {
        saveDirectory_ = builder.saveDirectory_;
        saveNamePrefix_ = builder.saveNamePrefix_;
        saveDuringAcq_ = builder.saveDuringAcq_;
        demoMode_ = builder.demoMode_;
        saveMode_ = builder.saveMode_;
        autofocusSettings_ = builder.afsb_.build();
    }

    /**
     * Returns the save name prefix.
     *
     * @return the save name prefix.
     */
    @Override
    public String saveNamePrefix() {
        return saveNamePrefix_;
    }

    /**
     * Returns the save directory.
     *
     * @return the save directory.
     */
    @Override
    public String saveDirectory() {
        return saveDirectory_;
    }

    /**
     * Returns true if saving images during an acquisition.
     *
     * @return true if saving images during an acquisition.
     */
    @Override
    public boolean isSavingImagesDuringAcquisition() {
        return saveDuringAcq_;
    }

    /**
     * Returns true if using demo mode.
     *
     * @return true if using demo mode
     */
    @Override
    public boolean demoMode() {
        return demoMode_;
    }

    /**
     * Returns the save mode.
     *
     * @return the save mode
     */
    @Override
    public DataStorage.SaveMode saveMode() {
        return saveMode_;
    }

    /**
     * Returns the autofocus settings.
     *
     * @return the autofocus settings
     */
    @Override
    public DefaultAutofocusSettings autofocusSettings() {
        return autofocusSettings_;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[saveDirectory=%s, saveNamePrefix=%s, saveDuringAcq=%s, demoMode=%s, saveMode=%s]",
                getClass().getSimpleName(), saveDirectory_, saveNamePrefix_, saveDuringAcq_, demoMode_, saveMode_
        );
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String toPrettyJson() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static <T extends AcquisitionSettings> T fromJson(final String json, final Class<T> cls) {
        return new Gson().fromJson(json, cls);
    }

//    public static DefaultAcquisitionSettingsDISPIM fromJson(final String json) {
//        return new Gson().fromJson(json, DefaultAcquisitionSettingsDISPIM.class);
//    }

}
