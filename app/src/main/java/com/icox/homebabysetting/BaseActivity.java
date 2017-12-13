package com.icox.homebabysetting;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import Utils.SizeUtil;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-7 11:34
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class BaseActivity extends FragmentActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    public static float[] mSizeRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(this);
        mSizeRatio = SizeUtil.getScreenScale(this);
    }

    public void initWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        //保持屏幕常亮
        //        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onGlobalLayout() {
        initWindow();
        findViewById(android.R.id.content).getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }
}
