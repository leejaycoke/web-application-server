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

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> params = new HashMap<>();

    private Map<String, String> data = new HashMap<>();

    public HttpRequest(String rawHeader) {
        if (Strings.isNullOrEmpty(rawHeader)) {
            throw new RuntimeException("헤더가 없습니다.");
        }
        this.rawHeader = rawHeader.trim();
        parse();
    }

    private void parse() {
        List<String> headers = Arrays.asList(rawHeader.split("\n"));

        List<String> requests = Arrays.asList(headers.get(0).split(" "));
        if (requests.size() != 3) {
            throw new RuntimeException("알 수 없는 요청입니다.");
        }

        parseMethod(requests.get(0));
        parsePath(requests.get(1));
        parseParams(requests.get(2));

        if (headers.size() > 1) {
            parseHeaders(headers.subList(1, headers.size()));
        }
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
        params.forEach(param -> {
            KeyValue keyValue = parseKeyValue(param, "=");
            this.params.put(keyValue.getKey(), keyValue.getValue());
        });
    }

    private KeyValue parseKeyValue(String paramSet, String delimiter) { // "a=b"
        String[] keyValue = paramSet.split(delimiter, -2);

        if (keyValue.length < 2) {
            return new KeyValue(keyValue[0], "");
        }
        return new KeyValue(keyValue[0], keyValue[1]);
    }

    private void parseHeaders(List<String> headers) {
        headers.forEach(header -> {
            KeyValue keyValue = parseKeyValue(header, ": ");
            this.headers.put(keyValue.getKey(), keyValue.getValue());
        });
    }

    void setRawData(String rawData) {
        parseRawData(rawData);
    }

    private void parseRawData(String rawData) {
        List<String> data = Arrays.asList(rawData.split("&"));
        data.forEach(rawKeyValue -> {
            KeyValue keyValue = parseKeyValue(rawKeyValue, "=");
            this.data.put(keyValue.getKey(), keyValue.getValue());
        });
    }

    public String getHeader(String key) {
        return headers.get(key);
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

    String getData(String key) {
        return data.get(key);
    }

    public enum Method {
        GET, POST
    }

    private class KeyValue {

        private String key;

        private String value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
