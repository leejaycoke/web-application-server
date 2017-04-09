package webserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JuHyunLee on 2017. 4. 8..
 */
public class HttpResponse {

    private int statusCode;

    private String path;

    private boolean isRedirect;

    private Map<String, String> headers = new HashMap<>();

    public HttpResponse(int statusCode, String path) {
        this.statusCode = statusCode;
        this.path = path;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRedirect() {
        return isRedirect;
    }

    public void setRedirect(boolean redirect) {
        isRedirect = redirect;
    }

    public void addCookie(String key, String value) {
        if (headers.containsKey("Set-Cookie")) {
            headers.put("Set-Cookie", headers.get("Set-Cookie") + "; " + key + "=" + value);
        } else {
            headers.put("Set-Cookie", key + "=" + value);
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
