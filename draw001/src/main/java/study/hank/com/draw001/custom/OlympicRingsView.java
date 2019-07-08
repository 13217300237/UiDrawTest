package study.hank.com.draw001.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import study.hank.com.draw001.R;
import study.hank.com.draw001.Utils;

/**
 * 奥运五环做试验
 * <p>
 * 支持自适应
 *
 *
 */
public class OlympicRingsView extends View {

    private RectF rectF;

    public OlympicRingsView(Context context) {
        this(context, null);
    }

    public OlympicRingsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OlympicRingsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaints();
        initParams();
    }

    private void initParams() {
        radius = Utils.dp2px(100);// 圆圈半径
        strokeWidth = Utils.dp2px(12);//圆环粗细
        startLocationPoint = Utils.dp2px(50);//其实坐标位置
        rectF = new RectF(startLocationPoint, startLocationPoint, radius * 2, radius * 2);
    }

    private boolean ifShowHelperRect = false;

    private int rulerWidth = Utils.dp2px(650);//标尺宽
    private int rulerHeight = Utils.dp2px(300);//标尺高 , 绘制试验，是在这样一个标尺下执行的，
    // 所以，如果真正的view宽高不等于这个宽高，就要计算出倍率然后进行所有绘制参数的缩放

    Paint paintBlue, paintOrange, paintBlack, paintGreen, paintRed, paintHelper;

    private float radius = Utils.dp2px(100);// 圆圈半径
    private int strokeWidth = Utils.dp2px(12);//圆环粗细
    private int startLocationPoint = Utils.dp2px(50);//其实坐标位置

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(rulerWidth, rulerHeight);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(rulerWidth, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, rulerHeight);
        } else {
            super.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        init();//有多次测量，所以必须在每次测量之时，把参数还原。。不然就可能重复计算

        float scaleRatio;
        //比较真实宽高，与标尺宽高,有这么几个情况
        //计算真实的宽高比
        float r = measuredWidth / (measuredHeight * 1f);
        //标尺的宽高比
        float rule = rulerWidth / (rulerHeight * 1f);
        if (r > rule) {//如果真实宽高比，比标尺宽高比要大
            //那就以真实高度为准，来计算 参数缩放比例，保证图形显示完整
            scaleRatio = measuredHeight / (rulerHeight * 1f);
        } else {
            //否则，就以真实高度为准来计算
            scaleRatio = measuredWidth / (rulerWidth * 1f);
        }
        Log.d("scaleRatio", "" + scaleRatio);

//        //来确定最终的 绘制参数值
        radius = radius * scaleRatio;// 圆圈半径
        strokeWidth = (int) (strokeWidth * scaleRatio);//圆环粗细
        startLocationPoint = (int) (startLocationPoint * scaleRatio);//起始坐标位置
        rectF = new RectF(startLocationPoint, startLocationPoint, radius * 2, radius * 2);

        Log.d("scaleRatio", "radius:" + radius);
        Log.d("scaleRatio", "strokeWidth:" + strokeWidth);
        Log.d("scaleRatio", "startLocationPoint:" + startLocationPoint);
    }


    /**
     * 奥运五环的颜色们
     */
    private void initPaints() {

        paintHelper = new Paint();
        paintHelper.setColor(Color.GRAY);
        paintHelper.setAntiAlias(true);
        paintHelper.setStyle(Paint.Style.STROKE);
        paintHelper.setStrokeWidth(Utils.dp2px(1));

        paintBlue = new Paint();
        paintBlue.setColor(getResources().getColor(R.color.aoyun_1));
        paintBlue.setAntiAlias(true);
        paintBlue.setStyle(Paint.Style.STROKE);
        paintBlue.setStrokeWidth(strokeWidth);

        paintOrange = new Paint();
        paintOrange.setColor(getResources().getColor(R.color.aoyun_2));
        paintOrange.setAntiAlias(true);
        paintOrange.setStyle(Paint.Style.STROKE);
        paintOrange.setStrokeWidth(strokeWidth);

        paintBlack = new Paint();
        paintBlack.setAntiAlias(true);
        paintBlack.setColor(getResources().getColor(R.color.aoyun_3));
        paintBlack.setStyle(Paint.Style.STROKE);
        paintBlack.setStrokeWidth(strokeWidth);

        paintGreen = new Paint();
        paintGreen.setAntiAlias(true);
        paintGreen.setColor(getResources().getColor(R.color.aoyun_4));
        paintGreen.setStyle(Paint.Style.STROKE);
        paintGreen.setStrokeWidth(strokeWidth);

        paintRed = new Paint();
        paintRed.setAntiAlias(true);
        paintRed.setColor(getResources().getColor(R.color.aoyun_5));
        paintRed.setStyle(Paint.Style.STROKE);
        paintRed.setStrokeWidth(strokeWidth);
    }


    /**
     * onDraw，draw內容
     * <p>
     * 注意，onDraw这玩意很可能频繁调用，所以在这个里面，不要new！不要new，不要new！说三遍了！
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        final float xOffset = radius * 2;//上面一排，横向3个圆的偏移量，设定为半径的两倍
        final float yOffset = radius;//下面2个圆，y偏移量为一倍半径

        canvas.drawArc(rectF, 0, 360, false, paintBlue);
        canvas.save();

        //向下向右偏移，画橙色
        canvas.translate(yOffset, yOffset - radius * 0.5f);//平移画布
        canvas.drawArc(rectF, 0, 360, false, paintOrange);//橙色环

        canvas.restore();
        canvas.drawArc(rectF, 30, 90, false, paintBlue);
        canvas.save();// 原来你是保存状态

        //再画黑色圆环
        canvas.translate(xOffset, 0);//平移画布
        canvas.drawArc(rectF, 0, 360, false, paintBlack);

        //再画黑色和橙色的下部交汇处
        canvas.restore();
        canvas.translate(yOffset, yOffset - radius * 0.5f);//平移画布
        canvas.drawArc(rectF, 0, 90, false, paintOrange);//橙色环

        //然后画绿色的
        canvas.translate(xOffset, 0);//平移画布
        canvas.drawArc(rectF, 0, 360, false, paintGreen);//绿色环
        canvas.save();

        //这里，绿色和黑色的上面交界处，需要用 黑色填充，所以要回到 黑色那个边框上去画
        canvas.translate(-yOffset, -yOffset + radius * 0.5f);//平移画布
        canvas.drawArc(rectF, -45, 90, false, paintBlack);
        canvas.restore();

        canvas.translate(yOffset, -yOffset + radius * 0.5f);//平移画布
        canvas.drawArc(rectF, 0, 360, false, paintRed);

        canvas.translate(-yOffset, yOffset - radius * 0.5f);//平移画布
        canvas.drawArc(rectF, -90, 45, false, paintGreen);//绿色环
        drawHelperRect(canvas);
    }

    private void drawHelperRect(Canvas canvas) {
        if (ifShowHelperRect)
            canvas.drawRect(rectF, paintHelper);//画辅助框
    }


}
