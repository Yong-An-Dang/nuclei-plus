package com.g3g4x5x6.nuclei.ultils;

import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.model.GlobalConfigModel;
import com.g3g4x5x6.nuclei.panel.tab.RunningPanel;
import com.g3g4x5x6.nuclei.panel.console.ConsolePanel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExecUtils {
    public static void runGlobalNucleiConfig(ConsolePanel consolePanel){
        Map<String, Object> config = new HashMap<>(CommonUtil.getNucleiConfigObject());
        config.put("target", CommonUtil.getTargets());

        // 配置对象
        String configPath = CommonUtil.getNucleiConfigFile(config);

        consolePanel.write("nuclei -config " + configPath + "\r");
        NucleiFrame.frameTabbedPane.setSelectedIndex(3);
        RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
    }
}
