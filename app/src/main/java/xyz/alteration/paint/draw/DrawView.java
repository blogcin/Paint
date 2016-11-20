package xyz.alteration.paint.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by blogcin on 2016-11-20.
 */

public class DrawView extends View {
    public DrawView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void touchStart(float x, float y) {

    }

    private void touchMove(float x, float y) {

    }

    private void touchUp() {

    }

    public void toJpeg(String path) {

    }

    public void toPng(String path) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }

}
