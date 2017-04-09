package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by JuHyunLee on 2017. 4. 8..
 */
public class HttpContainer {

    private final static Logger log = LoggerFactory.getLogger(HttpContainer.class);

    public HttpResponse get(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        HttpResponse response = new HttpResponse(200, path);
        return response;
    }

    public HttpResponse post(HttpRequest httpRequest) {
        if (httpRequest.getPath().equals("/user/create")) {
            return createUser(httpRequest);
        } else if (httpRequest.getPath().equals("/user/login")) {
            return login(httpRequest);
        }

        return null;
    }

    private HttpResponse createUser(HttpRequest httpRequest) {
        String userId = httpRequest.getData("userId");
        String password = httpRequest.getData("password");
        String name = httpRequest.getData("name");
        String email = httpRequest.getData("email");
        User user = new User(userId, password, name, email);
        DataBase.addUser(user);

        HttpResponse response = new HttpResponse(302, "/index.html");
        response.setRedirect(true);
        return response;
    }

    private HttpResponse login(HttpRequest httpRequest) {
        String userId = httpRequest.getData("userId");
        String password = httpRequest.getData("password");

        User user = DataBase.findUserById(userId);
        if (user != null && user.getPassword().equals(password)) {
            HttpResponse response = new HttpResponse(200, "/index.html");
            response.setRedirect(true);
            response.addCookie("logined", "true");
            response.addCookie("domain", "localhost");
            response.addCookie("path", "/");
            return response;
        }

        return new HttpResponse(200, "/user/login_failed.html");
    }
}
