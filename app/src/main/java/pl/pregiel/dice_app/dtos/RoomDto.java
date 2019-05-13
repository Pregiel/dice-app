package pl.pregiel.dice_app.dtos;


import java.util.List;

public class RoomDto {
    private int id;
    private String title;
    private UserDto owner;
    private String clientAmount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public String getClientAmount() {
        return clientAmount;
    }

    public void setClientAmount(String clientAmount) {
        this.clientAmount = clientAmount;
    }
}
