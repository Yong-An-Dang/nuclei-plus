package com.g3g4x5x6.nuclei.panel.dialog;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.panel.console.ConsolePanel;

import javax.swing.*;
import java.awt.*;

public class RunningDialog extends JDialog {
    private final ConsolePanel console = new ConsolePanel();
    private final JToolBar toolBar = new JToolBar();

    private final JButton refreshBtn = new JButton(new FlatSVGIcon("icons/rerun.svg"));

    public RunningDialog(JFrame parent) {
        super(parent);
        this.setTitle("模板执行终端");
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(1000, 600));
        this.setPreferredSize(new Dimension(1000, 600));
        this.setMinimumSize(new Dimension(900, 500));
        this.setLocationRelativeTo(null);

        initToolBar();

        this.add(toolBar, BorderLayout.NORTH);
        this.add(console, BorderLayout.CENTER);
    }

    private void initToolBar() {
        refreshBtn.setToolTipText("");

        toolBar.add(refreshBtn);
    }

    public void runCommand(String command) {
        console.write(command);
    }
}
