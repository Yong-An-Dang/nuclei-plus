package com.g3g4x5x6.nuclei.ultils;


import com.g3g4x5x6.nuclei.NucleiConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class ProjectUtil {
    @SneakyThrows
    public static String reportDir() {
        String projectName = NucleiConfig.projectName;
        if (!Files.exists(Path.of(NucleiConfig.getWorkPath(), "projects", projectName, "report")))
            Files.createDirectories(Path.of(NucleiConfig.getWorkPath(), "projects", projectName, "report"));
        return Path.of(NucleiConfig.getWorkPath(), "projects", projectName, "report").toString();
    }

    @SneakyThrows
    public static String configDir() {
        String projectName = NucleiConfig.projectName;
        if (!Files.exists(Path.of(NucleiConfig.getWorkPath(), "projects", projectName, "config")))
            Files.createDirectories(Path.of(NucleiConfig.getWorkPath(), "projects", projectName, "config"));
        return Path.of(NucleiConfig.getWorkPath(), "projects", projectName, "config").toString();
    }

    public static String ConfigFilePath() {
        return Path.of(NucleiConfig.getWorkPath(), "projects", NucleiConfig.projectName, NucleiConfig.projectName + ".properties").toString();
    }

    public static void newProject(String projectName) throws IOException {
        NucleiConfig.projectName = projectName;
        String newPath = NucleiConfig.getWorkPath() + "/projects/" + NucleiConfig.projectName;
        if (!Files.exists(Path.of(newPath)))
            Files.createDirectories(new File(newPath).getParentFile().toPath());
        else
            DialogUtil.warn("项目已存在！");
        // TODO 生成并加载初始化配置
    }

    public static void saveProject() {

    }

    public static void loadProject() {

    }

    public static void deleteProject() {

    }
}
