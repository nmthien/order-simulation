package challenge.cloudkitchen;

public class Constants {
    public static final int SHELF_DECAY_MODIFIER_SINGLE_TEMPERATURE = 1;
    public static final int SHELF_DECAY_MODIFIER_OVERFLOW = 2;
    public static final int OVERFLOW_SHELF_CAPACITY = 15;
    public static final int SINGLE_TEMPERATURE_SHELF_CAPACITY = 10;

    enum ShelfType {
        HOT,
        COLD,
        FROZEN,
        OVERFLOW
    }

    enum EnShelfResponse {
        COMPLETED,
        FAIL_SHELF_FULL
    }
}