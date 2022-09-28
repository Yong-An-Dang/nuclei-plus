package com.g3g4x5x6.nuclei.panel.settings;

import com.g3g4x5x6.nuclei.panel.settings.target.StringTargetPanel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;


@Slf4j
public class SettingTarget extends JPanel {
    private StringTargetPanel stringTargetPanel = new StringTargetPanel();

    public static SettingTarget settingTarget;

    public static SettingTarget getInstance(){
        if (settingTarget == null){
            settingTarget = new SettingTarget();
        }
        return settingTarget;
    }

    public SettingTarget() {
        this.setLayout(new BorderLayout());
        this.add(stringTargetPanel, BorderLayout.CENTER);
    }

    /**
     * 获取目标列表文本
     * @return targets‘ text
     */
    public String getTargetText(){
        return stringTargetPanel.getTextArea().getText();
    }
}
