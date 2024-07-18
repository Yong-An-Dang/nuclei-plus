package com.g3g4x5x6.nuclei.ultils;

import com.g3g4x5x6.nuclei.NucleiConfig;
import com.g3g4x5x6.nuclei.sync.FeignClientConfiguration;
import com.g3g4x5x6.nuclei.sync.SyncTemplateClient;
import com.g3g4x5x6.nuclei.sync.models.*;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@Slf4j
public class SyncUtil {
    private static final String syncTemplatePath = NucleiConfig.getProperty("nuclei.templates.sync.path");

    private static final SyncTemplateClient client = FeignClientConfiguration.createClient(NucleiConfig.getProperty("nuclei.templates.sync.url"));


    public static void upload() {
        log.info("上传所有同步模板 From: {}", syncTemplatePath);

        int fromIndex = 0;
        int toIndex = 3;
        LinkedList<Template> templates = getAllSyncTemplates();
        int total = templates.size();
        while (true) {
            // Upload example
            UploadRequest uploadRequest = new UploadRequest();
            uploadRequest.setAction("upload");
            uploadRequest.setCount(templates.subList(fromIndex, toIndex).size());
            uploadRequest.setTemplates(templates.subList(fromIndex, toIndex));

            ApiResponse uploadResponse = client.upload(NucleiConfig.getProperty("nuclei.templates.sync.auth.value"), uploadRequest);
            log.info("Code: {}, Reason: {}", uploadResponse.getCode(), uploadResponse.getReason());

            if (toIndex == total) break;
            //
            fromIndex += 3;
            toIndex += 3;
            if (fromIndex > total) fromIndex = total;
            if (toIndex > total) toIndex = total;
        }

    }

    public static void download() {
        log.info("下载所有同步模板 To: {}", syncTemplatePath);

        int pageSize = 10;
        int curPage = 1;
        while (true) {
            // Download example
            DownloadRequest downloadRequest = new DownloadRequest();
            downloadRequest.setAction("download");
            Filter filter = new Filter();
            filter.setDir("");
            filter.setId("");
            filter.setName("");
            filter.setAuthor("");
            filter.setSeverity("");
            filter.setTags("");
            downloadRequest.setFilter(filter);
            downloadRequest.setPageSize(pageSize);
            downloadRequest.setCurrentPage(curPage);

            ApiResponse downloadResponse = client.download(NucleiConfig.getProperty("nuclei.templates.sync.auth.value"), downloadRequest);
            log.info("Code: {}, Reason: {}", downloadResponse.getCode(), downloadResponse.getReason());

            downloadResponse.getTemplates().forEach(SyncUtil::createOrUpdate);
            // total: 8
            // pageSize: 3
            // currentPage: 3
            // totalPage: (int)Math.ceil( total / pageSize ) = 3
            // pageSize * currentPage = 8 > total
            if (downloadResponse.getPageSize() * downloadResponse.getCurrentPage() >= downloadResponse.getTotal()) {
                break;
            }
            curPage++;
        }
    }

    private static void createOrUpdate(Template template) {
        System.out.println("Template content: " + template.getContent());

        String dir = template.getDir();
        String content = template.getContent();

        Yaml yaml = new Yaml();
        Map templateInfo = yaml.loadAs(content, Map.class);

        Path path = Path.of(syncTemplatePath, dir, templateInfo.get("id") + ".yaml");
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static LinkedList<Template> getAllSyncTemplates() {
        LinkedList<Template> templates = new LinkedList<>();

        try {
            Files.walkFileTree(Paths.get(syncTemplatePath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String dir = file.toString().replace("\\", "/").replace(syncTemplatePath, "");
                    String content = Files.readString(file);
                    // 在这里可以对每个文件进行处理
                    Template template = new Template();
                    template.setDir(dir);
                    template.setContent(content);
                    templates.add(template);
                    //
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return templates;
    }
}

