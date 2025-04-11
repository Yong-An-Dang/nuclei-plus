package com.g3g4x5x6;

import com.g3g4x5x6.nuclei.NucleiConfig;
import com.g3g4x5x6.nuclei.ultils.os.OsInfoUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.Objects;


@Getter
@Slf4j
public class WorkPathSelectionDialog extends JDialog {
    private final JRadioButton choosePathButton;
    private String selectedPath;

    public WorkPathSelectionDialog(Frame parent) {
        super(parent, "初始化程序工作目录（请选择空目录）", true);
        setLayout(new BorderLayout());
        setSize(new Dimension(550, 150));
        setPreferredSize(new Dimension(550, 150));
        setLocationRelativeTo(parent);
        setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());

        EmptyBorder border = (EmptyBorder) BorderFactory.createEmptyBorder(0, 10, 10, 10);

        // Create radio buttons
        JRadioButton useDefaultPathButton = new JRadioButton("使用默认的工作目录：" + NucleiConfig.getWorkPath());
        choosePathButton = new JRadioButton("自定义工作目录");
        choosePathButton.setToolTipText("单击选择目录");
        ButtonGroup group = new ButtonGroup();
        group.add(useDefaultPathButton);
        group.add(choosePathButton);
        useDefaultPathButton.setSelected(true);

        // Create panel and add radio buttons
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.setBorder(border);
        radioPanel.add(useDefaultPathButton);
        radioPanel.add(choosePathButton);

        choosePathButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(WorkPathSelectionDialog.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedPath = selectedFile.getAbsolutePath();
                    choosePathButton.setText("自定义工作目录：" + selectedPath);
                }
            }
        });

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            if (choosePathButton.isSelected()) {
                if (selectedPath != null && !selectedPath.isEmpty() && Files.exists(Path.of(selectedPath))) {
                    if (writeWorkPath(selectedPath)) log.debug(selectedPath);
                } else {
                    JOptionPane.showMessageDialog(WorkPathSelectionDialog.this, "尚未选择有效的自定义工作目录！", "自定义工作目录", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                if (writeWorkPath("")) log.debug("default work path");
            }
            dispose();
        });

        JPanel okPanel = new JPanel(new BorderLayout());
        okPanel.setBorder(border);
        okPanel.add(okButton, BorderLayout.CENTER);

        // Add components to dialog
        add(radioPanel, BorderLayout.CENTER);
        add(okPanel, BorderLayout.SOUTH);

        // Set dialog properties
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Add window listener to intercept the close action
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private boolean writeWorkPath(String path) {
        try {
            Files.createDirectories(NucleiConfig.initWorkPathFlagFilePath.getParent());
            Files.writeString(NucleiConfig.initWorkPathFlagFilePath, path, StandardOpenOption.CREATE);

            if (!OsInfoUtil.isMacOS() && !OsInfoUtil.isMacOSX() && !OsInfoUtil.isLinux()) {
                DosFileAttributeView attributeView = Files.getFileAttributeView(Path.of(path), DosFileAttributeView.class);
                attributeView.setHidden(true);
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
            log.debug(path);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Test Path Selection Dialog");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300, 200);
                frame.setLocationRelativeTo(null);

                JButton openDialogButton = new JButton("Open Dialog");
                openDialogButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        WorkPathSelectionDialog dialog = new WorkPathSelectionDialog(frame);
                        dialog.setVisible(true);
                        String selectedPath = dialog.getSelectedPath();
                        if (selectedPath != null) {
                            JOptionPane.showMessageDialog(frame, "Selected Path: " + selectedPath);
                        }
                    }
                });

                frame.getContentPane().add(openDialogButton, BorderLayout.CENTER);
                frame.setVisible(true);
            }
        });
    }
}
