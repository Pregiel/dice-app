package pl.pregiel.dice_app.web;


public enum HttpResultMessage {
    CredentialsInvalid, RoomNotFound, PasswordNull;

    @Override
    public String toString() {
        switch (this) {
            case CredentialsInvalid:
                return "credentials.invalid";
            case RoomNotFound:
                return "room.notFound";
            case PasswordNull:
                return "password.null";
        }
        return "";
    }
}
