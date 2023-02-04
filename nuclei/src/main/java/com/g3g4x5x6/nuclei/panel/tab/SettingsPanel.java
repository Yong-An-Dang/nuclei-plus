package com.g3g4x5x6.nuclei.panel.tab;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.panel.setting.*;
import com.g3g4x5x6.nuclei.ultils.DialogUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;


@Slf4j
public class SettingsPanel extends JPanel {
    public static JTabbedPane tabbedPane;

    public ConfigPanel configPanel = new ConfigPanel();
    public ConfigPanel activeConfigPanel = configPanel;

    public SettingsPanel() {
        this.setLayout(new BorderLayout());

        initTabbedPane();
    }

    private void initTabbedPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        initClosableTabs();
        customComponents();

        // add Tab
        tabbedPane.addTab("Default", new FlatSVGIcon("icons/output.svg"), configPanel);

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private void initClosableTabs() {
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
                    if (tabIndex >= 1) {
                        tabbedPane.removeTabAt(tabIndex);
                    }
                });
    }

    private void customComponents() {
        JToolBar trailing;
        trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        JButton addBtn = new JButton(new FlatSVGIcon("icons/add.svg"));
        addBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Add ConfigPanel");
                ConfigPanel tmpConfigPanel = new ConfigPanel();
                String configName = DialogUtil.input(SettingsPanel.this, "请输入配置名称");
                if (configName.strip().equals("")) {
                    DialogUtil.warn("配置名称不能为空");
                } else {
                    tmpConfigPanel.setTitle(configName);
                    tabbedPane.addTab(tmpConfigPanel.getTitle(), tmpConfigPanel.getIcon(), tmpConfigPanel);
                    tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                }
            }
        });

        // 选项卡面板后置工具栏

        String iconPath = "icons/windows.svg";
        JButton trailMenuBtn = new JButton(new FlatSVGIcon(iconPath));
        trailMenuBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug(String.valueOf(configPanel.getNucleiConfig()));
            }
        });

        trailing.add(addBtn);
        trailing.add(Box.createGlue());
        trailing.add(trailMenuBtn);
        tabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    public void setActiveConfigPanel(ConfigPanel activeConfigPanel) {
        this.activeConfigPanel = activeConfigPanel;
    }

}
