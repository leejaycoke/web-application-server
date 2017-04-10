package webserver;

/**
 * Created by JuHyunLee on 2017. 4. 10..
 */
public class Cookie {

    private String key;

    private String value;

    public Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
