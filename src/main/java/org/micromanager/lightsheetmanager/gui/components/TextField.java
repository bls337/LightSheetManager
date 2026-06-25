package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.util.function.Consumer;

public class TextField extends JTextField {

    private static final String ILLEGAL_FILENAME_CHARS = "\\/:*?\"<>|";
    private static final Color ERROR_COLOR = new Color(110, 25, 25);

    private final Color defaultColor_;

    public TextField() {
        setColumns(5);
        defaultColor_ = getBackground();
    }

    public TextField(final int size) {
        setColumns(size);
        defaultColor_ = getBackground();
    }

    public TextField(final String text, final int size) {
        super(text);
        setColumns(size);
        defaultColor_ = getBackground();
    }

    public void registerListener(final Runnable listener) {
        // fires when enter is pressed
        addActionListener(e -> listener.run());
    }

    /**
     * Registers a listener that validates the current text as a Windows
     * filename whenever the document changes.
     *
     * @param listener receives {@code true} if the current text is valid
     */
    public void registerFilenameValidationListener(final Consumer<Boolean> listener) {
        final Runnable validate = () -> {
            final boolean isValid = isValidWindowsFilename(getText());
            setBackground(isValid ? defaultColor_ : ERROR_COLOR);
            listener.accept(isValid);
        };

        final DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validate.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validate.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validate.run();
            }
        };

        getDocument().addDocumentListener(docListener);
    }

    /**
     * Returns {@code true} if the filename is valid on Windows.
     *
     * @param name the name to check
     * @return {@code true} if the filename is valid
     */
    private static boolean isValidWindowsFilename(final String name) {
        // empty names are invalid
        if (name.isEmpty()) {
            return false;
        }

        // trailing space or period
        final char last = name.charAt(name.length() - 1);
        if (last == ' ' || last == '.') {
            return false;
        }

        // check for illegal characters and control characters
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            // c < 32 rejects all ASCII control characters (0x00–0x1F)
            if (c < 32 || ILLEGAL_FILENAME_CHARS.indexOf(c) >= 0) {
                return false;
            }
        }

        return true;
    }
}
