package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler extends Thread {

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

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    private int port;

    public RequestHandler(Socket connectionSocket, int port) throws SocketException {
        this.connection = connectionSocket;
        connection.setSoTimeout(3000);
        this.port = port;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            StringBuilder rawHeader = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null && !line.equals("")) {
                rawHeader.append("\n").append(line);
                log.info("line={}", line);
            }

            HttpRequest httpRequest = new HttpRequest(rawHeader.toString());
            if (httpRequest.getMethod() == HttpRequest.Method.POST) {
                Long contentLength = Long.parseLong(httpRequest.getHeader("Content-Length"));

                StringBuilder data = new StringBuilder();
                for (Long count = 0L; count < contentLength; count++) {
                    data.append((char) br.read());
                }

                httpRequest.setRawData(data.toString());
            }

            DataOutputStream dos = new DataOutputStream(out);

            HttpResponse httpResponse = sendToContainer(httpRequest);
            if (httpResponse.isRedirect()) {
                responseToRedirect(dos, httpResponse.getPath(), httpResponse.getHeaders());
            } else {
                responseToPath(dos, "./webapp" + httpResponse.getPath(), httpResponse.getHeaders());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private HttpResponse sendToContainer(HttpRequest httpRequest) {
        HttpContainer httpContainer = new HttpContainer();
        String requestMethod = httpRequest.getMethod().name().toLowerCase();

        try {
            Method method = httpContainer.getClass().getMethod(requestMethod, httpRequest.getClass());
            log.info("try invoke method name={}", requestMethod);
            HttpResponse response = (HttpResponse) method.invoke(httpContainer, httpRequest);
            return response;
        } catch (Exception e) {
            log.error("invoke error={}", e.getMessage());
        }

        return null;
    }

    private void responseToRedirect(DataOutputStream dos, String location, Map<String, String> headers) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            for (String key : headers.keySet()) {
                dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
            }
            dos.writeBytes("Location: http://localhost:" + port + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseToPath(DataOutputStream dos, String path, Map<String, String> headers) throws IOException {
        File file = new File(path);
        log.info("file.exists={}", file.exists());
        byte[] body = Files.readAllBytes(file.toPath());

        String extension = getFileExtension(file.getName());
        responseHeader(dos, body.length, getContentType(extension), headers);
        responseBody(dos, body);
    }

    private void responseHeader(DataOutputStream dos, int lengthOfBodyContent, String contentType, Map<String, String> headers) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");

            for (String key : headers.keySet()) {
                dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        String[] args = filename.split("\\.");
        if (args.length > 0) {
            return "." + args[args.length - 1];
        }
        return null;
    }

    private String getContentType(String extension) {
        return CONTENT_TYPES.get(extension);
    }

}
