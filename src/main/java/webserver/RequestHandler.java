package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder rawHeader = new StringBuilder();
            rawHeader.append(br.readLine());

            String headerLine;
            while (!(headerLine = br.readLine()).equals("")) {
                log.info("headerLine={}", headerLine);
                rawHeader.append("\n").append(headerLine);
            }

            HttpRequest httpRequest = new HttpRequest(rawHeader.toString());
            if (httpRequest.getMethod() == HttpRequest.Method.POST) {
                String data = readData(br);
                log.info("data={}", data);
                httpRequest.setRawData(data);
            }

            log.info("header={}", rawHeader.toString());

//            HttpResponse httpResponse = sendToContainer(httpRequest);

            File file = new File("./webapp" + httpRequest.getPath());
            byte[] body = Files.readAllBytes(file.toPath());

            DataOutputStream dos = new DataOutputStream(out);
            String contentType = getContentTypeByExtension(getFileExtension(file.getName()));
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String readData(BufferedReader br) throws IOException {
        StringBuilder data = new StringBuilder();
        data.append(br.readLine());

        String headerLine;
        while (!(headerLine = br.readLine()).equals("")) {
            data.append("\n").append(headerLine);
        }

        return data.toString();
    }

    private HttpResponse sendToContainer(HttpRequest httpRequest) {
        HttpContainer httpContainer = new HttpContainer();
        String requestMethod = httpRequest.getMethod().name().toLowerCase();

        try {
            Method method = httpContainer.getClass().getMethod(requestMethod, httpRequest.getClass());
            log.info("try invoke method name={}", requestMethod);
            HttpResponse httpResponse = (HttpResponse) method.invoke(httpContainer, httpRequest);
            return httpResponse;
        } catch (Exception e) {
            log.error("invoke error={}", e.getMessage());
        }

        return null;
    }

    private String getFileExtension(String filename) {
        String[] args = filename.split("\\.");
        if (args.length > 0) {
            return "." + args[args.length - 1];
        }
        return null;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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

    private static String getContentTypeByExtension(String extension) {
        Map<String, String> types = new HashMap<>();
        types.put(".jpeg", "image/jpeg");
        types.put(".jpg", "image/jpg");
        types.put(".html", "text/html; charset=UTF-8");
        types.put(".htm", "text/html; charset=UTF-8");
        types.put(".js", "application/javascript");
        types.put(".css", "text/css");
        types.put(".woff", "application/x-font-woff");

        String contentType = types.get(extension);
        if (contentType == null) {
            return types.get(".html");
        }
        return contentType;
    }
}
