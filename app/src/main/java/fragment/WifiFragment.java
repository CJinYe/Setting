package fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.icox.homebabysetting.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Utils.WIFITool;
import adapter.WifiAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import dialog.WifiDialog;
import dialog.WifiSaveDialog;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-28 15:54
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class WifiFragment extends Fragment {
    @InjectView(R.id.fragment_wifi_but_onoff)
    ImageButton mWifiButOnoff;
    @InjectView(R.id.fragment_wifi_lv)
    ListView mWifiLv;
    @InjectView(R.id.fragment_wifi_tishi)
    ImageView mWifiTishi;
    private WIFITool mWifiTool;
    private WifiAdapter mWifiAdapter;
    private NetworkInfo.State infoState = NetworkInfo.State.CONNECTED;

    private long lastClickOnOff = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        ButterKnife.inject(this, view);
        registerBroadcastReceiver();
        initView();
        return view;
    }

    private void initView() {

        mWifiTool = WIFITool.WIFIToolInInstance(getActivity());
        if (mWifiTool.isWifiEnabled()) {
            mWifiButOnoff.setImageResource(R.drawable.but_off);
            mhandler.sendEmptyMessage(1);
            //            initWifi();
        } else {
            mWifiButOnoff.setImageResource(R.drawable.but_on);
            mWifiLv.setVisibility(View.GONE);
            mWifiTishi.setVisibility(View.VISIBLE);
        }

        mWifiButOnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (System.currentTimeMillis() - lastClickOnOff < 500) {
                    //                    Toast.makeText(getActivity(), "请不要频繁点击！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mWifiTool.isWifiEnabled()) {
                    mWifiButOnoff.setImageResource(R.drawable.but_on);
                    mWifiLv.setVisibility(View.GONE);
                    mWifiTishi.setVisibility(View.VISIBLE);
                    mWifiTool.setWifiState(false);
                    //                    mhandler.removeMessages(1);
                    mhandler.sendEmptyMessageDelayed(1, 1000);
                } else {
                    mWifiTool.setWifiState(true);
                    mWifiLv.setVisibility(View.VISIBLE);
                    mWifiTishi.setVisibility(View.GONE);
                    mWifiButOnoff.setImageResource(R.drawable.but_off);
                    //                    mhandler.removeMessages(1);
                    mhandler.sendEmptyMessageDelayed(1, 1000);
                }

                lastClickOnOff = System.currentTimeMillis();
            }
        });

        mWifiLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ScanResult scanResult = mWifiAdapter.getScanResut(position);
                final WifiConfiguration wifiConfiguration = mWifiTool.isExist(scanResult.SSID);
                if (wifiConfiguration != null) {
                    //如果已经保存
                    WifiSaveDialog dialog = new WifiSaveDialog(getActivity(), scanResult) {
                        @Override
                        public void clickCommit() {
                            mWifiTool.enableNetwork(wifiConfiguration.networkId, true);
                        }

                        @Override
                        public void clickCancelSave() {
                            mWifiTool.removeWifi(wifiConfiguration.networkId);
                        }
                    };
                    dialog.show();
                } else {
                    if (scanResult.capabilities != null &&
                            (scanResult.capabilities.contains("WPA")
                                    || scanResult.capabilities.contains("WPA2")
                                    || scanResult.capabilities.contains("WPA/WPA2"))) {
                        WifiDialog dialog = new WifiDialog(getActivity()) {
                            @Override
                            public void clickSure(String pws) {
                                mWifiTool.enableNetwork(scanResult.SSID, pws, WIFITool.WIFICIPHER_WPA);
                            }
                        };
                        dialog.show();
                        dialog.setTvTitle(scanResult.SSID);
                    } else {
                        mWifiTool.enableNetwork(scanResult.SSID, "", WIFITool.WIFICIPHER_NOPASS);
                    }
                }

                //                mhandler.removeMessages(1);
                mhandler.sendEmptyMessageDelayed(1, 1000);
            }
        });
    }

    private BroadcastReceiver mBroadcastReceiver;

    private void registerBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    infoState = info.getState();
                }

                //                mhandler.removeMessages(1);
                mhandler.sendEmptyMessageDelayed(1, 1000);
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            post(new Runnable() {
                @Override
                public void run() {
                    List<ScanResult> scanResult = mWifiTool.getScanResults();
                    List<ScanResult> scanResults = new ArrayList<>();
                    ScanResult currentResult = null;
                    if (scanResult != null) {

                        for (ScanResult sc : scanResult) {
                            if (sc.SSID != null && !TextUtils.isEmpty(sc.SSID) && !scanResults.contains(sc)) {
                                scanResults.add(sc);
                                if (sc.BSSID.equals(mWifiTool.getCurrentWifi().getBSSID())) {
                                    currentResult = sc;
                                }
                            }
                        }
                    }

                    Collections.sort(scanResults, new Comparator<ScanResult>() {
                        @Override
                        public int compare(ScanResult lhs, ScanResult rhs) {
                            return rhs.level - lhs.level;
                        }
                    });

                    if (currentResult != null) {
                        scanResults.remove(currentResult);
                        scanResults.add(0, currentResult);
                    }


                    if (mWifiAdapter == null)
                        mWifiAdapter = new WifiAdapter(getActivity(), scanResults, mWifiTool.getCurrentWifi(), infoState);
                    else {
                        mWifiAdapter.updateDate(scanResults, mWifiTool.getCurrentWifi(), infoState);
                    }
                    mWifiLv.setAdapter(mWifiAdapter);

                    if (scanResults.size() < 1) {
                        mhandler.sendEmptyMessageDelayed(1, 500);
                    }
                }
            });
        }
    };

    private void initWifi() {
        List<ScanResult> scanResult = mWifiTool.getScanResults();
        List<ScanResult> scanResults = new ArrayList<>();
        for (ScanResult sc : scanResult) {
            if (sc.SSID != null && !TextUtils.isEmpty(sc.SSID))
                scanResults.add(sc);
        }
        mWifiAdapter = new WifiAdapter(getActivity(), scanResults, mWifiTool.getCurrentWifi(), infoState);
        mWifiLv.setAdapter(mWifiAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        mhandler.removeMessages(1);
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
