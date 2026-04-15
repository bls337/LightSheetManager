package org.micromanager.lightsheetmanager.gui.utils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JFrame;

/**
 * Utilities for
 */
public final class WindowUtils {

    /** This class should not be instantiated. */
    private WindowUtils() {
        throw new AssertionError("Utility class; do not instantiate.");
    }

    /**
     * Returns true if the window is open.
     *
     * @param frame the frame to check
     * @return true if the window is open
     */
    public static boolean isOpen(final JFrame frame) {
        return frame != null && frame.isDisplayable() && frame.isVisible();
    }

    /**
     * Returns true if the window is minimized.
     *
     * @param frame the frame to check
     * @return true if the window is minimized
     */
    public static boolean isMinimized(final JFrame frame) {
        return frame != null && frame.getState() == JFrame.ICONIFIED;
    }

    /**
     * Creates a window event object and dispatches the "window is closing" event.
     *
     * @param frame the frame to close
     */
    public static void close(final JFrame frame) {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Registers a listener on the {@link JFrame} that listens for the "window is closing" event.
     * Window's 'X' button clicked, use for save dialogs and shutting down hardware.
     *
     * @param frame the frame to register the listener
     * @param action the {@link Consumer} to run when the window is closing;
     * receives the {@link WindowEvent} as an argument.
     */
    public static void registerWindowClosingEvent(final JFrame frame, final Consumer<WindowEvent> action) {
        Objects.requireNonNull(action, "The closing event cannot be null");
        if (frame == null) {
            return; // early exit => do nothing
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                action.accept(event);
            }
        });
    }

    /**
     * Registers a listener on the {@link JFrame} that listens for the "window is closed" event.
     * Window is fully disposed, use to set references to null and free memory.
     *
     * @param frame the frame to register the listener
     * @param action the {@link Consumer} to run when the window is closed;
     * receives the {@link WindowEvent} as an argument.
     */
    public static void registerWindowClosedEvent(final JFrame frame, final Consumer<WindowEvent> action) {
        Objects.requireNonNull(action, "The closed event cannot be null");
        if (frame == null) {
            return; // early exit => do nothing
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(final WindowEvent event) {
                action.accept(event);
            }
        });
    }

}
