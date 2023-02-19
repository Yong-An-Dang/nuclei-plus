package com.g3g4x5x6.nuclei.panel.search.fofa;

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
import java.util.LinkedList;


@Slf4j
public class FofaBot extends FofaVo {
    private final OkHttpClient client = new OkHttpClient();
    private String email;
    private String secret;

    public FofaBot() {
        if (NucleiConfig.getProperty("nuclei.fofa.api") == null || NucleiConfig.getProperty("nuclei.fofa.api").strip().equals("")) {
            apiUrl = "https://fofa.info/api/v1/search/all";
        } else {
            apiUrl = NucleiConfig.getProperty("nuclei.fofa.api");
        }
        if (NucleiConfig.getProperty("nuclei.fofa.email") == null || NucleiConfig.getProperty("nuclei.fofa.email").strip().equals("")) {
            DialogUtil.warn("请配置有效邮箱");
        } else {
            email = NucleiConfig.getProperty("nuclei.fofa.email");
        }
        if (NucleiConfig.getProperty("nuclei.fofa.secret") == null || NucleiConfig.getProperty("nuclei.fofa.secret").strip().equals("")) {
            DialogUtil.warn("请配置有效密钥");
        } else {
            secret = NucleiConfig.getProperty("nuclei.fofa.secret");
        }

        qbase64 = "5Y2O6aG65L%2Bh5a6J";
        fields = "ip,host,port,title,domain,icp,city";
        page = "1";
        size = "1000";
        full = String.valueOf(false);
    }

    public String packageUrl(String qbase64) {
        String url = apiUrl +
                "?email=" + email +
                "&key=" + secret +
                "&qbase64=" + qbase64 +
                "&fields=" + fields +
                "&page=" + page +
                "&size=" + size +
                "&full=" + full;
        log.debug("Query URL: " + url);
        return url;
    }

    public JSONArray get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            return jsonObject.getJSONArray("results");
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public String toString() {
        return "FofaBot{" +
                ", email='" + email + '\'' +
                ", secret='" + secret + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", qbase64='" + qbase64 + '\'' +
                ", fields='" + fields + '\'' +
                ", page='" + page + '\'' +
                ", size='" + size + '\'' +
                ", full='" + full + '\'' +
                '}';
    }

    public static void main(String[] args) throws IOException {
        FofaBot fofaBot = new FofaBot();
        JSONArray jsonArray = fofaBot.get(fofaBot.packageUrl("5Y2O6aG65L%2Bh5a6J"));
        log.debug(jsonArray.toJSONString());
    }
}
