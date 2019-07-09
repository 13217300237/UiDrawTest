package study.hank.com.draw001.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;
import study.hank.com.draw001.custom.MemoryClearDrawable;

public class CustomDrawableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_drawable);

        ImageView iv = findViewById(R.id.iv);
        MemoryClearDrawable memoryClearDrawable = new MemoryClearDrawable(this, Utils.dp2px(300), Utils.dp2px(400));
        iv.setImageDrawable(memoryClearDrawable);

        iv.setOnClickListener(v -> memoryClearDrawable.startAnimator());

    }
}
