package com.g3g4x5x6.nuclei.panel.setting;

import com.formdev.flatlaf.FlatClientProperties;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;


@Slf4j
public class SettingUpdate extends JPanel implements SearchListener {
    private JCheckBox updateBtn = new JCheckBox("-update    ");
    private JCheckBox updateTpBtn = new JCheckBox("-update-templates    ");
    private JCheckBox disableBtn = new JCheckBox("-disable-update-check    ");
    private JTextField udField = new JTextField(11);

    private static RSyntaxTextArea textArea;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;
    private final JButton searchBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
    private final JButton replaceBtn = new JButton(new FlatSVGIcon("icons/replace.svg"));
    private final JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("icons/toggleSoftWrap.svg"));

    private final String preText = "#\tUPDATE:\n" +
            "#\t   -update                        update nuclei engine to the latest released version\n" +
            "#\t   -ut, -update-templates         update nuclei-templates to latest released version\n" +
            "#\t   -ud, -update-directory string  overwrite the default directory to install nuclei-templates\n" +
            "#\t   -duc, -disable-update-check    disable automatic nuclei/templates update check\n" +
            "\n";

    public SettingUpdate() {
        this.setLayout(new BorderLayout());
        this.setBorder(null);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setFloatable(false);
        toolBar.add(lineWrapBtn);
        toolBar.add(searchBtn);
        toolBar.add(replaceBtn);
        toolBar.addSeparator();
        toolBar.add(updateBtn);
        toolBar.add(updateTpBtn);
        toolBar.add(disableBtn);
        toolBar.add(udField);

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
        updateBtn.setToolTipText("update nuclei engine to the latest released version");
        updateTpBtn.setToolTipText("update nuclei-templates to latest released version");
        disableBtn.setToolTipText("disable automatic nuclei/templates update check");
        udField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "-ud   -update-directory   overwrite the default directory to install nuclei-templates");

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

    public static void addTemplates(String templatePath){
        textArea.append(templatePath + "\n");
    }

}
