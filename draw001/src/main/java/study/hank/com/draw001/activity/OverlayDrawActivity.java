package study.hank.com.draw001.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import study.hank.com.draw001.R;
import study.hank.com.draw001.custom.DrawOverlayDemoView;

public class OverlayDrawActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay_draw);

        findViewById(R.id.v).setOnClickListener(v -> ((DrawOverlayDemoView) v).startAnimator());
    }
}
