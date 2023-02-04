package com.g3g4x5x6.nuclei.panel.settings;


import com.g3g4x5x6.nuclei.panel.settings.template.GlobalTemplatePanel;
import com.g3g4x5x6.nuclei.panel.settings.template.GlobalWorkflowPanel;

import javax.swing.*;
import java.awt.*;

public class SettingTemplate extends JPanel {

    private final GlobalTemplatePanel globalTemplatePanel;
    private final GlobalWorkflowPanel globalWorkflowPanel;

    public SettingTemplate(){
        this.setLayout(new BorderLayout());

        globalTemplatePanel = new GlobalTemplatePanel();
        globalWorkflowPanel = new GlobalWorkflowPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(580);
        splitPane.setLeftComponent(globalTemplatePanel);
        splitPane.setRightComponent(globalWorkflowPanel);

        this.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * 获取 `PoC` 模板文本
     * @return PoC’s text
     */
    public String getTemplateText(){
        return globalTemplatePanel.getTextArea().getText();
    }

    /**
     * 获取 `PoC` 工作流模板文本
     * @return PoC workflow’s text
     */
    public String getWorkflowText(){
        return globalWorkflowPanel.getTextArea().getText();
    }
}
