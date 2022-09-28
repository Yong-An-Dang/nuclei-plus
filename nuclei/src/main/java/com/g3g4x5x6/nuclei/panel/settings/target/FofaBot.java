package com.g3g4x5x6.nuclei.panel.settings.target;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.LinkedList;


@Slf4j
public class FofaBot extends Fofa{
    private final OkHttpClient client = new OkHttpClient();
    private String url;
    private String email;
    private String secret;

    public FofaBot() {
        apiUrl = "https://fofa.info/api/v1/search/all";
        qbase64 = "";
        fields = "protocol,host,port,ip";
        page = "1";
        size = "100";
        full = String.valueOf(false);
    }

    public void packageUrl(){
        url = apiUrl +
                "?email=" + email +
                "&key=" + secret +
                "&qbase64=" + qbase64 +
                "&fields=" + fields +
                "&page=" + page +
                "&size=" + size +
                "&full=" + full;
        log.debug("Query URL: " + url);
    }

    public String run() {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public LinkedList<String> parserTargets(JSONArray jsonArray, int httpIndex, int host_index){
        LinkedList<String> hosts = new LinkedList<>();
        for (Object o : jsonArray) {
            JSONArray array = (JSONArray) o;
            String protocol = (String) array.get(httpIndex);
            String host = (String) array.get(host_index);
            if (host.startsWith("http") || host.startsWith("https")){
                hosts.add(host);
            } else {
                hosts.add(protocol + "://" + host);
            }
        }
        return hosts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
