package study.hank.com.draw001;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class Utils {

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    private static DisplayMetrics getScreenDisplay(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return getScreenDisplay(context).widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return getScreenDisplay(context).heightPixels;
    }

    /**
     * 根据宽度来获得自适应的位图
     *
     * @param w       限制宽度
     * @param resId   图片资源id
     * @param context
     */
    public static Bitmap getAdjustBitmapByWidth(int w, int resId, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//自适应边框
        BitmapFactory.decodeResource(context.getResources(), resId, options);// 利用options来解码一个图
        options.inJustDecodeBounds = false;
        options.inTargetDensity = w;//指定宽
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }
}