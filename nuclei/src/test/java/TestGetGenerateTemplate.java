import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestGetGenerateTemplate {

  @Test
  public void testGetGenerateTemplate() {
    String genTemplate = " ```yaml\r\n" + //
        "id: example-template\r\n" + //
        "info:\r\n" + //
        "  name: Example Vulnerability\r\n" + //
        "  author: YourName\r\n" + //
        "  severity: high\r\n" + //
        "  description: This is a template for detecting a potential vulnerability via HTTP flow analysis.\r\n" + //
        "requests:\r\n" + //
        "  - Verb: GET\r\n" + //
        "    Path: {{BaseURL}}/vulnerable/path\r\n" + //
        "    Headers:\r\n" + //
        "      - User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Firefox/102.0\r\n" + //
        "    Matchers:\r\n" + //
        "      - type: word\r\n" + //
        "        words:\r\n" + //
        "          - \"vulnerable response\"\r\n" + //
        "          - \"error\"\r\n" + //
        "        part: body\r\n" + //
        "      - type: status\r\n" + //
        "        status:\r\n" + //
        "          - 200\r\n" + //
        "```sssssss";

    String templateString = getGenerateTemplate(genTemplate);
    log.info(templateString);

  }

  private static String getGenerateTemplate(String genString) {
    log.info(genString);
    String templateString = "";

    Pattern pattern = Pattern.compile(".*?```yaml(.*?)```.*?", Pattern.DOTALL | Pattern.MULTILINE);
    Matcher matcher = pattern.matcher(genString);

    // 诊断是否匹配到字符串
    // log.info("Matcher: {}", matcher.matches());

    while (matcher.find()) {
      String yamlContent = matcher.group(1);
      System.out.println("YAML Content:\n" + yamlContent);
    }
    return templateString;
  }

  public static void main(String[] args) {
    String genTemplate = " ```yaml\r\n" + //
        "id: example-template\r\n" + //
        "info:\r\n" + //
        "  name: Example Vulnerability\r\n" + //
        "  author: YourName\r\n" + //
        "  severity: high\r\n" + //
        "  description: This is a template for detecting a potential vulnerability via HTTP flow analysis.\r\n" + //
        "requests:\r\n" + //
        "  - Verb: GET\r\n" + //
        "    Path: {{BaseURL}}/vulnerable/path\r\n" + //
        "    Headers:\r\n" + //
        "      - User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Firefox/102.0\r\n" + //
        "    Matchers:\r\n" + //
        "      - type: word\r\n" + //
        "        words:\r\n" + //
        "          - \"vulnerable response\"\r\n" + //
        "          - \"error\"\r\n" + //
        "        part: body\r\n" + //
        "      - type: status\r\n" + //
        "        status:\r\n" + //
        "          - 200\r\n" + //
        "```";

    String templateString = getGenerateTemplate(genTemplate);
    log.info(templateString);
  }
}