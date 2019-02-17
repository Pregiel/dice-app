package pl.pregiel.dice_app;


import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class WebController {
    public static final String BASE_URL = "http://192.168.1.20:8080/",
            LOGIN_URL = BASE_URL + "%s/roomlist/",
            REGISTRATION_URL = BASE_URL + "registration/",
            ROOM_URL = BASE_URL + "/room/%s/";

    private static HttpEntity<String> request;

    public static void setupRequest(String username, String password) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setAuthorization(new HttpBasicAuthentication(username, password));

        request = new HttpEntity<>(httpHeaders);
    }

    public static HttpEntity<String> getRequest() {
        return request;
    }

}
