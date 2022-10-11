import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TestEncoder {

    @Test
    public void testEncode() throws UnsupportedEncodingException {
        String url = "https://baidu.com+";
        String encodeUrl = URLEncoder.encode(url, "UTF-8");
        System.out.println(encodeUrl);

    }
}
