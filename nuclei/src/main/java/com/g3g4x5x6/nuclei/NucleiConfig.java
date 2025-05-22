package com.g3g4x5x6.nuclei;

import com.g3g4x5x6.nuclei.ultils.DialogUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


@Slf4j
public class NucleiConfig {
    public static Path initWorkPathFlagFilePath = Path.of(System.getProperties().getProperty("user.home"), ".nuclei-plus", ".init_work_path_flag");
    public static Properties properties = loadProperties();
    public static String projectName = "";

    private NucleiConfig() {

    }

    private static Properties loadProperties() {
        // 初始化应用配置
        if (!Files.exists(Path.of(NucleiConfig.getPropertiesPath()))) {
            try {
                InputStream nucleiIn = NucleiFrame.class.getClassLoader().getResourceAsStream("nuclei.properties");
                assert nucleiIn != null;

                Path targetPath = Path.of(NucleiConfig.getPropertiesPath());
                // 确保父目录存在
                Files.createDirectories(targetPath.getParent());

                Files.copy(nucleiIn, targetPath);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }

        // 加载配置
        Properties properties = new Properties();
        try {
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream(NucleiConfig.getPropertiesPath()), StandardCharsets.UTF_8);
            properties.load(inputStream);
        } catch (Exception ignored) {

        }
        return properties;
    }

    public static void reloadProperties() {
        NucleiConfig.properties = loadProperties();
    }

    public static String getProperty(String key) {
        String[] vars = new String[]{"{home}#" + getHomePath(), "{workspace}#" + getWorkPath(),};
        String value = properties.getProperty(key);
        for (String var : vars) {
            if (value.contains(var.split("#")[0])) {
                value = value.replace(var.split("#")[0], var.split("#")[1]);
            }
        }
        return value;
    }

    public static String getHomePath() {
        return Path.of(System.getProperties().getProperty("user.home")).toString();
    }

    public static String getWorkPath() {
        String workPath = Path.of(getHomePath() + "/.nuclei-plus/").toString();
        if (Files.exists(NucleiConfig.initWorkPathFlagFilePath)) {
            try {
                String tmpPath = Files.readString(NucleiConfig.initWorkPathFlagFilePath);
                if (!tmpPath.isEmpty() && Files.exists(Path.of(tmpPath))) {
                    workPath = tmpPath;
                }
            } catch (IOException e) {
                e.fillInStackTrace();
                log.error(e.getMessage());
            }
        }
        return workPath;
    }

    public static String getConfigPath() {
        return getWorkPath() + "/config";
    }

    public static String getPropertiesPath() {
        return getWorkPath() + "/config/nuclei.properties";
    }

    public static void setProperty(String key, String value) {
        if (value.startsWith(getHomePath().replace("\\", "/")))
            NucleiConfig.properties.setProperty(key, "{home}" + value.replace(getHomePath().replace("\\", "/"), ""));
        else NucleiConfig.properties.setProperty(key, value);
    }

    public static void saveSettingsProperties() {
        try {
            StringBuilder settingsText = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(getPropertiesPath()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && !line.isBlank()) {
                    String key = line.strip().split("=")[0];
                    line = key + "=" + (NucleiConfig.properties.getProperty(key) != null ? NucleiConfig.properties.getProperty(key) : line.strip().split("=")[1]);
                }
                settingsText.append(line).append("\n");
            }
            Files.write(Path.of(getPropertiesPath()), settingsText.toString().getBytes(StandardCharsets.UTF_8));
            DialogUtil.info("保存配置成功!");
        } catch (Exception e) {
            DialogUtil.error("保存配置失败：" + e.getMessage());
        }
    }
}
