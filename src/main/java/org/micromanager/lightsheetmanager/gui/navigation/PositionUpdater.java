package org.micromanager.lightsheetmanager.gui.navigation;

import javax.swing.SwingWorker;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO: use a SwingWorker? will it interfere with +/- buttons?

/**
 * Updates the positions on the navigation panel periodically using a timer.
 */
public class PositionUpdater {

    private int pollingDelayMs_;

    private SwingWorker<Void, Void> worker_;

    private NavigationPanel navPanel_;

    private AtomicBoolean isPolling_;
    public PositionUpdater(final NavigationPanel navPanel) {
        navPanel_ = Objects.requireNonNull(navPanel);
        isPolling_ = new AtomicBoolean(true);
        startPolling();
    }

    private void createPollingTask() {
        isPolling_.set(true);
        worker_ = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {

                while (isPolling_.get()) {
                    navPanel_.updatePositions();
                    if (!isPolling_.get()) {
                        System.out.println("break!");
                        break;
                    }
                }

                return null;
            }
        };
    }

    public void startPolling() {
        createPollingTask();
        worker_.execute();
    }

    public void stopPolling() {
        isPolling_.set(false);
    }
}
