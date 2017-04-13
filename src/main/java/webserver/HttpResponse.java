package webserver;

import com.google.common.io.Files;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JuHyunLee on 2017. 4. 8..
 */
public class HttpResponse {

    private final static Map<String, String> CONTENT_TYPES = new HashMap<>();

    static {
        CONTENT_TYPES.put(".jpeg", "image/jpeg");
        CONTENT_TYPES.put(".jpg", "image/jpg");
        CONTENT_TYPES.put(".html", "text/html; charset=UTF-8");
        CONTENT_TYPES.put(".htm", "text/html; charset=UTF-8");
        CONTENT_TYPES.put(".js", "application/javascript");
        CONTENT_TYPES.put(".css", "text/css");
        CONTENT_TYPES.put(".woff", "application/x-font-woff");
    }

    private final Builder builder;

    public HttpResponse(Builder builder) {
        this.builder = builder;
    }

    public void send(DataOutputStream dos, String viewFolder) throws IOException {
        createHeader(dos);

        if (!builder.isRedirect()) {
            createBody(dos, viewFolder);
        }
        dos.flush();
    }

    public void createHeader(DataOutputStream dos) throws IOException {
        dos.writeBytes("HTTP/1.1 " + builder.getStatusCode() + " OK \r\n");

        if (builder.hasCookie()) {
            writeCookie(dos);
        }

        if (builder.isRedirect()) {
            dos.writeBytes("Location: " + builder.getPath() + "\r\n");
        }

        String contentType = getContentType();
        if (contentType != null) {
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
        }

        dos.writeBytes("\r\n");
    }

    private void createBody(DataOutputStream dos, String viewFolder) throws IOException {
        File file = new File(viewFolder + builder.getPath());
        if (!file.exists()) {
            return;
        }

        byte[] body = Files.toByteArray(file);
        dos.write(body, 0, body.length);
    }

    private void writeCookie(DataOutputStream dos) throws IOException {
        Cookie cookie = builder.getCookie();
        if (cookie == null) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        cookie.getValues().forEach((key, value) -> {
            stringBuilder.append(key).append("=").append(value).append("; ");
        });

        stringBuilder.append("Domain=").append(cookie.getDomain())
                .append("; ")
                .append("Path=").append(cookie.getPath());

        dos.writeBytes("Set-Cookie: " + stringBuilder.toString() + "\r\n");
    }

    private String getContentType() {
        if (builder.getPath() == null) {
            return null;
        }

        String[] uris = builder.getPath().split("\\.");
        return CONTENT_TYPES.get("." + uris[uris.length - 1]);
    }

    public static class Builder {

        private boolean isRedirect = false;

        private String path = null;

        private Cookie cookie = null;

        private Map<String, String> headers = new HashMap<>();

        private int statusCode = 200;

        public Builder() {

        }

        public boolean isRedirect() {
            return isRedirect;
        }

        public Builder(int statusCode) {
            this.statusCode = statusCode;
        }

        public Builder setRedirect(boolean redirect) {
            isRedirect = redirect;
            if (redirect) {
                this.statusCode = 302;
            } else {
                this.statusCode = 200;
            }
            return this;
        }

        public String getPath() {
            return path;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Cookie getCookie() {
            return cookie;
        }

        public Builder setCookie(Cookie cookie) {
            this.cookie = cookie;
            return this;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public boolean hasCookie() {
            return cookie != null;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
