package xyz.alteration.paint.draw;

/**
 * Created by blogcin on 2016-11-21.
 */

public enum DrawType {
    NONE(-1),
    BRUSH(0),
    PEN(1),
    TRIANGLE(2),
    RECTANGLE(3),
    ERASER(4),
    TEXT(5),
    SAVE(6),
    LOAD(7),
    SIZE(8),
    COLOR(9);

    private int value;

    DrawType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static DrawType getStatusFromInt(int status) {
        switch(status) {
            case -1:
                return NONE;
            case 0:
                return BRUSH;
            case 1:
                return PEN;
            case 2:
                return TRIANGLE;
            case 3:
                return RECTANGLE;
            case 4:
                return ERASER;
            case 5:
                return TEXT;
            case 6:
                return SAVE;
            case 7:
                return LOAD;
            case 8:
                return SIZE;
            case 9:
                return COLOR;
        }

        return null;
    }
}
