package webserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JuHyunLee on 2017. 4. 10..
 */
public class Cookie {

    private String domain;

    private String path;

    private Map<String, String> values = new HashMap<>();

    public Cookie(String domain, String path) {
        this.domain = domain;
        this.path = path;
    }

    public void addValue(String key, String value) {
        values.put(key, value);
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getValues() {
        return values;
    }
}
