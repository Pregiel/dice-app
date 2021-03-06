package pl.pregiel.dice_app.dtos;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RollDto {
    private int id;
    private int userId;
    private String username;
    private int roomId;
    private int modifier;
    private List<RollValueDto> rollValues;
    private String createdTime;

    public RollDto() {
        rollValues = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public List<RollValueDto> getRollValues() {
        return rollValues;
    }

    public void setRollValues(List<RollValueDto> rollValues) {
        this.rollValues = rollValues;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
