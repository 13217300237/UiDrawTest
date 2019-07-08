package study.hank.com.draw001.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;

/**
 * 如何在绘制文字的时候 居中？ 到底有什么梗？！
 */
public class TextDrawView extends View {
    public TextDrawView(Context context) {
        this(context, null);
    }

    public TextDrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    Paint mPaint, mPaintHelper;
    private String textStr = "fgab"; //英文居中貌似有问题！
    private String textStr2 = "aaaaaaaaa";

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.aoyun_1));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(Utils.dp2px(30));
        mPaint.setTextAlign(Paint.Align.CENTER);

        mPaintHelper = new Paint();
        mPaintHelper.setColor(getResources().getColor(R.color.aoyun_4));
        mPaintHelper.setStrokeWidth(Utils.dp2px(1));
        mPaintHelper.setStyle(Paint.Style.STROKE);
        mPaintHelper.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int centerX = Utils.dp2px(200);
        int centerY = Utils.dp2px(200);
        int radius = Utils.dp2px(100);

        canvas.drawCircle(centerX, centerY, radius, mPaintHelper);
        canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, mPaintHelper);// 然后是辅助线 横向
        canvas.drawLine(centerX, centerY - radius, centerX, centerY + radius, mPaintHelper);// 然后是辅助线 纵向


        //卧槽？没居中,有一部分还在上面，看来这个和drawText的内部实现有关系
        Rect rect = new Rect();
        mPaint.getTextBounds(textStr, 0, textStr.length(), rect);//这样就能获得它的绘制区域矩形
        //拿到矩形之后，计算出偏移量
        float yOffset = (rect.top + rect.bottom) / 2;
        Log.d("yOffset", "" + rect.top + "/" + rect.bottom);
        canvas.drawText(textStr, centerX, centerY - yOffset, mPaint); //这样，就基本居中了,但是其实 字母a并没有居中

        canvas.translate(Utils.dp2px(300), 0);
        canvas.drawCircle(centerX, centerY, radius, mPaintHelper);
        canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, mPaintHelper);// 然后是辅助线 横向
        canvas.drawLine(centerX, centerY - radius, centerX, centerY + radius, mPaintHelper);// 然后是辅助线 纵向

        //另一种 获取文本矩形的方式
        Paint.FontMetrics metrics = new Paint.FontMetrics();
        mPaint.getFontMetrics(metrics);
        float yOffset2 = (metrics.ascent + metrics.descent) / 2;
        canvas.drawText(textStr2, centerX, centerY - yOffset2, mPaint); //这样，就基本居中了,但是其实 字母a并没有居中


        //沿着任意路径
        Path bSplinePath = new Path();
        bSplinePath.moveTo(Utils.dp2px(5), Utils.dp2px(320));
        bSplinePath.cubicTo(Utils.dp2px(80), Utils.dp2px(260),
                Utils.dp2px(200), Utils.dp2px(480),
                Utils.dp2px(350), Utils.dp2px(350));// 三阶贝塞尔曲线
        mPaint.setStyle(Paint.Style.STROKE);
        // 先画出这两个路径
        canvas.drawPath(bSplinePath, mPaint);
        // 依据路径写出文字
        String text = "风萧萧兮易水寒，壮士一去兮不复返";
        mPaint.setColor(Color.GRAY);
        mPaint.setTextScaleX(1.0f);
        mPaint.setTextSize(Utils.dp2px(20));
        canvas.drawTextOnPath(text, bSplinePath, 0, 15, mPaint);//这个方法，可以

        // 那么我能不能做出一个动态效果,波动效果，算了。看看今天的作业是什么。

    }
}
