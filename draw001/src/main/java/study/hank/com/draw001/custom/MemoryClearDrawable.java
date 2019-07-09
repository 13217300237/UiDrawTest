package study.hank.com.draw001.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;

/*
 * 清空内存的动效
 */
public class MemoryClearDrawable extends Drawable {

    private Paint mPaint;
    private Paint mSupportPaint, mSupportPaint2;
    private Paint mArcPaint, mCrossPaint, mTickPaint, mPointPaint;
    private int mWidth;
    private int mHeight;
    private Context mContext;
    private int radius;


    private final double arcPerDegree = 180 / Math.PI;//把角度换算成弧度,每度等于多少弧度

    public MemoryClearDrawable(Context context, int mWidth, int mHeight) {
        super();
        this.mContext = context;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mContext.getResources().getColor(R.color.aoyun_green));
        mPaint.setStyle(Paint.Style.FILL);

        //画虚线，在23设备上虚线不生效，我擦？！
        mSupportPaint = new Paint();
        mSupportPaint.setAntiAlias(true);
        mSupportPaint.setColor(mContext.getResources().getColor(R.color.aoyun_black));
        mSupportPaint.setPathEffect(new DashPathEffect(new float[]{20f, 10f}, 0));
        mSupportPaint.setStyle(Paint.Style.STROKE);
        mSupportPaint.setStrokeWidth(4);

        mSupportPaint2 = new Paint();
        mSupportPaint2.setAntiAlias(true);
        mSupportPaint2.setColor(mContext.getResources().getColor(R.color.aoyun_red));
        mSupportPaint2.setPathEffect(new DashPathEffect(new float[]{20f, 10f}, 0));
        mSupportPaint2.setStyle(Paint.Style.STROKE);
        mSupportPaint2.setStrokeWidth(4);

        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(Utils.dp2px(8));
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStrokeJoin(Paint.Join.ROUND);
        SweepGradient mColorShader = new SweepGradient(0, 0, new int[]{getColor(R.color.colorF), getColor(R.color.colorF0)}, new float[]{0.2f, 0.8f});
        mArcPaint.setShader(mColorShader);

        mCrossPaint = new Paint();
        mCrossPaint.setColor(getColor(R.color.colorF));
        mCrossPaint.setStrokeWidth(Utils.dp2px(6));
        mCrossPaint.setAntiAlias(true);
        mCrossPaint.setStyle(Paint.Style.STROKE);
        mCrossPaint.setStrokeCap(Paint.Cap.ROUND);

        mTickPaint = new Paint();
        mTickPaint.setColor(getColor(R.color.colorF));
        mTickPaint.setStrokeWidth(Utils.dp2px(6));
        mTickPaint.setAntiAlias(true);
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);

        mPointPaint = new Paint();
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(getColor(R.color.colorF));
        mPointPaint.setAntiAlias(true);
    }

    private int getColor(int resId) {
        return mContext.getResources().getColor(resId);
    }

    private void drawSupportLines(Canvas canvas) {
        //坐标移动到中央
        //2 纵横辅助线
        canvas.drawLine(
                -mWidth / 2,
                0,
                mWidth / 2,
                0,
                mSupportPaint);

        canvas.drawLine(
                0,
                -mHeight / 2,
                0,
                mHeight / 2,
                mSupportPaint2);
    }

    private void drawBg(Canvas canvas) {
        //不行，我的背景要换成方形的
        int sideLength = Math.min(mWidth, mHeight);//方形边长
        //此时，坐标轴已经移动到了中心
        RectF rectF = new RectF(-sideLength / 2, -sideLength / 2, sideLength / 2, sideLength / 2);
        canvas.drawRoundRect(rectF, Utils.dp2px(10), Utils.dp2px(10), mPaint);//中间两个参数决定x y方向上的圆角角度值
    }

    private void drawArc(Canvas canvas) {
        radius = Math.min(mWidth, mHeight) / 3;//确定圆圈的半径
        RectF rectF = new RectF(-radius, -radius, radius, radius);
        canvas.rotate(-90 - degreeOffset);
        canvas.drawArc(rectF, 0, 300, false, mArcPaint);
    }

    private void drawCross(Canvas canvas) {
        float lineLength = radius * 0.5f;

        float x1 = (float) (-lineLength * Math.cos(45 / arcPerDegree)) * crossAnimatorFactor;
        float y1 = (float) (-lineLength * Math.sin(45 / arcPerDegree)) * crossAnimatorFactor;
        float x2 = (float) (lineLength * Math.cos(45 / arcPerDegree)) * crossAnimatorFactor;
        float y2 = (float) (lineLength * Math.sin(45 / arcPerDegree)) * crossAnimatorFactor;
        canvas.drawLine(x1, y1, x2, y2, mCrossPaint);

        x1 = (float) (lineLength * Math.cos(45 / arcPerDegree)) * crossAnimatorFactor;
        y1 = (float) (-lineLength * Math.sin(45 / arcPerDegree)) * crossAnimatorFactor;
        x2 = (float) (-lineLength * Math.cos(45 / arcPerDegree)) * crossAnimatorFactor;
        y2 = (float) (lineLength * Math.sin(45 / arcPerDegree)) * crossAnimatorFactor;
        canvas.drawLine(x1, y1, x2, y2, mCrossPaint);
    }

    private void drawTick(Canvas canvas) {
        canvas.translate(0, radius * pointFactor);
        float leftLength = radius * 0.6f;
        float rightLength = radius * 0.9f;
        //左起始坐标值
        float x1 = (float) (-leftLength * Math.cos(40 / arcPerDegree)) * tickFactor;
        float y1 = (float) (-leftLength * Math.sin(40 / arcPerDegree)) * tickFactor;
        canvas.drawLine(0, 0, x1, y1, mTickPaint);

        //右起始坐标值
        float x2 = (float) (rightLength * Math.cos(50 / arcPerDegree)) * tickFactor;
        float y2 = (float) (-rightLength * Math.sin(50 / arcPerDegree)) * tickFactor;
        canvas.drawLine(0, 0, x2, y2, mTickPaint);
    }

    private void drawPoint(Canvas canvas) {
        canvas.drawCircle(0, 0 - pointUpFactor * upMovingLength, Utils.dp2px(5), mPointPaint);
    }

    public int mState = 0;//当前状态

    public static class States {
        public final static int STATE_ORI = 0;//初始
        public final static int STATE_CLEANING = 1;//外圈旋转
        public final static int STATE_CROSSING = 2;//叉叉收缩
        public final static int STATE_POINT_UP = 3;//点点上移
        public final static int STATE_POINT_DOWN = 4;//点点下移
        public final static int STATE_TICKING = 5;//勾勾伸开
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.translate(mWidth / 2, mHeight / 2);
        drawBg(canvas);
        drawSupportLines(canvas);
        canvas.save();

        switch (mState) {
            case States.STATE_ORI:// 初始状态，圆圈，有叉叉，静止状态
                drawArc(canvas);//外圈
                canvas.restore();
                drawCross(canvas);//叉叉
                canvas.save();
                break;
            case States.STATE_CLEANING://cleaning状态，圈圈旋转3圈，叉叉不动
                drawArc(canvas);//外圈, 绘制的时候要考虑 起始角度参数
                canvas.restore();
                drawCross(canvas);//叉叉
                canvas.save();
                break;
            case States.STATE_CROSSING://clean完毕，叉叉收缩
                drawArc(canvas);//外圈
                canvas.restore();
                drawCross(canvas);//叉叉
                canvas.save();
                break;
            case States.STATE_POINT_UP://叉叉不存在了,不用画
                drawArc(canvas);//外圈
                canvas.restore();
                canvas.save();
                drawPoint(canvas);
                break;
            case States.STATE_POINT_DOWN://点点下移
                drawArc(canvas);//外圈
                canvas.restore();
                canvas.save();
                drawPoint(canvas);
                break;
            case States.STATE_TICKING:
                drawArc(canvas);
                canvas.restore();
                canvas.save();
                drawTick(canvas);
                break;
            default:
                break;
        }
    }

    private float degreeOffset = 0;


    /**
     * 清除中 动画
     */
    private void startCleaningAnimator() {
        mState = States.STATE_CLEANING;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(200);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(3);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            degreeOffset = 360 * a;
            invalidateSelf();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {// 当它执行完毕，状态值+1，然后启动下一个动画
                startCrossAnimator();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                running = true;
            }
        });
        valueAnimator.start();
    }

    private float crossAnimatorFactor = 1;//叉叉动画系数,影响叉叉长度

    private void startCrossAnimator() {
        mState = States.STATE_CROSSING;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            crossAnimatorFactor = a;
            invalidateSelf();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {// 当它执行完毕，状态值+1，然后启动下一个动画
                startPointUp();
            }
        });
        valueAnimator.start();
    }

    private float pointUpFactor = 0;//圆点上移，从圆点向上
    private float upMovingLength;

    private float pointFactor = 0.45f;

    private void startPointUp() {
        mState = States.STATE_POINT_UP;
        upMovingLength = radius * pointFactor;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            pointUpFactor = a;
            invalidateSelf();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {// 当它执行完毕，状态值+1，然后启动下一个动画
                startPointDown();
            }
        });
        valueAnimator.start();
    }

    private void startPointDown() {
        mState = States.STATE_POINT_DOWN;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, -1);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            pointUpFactor = a;
            invalidateSelf();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {// 当它执行完毕，状态值+1，然后启动下一个动画
                startTicking();
            }
        });
        valueAnimator.start();
    }

    private float tickFactor = 0;

    //最后的勾勾伸展
    private void startTicking() {
        mState = States.STATE_TICKING;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            tickFactor = a;
            invalidateSelf();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {// 当它执行完毕，状态值+1，然后启动下一个动画
                running = false;
            }
        });
        valueAnimator.start();
    }

    private boolean running = false;

    public void startAnimator() {
        crossAnimatorFactor = 1;
        if (!running)
            startCleaningAnimator();
        else
            Log.d("startAnimatorTag", "动画进行中，请稍后");
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
