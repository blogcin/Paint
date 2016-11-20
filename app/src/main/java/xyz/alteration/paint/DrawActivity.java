package xyz.alteration.paint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import xyz.alteration.paint.draw.DrawView;

public class DrawActivity extends AppCompatActivity {
    private DrawView drawView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawView = new DrawView(getApplicationContext());
        setContentView(drawView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "브러쉬");
        menu.add(0, 2, 0, "삼각형 그리기");
        menu.add(0, 3, 0, "사각형 그리기");
        menu.add(0, 4, 0, "저장");
        return true;
    }
}
