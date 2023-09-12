package org.micromanager.lightsheetmanager.gui.navigation;

import javax.swing.SwingWorker;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Updates the positions on the navigation panel periodically using a separate thread.
 * <p>
 * The updater starts updating after the UI is created.
 */
public class PositionUpdater {

    private int pollingDelayMs_; // TODO: use this!

    private SwingWorker<Void, Void> worker_;

    private NavigationPanel navPanel_;

    private final AtomicBoolean isPolling_;
    public PositionUpdater(final NavigationPanel navPanel) {
        navPanel_ = Objects.requireNonNull(navPanel);
        isPolling_ = new AtomicBoolean(true);
        pollingDelayMs_ = 500;
    }

    private void createPollingTask() {
        isPolling_.set(true);
        worker_ = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                while (isPolling_.get()) {
                    //System.out.println("updater tick");
                    try {
                        navPanel_.updatePositions();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!isPolling_.get()) {
                        System.out.println("break!");
                        break;
                    }
                    try {
                        Thread.sleep(pollingDelayMs_);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
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
