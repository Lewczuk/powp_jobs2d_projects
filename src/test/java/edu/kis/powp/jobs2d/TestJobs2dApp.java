package edu.kis.powp.jobs2d;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindow;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindowCommandChangeObserver;
import edu.kis.powp.jobs2d.command.gui.CommandPreviewObserver;
import edu.kis.powp.jobs2d.command.gui.CommandPreviewWindow;
import edu.kis.powp.jobs2d.drivers.usage.LoggerUsageMonitorSubscriber;
import edu.kis.powp.jobs2d.drivers.usage.UsageMonitorDriver;
import edu.kis.powp.jobs2d.features.*;
import edu.kis.powp.jobs2d.features.history.HistoryWindow;
import edu.kis.powp.jobs2d.features.history.HistoryWindowObserver;
import edu.kis.powp.jobs2d.features.history.UpdateHistoryOnCommandChangeObserver;
import edu.kis.powp.jobs2d.features.history.SizeLimitHistorySubscriber;
import edu.kis.powp.jobs2d.drivers.RealTimeDriver;
import edu.kis.powp.jobs2d.drivers.RecordingDriver;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.logger.TrackingLoggerDriver;
import edu.kis.powp.jobs2d.drivers.packet_composite.CompositeDriver;
import edu.kis.powp.jobs2d.drivers.transformations.CoordinateTransformer;
import edu.kis.powp.jobs2d.drivers.transformations.FlipTransformer;
import edu.kis.powp.jobs2d.drivers.transformations.RotateTransformer;
import edu.kis.powp.jobs2d.drivers.transformations.ScaleTransformer;
import edu.kis.powp.jobs2d.drivers.transformations.TransformingDriver;
import edu.kis.powp.jobs2d.drivers.visitor.FullNameGetterVisitor;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;
import edu.kis.powp.jobs2d.events.SelectCheckCanvasBoundsOptionListener;
import edu.kis.powp.jobs2d.events.SelectClearPanelOptionListener;
import edu.kis.powp.jobs2d.events.SelectClearRecordingOptionListener;
import edu.kis.powp.jobs2d.events.SelectCountCommandsOptionListener;
import edu.kis.powp.jobs2d.events.SelectFullNameGetterVisitorTestListener;
import edu.kis.powp.jobs2d.events.SelectLoadImmutableRectangleCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectLoadKiteCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectLoadRecordedMacroOptionListener;
import edu.kis.powp.jobs2d.events.SelectLoadSecretCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectTestFigure2OptionListener;
import edu.kis.powp.jobs2d.events.SelectTestFigureOptionListener;
import edu.kis.powp.jobs2d.events.SelectToggleRecordingOptionListener;
import edu.kis.powp.jobs2d.events.SelectTransformCommandOptionListener;

import javax.swing.JSpinner;

public class TestJobs2dApp {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Setup test concerning preset figures in context.
     *
     * @param application Application context.
     */
    private static void setupPresetTests(Application application) {
        SelectTestFigureOptionListener selectTestFigureOptionListener = new SelectTestFigureOptionListener(
                DriverFeature.getDriverManager());
        SelectTestFigure2OptionListener selectTestFigure2OptionListener = new SelectTestFigure2OptionListener(
                DriverFeature.getDriverManager());

        application.addTest("Figure Joe 1", selectTestFigureOptionListener);
        application.addTest("Figure Joe 2", selectTestFigure2OptionListener);
    }

    /**
     * Setup test using driver commands in context.
     *
     * @param application Application context.
     */
    private static void setupCommandTests(Application application) {
        application.addTest("Load secret command", new SelectLoadSecretCommandOptionListener());
        application.addTest("Load immutable rectangle command",
                new SelectLoadImmutableRectangleCommandOptionListener());

        application.addTest("Load kite command", new SelectLoadKiteCommandOptionListener());
        application.addTest("Load recorded macro", new SelectLoadRecordedMacroOptionListener());

        application.addTest("Clear panel", new SelectClearPanelOptionListener());
        application.addTest("Run command", new SelectRunCurrentCommandOptionListener(DriverFeature.getDriverManager()));
        application.addTest("Count current command", new SelectCountCommandsOptionListener());

        application.addTest("Check current command bounds", new SelectCheckCanvasBoundsOptionListener());
        application.addTest("Transform current command: Scale 2x",
                new SelectTransformCommandOptionListener(new ScaleTransformer(2.0, 2.0), "Scale 2x"));
        application.addTest("Transform current command: Scale 0.5x",
                new SelectTransformCommandOptionListener(new ScaleTransformer(0.5, 0.5), "Scale 0.5x"));
        application.addTest("Transform current command: Rotate 45 degrees",
                new SelectTransformCommandOptionListener(new RotateTransformer(45.0), "Rotate 45 degrees"));
        application.addTest("Transform current command: Flip Y",
                new SelectTransformCommandOptionListener(new FlipTransformer(false, true), "Flip Y"));
        application.addTest("FullNameGetter visitor test",
                new SelectFullNameGetterVisitorTestListener(new FullNameGetterVisitor()));

        application.addComponentMenuElement(DriverFeature.class, "Clear recording",
                new SelectClearRecordingOptionListener());
    }

    /**
     * Setup driver manager, and set default VisitableDriver for application.
     *
     * @param application Application context.
     */
    private static void setupDrivers(Application application) {

        DrawPanelController drawerController = DrawerFeature.getDrawerController();
        VisitableDriver driver = new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic");
        DriverFeature.addDriver("Line Simulator", driver);
        DriverFeature.getDriverManager().setCurrentDriver(driver);

        driver = new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(), "special");
        DriverFeature.addDriver("Special line Simulator", driver);

        UsageMonitorDriver usageMonitorDriver = new UsageMonitorDriver();
        usageMonitorDriver.getChangePublisher().addSubscriber(new LoggerUsageMonitorSubscriber(usageMonitorDriver));
        CompositeDriver monitoredDriverComposite = new CompositeDriver("Line Simulator with Usage Monitor");
        monitoredDriverComposite
                .addDriver(new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic"));
        monitoredDriverComposite.addDriver(usageMonitorDriver);
        DriverFeature.addDriver(monitoredDriverComposite.toString(), monitoredDriverComposite);

        DriverFeature.updateDriverInfo();

        CoordinateTransformer scale = new ScaleTransformer(2.0, 2.0);
        VisitableDriver scaledDriver = new TransformingDriver(driver, scale, "Transform: Scaled 2x");
        DriverFeature.addDriver(scaledDriver.toString(), scaledDriver);

        CoordinateTransformer scaleDown = new ScaleTransformer(0.5, 0.5);
        VisitableDriver scaledDownDriver = new TransformingDriver(driver, scaleDown, "Transform: Scaled 0.5x");
        DriverFeature.addDriver(scaledDownDriver.toString(), scaledDownDriver);

        CoordinateTransformer flip = new FlipTransformer(false, true);
        VisitableDriver flippedDriver = new TransformingDriver(driver, flip, "Transform: Flipped Y");
        DriverFeature.addDriver(flippedDriver.toString(), flippedDriver);

        CoordinateTransformer rotate = new RotateTransformer(45.0);
        VisitableDriver rotatedDriver = new TransformingDriver(driver, rotate, "Transform: Rotated 45 degrees");
        DriverFeature.addDriver(rotatedDriver.toString(), rotatedDriver);

        VisitableDriver scaledAndRotatedDriver = new TransformingDriver(scaledDriver, rotate,
                "Transform: Scaled 2x & Rotated 45");
        DriverFeature.addDriver(scaledAndRotatedDriver.toString(), scaledAndRotatedDriver);

        CompositeDriver chaosCompositeDriver = new CompositeDriver("Chaos Composite Driver");
        chaosCompositeDriver.addDriver(driver);
        chaosCompositeDriver.addDriver(scaledDownDriver);
        DriverFeature.addDriver(chaosCompositeDriver.toString(), chaosCompositeDriver);

        driver = new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic");
        VisitableDriver animatedDriver = new RealTimeDriver(driver, 10, 10, "Real-Time Driver 1x speed");
        DriverFeature.addDriver(animatedDriver.toString(), animatedDriver);

        animatedDriver = new RealTimeDriver(driver, 5, 5, "Real-Time Driver 2x speed");
        DriverFeature.addDriver(animatedDriver.toString(), animatedDriver);

        animatedDriver = new RealTimeDriver(driver, 1, 1, "Real-Time Driver 10x speed");
        DriverFeature.addDriver(animatedDriver.toString(), animatedDriver);
    }

    private static void setupExtensions() {
        VisitableDriver loggerDriver = new TrackingLoggerDriver();
        ExtensionFeature.addExtension("Logger", loggerDriver);

        RecordingDriver rec = RecordingFeature.getRecordingDriver();
        ExtensionFeature.addMenuToggle("Recording", new SelectToggleRecordingOptionListener(rec),
                rec.isRecordingEnabled());
    }

    private static void setupWindows(Application application) {

        CommandManagerWindow commandManager = new CommandManagerWindow(CommandsFeature.getDriverCommandManager());
        application.addWindowComponent("Command Manager", commandManager);

        CommandManagerWindowCommandChangeObserver windowObserver = new CommandManagerWindowCommandChangeObserver(
                commandManager);
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(windowObserver);

        HistoryWindow historyWindow = new HistoryWindow(HistoryFeature.getHistoryManager());
        SizeLimitHistorySubscriber limitSubscriber = HistoryFeature.getSizeLimitSubscriber();
        if (limitSubscriber != null) {
            historyWindow.setLimitValue(limitSubscriber.getMaxSize());
            historyWindow.addLimitChangeListener(e -> {
                JSpinner spinner = (JSpinner) e.getSource();
                limitSubscriber.setMaxSize((Integer) spinner.getValue());
            });
        }
        historyWindow.addLoadButtonListener(e -> {
            edu.kis.powp.jobs2d.features.history.HistoryEntry selected = historyWindow.getSelectedHistoryEntry();
            if (selected != null && selected.getCommand() != null) {
                CommandsFeature.getDriverCommandManager().setCurrentCommand(selected.getCommand());
            }
        });
        application.addWindowComponent("History", historyWindow);

        HistoryWindowObserver historyWindowObserver = new HistoryWindowObserver(historyWindow);
        HistoryFeature.getHistoryManager().getChangePublisher().addSubscriber(historyWindowObserver);

        UpdateHistoryOnCommandChangeObserver historyCommandObserver = new UpdateHistoryOnCommandChangeObserver(
                HistoryFeature.getHistoryManager(), CommandsFeature.getDriverCommandManager());
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(historyCommandObserver);

        CommandPreviewWindow commandPreview = new CommandPreviewWindow();
        application.addWindowComponent("Command Preview", commandPreview);

        DrawPanelController previewDrawController = commandPreview.getDrawPanelController();
        VisitableDriver basicDriver = new LineDriverAdapter(previewDrawController, LineFactory.getBasicLine(), "basic");
        CoordinateTransformer scaleDown = new ScaleTransformer(0.5, 0.5);
        VisitableDriver scaledDownDriver = new TransformingDriver(basicDriver, scaleDown,
                "Preview Transform: Scaled 0.5x");
        commandPreview.setPreviewDriver(scaledDownDriver);

        CommandPreviewObserver previewObserver = new CommandPreviewObserver(CommandsFeature.getDriverCommandManager(),
                commandPreview);
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(previewObserver);
    }

    /**
     * Setup menu for adjusting logging settings.
     *
     * @param application Application context.
     */
    private static void setupLogger(Application application) {

        application.addComponentMenu(Logger.class, "Logger", 0);
        application.addComponentMenuElement(Logger.class, "Clear log",
                (ActionEvent e) -> application.flushLoggerOutput());
        application.addComponentMenuElement(Logger.class, "Fine level", (ActionEvent e) -> logger.setLevel(Level.FINE));
        application.addComponentMenuElement(Logger.class, "Info level", (ActionEvent e) -> logger.setLevel(Level.INFO));
        application.addComponentMenuElement(Logger.class, "Warning level",
                (ActionEvent e) -> logger.setLevel(Level.WARNING));
        application.addComponentMenuElement(Logger.class, "Severe level",
                (ActionEvent e) -> logger.setLevel(Level.SEVERE));
        application.addComponentMenuElement(Logger.class, "OFF logging", (ActionEvent e) -> logger.setLevel(Level.OFF));
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Application app = new Application("Jobs 2D");

                // Przykład użycia automatycznego zarządzania funkcjami (features management)
                // Zarejestruj funkcje, które mają być automatycznie skonfigurowane
                FeaturesManager.registerFeature(new DrawerFeature());
                FeaturesManager.registerFeature(new CommandsFeature());
                FeaturesManager.registerFeature(new DriverFeature());
                FeaturesManager.registerFeature(new ExtensionFeature());
                FeaturesManager.registerFeature(new CanvasFeature());
                FeaturesManager.registerFeature(new MouseClickFeature());
                FeaturesManager.registerFeature(new HistoryFeature());

                // Automatycznie skonfiguruj wszystkie zarejestrowane funkcje
                // To zastępuje ręczne wywołania setup dla każdej funkcji
                FeaturesManager.setupAllFeatures(app);

                setupDrivers(app);
                RecordingFeature.setup(DriverFeature.getDriverManager());
                setupExtensions();
                setupPresetTests(app);
                setupCommandTests(app);
                setupLogger(app);
                setupWindows(app);

                app.setVisibility(true);
            }
        });
    }

}
