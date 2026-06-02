package edu.kis.powp.jobs2d.features;

import edu.kis.legacy.drawer.shape.ILine;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.canvas.PaperFormat;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.observer.Publisher;

public class CanvasFeature implements IFeature {
    private static ICanvas currentFormat;
    private static ILine guidesLineType = LineFactory.getSpecialLine();
    private static Publisher changePublisher = new Publisher();

    @Override
    public void setup(Application application) {
        setupCanvasPlugin(application);
    }

    @Override
    public String getName() {
        return "Canvas";
    }

    public static void setupCanvasPlugin(Application application) {
        application.addComponentMenu(CanvasFeature.class, "Canvas", 0);

        for (PaperFormat format : PaperFormat.values()) {
            application.addComponentMenuElement(CanvasFeature.class, format.getName(), event -> setCanvas(format));
        }
    }

    public static void clearPanel() {
        DrawerFeature.getDrawerController().clearPanel();
        redrawCanvas(currentFormat, true);
    }

    public static void setCanvas(ICanvas format) {
        redrawCanvas(format, false);
    }

    public static ICanvas getCanvas() {
        return currentFormat;
    }

    public static void setGuidesLineType(ILine lineType) {
        guidesLineType = lineType;
    }

    public static ILine getGuidesLineType() {
        return guidesLineType;
    }

    private static void redrawCanvas(ICanvas format, boolean forceRedraw) {
        if (!forceRedraw && format == currentFormat) {
            return;
        }

        format.toCommand().execute(new LineDriverAdapter(DrawerFeature.getDrawerController(), guidesLineType, "Canvas Guides"));

        currentFormat = format;
        changePublisher.notifyObservers();
    }

    public static Publisher getChangePublisher() {
        return changePublisher;
    }
}
