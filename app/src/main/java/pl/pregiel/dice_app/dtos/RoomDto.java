package pl.pregiel.dice_app.dtos;


import java.util.List;

public class RoomDto {
    private int id;
    private String title;
    private String password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
