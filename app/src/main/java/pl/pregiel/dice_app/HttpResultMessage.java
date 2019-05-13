package pl.pregiel.dice_app;


public enum HttpResultMessage {
    CredentialsInvalid, RoomNotFound;

    @Override
    public String toString() {
        switch (this) {
            case CredentialsInvalid:
                return "credentials.invalid";
            case RoomNotFound:
                return "room.notFound";
        }
        return "";
    }
}
