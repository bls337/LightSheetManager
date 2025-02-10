package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JTextField;

public class TextField extends JTextField {

    public TextField() {
        setColumns(5);
    }

    public TextField(final int size) {
        setColumns(size);
    }

    public TextField(final String text, final int size) {
        super(text);
        setColumns(size);
    }

    public void registerListener(final Method method) {
        addActionListener(method::run);
    }

}
