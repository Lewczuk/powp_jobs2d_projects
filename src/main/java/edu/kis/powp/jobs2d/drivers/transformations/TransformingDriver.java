package edu.kis.powp.jobs2d.drivers.transformations;

import edu.kis.powp.jobs2d.drivers.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;

import edu.kis.powp.jobs2d.drivers.DriverDecorator;

public class TransformingDriver extends DriverDecorator {
    private final CoordinateTransformer transformer;
    private final String name;

    public TransformingDriver(VisitableDriver innerDriver, CoordinateTransformer transformer, String name) {
        super(innerDriver);
        this.transformer = transformer;
        this.name = name;
    }

    public TransformingDriver(CoordinateTransformer transformer, String name) {
        this(null, transformer, name);
    }

    @Override
    public void setPosition(int x, int y) {
        int[] newCoords = transformer.transform(x, y);
        super.setPosition(newCoords[0], newCoords[1]);
    }

    @Override
    public void operateTo(int x, int y) {
        int[] newCoords = transformer.transform(x, y);
        super.operateTo(newCoords[0], newCoords[1]);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void accept(DriverVisitor visitor) {
        visitor.visit(this);
    }
}