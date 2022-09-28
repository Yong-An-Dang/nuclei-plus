package com.jediterm.terminal.ui.settings;

import com.jediterm.terminal.emulator.ColorPalette;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DefaultColorPaletteImpl extends ColorPalette {
    private final Color[] myColors;

    public DefaultColorPaletteImpl(@NotNull ColorScheme colorScheme) {
        myColors = colorScheme.getColors();
    }

    @NotNull
    @Override
    public Color getForegroundByColorIndex(int colorIndex) {
        return myColors[colorIndex];
    }

    @NotNull
    @Override
    protected Color getBackgroundByColorIndex(int colorIndex) {
        return myColors[colorIndex];
    }

}
