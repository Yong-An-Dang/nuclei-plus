package com.g3g4x5x6.nuclei.panel.settings;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.ultils.DialogUtil;
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
import java.util.List;
import java.util.Map;


@Slf4j
public class SettingDebug extends JPanel implements SearchListener {
    private final JCheckBox debugBtn = new JCheckBox("-debug    ");
    private final JCheckBox dreqBtn = new JCheckBox("-dreq    ");
    private final JCheckBox drespBtn = new JCheckBox("-dresp    ");
    private final JCheckBox piBtn = new JCheckBox("-pi    ");
    private final JCheckBox versionBtn = new JCheckBox("-version    ");
    private final JCheckBox hmBtn = new JCheckBox("-hm    ");
    private final JCheckBox vBtn = new JCheckBox("-v    ");
    private final JCheckBox vvBtn = new JCheckBox("-vv    ");
    private final JCheckBox epBtn = new JCheckBox("-ep    ");
    private final JCheckBox tvBtn = new JCheckBox("-tv    ");
    private final JCheckBox hcBtn = new JCheckBox("-health-check    ");

    private static RSyntaxTextArea textArea;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;
    private final JButton searchBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
    private final JButton replaceBtn = new JButton(new FlatSVGIcon("icons/replace.svg"));
    private final JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("icons/toggleSoftWrap.svg"));

    private final String preText = "#\tDEBUG:\n" +
            "#\t   -debug                    show all requests and responses\n" +
            "#\t   -dreq, -debug-req         show all sent requests\n" +
            "#\t   -dresp, -debug-resp       show all received responses\n" +
            "#\t   -p, -proxy string[]       list of http/socks5 proxy to use (comma separated or file input)\n" +
            "#\t   -pi, -proxy-internal      proxy all internal requests\n" +
            "#\t   -tlog, -trace-log string  file to write sent requests trace log\n" +
            "#\t   -elog, -error-log string  file to write sent requests error log\n" +
            "#\t   -version                  show nuclei version\n" +
            "#\t   -hm, -hang-monitor        enable nuclei hang monitoring\n" +
            "#\t   -v, -verbose              show verbose output\n" +
            "#\t   -vv                       display templates loaded for scan\n" +
            "#\t   -ep, -enable-pprof        enable pprof debugging server\n" +
            "#\t   -tv, -templates-version   shows the version of the installed nuclei-templates\n" +
            "#\t   -health-check             run diagnostic check up\n" +
            "\n" +
            "# file to write sent requests trace log\n" +
            "tlog: \"\"\n" +
            "\n" +
            "# file to write sent requests error log\n" +
            "elog: \"\"\n" +
            "\n" +
            "# list of http/socks5 proxy to use (comma separated or file input)\n" +
            "proxy: [\n" +
            "    # socks5://XXX:XXX@XYZ.tld:5080\n" +
            "    socks5://127.0.0.1:10808,\n" +
            "]";

    public SettingDebug() {
        this.setLayout(new BorderLayout());
        this.setBorder(null);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setFloatable(false);
        toolBar.add(lineWrapBtn);
        toolBar.add(searchBtn);
        toolBar.add(replaceBtn);
        toolBar.addSeparator();
        toolBar.add(debugBtn);
        toolBar.add(dreqBtn);
        toolBar.add(drespBtn);
        toolBar.add(piBtn);
        toolBar.add(versionBtn);
        toolBar.add(hmBtn);
        toolBar.add(vBtn);
        toolBar.add(vvBtn);
        toolBar.add(epBtn);
        toolBar.add(tvBtn);
        toolBar.add(hcBtn);

        initToolBarAction();

        textArea = createTextArea();
        textArea.setText(preText);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setBorder(null);
        initSearchDialogs();

        this.add(toolBar, BorderLayout.NORTH);
        this.add(sp, BorderLayout.CENTER);
    }

    private void initToolBarAction() {
        lineWrapBtn.addChangeListener(e -> textArea.setLineWrap(lineWrapBtn.isSelected()));
        searchBtn.setToolTipText("搜索......");
        searchBtn.addActionListener(showFindDialogAction);
        searchBtn.registerKeyboardAction(showFindDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        replaceBtn.setToolTipText("替换......");
        replaceBtn.addActionListener(showReplaceDialogAction);
        replaceBtn.registerKeyboardAction(showReplaceDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        debugBtn.setToolTipText("show all requests and responses");
        dreqBtn.setToolTipText("show all sent requests");
        drespBtn.setToolTipText("show all received responses");
        piBtn.setToolTipText("proxy all internal requests");
        versionBtn.setToolTipText("show nuclei version");
        hmBtn.setToolTipText("enable nuclei hang monitoring");
        vBtn.setToolTipText("show verbose output");
        vvBtn.setToolTipText("display templates loaded for scan");
        epBtn.setToolTipText("enable pprof debugging server");
        tvBtn.setToolTipText("shows the version of the installed nuclei-templates");
        hcBtn.setToolTipText("run diagnostic check up");

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

    /**
     * 获取代理配置
     * @return
     */
    public List<String> getProxy() {
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(textArea.getText());
        return (List<String>) obj.get("proxy");
    }

    /**
     * 获取调试配置
     * @return
     */
    public boolean isDebug(){
        return debugBtn.isSelected();
    }

}
