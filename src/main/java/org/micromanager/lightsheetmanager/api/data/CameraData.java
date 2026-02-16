package org.micromanager.lightsheetmanager.api.data;

// Used to track imaging camera order for simultaneous imaging cameras.
public class CameraData {

    private String name_;
    private boolean isActive_;

    public CameraData(final String name, final boolean isActive) {
        name_ = name;
        isActive_ = isActive;
    }

    public String name() {
        return name_;
    }

    public void name(final String name) {
        name_ = name;
    }

    public boolean isActive() {
        return isActive_;
    }

    public void isActive(final boolean isActive) {
        isActive_ = isActive;
    }

    public static boolean isCameraActive(final CameraData[] cameras, final String cameraName) {
        for (CameraData camera : cameras) {
            if (camera.name().equals(cameraName)) {
                return camera.isActive();
            }
        }
        return false;
    }
}
