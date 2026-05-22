package edu.kis.powp.jobs2d.drivers.visitor;

import edu.kis.powp.jobs2d.drivers.RealTimeDriver;
import edu.kis.powp.jobs2d.drivers.RecordingDriver;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.logger.TrackingLoggerDriver;
import edu.kis.powp.jobs2d.drivers.packet_composite.CompositeDriver;
import edu.kis.powp.jobs2d.drivers.transformations.TransformingDriver;
import edu.kis.powp.jobs2d.drivers.usage.UsageMonitorDriver;

public interface DriverVisitor {
    public void visit(CompositeDriver driver);

    public void visit(LineDriverAdapter adapter);

    public void visit(TrackingLoggerDriver driver);

    public void visit(RealTimeDriver driver);

    public void visit(RecordingDriver driver);

    public void visit(TransformingDriver driver);

    public void visit(UsageMonitorDriver driver);
}
