package com.g3g4x5x6.nuclei.panel;

import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.model.GlobalConfigModel;
import com.g3g4x5x6.nuclei.model.SelectedTagsConfig;
import com.g3g4x5x6.nuclei.model.SelectedTemplatesConfig;
import com.g3g4x5x6.nuclei.panel.connector.ConsolePanel;
import com.g3g4x5x6.nuclei.panel.settings.SettingTarget;
import com.g3g4x5x6.nuclei.panel.settings.template.GlobalTemplatePanel;
import com.g3g4x5x6.nuclei.panel.settings.template.GlobalWorkflowPanel;
import com.g3g4x5x6.nuclei.ui.AccentColorIcon;
import com.g3g4x5x6.nuclei.ultils.NucleiConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.TypeDescription;
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
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;

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

    private final JScrollPane tableScroll;
    private final JTable templatesTable;
    private DefaultTableModel tableModel;
    private JPopupMenu tablePopMenu;
    private TableRowSorter<DefaultTableModel> sorter;
    private Clipboard clipboard;

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
        customBtn.setToolTipText("???????????????");
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

        initToolBarAction();


        tablePopMenu = new JPopupMenu();
        tablePopMenu.add(new AbstractAction("?????????????????????????????????") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("?????????????????????????????????");
                for (int index : templatesTable.getSelectedRows()) {
                    int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                    String savePath = templates.get(num).get("path");
                    if (savePath.contains("workflow")) {
                        GlobalWorkflowPanel.addWorkflows(savePath);
                    } else {
                        GlobalTemplatePanel.addTemplates(savePath);
                    }
                }
            }
        });
        tablePopMenu.addSeparator();
        tablePopMenu.add(editAction);
        tablePopMenu.add(openDirAction);
        tablePopMenu.add(copyPathAction);
        tablePopMenu.add(deleteTemplateAction);
        tablePopMenu.addSeparator();
        tablePopMenu.add(generateWithSelectedAction);
        tablePopMenu.add(generateWithTagsAction);
        JMenu selectedTemplatesMenu = new JMenu("?????????????????????");
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
        tablePopMenu.add(selectedTemplatesMenu);
        JMenu selectedTagsMenu = new JMenu("?????????????????????????????????");
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
        tablePopMenu.add(selectedTagsMenu);

        templatesTable = new JTable();
        tableModel = new DefaultTableModel() {
            // ????????????
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
        tableScroll = new JScrollPane(templatesTable);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
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
        templatesTable.setComponentPopupMenu(tablePopMenu);
        templatesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });

        this.add(toolBar, BorderLayout.NORTH);
        this.add(tableScroll, BorderLayout.CENTER);
    }

    private void initToolBarAction() {
        openBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ????????????????????????????????????
                JFileChooser fileChooser = new JFileChooser();
                // ????????????????????????????????????????????????
                fileChooser.setCurrentDirectory(new File(NucleiConfig.getWorkPath()));
                // ??????????????????????????????????????????????????????????????????????????????????????????
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // ????????????????????????
                fileChooser.setMultiSelectionEnabled(false);
                // ??????????????????????????????????????????, ???????????????????????????
                int result = fileChooser.showOpenDialog(TemplatesPanel.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    // ???????????????"??????", ??????????????????????????????
                    File file = fileChooser.getSelectedFile();
                    NucleiConfig.setProperty("nuclei.templates.custom.path", file.getAbsolutePath().replace("\\", "/"));
                    NucleiConfig.saveSettingsProperties();
                    refreshDataForTable();
                }
            }
        });

        filterBtn.setToolTipText("????????????");
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

                    // ???????????????
                    templates.clear();
                    log.debug("Templates Count: " + templates.size());
                    // ?????????????????????
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

    /**
     * ????????????
     */
    private void refreshDataForTable() {
        // ????????????
        sorter = new TableRowSorter<>(tableModel);
        templatesTable.setRowSorter(sorter);
        new Thread(() -> {
            tableModel.setRowCount(0);
            templates.clear();
            try {
                // ????????????????????????????????????
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
        // ???????????????
        filterList.clear();

        // ???????????????
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

        // ????????????
        refreshDataForTable();
    }

    private LinkedHashMap<String, String> getTemplate(String path) {
        LinkedHashMap<String, String> templateInfo = new LinkedHashMap<>();
        Map map = getMapFromYaml(path);
        if (map != null) {
            JSONObject jsonObject = new JSONObject(map);
            JSONObject info = jsonObject.getJSONObject("info");

            templateInfo.put("path", path);
            templateInfo.put("id", jsonObject.getString("id") == null ? "???" : jsonObject.getString("id"));
            templateInfo.put("name", info.getString("name"));
            templateInfo.put("severity", info.getString("severity"));
            templateInfo.put("author", info.getString("author"));
            templateInfo.put("description", info.getString("description"));
            templateInfo.put("reference", info.getString("reference"));
            templateInfo.put("tags", info.getString("tags"));
        } else {
            templateInfo.put("path", path);
            templateInfo.put("id", "???");
            templateInfo.put("name", "???");
            templateInfo.put("severity", "???");
            templateInfo.put("author", "???");
            templateInfo.put("description", "???");
            templateInfo.put("reference", "???");
            templateInfo.put("tags", "???");
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
        // ???????????????????????????
        Yaml yaml = new Yaml();
        template = yaml.loadAs(inputStream, Map.class);
        return template;
    }

    /**
     * ??????????????????????????? yaml ??????
     *
     * @return ????????????????????????
     * @throws IOException ????????????
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
            // ?????????????????????
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().endsWith(".yaml")) {
                    templates.add(getTemplate(file.toString()));
                }
                return FileVisitResult.CONTINUE;
            }

            // ?????????????????????
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

        GlobalConfigModel globalConfigModel = GlobalConfigModel.createGlobalConfigModel();

        if (!NucleiApp.nuclei.getSettingsPanel().getTargetSetting().getTargetText().strip().equals("")) {
            List<String> template = new LinkedList<>();
            List<String> workflow = new LinkedList<>();
            // ??????????????????
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                String savePath = templates.get(num).get("path");
                if (savePath.contains("workflow")) {
                    workflow.add(savePath);
                } else {
                    template.add(savePath);
                }
            }
            globalConfigModel.setTemplates(template);
            globalConfigModel.setWorkflows(workflow);

            String configPath = NucleiConfig.getProperty("nuclei.temp.path") + "/" + UUID.randomUUID() + ".yaml";
            globalConfigModel.toYaml(globalConfigModel, configPath);

            consolePanel.write("nuclei -config " + configPath + "\r");
            NucleiFrame.frameTabbedPane.setSelectedIndex(2);
            RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
        } else {
            JOptionPane.showMessageDialog(NucleiApp.nuclei, "????????????????????????", "??????", JOptionPane.WARNING_MESSAGE);
        }
    }

    @SneakyThrows
    private void runTagsInSelectedConsole(ConsolePanel consolePanel) {
        if (!NucleiApp.nuclei.getSettingsPanel().getTargetSetting().getTargetText().strip().equals("")) {
            // ????????????
            SelectedTagsConfig selected = new SelectedTagsConfig();
            selected.setTarget(Arrays.asList(NucleiApp.nuclei.getSettingsPanel().getTargetSetting().getTargetText().split("\\s+")));

            ArrayList<String> tempTags = new ArrayList<>();
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                String tags = templates.get(num).get("tags");
                tempTags.addAll(Arrays.asList(tags.split(",")));
            }
            selected.setTags(tempTags);
            // ??????????????????
            String configPath = NucleiConfig.getWorkPath() + "/temp/nuclei/tags_" + UUID.randomUUID() + ".yaml";
            Yaml yaml = new Yaml();
            yaml.dump(selected, new FileWriter(configPath));
            // ????????????
            String command = "nuclei -config " + configPath + " -markdown-export " + NucleiFrame.reportDir;
            consolePanel.write(command + "\r");
            NucleiFrame.frameTabbedPane.setSelectedIndex(2);
            RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
        } else {
            JOptionPane.showMessageDialog(NucleiApp.nuclei, "????????????????????????", "??????", JOptionPane.WARNING_MESSAGE);
        }
    }

    private AbstractAction editAction = new AbstractAction("??????-??????????????????") {
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

    private AbstractAction openDirAction = new AbstractAction("??????-?????????????????????") {
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

    private AbstractAction copyPathAction = new AbstractAction("??????-??????????????????") {
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
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //?????????????????????
            Transferable transferable = new StringSelection(savePath);
            clipboard.setContents(transferable, null);
        }
    };

    private AbstractAction deleteTemplateAction = new AbstractAction("??????-???????????????") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO ???????????????????????????
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
     * ???????????????????????????
     * <html><font style='color:red'></font></html>
     */
    private AbstractAction generateWithSelectedAction = new AbstractAction("<html>????????????<font style='color:red'>??????</font>??????????????????</html>") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Generate command with Selected");

            if (!NucleiApp.nuclei.getSettingsPanel().getTargetSetting().getTargetText().strip().equals("")) {
                SelectedTemplatesConfig selected = new SelectedTemplatesConfig();

                ArrayList<String> tempTemplate = new ArrayList<>();
                for (int index : templatesTable.getSelectedRows()) {
                    int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                    String savePath = templates.get(num).get("path");
                    tempTemplate.add(savePath);
                }
                selected.setTemplates(tempTemplate);
                selected.setTarget(Arrays.asList(NucleiApp.nuclei.getSettingsPanel().getTargetSetting().getTargetText().split("\\s+")));

                String configPath = NucleiConfig.getWorkPath() + "/temp/nuclei/templates_" + UUID.randomUUID() + ".yaml";
                Yaml yaml = new Yaml();
                yaml.dump(selected, new FileWriter(configPath));
                String command = "nuclei -config " + configPath + " -markdown-export " + NucleiFrame.reportDir;
                if (clipboard == null)
                    clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //?????????????????????
                Transferable transferable = new StringSelection(command);
                clipboard.setContents(transferable, null);

                ConsolePanel consolePanel = (ConsolePanel) RunningPanel.tabbedPane.getSelectedComponent();
                consolePanel.write(command);
                NucleiFrame.frameTabbedPane.setSelectedIndex(2);
            } else {
                JOptionPane.showMessageDialog(NucleiApp.nuclei, "????????????????????????", "??????", JOptionPane.WARNING_MESSAGE);
            }
        }
    };

    private AbstractAction generateWithTagsAction = new AbstractAction("<html>????????????<font style='color:blue'>??????</font>??????????????????</html>") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Generate command with Tags");
            if (!NucleiApp.nuclei.getSettingsPanel().getTargetSetting().getTargetText().strip().equals("")) {
                // ????????????
                SelectedTagsConfig selected = new SelectedTagsConfig();
                selected.setTarget(Arrays.asList(NucleiApp.nuclei.getSettingsPanel().getTargetSetting().getTargetText().split("\\s+")));

                ArrayList<String> tempTags = new ArrayList<>();
                for (int index : templatesTable.getSelectedRows()) {
                    int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                    String tags = templates.get(num).get("tags");
                    tempTags.addAll(Arrays.asList(tags.split(",")));
                }
                selected.setTags(tempTags);
                // ??????????????????
                String configPath = NucleiConfig.getWorkPath() + "/temp/nuclei/tags_" + UUID.randomUUID() + ".yaml";
                Yaml yaml = new Yaml();
                yaml.dump(selected, new FileWriter(configPath));
                // ????????????????????????
                String command = "nuclei -config " + configPath + " -markdown-export " + NucleiFrame.reportDir;
                if (clipboard == null)
                    clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //?????????????????????
                Transferable transferable = new StringSelection(command);
                clipboard.setContents(transferable, null);
                // ????????????????????????
                ConsolePanel consolePanel = (ConsolePanel) RunningPanel.tabbedPane.getSelectedComponent();
                consolePanel.write(command);
                NucleiFrame.frameTabbedPane.setSelectedIndex(2);
            } else {
                JOptionPane.showMessageDialog(NucleiApp.nuclei, "????????????????????????", "??????", JOptionPane.WARNING_MESSAGE);
            }
        }
    };
}
