package pl.pregiel.dice_app;


import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private static UserInfo userInfo;

    private String username;
    private String token;

//    private List<RoomDto> roomList = new ArrayList<>();

    public static synchronized UserInfo getInstance() {
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        return userInfo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    //    public List<RoomDto> getRoomList() {
//        return roomList;
//    }
//
//    public void setRoomList(List<RoomDto> roomList) {
//        this.roomList = roomList;
//    }
}
