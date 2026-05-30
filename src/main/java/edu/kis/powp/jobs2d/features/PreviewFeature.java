package edu.kis.powp.jobs2d.features;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.canvas.ICanvas;

import edu.kis.powp.jobs2d.command.gui.CommandPreviewObserver;
import edu.kis.powp.jobs2d.command.gui.CommandPreviewWindow;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.transformations.CoordinateTransformer;
import edu.kis.powp.jobs2d.drivers.transformations.TransformerFactory;
import edu.kis.powp.jobs2d.drivers.transformations.TransformingDriver;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;

public class PreviewFeature implements IFeature {

    private CommandPreviewWindow commandPreview;

    @Override
    public void setup(Application application) {
        commandPreview = new CommandPreviewWindow();
        application.addWindowComponent("Command Preview", commandPreview);

        DrawPanelController previewDrawController = commandPreview.getDrawPanelController();
        VisitableDriver basicDriver = new LineDriverAdapter(previewDrawController, LineFactory.getBasicLine(),
                "basic");
        CoordinateTransformer scaleDown = TransformerFactory.getScaleTransformer(TransformerFactory.DOUBLE_ZOOM_OUT);
        VisitableDriver scaledDownDriver = new TransformingDriver(basicDriver, scaleDown,
                "Preview Transform: Scaled 0.5x");
        commandPreview.setPreviewDriver(scaledDownDriver);

        CommandPreviewObserver previewObserver = new CommandPreviewObserver(
                CommandsFeature.getDriverCommandManager(),
                commandPreview,
                CanvasFeature::getCanvas);
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(previewObserver);

        VisitableDriver backgroundDriver = new LineDriverAdapter(previewDrawController,
                CanvasFeature.getGuidesLineType(),
                "background");
        VisitableDriver scaledDownBackgroundDriver = new TransformingDriver(backgroundDriver, scaleDown,
                "Preview Transform: Scaled 0.5x Background");
        commandPreview.setBackgroundDriver(scaledDownBackgroundDriver);

        CanvasFeature.getChangePublisher().addSubscriber(previewObserver);

        ICanvas initialCanvas = CanvasFeature.getCanvas();
        if (initialCanvas != null) {
            commandPreview.setBackgroundCommand(initialCanvas.toCommand());
        }
    }

    @Override
    public String getName() {
        return "Preview";
    }
}
