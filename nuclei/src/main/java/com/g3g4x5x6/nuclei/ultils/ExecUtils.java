package com.g3g4x5x6.nuclei.ultils;

import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.model.GlobalConfigModel;
import com.g3g4x5x6.nuclei.panel.tab.RunningPanel;
import com.g3g4x5x6.nuclei.panel.console.ConsolePanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExecUtils {
    public static void runGlobalNucleiConfig(ConsolePanel consolePanel){
        Map<String, Object> config = new HashMap<>(CommonUtil.getNucleiConfigObject());
        config.put("target", CommonUtil.getTargets());

        // 配置对象
        String configPath = CommonUtil.getNucleiConfigFile(config);

        consolePanel.write("nuclei -config " + configPath + "\r");
        NucleiFrame.frameTabbedPane.setSelectedIndex(3);
        RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
    }

    public static void runNewTemplates(ConsolePanel consolePanel){
        Map<String, Object> config = new HashMap<>(CommonUtil.getNucleiConfigObject());
        config.put("target", CommonUtil.getTargets());

        // 移除 templates，workflows
        config.remove("templates");
        config.remove("workflows");

        // 配置指定选项
        config.put("new-templates", true);

        // 配置对象
        String configPath = CommonUtil.getNucleiConfigFile(config);

        consolePanel.write("nuclei -config " + configPath + "\r");
        NucleiFrame.frameTabbedPane.setSelectedIndex(3);
        RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
    }

    public static void runAutomaticScan(ConsolePanel consolePanel){
        Map<String, Object> config = new HashMap<>(CommonUtil.getNucleiConfigObject());
        config.put("target", CommonUtil.getTargets());

        // 移除 templates，workflows
        config.remove("templates");
        config.remove("workflows");

        // 配置指定选项
        config.put("automatic-scan", true);

        // 配置对象
        String configPath = CommonUtil.getNucleiConfigFile(config);

        consolePanel.write("nuclei -config " + configPath + "\r");
        NucleiFrame.frameTabbedPane.setSelectedIndex(3);
        RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
    }

    public static void runGroupBy(ConsolePanel consolePanel, ArrayList<String> templates, ArrayList<String> workflows){
        Map<String, Object> config = NucleiApp.nuclei.settingsPanel.getActiveConfigAllPanel().getNucleiConfig();

        config.put("target", CommonUtil.getTargets());

        config.remove("templates");
        config.remove("workflows");

        // 配置分组 templates，workflows
        config.put("templates", templates);
        config.put("workflows", workflows);

        // 配置对象
        String configPath = CommonUtil.getNucleiConfigFile(config);

        consolePanel.write("nuclei -config " + configPath + "\r");
        NucleiFrame.frameTabbedPane.setSelectedIndex(3);
        RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
    }
}
