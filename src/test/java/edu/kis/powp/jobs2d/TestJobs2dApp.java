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
import edu.kis.powp.jobs2d.drivers.RealTimeDriver;
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
import edu.kis.powp.jobs2d.events.SelectTransformCommandOptionListener;
import edu.kis.powp.jobs2d.features.CanvasFeature;
import edu.kis.powp.jobs2d.features.CommandsFeature;
import edu.kis.powp.jobs2d.features.DrawerFeature;
import edu.kis.powp.jobs2d.features.DriverFeature;
import edu.kis.powp.jobs2d.features.ExtensionFeature;
import edu.kis.powp.jobs2d.features.FeaturesManager;
import edu.kis.powp.jobs2d.features.RecordingFeature;
import edu.kis.powp.jobs2d.features.MouseClickFeature;

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
                application.addTest("Run command",
                                new SelectRunCurrentCommandOptionListener(DriverFeature.getDriverManager()));
                application.addTest("Count current command", new SelectCountCommandsOptionListener());

                application.addTest("Check current command bounds", new SelectCheckCanvasBoundsOptionListener());
                application.addTest("Transform current command: Scale 2x",
                                new SelectTransformCommandOptionListener(new ScaleTransformer(2.0, 2.0), "Scale 2x"));
                application.addTest("Transform current command: Scale 0.5x",
                                new SelectTransformCommandOptionListener(new ScaleTransformer(0.5, 0.5), "Scale 0.5x"));
                application.addTest("Transform current command: Rotate 45 degrees",
                                new SelectTransformCommandOptionListener(new RotateTransformer(45.0),
                                                "Rotate 45 degrees"));
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
                usageMonitorDriver.getChangePublisher()
                                .addSubscriber(new LoggerUsageMonitorSubscriber(usageMonitorDriver));
                CompositeDriver monitoredDriverComposite = new CompositeDriver("Line Simulator with Usage Monitor");
                monitoredDriverComposite
                                .addDriver(new LineDriverAdapter(drawerController, LineFactory.getBasicLine(),
                                                "basic"));
                monitoredDriverComposite.addDriver(usageMonitorDriver);
                DriverFeature.addDriver(monitoredDriverComposite.toString(), monitoredDriverComposite);

                DriverFeature.updateDriverInfo();
        }

        private static void setupExtensions() {
                VisitableDriver loggerDriver = new TrackingLoggerDriver();
                ExtensionFeature.addExtension("Logger", loggerDriver);

                ExtensionFeature.addDecoratorExtension("Recording", RecordingFeature.getRecordingDriver());

                TransformingDriver transformExt = new TransformingDriver(new ScaleTransformer(2.0, 2.0),
                                "Transform: Scaled 2x");
                ExtensionFeature.addDecoratorExtension("Scale 2x", transformExt);

                TransformingDriver scaledDownDriver = new TransformingDriver(new ScaleTransformer(0.5, 0.5),
                                "Transform: Scaled 0.5x");
                ExtensionFeature.addDecoratorExtension("Scale 0.5x", scaledDownDriver);

                TransformingDriver flippedDriver = new TransformingDriver(new FlipTransformer(false, true),
                                "Transform: Flipped Y");
                ExtensionFeature.addDecoratorExtension("Flip Y", flippedDriver);

                TransformingDriver rotatedDriver = new TransformingDriver(new RotateTransformer(45.0),
                                "Transform: Rotated 45 degrees");
                ExtensionFeature.addDecoratorExtension("Rotate 45 degrees", rotatedDriver);

                RealTimeDriver realTime1x = new RealTimeDriver(10, 10, "Real-Time Driver 1x speed");
                ExtensionFeature.addDecoratorExtension("Real-Time 1x", realTime1x);

                RealTimeDriver realTime2x = new RealTimeDriver(5, 5, "Real-Time Driver 2x speed");
                ExtensionFeature.addDecoratorExtension("Real-Time 2x", realTime2x);

                RealTimeDriver realTime10x = new RealTimeDriver(1, 1, "Real-Time Driver 10x speed");
                ExtensionFeature.addDecoratorExtension("Real-Time 10x", realTime10x);
        }

        private static void setupWindows(Application application) {

                CommandManagerWindow commandManager = new CommandManagerWindow(
                                CommandsFeature.getDriverCommandManager());
                application.addWindowComponent("Command Manager", commandManager);

                CommandManagerWindowCommandChangeObserver windowObserver = new CommandManagerWindowCommandChangeObserver(
                                commandManager);
                CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(windowObserver);

                CommandPreviewWindow commandPreview = new CommandPreviewWindow();
                application.addWindowComponent("Command Preview", commandPreview);

                DrawPanelController previewDrawController = commandPreview.getDrawPanelController();
                VisitableDriver basicDriver = new LineDriverAdapter(previewDrawController, LineFactory.getBasicLine(),
                                "basic");
                CoordinateTransformer scaleDown = new ScaleTransformer(0.5, 0.5);
                VisitableDriver scaledDownDriver = new TransformingDriver(basicDriver, scaleDown,
                                "Preview Transform: Scaled 0.5x");
                commandPreview.setPreviewDriver(scaledDownDriver);

                CommandPreviewObserver previewObserver = new CommandPreviewObserver(
                                CommandsFeature.getDriverCommandManager(),
                                commandPreview);
                CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(previewObserver);

                VisitableDriver backgroundDriver = new LineDriverAdapter(previewDrawController, CanvasFeature.getGuidesLineType(),
                "background");
                VisitableDriver scaledDownBackgroundDriver = new TransformingDriver(backgroundDriver, scaleDown,
                        "Preview Transform: Scaled 0.5x Background");
                commandPreview.setBackgroundDriver(scaledDownBackgroundDriver);

                CommandPreviewObserver previewObserver = new CommandPreviewObserver(CommandsFeature.getDriverCommandManager(),
                        commandPreview);
                CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(previewObserver);

                CanvasFeature.getChangePublisher().addSubscriber(() -> {
                ICanvas canvas = CanvasFeature.getCanvas();
                if (canvas != null) {
                        commandPreview.setBackgroundCommand(canvas.toCommand());
                } else {
                        commandPreview.setBackgroundCommand(null);
                }
                commandPreview.updatePreview(CommandsFeature.getDriverCommandManager().getCurrentCommand());
                });

                ICanvas initialCanvas = CanvasFeature.getCanvas();
                if (initialCanvas != null) {
                commandPreview.setBackgroundCommand(initialCanvas.toCommand());
                }
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
                application.addComponentMenuElement(Logger.class, "Fine level",
                                (ActionEvent e) -> logger.setLevel(Level.FINE));
                application.addComponentMenuElement(Logger.class, "Info level",
                                (ActionEvent e) -> logger.setLevel(Level.INFO));
                application.addComponentMenuElement(Logger.class, "Warning level",
                                (ActionEvent e) -> logger.setLevel(Level.WARNING));
                application.addComponentMenuElement(Logger.class, "Severe level",
                                (ActionEvent e) -> logger.setLevel(Level.SEVERE));
                application.addComponentMenuElement(Logger.class, "OFF logging",
                                (ActionEvent e) -> logger.setLevel(Level.OFF));
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
                                RecordingFeature.setup();
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
