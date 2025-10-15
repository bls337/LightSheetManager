package org.micromanager.lightsheetmanager.model;

import org.micromanager.Studio;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.lightsheetmanager.api.DataSink;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: set acq name from playlist to datastore name?

// TODO: getPreferredSaveMode() might be a reasonable default
public class DataStorage implements DataSink {

    /**
     * Easier to convert to a String.
     */
    public enum SaveMode {
        SINGLEPLANE_TIFF_SERIES("Single Plane TIFF"),
        MULTIPAGE_TIFF("Multi Page TIFF"),
        ND_TIFF("NDTiff");

        private String text_;

        private static final Map<String, SaveMode> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        SaveMode(final String text) {
            text_ = text;
        }

        public static Datastore.SaveMode convert(final DataStorage.SaveMode mode) {
            switch (mode) {
               case ND_TIFF:
                  return Datastore.SaveMode.ND_TIFF;
               case MULTIPAGE_TIFF:
                  return Datastore.SaveMode.MULTIPAGE_TIFF;
               case SINGLEPLANE_TIFF_SERIES:
               default:
                  return Datastore.SaveMode.SINGLEPLANE_TIFF_SERIES;
            }
        }

        public static String[] toArray() {
            return Arrays.stream(values())
                    .map(SaveMode::toString)
                    .toArray(String[]::new);
        }

        public static SaveMode fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, SaveMode.ND_TIFF);
        }

        @Override
        public String toString() {
            return text_;
        }
    }

    private final Studio studio_;

    private Datastore datastore_;
    private DataStorage.SaveMode saveMode_;

    public DataStorage(final Studio studio) {
        studio_ = Objects.requireNonNull(studio);
        saveMode_ = SaveMode.SINGLEPLANE_TIFF_SERIES;
    }

    public String getDatastoreSavePath() {
        return datastore_.getSavePath();
    }

    public void setDatastoreSavePath(final String savePath) {
        datastore_.setSavePath(savePath);
    }

    public Datastore getDatastore() {
        return datastore_;
    }

    public DataStorage.SaveMode getSaveMode() {
        return saveMode_;
    }

    public void setSaveMode(final DataStorage.SaveMode saveMode) {
        saveMode_ = saveMode;
    }

    // TODO: expose params for MULTIPAGE_TIFF?
    public void createDatastore(final String savePath) {
        switch (saveMode_) {
            case SINGLEPLANE_TIFF_SERIES:
                try {
                    datastore_ = studio_.data().createSinglePlaneTIFFSeriesDatastore(savePath);
                } catch (IOException e) {
                    studio_.logs().showError("DataStorage: could not create single plane TIFF datastore.");
                }
                break;
            case MULTIPAGE_TIFF:
                try {
                    datastore_ = studio_.data().createMultipageTIFFDatastore(savePath, false, false);
                } catch (IOException e) {
                    studio_.logs().showError("DataStorage: could not create multi page TIFF datastore.");
                }
                break;
            default:
                datastore_ = studio_.data().createRAMDatastore();
                break;
        }
    }

    @Override
    public void putImage(final Image image) {
        try {
            datastore_.putImage(image);
        } catch (IOException e) {
            studio_.logs().showError("DataStorage: could not put image into the datastore.");
        }
    }
}
