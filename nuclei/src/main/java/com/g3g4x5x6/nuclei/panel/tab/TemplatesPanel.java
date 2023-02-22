package com.g3g4x5x6.nuclei.panel.tab;

import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.panel.console.ConsolePanel;
import com.g3g4x5x6.nuclei.panel.setting.ConfigAllPanel;
import com.g3g4x5x6.nuclei.ui.icon.AccentColorIcon;
import com.g3g4x5x6.nuclei.ultils.CommonUtil;
import com.g3g4x5x6.nuclei.ultils.DialogUtil;
import com.g3g4x5x6.nuclei.ultils.NucleiConfig;
import com.g3g4x5x6.nuclei.ultils.ProjectUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@Slf4j
public class TemplatesPanel extends JPanel {
    private static final String defaultNucleiTemplatesPath = NucleiConfig.getProperty("nuclei.templates.path");

    private final LinkedList<LinkedHashMap<String, String>> templates = new LinkedList<>();
    private final LinkedList<String> filterList = new LinkedList<>();

    private final JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private final FlatTriStateCheckBox customBtn = new FlatTriStateCheckBox();

    private final JButton filterBtn = new JButton(new FlatSVGIcon("icons/filter.svg"));
    private final JToggleButton infoBtn = new JToggleButton(new AccentColorIcon("#007AFF"));
    private final JToggleButton lowBtn = new JToggleButton(new AccentColorIcon("#28CD41"));
    private final JToggleButton mediumBtn = new JToggleButton(new AccentColorIcon("#FFCC00"));
    private final JToggleButton highBtn = new JToggleButton(new AccentColorIcon("#FF9500"));
    private final JToggleButton criticalBtn = new JToggleButton(new AccentColorIcon("#FF3B30"));
    private final JToggleButton templateBtn = new JToggleButton(new AccentColorIcon("#00CED1"));
    private final JToggleButton workflowBtn = new JToggleButton(new AccentColorIcon("#FF7F50"));

    private final JButton refreshBtn = new JButton(new FlatSVGIcon("icons/refresh.svg"));
    private final JTextField searchField = new JTextField();

    private final JTable templatesTable;
    private final DefaultTableModel tableModel;
    private JPopupMenu tablePopMenu;
    private TableRowSorter<DefaultTableModel> sorter;
    private Clipboard clipboard;

    private LinkedHashMap<String, Object> groupMap;

    public TemplatesPanel() {
        this.setLayout(new BorderLayout());

        // Severity
        infoBtn.setSelected(true);
        infoBtn.setToolTipText("Info");
        lowBtn.setSelected(true);
        lowBtn.setToolTipText("Low");
        mediumBtn.setSelected(true);
        mediumBtn.setToolTipText("Medium");
        highBtn.setSelected(true);
        highBtn.setToolTipText("High");
        criticalBtn.setSelected(true);
        criticalBtn.setToolTipText("Critical");
        // Type
        templateBtn.setSelected(true);
        templateBtn.setToolTipText("Templates");
        workflowBtn.setSelected(true);
        workflowBtn.setToolTipText("Workflows");

        // Source
        customBtn.setSelected(true);
        customBtn.setToolTipText("自定义模板");
        customBtn.getState();

        // Filter
        filterList.add("info");
        filterList.add("low");
        filterList.add("medium");
        filterList.add("high");
        filterList.add("critical");
        filterList.add("template");
        filterList.add("workflow");
        filterList.add("custom");

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(openBtn);
        toolBar.add(customBtn);

        toolBar.addSeparator();
        toolBar.add(infoBtn);
        toolBar.add(lowBtn);
        toolBar.add(mediumBtn);
        toolBar.add(highBtn);
        toolBar.add(criticalBtn);
        toolBar.add(filterBtn);
        toolBar.add(templateBtn);
        toolBar.add(workflowBtn);

        toolBar.addSeparator();
        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(Box.createGlue());
        toolBar.add(searchField);
        // 初始化工具栏动作
        initToolBarAction();

        templatesTable = new JTable();
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String[] columnNames = {
                "#",
                "templates_id",
                "templates_name",
                "templates_severity",
                "templates_tags",
                "templates_author",
                "templates_description",
                "templates_reference"};
        tableModel.setColumnIdentifiers(columnNames);
        templatesTable.setModel(tableModel);
        refreshDataForTable();
        JScrollPane tableScroll = new JScrollPane(templatesTable);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        templatesTable.getColumn("#").setCellRenderer(centerRenderer);
        templatesTable.getColumn("templates_severity").setCellRenderer(centerRenderer);

        templatesTable.getColumn("#").setPreferredWidth(20);
        templatesTable.getColumn("templates_id").setPreferredWidth(60);
        templatesTable.getColumn("templates_name").setPreferredWidth(100);
        templatesTable.getColumn("templates_severity").setPreferredWidth(40);
        templatesTable.getColumn("templates_author").setPreferredWidth(30);
        templatesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    // 模板面板右键功能
                    tablePopMenu = createTablePopMenu();
                    tablePopMenu.show(templatesTable, e.getX(), e.getY());
                }
            }
        });

        this.add(toolBar, BorderLayout.NORTH);
        this.add(tableScroll, BorderLayout.CENTER);
    }

    private void initToolBarAction() {
        openBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建一个默认的文件选取器
                JFileChooser fileChooser = new JFileChooser();
                // 设置默认显示的文件夹为当前文件夹
                fileChooser.setCurrentDirectory(new File(NucleiConfig.getProperty("nuclei.templates.custom.path")));
                // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // 设置是否允许多选
                fileChooser.setMultiSelectionEnabled(false);
                // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
                int result = fileChooser.showOpenDialog(TemplatesPanel.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    // 如果点击了"确定", 则获取选择的文件路径
                    File file = fileChooser.getSelectedFile();
                    NucleiConfig.setProperty("nuclei.templates.custom.path", file.getAbsolutePath().replace("\\", "/"));
                    NucleiConfig.saveSettingsProperties();
                    refreshDataForTable();
                }
            }
        });

        filterBtn.setToolTipText("点击筛选");
        filterBtn.addActionListener(e -> filter());

        customBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openBtn.setEnabled(customBtn.isSelected());
            }
        });

        refreshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    // nuclei -ut  [-ut, -update-templates         update nuclei-templates to latest released version]
                    ConsolePanel consolePanel = (ConsolePanel) RunningPanel.tabbedPane.getSelectedComponent();
                    consolePanel.write("nuclei -ut\r");

                    // 清除旧列表
                    templates.clear();
                    log.debug("Templates Count: " + templates.size());
                    // 展示列表内信息
                    refreshDataForTable();
                    log.debug("Templates Count: " + templates.size());
                }).start();
            }
        });
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search, Enter");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, new FlatSearchIcon());
        searchField.registerKeyboardAction(e -> {
                    String searchKeyWord = searchField.getText().strip();
                    sorter.setRowFilter(RowFilter.regexFilter(searchKeyWord));
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
    }

    private JPopupMenu createTablePopMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        popupMenu.add(new JMenuItem("已选中PoC模板数：" + templatesTable.getSelectedRowCount()));
        popupMenu.addSeparator();
        popupMenu.add(new AbstractAction("<html>追加选中模板到<font style='color:green'>活动配置</font></html>") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("追加选中模板到活动配置");
                for (int index : templatesTable.getSelectedRows()) {
                    int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                    String savePath = templates.get(num).get("path");
                    if (savePath.contains("workflow")) {
                        NucleiApp.nuclei.settingsPanel.getActiveConfigAllPanel().addWorkflows(savePath);
                    } else {
                        NucleiApp.nuclei.settingsPanel.getActiveConfigAllPanel().addTemplates(savePath);
                    }
                }
            }
        });

        JMenu toGroupByMenu = new JMenu("<html>追加选中模板到<font style='color:blue'>配置</font></html>");
        LinkedHashMap<String, ConfigAllPanel> configAllPanelLinkedHashMap = CommonUtil.getConfigPanels();
        for (String title : configAllPanelLinkedHashMap.keySet()){
            JMenuItem tmpItem = new JMenuItem(title);
            tmpItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ConfigAllPanel configAllPanel = configAllPanelLinkedHashMap.get(title);
                    for (int index : templatesTable.getSelectedRows()) {
                        int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                        String savePath = templates.get(num).get("path");
                        if (savePath.contains("workflow")) {
                            configAllPanel.addWorkflows(savePath);
                        } else {
                            configAllPanel.addTemplates(savePath);
                        }
                    }
                }
            });
            toGroupByMenu.add(tmpItem);
        }

        JMenu groupByMenu = new JMenu("自定义分组管理");

        JMenuItem manageGroupItem = new JMenuItem("管理分组");
        manageGroupItem.setIcon(new FlatSVGIcon("icons/GroupByPackage.svg"));

        JMenuItem createItem = new JMenuItem("新建分组");
        createItem.setIcon(new FlatSVGIcon("icons/addFolder.svg"));
        createItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("创建新的分组");
                String groupName = DialogUtil.input(createItem, "请输入分组名称：");
                if (groupName != null && !groupName.strip().equals("")) {
                    if (groupMap == null || groupMap.get(groupName.strip()) == null) {
                        if (groupMap == null)
                            groupMap = new LinkedHashMap<>();
                        groupMap.put(groupName, new LinkedList<String>());
                        saveGroupToYaml();
                    } else {
                        DialogUtil.warn("已存在该分组！");
                    }
                }
            }
        });
        groupByMenu.add(manageGroupItem);
        groupByMenu.addSeparator();

        groupMap = CommonUtil.loadGroupByMap();
        if (groupMap != null) {
            for (String key : groupMap.keySet()) {
                JMenuItem tmpItem = new JMenuItem(key);
                tmpItem.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        log.debug("添加选择模板到选中分组中");
                        ArrayList<String> list = new ArrayList<>();
                        if (groupMap.get(key) != null)
                            list = (ArrayList<String>) groupMap.get(key);
                        for (int index : templatesTable.getSelectedRows()) {
                            int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                            String savePath = templates.get(num).get("path");
                            list.add(savePath);
                        }
                        groupMap.put(key, list);
                        saveGroupToYaml();
                    }
                });
                groupByMenu.add(tmpItem);
            }
            groupByMenu.addSeparator();
        }
        if (groupByMenu.getItemCount() == 2) {
            groupByMenu.add(new JMenuItem("<空>"));
            groupByMenu.addSeparator();
        }
        groupByMenu.add(createItem);

        JMenu selectedTemplatesMenu = new JMenu("运行选中的模板");
        selectedTemplatesMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                log.debug("menuSelected");
                selectedTemplatesMenu.removeAll();
                LinkedHashMap<String, ConsolePanel> consolePanels = getConsolePanels();
                for (String title : consolePanels.keySet()) {
                    JMenuItem tempItem = new JMenuItem(title);
                    tempItem.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            log.debug("Execute in " + title);
                            runTemplatesInSelectedConsole(consolePanels.get(title));
                        }
                    });
                    selectedTemplatesMenu.add(tempItem);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                log.debug("menuDeselected");
            }

            @Override
            public void menuCanceled(MenuEvent e) {
                log.debug("menuCanceled");
            }
        });
        JMenu selectedTagsMenu = new JMenu("运行包含选中标签的模板");
        selectedTagsMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                selectedTagsMenu.removeAll();
                LinkedHashMap<String, ConsolePanel> consolePanels = getConsolePanels();
                for (String title : consolePanels.keySet()) {
                    JMenuItem tempItem = new JMenuItem(title);
                    tempItem.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            log.debug("Execute in " + title);
                            runTagsInSelectedConsole(consolePanels.get(title));
                        }
                    });
                    selectedTagsMenu.add(tempItem);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });

        popupMenu.add(toGroupByMenu);
        popupMenu.addSeparator();
        popupMenu.add(groupByMenu);
        popupMenu.addSeparator();
        popupMenu.add(editAction);
        popupMenu.add(openDirAction);
        popupMenu.add(copyPathAction);
        popupMenu.add(deleteTemplateAction);
        popupMenu.addSeparator();
        popupMenu.add(generateWithTemplatesAction);
        popupMenu.add(generateWithTagsAction);
        popupMenu.add(selectedTemplatesMenu);
        popupMenu.add(selectedTagsMenu);

        return popupMenu;
    }

    /**
     * 耗时操作
     */
    private void refreshDataForTable() {
        // 搜索功能
        sorter = new TableRowSorter<>(tableModel);
        templatesTable.setRowSorter(sorter);
        new Thread(() -> {
            tableModel.setRowCount(0);
            templates.clear();
            try {
                // 初始化列表并输出列表大小
                int templateCount = getAllTemplatesFromPath();
                log.debug("Templates Count: " + templateCount);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String customPath = NucleiConfig.getProperty("nuclei.templates.custom.path").replace("\\", "/");
            int count = 0;
            for (LinkedHashMap<String, String> templateInfo : templates) {
                count++;
                // Filter custom
                String path = templateInfo.get("path");
                if (customBtn.getState() == FlatTriStateCheckBox.State.INDETERMINATE) {
                    if (!path.replace("\\", "/").startsWith(customPath))
                        continue;
                }
                if (!filterList.contains("custom")) {
                    if (path.replace("\\", "/").startsWith(customPath))
                        continue;
                }
                String tType = templateInfo.get("path").endsWith("workflow.yaml") ? "workflow" : "template";
                // Filter template, workflow, custom
                if (!filterList.contains(tType))
                    continue;
                String id = templateInfo.get("id");
                String name = templateInfo.get("name");
                String severity = templateInfo.get("severity");
                // Filter Severity && workflow without severity
                if (!filterList.contains(severity) && !tType.equalsIgnoreCase("workflow"))
                    continue;

                String author = templateInfo.get("author");
                String description = templateInfo.get("description");
                String reference = templateInfo.get("reference");
                String tags = templateInfo.get("tags");
                tableModel.addRow(new String[]{String.valueOf(count), id, name, severity, tags, author, description, reference});
            }
        }).start();
    }

    private void filter() {
        // 清除过滤器
        filterList.clear();

        // 配置过滤器
        if (infoBtn.isSelected())
            filterList.add("info");
        if (lowBtn.isSelected())
            filterList.add("low");
        if (mediumBtn.isSelected())
            filterList.add("medium");
        if (highBtn.isSelected())
            filterList.add("high");
        if (criticalBtn.isSelected())
            filterList.add("critical");
        if (templateBtn.isSelected())
            filterList.add("template");
        if (workflowBtn.isSelected())
            filterList.add("workflow");
        if (customBtn.isSelected())
            filterList.add("custom");

        log.debug(filterList.toString());

        // 刷新数据
        refreshDataForTable();
    }

    private LinkedHashMap<String, String> getTemplate(String path) {
        LinkedHashMap<String, String> templateInfo = new LinkedHashMap<>();
        Map map = getMapFromYaml(path);
        if (map != null) {
            JSONObject jsonObject = new JSONObject(map);
            JSONObject info = jsonObject.getJSONObject("info");

            templateInfo.put("path", path);
            templateInfo.put("id", jsonObject.getString("id") == null ? "空" : jsonObject.getString("id"));
            templateInfo.put("name", info.getString("name"));
            templateInfo.put("severity", info.getString("severity"));
            templateInfo.put("author", info.getString("author"));
            templateInfo.put("description", info.getString("description"));
            templateInfo.put("reference", info.getString("reference"));
            templateInfo.put("tags", info.getString("tags"));
        } else {
            templateInfo.put("path", path);
            templateInfo.put("id", "空");
            templateInfo.put("name", "空");
            templateInfo.put("severity", "空");
            templateInfo.put("author", "空");
            templateInfo.put("description", "空");
            templateInfo.put("reference", "空");
            templateInfo.put("tags", "空");
        }
        return templateInfo;
    }

    private Map getMapFromYaml(String path) {
        Map template;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 调基础工具类的方法
        Yaml yaml = new Yaml();
        template = yaml.loadAs(inputStream, Map.class);
        return template;
    }

    /**
     * 遍历出目录下的所有 yaml 文件
     *
     * @return 匹配到的文件总数
     * @throws IOException 抛出异常
     */
    private int getAllTemplatesFromPath() throws IOException {
        // office template
        if (Files.exists(Path.of(TemplatesPanel.defaultNucleiTemplatesPath))) {
            walkFiles(TemplatesPanel.defaultNucleiTemplatesPath);
        }

        // custom template
        if (Files.exists(Path.of(NucleiConfig.getProperty("nuclei.templates.custom.path")))) {
            walkFiles(NucleiConfig.getProperty("nuclei.templates.custom.path"));
        }

        return templates.size();
    }

    private void walkFiles(String path) throws IOException {
        Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<>() {
            // 访问文件时触发
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().endsWith(".yaml")) {
                    templates.add(getTemplate(file.toString()));
                }
                return FileVisitResult.CONTINUE;
            }

            // 访问目录时触发
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private LinkedHashMap<String, ConsolePanel> getConsolePanels() {
        LinkedHashMap<String, ConsolePanel> consolePanels = new LinkedHashMap<>();
        int count = RunningPanel.tabbedPane.getTabCount();
        for (int i = 0; i < count; i++) {
            consolePanels.put(RunningPanel.tabbedPane.getTitleAt(i), (ConsolePanel) RunningPanel.tabbedPane.getComponentAt(i));
        }
        return consolePanels;
    }

    @SneakyThrows
    private void runTemplatesInSelectedConsole(ConsolePanel consolePanel) {
        log.debug("Run command with Selected");

        if (!NucleiApp.nuclei.targetPanel.getTargetText().strip().equals("")) {
            // 配置对象
            String configPath = CommonUtil.getNucleiConfigFile(getSelectedTemplateMap());

            // 执行命令
            String command = "nuclei -config " + configPath + " -markdown-export " + ProjectUtil.reportDir();
            consolePanel.write(command + "\r");
            NucleiFrame.frameTabbedPane.setSelectedIndex(3);
            RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
        } else {
            JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
            CommonUtil.goToTarget();
        }
    }

    @SneakyThrows
    private void runTagsInSelectedConsole(ConsolePanel consolePanel) {
        if (!NucleiApp.nuclei.targetPanel.getTargetText().strip().equals("")) {
            // 配置对象
            String configPath = CommonUtil.getNucleiConfigFile(getSelectedTagMap());
            // 执行命令
            String command = "nuclei -config " + configPath + " -markdown-export " + ProjectUtil.reportDir();
            consolePanel.write(command + "\r");
            NucleiFrame.frameTabbedPane.setSelectedIndex(3);
            RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
        } else {
            JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
            CommonUtil.goToTarget();
        }
    }

    private AbstractAction editAction = new AbstractAction("编辑-概念验证模板") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Edit This Template");
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                String savePath = templates.get(num).get("path");
                log.debug(savePath);
                EditTemplatePanel editPanel = new EditTemplatePanel(savePath);
                NucleiFrame.frameTabbedPane.addTab(editPanel.getTitle(), editPanel.getIcon(), editPanel);
                NucleiFrame.frameTabbedPane.setSelectedIndex(NucleiFrame.frameTabbedPane.getTabCount() - 1);
                log.debug(templates.get(num).toString());
            }
        }
    };

    private AbstractAction openDirAction = new AbstractAction("打开-模板所在文件夹") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Open in Folder");
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                String savePath = templates.get(num).get("path");
                log.debug(savePath);
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(new File(savePath).getParentFile());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }).start();
            }
        }
    };

    private AbstractAction copyPathAction = new AbstractAction("复制-模板绝对路径") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Copy Path");
            String savePath = "\n";
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                savePath += templates.get(num).get("path") + "\n";
                log.debug(savePath);
            }
            savePath = savePath.strip();
            if (clipboard == null)
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //获得系统剪贴板
            Transferable transferable = new StringSelection(savePath);
            clipboard.setContents(transferable, null);
        }
    };

    private AbstractAction deleteTemplateAction = new AbstractAction("删除-选中的模板") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO 警告提示，再次确认
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                String savePath = templates.get(num).get("path");
                Files.delete(Path.of(savePath));
                templates.remove(num);
            }
            refreshDataForTable();
        }
    };

    /**
     * 目标是可以做到多选
     * <html><font style='color:red'></font></html>
     */
    private AbstractAction generateWithTemplatesAction = new AbstractAction("<html>为选中的<font style='color:red'>模板</font>生成执行命令</html>") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Generate command with Templates");

            if (!NucleiApp.nuclei.targetPanel.getTargetText().strip().equals("")) {
                // 配置对象
                String configPath = CommonUtil.getNucleiConfigFile(getSelectedTemplateMap());
                String command = "nuclei -config " + configPath + " -markdown-export " + ProjectUtil.reportDir();
                if (clipboard == null)
                    clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //获得系统剪贴板
                Transferable transferable = new StringSelection(command);
                clipboard.setContents(transferable, null);

                ConsolePanel consolePanel = (ConsolePanel) RunningPanel.tabbedPane.getSelectedComponent();
                consolePanel.write(command);
                NucleiFrame.frameTabbedPane.setSelectedIndex(3);
            } else {
                JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
                CommonUtil.goToTarget();
            }
        }
    };

    private AbstractAction generateWithTagsAction = new AbstractAction("<html>为选中的<font style='color:blue'>标签</font>生成执行命令</html>") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Generate command with Tags");
            if (!NucleiApp.nuclei.targetPanel.getTargetText().strip().equals("")) {
                // 配置对象
                String configPath = CommonUtil.getNucleiConfigFile(getSelectedTagMap());
                // 复制命令到粘贴板
                String command = "nuclei -config " + configPath + " -markdown-export " + ProjectUtil.reportDir();
                if (clipboard == null)
                    clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //获得系统剪贴板
                Transferable transferable = new StringSelection(command);
                clipboard.setContents(transferable, null);
                // 跳转到命令行面板
                ConsolePanel consolePanel = (ConsolePanel) RunningPanel.tabbedPane.getSelectedComponent();
                consolePanel.write(command);
                NucleiFrame.frameTabbedPane.setSelectedIndex(3);
            } else {
                JOptionPane.showMessageDialog(NucleiApp.nuclei, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
                CommonUtil.goToTarget();
            }
        }
    };

    private Map<String, Object> getSelectedTemplateMap() {
        ArrayList<String> template = new ArrayList<>();
        ArrayList<String> workflow = new ArrayList<>();
        for (int index : templatesTable.getSelectedRows()) {
            int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
            String savePath = templates.get(num).get("path");
            if (savePath.contains("workflow")) {
                workflow.add(savePath);
            } else {
                template.add(savePath);
            }
        }

        Map<String, Object> selectedMap = new HashMap<>();
        selectedMap.put("templates", template);
        selectedMap.put("workflows", workflow);

        Map<String, Object> configMap = CommonUtil.getNucleiConfigObject();
        configMap.remove("templates");
        configMap.remove("workflows");

        Map<String, Object> config = new HashMap<>();
        config.putAll(selectedMap);
        config.putAll(configMap);

        config.put("target", CommonUtil.getTargets());

        return config;
    }

    private Map<String, Object> getSelectedTagMap() {
        ArrayList<String> tempTags = new ArrayList<>();
        for (int index : templatesTable.getSelectedRows()) {
            int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
            String tags = templates.get(num).get("tags");
            tempTags.addAll(Arrays.asList(tags.split(",")));
        }

        Map<String, Object> tagMap = new HashMap<>();
        tagMap.put("tags", tempTags);

        Map<String, Object> configMap = CommonUtil.getNucleiConfigObject();
        configMap.remove("templates");
        configMap.remove("workflows");
        configMap.remove("tags");

        Map<String, Object> config = new HashMap<>();
        config.putAll(tagMap);
        config.putAll(configMap);

        config.put("target", CommonUtil.getTargets());
        return config;
    }

    @SneakyThrows
    private void saveGroupToYaml() {
        if (!Files.exists(Path.of(NucleiConfig.getConfigPath() + "/groupby.yaml")))
            Files.createFile(Path.of(NucleiConfig.getConfigPath() + "/groupby.yaml"));
        if (groupMap != null) {
            Yaml yaml = new Yaml();
            yaml.dump(groupMap, new FileWriter(String.valueOf(Path.of(NucleiConfig.getConfigPath() + "/groupby.yaml"))));
        }
    }
}
