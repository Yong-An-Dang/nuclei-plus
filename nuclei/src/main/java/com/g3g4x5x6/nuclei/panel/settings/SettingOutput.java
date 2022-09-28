package com.g3g4x5x6.nuclei.panel.settings;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.ultils.DialogUtil;
import com.g3g4x5x6.nuclei.ultils.NucleiConfig;
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
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;


@Slf4j
public class SettingOutput extends JPanel implements SearchListener {
    private final JButton searchBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
    private final JButton replaceBtn = new JButton(new FlatSVGIcon("icons/replace.svg"));
    private final JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("icons/toggleSoftWrap.svg"));


    private final JCheckBox jsonBtn = new JCheckBox("-json    ");
    private final JCheckBox seBtn = new JCheckBox("-se    ");
//    private final JCheckBox drespBtn = new JCheckBox("-dresp    ");
//    private final JCheckBox piBtn = new JCheckBox("-pi    ");
//    private final JCheckBox versionBtn = new JCheckBox("-version    ");
//    private final JCheckBox hmBtn = new JCheckBox("-hm    ");
//    private final JCheckBox vBtn = new JCheckBox("-v    ");
//    private final JCheckBox vvBtn = new JCheckBox("-vv    ");
//    private final JCheckBox epBtn = new JCheckBox("-ep    ");
//    private final JCheckBox tvBtn = new JCheckBox("-tv    ");
//    private final JCheckBox hcBtn = new JCheckBox("-health-check    ");

    private static RSyntaxTextArea textArea;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;

    private String preText = "#\tOUTPUT:\n" +
            "#\t   -o, -output string            output file to write found issues/vulnerabilities\n" +
            "#\t   -sresp, -store-resp           store all request/response passed through nuclei to output directory\n" +
            "#\t   -srd, -store-resp-dir string  store all request/response passed through nuclei to custom directory (default \"output\")\n" +
            "#\t   -silent                       display findings only\n" +
            "#\t   -nc, -no-color                disable output content coloring (ANSI escape codes)\n" +
            "#\t   -json                         write output in JSONL(ines) format\n" +
            "#\t   -irr, -include-rr             include request/response pairs in the JSONL output (for findings only)\n" +
            "#\t   -nm, -no-meta                 disable printing result metadata in cli output\n" +
            "#\t   -nts, -no-timestamp           disable printing timestamp in cli output\n" +
            "#\t   -rdb, -report-db string       nuclei reporting database (always use this to persist report data)\n" +
            "#\t   -ms, -matcher-status          display match failure status\n" +
            "#\t   -me, -markdown-export string  directory to export results in markdown format\n" +
            "#\t   -se, -sarif-export string     file to export results in SARIF format\n" +
            "\n" +
            "# 注意：请输入文件路径，不能是目录。最好不要改了，要提取漏洞目标\n" +
            "# output file to write found issues/vulnerabilities\n" +
            "output: \"" +
            Path.of(NucleiConfig.getProperty("nuclei.report.path"), "issues.txt").toString().replace("\\", "\\\\") +
            "\"\n\n" +
            "# 导出 `markdown` 格式报告，每个目标生成一份报告\n" +
            "# directory to export results in markdown format\n" +
            "# markdown: \"" +
            Path.of(NucleiConfig.getProperty("nuclei.report.path")).toString().replace("\\", "\\\\") +
            "\"\n\n" +
            "# file to export results in SARIF format\n" +
            "# se: \"" +
            Path.of(NucleiConfig.getProperty("nuclei.report.path"), "SARIF.txt").toString().replace("\\", "\\\\") +
            "\"\n";

    public SettingOutput() {
        this.setLayout(new BorderLayout());
        this.setBorder(null);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(lineWrapBtn);
        toolBar.add(searchBtn);
        toolBar.add(replaceBtn);
        toolBar.addSeparator();
        toolBar.add(jsonBtn);

        initToolBarAction();

        textArea = createTextArea();
        textArea.setText(preText);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setBorder(null);
        initSearchDialogs();

        this.add(toolBar, BorderLayout.NORTH);
        this.add(sp, BorderLayout.CENTER);
    }

    private RSyntaxTextArea createTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.requestFocusInWindow();
        textArea.setCaretPosition(0);
        textArea.setMarkOccurrences(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setClearWhitespaceLinesEnabled(false);
        textArea.setCodeFoldingEnabled(true);
        textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_YAML);

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

        lineWrapBtn.addChangeListener(e -> {
            textArea.setLineWrap(lineWrapBtn.isSelected());
        });

        jsonBtn.setToolTipText("write output in JSONL(ines) format");
        jsonBtn.setSelected(false);

        seBtn.setToolTipText("file to export results in SARIF format");
        seBtn.setSelected(true);

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
                JOptionPane.showMessageDialog(null, result.getCount() +
                        " occurrences replaced.");
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

    public String getOutputPath() {
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(textArea.getText());
        return (String) obj.get("output");
    }

    public String getMarkdownExportPath() {
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(textArea.getText());
        return (String) obj.get("markdown");
    }

    public boolean isJson(){
        return jsonBtn.isSelected();
    }

    public String getSeFilePath(){
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(textArea.getText());
        return (String) obj.get("se");
    }
}
