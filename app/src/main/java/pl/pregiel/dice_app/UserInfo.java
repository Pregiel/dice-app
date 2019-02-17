package pl.pregiel.dice_app;


import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private static UserInfo userInfo;

    private String username;

    private List<Room> roomList = new ArrayList<>();

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

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }
}
