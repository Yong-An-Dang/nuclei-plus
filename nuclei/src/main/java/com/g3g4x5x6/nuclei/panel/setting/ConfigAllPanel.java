package com.g3g4x5x6.nuclei.panel.setting;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import javax.swing.*;
import java.awt.*;
import java.util.Map;


@Slf4j
public class ConfigAllPanel extends JPanel {
    private String title = "Default";
    private final ConfigNucleiPanel configPanel = new ConfigNucleiPanel();
    private final ConfigTemplatePanel configTemplatePanel = new ConfigTemplatePanel();
    private final ConfigWorkflowPanel configWorkflowPanel = new ConfigWorkflowPanel();

    private FlatSVGIcon icon = new FlatSVGIcon("icons/output.svg");

    public ConfigAllPanel() {
        this.setLayout(new BorderLayout());

        JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setLeftComponent(configTemplatePanel);
        verticalSplitPane.setDividerLocation(250);
        verticalSplitPane.setRightComponent(configWorkflowPanel);

        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        horizontalSplitPane.setDividerLocation(580);
        horizontalSplitPane.setLeftComponent(verticalSplitPane);
        horizontalSplitPane.setRightComponent(configPanel);

        this.add(horizontalSplitPane, BorderLayout.CENTER);
    }

    public FlatSVGIcon getIcon() {
        return icon;
    }

    public void setIcon(FlatSVGIcon icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void addWorkflows(String workflowPath) {
        configWorkflowPanel.addWorkflows(workflowPath);
    }

    public void addTemplates(String templatePath) {
        configTemplatePanel.addTemplates(templatePath);
    }

    public Map<String, Object> getNucleiConfig() {
        Yaml yaml = new Yaml(new SafeConstructor());
        Map<String, Object> config = yaml.load(configPanel.getConfig());
        config.put("templates", configTemplatePanel.getTemplates());
        config.put("workflows", configWorkflowPanel.getWorkflows());
        return config;
    }

    public void saveConfigToYaml(){
        Map<String, Object> config = getNucleiConfig();

    }
}
