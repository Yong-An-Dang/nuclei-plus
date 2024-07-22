package com.g3g4x5x6.nuclei.http;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;

public class FeignClientConfiguration {

    public static FeignClient createClient(String url) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(FeignClient.class, url);
    }
}
