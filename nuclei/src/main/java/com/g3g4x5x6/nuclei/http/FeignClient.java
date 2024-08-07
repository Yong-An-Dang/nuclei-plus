package com.g3g4x5x6.nuclei.http;

import com.g3g4x5x6.nuclei.http.models.chat.ChatApiRequest;
import com.g3g4x5x6.nuclei.http.models.chat.ChatApiResponse;
import com.g3g4x5x6.nuclei.http.models.sync.SyncApiResponse;
import com.g3g4x5x6.nuclei.http.models.sync.SyncDownloadRequest;
import com.g3g4x5x6.nuclei.http.models.sync.SyncUploadRequest;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface FeignClient {

    @RequestLine("POST /template/sync")
    @Headers("X-Access-Token: {token}")
    SyncApiResponse upload(@Param("token") String token, SyncUploadRequest request);

    @RequestLine("POST /template/sync")
    @Headers("X-Access-Token: {token}")
    SyncApiResponse download(@Param("token") String token, SyncDownloadRequest request);

    @RequestLine("POST /openai/chat")
    @Headers("X-Access-Token: {token}")
    ChatApiResponse openaiChat(@Param("token") String token, ChatApiRequest request);
}
