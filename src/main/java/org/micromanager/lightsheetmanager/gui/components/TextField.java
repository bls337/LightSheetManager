package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextField extends JTextField {

    private static int DEFAULT_SIZE = 5;

    private static DocumentListener docListener = null;

    public TextField() {
        setColumns(DEFAULT_SIZE);
        if (docListener != null) {
            getDocument().addDocumentListener(docListener);
        }
    }

    public TextField(final int size) {
        setColumns(size);
        if (docListener != null) {
            getDocument().addDocumentListener(docListener);
        }
    }

    public static void setDefaultSize(final int size) {
        DEFAULT_SIZE = size;
    }

    public static void setDefaultDocumentListener(final DocumentMethod method) {
        docListener = new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent event) {
                method.run(event);
            }

            @Override
            public void removeUpdate(final DocumentEvent event) {
                // not necessary to validate deleting characters
            }

            @Override
            public void changedUpdate(final DocumentEvent event) {
                // this event only fires for StyledDocument
            }
        };
    }

    // TODO: needed? how to not interfere with docListener?
    public void addDocumentListener(final DocumentMethod method) {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent event) {
                method.run(event);
            }

            @Override
            public void removeUpdate(final DocumentEvent event) {
                // not necessary to validate deleting characters
            }

            @Override
            public void changedUpdate(final DocumentEvent event) {
                // this event only fires for StyledDocument
            }
        });
    }

    public void acceptOnlyNumericInput() {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent event) {
                final String text = getText();
                try {
                    Double.parseDouble(getText());
                } catch (NumberFormatException e) {
                    // do nothing
                }
                System.out.println(text);
            }

            @Override
            public void removeUpdate(final DocumentEvent event) {
                // not necessary to validate deleting characters
            }

            @Override
            public void changedUpdate(final DocumentEvent event) {
                // this event only fires for StyledDocument
            }
        });
    }

    public void registerListener(final Method method) {
        addPropertyChangeListener(method::run);
    }

}
