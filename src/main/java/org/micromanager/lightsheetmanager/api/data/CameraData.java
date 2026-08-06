package org.micromanager.lightsheetmanager.api.data;

// Used to track imaging camera order for simultaneous imaging cameras.
public class CameraData {

    private String name;
    private boolean isActive;

    public CameraData(final String name, final boolean isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    public String name() {
        return name;
    }

    public void name(final String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void isActive(final boolean isActive) {
        this.isActive = isActive;
    }

    public static boolean isCameraActive(final CameraData[] cameras, final String cameraName) {
        for (CameraData camera : cameras) {
            if (camera.name().equals(cameraName)) {
                return camera.isActive();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[name=%s]", getClass().getSimpleName(), name);
    }

}
