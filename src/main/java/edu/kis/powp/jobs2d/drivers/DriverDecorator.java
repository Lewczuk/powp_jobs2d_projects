package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;

/**
 * Abstract base class for driver decorators.
 * Subclasses should call super.setPosition / super.operateTo to delegate
 * to the inner driver. Delegation is skipped silently when innerDriver is null
 * (e.g. before the decorator is wired up by DriverManager).
 */
public abstract class DriverDecorator implements VisitableDriver {

    private VisitableDriver innerDriver;

    protected DriverDecorator(VisitableDriver innerDriver) {
        this.innerDriver = innerDriver;
    }

    public VisitableDriver getInnerDriver() {
        return innerDriver;
    }

    public void setInnerDriver(VisitableDriver driver) {
        this.innerDriver = driver;
    }

    @Override
    public void setPosition(int x, int y) {
        if (innerDriver != null) {
            innerDriver.setPosition(x, y);
        }
    }

    @Override
    public void operateTo(int x, int y) {
        if (innerDriver != null) {
            innerDriver.operateTo(x, y);
        }
    }
}
