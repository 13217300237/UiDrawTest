package study.hank.com.draw001.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;

/*
 * 清空内存的动效
 */
public class MemoryClearDrawable extends Drawable {

    private Paint mPaint, mSupportPaint, mSupportPaint2, mArcPaint, mCrossPaint, mTickPaint, mPointPaint;
    private int mWidth, mHeight, radius;//宽 高 圆半径
    private Context mContext;

    private float upMovingLength;// 原点上移的最大距离
    private float degreeOffset = 0;// 角度偏移量，影响外圈的起始角度

    private float crossAnimatorFactor = 1;//叉叉动画系数,影响叉叉长度
    private float pointMoveFactor = 0;//圆点上移距离 系数
    private float pointFactor = 0.45f;// 圆点的最大上下移系数
    private float tickFactor = 0;// 勾勾的长度系数，影响勾勾长度

    private boolean showSupportLine = false;//是否显示辅助线
    private float animatorTotalSpeedFactor = 1f;//动画执行的整体速度系数 1f表示1倍速度，

    /**
     * 是否显示辅助线
     *
     * @param showSupportLine
     */
    public void setShowSupportLine(boolean showSupportLine) {
        this.showSupportLine = showSupportLine;
        invalidateSelf();
    }

    /**
     * 设置动画的执行速率
     *
     * @param animatorTotalSpeedFactor
     */
    public void setAnimatorTotalSpeedFactor(float animatorTotalSpeedFactor) {
        this.animatorTotalSpeedFactor = animatorTotalSpeedFactor;
        initAnimators();
    }

    private final double arcPerDegree = 180 / Math.PI;//把角度换算成弧度,每度等于多少弧度

    public MemoryClearDrawable(Context context, int mWidth, int mHeight) {
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

        //画虚线，在23设备上虚线不生效，!?!?!?
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

        radius = Math.min(mWidth, mHeight) / 3;//确定圆圈的半径

        initAnimators();
    }

    private void initAnimators() {
        initCleaningAnimator();
        initCrossAnimator();
        initPointUpAnimator();
        intPointDownAnimator();
        initTickingAnimator();
    }

    private int getColor(int resId) {
        return mContext.getResources().getColor(resId);
    }

    private void drawBg(Canvas canvas) {
        int sideLength = Math.min(mWidth, mHeight);//方形边长
        RectF rectF = new RectF(-sideLength / 2, -sideLength / 2, sideLength / 2, sideLength / 2);
        canvas.drawRoundRect(rectF, Utils.dp2px(10), Utils.dp2px(10), mPaint);//中间两个参数决定x y方向上的圆角角度值
    }

    private void drawArc(Canvas canvas) {
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
        canvas.drawCircle(0, 0 - pointMoveFactor * upMovingLength, Utils.dp2px(5), mPointPaint);
    }

    private void drawSupportLines(Canvas canvas) {
        if (!showSupportLine) return;

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
        canvas.translate(mWidth / 2, mHeight / 2);//第一件事，先把坐标轴移动到中央，为了绘制方便
        drawBg(canvas);// 画背景
        drawSupportLines(canvas);//画辅助线
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

    private AnimatorSet animatorSet;
    private ValueAnimator cleaningAnimator, crossingAnimator, pointUpAnimator, pointDownAnimator, tickingAnimator;

    /**
     * 清除中 动画
     */
    private void initCleaningAnimator() {
        cleaningAnimator = ValueAnimator.ofFloat(0, 1);
        cleaningAnimator.setDuration((long) (200 / animatorTotalSpeedFactor));
        cleaningAnimator.setRepeatMode(ValueAnimator.RESTART);
        cleaningAnimator.setRepeatCount(3);
        cleaningAnimator.setInterpolator(new LinearInterpolator());
        cleaningAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            degreeOffset = 360 * a;
            invalidateSelf();
        });
        cleaningAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mState = States.STATE_CLEANING;
            }
        });
    }

    private void initCrossAnimator() {
        crossingAnimator = ValueAnimator.ofFloat(1, 0);
        crossingAnimator.setDuration((long) (500 / animatorTotalSpeedFactor));
        crossingAnimator.setInterpolator(new LinearInterpolator());
        crossingAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            crossAnimatorFactor = a;
            invalidateSelf();
        });
        crossingAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {// 当它执行完毕，状态值+1，然后启动下一个动画
                mState = States.STATE_CROSSING;
            }
        });
    }

    private void initPointUpAnimator() {
        upMovingLength = radius * pointFactor;
        pointUpAnimator = ValueAnimator.ofFloat(0, 1);
        pointUpAnimator.setDuration((long) (500 / animatorTotalSpeedFactor));
        pointUpAnimator.setInterpolator(new DecelerateInterpolator());
        pointUpAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            pointMoveFactor = a;
            invalidateSelf();
        });
        pointUpAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {// 当它执行完毕，状态值+1，然后启动下一个动画
                mState = States.STATE_POINT_UP;
            }
        });
    }

    private void intPointDownAnimator() {
        pointDownAnimator = ValueAnimator.ofFloat(1, -1);
        pointDownAnimator.setDuration((long) (500 / animatorTotalSpeedFactor));
        pointDownAnimator.setInterpolator(new AccelerateInterpolator());
        pointDownAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            pointMoveFactor = a;
            invalidateSelf();
        });
        pointDownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {// 当它执行完毕，状态值+1，然后启动下一个动画
                mState = States.STATE_POINT_DOWN;
            }
        });
    }

    //最后的勾勾伸展
    private void initTickingAnimator() {
        tickingAnimator = ValueAnimator.ofFloat(0, 1);
        tickingAnimator.setDuration((long) (500 / animatorTotalSpeedFactor));
        tickingAnimator.setInterpolator(new AccelerateInterpolator());
        tickingAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            tickFactor = a;
            invalidateSelf();
        });
        tickingAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mState = States.STATE_TICKING;
            }
        });
    }


    public void startAnimator() {
        crossAnimatorFactor = 1;
        if (null != animatorSet) {
            animatorSet.cancel();
            animatorSet = null;
        }
        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(
                cleaningAnimator,
                crossingAnimator,
                pointUpAnimator,
                pointDownAnimator,
                tickingAnimator);//次序执行
        animatorSet.start();
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
