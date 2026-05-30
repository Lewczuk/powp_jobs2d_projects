package edu.kis.powp.jobs2d.command.gui;

import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import java.util.function.Supplier;
import edu.kis.powp.observer.Subscriber;

public class CommandPreviewObserver implements Subscriber {

    private CommandManager commandManager;
    private CommandPreviewWindow commandPreviewWindow;
    private Supplier<ICanvas> canvasSupplier;

    public CommandPreviewObserver(CommandManager commandManager, CommandPreviewWindow commandPreviewWindow, Supplier<ICanvas> canvasSupplier) {
        this.commandManager = commandManager;
        this.commandPreviewWindow = commandPreviewWindow;
        this.canvasSupplier = canvasSupplier;
    }

    @Override
    public void update() {
        ICanvas canvas = canvasSupplier.get();
        if (canvas != null) {
            commandPreviewWindow.setBackgroundCommand(canvas.toCommand());
        } else {
            commandPreviewWindow.setBackgroundCommand(null);
        }

        DriverCommand command = commandManager.getCurrentCommand();
        commandPreviewWindow.updatePreview(command);
    }

    @Override
    public String toString() {
        return "Command Preview Observer";
    }
}
