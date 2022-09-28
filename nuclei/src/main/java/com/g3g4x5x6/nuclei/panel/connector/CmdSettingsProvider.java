package com.g3g4x5x6.nuclei.panel.connector;

import com.g3g4x5x6.nuclei.ultils.NucleiConfig;
import com.jediterm.terminal.HyperlinkStyle;
import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.model.TerminalTypeAheadSettings;
import com.jediterm.terminal.ui.TerminalActionPresentation;
import com.jediterm.terminal.ui.UIUtil;
import com.jediterm.terminal.ui.settings.ColorScheme;
import com.jediterm.terminal.ui.settings.DefaultColorPaletteImpl;
import com.jediterm.terminal.ui.settings.SettingsProvider;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Objects;

public class CmdSettingsProvider implements SettingsProvider {
    private Font chineseFont;

    private final ColorScheme colorScheme = new ColorScheme(NucleiConfig.getProperty("nuclei.color.scheme"));

    public ColorPalette getTerminalColorPalette() {
        return new DefaultColorPaletteImpl(colorScheme);
    }

    @Override
    public float getLineSpacing() {
        return Float.parseFloat(NucleiConfig.getProperty("nuclei.line.space"));
    }

    @SneakyThrows
    public Font getTerminalFont() {
        String fontName;
        if (UIUtil.isWindows) {
            fontName = NucleiConfig.getProperty("nuclei.font");
        } else if (UIUtil.isMac) {
            fontName = "Menlo";
        } else {
            fontName = "Monospaced";
        }
        return new Font(fontName, Font.BOLD, (int) this.getTerminalFontSize());
//        Font myFont = Font.createFont(Font.PLAIN, Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("fonts/" + "CascadiaCode.ttf")));
//        return myFont.deriveFont(Font.PLAIN, (int) this.getTerminalFontSize());
    }

    @SneakyThrows
    public Font getTerminalChineseFont(){
        if (chineseFont == null){
            chineseFont = Font.createFont(Font.PLAIN, Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("fonts/" + "NotoSansSC-Regular.otf")));
//            chineseFont = new Font("宋体", Font.PLAIN, (int) this.getTerminalFontSize());
        }
        return chineseFont.deriveFont(Font.PLAIN, (int) this.getTerminalFontSize());
    }

    public float getTerminalFontSize() {
        return Float.parseFloat(NucleiConfig.getProperty("nuclei.font.size"));
    }

    @Override
    public TextStyle getDefaultStyle() {
        return new TextStyle(
                TerminalColor.rgb(
                        colorScheme.getForegroundColor().getRed(),
                        colorScheme.getForegroundColor().getGreen(),
                        colorScheme.getForegroundColor().getBlue()),
                TerminalColor.rgb(
                        colorScheme.getBackgroundColor().getRed(),
                        colorScheme.getBackgroundColor().getGreen(),
                        colorScheme.getBackgroundColor().getBlue())
        );
    }

    public TextStyle getSelectionColor() {
        return new TextStyle(
                TerminalColor.rgb(
                        colorScheme.getForegroundColor().getRed(),
                        colorScheme.getForegroundColor().getGreen(),
                        colorScheme.getForegroundColor().getBlue()),
                TerminalColor.rgb(
                        colorScheme.getSelectedColor().getRed(),
                        colorScheme.getSelectedColor().getGreen(),
                        colorScheme.getSelectedColor().getBlue())
        );
    }

    public TextStyle getFoundPatternColor() {
        return new TextStyle(TerminalColor.rgb(200, 200, 200), TerminalColor.rgb(255, 255, 0));
    }

    public TextStyle getHyperlinkColor() {
        return new TextStyle(TerminalColor.awt(Color.BLUE), TerminalColor.WHITE);
    }

    public HyperlinkStyle.HighlightMode getHyperlinkHighlightingMode() {
        return HyperlinkStyle.HighlightMode.HOVER;
    }

    public boolean useInverseSelectionColor() {
        return true;
    }

    public boolean copyOnSelect() {
        return this.emulateX11CopyPaste();
    }

    public boolean pasteOnMiddleMouseClick() {
        return this.emulateX11CopyPaste();
    }

    public boolean emulateX11CopyPaste() {
        return false;
    }

    public boolean useAntialiasing() {
        return true;
    }

    public int maxRefreshRate() {
        return 50;
    }

    public boolean audibleBell() {
        return true;
    }

    public boolean enableMouseReporting() {
        return true;
    }

    public int caretBlinkingMs() {
        return 505;
    }

    public boolean scrollToBottomOnTyping() {
        return true;
    }

    public boolean DECCompatibilityMode() {
        return true;
    }

    public boolean forceActionOnMouseReporting() {
        return false;
    }

    public int getBufferMaxLinesCount() {
        return 5000;
    }

    public boolean altSendsEscape() {
        return true;
    }

    public boolean ambiguousCharsAreDoubleWidth() {
        return false;
    }

    @NotNull
    public TerminalTypeAheadSettings getTypeAheadSettings() {
        return DefaultConsoleTypeAheadSettings.DEFAULT;
    }

    @NotNull
    public TerminalActionPresentation getNewSessionActionPresentation() {
        return new TerminalActionPresentation("New Session", UIUtil.isMac ? KeyStroke.getKeyStroke(84, 256) : KeyStroke.getKeyStroke(84, 192));
    }

    @NotNull
    public TerminalActionPresentation getOpenUrlActionPresentation() {
        return new TerminalActionPresentation("Open as URL", Collections.emptyList());
    }

    @NotNull
    public TerminalActionPresentation getCopyActionPresentation() {
        KeyStroke keyStroke = UIUtil.isMac ? KeyStroke.getKeyStroke(67, 256) : KeyStroke.getKeyStroke(67, 192);
        return new TerminalActionPresentation("Copy", keyStroke);
    }

    @NotNull
    public TerminalActionPresentation getPasteActionPresentation() {
        KeyStroke keyStroke = UIUtil.isMac ? KeyStroke.getKeyStroke(86, 256) : KeyStroke.getKeyStroke(86, 192);
        return new TerminalActionPresentation("Paste", keyStroke);
    }

    @NotNull
    public TerminalActionPresentation getClearBufferActionPresentation() {
        return new TerminalActionPresentation("Clear Buffer", UIUtil.isMac ? KeyStroke.getKeyStroke(75, 256) : KeyStroke.getKeyStroke(76, 128));
    }

    @NotNull
    public TerminalActionPresentation getPageUpActionPresentation() {
        return new TerminalActionPresentation("Page Up", KeyStroke.getKeyStroke(33, 64));
    }

    @NotNull
    public TerminalActionPresentation getPageDownActionPresentation() {
        return new TerminalActionPresentation("Page Down", KeyStroke.getKeyStroke(34, 64));
    }

    @NotNull
    public TerminalActionPresentation getLineUpActionPresentation() {
        return new TerminalActionPresentation("Line Up", UIUtil.isMac ? KeyStroke.getKeyStroke(38, 256) : KeyStroke.getKeyStroke(38, 128));
    }

    @NotNull
    public TerminalActionPresentation getLineDownActionPresentation() {
        return new TerminalActionPresentation("Line Down", UIUtil.isMac ? KeyStroke.getKeyStroke(40, 256) : KeyStroke.getKeyStroke(40, 128));
    }

    @NotNull
    public TerminalActionPresentation getCloseSessionActionPresentation() {
        return new TerminalActionPresentation("Close Session", UIUtil.isMac ? KeyStroke.getKeyStroke(87, 256) : KeyStroke.getKeyStroke(87, 192));
    }

    @NotNull
    public TerminalActionPresentation getFindActionPresentation() {
        return new TerminalActionPresentation("Find", UIUtil.isMac ? KeyStroke.getKeyStroke(70, 256) : KeyStroke.getKeyStroke(70, 128));
    }

    @NotNull
    public TerminalActionPresentation getSelectAllActionPresentation() {
        return new TerminalActionPresentation("Select All", Collections.emptyList());
    }

}
