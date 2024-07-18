package com.g3g4x5x6.nuclei;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.action.GroupByAction;
import com.g3g4x5x6.nuclei.model.GlobalConfigModel;
import com.g3g4x5x6.nuclei.panel.console.ConsolePanel;
import com.g3g4x5x6.nuclei.panel.dialog.EditorDialog;
import com.g3g4x5x6.nuclei.panel.search.SearchTabbedPanel;
import com.g3g4x5x6.nuclei.panel.tab.*;
import com.g3g4x5x6.nuclei.ui.StatusBar;
import com.g3g4x5x6.nuclei.ultils.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;

@Slf4j
public class NucleiFrame extends JFrame {
    public static JTabbedPane frameTabbedPane;
    public static JButton activeBtn;

    public static String templatesDir = NucleiConfig.getProperty("nuclei.templates.path");
    public static GlobalConfigModel globalConfigModel = new GlobalConfigModel();

    // look to the master,follow the master,walk with the master,see through the master,become the master.
    // 寻找大师，追随大师，与师偕行，领悟大师，成为大师
    private final JLabel mottoLabel = new JLabel(L.M("bar.tool.motto"));

    public final TemplatesPanel templatesPanel = new TemplatesPanel();
    public final StringTargetPanel targetPanel = new StringTargetPanel();
    public final SettingsPanel settingsPanel = new SettingsPanel();
    public final RunningPanel runningPanel = new RunningPanel();
    public final SearchTabbedPanel searchTabbedPanel = new SearchTabbedPanel();

    public final StatusBar statusBar = new StatusBar();

    private JMenuBar menuBar;
    private final JMenu fileMenu = new JMenu(L.M("bar.menu.open"));
    private final JMenu settingsMenu = new JMenu(L.M("bar.menu.setting"));
    private final JMenu runMenu = new JMenu(L.M("bar.menu.running"));
    private final JMenu pluginMenu = new JMenu(L.M("bar.menu.plugin"));
    private final JMenu winMenu = new JMenu(L.M("bar.menu.window"));
    private final JMenu aboutMenu = new JMenu(L.M("bar.menu.about"));

    private final JPopupMenu trailPopupMenu = new JPopupMenu();

    public NucleiFrame() {
        this.setLayout(new BorderLayout());
        this.setTitle(L.M("app.title"));
        this.setSize(new Dimension(1200, 700));
        this.setPreferredSize(new Dimension(1200, 700));
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());

        initMenuBar();

        initToolBar();

        initTabbedPane();

        initStatusBar();
    }

    private void initStatusBar() {
        this.add(statusBar, BorderLayout.SOUTH);
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        menuBar.add(runMenu);
        menuBar.add(pluginMenu);
        menuBar.add(winMenu);
        menuBar.add(aboutMenu);

        runMenu.add(new JMenuItem("敬请期待"));
        pluginMenu.add(new JMenuItem("敬请期待"));
//        winMenu.add(new GroupByAction(L.M("bar.menu.window.custom.group")));
        winMenu.add(new GroupByAction("自定义分组管理"));

        // 置顶图标按钮
        FlatToggleButton toggleButton = new FlatToggleButton();
        toggleButton.setIcon(new FlatSVGIcon("icons/pinTab.svg"));
        toggleButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        toggleButton.setToolTipText(L.M("bar.menu.icon.tips.always.enable"));
        toggleButton.setFocusable(false);
        toggleButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toggleButton.isSelected()) {
                    setAlwaysOnTop(true);
                    toggleButton.setToolTipText(L.M("bar.menu.icon.tips.always.disable"));
                } else {
                    setAlwaysOnTop(false);
                    toggleButton.setToolTipText(L.M("bar.menu.icon.tips.always.enable"));
                }
            }
        });

        // 程序退出图标按钮
        FlatButton closeBtn = new FlatButton();
        closeBtn.setIcon(new FlatSVGIcon("icons/popFrame.svg"));
        closeBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        closeBtn.setFocusable(false);
        closeBtn.addActionListener(e -> {
            int i = JOptionPane.showConfirmDialog(NucleiApp.nuclei, L.M("bar.menu.icon.quit.question"), L.M("bar.menu.icon.quit.title"), JOptionPane.OK_CANCEL_OPTION);
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
        // fileMenu
        JMenuItem newProjectItem = new JMenuItem(L.M("bar.menu.open.new"));
        newProjectItem.setIcon(new FlatSVGIcon("icons/newFolder.svg"));
        newProjectItem.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                String projectName = DialogUtil.input(NucleiFrame.this, "请输出项目名称（目录）");
                log.debug(projectName);
                if (projectName != null) {
                    if (projectName.isBlank())
                        DialogUtil.warn("【项目名称（目录）】不能为空");
                    else {
                        Files.createDirectories(Path.of(NucleiConfig.getWorkPath() + "/projects/" + projectName));
                        CommonUtil.createProjectStruct(projectName);

                        NucleiApp.nuclei.dispose();
                        NucleiConfig.projectName = projectName;
                        NucleiApp.createGUI();
                    }
                }
            }
        });

        JMenu openProjectMenu = new JMenu(L.M("bar.menu.open.open"));
        openProjectMenu.setIcon(new FlatSVGIcon("icons/menu-open.svg"));
        openProjectMenu.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("打开项目");
            }
        });

        JMenuItem openSpaceItem = new JMenuItem(L.M("bar.menu.open.dir.workspace"));
        openSpaceItem.setIcon(new FlatSVGIcon("icons/moduleDirectory.svg"));
        openSpaceItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(new File(NucleiConfig.getWorkPath()));
                    } catch (IOException ioException) {
                        ioException.fillInStackTrace();
                    }
                }).start();
            }
        });

        JMenuItem openTemplateItem = new JMenuItem(L.M("bar.menu.open.dir.templates"));
        openTemplateItem.setIcon(new FlatSVGIcon("icons/moduleDirectory.svg"));
        openTemplateItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(new File(NucleiConfig.getProperty("nuclei.templates.path")));
                    } catch (IOException ioException) {
                        ioException.fillInStackTrace();
                    }
                }).start();
            }
        });

        JMenuItem openProjectDirItem = new JMenuItem(L.M("bar.menu.open.dir.projects"));
        openProjectDirItem.setIcon(new FlatSVGIcon("icons/moduleDirectory.svg"));
        openProjectDirItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(new File(NucleiConfig.getWorkPath() + "/projects/" + NucleiConfig.projectName));
                    } catch (IOException ioException) {
                        ioException.fillInStackTrace();
                    }
                }).start();
            }
        });

        JMenuItem openReportDirItem = new JMenuItem(L.M("bar.menu.open.dir.reports"));
        openReportDirItem.setIcon(new FlatSVGIcon("icons/moduleDirectory.svg"));
        openReportDirItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(new File(ProjectUtil.reportDir()));
                    } catch (IOException ioException) {
                        ioException.fillInStackTrace();
                    }
                }).start();
            }
        });

        // Quit
        JMenuItem quitItem = new JMenuItem(L.M("bar.menu.open.quit"));
        quitItem.setIcon(new FlatSVGIcon("icons/exit.svg"));
        quitItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (DialogUtil.yesOrNo(NucleiFrame.this, "是否退出程序？") == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });

        fileMenu.add(newProjectItem);
        fileMenu.add(openProjectMenu);
        fileMenu.addSeparator();
        fileMenu.add(openSpaceItem);
        fileMenu.add(openTemplateItem);
        fileMenu.add(openProjectDirItem);
        fileMenu.add(openReportDirItem);
        fileMenu.addSeparator();
        fileMenu.add(quitItem);
        fileMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openProjectMenu.removeAll();
                File projects = new File(NucleiConfig.getWorkPath() + "/projects");
                for (File project : Objects.requireNonNull(projects.listFiles(File::isDirectory))) {
                    JMenuItem tmpItem = new JMenuItem(project.getName());
                    tmpItem.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // 打开已有项目
                            NucleiApp.nuclei.dispose();
                            NucleiConfig.projectName = project.getName();
                            NucleiApp.createGUI();
                        }
                    });
                    openProjectMenu.add(tmpItem);
                }
                if (openProjectMenu.getItemCount() == 0) {
                    openProjectMenu.add(new JMenuItem("<空>"));
                }
            }
        });

        // settingsMenu
        JMenuItem globalItem = new JMenuItem(L.M("bar.menu.setting.global"));
        globalItem.setToolTipText(L.M("bar.menu.setting.global.tips"));
        globalItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditorDialog editorDialog = new EditorDialog(NucleiFrame.this, "全局配置",
                        Path.of(NucleiConfig.getConfigPath(), "nuclei.properties").toString());
                editorDialog.setLocationRelativeTo(null);
                editorDialog.setVisible(true);
            }
        });

        JMenuItem projectItem = new JMenuItem(L.M("bar.menu.setting.project"));
        projectItem.setToolTipText(L.M("bar.menu.setting.project.tips"));
        projectItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditorDialog editorDialog = new EditorDialog(NucleiFrame.this, "项目配置", ProjectUtil.ConfigFilePath());
                editorDialog.setLocationRelativeTo(null);
                editorDialog.setVisible(true);
            }
        });

        settingsMenu.add(globalItem);
        settingsMenu.addSeparator();
        settingsMenu.add(projectItem);

        // help
        JMenuItem helpItem = new JMenuItem(L.M("bar.menu.about.help"));
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
                    exception.fillInStackTrace();
                }
                editPanel.setTextArea(stringBuilder.toString());

                frameTabbedPane.addTab(editPanel.getTitle(), editPanel.getIcon(), editPanel);
                frameTabbedPane.setSelectedIndex(frameTabbedPane.getTabCount() - 1);
            }
        });

        JMenuItem supportItem = new JMenuItem(L.M("bar.menu.about.support"));
        supportItem.setToolTipText("https://yong-an-dang.github.io/nuclei-plus/");
        supportItem.setIcon(new FlatSVGIcon("icons/cwmInvite.svg"));
        supportItem.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop.getDesktop().browse(new URI("https://yong-an-dang.github.io/nuclei-plus/"));
            }
        });

        JMenuItem aboutItem = new JMenuItem(L.M("bar.menu.about.nuclei-plus"));
        aboutItem.setToolTipText("https://github.com/Yong-An-Dang/nuclei-plus");
        aboutItem.setIcon(new FlatSVGIcon("icons/cwmInvite.svg"));
        aboutItem.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop.getDesktop().browse(new URI("https://github.com/Yong-An-Dang/nuclei-plus"));
            }
        });

        aboutMenu.add(helpItem);
        aboutMenu.addSeparator();
        aboutMenu.add(supportItem);
        aboutMenu.add(aboutItem);

    }

    private void initToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

//        JButton executeBtn = new JButton(new FlatSVGIcon("icons/execute.svg"));
        JButton executeBtn = new JButton(new FlatSVGIcon("icons/runAll.svg"));
        executeBtn.setToolTipText("默认新建终端执行活动配置（右键可选择已有终端执行）");
        executeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("GlobalConfigModel: \n{}", globalConfigModel.toString());

                if (e.getButton() == 3) {
                    JPopupMenu popupMenu = new JPopupMenu();

                    LinkedHashMap<String, ConsolePanel> consolePanels = runningPanel.getConsolePanels();
                    for (String title : consolePanels.keySet()) {
                        JMenuItem tempItem = new JMenuItem(title);
                        tempItem.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                log.debug("Execute in " + title);
                                if (!NucleiApp.nuclei.targetPanel.getTargetText().strip().equals("")) {
                                    ExecUtils.runGlobalNucleiConfig(consolePanels.get(title));

                                    // 跳转至运行终端
                                    NucleiFrame.frameTabbedPane.setSelectedIndex(3);
                                    RunningPanel.tabbedPane.setSelectedComponent(consolePanels.get(title));
                                } else {
                                    JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
                                    CommonUtil.goToTarget();
                                }
                            }
                        });
                        popupMenu.add(tempItem);
                    }
                    popupMenu.show(executeBtn, e.getX(), e.getY());
                } else {
                    if (!NucleiApp.nuclei.targetPanel.getTargetText().strip().equals("")) {
                        // 创建终端执行任务
                        ConsolePanel consolePanel = runningPanel.createConsole();

                        ExecUtils.runGlobalNucleiConfig(consolePanel);

                        // 跳转至运行终端
                        NucleiFrame.frameTabbedPane.setSelectedIndex(3);
                        RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
                    } else {
                        JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
                        CommonUtil.goToTarget();
                    }
                }
            }
        });

        // cwmInvite.svg
        JButton ntBtn = new JButton(new FlatSVGIcon("icons/execute.svg"));
        ntBtn.setToolTipText("仅运行在最新的 nuclei-templates 版本中添加的新模板");
        ntBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // nuclei -nt, -new-templates          run only new templates added in latest nuclei-templates release
                if (!NucleiApp.nuclei.targetPanel.getTargetText().strip().equals("")) {
                    // 创建终端执行任务
                    ConsolePanel consolePanel = runningPanel.createConsole();

                    ExecUtils.runNewTemplates(consolePanel);

                    // 跳转至运行终端
                    NucleiFrame.frameTabbedPane.setSelectedIndex(3);
                    RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
                } else {
                    JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
                    CommonUtil.goToTarget();
                }
            }
        });

        JButton asBtn = new JButton(new FlatSVGIcon("icons/runWithCoverage.svg"));
        asBtn.setToolTipText("使用 wappalyzer 技术检测到标签映射的自动 Web 扫描");
        asBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                // nuclei -as, -automatic-scan         automatic web scan using wappalyzer technology detection to tags mapping
                if (!NucleiApp.nuclei.targetPanel.getTargetText().strip().equals("")) {
                    // 创建终端执行任务
                    ConsolePanel consolePanel = runningPanel.createConsole();

                    ExecUtils.runAutomaticScan(consolePanel);

                    // 跳转至运行终端
                    NucleiFrame.frameTabbedPane.setSelectedIndex(3);
                    RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
                } else {
                    JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
                    CommonUtil.goToTarget();
                }
            }
        });

        JButton runGroupBtn = new JButton(new FlatSVGIcon("icons/profile.svg"));
        runGroupBtn.setToolTipText("使用选中分组模板扫描目标");
        runGroupBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 使用选中分组模板扫描目标
                JPopupMenu popupMenu = createPopupMenu();
                popupMenu.show(runGroupBtn, e.getX(), e.getY());
            }
        });

        JButton saveBtn = new JButton(new FlatSVGIcon("icons/menu-saveall.svg"));
        saveBtn.setToolTipText("保存项目中所有配置");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    settingsPanel.save();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        activeBtn = new JButton(new FlatSVGIcon("icons/active.svg"));
        activeBtn.setSelected(true);
        activeBtn.setText("当前活动配置：Default");
        activeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu popupMenu = CommonUtil.getConfigPopupMenu();
                popupMenu.show(activeBtn, e.getX(), e.getY());
            }
        });

        toolBar.add(executeBtn);
        toolBar.addSeparator();
        toolBar.add(ntBtn);
        toolBar.add(asBtn);
        toolBar.add(runGroupBtn);
        toolBar.addSeparator();
        toolBar.add(saveBtn);
        toolBar.add(activeBtn);

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
        frameTabbedPane.addTab("Targets", new FlatSVGIcon("icons/pinTab.svg"), targetPanel);
        frameTabbedPane.addTab("Settings", new FlatSVGIcon("icons/pinTab.svg"), settingsPanel);
        frameTabbedPane.addTab("Running", new FlatSVGIcon("icons/pinTab.svg"), runningPanel);
        frameTabbedPane.addTab("Searching", new FlatSVGIcon("icons/pinTab.svg"), searchTabbedPanel);

        this.add(frameTabbedPane, BorderLayout.CENTER);
    }

    private void initClosableTabs(JTabbedPane frameTabbedPane) {
        frameTabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        frameTabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        frameTabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
                    if (tabIndex >= 5) {
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
                // 添加 Template
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
                Desktop.getDesktop().open(new File(ProjectUtil.reportDir()));
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

        // 选项卡面板后置工具栏
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

    private JPopupMenu createPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();
        LinkedHashMap<String, Object> groupMap = CommonUtil.loadGroupByMap();
        if (groupMap != null) {
            for (String key : groupMap.keySet()) {
                JMenuItem tmpItem = new JMenuItem(key);
                tmpItem.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        log.debug("应用选中分组");
                        ArrayList<String> list = (ArrayList<String>) groupMap.get(key);
                        ArrayList<String> templates = new ArrayList<>();
                        ArrayList<String> workflows = new ArrayList<>();
                        for (String pocPath : list) {
                            if (pocPath.contains("workflow")) {
                                workflows.add(pocPath);
                            } else {
                                templates.add(pocPath);
                            }
                        }
                        //
                        if (!NucleiApp.nuclei.targetPanel.getTargetText().isBlank()) {
                            // 创建终端执行任务
                            ConsolePanel consolePanel = runningPanel.createConsole();

                            ExecUtils.runGroupBy(consolePanel, templates, workflows);

                            // 跳转至运行终端
                            NucleiFrame.frameTabbedPane.setSelectedIndex(3);
                            RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
                        } else {
                            JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
                            CommonUtil.goToTarget();
                        }

                    }
                });
                popupMenu.add(tmpItem);
            }
        }
        return popupMenu;
    }
}
