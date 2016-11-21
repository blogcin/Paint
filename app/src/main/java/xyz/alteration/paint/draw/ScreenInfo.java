package xyz.alteration.paint.draw;

/**
 * Created by blogcin on 2016-11-21.
 */

public class ScreenInfo {
    private float screenX = 0.0f;
    private float screenY = 0.0f;

    public ScreenInfo(float x, float y) {
        this.screenX = x;
        this.screenY = y;
    }

    public synchronized float getScreenX() {
        return screenX;
    }

    public synchronized float getScreenY() {
        return screenY;
    }

}
