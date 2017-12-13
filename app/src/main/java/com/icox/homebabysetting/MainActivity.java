package com.icox.homebabysetting;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.icox.updateapp.utils.AES;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fragment.BluetoothFileFragment;
import fragment.BluetoothFragment;
import fragment.DeviceFragment;
import fragment.MoreFragment;
import fragment.SceneFragment;
import fragment.StoreFragment;
import fragment.WifiFragment;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @InjectView(R.id.main_but_goBack)
    ImageButton mButGoBack;
    @InjectView(R.id.main_radio_wifi)
    RadioButton mRadioWifi;
    @InjectView(R.id.main_radio_bluetooth)
    RadioButton mRadioBluetooth;
    @InjectView(R.id.main_radio_store)
    RadioButton mRadioStore;
    @InjectView(R.id.main_radio_scene)
    RadioButton mRadioScene;
    @InjectView(R.id.main_radio_device)
    RadioButton mRadioDevice;
    @InjectView(R.id.main_radio_more)
    RadioButton mRadioMore;
    @InjectView(R.id.main_radio_group)
    RadioGroup mRadioGroup;
    @InjectView(R.id.main_frameLayout)
    FrameLayout mFrameLayout;
    @InjectView(R.id.activity_main)
    LinearLayout mActivityMain;
    private Context mContext;
    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mContext = this;
        initView();
        initMorePwd();
    }

    private void initMorePwd() {

        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://" + Constants.AUTHORITY + "/" + Constants.INFO_TABLE);

        String pn = getIntent().getStringExtra("package_name");
        Constants.PACKAGE_NAME = getIntent().getStringExtra("package_name");

        if (pn != null && !TextUtils.isEmpty(pn)) {
            Cursor cursor = resolver.query(uri, new String[]{Constants.VALUE},
                    Constants.PN + " =? and " + Constants.KEY + " =?",
                    new String[]{pn, Constants.KEY_PWD}, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Constants.PASSWORD = cursor.getString(cursor.getColumnIndex(Constants.VALUE));
                }
                cursor.close();
            }

            AES aes = new AES();

            Cursor cursorImeI = resolver.query(uri, new String[]{Constants.VALUE},
                    Constants.PN + " =? and " + Constants.KEY + " =?",
                    new String[]{pn, Constants.KEY_IMEI}, null);
            if (cursorImeI != null) {
                while (cursorImeI.moveToNext()) {
                    Constants.IMEI = cursorImeI.getString(cursorImeI.getColumnIndex(Constants.VALUE));
                    Constants.IMEI = aes.stringFromJNIDecrypt(mContext, Constants.IMEI);
                }
                cursorImeI.close();
            }

            Cursor cursorMac = resolver.query(uri, new String[]{Constants.VALUE},
                    Constants.PN + " =? and " + Constants.KEY + " =?",
                    new String[]{pn, Constants.KEY_MAC}, null);
            if (cursorMac != null) {
                while (cursorMac.moveToNext()) {
                    Constants.MAC = cursorMac.getString(cursorMac.getColumnIndex(Constants.VALUE));
                    Constants.MAC = aes.stringFromJNIDecrypt(mContext, Constants.MAC);
                }
                cursorMac.close();
            }


        }

    }

    private void initView() {
        if (Constants.mScreenWidth == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            Constants.mScreenWidth = metrics.widthPixels;
            Constants.mScreenHeight = metrics.heightPixels;
            Constants.mScreenDip = metrics.densityDpi;
        }


        initFragment();
        mRadioGroup.check(R.id.main_radio_wifi);
        selectorFragment(0);

        mRadioGroup.setOnCheckedChangeListener(this);
        mButGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter==null){
            mRadioBluetooth.setVisibility(View.GONE);
        }
    }

    private void selectorFragment(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = mFragments.get(i);
            if (i == position) {
                if (fragment.isAdded())
                    transaction.show(fragment);
                else
                    transaction.add(R.id.main_frameLayout, fragment);

                if (position == 6) {
                    BluetoothFileFragment fileFragment = (BluetoothFileFragment) fragment;
                    fileFragment.updateAdapter();
                }
            } else {
                if (fragment.isAdded())
                    transaction.hide(fragment);
            }
        }

        if (position == 2 || position == 3)
            mActivityMain.setBackgroundResource(R.drawable.main_background_store);
        else
            mActivityMain.setBackgroundResource(R.drawable.main_background);
        transaction.commit();
    }

    private void initFragment() {
        WifiFragment wifiFragment = new WifiFragment();
        BluetoothFragment bluetoothFragment = new BluetoothFragment();
        StoreFragment storeFragment = new StoreFragment();
        SceneFragment sceneFragment = new SceneFragment();
        DeviceFragment deviceFragment = new DeviceFragment();
        MoreFragment moreFragment = new MoreFragment();
        BluetoothFileFragment bluetoothFileFragment = new BluetoothFileFragment();
        mFragments = new ArrayList<>();
        mFragments.add(wifiFragment);
        mFragments.add(bluetoothFragment);
        mFragments.add(storeFragment);
        mFragments.add(sceneFragment);
        mFragments.add(deviceFragment);
        mFragments.add(moreFragment);
        mFragments.add(bluetoothFileFragment);

        bluetoothFragment.setBluetoothFileListener(new BluetoothFragment.BluetoothFileListener() {
            @Override
            public void checkBluetoothFile() {
                selectorFragment(6);
            }
        });

        bluetoothFileFragment.setBluetoothFileListener(new BluetoothFileFragment.BluetoothFileListener() {
            @Override
            public void bluetoothFileClickFinish() {
                selectorFragment(1);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.main_radio_wifi:
                selectorFragment(0);
                break;
            case R.id.main_radio_bluetooth:
                selectorFragment(1);
                break;
            case R.id.main_radio_store:
                selectorFragment(2);
                break;
            case R.id.main_radio_scene:
                selectorFragment(3);
                break;
            case R.id.main_radio_device:
                selectorFragment(4);
                break;
            case R.id.main_radio_more:
                selectorFragment(5);
                break;

            default:
                break;
        }
    }

}
