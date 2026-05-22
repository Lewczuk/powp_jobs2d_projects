package edu.kis.powp.jobs2d.drivers.visitor;

import edu.kis.powp.jobs2d.drivers.RealTimeDriver;
import edu.kis.powp.jobs2d.drivers.RecordingDriver;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.logger.TrackingLoggerDriver;
import edu.kis.powp.jobs2d.drivers.packet_composite.CompositeDriver;
import edu.kis.powp.jobs2d.drivers.transformations.TransformingDriver;
import edu.kis.powp.jobs2d.drivers.usage.UsageMonitorDriver;

public class FullNameGetterVisitor implements DriverVisitor {

    private StringBuilder builder = new StringBuilder("Full name: ");

    @Override
    public void visit(CompositeDriver driver) {
        builder.append(driver.toString()).append(" ");

        for (VisitableDriver vd : driver.getDrivers()) {
            vd.accept(this);
        }
    }

    @Override
    public void visit(LineDriverAdapter adapter) {
        builder.append(adapter.toString());
    }

    @Override
    public void visit(TrackingLoggerDriver driver) {
        builder.append(driver.toString());
    }

    @Override
    public void visit(RealTimeDriver driver) {
        builder.append(driver.toString());
        driver.getInnerDriver().accept(this);
    }

    @Override
    public void visit(RecordingDriver driver) {
        builder.append(driver.toString());
        driver.getInnerDriver().accept(this);
    }

    @Override
    public void visit(TransformingDriver driver) {
        builder.append(driver.toString());
        driver.getInnerDriver().accept(this);
    }

    @Override
    public void visit(UsageMonitorDriver driver) {
        builder.append(driver.toString());
    }

    public String getAndResetFullName() {
        String ret = builder.toString();
        builder = new StringBuilder();
        return ret;
    }
}
