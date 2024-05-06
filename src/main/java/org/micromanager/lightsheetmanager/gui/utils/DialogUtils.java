package org.micromanager.lightsheetmanager.gui.utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

// TODO: is errorLog the best way to send errors to JTextArea? reconsider impl for AcquisitionTable

/**
 * A utility class for making dialog boxes.
 *
 */
public class DialogUtils {

    /**Standard error reporting or delegate to JTextArea component. */
    public static boolean SEND_ERROR_TO_COMPONENT = false;

    /**The component to display the errors. */
    private static JTextArea errorLog = null;

    /**
     * Sets the JTextArea to log errors.
     *
     * @param textArea a reference to the object
     */
    public static void setErrorLog(final JTextArea textArea) {
        errorLog = textArea;
    }

    /**
     * Return the user's input or null if the action is canceled.
     *
     * @param frame the parent frame
     * @param title the title string
     * @param message the message to display
     * @return the user's input or {@code null} meaning the user canceled the input
     */
    public static String showTextEntryDialog(final JFrame frame, final String title, final String message) {
        return (String) JOptionPane.showInputDialog(frame, message, title,
                JOptionPane.PLAIN_MESSAGE, null, null, "");
    }

    /**
     * Return 0 if "Yes" is selected or 1 if "No" is selected.
     *
     * @param frame the parent frame
     * @param title the title string
     * @param message the message to display
     * @return an int indicating the option selected by the user.
     */
    public static int showYesNoDialog(final JFrame frame, final String title, final String message) {
        return JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.YES_NO_OPTION);
    }

    /**
     * Shows a customized message dialog box, this method does not log the error.<P>
     * This is used for reporting errors in the AcquisitionTable.
     *
     * @param frame the parent frame
     * @param title the title string
     * @param message the message to display
     */
    public static void showErrorMessage(final JFrame frame, final String title, final String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
