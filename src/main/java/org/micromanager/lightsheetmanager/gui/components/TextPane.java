package org.micromanager.lightsheetmanager.gui.components;

import org.micromanager.internal.MMStudio;

import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TextPane extends JTextPane {

    public TextPane() {
        setContentType("text/html");
        setEditable(false);
    }

    public void registerHyperlinkListener() {
        addHyperlinkListener(event -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(event.getEventType())) {
                try {
                    Desktop.getDesktop().browse(new URI(event.getURL().toString()));
                } catch (URISyntaxException | IOException e) {
                    MMStudio.getInstance().logs().logMessage("Could not open web browser.");
                }
            }
        });
    }
}
