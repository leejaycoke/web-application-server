package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JuHyunLee on 2017. 4. 8..
 */
public class HttpResponse {

    private Builder builder;

    public HttpResponse(Builder builder) {
        this.builder = builder;
    }

    public void createHeader(DataOutputStream dos) throws IOException {
        dos.writeBytes("HTTP/1.1 " + builder.getStatusCode() + " OK \r\n");
        writeCookieHeader(dos);
        for (String key : headers.keySet()) {
            dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
        }
        dos.writeBytes("Location: http://localhost:" + port + location + "\r\n");
        dos.writeBytes("\r\n");
    }

    public void createBody(DataOutputStream dos) {

    }

    private void writeCookieHeader(DataOutputStream dos) {
        builder.getCookies().forEach(cookie -> {
            try {
                dos.writeBytes("Set-Cookie: " + cookie.getKey() + "=" + cookie.getValue() + "\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static class Builder {

        private boolean isRedirect = false;

        private String path = null;

        private List<Cookie> cookies = new ArrayList<>();

        private Map<String, String> headers = new HashMap<>();

        private int statusCode = 200;

        public boolean isRedirect() {
            return isRedirect;
        }

        public Builder setRedirect(boolean redirect) {
            isRedirect = redirect;
            return this;
        }

        public String getPath() {
            return path;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public List<Cookie> getCookies() {
            return cookies;
        }

        public Builder setCookies(List<Cookie> cookies) {
            this.cookies = cookies;
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

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
