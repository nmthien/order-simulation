package challenge.cloudkitchen;

public class Constants {
    public static final int SHELF_DECAY_MODIFIER_SINGLE_TEMPERATURE = 1;
    public static final int SHELF_DECAY_MODIFIER_OVERFLOW = 2;
    public static final int OVERFLOW_SHELF_CAPACITY = 15;
    public static final int SINGLE_TEMPERATURE_SHELF_CAPACITY = 10;
    public static final int MIN_TIME_PICK_UP = 2;
    public static final int MAX_TIME_PICK_UP = 6;

    enum OrderTemperature {
        HOT,
        COLD,
        FROZEN
    }

    enum ShelfType {
        HOT,
        COLD,
        FROZEN,
        OVERFLOW
    }
}