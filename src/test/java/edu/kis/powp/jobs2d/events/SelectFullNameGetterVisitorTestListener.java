package edu.kis.powp.jobs2d.events;

import edu.kis.powp.jobs2d.drivers.visitor.FullNameGetterVisitor;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;
import edu.kis.powp.jobs2d.features.DriverFeature;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class SelectFullNameGetterVisitorTestListener implements ActionListener {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private FullNameGetterVisitor visitor = null;

    public SelectFullNameGetterVisitorTestListener(FullNameGetterVisitor fullNameGetterVisitor) {
        visitor = fullNameGetterVisitor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        VisitableDriver driver = DriverFeature.getDriverManager().getCurrentDriver();
        if (driver == null) {
            logger.info("No driver selected.");
            return;
        }

        driver.accept(visitor);
        logger.info("Full name for current driver: " + visitor.getAndResetFullName());
    }
}
