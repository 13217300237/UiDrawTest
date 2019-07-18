package study.hank.com.draw001.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;

/**
 * 刮刮卡案例，讲解 XFermode 的使用
 * <p>
 * 继承ImageView，原始图片的事情就不归我操心了，我只需要把握 刮刮的部分
 */
public class ScratchCardImageView extends AppCompatImageView {

    private Paint mPaint;
    private Path mPath;//要接收手指滑过的地方，要用Path保存
    private PorterDuffXfermode xfermode;//要使用Xfermode图层覆盖模式
    private int cardColor;//刮刮卡原始涂层颜色
    private Bitmap mCoatBitmap;//涂层bitmap

    /**
     * 展示全图的临界点
     */
    private final float SHOW_ALL_PERCENT = 0.5f;
    private int w, h;

    public ScratchCardImageView(Context context) {
        this(context, null);
    }

    public ScratchCardImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScratchCardImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();//既然它是用来画path的，那么，
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(Utils.dp2px(20));//笔画宽度
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setStrokeCap(Paint.Cap.ROUND);//头部圆角
        mPaint.setStrokeJoin(Paint.Join.ROUND);//转角处圆角
        mPaint.setColor(getResources().getColor(R.color.colorC));

        cardColor = getResources().getColor(R.color.colorC);// 刮刮卡的原始图层颜色

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);//覆盖模式

        mPath = new Path();

        setClickable(true);//为什么要加这个？！？！ 因为：你不加，他就可能收不到move事件（源码还没有去查）
        // 贼特么诡异，反正如果发现不能点击了，就看看是不是setClickable的问题
    }

    private boolean inited = false;//是否已经初始化
    private Canvas mCoatCanvas;//凃层canvas


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!inited) {//由于onMeasure有可能会执行两次，而我们没有必要去执行两次初始化绘制，所以加一层控制
            //让涂层 和  图片一样宽高
            w = getMeasuredWidth();
            h = getMeasuredHeight();
            mCoatBitmap = createCoatBitmap(w, h);
            mCoatCanvas = new Canvas(mCoatBitmap);//把bitmap的内容委托给canvas
            inited = true;
        }
    }

    /**
     * 构建一个灰色图层Bitmap
     * @param w
     * @param h
     * @return
     */
    private Bitmap createCoatBitmap(int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);//构建一个指定宽高的bitmap
        Canvas canvas = new Canvas(bitmap);//新建一个画布
        Paint paint = new Paint();//新建画笔
        paint.setColor(cardColor);// 灰色
        paint.setAntiAlias(true);// 抗锯齿
        canvas.drawRect(0, 0, w, h, paint);//这个draw动作，会改变bitmap的内容
        return bitmap;
    }


    /**
     * 确定思路：
     * 1 画原图
     * 2 画涂层
     * 3 画手指刮过的path
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //1 画原图
        super.onDraw(canvas);//这里不可以删掉。。因为画原始图片，就是这里画的

        if (ifShowAll)//如果是展示全图，那么下面的遮盖层的绘制，就不要执行了
            return;

        //看来drawBitmap有另外的图层保存方式
        int layer = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);//保存当前图层
        // 2 画涂层
        canvas.drawBitmap(mCoatBitmap, 0, 0, mPaint);//绘制位图只需要 l和t两个参数，已这个为左上角起点开始绘制，可能会越界
        // 3 画path
        mPaint.setXfermode(xfermode);//先设置模式
        canvas.drawPath(mPath, mPaint);//再去画
        //  记得要把mCoatBitmap的状态保存一下
        mCoatCanvas.drawPath(mPath, mPaint);
        //记得清掉mode。。不然下一次 onDraw 会出事
        mPaint.setXfermode(null);
        canvas.restoreToCount(layer);// 回到指定图层
    }

    private float lastX, lastY;

    /**
     * 监控手势，改变mPath
     *
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (ifShowAll)
            return super.onTouchEvent(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = e.getX();
                lastY = e.getY();
                mPath.moveTo(lastX, lastY);
                break;
            case MotionEvent.ACTION_MOVE: //?!!? MMP，为什么你收不到MOVE事件，只能认为是被拦截了
                // 手指移动，划线
                float endX = (lastX + e.getX()) / 2;
                float endY = (lastY + e.getY()) / 2;
                mPath.quadTo(lastX, lastY, endX, endY);
                lastX = endX;
                lastY = endY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        post(showAllRunnable);//利用post方法，发送一个runnable到消息队列中，这样可以避免和前面的handler消息发生冲突，按照队列去处理，很好
        postInvalidate();
        return super.onTouchEvent(e);
        //  这里有个疑问.为什么这里不是true，我就收不到move事件，是不是ImageView特有的
        //  MMP,原来是clickable的问题。 我在xml里面加了
    }

    private boolean ifShowAll = false;

    Runnable showAllRunnable = new Runnable() {
        @Override
        public void run() {
            //判定是否具备展示全图的条件：
            //条件为：当前滑过的面积是否超过了临界百分比，比如30%

            int w = getWidth();
            int h = getHeight();
            float totalPx = w * h;//总像素点个数
            int[] pxArr = new int[w * h];//用一个数组去缓存

            mCoatBitmap.getPixels(pxArr, 0, w, 0, 0, w, h);// 获得当前bitmap的像素点数组，把它注入到pxArr中

            int cleanedPx = 0;
            //宽高二维解析
            for (int col = 0; col < h; col++) {
                for (int row = 0; row < w; row++) {
                    if (pxArr[col * w + row] == 0) {//手指滑过之后，当前位置的像素点就会被置为0
                        cleanedPx++;//已经被清空的像素点个数
                    }
                }
            }

            float res = cleanedPx / totalPx;//计算一共滑过了百分之多少

            if (callback != null)
                callback.scratch(res);
            if (res > SHOW_ALL_PERCENT) {//临界值0.2
                ifShowAll = true;//保存状态，显示全图
                postInvalidate();//利用handler发送事件
            } else {
                ifShowAll = false;
            }

        }
    };

    public interface ScratchCallback {
        void scratch(float currentPercent);
    }

    ScratchCallback callback;

    public void setCallback(ScratchCallback callback) {
        this.callback = callback;
    }


    public void recycle() {
        if (mCoatBitmap != null)
            mCoatBitmap.recycle();
    }

    public void showAll() {
        ifShowAll = true;//保存状态，显示全图
        postInvalidate();
    }

    public void reset() {
        //重置mCoatBitmap，重置path，重置ifShowAll为false，然后刷新
        mCoatBitmap = createCoatBitmap(w, h);
        mCoatCanvas = new Canvas(mCoatBitmap);
        mPath.reset();
        ifShowAll = false;
        postInvalidate();
    }
}
