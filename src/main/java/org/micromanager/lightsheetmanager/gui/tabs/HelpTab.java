package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextPane;

/**
 *
 */
public class HelpTab extends Panel implements ListeningPanel {

    private TextPane textPane_;

    public HelpTab() {
        createUserInterface();
        createEventHandlers();
    }

    // TODO: update text
    private void createUserInterface() {

        textPane_ = new TextPane();
        textPane_.setText("This plugin is a work in progress");
//        textPane_.setText(
//                "This plugin is a work in progress; please contact the authors "
//                        + "Jon and Nico with bug reports or feature requests "
//                        + "(<a href='mailto:jon@asiimaging.com'>jon@asiimaging.com</a>)."
//                        + "<p>If you encounter bugs, the first step is to check and see if your "
//                        + "problem has already been fixed by using a recent nightly build of Micro-Manager. "
//                        + "If not, it is helpful to generate a problem report using \"Help\""
//                        + "-> \"Report Problem...\" in the main Micro-Manager window.  After clicking "
//                        + "\"Done\", click \"View Report\", save the text as a file, "
//                        + "and then email that file directly to Jon."
//                        + "<p>Further information and instructions are on the Micro-Manager wiki "
//                        + "<a href='http://micro-manager.org/wiki/ASIdiSPIM_Plugin'>"
//                        + "ASIdiSPIM Plugin</a>"
//                        + " as well as in the "
//                        + "<a href='http://dispim.org/docs/manual'>"
//                        + "diSPIM User Manual</a>."
//        );

        // add ui elements to the panel
        add(textPane_, "");
    }

    private void createEventHandlers() {
        textPane_.registerHyperlinkListener();

    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }
}
