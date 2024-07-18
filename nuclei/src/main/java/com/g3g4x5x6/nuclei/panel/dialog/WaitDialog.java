package com.g3g4x5x6.nuclei.panel.dialog;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.nuclei.NucleiConfig;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

@Deprecated
public class WaitDialog extends JDialog {

    public WaitDialog(JFrame frame) {
        super(frame);
        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(400, 70));
        this.setLocationRelativeTo(frame);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        JToolBar toolBar = new JToolBar();
        toolBar.setBorder(new TitledBorder("主程序加载中..."));
        toolBar.add(progressBar);

        this.add(toolBar);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initFlatLaf();

            WaitDialog waitDialog = new WaitDialog(null);
            waitDialog.setVisible(true);
        });
    }

    private static void initFlatLaf() {
        try {
            if (NucleiConfig.getProperty("nuclei.theme").equals(""))
                UIManager.setLookAndFeel(new FlatLightLaf());
            else
                UIManager.setLookAndFeel(NucleiConfig.getProperty("nuclei.theme"));
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        UIManager.put("TextComponent.arc", 5);
    }
}