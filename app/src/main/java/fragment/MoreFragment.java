package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.icox.homebabysetting.BaseActivity;
import com.icox.homebabysetting.Constants;
import com.icox.homebabysetting.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-28 15:54
 * @des ${TODO}
 */
public class MoreFragment extends Fragment implements View.OnClickListener {
    @InjectView(R.id.fragment_more_edt)
    TextView mEdt;
    @InjectView(R.id.fragment_more_but_number_1)
    ImageButton mButNumber1;
    @InjectView(R.id.fragment_more_but_number_2)
    ImageButton mButNumber2;
    @InjectView(R.id.fragment_more_but_number_3)
    ImageButton mButNumber3;
    @InjectView(R.id.fragment_more_but_number_4)
    ImageButton mButNumber4;
    @InjectView(R.id.fragment_more_but_number_5)
    ImageButton mButNumber5;
    @InjectView(R.id.fragment_more_but_number_6)
    ImageButton mButNumber6;
    @InjectView(R.id.fragment_more_but_number_7)
    ImageButton mButNumber7;
    @InjectView(R.id.fragment_more_but_number_8)
    ImageButton mButNumber8;
    @InjectView(R.id.fragment_more_but_number_9)
    ImageButton mButNumber9;
    @InjectView(R.id.fragment_more_but_number_0)
    ImageButton mButNumber0;
    @InjectView(R.id.fragment_more_but_number_clear)
    ImageButton mButNumberClear;
    @InjectView(R.id.fragment_more_but_number_complete)
    ImageButton mButNumberComplete;
    @InjectView(R.id.fragment_more_but_go)
    ImageButton mButGo;
    @InjectView(R.id.fragment_more_but_show_pwd)
    ImageButton mButShowPwd;
    private boolean isShowPwd = false;

    private String mPwd = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    private void initView() {

        mEdt.setTextSize(BaseActivity.mSizeRatio[1] * 18);

        //        mEdt.setInputType(InputType.TYPE_NULL);
        mButNumber0.setOnClickListener(this);
        mButNumber1.setOnClickListener(this);
        mButNumber2.setOnClickListener(this);
        mButNumber3.setOnClickListener(this);
        mButNumber4.setOnClickListener(this);
        mButNumber5.setOnClickListener(this);
        mButNumber6.setOnClickListener(this);
        mButNumber7.setOnClickListener(this);
        mButNumber8.setOnClickListener(this);
        mButNumber9.setOnClickListener(this);
        mButNumberClear.setOnClickListener(this);
        mButNumberComplete.setOnClickListener(this);
        mButGo.setOnClickListener(this);

        mButShowPwd.setOnClickListener(this);
        mEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());

        if (Constants.PASSWORD.equals("666666")) {
            Toast.makeText(getActivity(), "初始密码为：666666", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fragment_more_but_go || v.getId() == R.id.fragment_more_but_number_complete) {
            if (TextUtils.isEmpty(Constants.PASSWORD) || Constants.PASSWORD.equals(mPwd)) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                mPwd = "";
                mEdt.setText(mPwd);
            } else {
                Toast.makeText(getActivity(), "密码不正确", Toast.LENGTH_SHORT).show();
            }

            return;
        }

        switch (v.getId()) {
            case R.id.fragment_more_but_number_0:
                mPwd += "0";
                break;
            case R.id.fragment_more_but_number_1:
                mPwd += "1";
                break;
            case R.id.fragment_more_but_number_2:
                mPwd += "2";
                break;
            case R.id.fragment_more_but_number_3:
                mPwd += "3";
                break;
            case R.id.fragment_more_but_number_4:
                mPwd += "4";
                break;
            case R.id.fragment_more_but_number_5:
                mPwd += "5";
                break;
            case R.id.fragment_more_but_number_6:
                mPwd += "6";
                break;
            case R.id.fragment_more_but_number_7:
                mPwd += "7";
                break;
            case R.id.fragment_more_but_number_8:
                mPwd += "8";
                break;
            case R.id.fragment_more_but_number_9:
                mPwd += "9";
                break;
            case R.id.fragment_more_but_number_clear:
                if (mPwd.length() > 0)
                    mPwd = mPwd.substring(0, mPwd.length() - 1);
                break;
            case R.id.fragment_more_but_show_pwd:
                isShowPwd = !isShowPwd;
                if (isShowPwd) {
                    mButShowPwd.setImageResource(R.drawable.show_pwd_check);
                    mEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mButShowPwd.setImageResource(R.drawable.show_pwd_normal);
                    mEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                break;
            default:
                break;
        }

        mEdt.setText(mPwd);
    }
}