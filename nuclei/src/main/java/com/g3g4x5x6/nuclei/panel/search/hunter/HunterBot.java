package com.g3g4x5x6.nuclei.panel.search.hunter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.g3g4x5x6.nuclei.ultils.DialogUtil;
import com.g3g4x5x6.nuclei.ultils.NucleiConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@Slf4j
public class HunterBot extends HunterVo {
    private final OkHttpClient client = new OkHttpClient();
    private String secret;

    public HunterBot() {
        if (NucleiConfig.getProperty("nuclei.fofa.api") == null || NucleiConfig.getProperty("nuclei.fofa.api").strip().equals("")) {
            apiUrl = "https://hunter.qianxin.com/openApi/search";
        } else {
            apiUrl = NucleiConfig.getProperty("nuclei.hunter.api");
        }
        if (NucleiConfig.getProperty("nuclei.hunter.key") == null || NucleiConfig.getProperty("nuclei.hunter.key").strip().equals("")) {
            DialogUtil.warn("请配置有效密钥");
        } else {
            secret = NucleiConfig.getProperty("nuclei.hunter.key");
        }

        search = "5Y2O6aG65L%2Bh5a6J";
        page = "1";
        size = "100";
    }

    public String packageUrl(String qbase64) {
        String url = apiUrl +
                "?api-key=" + secret +
                "&search=" + qbase64 +
                "&page=" + page +
                "&page_size=" + size +
                "&is_web=" + 3;         // 资产类型，1代表”web资产“，2代表”非web资产“，3代表”全部“
        log.debug("Query URL: " + url);
        return url;
    }

    public JSONArray get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            // log.debug(jsonObject.toJSONString());
            return jsonObject.getJSONObject("data").getJSONArray("arr");
        } catch (IOException e) {
            log.debug(e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "FofaBot{" +
                ", secret='" + secret + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", search='" + search + '\'' +
                ", page='" + page + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    public static void main(String[] args) {
        HunterBot hunterBot = new HunterBot();
        JSONArray jsonArray = hunterBot.get(hunterBot.packageUrl("5Y2O6aG6"));
        log.debug(jsonArray.toJSONString());
    }
}
