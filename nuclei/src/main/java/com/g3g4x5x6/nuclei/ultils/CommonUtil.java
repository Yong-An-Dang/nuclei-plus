package com.g3g4x5x6.nuclei.ultils;

import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.panel.setting.ConfigAllPanel;
import com.g3g4x5x6.nuclei.panel.tab.SettingsPanel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class CommonUtil {
    /**
     * 获取 url
     */
    public static String urlRegex(String line) {
        // 按指定模式在字符串查找
        String pattern = "(http|https)://(www.)?(\\w+(\\.)?)+:*\\d+";
        String result = "";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        if (m.find()) {
            result = m.group(0);
            System.out.println("Found value: " + m.group(0));
        } else {
            System.out.println("NO MATCH");
        }
        return result;
    }

    /**
     * 把文本设置到剪贴板（复制）
     */
    public static void setClipboardString(String text) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = new StringSelection(text);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(trans, null);
    }

    /**
     * 从剪贴板中获取文本（粘贴）
     */
    public static String getClipboardString() {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // 获取剪贴板中的内容
        Transferable trans = clipboard.getContents(null);

        if (trans != null) {
            // 判断剪贴板中的内容是否支持文本
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // 获取剪贴板中的文本内容
                    return (String) trans.getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static Map<String, Object> getNucleiConfigObject() {
        return NucleiApp.nuclei.settingsPanel.activeConfigAllPanel.getNucleiConfig();
    }

    public static String[] getTargets() {
        return NucleiApp.nuclei.targetPanel.getTargetText().split("\n");
    }

    @SneakyThrows
    public static String getNucleiConfigFile(Map<String, Object> config) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String savePath = NucleiConfig.getWorkPath() + "/projects/" + NucleiConfig.projectName + "/temp/config_" + format.format(new Date()) + ".yaml";
        if (!Files.exists(Path.of(savePath)))
            Files.createDirectories(new File(savePath).getParentFile().toPath());

        Yaml yaml = new Yaml(new SafeConstructor());
        yaml.dump(config, new FileWriter(savePath));
        return new File(savePath).getCanonicalPath();
    }

    public static LinkedHashMap<String, ConfigAllPanel> getConfigPanels() {
        LinkedHashMap<String, ConfigAllPanel> configPanels = new LinkedHashMap<>();
        int count = SettingsPanel.tabbedPane.getTabCount();
        for (int i = 0; i < count; i++) {
            configPanels.put(SettingsPanel.tabbedPane.getTitleAt(i), (ConfigAllPanel) SettingsPanel.tabbedPane.getComponentAt(i));
        }
        return configPanels;
    }

    public static JPopupMenu getConfigPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        LinkedHashMap<String, ConfigAllPanel> configPanels = getConfigPanels();
        for (String configName : configPanels.keySet()) {
            JMenuItem tempItem = new JMenuItem(configName);
            tempItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log.debug("Active config: " + configName);
                    NucleiFrame.activeBtn.setText("当前活动配置：" + configName);
                    NucleiApp.nuclei.settingsPanel.setActiveConfigPanel(configPanels.get(configName));
                }
            });
            popupMenu.add(tempItem);
        }
        return popupMenu;
    }
}
