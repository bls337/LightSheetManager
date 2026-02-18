package org.micromanager.lightsheetmanager.gui.data;

import java.net.URL;
import java.util.Objects;

import javax.swing.ImageIcon;

import org.micromanager.Studio;

/**
 * Load icon image resources from Micro-Manager to be used in the UI.
 */
public final class Icons {

    private static final URL PAUSE_PATH = Studio.class.getResource("/org/micromanager/icons/control_pause.png");
    private static final URL PLAY_PATH = Studio.class.getResource("/org/micromanager/icons/control_play_blue.png");
    private static final URL CANCEL_PATH = Studio.class.getResource("/org/micromanager/icons/cancel.png");
    private static final URL CAMERA_PATH = Studio.class.getResource("/org/micromanager/icons/camera.png");
    private static final URL CAMERA_GO_PATH = Studio.class.getResource("/org/micromanager/icons/camera_go.png");
    private static final URL ARROW_UP_PATH = Studio.class.getResource("/org/micromanager/icons/arrow_up.png");
    private static final URL ARROW_DOWN_PATH = Studio.class.getResource("/org/micromanager/icons/arrow_down.png");
    private static final URL ARROW_RIGHT_PATH = Studio.class.getResource("/org/micromanager/icons/arrow_right.png");
    private static final URL MICROSCOPE_PATH = Studio.class.getResource("/org/micromanager/icons/microscope.gif");
    private static final URL FOLDER_PATH = Studio.class.getResource("/org/micromanager/icons/folder.png");

    public static final ImageIcon PAUSE = new ImageIcon(Objects.requireNonNull(PAUSE_PATH));
    public static final ImageIcon PLAY = new ImageIcon(Objects.requireNonNull(PLAY_PATH));
    public static final ImageIcon CANCEL = new ImageIcon(Objects.requireNonNull(CANCEL_PATH));
    public static final ImageIcon CAMERA = new ImageIcon(Objects.requireNonNull(CAMERA_PATH));
    public static final ImageIcon CAMERA_GO = new ImageIcon(Objects.requireNonNull(CAMERA_GO_PATH));
    public static final ImageIcon ARROW_UP = new ImageIcon(Objects.requireNonNull(ARROW_UP_PATH));
    public static final ImageIcon ARROW_DOWN = new ImageIcon(Objects.requireNonNull(ARROW_DOWN_PATH));
    public static final ImageIcon ARROW_RIGHT = new ImageIcon(Objects.requireNonNull(ARROW_RIGHT_PATH));
    public static final ImageIcon MICROSCOPE = new ImageIcon(Objects.requireNonNull(MICROSCOPE_PATH));
    public static final ImageIcon FOLDER = new ImageIcon(Objects.requireNonNull(FOLDER_PATH));

}