package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JTextField;

public class TextField extends JTextField {

    private static int DEFAULT_SIZE = 5;

    public TextField() {
        setColumns(DEFAULT_SIZE);
    }

    public TextField(final int size) {
        setColumns(size);
    }

    public static void setDefaultSize(final int size) {
        DEFAULT_SIZE = size;
    }

    public void registerListener(final Method method) {
        addActionListener(method::run);
    }

}
