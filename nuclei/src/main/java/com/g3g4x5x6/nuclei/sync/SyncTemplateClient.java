package com.g3g4x5x6.nuclei.sync;


import com.g3g4x5x6.nuclei.sync.models.ApiResponse;
import com.g3g4x5x6.nuclei.sync.models.DownloadRequest;
import com.g3g4x5x6.nuclei.sync.models.UploadRequest;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface SyncTemplateClient {

    @RequestLine("POST /sync")
    @Headers("X-Sync-Token: {token}")
    ApiResponse upload(@Param("token") String token, UploadRequest request);

    @RequestLine("POST /sync")
    @Headers("X-Sync-Token: {token}")
    ApiResponse download(@Param("token") String token, DownloadRequest request);
}
