package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.features.history.HistoryManager;
import edu.kis.powp.jobs2d.features.history.SizeLimitHistorySubscriber;
import edu.kis.powp.jobs2d.features.history.HistoryWindow;
import edu.kis.powp.jobs2d.features.history.HistoryWindowObserver;
import edu.kis.powp.jobs2d.features.history.UpdateHistoryOnCommandChangeObserver;
import edu.kis.powp.jobs2d.features.history.HistoryEntry;

import javax.swing.JSpinner;

public class HistoryFeature implements IFeature {

    public static final int MAX_SIZE = 10;
    private static HistoryManager historyManager;
    private static SizeLimitHistorySubscriber sizeLimitSubscriber;

    @Override
    public void setup(Application application) {
        historyManager = new HistoryManager();
        sizeLimitSubscriber = new SizeLimitHistorySubscriber(historyManager, MAX_SIZE);
        historyManager.getChangePublisher().addSubscriber(sizeLimitSubscriber);

        HistoryWindow historyWindow = new HistoryWindow(historyManager);
        if (sizeLimitSubscriber != null) {
            historyWindow.setLimitValue(sizeLimitSubscriber.getMaxSize());
            historyWindow.addLimitChangeListener(e -> {
                JSpinner spinner = (JSpinner) e.getSource();
                sizeLimitSubscriber.setMaxSize((Integer) spinner.getValue());
            });
        }
        historyWindow.addLoadButtonListener(e -> {
            HistoryEntry selected = historyWindow.getSelectedHistoryEntry();
            if (selected != null && selected.getCommand() != null) {
                CommandsFeature.getDriverCommandManager().setCurrentCommand(selected.getCommand());
            }
        });
        application.addWindowComponent("Command History", historyWindow);

        HistoryWindowObserver historyWindowObserver = new HistoryWindowObserver(historyWindow);
        historyManager.getChangePublisher().addSubscriber(historyWindowObserver);

        UpdateHistoryOnCommandChangeObserver historyCommandObserver = new UpdateHistoryOnCommandChangeObserver(
                historyManager, CommandsFeature.getDriverCommandManager());
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(historyCommandObserver);
    }

    @Override
    public String getName() {
        return "History";
    }

    public static HistoryManager getHistoryManager() {
        return historyManager;
    }

    public static SizeLimitHistorySubscriber getSizeLimitSubscriber() {
        return sizeLimitSubscriber;
    }
}
