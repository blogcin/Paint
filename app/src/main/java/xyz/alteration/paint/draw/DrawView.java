package xyz.alteration.paint.draw;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.fraction;
import static android.R.attr.width;
import static android.support.v7.appcompat.R.attr.height;

/**
 * Created by blogcin on 2016-11-20.
 */

public class DrawView extends View {
    private float coordinatesX = 0.0f;
    private float coordinatesY = 0.0f;

    private float screenX = 0.0f;
    private float screenY = 0.0f;

    private DrawType drawType;

    private Path path = null;

    private Paint paint = null;
    private Paint bitmapPaint = null;

    private Canvas canvas = null;
    private Bitmap bitmap = null;

    private final float TOUCH_TOLERANCE = 4;

    private final String TAG = getClass().getSimpleName();

    private Activity parentActivity = null;

    public DrawView(Context context, ScreenInfo screenInfo, Activity activity) {
        super(context);

        this.screenX = screenInfo.getScreenX();
        this.screenY = screenInfo.getScreenY();
        this.parentActivity = activity;

        bitmap = Bitmap.createBitmap((int)screenX, (int)screenY, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);
        canvas = new Canvas(bitmap);
        path = new Path();

        setPen();

        bitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setEraser() {
        paint = new Paint();
        paint.setStrokeWidth(16);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        drawType = DrawType.ERASER;
    }

    public void setPen() {
        paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        drawType = DrawType.PEN;
    }

    public void setBrush() {
        paint = new Paint();
        paint.setAlpha(0x80);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(Color.RED);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeWidth(30);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[] { 2, 2, 2, 2, 2 }, 0));
        drawType = DrawType.BRUSH;
    }

/*
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super(w, h, oldW, oldH);
    }
*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.drawPath(path, paint);
    }

    private void touchStart(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        coordinatesX = x;
        coordinatesY = y;
    }

    private void setFigure() {
        paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - coordinatesX);
        float dy = Math.abs(y - coordinatesY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

            if (drawType != null) {

                switch(drawType) {
                    case RECTANGLE:
                    case TRIANGLE:
                        break;
                    case PEN:
                    case BRUSH:
                    case ERASER:
                        // x1, y1 에서 x2, y2까지 곡선을 그림
                        path.quadTo(coordinatesX, coordinatesY, x, y);
                        coordinatesX = x;
                        coordinatesY = y;
                        break;
                }
            }

        }
    }

    private void touchUp(float x, float y) {

        final int xFinal = (int)x;
        final int yFinal = (int)y;

        switch(drawType) {
            case TEXT:
                AlertDialog.Builder alert = new AlertDialog.Builder(parentActivity);
                final EditText edittext = new EditText(parentActivity);
                alert.setMessage("입력할 문장 입력");
                alert.setTitle("문장 입력");
                alert.setCancelable(false);
                alert.setView(edittext);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable str = edittext.getText();

                        if (str != null) {
                            paint.setTextSize(100);
                            canvas.drawText(str.toString(), xFinal, yFinal, paint);
                            invalidate();
                        }
                    }
                });

                alert.show();
                // show dialog
            case RECTANGLE:
                canvas.drawRect(coordinatesX, coordinatesY, x, y, paint);
                break;
            case TRIANGLE:

                Point topVertex = new Point((int)coordinatesX, (int)coordinatesY);
                Point rightVertex = new Point((int)x, (int)y);
                // get LeftVertex

                int leftVertexX = (int)(coordinatesX - (x / 2));
                //int leftVertexY = (int)(coordinatesY - (y / 2));
                int leftVertexY = (int)y;

                if (leftVertexX < 0) {
                    leftVertexX = 0;
                }

                if (leftVertexY < 0) {
                    leftVertexY = 0;
                }

                Point leftVertex = new Point(leftVertexX, leftVertexY);

                path.setFillType(Path.FillType.EVEN_ODD);

                path.quadTo(topVertex.x, topVertex.y, rightVertex.x, rightVertex.y);
                path.quadTo(rightVertex.x, rightVertex.y, leftVertex.x, leftVertex.y);
                path.quadTo(leftVertex.x, leftVertex.y, topVertex.x, topVertex.y);

                canvas.drawPath(path, paint);

                break;
            case ERASER:
            case PEN:
            case BRUSH:
                path.lineTo(coordinatesX, coordinatesY);
                canvas.drawPath(path, paint);
                path.reset();
                break;
        }

    }

    public void drawTriangle() {
        drawType = DrawType.TRIANGLE;
        setFigure();
    }

    public void drawRectangle() {
        drawType = DrawType.RECTANGLE;
        setFigure();
    }


    public void load(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
            invalidate();
            Toast.makeText(getContext(), "파일 불러오기 성공", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "bitmap is null");
        }
    }

    public void drawText() {
        drawType = DrawType.TEXT;
        setFigure();
    }

    public void save(String filePath) {

        setDrawingCacheEnabled(true);
        Bitmap saveImage = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);

        File imageFile = new File(filePath);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imageFile);
            saveImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Toast.makeText(getContext(), "저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "저장 실패", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 터치한 이벤트의 X 좌표와 Y 좌표를 구함
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
                invalidate();
                break;
        }

        return true;

    }

}
