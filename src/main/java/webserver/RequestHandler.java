package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;

public class RequestHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    private int port;

    private final String viewFolder;

    public RequestHandler(Socket connectionSocket, int port, String viewFolder) throws SocketException {
        this.connection = connectionSocket;
        connection.setSoTimeout(5000);
        this.port = port;
        this.viewFolder = viewFolder;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = sendToContainer(httpRequest);
            if (httpResponse == null) {
                throw new RuntimeException("응답 생성도중 에러 발생");
            }

            DataOutputStream dos = new DataOutputStream(out);
            httpResponse.send(dos, viewFolder);
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

}
