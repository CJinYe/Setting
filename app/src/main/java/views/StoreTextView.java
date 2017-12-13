package views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-10 17:27
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class StoreTextView extends TextView {

    private Context mContext;

    public StoreTextView(Context context) {
        super(context);
        mContext = context;
    }

    public StoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public StoreTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        float ratio = (float) h/68;
//        float size = 16 * ratio;
//        setTextSize(size);
    }
}
