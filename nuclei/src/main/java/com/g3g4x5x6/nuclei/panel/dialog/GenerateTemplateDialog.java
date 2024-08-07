package com.g3g4x5x6.nuclei.panel.dialog;

import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.panel.template.GenerateTemplatePanel;

import javax.swing.*;


import java.awt.*;

public class GenerateTemplateDialog extends JDialog {

    public GenerateTemplateDialog() {
        super(NucleiApp.nuclei);
        this.setTitle("HTTP流 -> PoC模板");
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(1000, 600));
        this.setPreferredSize(new Dimension(1000, 600));
        this.setMinimumSize(new Dimension(900, 500));
        this.setLocationRelativeTo(null);

        this.add(new GenerateTemplatePanel());
    }

}
