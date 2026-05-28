package edu.kis.powp.jobs2d.features.history;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.kis.powp.appbase.gui.WindowComponent;

public class HistoryWindow extends JFrame implements WindowComponent {

    private HistoryManager historyManager;
    private javax.swing.JList<HistoryEntry> historyListUI;
    private javax.swing.DefaultListModel<HistoryEntry> listModel;
    private JButton btnLoadCommand;
    private javax.swing.JSpinner limitSpinner;

    private static final long serialVersionUID = 1L;

    public HistoryWindow(HistoryManager historyManager) {
        this.setTitle("Command History");
        this.setSize(400, 400);
        Container content = this.getContentPane();
        content.setLayout(new GridBagLayout());

        this.historyManager = historyManager;

        GridBagConstraints c = new GridBagConstraints();

        listModel = new javax.swing.DefaultListModel<>();
        historyListUI = new javax.swing.JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(historyListUI);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 1;
        content.add(scrollPane, c);

        JPanel bottomPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        javax.swing.JLabel limitLabel = new javax.swing.JLabel("Limit:");
        bottomPanel.add(limitLabel);

        limitSpinner = new javax.swing.JSpinner(
                new javax.swing.SpinnerNumberModel(10, 1, 1000, 1));
        bottomPanel.add(limitSpinner);

        btnLoadCommand = new JButton("Load command");
        bottomPanel.add(btnLoadCommand);

        JButton btnClearHistory = new JButton("Clear history");
        btnClearHistory.addActionListener((ActionEvent e) -> this.clearHistory());
        bottomPanel.add(btnClearHistory);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0;
        content.add(bottomPanel, c);

        updateHistoryField();
    }

    private void clearHistory() {
        historyManager.clearHistory();
    }

    public void setLimitValue(int value) {
        limitSpinner.setValue(value);
    }

    public void addLimitChangeListener(javax.swing.event.ChangeListener listener) {
        limitSpinner.addChangeListener(listener);
    }

    public HistoryEntry getSelectedHistoryEntry() {
        return historyListUI.getSelectedValue();
    }

    public void addLoadButtonListener(java.awt.event.ActionListener listener) {
        btnLoadCommand.addActionListener(listener);
    }

    public void updateHistoryField() {
        listModel.clear();
        List<HistoryEntry> historyList = historyManager.getHistoryList();
        for (HistoryEntry entry : historyList) {
            listModel.addElement(entry);
        }
    }

    @Override
    public void HideIfVisibleAndShowIfHidden() {
        if (this.isVisible()) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
    }
}
