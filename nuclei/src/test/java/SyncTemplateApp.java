import com.g3g4x5x6.nuclei.http.FeignClient;
import com.g3g4x5x6.nuclei.http.FeignClientConfiguration;
import com.g3g4x5x6.nuclei.http.models.sync.*;

import java.util.Collections;

public class SyncTemplateApp {

    public static void main(String[] args) {
        FeignClient client = FeignClientConfiguration.createClient("http://127.0.0.1:4523/m1/4821097-4475877-default");

        // Upload example
        SyncUploadRequest uploadRequest = new SyncUploadRequest();
        uploadRequest.setAction("upload");
        uploadRequest.setCount(1);
        SyncTemplate template = new SyncTemplate();
        template.setDir("file/bash");
        template.setContent("Template content");
        uploadRequest.setTemplates(Collections.singletonList(template));

        SyncApiResponse uploadResponse = client.upload("xxxxxxxxxxxxxxxx", uploadRequest);
        System.out.println("Upload response: " + uploadResponse.getReason());

        // Download example
        SyncDownloadRequest downloadRequest = new SyncDownloadRequest();
        downloadRequest.setAction("download");
        SyncFilter filter = new SyncFilter();
        filter.setDir("");
        filter.setId("");
        filter.setName("");
        filter.setAuthor("");
        filter.setSeverity("");
        filter.setTags("");
        downloadRequest.setFilter(filter);
        downloadRequest.setPageSize(1);
        downloadRequest.setCurrentPage(1);

        SyncApiResponse downloadResponse = client.download("xxxxxxxxxxxxxxxx", downloadRequest);
        System.out.println("Download response: " + downloadResponse.getReason());
        downloadResponse.getTemplates().forEach(t -> System.out.println("Template content: " + t.getContent()));
    }
}
