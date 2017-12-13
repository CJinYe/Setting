package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icox.homebabysetting.BaseActivity;
import com.icox.homebabysetting.R;

import Utils.SizeUtil;


/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-30 14:16
 * @des ${TODO}
 */
public abstract class WifiDialog extends Dialog implements View.OnClickListener {

    private EditText mEditText;
    private TextView mTvTitle;
    private final Context mContext;
    private ImageButton mButShowPwd;
    private boolean isShowPwd = false;

    public WifiDialog(Context context) {
        super(context, R.style.CustomDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifi);

        mEditText = (EditText) findViewById(R.id.dialog_wifi_edt_pws);
        mTvTitle = (TextView) findViewById(R.id.dialog_wifi_title);
        mButShowPwd = (ImageButton) findViewById(R.id.dialog_wifi_but_show_pwd);
        mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        LinearLayout layout = (LinearLayout) findViewById(R.id.dialog_wifi_layout);
        SizeUtil.setLinearParams(layout, BaseActivity.mSizeRatio[0] * 652, BaseActivity.mSizeRatio[0] * 348);

        ImageButton cancel = (ImageButton) findViewById(R.id.dialog_wifi_btn_cancel);
        ImageButton sure = (ImageButton) findViewById(R.id.dialog_wifi_btn_sure);

        SizeUtil.setLinearParams(cancel,BaseActivity.mSizeRatio[0]*181,BaseActivity.mSizeRatio[0]*60);
        SizeUtil.setLinearParams(sure,BaseActivity.mSizeRatio[0]*181,BaseActivity.mSizeRatio[0]*60);

        findViewById(R.id.dialog_wifi_btn_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_wifi_btn_sure).setOnClickListener(this);


        mButShowPwd.setOnClickListener(this);

        mTvTitle.setTextSize(BaseActivity.mSizeRatio[1] * 22);
        mEditText.setTextSize(BaseActivity.mSizeRatio[1] * 18);

    }

    public void setTvTitle(String title) {
        mTvTitle.setText(title);
    }

    public abstract void clickSure(String pws);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_wifi_btn_sure:
                String str = mEditText.getText().toString().trim();
                if (str.length() < 8) {
                    Toast.makeText(mContext, "密码不可少于8位数", Toast.LENGTH_SHORT).show();
                    return;
                }
                clickSure(mEditText.getText().toString().trim());
                dismiss();
                break;
            case R.id.dialog_wifi_btn_cancel:
                dismiss();
                break;

            case R.id.dialog_wifi_but_show_pwd:
                isShowPwd = !isShowPwd;
                if (isShowPwd) {
                    mButShowPwd.setImageResource(R.drawable.show_pwd_check);
                    mEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mButShowPwd.setImageResource(R.drawable.show_pwd_normal);
                    mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                mEditText.setSelection(mEditText.getText().toString().trim().length());
                break;

            default:
                break;
        }


    }
}
