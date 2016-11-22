package xyz.alteration.paint;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import xyz.alteration.paint.draw.DrawType;
import xyz.alteration.paint.draw.DrawView;
import xyz.alteration.paint.draw.ScreenInfo;

public class DrawActivity extends AppCompatActivity {
    private DrawView drawView = null;

    private final String TAG = getClass().getSimpleName();

    private int drawSize = 0;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenInfo screenInfo = getDisplaySize();

        if (screenInfo != null) {
            drawView = new DrawView(getApplicationContext(), screenInfo, this);
            setContentView(drawView);
        }
    }

    private ScreenInfo getDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();

        if (display != null) {
            display.getSize(size);
        }

        return new ScreenInfo(size.x, size.y);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        String[] menuItems = {"브러쉬", "펜", "삼각형 그리기", "사각형 그리기", "지우개", "글자 입력", "저장", "불러오기", "크기 설정", "색 설정"};

        for (int i = 0; i < menuItems.length; i++) {
            menu.add(0, i, 0, menuItems[i]);
        }

        return true;
    }

    private void setColor() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);

        alert.setTitle("색 설정");
        alert.setCancelable(false);

        Button[] button = new Button[5];
        int index = 0;
        final int[] colors = new int[]{ Color.BLACK, Color.WHITE, Color.BLUE, Color.CYAN, Color.RED };

        for(Button b : button) {
            b  = new Button(DrawActivity.this);
            b.setBackgroundColor(colors[index]);

            final int finalIndex = index;

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    color = colors[finalIndex];
                }
            });

            index += 1;
            linearLayout.addView(b);
        }

        alert.setView(linearLayout);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                drawView.setColor(color);
                drawView.update();
            }
        });

        alert.show();
    }

    private void setSize() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final SeekBar seekBar = new SeekBar(getApplicationContext());
        final TextView textView = new TextView(getApplicationContext());

        alert.setTitle("크기 설정");
        alert.setCancelable(false);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText(Float.toString(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        textView.setGravity(Gravity.CENTER);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(seekBar);
        linearLayout.addView(textView);

        alert.setView(linearLayout);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                drawView.setSize((int)seekBar.getProgress());
                drawView.update();
            }
        });

        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int permissionCheck = 0;
        AlertDialog.Builder alert = null;

        if (drawView != null) {
            DrawType drawType = DrawType.getStatusFromInt(item.getItemId());

            if (drawType != null) {
                alert = new AlertDialog.Builder(DrawActivity.this);

                switch (drawType) {
                    case BRUSH:
                        drawView.setBrush();
                        break;
                    case PEN:
                        drawView.setPen();
                        break;
                    case TRIANGLE:
                        drawView.drawTriangle();
                        break;
                    case RECTANGLE:
                        drawView.drawRectangle();
                        break;
                    case ERASER:
                        drawView.setEraser();
                        break;
                    case TEXT:
                        drawView.drawText();
                        break;
                    case SAVE:
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(this, "파일저장을 하는데 권한을 얻는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            alert = new AlertDialog.Builder(this);
                            final EditText edittext = new EditText(getApplicationContext());
                            alert.setMessage("저장할 파일 이름 입력");
                            alert.setTitle("파일 저장");
                            alert.setCancelable(false);
                            alert.setView(edittext);

                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Editable fileName = edittext.getText();

                                    drawView.save(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName.toString());

                                }
                            });
                            alert.show();
                        }

                        break;
                    case LOAD:
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

                        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(this, "파일을 불러오는 권한을 얻는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            alert = new AlertDialog.Builder(this);
                            final EditText edittext = new EditText(getApplicationContext());
                            alert.setMessage("불러 올 파일 이름 입력");
                            alert.setTitle("파일 열기");
                            alert.setCancelable(false);
                            alert.setView(edittext);

                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Editable fileName = edittext.getText();

                                    drawView.load(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName.toString());

                                }
                            });
                            alert.show();
                        }
                        break;
                    case SIZE:
                        setSize();
                        break;
                    case COLOR:
                        setColor();
                        break;
                    default:
                        break;
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
