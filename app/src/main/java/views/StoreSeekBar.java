package views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class StoreSeekBar extends SeekBar {

    private int mScreenWidth = 0;
    private int mScreenHeigh = 0;
    private Paint mPaint;
    private String mText = "";
    private Rect mBound;
    private Context mContext;

    public StoreSeekBar(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public StoreSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    public StoreSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        this.setMax(100);
        //        this.setThumbOffset(dip2px(getContext(), 5));
        int padding = dip2px(getContext(), (float) 10);
        this.setPadding(padding, 0, padding, 0);
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#4f6624"));
        mPaint.setTextSize(35);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //        mScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
        //        mScreenHeigh = MeasureSpec.getSize(heightMeasureSpec);
        //        setMeasuredDimension(mScreenWidth,mScreenHeigh);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenWidth = w;
        mScreenHeigh = h;
    }

    public void setText(int text) {
        mText = text + "%";
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mText != null) {
            float ratio = (float) mScreenHeigh / 30;
            float size = 35 * ratio;
            mPaint.setTextSize(size);
            canvas.drawText(mText, mScreenWidth / 2 - mPaint.getTextSize() / 2,
                    mScreenHeigh, mPaint);
        }

    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}


