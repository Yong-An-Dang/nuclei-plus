package com.g3g4x5x6.nuclei.panel.dialog;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.nuclei.ui.EditorPanel;
import com.g3g4x5x6.nuclei.ui.StatusBar;
import com.g3g4x5x6.nuclei.ultils.NucleiConfig;

import javax.swing.*;
import java.awt.*;

public class EditorDialog extends JDialog {

    public EditorDialog(JFrame frame, String title, String filePath){
        super(frame);
        this.setTitle(title);
        this.setSize(new Dimension(800, 600));
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(frame);
        this.setModal(true);

        EditorPanel editorPanel = new EditorPanel(filePath);

        this.add(editorPanel, BorderLayout.CENTER);
        this.add(new StatusBar(), BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initFlatLaf();

            EditorDialog editorDialog = new EditorDialog(null, "配置编辑器", "C:\\Users\\18312\\.nuclei-plus\\config\\nuclei.properties");
            editorDialog.setLocationRelativeTo(null);
            editorDialog.setVisible(true);
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
