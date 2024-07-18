package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.nuclei.ultils.CommonUtil;
import com.g3g4x5x6.nuclei.ultils.DialogUtil;
import com.g3g4x5x6.nuclei.NucleiConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


@Slf4j
public class StartupFrame extends JFrame {
    private final JRadioButton defaultBtn = new JRadioButton("默认项目（Default）");
    private final JRadioButton newBtn = new JRadioButton("创建项目");
    private final JRadioButton selectBtn = new JRadioButton("打开项目");

    private final JTextField newTextField = new JTextField();
    private final JTable projectsTable = new JTable();
    private DefaultTableModel tableModel;

    private final JButton okBtn = new JButton("启动");
    private final JButton cancelBtn = new JButton("取消");

    public StartupFrame() {
        this.setLayout(new BorderLayout());
        this.setTitle(NucleiConfig.getProperty("nuclei.title"));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(new Dimension(700, 450));
        this.setPreferredSize(new Dimension(700, 450));
        this.setMinimumSize(new Dimension(700, 450));
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());

        this.setLayout(new BorderLayout());

        setupBottomPanel();
        initComponent();
        initLayout();
        initAction();
    }

    private void setupBottomPanel() {
        EmptyBorder border = (EmptyBorder) BorderFactory.createEmptyBorder(0, 0, 10, 10);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(border);
        panel.add(cancelBtn);
        panel.add(okBtn);
        this.add(panel, BorderLayout.SOUTH);
    }

    private void initComponent() {
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String[] columnNames = {"project_name", "project_file"};
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.addRow(new String[]{"空", "空"});
        projectsTable.setModel(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        projectsTable.getColumn("project_name").setCellRenderer(centerRenderer);
        projectsTable.getColumn("project_name").setPreferredWidth(20);
        initTable();
    }

    private void initTable() {
        File projects = new File(NucleiConfig.getWorkPath() + "/projects");
        tableModel.setRowCount(0);
        for (File project : Objects.requireNonNull(projects.listFiles(File::isDirectory))) {
            tableModel.addRow(new String[]{project.getName(), project.getAbsolutePath()});
        }
    }

    private void initLayout() {
        defaultBtn.setSelected(true);

        // 单选按钮
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(defaultBtn);
        btnGroup.add(newBtn);
        btnGroup.add(selectBtn);

        JScrollPane tableScrollPane = new JScrollPane(projectsTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        EmptyBorder border = (EmptyBorder) BorderFactory.createEmptyBorder(15, 25, 10, 15);

        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new BorderLayout());
        JPanel panel3 = new JPanel(new BorderLayout());

        panel1.setBorder(border);
        panel2.setBorder(border);
        panel3.setBorder(border);

        panel1.add(defaultBtn, BorderLayout.WEST);

        panel2.add(newBtn, BorderLayout.NORTH);
        panel2.add(newTextField, BorderLayout.CENTER);

        panel3.add(selectBtn, BorderLayout.NORTH);
        panel3.add(tableScrollPane, BorderLayout.CENTER);

        Box vBox = Box.createVerticalBox();
        vBox.add(panel1);
        vBox.add(panel2);
        vBox.add(panel3);
        this.add(vBox, BorderLayout.CENTER);
    }

    public void initAction() {
        newTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                log.debug("输入项目名称");
                newBtn.setSelected(true);
            }
        });

        projectsTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                log.debug("选择已有项目");
                selectBtn.setSelected(true);
            }
        });

        okBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                // 项目名称至关重要
                String projectName = "";

                if (defaultBtn.isSelected()) {
                    projectName = "default";
                    if (!Files.exists(Path.of(NucleiConfig.getWorkPath() + "/projects/default"))) {
                        Files.createDirectories(Path.of(NucleiConfig.getWorkPath() + "/projects/default"));
                        Files.createDirectories(Path.of(NucleiConfig.getWorkPath() + "/projects/" + projectName));
                        CommonUtil.createProjectStruct(projectName);
                    }
                }
                if (newBtn.isSelected()) {
                    projectName = newTextField.getText().strip();
                    if (!projectName.equals("")) {
                        Files.createDirectories(Path.of(NucleiConfig.getWorkPath() + "/projects/" + projectName));
                        CommonUtil.createProjectStruct(projectName);
                    } else {
                        DialogUtil.warn("新建项目不能为空");
                    }
                }
                if (selectBtn.isSelected()) {
                    projectName = getProjectName();
                }

                if (!projectName.equals("")) {
                    next(projectName);
                }
            }
        });
        cancelBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (DialogUtil.yesOrNo(StartupFrame.this, "是否退出程序？") == JOptionPane.YES_OPTION) System.exit(0);
            }
        });
    }

    private String getProjectName() {
        int index = projectsTable.getSelectedRow();
        String projectName = projectsTable.getValueAt(index, 0).toString();
        if (!isExistProject(projectName)) {
            DialogUtil.warn("项目不存在");
            projectName = "";
        }
        return projectName;
    }

    @SneakyThrows
    private boolean isExistProject(String projectName) {
        if (!Files.exists(Path.of(NucleiConfig.getWorkPath() + "/projects/" + projectName))) return false;

        // 项目配置文件不存在则创建
        if (!Files.exists(Path.of(NucleiConfig.getWorkPath() + "/projects/" + projectName, "/", projectName + ".properties")))
            CommonUtil.createProjectStruct(projectName);

        return true;
    }

    private void showDisabledStatus() {
        cancelBtn.setEnabled(false);

        okBtn.setEnabled(false);
        okBtn.setText("加载中...");

        defaultBtn.setEnabled(false);
        newBtn.setEnabled(false);
        selectBtn.setEnabled(false);

        newTextField.setEnabled(false);
        projectsTable.setEnabled(false);

    }

    private void next(String projectName) {
        NucleiConfig.projectName = projectName;

        // 创建后台任务，启动主程序
        SwingWorker<String, Object> task = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                // 程序加载进度条
                showDisabledStatus();

                NucleiApp.createGUI();
                return "OK";
            }

            @Override
            protected void done() {
                // 此方法将在后台任务完成后在事件调度线程中被回调
                // 取消启动窗口
                try {
                    String result = get();  // 获取 doInBackground 的返回值
                    if ("OK".equals(result)) {
                        log.debug("已执行：NucleiApp.createGUI();");
                        StartupFrame.this.dispose();
                        log.debug("已执行：StartupFrame.this.dispose();");
                    }
                } catch (InterruptedException e) {
                    // 处理中断异常
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // 处理执行异常
                    e.printStackTrace();
                }
            }
        };

        // 启动任务
        task.execute();
    }

    public static void setup() {
        StartupFrame frame = new StartupFrame();

        if (!Files.exists(NucleiConfig.initWorkPathFlagFilePath)) {
            WorkPathSelectionDialog dialog = new WorkPathSelectionDialog(frame);
            dialog.setVisible(true);
        }
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        initFlatLaf();

        StartupFrame frame = new StartupFrame();
        frame.pack();
        frame.setVisible(true);
    }


    private static void initFlatLaf() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        UIManager.put(Optional.of("TextComponent.arc"), 5);
    }
}
