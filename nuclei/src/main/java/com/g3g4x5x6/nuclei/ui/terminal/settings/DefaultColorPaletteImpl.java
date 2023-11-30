package com.g3g4x5x6.nuclei.ui.terminal.settings;

import com.jediterm.core.Color;
import com.jediterm.terminal.emulator.ColorPalette;
import org.jetbrains.annotations.NotNull;

public class DefaultColorPaletteImpl extends ColorPalette {
    private Color[] myColors;

    public DefaultColorPaletteImpl(@NotNull ColorScheme colorScheme) {
        myColors = colorScheme.getColors();
    }

    @Override
    protected @NotNull Color getForegroundByColorIndex(int i) {
        return myColors[i];
    }

    @Override
    protected @NotNull Color getBackgroundByColorIndex(int i) {
        return myColors[i];
    }
}
