package com.g3g4x5x6.nuclei.panel.template;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.http.ChatUtil;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import com.g3g4x5x6.nuclei.ultils.L;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.g3g4x5x6.nuclei.ultils.TextAreaUtils.createTextArea;

@Slf4j
public class GenerateTemplatePanel extends JPanel {
    private final JToolBar toolBar;
    private final RSyntaxTextArea textArea;

    private final JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("icons/toggleSoftWrap.svg"));
    private final JButton pasteBtn = new JButton(new FlatSVGIcon("icons/menu-paste.svg"));
    private final JButton compileBtn = new JButton(new FlatSVGIcon("icons/compile.svg"));
    private final JButton cancelBtn = new JButton(new FlatSVGIcon("icons/stop.svg"));
    private final JButton listBtn = new JButton(new FlatSVGIcon("icons/bulletList.svg"));
    private final JPopupMenu historyPopup = new JPopupMenu();
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel generatorStatusLabel = new JLabel("已复制到粘贴板");

    private SwingWorker<Void, Void> worker;

    private final HashMap<String, String> historyGenerateMap = new HashMap<>();
    private boolean isGenerate = false;

    public GenerateTemplatePanel() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(800, 900));

        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        initToolBar();

        textArea = createTextArea();
        textArea.setSyntaxEditingStyle("text/markdown");

        String templateStr = "# 模板要求\n" +
                "\n" +
                "1. poc模板除外，禁止回答其他内容\n" +
                "2. “path” 字段必须由模板变量 “BaseURL” 拼接路径\n" +
                "\n" +
                "\n" +
                "\n" +
                "# HTTP流量\n" +
                "\n" +
                "```HTTP-FLOW\n" +
                "\n" +
                "```\n" +
                "\n" +
                "\n" +
                "请根据 “HTTP流量” 以及 “模板要求”，编写一个poc模板。";
        textArea.setText(templateStr);
        resetTextAreaPopupMenu();
        RTextScrollPane rTextScrollPane = new RTextScrollPane(textArea);
        rTextScrollPane.setBorder(null);

        this.add(toolBar, BorderLayout.NORTH);
        this.add(rTextScrollPane, BorderLayout.CENTER);
    }

    private void initToolBar() {
        lineWrapBtn.setToolTipText(L.M("tab.panel.editor.generate.wrap", "编辑文本换行"));
        lineWrapBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setLineWrap(lineWrapBtn.isSelected());
            }
        });
        pasteBtn.setToolTipText(L.M("tab.panel.editor.generate.paste", "粘贴HTTP流"));
        compileBtn.setToolTipText(L.M("tab.panel.editor.generate.build", "生成PoC模板"));
        compileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        generatorStatusLabel.setVisible(false);
                        compileBtn.setEnabled(false);
                        cancelBtn.setEnabled(true);
                        // 设置进度条
                        progressBar.setIndeterminate(true);
                        progressBar.setStringPainted(true);
                        progressBar.setString("正在生成PoC模板...");
                        progressBar.setVisible(true);
                        // 调用ChatGPT
                        String resp = ChatUtil.chat(textArea.getText());
                        progressBar.setVisible(false);
                        compileBtn.setEnabled(true);
                        cancelBtn.setEnabled(false);
                        // 输出结果
                        log.debug(resp);
                        // 复制到剪贴板
                        ClipboardUtil.setStr(getGenerateTemplate(resp).strip());
                        // 标志位
                        isGenerate = true;
                        // 添加历史记录
                        addHistoryPopupMenuItem(resp);
                        // 提示
                        generatorStatusLabel.setText("生成成功，已复制到粘贴板");
                        generatorStatusLabel.setVisible(true);
                        return null;
                    }
                };
                worker.execute();
            }
        });

        cancelBtn.setEnabled(false);
        cancelBtn.setToolTipText(L.M("tab.panel.editor.generate.cancel", "取消正在执行的生成任务"));
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 取消任务
                worker.cancel(true);
                // 任务提示
                generatorStatusLabel.setText("已取消生成任务！");
                generatorStatusLabel.setVisible(true);
                // 重置组件状态
                progressBar.setVisible(false);
                compileBtn.setEnabled(true);
                cancelBtn.setEnabled(false);
            }
        });

        listBtn.setToolTipText(L.M("tab.panel.editor.generate.history", "历史生成结果"));
        listBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (historyPopup.getComponentCount() == 0) {
                    JPopupMenu jPopupMenu = new JPopupMenu();
                    jPopupMenu.add(new JMenuItem("无生成历史记录"));
                    jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                } else {
                    historyPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        progressBar.setVisible(false);
        generatorStatusLabel.setVisible(false);

        toolBar.add(lineWrapBtn);
        toolBar.add(pasteBtn);
        toolBar.addSeparator();
        toolBar.add(compileBtn);
        toolBar.add(cancelBtn);
        toolBar.add(listBtn);
        toolBar.add(Box.createGlue());
        toolBar.add(progressBar);
        toolBar.add(generatorStatusLabel);
        toolBar.add(Box.createGlue());
    }

    private String getGenerateTemplate(String genString) {
        String templateString;

        Pattern pattern = Pattern.compile(".*?```yaml(.*?)```.*?", Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(genString);
        if (matcher.find()) {
            templateString = matcher.group(1);
        } else {
            log.error("未匹配 PoC 模板信息");
            templateString = genString;

        }
        return templateString;
    }

    /**
     * 区分智能生成和规则生成（菜单项图标区分）
     * 键：时间
     * 值：生成内容
     * 点击动作：复制模板内容到粘贴板
     */
    private void addHistoryPopupMenuItem(String genString) {
        // 获取当前日期和时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化日期时间
        String formattedDateTime = currentDateTime.format(formatter);
        historyGenerateMap.put(formattedDateTime, genString);
        // 插入菜单项
        JMenuItem menuItem = new JMenuItem(formattedDateTime);
        menuItem.setToolTipText(genString);
        if (isGenerate) {
            menuItem.setIcon(new FlatSVGIcon("icons/aiAssistantColored.svg"));
        } else {
            menuItem.setIcon(new FlatSVGIcon("icons/build.svg"));
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClipboardUtil.setStr(getGenerateTemplate(genString).strip());
                generatorStatusLabel.setText("复制历史生成：" + formattedDateTime);
            }
        });
        historyPopup.insert(menuItem, 0);
    }

    @SneakyThrows
    private void resetTextAreaPopupMenu() {
        JPopupMenu popupMenu = textArea.getPopupMenu();

        JMenu insertMenu = new JMenu(L.M("tab.panel.editor.generate.popupmenu.matcher", "匹配器"));
        insertMenu.setIcon(new FlatSVGIcon("icons/regexSelected.svg"));

        JMenu extractorMenu = new JMenu(L.M("tab.panel.editor.generate.popupmenu.extractor", "提取器"));
        extractorMenu.setIcon(new FlatSVGIcon("icons/traceInto.svg"));

        JMenu helperMenu = new JMenu(L.M("tab.panel.editor.generate.popupmenu.helper", "辅助函数"));
        helperMenu.setIcon(new FlatSVGIcon("icons/function.svg"));

        popupMenu.insert(new JSeparator(), 0);
        popupMenu.insert(helperMenu, 0);
        popupMenu.insert(extractorMenu, 0);
        popupMenu.insert(insertMenu, 0);

        configTextAreaPopupMenu(insertMenu);

        textArea.setComponentPopupMenu(popupMenu);
    }

    private void configTextAreaPopupMenu(JMenu insertMenu) {
        insertMenu.add(new AbstractAction(L.M("tab.panel.editor.generate.popupmenu.matcher.ge", "大于等于")) {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}
