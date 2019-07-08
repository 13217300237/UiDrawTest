package study.hank.com.draw001.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;

/**
 * 心形波浪
 */
public class DrawOverlayDemoView extends View {
    public DrawOverlayDemoView(Context context) {
        this(context, null);
    }

    public DrawOverlayDemoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawOverlayDemoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mTopPaint, mBottomPaint;
    private Paint textBottomPaint, textTopPaint;

    private Path mMainPath;//主绘制区域，心形
    private Rect mainRect;//主绘制区域的矩形范围
    private String text = "一条大灰狼";
    private Path bottomWavePath, topWavePath;// 上下区域两块贝塞尔封闭区域

    private float curXOffset = 0;
    private float growProcess = 0;//纵向增长的进度值
    private float waveProcess = 0;//

    private float animatorSpeedCoefficient = 1f;
    RectF mHeartRect;//心形的矩形区域

    private void init() {
        mTopPaint = new Paint();
        mTopPaint.setAntiAlias(true);
        mTopPaint.setStyle(Paint.Style.FILL);
        mTopPaint.setColor(getResources().getColor(R.color.aoyun_2));

        mBottomPaint = new Paint();
        mBottomPaint.setAntiAlias(true);
        mBottomPaint.setStyle(Paint.Style.FILL);
        mBottomPaint.setColor(getResources().getColor(R.color.aoyun_4));

        mHeartRect = new RectF();

        textBottomPaint = new TextPaint();
        textBottomPaint.setTextAlign(Paint.Align.CENTER);
        textBottomPaint.setTextSize(Utils.dp2px(30));
        textBottomPaint.setColor(getResources().getColor(R.color.colorC));

        textTopPaint = new TextPaint();
        textTopPaint.setTextAlign(Paint.Align.CENTER);
        textTopPaint.setTextSize(Utils.dp2px(30));
        textTopPaint.setColor(getResources().getColor(R.color.colorF));

        mMainPath = new Path();
        bottomWavePath = new Path();
        topWavePath = new Path();
    }

    /**
     * @param ifTop   是否是上部分; 上下部分的封口位置不一样
     * @param r       心形的矩形区域
     * @param process 当前进度值
     */
    private void resetWavePath(boolean ifTop, RectF r, float process, Path path) {
        final float width = r.width();
        final float height = r.width();

        path.reset();

        if (ifTop) {
            path.moveTo(r.left - width, r.top);
        } else {
            path.moveTo(r.left - width, r.bottom); //下部，初始位置点在 下
        }

        float waveHeight = height / 8f;//波动的最大幅度

        //找到矩形区域的左边线中点
        path.lineTo(r.left - width, r.bottom - height * process);

        //做两个周期的贝塞尔曲线
        for (int i = 0; i < 2; i++) {
            float px1, py1, px2, py2, px3, py3;

            px1 = width / 4;
            py1 = -waveHeight;

            px2 = width / 4 * 3;
            py2 = waveHeight;

            px3 = width;
            py3 = 0;

            path.rCubicTo(px1, py1, px2, py2, px3, py3);
        }
        if (ifTop) {
            path.lineTo(r.right, r.top);
        } else {
            path.lineTo(r.right, r.bottom);
        }
        path.close();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initHeartPath(mMainPath);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();
        canvas.translate(width / 2, height / 2);
        curXOffset = waveProcess * mHeartRect.width();//当前X轴方向上 波浪偏移量

        canvas.clipPath(mMainPath);
        canvas.save();

        mainRect = new Rect();
        textBottomPaint.getTextBounds(text, 0, text.length(), mainRect);

        resetWavePath(true, mHeartRect, growProcess, topWavePath);
        canvas.translate(curXOffset, 0);
        canvas.clipPath(topWavePath);
        canvas.drawPath(topWavePath, mTopPaint);
        canvas.drawText(text, -curXOffset, -(mainRect.top + mainRect.bottom) / 2, textTopPaint);

        resetWavePath(false, mHeartRect, growProcess, bottomWavePath);
        canvas.restore();
        canvas.translate(curXOffset, 0);
        canvas.clipPath(bottomWavePath);
        canvas.drawPath(bottomWavePath, mBottomPaint);
        canvas.drawText(text, -curXOffset, -(mainRect.top + mainRect.bottom) / 2, textBottomPaint); //再覆盖一次文字

    }

    AnimatorSet animatorSet;

    // 动起来
    public void startAnimator() {

        if (animatorSet == null) {
            animatorSet = new AnimatorSet();
            ValueAnimator growAnimator = ValueAnimator.ofFloat(0f, 1f);
            growAnimator.addUpdateListener(animation -> growProcess = (float) animation.getAnimatedValue());
            growAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animatorSet.cancel();
                }
            });
            growAnimator.setInterpolator(new DecelerateInterpolator());
            growAnimator.setDuration((long) (4000 / animatorSpeedCoefficient));

            ValueAnimator waveAnimator = ValueAnimator.ofFloat(0f, 1f);
            waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
            waveAnimator.setRepeatMode(ValueAnimator.RESTART);
            waveAnimator.addUpdateListener(animation -> {
                waveProcess = (float) animation.getAnimatedValue();
                invalidate();
            });
            waveAnimator.setInterpolator(new LinearInterpolator());
            waveAnimator.setDuration((long) (1000 / animatorSpeedCoefficient));

            animatorSet.playTogether(growAnimator, waveAnimator);
            animatorSet.start();
        } else {
            animatorSet.cancel();
            animatorSet.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }

    //那么文字呢?是怎么出现上半截下半截颜色不相同的情况,  原来只是单纯的多次绘制覆盖效果.

    /**
     * 构建心形
     * <p>
     * 注意，它这个是以 矩形区域中心点为基准的图形，所以绘制的时候，必须先把坐标轴移动到 区域中心
     */
    private void initHeartPath(Path path) {
        List<PointF> pointList = new ArrayList<>();
        pointList.add(new PointF(0, Utils.dp2px(-38)));
        pointList.add(new PointF(Utils.dp2px(50), Utils.dp2px(-103)));
        pointList.add(new PointF(Utils.dp2px(112), Utils.dp2px(-61)));
        pointList.add(new PointF(Utils.dp2px(112), Utils.dp2px(-12)));
        pointList.add(new PointF(Utils.dp2px(112), Utils.dp2px(37)));
        pointList.add(new PointF(Utils.dp2px(51), Utils.dp2px(90)));
        pointList.add(new PointF(0, Utils.dp2px(129)));
        pointList.add(new PointF(Utils.dp2px(-51), Utils.dp2px(90)));
        pointList.add(new PointF(Utils.dp2px(-112), Utils.dp2px(37)));
        pointList.add(new PointF(Utils.dp2px(-112), Utils.dp2px(-12)));
        pointList.add(new PointF(Utils.dp2px(-112), Utils.dp2px(-61)));
        pointList.add(new PointF(Utils.dp2px(-50), Utils.dp2px(-103)));

        path.reset();
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                path.moveTo(pointList.get(i * 3).x, pointList.get(i * 3).y);
            } else {
                path.lineTo(pointList.get(i * 3).x, pointList.get(i * 3).y);
            }

            int endPointIndex;
            if (i == 3) {
                endPointIndex = 0;
            } else {
                endPointIndex = i * 3 + 3;
            }

            path.cubicTo(pointList.get(i * 3 + 1).x, pointList.get(i * 3 + 1).y,
                    pointList.get(i * 3 + 2).x, pointList.get(i * 3 + 2).y,
                    pointList.get(endPointIndex).x, pointList.get(endPointIndex).y); //你的心形就是用贝塞尔曲线来画的吗
        }
        path.close();
        path.computeBounds(mHeartRect, false);//把path所占据的最小矩形区域，返回出去
    }
}
