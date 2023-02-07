package com.g3g4x5x6.nuclei.ultils;


import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class ProjectUtil {
    public static String reportDir(){

        return "";
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
