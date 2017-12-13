package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.icox.homebabysetting.R;


/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-30 14:16
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public abstract class BluetoothDialog extends Dialog implements View.OnClickListener {

    private EditText mEditText;
    private TextView mTvTitle;
    private final Context mMContext;
    private final String mMName;

    public BluetoothDialog(Context context, String name) {
        super(context, R.style.CustomDialog);
        mMContext = context;
        mMName = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bluetooth_setting);

        findViewById(R.id.dialog_bluetooth_file).setOnClickListener(this);
        findViewById(R.id.dialog_bluetooth_refresh).setOnClickListener(this);
        findViewById(R.id.dialog_bluetooth_rename).setOnClickListener(this);
    }


    public abstract void clickFile();

    public abstract void clickRefresh();

    public abstract void clickRename(String name);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_bluetooth_file:
                clickFile();
                break;
            case R.id.dialog_bluetooth_refresh:
                clickRefresh();
                break;
            case R.id.dialog_bluetooth_rename:
                BluetoothRenameDialog dialog = new BluetoothRenameDialog(mMContext) {
                    @Override
                    public void clickSure(String pws) {
                        clickRename(pws);
                    }
                };
                dialog.show();
                dialog.setEditText(mMName);
                break;

            default:
                break;
        }

        dismiss();
    }
}
