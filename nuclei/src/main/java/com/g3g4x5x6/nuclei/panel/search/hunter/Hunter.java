package com.g3g4x5x6.nuclei.panel.search.hunter;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.panel.console.ConsolePanel;
import com.g3g4x5x6.nuclei.panel.tab.RunningPanel;
import com.g3g4x5x6.nuclei.ultils.Base64Utils;
import com.g3g4x5x6.nuclei.ultils.CommonUtil;
import com.g3g4x5x6.nuclei.ultils.ProjectUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class Hunter extends JPanel {
    private String title;
    private String tips;
    private FlatSVGIcon icon;

    private JToolBar toolBar;
    private final JTextField inputField = new JTextField();
    private final JTextField searchField = new JTextField();
    // intentionBulb.svg intentionBulbGrey.svg
    private final JButton statusBtn = new JButton(new FlatSVGIcon("icons/intentionBulbGrey.svg"));

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPopupMenu popupMenu;

    private final String[] columnNames = {
            "#",
            "IP",
            "URL",
            "Port",
            "Title",
            "Domain",
            "ICP",
            "City"
    };
    private final HunterBot hunterBot = new HunterBot();

    public Hunter() {
        this.setLayout(new BorderLayout());
        this.title = "Hunter";
        this.tips = "奇安信 Hunter 网络空间搜索引擎";
        this.icon = new FlatSVGIcon("icons/inlayGlobe.svg");

        initToolBar();
        initTable();
    }

    private void initToolBar() {
        toolBar = new JToolBar();

        inputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Hunter Search... & Enter");
        inputField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, new FlatSearchIcon());
        inputField.registerKeyboardAction(e -> {
            // 搜索动作
            String qbase64 = Base64.encode(inputField.getText());
            statusBtn.setIcon(new FlatSVGIcon("icons/intentionBulb.svg"));
            new Thread(() -> {
                try {
                    resetTableRows(hunterBot.get(hunterBot.packageUrl(qbase64)));
                    statusBtn.setIcon(new FlatSVGIcon("icons/intentionBulbGrey.svg"));
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                    statusBtn.setIcon(new FlatSVGIcon("icons/intentionBulbGrey.svg"));
                }
            }).start();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);

        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Filter... & Enter");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, new FlatSearchIcon());
        searchField.registerKeyboardAction(e -> {
                    String searchKeyWord = searchField.getText().strip();
                    sorter.setRowFilter(RowFilter.regexFilter(searchKeyWord));
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);

        statusBtn.setToolTipText("搜素状态提示灯");


        toolBar.add(inputField);
        toolBar.addSeparator();
        toolBar.add(statusBtn);
        toolBar.addSeparator();
        toolBar.add(searchField);
        this.add(toolBar, BorderLayout.NORTH);
    }

    private void initTable() {
        table = new JTable();
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);
        table.setModel(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        table.getColumn("#").setCellRenderer(centerRenderer);
        table.getColumn("Port").setCellRenderer(centerRenderer);

        table.getColumn("#").setPreferredWidth(10);
        table.getColumn("Port").setPreferredWidth(10);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    log.debug("右键菜单");
                    // 模板面板右键功能
                    popupMenu = createPopupMenu();
                    popupMenu.show(table, e.getX(), e.getY());
                }
            }
        });
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void resetTableRows(JSONArray jsonArray) {
        // 搜索功能
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        tableModel.setRowCount(0);
        int num = 1;
        if (jsonArray != null) {
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                tableModel.addRow(new String[]{
                        String.valueOf(num),
                        jsonObject.getString("ip"),
                        jsonObject.getString("url"),
                        jsonObject.getString("port"),
                        jsonObject.getString("web_title"),
                        jsonObject.getString("domain"),
                        jsonObject.getString("number"),
                        jsonObject.getString("city")
                });
                num++;
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public FlatSVGIcon getIcon() {
        return icon;
    }

    public void setIcon(FlatSVGIcon icon) {
        this.icon = icon;
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem openUrlItem = new JMenuItem("打开URL");
        openUrlItem.setToolTipText("打开选中的第一行的URL");
        openUrlItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = (String) table.getValueAt(table.getSelectedRow(), 2);
                if (!url.toLowerCase().startsWith("http"))
                    url = "http://" + url;
                try {
                    Desktop.getDesktop().browse(URI.create(url));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JMenu applyGroupMenu = new JMenu("扫描自定义分组模板");
        LinkedHashMap<String, Object> groupMap = CommonUtil.loadGroupByMap();
        if (groupMap != null) {
            for (String key : groupMap.keySet()) {
                JMenuItem tmpItem = new JMenuItem(key);
                tmpItem.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        log.debug("扫描选中的自定义分组模板");
                        ArrayList<String> template = new ArrayList<>();
                        ArrayList<String> workflow = new ArrayList<>();

                        ArrayList<String> list = (ArrayList<String>) groupMap.get(key);
                        for (String pocPath : list) {
                            if (pocPath.contains("workflow")) {
                                workflow.add(pocPath);
                            } else {
                                template.add(pocPath);
                            }
                        }

                        runSelectedGroupInNewConsole(getScanConfig(template, workflow));
                    }
                });
                applyGroupMenu.add(tmpItem);
            }
        }

        JMenuItem addTargetItem = new JMenuItem("追加到全局目标");
        addTargetItem.setToolTipText("追加到 Target 面板");
        addTargetItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int index : table.getSelectedRows()) {
                    String url = (String) table.getValueAt(index, 2);
                    if (!url.toLowerCase().startsWith("http"))
                        url = "http://" + url;
                    NucleiApp.nuclei.targetPanel.getTextArea().append(url + "\n");
                }
            }
        });


        popupMenu.add(addTargetItem);
        popupMenu.add(openUrlItem);
        popupMenu.addSeparator();
        popupMenu.add(applyGroupMenu);

        return popupMenu;
    }

    private Map<String, Object> getScanConfig(ArrayList<String> templates, ArrayList<String> workflows) {
        Map<String, Object> selectedGroupMap = new HashMap<>();
        selectedGroupMap.put("templates", templates);
        selectedGroupMap.put("workflows", workflows);

        Map<String, Object> configMap = CommonUtil.getNucleiConfigObject();
        configMap.remove("templates");
        configMap.remove("workflows");

        Map<String, Object> config = new HashMap<>();
        config.putAll(selectedGroupMap);
        config.putAll(configMap);
        config.put("target", getSelectedTarget());

        return config;
    }

    private ArrayList<String> getSelectedTarget() {
        ArrayList<String> targets = new ArrayList<>();

        for (int index : table.getSelectedRows()) {
            String url = (String) table.getValueAt(index, 2);
            if (!url.toLowerCase().startsWith("http"))
                url = "http://" + url;
            targets.add(url);
        }
        return targets;
    }

    private void runSelectedGroupInNewConsole(Map<String, Object> config) {
        log.debug("Run command with Selected");
        ConsolePanel consolePanel = NucleiApp.nuclei.runningPanel.createConsole();

        // 配置对象
        String configPath = CommonUtil.getNucleiConfigFile(config);

        // 执行命令
        String command = "nuclei -config " + configPath + " -markdown-export " + ProjectUtil.reportDir();
        consolePanel.write(command + "\r");
        NucleiFrame.frameTabbedPane.setSelectedIndex(3);
        RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
    }

}
