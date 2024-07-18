package com.g3g4x5x6.nuclei.panel.console;

import com.g3g4x5x6.nuclei.panel.tab.RunningPanel;
import com.g3g4x5x6.nuclei.NucleiConfig;
import com.g3g4x5x6.nuclei.ultils.os.OsInfoUtil;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ConsolePanel extends JPanel {
    private NucleiProcessTtyConnector ttyConnector;

    public ConsolePanel() {
        this.setLayout(new BorderLayout());
        this.add(createTerminal(), BorderLayout.CENTER);
    }

    public void refreshTerminal() {
        this.add(createTerminal(), BorderLayout.CENTER);
    }

    public void write(String command) {
        try {
            ttyConnector.write(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JediTermWidget createTerminal() {
        CmdSettingsProvider cmdSettingsProvider = new CmdSettingsProvider();
        JediTermWidget terminalPanel = new JediTermWidget(800, 450, cmdSettingsProvider);
        terminalPanel.setTtyConnector(createTtyConnector());
        terminalPanel.start();
        ttyConnector = (NucleiProcessTtyConnector) terminalPanel.getTtyConnector();
        return terminalPanel;
    }

    private static @NotNull TtyConnector createTtyConnector() {
        try {
            Map<String, String> envs = System.getenv();
            String[] command;
            if (OsInfoUtil.isWindows()) {
                String path = envs.get("Path") + ";" + Path.of(RunningPanel.nucleiPath);
                envs = new HashMap<>(System.getenv());
                envs.put("Path", path);
                command = new String[]{NucleiConfig.getProperty("nuclei.terminal.shell")};
            } else {
                // 注意拼接符
                String path = envs.get("PATH") + ":" + Path.of(RunningPanel.nucleiPath);
                envs = new HashMap<>(System.getenv());
                envs.put("TERM", "xterm-256color");
                envs.put("PATH", path);
                command = new String[]{"/bin/bash"};

            }
            log.debug(envs.toString());
            PtyProcess process = new PtyProcessBuilder().setDirectory(NucleiConfig.getWorkPath()).setCommand(command).setEnvironment(envs).start();

            return new NucleiProcessTtyConnector(process, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static class NucleiProcessTtyConnector extends ProcessTtyConnector {

        private final PtyProcess myProcess;

        public NucleiProcessTtyConnector(@NotNull PtyProcess process, @NotNull Charset charset) {
            super(process, charset);
            this.myProcess = process;
        }

        public void resize(@NotNull Dimension termWinSize) {
            if (this.isConnected()) {
                this.myProcess.setWinSize(new WinSize(termWinSize.width, termWinSize.height));
            }

        }

        public boolean isConnected() {
            return this.myProcess.isRunning();
        }

        public String getName() {
            return "Nuclei-Console";
        }
    }
}
