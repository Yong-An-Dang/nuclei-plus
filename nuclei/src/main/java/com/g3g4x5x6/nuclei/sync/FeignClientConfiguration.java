package com.g3g4x5x6.nuclei.sync;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;

public class FeignClientConfiguration {

    public static SyncTemplateClient createClient(String url) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(SyncTemplateClient.class, url);
    }
}
