package com.g3g4x5x6.nuclei.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.panel.settings.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SettingsPanel extends JPanel {
    // TODO 搞个全局配置对象

    private final JButton newBtn = new JButton(new FlatSVGIcon("icons/addFile.svg"));
    private final JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private final JButton saveBtn = new JButton(new FlatSVGIcon("icons/menu-saveall.svg"));
    private final JButton terminalBtn = new JButton(new FlatSVGIcon("icons/changeView.svg"));

    public static JTabbedPane tabbedPane;
    SettingTarget targetSetting = new SettingTarget();
    SettingTemplate templateSetting = new SettingTemplate();
    SettingFiltering filteringSetting = new SettingFiltering();
    SettingOutput outputSetting = new SettingOutput();
    SettingConfiguration configurationSetting = new SettingConfiguration();
    SettingInteractsh interactshSetting = new SettingInteractsh();
    SettingRateLimit rateLimitSetting = new SettingRateLimit();
    SettingOptimization optimizationSetting = new SettingOptimization();
    SettingHeadless headlessSetting = new SettingHeadless();
    SettingDebug debugSetting = new SettingDebug();
    SettingUpdate updateSetting = new SettingUpdate();
    SettingStatistics statisticsSetting = new SettingStatistics();

    public SettingsPanel() {
        this.setLayout(new BorderLayout());

        initToolBar();

        initTabbedPane();
    }

    private void initToolBar(){
        JToolBar toolBar = new JToolBar(SwingConstants.HORIZONTAL);
        toolBar.setFloatable(false);

        newBtn.setToolTipText("新建全局配置");
        newBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
            }
        });

        toolBar.add(newBtn);
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.addSeparator();
        toolBar.add(terminalBtn);
        this.add(toolBar, BorderLayout.NORTH);
    }

    private void initTabbedPane(){
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        // add Tab
        tabbedPane.addTab("Target", new FlatSVGIcon("icons/Target.svg"), targetSetting);
        tabbedPane.addTab("Templates", new FlatSVGIcon("icons/template.svg"), templateSetting);
        tabbedPane.addTab("Filtering", new FlatSVGIcon("icons/shortcutFilter.svg"), filteringSetting);
        tabbedPane.addTab("Output", new FlatSVGIcon("icons/output.svg"), outputSetting);
        tabbedPane.addTab("Configurations", new FlatSVGIcon("icons/pinTab.svg"), configurationSetting);
        tabbedPane.addTab("Interactsh", new FlatSVGIcon("icons/pinTab.svg"), interactshSetting);
        tabbedPane.addTab("RateLimit", new FlatSVGIcon("icons/overhead.svg"), rateLimitSetting);
        tabbedPane.addTab("Optimizations", new FlatSVGIcon("icons/pinTab.svg"), optimizationSetting);
        tabbedPane.addTab("Headless", new FlatSVGIcon("icons/Header_level_down.svg"), headlessSetting);
        tabbedPane.addTab("Debug", new FlatSVGIcon("icons/cwmInvite.svg"), debugSetting);
        tabbedPane.addTab("Update", new FlatSVGIcon("icons/updateRunningApplication.svg"), updateSetting);
        tabbedPane.addTab("Statistics", new FlatSVGIcon("icons/pinTab.svg"), statisticsSetting);

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    public SettingTarget getTargetSetting() {
        return targetSetting;
    }

    public void setTargetSetting(SettingTarget targetSetting) {
        this.targetSetting = targetSetting;
    }

    public SettingTemplate getTemplateSetting() {
        return templateSetting;
    }

    public void setTemplateSetting(SettingTemplate templateSetting) {
        this.templateSetting = templateSetting;
    }

    public SettingFiltering getFilteringSetting() {
        return filteringSetting;
    }

    public void setFilteringSetting(SettingFiltering filteringSetting) {
        this.filteringSetting = filteringSetting;
    }

    public SettingOutput getOutputSetting() {
        return outputSetting;
    }

    public void setOutputSetting(SettingOutput outputSetting) {
        this.outputSetting = outputSetting;
    }

    public SettingConfiguration getConfigurationSetting() {
        return configurationSetting;
    }

    public void setConfigurationSetting(SettingConfiguration configurationSetting) {
        this.configurationSetting = configurationSetting;
    }

    public SettingInteractsh getInteractshSetting() {
        return interactshSetting;
    }

    public void setInteractshSetting(SettingInteractsh interactshSetting) {
        this.interactshSetting = interactshSetting;
    }

    public SettingRateLimit getRateLimitSetting() {
        return rateLimitSetting;
    }

    public void setRateLimitSetting(SettingRateLimit rateLimitSetting) {
        this.rateLimitSetting = rateLimitSetting;
    }

    public SettingOptimization getOptimizationSetting() {
        return optimizationSetting;
    }

    public void setOptimizationSetting(SettingOptimization optimizationSetting) {
        this.optimizationSetting = optimizationSetting;
    }

    public SettingHeadless getHeadlessSetting() {
        return headlessSetting;
    }

    public void setHeadlessSetting(SettingHeadless headlessSetting) {
        this.headlessSetting = headlessSetting;
    }

    public SettingDebug getDebugSetting() {
        return debugSetting;
    }

    public void setDebugSetting(SettingDebug debugSetting) {
        this.debugSetting = debugSetting;
    }

    public SettingUpdate getUpdateSetting() {
        return updateSetting;
    }

    public void setUpdateSetting(SettingUpdate updateSetting) {
        this.updateSetting = updateSetting;
    }

    public SettingStatistics getStatisticsSetting() {
        return statisticsSetting;
    }

    public void setStatisticsSetting(SettingStatistics statisticsSetting) {
        this.statisticsSetting = statisticsSetting;
    }
}
