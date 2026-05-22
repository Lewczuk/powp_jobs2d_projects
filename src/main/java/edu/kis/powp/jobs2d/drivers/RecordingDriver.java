package edu.kis.powp.jobs2d.drivers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;
import edu.kis.powp.jobs2d.drivers.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;

/**
 * Extension driver that records all calls as command objects.
 * Recording can be temporarily disabled (used during playback).
 * Extends DriverDecorator so it can be used as a regular extension
 * in ExtensionFeature — the innerDriver is injected by DriverManager.
 */
public class RecordingDriver extends DriverDecorator {

    private final List<DriverCommand> recorded = new ArrayList<>();
    private boolean recordingEnabled = true;

    public RecordingDriver() {
        super(null);
    }

    /**
     * Enable or disable recording of subsequent driver calls.
     * When disabled, setPosition/operateTo will still delegate to the inner driver
     * but won't add commands to the recorded list.
     */
    public synchronized void setRecordingEnabled(boolean enabled) {
        this.recordingEnabled = enabled;
    }

    public synchronized boolean isRecordingEnabled() {
        return recordingEnabled;
    }

    public synchronized void clearRecorded() {
        recorded.clear();
    }

    public synchronized List<DriverCommand> getRecordedCommands() {
        return Collections.unmodifiableList(new ArrayList<>(recorded));
    }

    @Override
    public synchronized void setPosition(int x, int y) {
        if (recordingEnabled) {
            recorded.add(new SetPositionCommand(x, y));
        }
        super.setPosition(x, y);
    }

    @Override
    public synchronized void operateTo(int x, int y) {
        if (recordingEnabled) {
            recorded.add(new OperateToCommand(x, y));
        }
        super.operateTo(x, y);
    }

    @Override
    public synchronized String toString() {
        return "RecordingDriver -> " + getInnerDriver();
    }

    @Override
    public void accept(DriverVisitor visitor) {
        visitor.visit(this);
    }
}
