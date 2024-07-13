package com.g3g4x5x6.nuclei.ultils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SyncUtil {
    private static final String syncTemplatePath = NucleiConfig.getProperty("nuclei.templates.path.sync");

    public static void upload() {
        log.info("上传所有同步模板 From: {}", syncTemplatePath);
    }

    public static void download() {
        log.info("下载所有同步模板 To: {}", syncTemplatePath);
    }
}
