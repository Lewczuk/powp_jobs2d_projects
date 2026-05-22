package edu.kis.powp.jobs2d.features;

import edu.kis.powp.jobs2d.drivers.DriverDecorator;
import edu.kis.powp.jobs2d.drivers.DriverManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DecoratorToggleListener implements ActionListener {

    private final DriverManager driverManager;
    private final DriverDecorator extension;
    private boolean enabled = false;

    public DecoratorToggleListener(DriverManager driverManager, DriverDecorator extension) {
        this.driverManager = driverManager;
        this.extension = extension;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (enabled) {
            driverManager.removeDecoratorExtension(extension);
            enabled = false;
        } else {
            driverManager.addDecoratorExtension(extension);
            enabled = true;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}
