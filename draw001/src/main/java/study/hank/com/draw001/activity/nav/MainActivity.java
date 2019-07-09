package study.hank.com.draw001.activity.nav;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import study.hank.com.draw001.R;
import study.hank.com.draw001.activity.CustomDrawableActivity;
import study.hank.com.draw001.activity.DrawTextActivity;
import study.hank.com.draw001.activity.MatrixActivity;
import study.hank.com.draw001.activity.OlympicRingsActivity;
import study.hank.com.draw001.activity.OverlayDrawActivity;
import study.hank.com.draw001.activity.RoundImageViewActivity;
import study.hank.com.draw001.activity.ScrollViewInnerCustomViewActivity;
import study.hank.com.draw001.activity.ShaderActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator);

        findViewById(R.id.btn1).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, OlympicRingsActivity.class);
            startActivity(i);
        });
        findViewById(R.id.btn2).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, DrawTextActivity.class);
            startActivity(i);
        });

        findViewById(R.id.btn3).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ShaderActivity.class);
            startActivity(i);
        });

        findViewById(R.id.btn4).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ScrollViewInnerCustomViewActivity.class);
            startActivity(i);
        });

        findViewById(R.id.btn5).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, RoundImageViewActivity.class);
            startActivity(i);
        });

        findViewById(R.id.btn6).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, MatrixActivity.class);
            startActivity(i);
        });

        findViewById(R.id.btn7).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, OverlayDrawActivity.class);
            startActivity(i);

        });

        findViewById(R.id.btn8).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, CustomDrawableActivity.class);
            startActivity(i);
        });
    }
}
