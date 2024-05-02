package org.micromanager.lightsheetmanager.gui.utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * This is primarily used in the acquisition playlist ui.
 */
public class DialogUtils {

    /**Standard error reporting or delegate to JTextArea component. */
    public static boolean SEND_ERROR_TO_COMPONENT = false;

    /**The component to display the errors. */
    private static JTextArea errorLog = null;

    public DialogUtils() {
    }

    /**
     * Sets the JTextArea to log errors.
     *
     * @param textArea a reference to the object
     */
    public static void setErrorLog(final JTextArea textArea) {
        errorLog = textArea;
    }

    public static String showTextEntryDialog(final JFrame frame, final String title, final String message) {
        return (String) JOptionPane.showInputDialog(frame, message, title,
                JOptionPane.PLAIN_MESSAGE, null, null, "");
    }

    public static int showYesNoDialog(final JFrame frame, final String title, final String message) {
        return JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.YES_NO_OPTION);
    }

    /**
     * Shows a customized message dialog box, this method does not log the error.<P>
     * This is used for reporting errors in the AcquisitionTable.
     *
     * @param frame the frame in which the dialog is displayed
     * @param title the title string for the dialog
     * @param message the message to display
     */
    public static void showErrorMessage(final JFrame frame, final String title, final String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
