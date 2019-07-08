package study.hank.com.draw001.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import study.hank.com.draw001.R;
import study.hank.com.draw001.custom.RoundImageView;

public class RoundImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_image_view);
        RoundImageView img = findViewById(R.id.img);

        findViewById(R.id.x_increase).setOnClickListener(v -> {
            img.reBitmapStartX += 100;
            img.refresh();
        });
        findViewById(R.id.x_reduce).setOnClickListener(v -> {
            img.reBitmapStartX -= 100;
            img.refresh();
        });
        findViewById(R.id.y_increase).setOnClickListener(v -> {
            img.reBitmapStartY += 100;
            img.refresh();
        });
        findViewById(R.id.y_reduce).setOnClickListener(v -> {
            img.reBitmapStartY -= 100;
            img.refresh();
        });

    }
}
