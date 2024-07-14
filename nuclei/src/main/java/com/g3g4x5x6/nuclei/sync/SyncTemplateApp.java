package com.g3g4x5x6.nuclei.sync;

import com.g3g4x5x6.nuclei.sync.models.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

import java.util.Collections;

public class SyncTemplateApp {

    public static void main(String[] args) {
        SyncTemplateClient client = FeignClientConfiguration.createClient("http://127.0.0.1:4523/m1/4821097-4475877-default");

        // Upload example
        UploadRequest uploadRequest = new UploadRequest();
        uploadRequest.setAction("upload");
        uploadRequest.setCount(1);
        Template template = new Template();
        template.setDir("file/bash");
        template.setContent("Template content");
        uploadRequest.setTemplates(Collections.singletonList(template));

        ApiResponse uploadResponse = client.upload("xxxxxxxxxxxxxxxx", uploadRequest);
        System.out.println("Upload response: " + uploadResponse.getReason());

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
        downloadRequest.setPageSize(1);
        downloadRequest.setCurrentPage(1);

        ApiResponse downloadResponse = client.download("xxxxxxxxxxxxxxxx", downloadRequest);
        System.out.println("Download response: " + downloadResponse.getReason());
        downloadResponse.getTemplates().forEach(t -> System.out.println("Template content: " + t.getContent()));
    }
}
