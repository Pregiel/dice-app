package pl.pregiel.dice_app;


import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private static UserInfo userInfo;

    private String username;

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

//    public List<RoomDto> getRoomList() {
//        return roomList;
//    }
//
//    public void setRoomList(List<RoomDto> roomList) {
//        this.roomList = roomList;
//    }
}
