package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

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

    public HttpRequest(InputStream in) throws IOException {
        parse(new BufferedReader(new InputStreamReader(in, "UTF-8")));
    }

    private void parse(BufferedReader br) throws IOException {
        List<String> lines = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null && !line.equals("")) {
            lines.add(line);
        }

        parseRequest(lines.get(0));
        parseHeaders(lines.subList(1, lines.size() - 1));

        Integer contentLength = getContentLength();
        if (getMethod() == Method.POST && contentLength > 0) {
            char[] body = new char[contentLength];
            br.read(body, 0, contentLength);
            parseBody(new String(body));
        }

    }

    private Integer getContentLength() {
        String contentLength = getHeader("Content-Length");
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    private void parseRequest(String requestLine) {
        String[] requestLines = requestLine.split(" ");
        if (requestLines.length != 3) {
            return;
        }

        parseMethod(requestLines[0]);
        parsePath(requestLines[1]);
    }

    private void parseMethod(String requestLine) {
        String[] requestLines = requestLine.split(" ");
        method = Method.valueOf(requestLines[0]);
    }

    private void parsePath(String stringPath) {
        String[] uris = stringPath.split("\\?", 2);
        path = uris[0];

        if (uris.length == 2) {
            parseParams(uris[1]);
        }
    }

    private void parseParams(String rawParams) {
        List<String> params = Arrays.asList(rawParams.split("&"));
        params.forEach(param -> {
            KeyValue keyValue = parseKeyValue(param, "=");
            this.params.put(keyValue.getKey(), keyValue.getValue());
        });
    }

    private void parseHeaders(List<String> headers) {
        headers.forEach(header -> {
            KeyValue keyValue = parseKeyValue(header, ": ");
            this.headers.put(keyValue.getKey(), keyValue.getValue());
        });
    }

    private void parseBody(String body) {
        List<String> data = Arrays.asList(body.split("&"));
        data.forEach(rawKeyValue -> {
            KeyValue keyValue = parseKeyValue(rawKeyValue, "=");
            this.data.put(keyValue.getKey(), keyValue.getValue());
        });
    }

    private KeyValue parseKeyValue(String paramSet, String delimiter) { // "a=b"
        String[] keyValue = paramSet.split(delimiter, -2);

        if (keyValue.length < 2) {
            return new KeyValue(keyValue[0], "");
        }
        return new KeyValue(keyValue[0], keyValue[1]);
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
