package pl.pregiel.dice_app.web;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class WebController {
    public static final String BASE_URL = "http://192.168.1.20:45455/api/",
            USER_LIST_URL = BASE_URL + "users/",
            AUTHENTICATE_URL = USER_LIST_URL + "authenticate/",
            ROOM_LIST_URL = BASE_URL + "rooms/",
            ROOM_URL = ROOM_LIST_URL + "%s/";

    private static HttpEntity<String> httpEntity;

    public static void setupHttpEntity(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        httpEntity = new HttpEntity<>(httpHeaders);
    }

    public static HttpEntity<String> getHttpEntityWithoutAuth() {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        return httpEntity;
    }

    public static HttpEntity<String> getHttpEntity() {
        return httpEntity;
    }

    public static boolean checkResponseBody(String body, HttpResultMessage... messages) {
        for (HttpResultMessage message : messages) {
            if (!body.contains(message.toString())) {
                return false;
            }
        }
        return true;
    }
}
