package dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icox.homebabysetting.BaseActivity;
import com.icox.homebabysetting.R;

import Utils.SizeUtil;


/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-30 14:16
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public abstract class WifiSaveDialog extends Dialog implements View.OnClickListener {

    private TextView mTvTitle;
    private final Context mContext;
    private TextView mTvLevel;
    private ScanResult mScanResult;
    private TextView mTvAnquan;

    public WifiSaveDialog(Context context, ScanResult scanResult) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mScanResult = scanResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifi_save);

        float[] size = BaseActivity.mSizeRatio;

        mTvTitle = (TextView) findViewById(R.id.dialog_wifi_title);
        mTvLevel = (TextView) findViewById(R.id.dialog_wifi_save_level);
        mTvAnquan = (TextView) findViewById(R.id.dialog_wifi_save_anquan);

        LinearLayout layout = (LinearLayout) findViewById(R.id.dialog_wifi_save_layout);
        SizeUtil.setLinearParams(layout, BaseActivity.mSizeRatio[0] * 651, BaseActivity.mSizeRatio[0] * 348);

        mTvTitle.setTextSize(BaseActivity.mSizeRatio[1] * 22);
        mTvLevel.setTextSize(BaseActivity.mSizeRatio[1] * 18);
        mTvAnquan.setTextSize(BaseActivity.mSizeRatio[1] * 18);


        mTvTitle.setText(mScanResult.SSID);

        String content = "信号强度：";
        if (mScanResult.level <= -70)
            content = content + "弱";
        else if (mScanResult.level > -70 && mScanResult.level <= -60)
            content = content + "一般";
        else if (mScanResult.level > -60)
            content = content + "强";

        mTvLevel.setText(content);
        mTvAnquan.setText("安全性：" + mScanResult.capabilities);

        ImageButton cancel = (ImageButton) findViewById(R.id.dialog_wifi_btn_cancel);
        ImageButton commit = (ImageButton) findViewById(R.id.dialog_wifi_btn_commit);
        ImageButton save = (ImageButton) findViewById(R.id.dialog_wifi_cancel_save);
        SizeUtil.setLinearParams(cancel, size[0] * 181, size[0] * 60);
        SizeUtil.setLinearParams(commit, size[0] * 181, size[0] * 60);
        SizeUtil.setLinearParams(save, size[0] * 181, size[0] * 60);

        findViewById(R.id.dialog_wifi_btn_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_wifi_btn_commit).setOnClickListener(this);
        findViewById(R.id.dialog_wifi_cancel_save).setOnClickListener(this);
    }

    public void setTvTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setTvLevel(String title) {
        mTvLevel.setText(title);
    }

    public abstract void clickCommit();

    public abstract void clickCancelSave();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_wifi_btn_commit:
                clickCommit();
                break;
            case R.id.dialog_wifi_cancel_save:
                clickCancelSave();
                break;

            default:
                break;
        }

        dismiss();
    }
}
