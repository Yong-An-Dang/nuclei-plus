package com.g3g4x5x6.nuclei.panel.setting;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.panel.setting.template.GlobalTemplatePanel;
import com.g3g4x5x6.nuclei.panel.setting.template.GlobalWorkflowPanel;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ConfigPanel extends JPanel {
    private String title = "Default";
    private final NucleiConfigPanel configPanel = new NucleiConfigPanel();
    private final GlobalTemplatePanel globalTemplatePanel = new GlobalTemplatePanel();
    private final GlobalWorkflowPanel globalWorkflowPanel = new GlobalWorkflowPanel();

    private FlatSVGIcon icon = new FlatSVGIcon("icons/output.svg");

    public ConfigPanel() {
        this.setLayout(new BorderLayout());

        JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setLeftComponent(globalTemplatePanel);
        verticalSplitPane.setDividerLocation(250);
        verticalSplitPane.setRightComponent(globalWorkflowPanel);

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

    public void addWorkflows(String workflowPath){
        globalWorkflowPanel.addWorkflows(workflowPath);
    }

    public void addTemplates(String templatePath){
        globalTemplatePanel.addTemplates(templatePath);
    }

    public Map<String, Object> getNucleiConfig() {
        Yaml yaml = new Yaml(new SafeConstructor());
        Map<String, Object> config = (Map<String, Object>) yaml.load(configPanel.getConfig());
        config.put("templates", globalTemplatePanel.getTemplates());
        config.put("workflows", globalWorkflowPanel.getWorkflows());
        return config;
    }
}
