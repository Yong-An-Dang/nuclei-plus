package com.g3g4x5x6.nuclei.http;


import com.g3g4x5x6.nuclei.http.models.sync.SyncApiResponse;
import com.g3g4x5x6.nuclei.http.models.sync.SyncDownloadRequest;
import com.g3g4x5x6.nuclei.http.models.sync.SyncUploadRequest;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface FeignClient {

    @RequestLine("POST /sync")
    @Headers("X-Sync-Token: {token}")
    SyncApiResponse upload(@Param("token") String token, SyncUploadRequest request);

    @RequestLine("POST /sync")
    @Headers("X-Sync-Token: {token}")
    SyncApiResponse download(@Param("token") String token, SyncDownloadRequest request);
}
