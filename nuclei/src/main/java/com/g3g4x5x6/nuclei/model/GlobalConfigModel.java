package com.g3g4x5x6.nuclei.model;

import com.g3g4x5x6.NucleiApp;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;


@Slf4j
@Data
public class GlobalConfigModel {
    private List<String> target = new LinkedList<>();
    private String list;
    private List<String> templates = new LinkedList<>();
    private List<String> templateUrl = new LinkedList<>();
    private List<String> workflows = new LinkedList<>();
    private List<String> workflowUrl = new LinkedList<>();
    private boolean validate;
    private boolean tl;
    private List<String> tags = new LinkedList<>();
    private List<String> includeTags = new LinkedList<>();
    private List<String> excludeTags = new LinkedList<>();
    private List<String> includeTemplates = new LinkedList<>();
    private List<String> excludeTemplates = new LinkedList<>();
    private String severity;
    private String excludeSeverity;
    private String type;
    private String excludeType;
    private List<String> author = new LinkedList<>();
    private String output;
    private boolean silent;
    private boolean noColor;
    private boolean json;
    private boolean includeRr;
    private boolean noMeta;
    private boolean noTimestamp;
    private String reportDb;
    private boolean matcherStatus;
    private String markdownExport;
    private String sarifExport;
    private String config;
    private String reportConfig;
    private List<LinkedHashMap<String, String>> header;
    private String var;
    private String resolvers;
    private boolean systemResolvers;
    private boolean passive;
    private boolean envVars;
    private String clientCert;
    private String clientKey;
    private String clientCa;
    private String interactshServer;
    private String interactshToken;
    private int interactionsCacheSize;
    private int interactionsEviction;
    private int interactionsPollDuration;
    private int interactionsCooldownPeriod;
    private boolean noInteractsh;
    private int rateLimit;
    private int rateLimitMinute;
    private int bulkSize;
    private int concurrency;
    private int headlessBulkSize;
    private int headlessConcurrency;
    private int timeout;
    private int retries;
    private int maxHostError;
    private boolean project;
    private String projectPath;
    private boolean stopAtFirstPath;
    private boolean stream;
    private boolean headless;
    private int pageTimeout;
    private boolean showBrowser;
    private boolean systemChrome;
    private boolean debug;
    private boolean debugReq;
    private boolean debugResp;
    private List<String> proxy = new LinkedList<>();
    private String traceLog;
    private String errorLog;
    private boolean version;
    private boolean verbose;
    private boolean vv;
    private boolean templatesVersion;
    private boolean update;
    private boolean updateTemplates;
    private String updateDirectory;
    private boolean disableUpdateCheck;
    private boolean stats;
    private int statsJson;
    private int statsInterval;
    private boolean metrics;
    private int metricsPort;


    public static GlobalConfigModel createGlobalConfigModel() {
        GlobalConfigModel globalConfigModel = new GlobalConfigModel();

        // Target
        globalConfigModel.setTarget(Arrays.asList(NucleiApp.nuclei.getSettingsPanel().getTargetSetting().getTargetText().split("\\s+")));

        // Templates
        globalConfigModel.setTemplates(Arrays.asList(NucleiApp.nuclei.getSettingsPanel().getTemplateSetting().getTemplateText().strip().split("\\s+")));
        globalConfigModel.setWorkflows(Arrays.asList(NucleiApp.nuclei.getSettingsPanel().getTemplateSetting().getWorkflowText().strip().split("\\s+")));

        // Filtering

        // Output
        globalConfigModel.setOutput(NucleiApp.nuclei.getSettingsPanel().getOutputSetting().getOutputPath());
        globalConfigModel.setMarkdownExport(NucleiApp.nuclei.getSettingsPanel().getOutputSetting().getMarkdownExportPath());
        globalConfigModel.setJson(NucleiApp.nuclei.getSettingsPanel().getOutputSetting().isJson());
        globalConfigModel.setSarifExport(NucleiApp.nuclei.getSettingsPanel().getOutputSetting().getSeFilePath());

        // Configuration

        // Interactsh

        // RateLimit

        // Optimizations

        // Headless

        // Debug
        // ??????????????????
        globalConfigModel.setDebug(NucleiApp.nuclei.getSettingsPanel().getDebugSetting().isDebug());
        // ??????????????????
        globalConfigModel.setProxy(NucleiApp.nuclei.getSettingsPanel().getDebugSetting().getProxy());

        // Update

        // Statics


        return globalConfigModel;
    }

    public void initTypeDescriptions(Yaml yaml) {

        TypeDescription globalConfigModelTypeDesc = new TypeDescription(GlobalConfigModel.class);
        globalConfigModelTypeDesc.substituteProperty("markdown-export", String.class, "getMarkdownExport", "setMarkdownExport");

        globalConfigModelTypeDesc.setExcludes("markdownExport");

        yaml.addTypeDescription(globalConfigModelTypeDesc);
    }

    @SneakyThrows
    public void toYaml(String path) {
        // ????????????????????????
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            if (file.getParentFile().mkdirs())
                log.debug("??????????????????");
            else
                log.debug("??????????????????");
        }
        // ?????? yaml ??????
        Yaml yaml = new Yaml();

        // ?????????????????????
        initTypeDescriptions(yaml);

        // ????????????????????????
        GlobalConfigModel globalConfigModel = createGlobalConfigModel();

        // ?????? yaml ??????
        yaml.dump(globalConfigModel, new FileWriter(path));
    }

    @SneakyThrows
    public void toYaml(GlobalConfigModel globalConfigModel, String path) {
        // ????????????????????????
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            if (file.getParentFile().mkdirs())
                log.debug("??????????????????");
            else
                log.debug("??????????????????");
        }
        // ?????? yaml ??????
        Yaml yaml = new Yaml();

        // ?????????????????????
        initTypeDescriptions(yaml);

        // ?????? yaml ??????
        yaml.dump(globalConfigModel, new FileWriter(path));
    }
}
