package com.g3g4x5x6.nuclei.panel.tabs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.panel.settings.target.FofaBot;
import com.g3g4x5x6.nuclei.ultils.DialogUtil;
import com.g3g4x5x6.nuclei.ultils.NucleiConfig;
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
import java.util.Arrays;
import java.util.LinkedList;


@Slf4j
public class StringTargetPanel extends JPanel implements SearchListener, ChangeListener {
    private final JButton newBtn = new JButton(new FlatSVGIcon("icons/deleteTagHover.svg"));
    private final JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private final JButton saveBtn = new JButton(new FlatSVGIcon("icons/menu-saveall.svg"));
    private final JButton searchBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
    private final JButton replaceBtn = new JButton(new FlatSVGIcon("icons/replace.svg"));
    private final JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("icons/toggleSoftWrap.svg"));
    private final JButton fofaxBtn = new JButton("Fofa");
    private final JButton dupBtn = new JButton(new FlatSVGIcon("icons/diffWithClipboard.svg"));

    private final RSyntaxTextArea textArea;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;

    private final JRadioButton targetBtn = new JRadioButton("-target  ", true);
    private final JRadioButton listBtn = new JRadioButton("-list   ");
    private final JRadioButton resumeBtn = new JRadioButton("-resume  ");

    private Fofax fofax;

    // 路漫漫其修远兮，吾将上下而求索
    private final JLabel showLabel = new JLabel("无目标的努力，有如在黑暗中远征");

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
        toolBar.add(fofaxBtn);
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
        dupBtn.setToolTipText("排除已验证目标（出现重复的目标）");
        dupBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("二次添加目标，对旧目标进行去除");
                textArea.setText(TextAreaUtils.getTargetsTextAgain(textArea.getText()));
            }
        });
        fofaxBtn.setSelected(true);
        fofaxBtn.addActionListener(e -> {
            if (fofax == null)
                fofax = new Fofax();
            fofax.setVisible(true);
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

    class Fofax extends JDialog implements SearchListener {
        private final FofaBot fofaBot = new FofaBot();

        private final JToolBar toolBar;
        private final JButton executeFofaxBtn = new JButton(new FlatSVGIcon("icons/execute.svg"));
        private final JButton iconHashBtn = new JButton(new FlatSVGIcon("icons/diffWithClipboard.svg"));
        private final JButton refreshBtn = new JButton(new FlatSVGIcon("icons/refresh.svg"));
        private final JButton runTipsBtn = new JButton(new FlatSVGIcon("icons/intentionBulbGrey.svg"));
        private JTextField emailField;
        private JPasswordField secretField;

        private final JToolBar statusBar;
        private JTextField qBase64Field;

        private final RSyntaxTextArea helpArea;
        private FindDialog helpFindDialog;
        private ReplaceDialog helpReplaceDialog;

        private final JButton helpSearchBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
        private final JButton helpReplaceBtn = new JButton(new FlatSVGIcon("icons/replace.svg"));
        private final JToggleButton helpLineWrapBtn = new JToggleButton(new FlatSVGIcon("icons/toggleSoftWrap.svg"));
        private final String templateArgument = "# API URL接口\n" +
                "#    protected String apiUrl;\n" +
                "apiUrl=https://fofa.info/api/v1/search/all\n" +
                "\n" +
                "# 经过base64编码后的查询语法，即输入的查询内容，这是输入搜索关键字即可，无需编码\n" +
                "# Example: \n" +
                "# \ticon_hash=\"-247388890\"\n" +
                "# \"BIG-IP&reg;-+Redirect\" && country!=\"CN\"\n" +
                "qbase64=sonar\n" +
                "\n" +
                "# 可选字段，默认host,ip,port，暂不支持修改为其他选项及其顺序\n" +
                "fields=protocol,host,port,ip\n" +
                "\n" +
                "# 是否翻页，默认为第一页，按照更新时间排序\n" +
                "page=1\n" +
                "\n" +
                "# 每页查询数量，默认为100条，最大支持10,000条/页\n" +
                "size=100\n" +
                "\n" +
                "# 默认搜索一年内的数据，指定为true即可搜索全部数据\n" +
                "full=false";

        public Fofax() {
            super(NucleiApp.nuclei);
            this.setTitle("Fofax");
            this.setLayout(new BorderLayout());
            this.setSize(new Dimension(800, 450));
            this.setLocationRelativeTo(StringTargetPanel.this);

            helpArea = createTextArea();
            helpArea.setText(templateArgument);
            helpArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_UNIX_SHELL);
            RTextScrollPane sp = new RTextScrollPane(helpArea);
            sp.setBorder(null);
            initSearchDialogs();

            toolBar = new JToolBar(JToolBar.HORIZONTAL);
            toolBar.setFloatable(false);
            initToolbar();

            statusBar = new JToolBar(JToolBar.HORIZONTAL);
            statusBar.setFloatable(false);
            initStatusBar();

            this.add(toolBar, BorderLayout.NORTH);
            this.add(sp);
            this.add(statusBar, BorderLayout.SOUTH);
            this.addWindowListener(new WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                    qBase64Field.requestFocus();
                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    log.debug("失去焦点");
                    setVisible(false);
                }
            });
        }

        private void initToolbar() {
            executeFofaxBtn.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Thread(() -> fofaxQuery()).start();
                }
            });

            iconHashBtn.setToolTipText("计算 Icon Hash");
            iconHashBtn.setEnabled(false);
            iconHashBtn.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            });

            refreshBtn.addActionListener(e -> helpArea.setText(templateArgument));

            helpLineWrapBtn.addChangeListener(e -> {
                helpArea.setLineWrap(helpLineWrapBtn.isSelected());
            });

            helpSearchBtn.setToolTipText("搜索......");
            helpSearchBtn.addActionListener(showHelpFindDialogAction);
            helpSearchBtn.registerKeyboardAction(showHelpFindDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
            helpReplaceBtn.setToolTipText("替换......");
            helpReplaceBtn.addActionListener(showHelpReplaceDialogAction);
            helpReplaceBtn.registerKeyboardAction(showHelpReplaceDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

            emailField = new JTextField();
            emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Input email");
            emailField.setText(NucleiConfig.getProperty("nuclei.fofa.email"));
            secretField = new JPasswordField();
            secretField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Input secret key");
            secretField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
            secretField.setText(NucleiConfig.getProperty("nuclei.fofa.secret"));

            toolBar.add(executeFofaxBtn);
            toolBar.add(refreshBtn);
            toolBar.add(runTipsBtn);
            toolBar.addSeparator();
            toolBar.add(iconHashBtn);
            toolBar.addSeparator();
            toolBar.add(helpLineWrapBtn);
            toolBar.addSeparator();
            toolBar.add(helpSearchBtn);
            toolBar.add(helpReplaceBtn);
            toolBar.addSeparator();
            toolBar.add(Box.createGlue());
            toolBar.add(emailField);
            toolBar.addSeparator();
            toolBar.add(secretField);
        }

        private void initStatusBar() {
            qBase64Field = new JTextField();
            qBase64Field.setText("country!=\"CN\" && ");
            qBase64Field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Input qbase64, not need encode with base64");
            qBase64Field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search, Enter");
            qBase64Field.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, new FlatSearchIcon());
            qBase64Field.registerKeyboardAction(e -> new Thread(this::fofaxQuery).start(),
                    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                    JComponent.WHEN_FOCUSED);

            statusBar.add(qBase64Field);
        }

        private void initSearchDialogs() {
            log.debug("initSearchDialogs");
            helpFindDialog = new FindDialog(this, this);
            helpReplaceDialog = new ReplaceDialog(this, this);

            // This ties the properties of the two dialogs together (match case,
            // regex, etc.).
            SearchContext context = helpFindDialog.getSearchContext();
            helpReplaceDialog.setSearchContext(context);
        }


        private void fofaxQuery() {
            runTipsBtn.setIcon(new FlatSVGIcon("icons/intentionBulb.svg"));
            packageFofaBot();
            String response = fofaBot.run();
            log.debug(response);

            JSONObject jsonObject = JSON.parseObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            if (jsonArray == null)
                jsonArray = new JSONArray();
            LinkedList<String> hosts = fofaBot.parserTargets(jsonArray, 0, 1);
            for (String host : hosts) {
                textArea.append(host + "\n");
            }
            runTipsBtn.setIcon(new FlatSVGIcon("icons/intentionBulbGrey.svg"));
        }

        private void packageFofaBot() {
            fofaBot.setEmail(emailField.getText());
            fofaBot.setSecret(String.valueOf(secretField.getPassword()));

            for (String arg : helpArea.getText().split("\n")) {
                if (!arg.strip().startsWith("#") && !arg.strip().equals("")) {
                    String[] args = arg.split("=", 2);
                    log.debug(Arrays.toString(args));
                    switch (args[0]) {
                        default:
                        case "apiUrl":
                            fofaBot.setApiUrl(args[1]);
                            break;
                        case "qbase64":
                            fofaBot.setQbase64(args[1]);
                            if (!qBase64Field.getText().strip().equals(""))
                                fofaBot.setQbase64(qBase64Field.getText());
                            break;
                        case "fields":
                            fofaBot.setFields(args[1]);
                            break;
                        case "page":
                            fofaBot.setPage(args[1]);
                            break;
                        case "size":
                            fofaBot.setSize(args[1]);
                            break;
                        case "full":
                            fofaBot.setFull(args[1]);
                            break;
                    }
                }
            }
            fofaBot.packageUrl();
        }

        AbstractAction showHelpFindDialogAction = new AbstractAction("查找") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (helpReplaceDialog.isVisible()) {
                    helpReplaceDialog.setVisible(false);
                }
                helpFindDialog.setVisible(true);
            }
        };

        AbstractAction showHelpReplaceDialogAction = new AbstractAction("替换") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (helpFindDialog.isVisible()) {
                    helpFindDialog.setVisible(false);
                }
                helpReplaceDialog.setVisible(true);
            }
        };

        @Override
        public String getSelectedText() {
            return helpArea.getSelectedText();
        }

        @Override
        public void searchEvent(SearchEvent e) {
            SearchEvent.Type type = e.getType();
            SearchContext context = e.getSearchContext();
            SearchResult result;

            switch (type) {
                default: // Prevent FindBugs warning later
                case MARK_ALL:
                    result = SearchEngine.markAll(helpArea, context);
                    break;
                case FIND:
                    result = SearchEngine.find(helpArea, context);
                    if (!result.wasFound() || result.isWrapped()) {
                        UIManager.getLookAndFeel().provideErrorFeedback(helpArea);
                    }
                    break;
                case REPLACE:
                    result = SearchEngine.replace(helpArea, context);
                    if (!result.wasFound() || result.isWrapped()) {
                        UIManager.getLookAndFeel().provideErrorFeedback(helpArea);
                    }
                    break;
                case REPLACE_ALL:
                    result = SearchEngine.replaceAll(helpArea, context);
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

    }
}
