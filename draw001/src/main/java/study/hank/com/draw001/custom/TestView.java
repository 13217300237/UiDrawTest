package study.hank.com.draw001.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;

public class TestView extends View {

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int c = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(widthSize, 500);
            Log.d("onMeasureTag", "第" + c++ + "次测量 - 之后的宽是：" + getMeasuredWidth() + "   /   高是：" + getMeasuredHeight());
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(getResources().getColor(R.color.aoyun_orange));

        Paint paint = new Paint();


        paint.setColor(getResources().getColor(R.color.aoyun_green));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(Utils.dp2px(50));

        canvas.drawText("测试文字", 100, 200, paint);

    }
}
