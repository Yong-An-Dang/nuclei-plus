package com.g3g4x5x6.nuclei.ultils;

import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.model.GlobalConfigModel;
import com.g3g4x5x6.nuclei.panel.tabs.RunningPanel;
import com.g3g4x5x6.nuclei.panel.console.ConsolePanel;

import java.util.UUID;

public class ExecUtils {
    public static void runGlobalNucleiConfig(ConsolePanel consolePanel){
        GlobalConfigModel globalConfigModel = GlobalConfigModel.createGlobalConfigModel();

        String configPath = NucleiConfig.getProperty("nuclei.temp.path") + "/" + UUID.randomUUID() + ".yaml";
        globalConfigModel.toYaml(globalConfigModel, configPath);

        consolePanel.write("nuclei -config " + configPath + "\r");
        NucleiFrame.frameTabbedPane.setSelectedIndex(3);
        RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
    }
}
