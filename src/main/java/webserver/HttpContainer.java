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
        HttpResponse response = new HttpResponse.Builder()
                .setPath(path)
                .build();

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

        return new HttpResponse.Builder()
                .setPath("/index.html")
                .setRedirect(true)
                .build();
    }

    private HttpResponse login(HttpRequest httpRequest) {
        String userId = httpRequest.getData("userId");
        String password = httpRequest.getData("password");

        User user = DataBase.findUserById(userId);
        if (user != null && user.getPassword().equals(password)) {
            Cookie cookie = new Cookie("localhost", "/");
            cookie.addValue("logined", "true");

            HttpResponse response = new HttpResponse.Builder()
                    .setPath("/index.html")
                    .setCookie(cookie)
                    .setRedirect(true)
                    .build();
            return response;
        }

        return new HttpResponse.Builder()
                .setRedirect(true)
                .setPath("/user/login_failed.html")
                .build();
    }
}
