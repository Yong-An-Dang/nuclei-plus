package com.g3g4x5x6.nuclei.panel.dialog;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.NucleiConfig;
import com.g3g4x5x6.nuclei.panel.template.CopyToTemplatePanel;

import javax.swing.*;
import java.awt.*;

public class CopyToTemplateDialog extends JDialog {


    public CopyToTemplateDialog(JFrame parent) {
        super(parent);
        this.setTitle("HTTP流 -> PoC模板");
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(1000, 600));
        this.setPreferredSize(new Dimension(1000, 600));
        this.setMinimumSize(new Dimension(900, 500));
        this.setLocationRelativeTo(null);

        this.add(new CopyToTemplatePanel());
    }


    public static void main(String[] args) {
        try {
            if (NucleiConfig.getProperty("nuclei.theme").isEmpty()) UIManager.setLookAndFeel(new FlatLightLaf());
            else UIManager.setLookAndFeel(NucleiConfig.getProperty("nuclei.theme"));
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        UIManager.put("TextComponent.arc", 5);

        // 创建主框架作为父窗口
        JFrame parentFrame = new JFrame("Test Frame");
        parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parentFrame.setSize(new Dimension(400, 300));
        parentFrame.setLocationRelativeTo(null);

        // 显示父框架
        parentFrame.setVisible(true);

        // 创建并显示对话框
        CopyToTemplateDialog dialog = new CopyToTemplateDialog(parentFrame);
        dialog.setVisible(true);
    }
}
