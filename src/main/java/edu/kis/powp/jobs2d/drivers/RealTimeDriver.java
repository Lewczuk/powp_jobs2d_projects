package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.jobs2d.drivers.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;

import javax.swing.SwingUtilities;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class RealTimeDriver extends DriverDecorator {
    private final int operationToDelayMs;
    private final int setPositionDelayMs;
    private final String name;

    private volatile int currentX;
    private volatile int currentY;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public RealTimeDriver(VisitableDriver innerDriver, int operationToDelayMs, int setPositionDelayMs, String name) {
        super(innerDriver);
        if (operationToDelayMs <= 0 || setPositionDelayMs <= 0) {
            throw new IllegalArgumentException("Delay must be a positive integer (milliseconds)!");
        }
        this.operationToDelayMs = operationToDelayMs;
        this.setPositionDelayMs = setPositionDelayMs;
        this.name = name;
    }

    public RealTimeDriver(int operationToDelayMs, int setPositionDelayMs, String name) {
        this(null, operationToDelayMs, setPositionDelayMs, name);
    }

    @Override
    public void operateTo(int x, int y) {
        int startX = currentX;
        int startY = currentY;

        currentX = x;
        currentY = y;

        VisitableDriver inner = getInnerDriver();
        if (inner != null) {
            executor.submit(() -> moveRealTime(startX, startY, x, y, inner::operateTo, operationToDelayMs));
        }
    }

    @Override
    public void setPosition(int x, int y) {
        int startX = currentX;
        int startY = currentY;

        currentX = x;
        currentY = y;

        VisitableDriver inner = getInnerDriver();
        if (inner != null) {
            executor.submit(() -> moveRealTime(startX, startY, x, y, inner::setPosition, setPositionDelayMs));
        }
    }

    private void moveRealTime(int x0, int y0, int x1, int y1, BiConsumer<Integer, Integer> biConsumer, int delay) {
        int x = x0;
        int y = y0;

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int stepX = x0 < x1 ? 1 : -1;
        int stepY = y0 < y1 ? 1 : -1;

        int error = dx - dy;

        while (true) {
            int fx = x;
            int fy = y;

            SwingUtilities.invokeLater(() -> biConsumer.accept(fx, fy));

            if (x == x1 && y == y1) {
                break;
            }

            int error2 = 2 * error;

            if (error2 > -dy) {
                error -= dy;
                x += stepX;
            }
            if (error2 < dx) {
                error += dx;
                y += stepY;
            }

            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
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