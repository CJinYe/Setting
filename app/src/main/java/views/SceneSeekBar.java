package views;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class SceneSeekBar extends SeekBar {

    private int mScreenWidth = 0;
    private int mScreenHeigh = 0;
    private Paint mPaint;
    private String mText;

    public SceneSeekBar(Context context) {
        super(context);
        init();
    }

    public SceneSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SceneSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setThumbOffset(dip2px(getContext(), 5));
        int padding = dip2px(getContext(), (float) 5);
        this.setPadding(padding, 2, padding, 2);
    }



    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}