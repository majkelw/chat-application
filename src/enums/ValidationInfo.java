package enums;

public enum ValidationInfo {
    SUCCESS(0),
    USER_EXISTS_IN_ROOM(1),
    ROOM_EXISTS(2),
    ROOM_DOES_NOT_EXIST(3);
    private final int value;

    ValidationInfo(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
