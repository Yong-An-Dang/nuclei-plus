package com.g3g4x5x6.nuclei.panel.dialog;

import com.g3g4x5x6.nuclei.ui.EditorPanel;
import com.g3g4x5x6.nuclei.ui.StatusBar;

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

}
