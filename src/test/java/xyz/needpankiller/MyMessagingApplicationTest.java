package xyz.needpankiller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@QuarkusTest
class MyMessagingApplicationTest {

    private static Logger logger = LoggerFactory.getLogger(MyMessagingApplicationTest.class);

    /*    @Inject
        MyMessagingApplication application;
    */
    @Test
    void test() {

        String value = "form-data; name=\"file.svg\"; filename=\"���� ���� ���̾�׷�?.drawio.svg\"; filename*=UTF-8''%EC%A0%9C%EB%AA%A9%20%EC%97%86%EB%8A%94%20%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8.drawio.svgform-data; name=\"file.svg\"; filename=\"���� ���� ���̾�׷�?.drawio.svg\"; filename*=UTF-8''%EC%A0%9C%EB%AA%A9%20%EC%97%86%EB%8A%94%20%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8.drawio.svg";
        String result = decodeFilename(value);
        logger.info("result : {}", result);

    }

    public static String decodeFilename(String headerString) {
        String decodedFilename = "";

        // filename*에서 SVG 파일 이름 추출을 위한 정규표현식 패턴
        Pattern utf8Pattern = Pattern.compile("filename\\*=UTF-8''([^;]*)");
        Matcher utf8matcher = utf8Pattern.matcher(headerString);

        // 매칭된 패턴이 있다면 추출
        if (utf8matcher.find()) {
            // 매칭된 부분을 가져와 디코딩
            String encodedFilename = utf8matcher.group(1);
            return URLDecoder.decode(encodedFilename, StandardCharsets.UTF_8);
        }


        Pattern filenamePattern = Pattern.compile("filename=\"[^\"]*\"");
        Matcher filenameMatcher = filenamePattern.matcher(headerString);

        // 매칭된 패턴이 있다면 추출
        if (filenameMatcher.find()) {
            // 매칭된 부분을 가져옴
            String matched = filenameMatcher.group();
            // filename 또는 filename* 중 적절한 부분 추출
            return matched.substring(matched.indexOf('"') + 1, matched.lastIndexOf('"'));
        }
        return "";
    }
}
