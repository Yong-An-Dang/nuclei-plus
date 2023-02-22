package com.g3g4x5x6.nuclei.panel.dialog;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.ultils.CommonUtil;
import com.g3g4x5x6.nuclei.ultils.L;
import com.g3g4x5x6.nuclei.ultils.NucleiConfig;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

@Slf4j
public class GroupDialog extends JDialog {
    private final LinkedList<LinkedHashMap<String, String>> templateInfos = new LinkedList<>();
    private LinkedHashMap<String, Object> groupMap;

    private final JButton groupBtn;
    private String currentKey;

    private final JTable templatesTable;
    private final DefaultTableModel tableModel;
    private JPopupMenu tablePopMenu;
    private TableRowSorter<DefaultTableModel> sorter;

    public GroupDialog(JFrame frame){
        super(frame);
        this.setTitle(L.M("dialog.custom.group.management"));
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(800, 500));
        this.setLocationRelativeTo(frame);
        this.setModal(true);

        groupBtn = new JButton(new FlatSVGIcon("icons/GroupByPackage.svg"));
        groupBtn.setText("当前管理分组: ");
        groupBtn.setToolTipText("选中分组进行管理");
        groupBtn.setSelected(true);
        groupBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu popupMenu = createPopupMenu();
                popupMenu.show(groupBtn, e.getX(), e.getY());
            }
        });

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(groupBtn);

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

        initDataForTable();

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

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        groupMap = CommonUtil.loadGroupByMap();
        if (groupMap != null) {
            for (String key : groupMap.keySet()) {
                JMenuItem tmpItem = new JMenuItem(key);
                tmpItem.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        log.debug("管理选中分组");
                        groupBtn.setText("当前管理分组：" + key);
                        currentKey = key;
                        renderTable((ArrayList<String>) groupMap.get(key));
                    }
                });
                popupMenu.add(tmpItem);
            }
        }

        return popupMenu;
    }

    private void renderTable(ArrayList<String> filePath){
        // 搜索功能
        sorter = new TableRowSorter<>(tableModel);
        templatesTable.setRowSorter(sorter);

        tableModel.setRowCount(0);
        templateInfos.clear();

        for (String path : filePath){
            templateInfos.add(CommonUtil.getTemplateInfoFromPath(path));
        }

        int count = 0;
        for (LinkedHashMap<String, String> templateInfo : templateInfos) {
            count++;
            String id = templateInfo.get("id");
            String name = templateInfo.get("name");
            String severity = templateInfo.get("severity");
            String author = templateInfo.get("author");
            String description = templateInfo.get("description");
            String reference = templateInfo.get("reference");
            String tags = templateInfo.get("tags");
            tableModel.addRow(new String[]{String.valueOf(count), id, name, severity, tags, author, description, reference});
        }

    }

    private void initDataForTable() {
         groupMap = CommonUtil.loadGroupByMap();
        if (groupMap != null) {
            for (String key : groupMap.keySet()) {
                groupBtn.setText("当前管理分组：" + key);
                currentKey = key;
                renderTable((ArrayList<String>) groupMap.get(key));
            }
        }
    }

    private JPopupMenu createTablePopMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem delItem = new JMenuItem("删除选中模板");
        delItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 删除模板
                ArrayList<String> list = (ArrayList<String>) groupMap.get(currentKey);
                for (int index : templatesTable.getSelectedRows()) {
                    int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                    String delPath = templateInfos.get(num).get("path");
                    list.remove(delPath);
                }
                groupMap.put(currentKey, list);
                CommonUtil.saveGroupToYaml(groupMap);

                // 刷新
                renderTable((ArrayList<String>) groupMap.get(currentKey));
            }
        });

        JMenu moveMenu = new JMenu("移动选中模板");
        for (String key : groupMap.keySet()){
            if (groupBtn.getText().contains(key))
                continue;   // 跳过当前分组

            JMenuItem tmpItem = new JMenuItem(key);
            tmpItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<String> currentList = (ArrayList<String>) groupMap.get(currentKey);
                    ArrayList<String> moveToList = (ArrayList<String>) groupMap.get(key);
                    for (int index : templatesTable.getSelectedRows()) {
                        int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                        String movePath = templateInfos.get(num).get("path");
                        currentList.remove(movePath);
                        moveToList.add(movePath);
                    }
                    groupMap.put(currentKey, currentList);
                    groupMap.put(key, moveToList);
                    CommonUtil.saveGroupToYaml(groupMap);

                    // 刷新
                    renderTable((ArrayList<String>) groupMap.get(currentKey));
                }
            });
            moveMenu.add(tmpItem);
        }

        popupMenu.add(delItem);
        popupMenu.add(moveMenu);

        return popupMenu;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initFlatLaf();

            GroupDialog editorDialog = new GroupDialog(null);
            editorDialog.setLocationRelativeTo(null);
            editorDialog.setVisible(true);
        });
    }

    private static void initFlatLaf() {
        try {
            if (NucleiConfig.getProperty("nuclei.theme").equals(""))
                UIManager.setLookAndFeel(new FlatLightLaf());
            else
                UIManager.setLookAndFeel(NucleiConfig.getProperty("nuclei.theme"));
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        UIManager.put("TextComponent.arc", 5);
    }
}
