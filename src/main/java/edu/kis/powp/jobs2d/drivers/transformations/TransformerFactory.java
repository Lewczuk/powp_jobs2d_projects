package edu.kis.powp.jobs2d.drivers.transformations;

public class TransformerFactory {

    public static final double DOUBLE_ZOOM_OUT = 0.5;

    public static CoordinateTransformer getScaleTransformer(double scale) {
        return new ScaleTransformer(scale, scale);
    }
}
