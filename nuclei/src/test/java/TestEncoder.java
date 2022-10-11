import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TestEncoder {

    @Test
    public void testEncode() {
        String url = "https://baidu.com?op=+";
        String encodeUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
        System.out.println(encodeUrl);

    }
}
