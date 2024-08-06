import com.g3g4x5x6.nuclei.http.ChatUtil;
import org.junit.jupiter.api.Test;

public class TestChatUtil {
    @Test
    public void testChatUtil() {
        String input = "{\"humanMessage\": \"\\n    # \\u6a21\\u677f\\u8981\\u6c42\\n\\n1. \\u201cpath\\u201d \\u5b57\\u6bb5\\u5fc5\\u987b\\u7531\\u6a21\\u677f\\u53d8\\u91cf \\u201cBaseURL\\u201d\\u62fc\\u63a5\\u8def\\u5f84\\n2. poc\\u6a21\\u677f\\u9664\\u5916\\uff0c\\u7981\\u6b62\\u56de\\u7b54\\u5176\\u4ed6\\u5185\\u5bb9\\n\\n# HTTP\\u6d41\\u91cf\\n\\n```HTTP-FLOW\\nGET / HTTP/1.1\\nHost: baidu.com\\nAccept-Language: zh-CN\\nUpgrade-Insecure-Requests: 1\\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.6478.127 Safari/537.36\\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\\nAccept-Encoding: gzip, deflate, br\\nConnection: keep-alive\\n\\n\\n\\nHTTP/1.1 302 Moved Temporarily\\nServer: bfe/1.0.8.18\\nDate: Sat, 13 Jul 2024 03:58:10 GMT\\nContent-Type: text/html\\nContent-Length: 161\\nConnection: Keep-Alive\\nLocation: https://www.baidu.com/\\nExpires: Sun, 14 Jul 2024 03:58:10 GMT\\nCache-Control: max-age=86400\\nCache-Control: privae\\n\\n<html>\\n<head><title>302 Found</title></head>\\n<body bgcolor=\\\"white\\\">\\n<center><h1>302 Found</h1></center>\\n<hr><center>bfe/1.0.8.18</center>\\n</body>\\n</html>\\n```\\n\\n\\n\\u8bf7\\u6839\\u636e \\u201c\\u6a21\\u677f\\u8981\\u6c42\\u201d \\u4ee5\\u53ca \\u201cHTTP\\u6d41\\u91cf\\u201d\\uff0c\\u7f16\\u5199\\u4e00\\u4e2apoc\\u6a21\\u677f\\u3002\\n    \"}\n";
        String resp = ChatUtil.chat(input);
        System.out.println(resp);
    }
}
