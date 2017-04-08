package webserver;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JuHyunLee on 2017. 4. 8..
 */
public class HttpRequest {

    private final static Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private String rawHeader;

    private String path;

    private Method method;

    private Map<String, String> params = new HashMap<>();

    public HttpRequest(String rawHeader) {
        if (Strings.isNullOrEmpty(rawHeader)) {
            throw new RuntimeException("헤더가 없습니다.");
        }
        this.rawHeader = rawHeader.trim();
        parse();
    }

    private void parse() {
        String[] headers = rawHeader.split("\n")[0].split(" ");
        if (headers.length != 3) {
            throw new RuntimeException("알 수 없는 헤더입니다.");
        }

        parseMethod(headers[0]);
        parsePath(headers[1]);
        parseParams(headers[1]);
    }

    private void parseMethod(String stringMethod) {
        method = Method.valueOf(stringMethod);
    }

    private void parsePath(String stringPath) {
        String[] uris = stringPath.split("\\?");
        path = uris[0];
    }

    private void parseParams(String stringPath) {
        String[] identifies = stringPath.split("\\?", 2); // String[0] = "/user/asdf", String[1] = "a=b&c=d&e=f"
        if (identifies.length < 2) {
            return;
        }

        List<String> params = Arrays.asList(identifies[1].split("&"));
        params.forEach(this::parseParam);
    }

    private void parseParam(String paramSet) { // "a=b"
        log.info("paramSet={}", paramSet);
        String[] keyValue = paramSet.split("=", -2);
        params.put(keyValue[0], keyValue[1]);
    }

    public void setRawData(String rawData) {

    }

    Method getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    String getParam(String key) {
        return params.get(key);
    }

    public enum Method {
        GET, POST
    }
}
