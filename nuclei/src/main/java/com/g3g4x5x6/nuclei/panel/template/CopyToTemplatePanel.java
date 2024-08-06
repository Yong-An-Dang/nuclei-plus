package com.g3g4x5x6.nuclei.panel.template;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.http.ChatUtil;
import com.g3g4x5x6.nuclei.panel.tab.EditTemplatePanel;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.g3g4x5x6.nuclei.ultils.TextAreaUtils.createTextArea;

@Slf4j
public class CopyToTemplatePanel extends JPanel {
    private final JToolBar toolBar;
    private final RSyntaxTextArea textArea;

    private final JButton pasteBtn = new JButton(new FlatSVGIcon("icons/menu-paste.svg"));
    private final JButton compileBtn = new JButton(new FlatSVGIcon("icons/compile.svg"));
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel generatorStatusLabel = new JLabel("已复制到粘贴板");

    public CopyToTemplatePanel() {
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
        pasteBtn.setToolTipText("粘贴HTTP流");
        compileBtn.setToolTipText("生成PoC模板");
        compileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        generatorStatusLabel.setVisible(false);
                        compileBtn.setEnabled(false);
                        // 设置进度条
                        progressBar.setIndeterminate(true);
                        progressBar.setStringPainted(true);
                        progressBar.setString("正在生成PoC模板...");
                        progressBar.setVisible(true);
                        // 调用ChatGPT
                        String resp = ChatUtil.chat(textArea.getText());
                        progressBar.setVisible(false);
                        compileBtn.setEnabled(true);
                        // 输出结果
                        log.debug(resp);
                        // 复制到剪贴板
                        ClipboardUtil.setStr(getGenerateTemplate(resp).strip());
                        generatorStatusLabel.setText("生成成功，已复制到粘贴板");
                        generatorStatusLabel.setVisible(true);
                        return null;
                    }
                };
                worker.execute();
            }
        });

        progressBar.setVisible(false);
        generatorStatusLabel.setVisible(false);

        toolBar.add(pasteBtn);
        toolBar.add(compileBtn);
        toolBar.add(Box.createGlue());
        toolBar.add(progressBar);
        toolBar.add(generatorStatusLabel);
        toolBar.add(Box.createGlue());
    }

    private String getGenerateTemplate(String genString) {
        String templateString = "";

        Pattern pattern = Pattern.compile(".*?```yaml(.*?)```.*?", Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(genString);
        if (matcher.find()) {
            templateString = matcher.group(1);
        } else {
            log.error("未匹配 PoC 模板信息");

        }
        return templateString;
    }

    @SneakyThrows
    private void resetTextAreaPopupMenu() {
        JPopupMenu popupMenu = textArea.getPopupMenu();

        JMenu insertMenu = new JMenu("匹配器");
        insertMenu.setIcon(new FlatSVGIcon("icons/regexSelected.svg"));

        JMenu extractorMenu = new JMenu("提取器");
        extractorMenu.setIcon(new FlatSVGIcon("icons/traceInto.svg"));

        JMenu helperMenu = new JMenu("辅助函数");
        helperMenu.setIcon(new FlatSVGIcon("icons/function.svg"));

        popupMenu.insert(new JSeparator(), 0);
        popupMenu.insert(helperMenu, 0);
        popupMenu.insert(extractorMenu, 0);
        popupMenu.insert(insertMenu, 0);

        configTextAreaPopupMenu(insertMenu);

        textArea.setComponentPopupMenu(popupMenu);
    }

    private void configTextAreaPopupMenu(JMenu insertMenu) {
        insertMenu.add(new AbstractAction("大于等于") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}
