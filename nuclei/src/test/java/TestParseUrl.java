import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestParseUrl {

    @Test
    public void testParseUrl(){
        String line = "[2022-09-26 15:23:44] [CVE-2022-1388] [http] [critical] https://185.157.192.185/mgmt/tm/util/bash";
        String line1 = "[2022-09-26 15:23:46] [CVE-2022-1388] [http] [critical] https://217.73.57.100:8443/mgmt/tm/util/bash";

        System.out.println(urlRegex(line));
        System.out.println(urlRegex(line1));
    }

    private String urlRegex(String line) {
        // 按指定模式在字符串查找
        String pattern = "(http|https)://(www.)?(\\w+(\\.)?)+:*\\d+";
        String result = "";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        if (m.find( )) {
            result=m.group(0);
            System.out.println("Found value: " + m.group(0) );
        } else {
            System.out.println("NO MATCH");
        }
        return result;

    }
}
