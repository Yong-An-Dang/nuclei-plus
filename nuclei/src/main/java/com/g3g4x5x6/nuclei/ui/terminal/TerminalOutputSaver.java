package com.g3g4x5x6.nuclei.ui.terminal;

import com.jediterm.core.typeahead.TerminalTypeAheadManager;
import com.jediterm.terminal.*;
import org.jetbrains.annotations.NotNull;

public class TerminalOutputSaver extends TerminalStarter {

    public TerminalOutputSaver(@NotNull Terminal terminal, @NotNull TtyConnector ttyConnector, @NotNull TerminalDataStream dataStream, @NotNull TerminalTypeAheadManager typeAheadManager, @NotNull TerminalExecutorServiceManager executorServiceManager) {
        super(terminal, ttyConnector, dataStream, typeAheadManager, executorServiceManager);
    }

}