package com.jediterm.terminal.ui;

import java.util.List;

/**
 * @author traff
 */
public interface TerminalActionProvider {
    List<TerminalAction> getActions();

    com.jediterm.terminal.ui.TerminalActionProvider getNextProvider();

    void setNextProvider(com.jediterm.terminal.ui.TerminalActionProvider provider);
}
