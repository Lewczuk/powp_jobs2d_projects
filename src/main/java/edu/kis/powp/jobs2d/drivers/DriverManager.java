package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.jobs2d.drivers.logger.TrackingLoggerDriver;
import edu.kis.powp.jobs2d.drivers.packet_composite.CompositeDriver;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;
import edu.kis.powp.observer.Publisher;

import java.util.List;
import java.util.LinkedList;

/**
 * Driver manager provides means to setup the driver. It also enables other
 * components and features of the application to react on configuration changes.
 */
public class DriverManager {

    private VisitableDriver coreDriver = new TrackingLoggerDriver();
    private final List<DriverDecorator> decoratorExtensions = new LinkedList<>();
    private final List<VisitableDriver> parallelExtensions = new LinkedList<>();
    private Publisher changePublisher = new Publisher();

    public synchronized void setCurrentDriver(VisitableDriver driver) {
        coreDriver = driver;
        changePublisher.notifyObservers();
    }

    public synchronized void addDecoratorExtension(DriverDecorator extension) {
        decoratorExtensions.add(extension);
        changePublisher.notifyObservers();
    }

    public synchronized void removeDecoratorExtension(DriverDecorator extension) {
        decoratorExtensions.remove(extension);
        changePublisher.notifyObservers();
    }

    public synchronized void addExtension(VisitableDriver extension) {
        parallelExtensions.add(extension);
        changePublisher.notifyObservers();
    }

    public synchronized void removeExtension(VisitableDriver extension) {
        parallelExtensions.remove(extension);
        changePublisher.notifyObservers();
    }

    public synchronized VisitableDriver getCurrentDriver() {
        VisitableDriver chain = coreDriver;
        for (DriverDecorator decorator : decoratorExtensions) {
            decorator.setInnerDriver(chain);
            chain = decorator;
        }
        if (parallelExtensions.isEmpty()) {
            return chain;
        }
        CompositeDriver activeDriver = new CompositeDriver(coreDriver.toString());
        activeDriver.addDriver(chain);
        for (VisitableDriver ext : parallelExtensions) {
            activeDriver.addDriver(ext);
        }
        return activeDriver;
    }

    public synchronized VisitableDriver getCoreDriver() {
        return coreDriver;
    }

    /**
     * @return changePublisher.
     */
    public Publisher getChangePublisher() {
        return changePublisher;
    }
}
