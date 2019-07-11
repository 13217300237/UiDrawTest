package study.hank.com.draw001.activity.children;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import study.hank.com.draw001.R;
import study.hank.com.draw001.custom.ScratchCardImageView;

public class ScratchCardActivity extends AppCompatActivity {

    ScratchCardImageView scratchCardImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_card);

        scratchCardImageView = findViewById(R.id.scView);
        findViewById(R.id.showAll).setOnClickListener(v -> scratchCardImageView.showAll());
        findViewById(R.id.reset).setOnClickListener(v -> scratchCardImageView.reset());
        TextView textView = findViewById(R.id.current);
        scratchCardImageView.setCallback(currentPercent -> textView.setText(Math.round(currentPercent * 100) + "%"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scratchCardImageView.recycle();
    }
}
