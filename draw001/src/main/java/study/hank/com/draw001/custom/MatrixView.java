package study.hank.com.draw001.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;

/**
 * 矩阵变换测试
 */
public class MatrixView extends View {
    public MatrixView(Context context) {
        this(context, null);
    }

    public MatrixView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatrixView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Bitmap mBitmap;
    private Paint mPaint;
    private int mW, mH;

    private void init() {
        //先弄一个图
        mBitmap = Utils.getAdjustBitmapByWidth(Utils.dp2px(40), R.drawable.girl, getContext());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mW = mBitmap.getWidth();
        mH = mBitmap.getHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawBitmap(mBitmap, 0, 0, mHeartPaint);

        //矩阵，是canvas底层的内容，矩阵的作用是  坐标映射
        Matrix matrix = new Matrix();//在原始位置
        //这里就生成了一个基础矩阵
        // 然而，我们 canvas.drawBitmap的时候可以传入matrix对象，来让图片的最终显示效果受到矩阵的影响
        // 矩阵的运算，数学规则，不需要知道的太清楚。但是有一点
        // 矩阵的乘法，不满足交换律，要清楚
        // 另外 矩阵的api，  setXXX方法，是清空矩阵，然后再set
        // post/pre 系列方法，
        // 如果使用post，可以按照正常思维顺序来写代码
        // 如果是用pre系列方法，就必须反过来
        // 所以，还是用post顺着来吧···

        //现在来对矩阵进行操作吧
//        matrix.setScale(0.5f, 0.5f);
//        matrix.setTranslate(100, 100);
//        matrix.setRotate(60);
//        matrix.setRotate(degree, mW / 2, mH / 2);//设置旋转的中心点

        matrix.setSkew(skewFloat, skewFloat);
        matrix.postRotate(degree);
        matrix.postTranslate(500, 500);

//        matrix.setSkew(mW / 2, mH / 2, 0, 0);

        canvas.drawBitmap(mBitmap, matrix, mPaint);

    }

    private float skewFloat = 0f;
    private int degree = 0;
    private ValueAnimator animator;

    public void startRotationAnimator() {
        stopAnimator();
        animator = ValueAnimator.ofInt(0, 360);
        animator.setDuration(3000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int a = (int) animation.getAnimatedValue();
            degree = a;
            skewFloat = (a + 0.5f) / 360;
            refresh();
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }

    private void stopAnimator() {
        if (animator != null) {
            animator.cancel();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startRotationAnimator();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimator();
    }

    public void refresh() {
        postInvalidate();
    }

}
