package fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icox.homebabysetting.BaseActivity;
import com.icox.homebabysetting.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import adapter.MyBluetoothAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import dialog.BluetoothDialog;
import views.MyListView;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-28 15:54
 * @des ${TODO}
 */
public class BluetoothFragment extends Fragment implements View.OnClickListener {
    @InjectView(R.id.fragment_bluetooth_iv_onOff)
    ImageButton mIvOnOff;
    @InjectView(R.id.fragment_bluetooth_lv_old)
    MyListView mLvOld;
    @InjectView(R.id.fragment_bluetooth_ll_old)
    LinearLayout mLlOld;
    @InjectView(R.id.fragment_bluetooth_lv_new)
    MyListView mLvNew;
    @InjectView(R.id.fragment_bluetooth_ll_new)
    LinearLayout mLlNew;
    @InjectView(R.id.fragment_bluetooth_ib_setting)
    ImageButton mIbSetting;
    @InjectView(R.id.fragment_bluetooth_ll_main)
    LinearLayout mLlMain;
    @InjectView(R.id.fragment_bluetooth_tv_tishi)
    ImageView mTvTishi;
    @InjectView(R.id.fragment_bluetooth_loading)
    LinearLayout mLoading;
    @InjectView(R.id.fragment_bluetooth_tv_name)
    TextView mTvName;
    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> mPairedDevices = new ArrayList<>();
    private List<BluetoothDevice> mNewDevices = new ArrayList<>();

    public interface BluetoothFileListener {
        void checkBluetoothFile();
    }

    private BluetoothFileListener mBluetoothFileListener;

    public void setBluetoothFileListener(BluetoothFileListener bluetoothFileListener) {
        mBluetoothFileListener = bluetoothFileListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        ButterKnife.inject(this, view);
        registerBroadcastReceiver();
        initView();
        return view;
    }

    private void initView() {

        mTvName.setTextSize(BaseActivity.mSizeRatio[1] * 20);

        if (isBluetoothEnable()) {
            mIvOnOff.setImageResource(R.drawable.but_off);
            mHandler.sendEmptyMessageDelayed(1, 1000);
        } else {
            mIvOnOff.setImageResource(R.drawable.but_on);
            mLlMain.setVisibility(View.GONE);
            mTvTishi.setVisibility(View.VISIBLE);
        }

        if (mBluetoothAdapter != null) {
            mTvName.setText(mBluetoothAdapter.getName());
            //            mHandler.sendEmptyMessage(4);
        } else {
            mLlMain.setVisibility(View.GONE);
            mTvTishi.setVisibility(View.VISIBLE);
            mLoading.setVisibility(View.GONE);
        }

        mIvOnOff.setOnClickListener(this);
        mIbSetting.setOnClickListener(this);

        setDiscoverableTimeout(300);

    }

    public void setDiscoverableTimeout(int timeout) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
            setScanMode.setAccessible(true);
            setDiscoverableTimeout.invoke(adapter, timeout);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, timeout);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("mytest", "错误 = " + e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        getActivity().unregisterReceiver(receiver);
    }

    private long lastClickOnOff = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_bluetooth_iv_onOff:
                if (System.currentTimeMillis() - lastClickOnOff < 500) {
                    //                    Toast.makeText(getActivity(), "请不要频繁点击！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mBluetoothAdapter == null) {
                    Toast.makeText(getActivity(), "请检查设备是否支持蓝牙！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isBluetoothEnable()) {
                    //关闭蓝牙
                    turnOffBluetooth();
                    mIvOnOff.setImageResource(R.drawable.but_on);
                    mLlMain.setVisibility(View.GONE);
                    mTvTishi.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                } else {
                    //打开蓝牙
                    boolean b = turnOnBluetooth();
                    mIvOnOff.setImageResource(R.drawable.but_off);
                    mHandler.sendEmptyMessageDelayed(1, 2000);
                    mLlMain.setVisibility(View.GONE);
                    mTvTishi.setVisibility(View.GONE);
                    mLoading.setVisibility(View.VISIBLE);
                    mHandler.removeMessages(3);
                    mHandler.sendEmptyMessageDelayed(3, 2000);
                }
                lastClickOnOff = System.currentTimeMillis();
                break;
            case R.id.fragment_bluetooth_ib_setting:

                if (!isBluetoothEnable()) {
                    Toast.makeText(getActivity(), "请检查蓝牙设备是否正常开启！", Toast.LENGTH_SHORT).show();
                    return;
                }

                BluetoothDialog dialog = new BluetoothDialog(getActivity(), mBluetoothAdapter.getName()) {
                    @Override
                    public void clickFile() {
                        //                        Intent intent = new Intent(getActivity(), BluetoothFileActivity.class);
                        //                        getActivity().startActivity(intent);
                        if (mBluetoothFileListener != null)
                            mBluetoothFileListener.checkBluetoothFile();
                    }

                    @Override
                    public void clickRefresh() {
                        if (isBluetoothEnable())
                            mHandler.sendEmptyMessageDelayed(1, 100);

                    }

                    @Override
                    public void clickRename(String name) {
                        if (name != null && !TextUtils.isEmpty(name) && mBluetoothAdapter != null) {
                            mBluetoothAdapter.setName(name);
                            mTvName.setText(name);

                            if (mBluetoothAdapter.isDiscovering())
                                mBluetoothAdapter.cancelDiscovery();
                            mBluetoothAdapter.startDiscovery();
                        }
                    }
                };
                dialog.show();
                break;

            default:
                break;
        }

    }

    private Handler mHandler = new Handler() {

        private MyBluetoothAdapter mNewAdapter;
        private MyBluetoothAdapter mPairedAdapter;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mPairedDevices.clear();
                    mNewDevices.clear();
                    if (isBluetoothEnable()) {
                        if (mBluetoothAdapter.isDiscovering())
                            mBluetoothAdapter.cancelDiscovery();

                        mBluetoothAdapter.startDiscovery();
                        mLlMain.setVisibility(View.GONE);
                        mTvTishi.setVisibility(View.GONE);
                        mLoading.setVisibility(View.VISIBLE);
                    } else {
                        if (mBluetoothAdapter.isDiscovering())
                            mBluetoothAdapter.cancelDiscovery();
                    }


                    break;
                case 2:

                    if (!isBluetoothEnable())
                        return;

                    mLlMain.setVisibility(View.VISIBLE);
                    mTvTishi.setVisibility(View.GONE);
                    mLoading.setVisibility(View.GONE);

                    mPairedDevices.clear();
                    // 获取已经配对的设备
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() < 1)
                        mLlOld.setVisibility(View.GONE);
                    else
                        for (BluetoothDevice device : pairedDevices) {
                            mPairedDevices.add(device);
                        }

                    if (mPairedAdapter == null)
                        mPairedAdapter = new MyBluetoothAdapter(mBluetoothAdapter, getActivity(), mPairedDevices);
                    else
                        mPairedAdapter.updateDevices(mPairedDevices);
                    mLvOld.setAdapter(mPairedAdapter);

                    if (mNewAdapter == null)
                        mNewAdapter = new MyBluetoothAdapter(mBluetoothAdapter, getActivity(), mNewDevices);
                    else
                        mNewAdapter.updateDevices(mNewDevices);
                    mLvNew.setAdapter(mNewAdapter);
                    break;
                case 3:
                    if (!isBluetoothEnable()) {
                        mIvOnOff.setImageResource(R.drawable.but_on);
                        turnOffBluetooth();
                        mLlMain.setVisibility(View.GONE);
                        mTvTishi.setVisibility(View.VISIBLE);
                        mLoading.setVisibility(View.GONE);
                        mHandler.removeMessages(1);
                        Toast.makeText(getActivity(), "请检查设备是否支持蓝牙！", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 4:
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    getActivity().startActivity(discoverableIntent);
                    mHandler.sendEmptyMessageDelayed(4, 300000);

                    break;
                default:
                    break;
            }
        }
    };

    // 广播接收器
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 收到的广播类型
            String action = intent.getAction();
            // 发现设备的广播
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 从intent中获取设备
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 判断是否配对过
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    // 添加到列表
                    if (!mNewDevices.contains(device))
                        mNewDevices.add(device);
                    Log.i("mytest", "name = " + device.getName() + "    address = " + device.getAddress());
                }
                // 搜索完成
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                //关闭进度条
                Log.e("mytest", "搜索完毕");
                mHandler.sendEmptyMessageDelayed(2, 1000);
            }
            Log.e("mytest", "广播  = " + action);
        }
    };


    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(receiver, intentFilter);

        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    public static boolean turnOnBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.enable();
    }

    public static boolean turnOffBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.disable();
    }

    public boolean isBluetoothEnable() {
        if (mBluetoothAdapter == null)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }
}
