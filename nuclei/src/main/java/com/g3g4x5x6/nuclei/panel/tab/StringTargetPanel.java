package com.g3g4x5x6.nuclei.panel.tab;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.ultils.DialogUtil;
import com.g3g4x5x6.nuclei.NucleiConfig;
import com.g3g4x5x6.nuclei.ultils.L;
import com.g3g4x5x6.nuclei.ultils.TextAreaUtils;
import lombok.extern.slf4j.Slf4j;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.MatchedBracketPopup;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class StringTargetPanel extends JPanel implements SearchListener, ChangeListener {
    private final JButton newBtn = new JButton(new FlatSVGIcon("icons/delete.svg"));
    private final JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private final JButton saveBtn = new JButton(new FlatSVGIcon("icons/menu-saveall.svg"));
    private final JButton searchBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
    private final JButton replaceBtn = new JButton(new FlatSVGIcon("icons/replace.svg"));
    private final JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("icons/toggleSoftWrap.svg"));
    private final JButton dupBtn = new JButton(new FlatSVGIcon("icons/diffWithClipboard.svg"));

    private final RSyntaxTextArea textArea;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;

    private final JRadioButton targetBtn = new JRadioButton("-target  ", true);
    private final JRadioButton listBtn = new JRadioButton("-list   ");
    private final JRadioButton resumeBtn = new JRadioButton("-resume  ");

    // 路漫漫其修远兮，吾将上下而求索
    private final JLabel showLabel = new JLabel(L.M("tab.panel.targets.moto", "无目标的努力，有如在黑暗中远征"));

    public StringTargetPanel() {
        this.setLayout(new BorderLayout());
        this.setBorder(null);

        initOptionsComponents();

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(targetBtn);
        toolBar.add(listBtn);
        toolBar.add(resumeBtn);
        toolBar.addSeparator();
        toolBar.add(newBtn);
        toolBar.addSeparator();
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.add(lineWrapBtn);
        toolBar.addSeparator();
        toolBar.add(searchBtn);
        toolBar.add(replaceBtn);
        toolBar.addSeparator();
        toolBar.add(dupBtn);
        toolBar.addSeparator();
        toolBar.add(Box.createGlue());
        toolBar.add(showLabel);
        toolBar.add(Box.createGlue());
        //
        initToolBarAction();

        textArea = createTextArea();
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setBorder(null);
        initSearchDialogs();

        this.add(toolBar, BorderLayout.NORTH);
        this.add(sp, BorderLayout.CENTER);
    }

    private void initOptionsComponents() {
        // 创建一个按钮组
        ButtonGroup btnGroup = new ButtonGroup();

        // 添加单选按钮到按钮组
        btnGroup.add(targetBtn);
        btnGroup.add(listBtn);
        btnGroup.add(resumeBtn);

        // 添加监听器
        targetBtn.addChangeListener(this);
        listBtn.addChangeListener(this);
        listBtn.setEnabled(false);
        resumeBtn.addChangeListener(this);
        resumeBtn.setEnabled(false);
    }

    private RSyntaxTextArea createTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.requestFocusInWindow();
        textArea.setCaretPosition(0);
        textArea.setMarkOccurrences(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setClearWhitespaceLinesEnabled(false);
        textArea.setCodeFoldingEnabled(true);
        textArea.setSyntaxEditingStyle("text/plain");

        InputMap im = textArea.getInputMap();
        ActionMap am = textArea.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "decreaseFontSize");
        am.put("decreaseFontSize", new RSyntaxTextAreaEditorKit.DecreaseFontSizeAction());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "increaseFontSize");
        am.put("increaseFontSize", new RSyntaxTextAreaEditorKit.IncreaseFontSizeAction());

        int ctrlShift = InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, ctrlShift), "copyAsStyledText");
        am.put("copyAsStyledText", new RSyntaxTextAreaEditorKit.CopyCutAsStyledTextAction(true));

        try {

            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, ctrlShift), "copyAsStyledTextMonokai");
            am.put("copyAsStyledTextMonokai", createCopyAsStyledTextAction("monokai"));

            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, ctrlShift), "copyAsStyledTextEclipse");
            am.put("copyAsStyledTextEclipse", createCopyAsStyledTextAction("dark"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Since this demo allows the LookAndFeel and RSyntaxTextArea Theme to
        // be toggled independently of one another, we set this property to
        // true so matched bracket popups look good.  In an app where the
        // developer ensures the RSTA Theme always matches the LookAndFeel as
        // far as light/dark is concerned, this property can be omitted.
        System.setProperty(MatchedBracketPopup.PROPERTY_CONSIDER_TEXTAREA_BACKGROUND, "true");

        return textArea;
    }

    private Action createCopyAsStyledTextAction(String themeName) throws IOException {
        String resource = "/org/fife/ui/rsyntaxtextarea/themes/" + themeName + ".xml";
        Theme theme = Theme.load(this.getClass().getResourceAsStream(resource));
        return new RSyntaxTextAreaEditorKit.CopyCutAsStyledTextAction(themeName, theme, true);
    }

    private void initSearchDialogs() {
        log.debug("initSearchDialogs");
        findDialog = new FindDialog(NucleiApp.nuclei, this);
        replaceDialog = new ReplaceDialog(NucleiApp.nuclei, this);

        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = findDialog.getSearchContext();
        replaceDialog.setSearchContext(context);
    }

    private void initToolBarAction() {
        dupBtn.setToolTipText(L.M("tab.panel.targets.dup.tip", "排除已验证目标（出现重复的目标）"));
        dupBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("二次添加目标，对旧目标进行去除");
                textArea.setText(TextAreaUtils.getTargetsTextAgain(textArea.getText()));
            }
        });

        newBtn.setToolTipText("清空当前目标");
        newBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });

        openBtn.addActionListener(e -> {
            // 创建一个默认的文件选取器
            JFileChooser fileChooser = new JFileChooser();
            // 设置默认显示的文件夹为当前文件夹
            fileChooser.setCurrentDirectory(new File(NucleiConfig.getWorkPath()));
            // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            // 设置是否允许多选
            fileChooser.setMultiSelectionEnabled(true);
            // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
            int result = fileChooser.showOpenDialog(StringTargetPanel.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                // 如果点击了"确定", 则获取选择的文件路径
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    new Thread(() -> {
                        try {
                            for (String line : Files.readAllLines(Path.of(file.getAbsolutePath()))) {
                                textArea.append(line + "\n");
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }).start();
                }
            }
        });

        saveBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    popupMenu.add(new AbstractAction("另存为") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // 创建一个默认的文件选取器
                            JFileChooser fileChooser = new JFileChooser();
                            // 设置默认显示的文件夹为当前文件夹
                            fileChooser.setCurrentDirectory(new File(NucleiConfig.getWorkPath()));
                            // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
                            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            // 设置是否允许多选
                            fileChooser.setMultiSelectionEnabled(true);
                            // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
                            int result = fileChooser.showSaveDialog(StringTargetPanel.this);

                            if (result == JFileChooser.APPROVE_OPTION) {
                                // 如果点击了"确定", 则获取选择的文件路径
                                File file = fileChooser.getSelectedFile();
                                try {
                                    Files.writeString(Path.of(file.getAbsolutePath()), textArea.getText());
                                    showLabel.setText(file.getAbsolutePath());
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    });
                    popupMenu.show(saveBtn, e.getX(), getY());

                } else {
                    if (!Files.exists(Path.of(showLabel.getText()))) {
                        // 创建一个默认的文件选取器
                        JFileChooser fileChooser = new JFileChooser();
                        // 设置默认显示的文件夹为当前文件夹
                        fileChooser.setCurrentDirectory(new File(NucleiConfig.getWorkPath()));
                        // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        // 设置是否允许多选
                        fileChooser.setMultiSelectionEnabled(true);
                        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
                        int result = fileChooser.showSaveDialog(StringTargetPanel.this);

                        if (result == JFileChooser.APPROVE_OPTION) {
                            // 如果点击了"确定", 则获取选择的文件路径
                            File file = fileChooser.getSelectedFile();
                            showLabel.setText(file.getAbsolutePath());
                        }
                    }
                    try {
                        Files.writeString(Path.of(showLabel.getText()), textArea.getText());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        lineWrapBtn.addChangeListener(e -> {
            textArea.setLineWrap(lineWrapBtn.isSelected());
        });

        searchBtn.setToolTipText("搜索......");
        searchBtn.addActionListener(showFindDialogAction);
        searchBtn.registerKeyboardAction(showFindDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        replaceBtn.setToolTipText("替换......");
        replaceBtn.addActionListener(showReplaceDialogAction);
        replaceBtn.registerKeyboardAction(showReplaceDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }

    public String getTargetText(){
        return this.textArea.getText();
    }

    @Override
    public String getSelectedText() {
        return textArea.getSelectedText();
    }

    @Override
    public void searchEvent(SearchEvent e) {
        SearchEvent.Type type = e.getType();
        SearchContext context = e.getSearchContext();
        SearchResult result;

        switch (type) {
            default: // Prevent FindBugs warning later
            case MARK_ALL:
                result = SearchEngine.markAll(textArea, context);
                break;
            case FIND:
                result = SearchEngine.find(textArea, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE:
                result = SearchEngine.replace(textArea, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE_ALL:
                result = SearchEngine.replaceAll(textArea, context);
                JOptionPane.showMessageDialog(null, result.getCount() + " occurrences replaced.");
                break;
        }

        String text;
        if (result.wasFound()) {
            text = "Text found; occurrences marked: " + result.getMarkedCount();
        } else if (type == SearchEvent.Type.MARK_ALL) {
            if (result.getMarkedCount() > 0) {
                text = "Occurrences marked: " + result.getMarkedCount();
            } else {
                text = "";
            }
        } else {
            text = "Text not found";
            DialogUtil.warn(text);
        }
    }

    AbstractAction showFindDialogAction = new AbstractAction("查找") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (replaceDialog.isVisible()) {
                replaceDialog.setVisible(false);
            }
            findDialog.setVisible(true);
        }
    };

    AbstractAction showReplaceDialogAction = new AbstractAction("替换") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (findDialog.isVisible()) {
                findDialog.setVisible(false);
            }
            replaceDialog.setVisible(true);
        }
    };

    @Override
    public void stateChanged(ChangeEvent e) {

    }
}
