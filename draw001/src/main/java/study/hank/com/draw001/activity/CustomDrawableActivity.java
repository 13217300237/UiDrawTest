package study.hank.com.draw001.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;
import study.hank.com.draw001.custom.MemoryClearDrawable;

public class CustomDrawableActivity extends AppCompatActivity {

    private float currentSpeed = 1f;
    private float perChange = 0.25f;//每次改变速率的幅度
    private boolean ifShowSupportLine = false;//是否显示辅助线

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_drawable);

        ImageView iv = findViewById(R.id.iv);
        MemoryClearDrawable memoryClearDrawable = new MemoryClearDrawable(this, Utils.dp2px(300), Utils.dp2px(400));
        iv.setImageDrawable(memoryClearDrawable);

        iv.setOnClickListener(v -> memoryClearDrawable.startAnimator());

        TextView currentSpeedTv = findViewById(R.id.current_speed);

        findViewById(R.id.speed_up).setOnClickListener(v -> {
            currentSpeed += perChange;
            currentSpeedTv.setText(currentSpeed + "");
            memoryClearDrawable.setAnimatorTotalSpeedFactor(currentSpeed);
        });
        findViewById(R.id.speed_down).setOnClickListener(v -> {
            if (currentSpeed <= perChange) {
                Toast.makeText(this, "速率不能再小了", Toast.LENGTH_SHORT).show();
                return;
            }
            currentSpeed -= perChange;
            currentSpeedTv.setText(currentSpeed + "");
            memoryClearDrawable.setAnimatorTotalSpeedFactor(currentSpeed);
        });

        TextView tvSupport = findViewById(R.id.tv_support_line);
        findViewById(R.id.show_support_line).setOnClickListener(v -> {
            ifShowSupportLine = !ifShowSupportLine;
            memoryClearDrawable.setShowSupportLine(ifShowSupportLine);
            tvSupport.setText(ifShowSupportLine ? "显示辅助线" : "不显示辅助线");
        });

        findViewById(R.id.start).setOnClickListener(v -> memoryClearDrawable.startAnimator());

    }
}
