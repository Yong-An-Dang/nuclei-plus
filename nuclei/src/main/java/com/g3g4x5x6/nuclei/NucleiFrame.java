package com.g3g4x5x6.nuclei;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.model.GlobalConfigModel;
import com.g3g4x5x6.nuclei.panel.EditTemplatePanel;
import com.g3g4x5x6.nuclei.panel.RunningPanel;
import com.g3g4x5x6.nuclei.panel.SettingsPanel;
import com.g3g4x5x6.nuclei.panel.TemplatesPanel;
import com.g3g4x5x6.nuclei.panel.connector.ConsolePanel;
import com.g3g4x5x6.nuclei.ultils.CommonUtil;
import com.g3g4x5x6.nuclei.ultils.ExecUtils;
import com.g3g4x5x6.nuclei.ultils.NucleiConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static com.formdev.flatlaf.FlatClientProperties.*;

@Slf4j
public class NucleiFrame extends JFrame {
    public static JTabbedPane frameTabbedPane;

    public static String reportDir = NucleiConfig.getProperty("nuclei.report.path");
    public static String templatesDir = NucleiConfig.getProperty("nuclei.templates.path");
    public static GlobalConfigModel globalConfigModel = new GlobalConfigModel();

    // look to the master,follow the master,walk with the master,see through the master,become the master.
    // 寻找大师，追随大师，与师偕行，领悟大师，成为大师
    private final JLabel mottoLabel = new JLabel("寻找大师，追随大师，成为大师，超越大师");

    private final TemplatesPanel templatesPanel = new TemplatesPanel();
    private final SettingsPanel settingsPanel = new SettingsPanel();
    private final RunningPanel runningPanel = new RunningPanel();

    private JMenuBar menuBar;
    private final JMenu fileMenu = new JMenu("开始");
    private final JMenu editMenu = new JMenu("编辑");
    private final JMenu searchMenu = new JMenu("搜索");
    private final JMenu viewMenu = new JMenu("视图");
    private final JMenu encodeMenu = new JMenu("编码");
    private final JMenu langMenu = new JMenu("语言");
    private final JMenu settingsMenu = new JMenu("设置");
    private final JMenu macroMenu = new JMenu("宏");
    private final JMenu runMenu = new JMenu("运行");
    private final JMenu pluginMenu = new JMenu("插件");
    private final JMenu winMenu = new JMenu("窗口");
    private final JMenu aboutMenu = new JMenu("关于");

    private final JPopupMenu trailPopupMenu = new JPopupMenu();

    public NucleiFrame() {
        this.setLayout(new BorderLayout());
        this.setTitle("PoC-概念验证框架");
        this.setSize(new Dimension(1200, 700));
        this.setPreferredSize(new Dimension(1200, 700));
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());

        initMenuBar();

        initToolBar();

        initTabbedPane();
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(searchMenu);
        menuBar.add(viewMenu);
        menuBar.add(encodeMenu);
        menuBar.add(langMenu);
        menuBar.add(settingsMenu);
        menuBar.add(macroMenu);
        menuBar.add(runMenu);
        menuBar.add(pluginMenu);
        menuBar.add(winMenu);
        menuBar.add(aboutMenu);

        // 置顶图标按钮
        FlatToggleButton toggleButton = new FlatToggleButton();
        toggleButton.setIcon(new FlatSVGIcon("icons/pinTab.svg"));
        toggleButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        toggleButton.setToolTipText("窗口置顶");
        toggleButton.setFocusable(false);
        toggleButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toggleButton.isSelected()) {
                    setAlwaysOnTop(true);
                    toggleButton.setToolTipText("取消置顶");
                } else {
                    setAlwaysOnTop(false);
                    toggleButton.setToolTipText("窗口置顶");
                }
            }
        });

        // 程序退出图标按钮
        FlatButton closeBtn = new FlatButton();
        closeBtn.setIcon(new FlatSVGIcon("icons/popFrame.svg"));
        closeBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        closeBtn.setFocusable(false);
        closeBtn.addActionListener(e -> {
            int i = JOptionPane.showConfirmDialog(NucleiApp.nuclei, "是否确认退出程序？", "退出", JOptionPane.OK_CANCEL_OPTION);
            if (i == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        menuBar.add(Box.createGlue());
        menuBar.add(toggleButton);
        menuBar.add(closeBtn);

        // 初始化一级菜单
        initMenu();

        this.setJMenuBar(menuBar);
    }

    private void initMenu() {
        // pluginIcon.svg
        JMenuItem openSpaceItem = new JMenuItem("打开工作目录");
        openSpaceItem.setIcon(new FlatSVGIcon("icons/pluginIcon.svg"));
        openSpaceItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(new File(NucleiConfig.getWorkPath()));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }).start();
            }
        });
        fileMenu.add(openSpaceItem);

        // help
        JMenuItem helpItem = new JMenuItem("帮助");
        helpItem.setIcon(new FlatSVGIcon("icons/help.svg"));
        helpItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditTemplatePanel editPanel = new EditTemplatePanel();
                editPanel.setTitle("nuclei -h");
                editPanel.setIcon(new FlatSVGIcon("icons/help.svg"));
                editPanel.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_UNIX_SHELL);

                InputStream helpIn = NucleiFrame.class.getClassLoader().getResourceAsStream("help/help_all.txt");
                assert helpIn != null;
                BufferedReader in = new BufferedReader(new InputStreamReader(helpIn));

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                editPanel.setTextArea(stringBuilder.toString());

                frameTabbedPane.addTab(editPanel.getTitle(), editPanel.getIcon(), editPanel);
                frameTabbedPane.setSelectedIndex(frameTabbedPane.getTabCount() - 1);
            }
        });
        aboutMenu.add(helpItem);

    }

    private void initToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton executeBtn = new JButton(new FlatSVGIcon("icons/execute.svg"));
        executeBtn.setToolTipText("默认新建终端运行（右键可选择已有终端运行）");
        executeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("GlobalConfigModel: \n" + globalConfigModel.toString());

                if (e.getButton() == 3) {
                    JPopupMenu popupMenu = new JPopupMenu();

                    LinkedHashMap<String, ConsolePanel> consolePanels = runningPanel.getConsolePanels();
                    for (String title : consolePanels.keySet()) {
                        JMenuItem tempItem = new JMenuItem(title);
                        tempItem.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                log.debug("Execute in " + title);
                                if (!NucleiApp.nuclei.getSettingsPanel().getTargetSetting().getTargetText().strip().equals("")) {
                                    ExecUtils.runGlobalNucleiConfig(consolePanels.get(title));

                                    // 跳转至运行终端
                                    NucleiFrame.frameTabbedPane.setSelectedIndex(2);
                                    RunningPanel.tabbedPane.setSelectedComponent(consolePanels.get(title));
                                } else {
                                    JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
                                }
                            }
                        });
                        popupMenu.add(tempItem);
                    }
                    popupMenu.show(executeBtn, e.getX(), e.getY());
                } else {
                    if (!NucleiApp.nuclei.getSettingsPanel().getTargetSetting().getTargetText().strip().equals("")) {
                        // 创建终端执行任务
                        ConsolePanel consolePanel = runningPanel.createConsole();

                        ExecUtils.runGlobalNucleiConfig(consolePanel);

                        // 跳转至运行终端
                        NucleiFrame.frameTabbedPane.setSelectedIndex(2);
                        RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
                    } else {
                        JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        // Target.svg
        JButton targetBtn = new JButton(new FlatSVGIcon("icons/Target.svg"));
        targetBtn.setToolTipText("设置全局目标");
        targetBtn.addActionListener(e -> {
            NucleiFrame.frameTabbedPane.setSelectedIndex(1);
            SettingsPanel.tabbedPane.setSelectedIndex(0);
        });

        // new FlatSVGIcon("icons/template.svg")
        JButton templateBtn = new JButton(new FlatSVGIcon("icons/template.svg"));
        templateBtn.setToolTipText("查看已配置PoC");
        templateBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NucleiFrame.frameTabbedPane.setSelectedIndex(1);
                SettingsPanel.tabbedPane.setSelectedIndex(1);
            }
        });

        // cwmInvite.svg
        JButton debugBtn = new JButton(new FlatSVGIcon("icons/cwmInvite.svg"));
        debugBtn.setToolTipText("调试及代理配置");
        debugBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NucleiFrame.frameTabbedPane.setSelectedIndex(1);
                SettingsPanel.tabbedPane.setSelectedIndex(9);
            }
        });


        JButton validBtn = new JButton(new FlatSVGIcon("icons/copy.svg"));
        validBtn.setToolTipText("复制漏洞目标");
        validBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = Path.of(NucleiConfig.getProperty("nuclei.report.path"), "issues.txt").toString().replace("\\", "\\\\");
                if (Files.exists(Path.of(fileName))) {
                    // 读取文件内容到Stream流中，按行读取
                    Stream<String> lines = Files.lines(Paths.get(fileName));

                    StringBuilder sb = new StringBuilder();
                    // 随机行顺序进行数据处理
                    lines.forEach(ele -> {
                        sb.append(CommonUtil.urlRegex(ele)).append("\n");
                    });
                    CommonUtil.setClipboardString(sb.toString());
                } else {
                    CommonUtil.setClipboardString("文件不存在");
                }
            }
        });

        toolBar.add(executeBtn);
        toolBar.addSeparator();
        toolBar.add(targetBtn);
        toolBar.add(templateBtn);
        toolBar.addSeparator();
        toolBar.add(debugBtn);
        toolBar.add(validBtn);

        toolBar.add(Box.createGlue());
        toolBar.add(new JLabel(""));
        toolBar.add(Box.createGlue());
        toolBar.add(mottoLabel);
        toolBar.add(Box.createGlue());

        this.add(toolBar, BorderLayout.NORTH);
    }

    private void initTabbedPane() {
        frameTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        initClosableTabs(frameTabbedPane);
        customComponents();
        frameTabbedPane.addTab("Templates", new FlatSVGIcon("icons/pinTab.svg"), templatesPanel);
        frameTabbedPane.addTab("Settings", new FlatSVGIcon("icons/pinTab.svg"), settingsPanel);
        frameTabbedPane.addTab("Running", new FlatSVGIcon("icons/pinTab.svg"), runningPanel);

        this.add(frameTabbedPane, BorderLayout.CENTER);
    }

    private void initClosableTabs(JTabbedPane frameTabbedPane) {
        frameTabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        frameTabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        frameTabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
                    if (tabIndex >= 3) {
                        frameTabbedPane.removeTabAt(tabIndex);
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
                log.debug("Add EditPanel");
                // TODO 添加 Template
                EditTemplatePanel editPanel = new EditTemplatePanel();
                frameTabbedPane.addTab(editPanel.getTitle(), editPanel.getIcon(), editPanel);
                frameTabbedPane.setSelectedIndex(frameTabbedPane.getTabCount() - 1);
            }
        });

        JMenuItem reportItem = new JMenuItem("查看扫描报告");
        reportItem.setIcon(new FlatSVGIcon("icons/MarkdownPlugin.svg"));
        reportItem.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop.getDesktop().open(new File(reportDir));
            }
        });
        trailPopupMenu.add(reportItem);

        JMenuItem templateItem = new JMenuItem("打开模板目录");
        templateItem.setIcon(new FlatSVGIcon("icons/template.svg"));
        templateItem.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop.getDesktop().open(new File(templatesDir));
            }
        });
        trailPopupMenu.add(templateItem);

        // TODO 选项卡面板后置工具栏
        String iconPath = "icons/windows.svg";
        JButton trailMenuBtn = new JButton(new FlatSVGIcon(iconPath));
        trailMenuBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                trailPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        trailing.add(addBtn);
        trailing.add(Box.createHorizontalGlue());
        trailing.add(trailMenuBtn);
        frameTabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    public TemplatesPanel getTemplatesPanel() {
        return templatesPanel;
    }

    public SettingsPanel getSettingsPanel() {
        return settingsPanel;
    }

    public RunningPanel getRunningPanel() {
        return runningPanel;
    }
}
