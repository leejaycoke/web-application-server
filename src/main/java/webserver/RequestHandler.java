package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String header = br.readLine();
            if (header == null) {
                return;
            }

            log.info("request = {}", header);
            String[] uris = header.split(" ");
            String path = uris[1];

            Map<String, String> params;

            if (path.contains("?")) {
                String[] args = path.split("\\?");
                path = args[0];
                if (args.length > 1) {
                    params = parseParameter(args[1]);
                }
            }

            log.info("path={}", path);

            File file = new File("./webapp" + path);
            byte[] body = Files.readAllBytes(file.toPath());

            while (!"".equals(header)) {
                log.info("header = {}", header);
                header = br.readLine();
            }

            if (path.equals("/user/create")) {
                log.info("good..");
            }

            DataOutputStream dos = new DataOutputStream(out);
            String contentType = getContentTypeByExtension(getFileExtension(file.getName()));
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void userCreate(Map<String, String> params) {
//        if (params)
    }

    private Map<String, String> parseParameter(String param) {
        Map<String, String> params = new HashMap<>();

        String[] valueSets = param.split("&");
        for (String valueSet : valueSets) {
            String[] args = valueSet.split("=");
            if (args.length > 1) {
                params.put(args[0], args[1]);
            } else {
                params.put(args[0], "");
            }
        }
        return params;
    }

    private String getFileExtension(String filename) {
        String[] args =  filename.split("\\.");
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
