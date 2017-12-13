package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

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
public abstract class BluetoothRenameDialog extends Dialog implements View.OnClickListener {

    private EditText mEditText;

    public BluetoothRenameDialog(Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bluetooth_rename);

        mEditText = (EditText) findViewById(R.id.dialog_bluetooth_edt_name);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        findViewById(R.id.dialog_bluetooth_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_bluetooth_sure).setOnClickListener(this);

        ImageButton cancel = (ImageButton) findViewById(R.id.dialog_bluetooth_cancel);
        ImageButton sure = (ImageButton) findViewById(R.id.dialog_bluetooth_sure);

        SizeUtil.setLinearParams(cancel,BaseActivity.mSizeRatio[0]*181,BaseActivity.mSizeRatio[0]*60);
        SizeUtil.setLinearParams(sure,BaseActivity.mSizeRatio[0]*181,BaseActivity.mSizeRatio[0]*60);
        mEditText.setTextSize(BaseActivity.mSizeRatio[1] * 18);
    }

    public abstract void clickSure(String pws);

    void setEditText(String name) {
        mEditText.setText(name);
        mEditText.setSelection(name.length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_bluetooth_sure:
                clickSure(mEditText.getText().toString().trim());
                break;

            default:
                break;
        }

        dismiss();
    }
}
