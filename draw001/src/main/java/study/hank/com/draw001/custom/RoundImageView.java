package study.hank.com.draw001.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;

public class RoundImageView extends View {

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Paint mPaint;
    private Bitmap bitmap;
    private int bitmapW, bitmapH;
    private Path mPath;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        //获得bitmap
        bitmap = Utils.getAdjustBitmapByWidth(Utils.dp2px(80), R.drawable.girl, getContext());
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 方案1：直接切割canvas
        // 缺点，边缘略有锯齿，优点：思路简单直观
        drawRoundImage1(canvas);

        // 方案2：生成切割之后新的Bitmap
        // 缺点，参数计算复杂生涩难懂，　优点：边缘没有锯齿
        Bitmap bt = getRoundImage(bitmap);//总感觉这个图被放大了。
        canvas.drawBitmap(bt, 0, 500, mPaint);

    }

    /**
     * 方案1：
     * <p>
     * 1、考虑到图片可能较大，直接放原图，屏幕装不下，所以先把图进行缩放
     * 2、定义一个矩形区域，然后将原图画在这个里面
     * 3、给这个矩形区域再次切割，成为一个圆形区域，取出其中的差异区，把差异区浇灌成白色底色（底色可调整）
     *
     * @param canvas
     */
    private void drawRoundImage1(Canvas canvas) {
        //假如这张图很大，超出了屏幕宽高，那么我如果想让图片显示完整，就必须对他进行缩放，利用矩阵缩放刚刚好
        bitmap = Utils.getAdjustBitmapByWidth(Utils.dp2px(80), R.drawable.girl, getContext());
        bitmapW = bitmap.getWidth();
        bitmapH = bitmap.getHeight();//获得位图宽高
        Log.d("bitmapTag", "bitmapW:" + bitmapW + "-" + bitmapH);

//        canvas.drawBitmap(bitmap, 0, 0, mHeartPaint);//justForTest

        //定义切割区域的矩形
        final int startX = Utils.dp2px(10);
        final int startY = Utils.dp2px(10);
        final int clipWidth = Utils.dp2px(250);
        final int clipHeight = Utils.dp2px(250);//切出一个正方形区域

        final RectF rectF = new RectF(startX, startY, startX + clipWidth, startY + clipHeight);

        canvas.save();
        canvas.clipRect(rectF);
//        canvas.drawColor(getResources().getColor(R.color.aoyun_1));//justForTest
        canvas.drawBitmap(bitmap, startX, startY, mPaint); // ok，现在有一个方形区域了

        //但是我要的不是一个方形头像呀，现在再来切一个圆型区域出来
        mPath.addOval(rectF, Path.Direction.CCW);// 方向?横着切还是数着切？反正都是切一个矩形。emmm,也许吧?? MMP，这参数没啥用
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);//这是交集并集补集的概念，diffrence 不同区域
        canvas.drawColor(getResources().getColor(R.color.colorF));//把其他区域都浇灌成 白色
        canvas.restore();
    }

    public int reBitmapStartX = 0;
    public int reBitmapStartY = 0;

    /**
     * 方案2：
     * <p>
     * 直接根据原bitmap，利用 XFermode 制造出一个原形bitmap
     * 好吧，具体参数的含义我也讲不清楚，就到这里吧。
     * <p>
     * 这里要控制个重要参数：
     * 切割起始位置x，y
     */
    private Bitmap getRoundImage(Bitmap bitmap) {
        Bitmap bitmapEx;
        Canvas canvasEx;
        int radius;
        int x, y;
        Paint paint = new Paint();

        //所以说，产生一个Bitmap的步骤是:
        //1. 构建一个Bitmap 对象，宽高和原始bitmap一样，这时候这个bitmap是空白的,只拥有大小，没有内容
        bitmapEx = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);//造出一个同样大小的bitmap？

        //2. 如何给他注入内容呢？我们需要用canvas。 先new一个canvas，
        canvasEx = new Canvas(bitmapEx);//new一个画布？这可以理解为一个图层
        radius = bitmapEx.getWidth() / 2;//这个，决定圆形切割区域的半径
        x = 0;
        y = 0;
        paint.setColor(getResources().getColor(R.color.colorF));// 确定底色
        paint.setAntiAlias(true);//抗锯齿
        canvasEx.drawOval(new RectF(x, y, x + 2 * radius, y + 2 * radius), paint); // 给一个矩形区域，画出底色

        //3. 建立Xfermode模式，
        paint.setAntiAlias(false);
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);//建立切割模式,srcIn，是重合模式，表示只绘制重合位置的内容
        paint.setXfermode(porterDuffXfermode);

        //注入内容
        canvasEx.drawBitmap(bitmap, reBitmapStartX, reBitmapStartY, paint);//利用新的canvas，将图内容注入

        //经历了这一步骤，bitmapEx就有了内容··！？！？ 这么诡异？

        return bitmapEx;
    }

    public void refresh() {
        postInvalidate();
    }
}
