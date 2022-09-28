package com.g3g4x5x6.nuclei.panel.connector;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;

import java.util.concurrent.TimeUnit;

public class DefaultConsoleTypeAheadSettings {
    public static final com.jediterm.terminal.model.TerminalTypeAheadSettings DEFAULT;
    private final boolean myEnabled;
    private final long myLatencyThreshold;
    private final TextStyle myTypeAheadStyle;

    public DefaultConsoleTypeAheadSettings(boolean enabled, long latencyThreshold, TextStyle typeAheadColor) {
        this.myEnabled = enabled;
        this.myLatencyThreshold = latencyThreshold;
        this.myTypeAheadStyle = typeAheadColor;
    }

    public boolean isEnabled() {
        return this.myEnabled;
    }

    public long getLatencyThreshold() {
        return this.myLatencyThreshold;
    }

    public TextStyle getTypeAheadStyle() {
        return this.myTypeAheadStyle;
    }

    static {
        DEFAULT = new com.jediterm.terminal.model.TerminalTypeAheadSettings(true, TimeUnit.MILLISECONDS.toNanos(100L), new TextStyle(new TerminalColor(8), (TerminalColor) null));
    }
}
