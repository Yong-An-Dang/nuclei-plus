package com.g3g4x5x6.nuclei.ultils;

import com.alibaba.fastjson.JSONObject;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.NucleiConfig;
import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.panel.setting.ConfigAllPanel;
import com.g3g4x5x6.nuclei.panel.tab.SettingsPanel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.*;
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
        return NucleiApp.nuclei.settingsPanel.getActiveConfigAllPanel().getNucleiConfig();
    }

    public static String[] getTargets() {
        return NucleiApp.nuclei.targetPanel.getTargetText().split("\n");
    }

    @SneakyThrows
    public static String getNucleiConfigFile(Map<String, Object> config) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String savePath = NucleiConfig.getWorkPath() + "/projects/" + NucleiConfig.projectName + "/temp/config_" + format.format(new Date()) + ".yaml";
        if (!Files.exists(Path.of(savePath))) Files.createDirectories(new File(savePath).getParentFile().toPath());

        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
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

    public static void goToTarget() {
        NucleiFrame.frameTabbedPane.setSelectedIndex(1);
    }

    @SneakyThrows
    public static LinkedHashMap getMapFromYaml(String path) {
        if (!Files.exists(Path.of(path))) Files.createFile(Path.of(path));

        LinkedHashMap yamlMap = new LinkedHashMap<>();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(path);
            // 调基础工具类的方法
            Yaml yaml = new Yaml();
            yamlMap = yaml.loadAs(inputStream, LinkedHashMap.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception exception) {
            // 自动跳过格式不正确的模板
            exception.printStackTrace();
        }
        return yamlMap;
    }

    @SneakyThrows
    public static LinkedHashMap<String, Object> loadGroupByMap() {
        if (!Files.exists(Path.of(NucleiConfig.getConfigPath() + "/groupby.yaml")))
            Files.createFile(Path.of(NucleiConfig.getConfigPath() + "/groupby.yaml"));
        LinkedHashMap yamlMap = getMapFromYaml(String.valueOf(Path.of(NucleiConfig.getConfigPath() + "/groupby.yaml")));
        return yamlMap;
    }

    @SneakyThrows
    public static void saveGroupToYaml(LinkedHashMap<String, Object> groupMap) {
        if (!Files.exists(Path.of(NucleiConfig.getConfigPath() + "/groupby.yaml")))
            Files.createFile(Path.of(NucleiConfig.getConfigPath() + "/groupby.yaml"));
        if (groupMap != null) {
            Yaml yaml = new Yaml();
            yaml.dump(groupMap, new FileWriter(String.valueOf(Path.of(NucleiConfig.getConfigPath() + "/groupby.yaml"))));
        }
    }

    public static void createProjectStruct(String projectName) {
        try {
            InputStream nucleiIn = NucleiFrame.class.getClassLoader().getResourceAsStream("project.properties");
            assert nucleiIn != null;
            Files.copy(nucleiIn, Path.of(NucleiConfig.getWorkPath() + "/projects/" + projectName, "/", projectName + ".properties"));

            // 创建项目子配置目录
            if (!Files.exists(Path.of(NucleiConfig.getWorkPath(), "projects", projectName, "config")))
                Files.createDirectories(Path.of(NucleiConfig.getWorkPath(), "projects", projectName, "config"));
            // 创建默认子配置
            if (!Files.exists(Path.of(NucleiConfig.getWorkPath(), "projects", projectName, "config", "Default.yaml")))
                Files.createFile(Path.of(NucleiConfig.getWorkPath(), "projects", projectName, "config", "Default.yaml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static LinkedHashMap<String, String> getTemplateInfoFromPath(String path) {
        LinkedHashMap<String, String> templateInfo = new LinkedHashMap<>();
        Map map = getMapFromYaml(path);
        if (map != null && !map.isEmpty()) {
            JSONObject jsonObject = new JSONObject(map);
            JSONObject info = jsonObject.getJSONObject("info");
            if (info == null) {
                info = new JSONObject();
                info.put("name", "<空>");
                info.put("severity", "<空>");
                info.put("author", "<空>");
                info.put("description", "<空>");
                info.put("reference", "<空>");
                info.put("tags", "<空>");
            }

            templateInfo.put("path", path);
            templateInfo.put("id", jsonObject.getString("id") == null ? "<空>" : jsonObject.getString("id"));
            templateInfo.put("name", info.getString("name") == null ? "<空>" : info.getString("name"));
            templateInfo.put("severity", info.getString("severity") == null ? "<空>" : info.getString("severity"));
            templateInfo.put("author", info.getString("author") == null ? "<空>" : info.getString("author"));
            templateInfo.put("description", info.getString("description") == null ? "<空>" : info.getString("description"));
            templateInfo.put("reference", info.getString("reference") == null ? "<空>" : info.getString("reference"));
            templateInfo.put("tags", info.getString("tags") == null ? "<空>" : info.getString("tags"));
        } else {
            templateInfo.put("path", path);
            templateInfo.put("id", "空");
            templateInfo.put("name", "空");
            templateInfo.put("severity", "空");
            templateInfo.put("author", "空");
            templateInfo.put("description", "空");
            templateInfo.put("reference", "空");
            templateInfo.put("tags", "空");
        }
        return templateInfo;
    }

}
