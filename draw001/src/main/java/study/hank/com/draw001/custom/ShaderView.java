package study.hank.com.draw001.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;

/**
 * 试试画笔颜色渐变
 */
public class ShaderView extends View {
    public ShaderView(Context context) {
        this(context, null);
    }

    public ShaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private Paint mPaint;

    private void init() {
        if (mPaint == null)
            mPaint = new Paint();
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.aoyun_green));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(Utils.dp2px(10));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();
        // 给画笔加上渐变，然后再画一条线

        canvas.translate(100, 100);

//        drawLine(canvas);
//        drawGradientCircle(canvas);
        drawGradientArc(canvas);
//
//        //可是如果要画一个 环形渐变呢？
//        canvas.restore();
//        canvas.restore();
//        canvas.restore();
//
//        Matrix matrix = new Matrix();
//        canvas.setMatrix(matrix);
//
//        canvas.translate(-Utils.dp2px(100), Utils.dp2px(150));
//
//        RadialGradient radialGradient = new RadialGradient(
//                centerX, centerY, radius,
//                Color.BLACK, Color.WHITE
//                , Shader.TileMode.CLAMP);
//
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setShader(radialGradient);
//        canvas.drawCircle(centerX, centerY, radius, mPaint);


    }

    private void drawGradientArc(Canvas canvas) {
        canvas.translate(Utils.dp2px(200), 0);
        int[] SWEEP_GRADIENT_COLORS = new int[]{Color.BLACK, Color.WHITE};
        SweepGradient mColorShader = new SweepGradient(centerX, centerY, SWEEP_GRADIENT_COLORS, null);
        mPaint.setShader(mColorShader);

        RectF rectF = new RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rectF, 0, 270, false, mPaint);
        canvas.save();
    }

    final int centerX = Utils.dp2px(150);
    final int centerY = Utils.dp2px(50);
    final int radius = Utils.dp2px(50);

    private void drawGradientCircle(Canvas canvas) {
        canvas.translate(100, 0);

        int[] SWEEP_GRADIENT_COLORS = new int[]{Color.BLACK, Color.WHITE};
        SweepGradient mColorShader = new SweepGradient(centerX, centerY, SWEEP_GRADIENT_COLORS, null);
        mPaint.setShader(mColorShader);

        canvas.drawCircle(centerX, centerY, radius, mPaint);
        canvas.save();
    }

    private void drawLine(Canvas canvas) {
        //如果确定要画一条渐变色的线，那么就先确定起点和终点XY
        final int startX = 0;
        final int startY = 0;
        final int endX = Utils.dp2px(140);
        final int endY = Utils.dp2px(0);

        //原来你是控制了渐变的方向
        //这是线性渐变，从点到点
        Shader mShader = new LinearGradient(
                startX, startY, endX, endY,//这4个参数，控制渐变的方向
                new int[]{Color.RED, Color.WHITE}, null,
                Shader.TileMode.CLAMP);

        mPaint.setShader(mShader);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(startX, startY, endX, endY, mPaint);
        canvas.save();
    }
}
