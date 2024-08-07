package com.g3g4x5x6.nuclei.http;

import com.g3g4x5x6.nuclei.NucleiConfig;
import com.g3g4x5x6.nuclei.http.models.chat.ChatApiRequest;
import com.g3g4x5x6.nuclei.http.models.chat.ChatApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatUtil {
    private static final FeignClient client = FeignClientConfiguration
            .createClient(NucleiConfig.getProperty("nuclei.cloud.server.url"));

    public static String chat(String humanMessage) {
        ChatApiRequest chatApiRequest = new ChatApiRequest();
        chatApiRequest.setHumanMessage(humanMessage);

        ChatApiResponse chatApiResponse = client
                .openaiChat(NucleiConfig.getProperty("nuclei.cloud.server.token"), chatApiRequest);
        log.info("AiMessage: {}", chatApiResponse.getAiMessage());

        return chatApiResponse.getAiMessage();
    }
}