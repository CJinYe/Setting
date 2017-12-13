package Utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-30 9:33
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class WIFITool {
    private static WIFITool wifiTool;
    private static WifiManager mWifiManager;

    public static WIFITool WIFIToolInInstance(Context context) {
        if (wifiTool == null)
            wifiTool = new WIFITool(context);
        return wifiTool;
    }

    private WIFITool(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 判断wifi是否开启
     *
     * @return wifi状态
     */
    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public int getWifiState() {
        return mWifiManager.getWifiState();
    }

    /**
     * 设置开关
     *
     * @param state
     */
    public void setWifiState(boolean state) {
        if (state) {
            if (!isWifiEnabled())
                mWifiManager.setWifiEnabled(true);
        } else {
            if (isWifiEnabled())
                mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 扫描wifi
     *
     * @return 返回wifi列表
     */
    public List<ScanResult> getScanResults() {
        if (isWifiEnabled()) {
            mWifiManager.startScan();
            return mWifiManager.getScanResults();
        }
        return null;
    }

    /**
     * 获得当前连接的wifi信息
     *
     * @return
     */
    public WifiInfo getCurrentWifi() {
        if (isWifiEnabled())
            return mWifiManager.getConnectionInfo();
        return null;
    }

    public static final int WIFICIPHER_NOPASS = 0;
    public static final int WIFICIPHER_WEP = 1;
    public static final int WIFICIPHER_WPA = 2;

    private WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        //初始化WifiConfiguration
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        //指定对应的SSID
        config.SSID = "\"" + ssid + "\"";

        //如果之前有类似的配置
        //        WifiConfiguration tempConfig = isExist(ssid);
        //        if (tempConfig != null) {
        //            //则清除旧有配置
        //            mWifiManager.removeNetwork(tempConfig.networkId);
        //        }

        //不需要密码的场景
        if (type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //以WEP加密的场景
        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    public void enableNetwork(String ssid, String pws, int type) {
        int netId = mWifiManager.addNetwork(createWifiConfig(ssid, pws, type));
        boolean enable = mWifiManager.enableNetwork(netId, true);
        mWifiManager.saveConfiguration();
    }

    public WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }

    public void enableNetwork(int id, boolean disableOthers) {
        mWifiManager.enableNetwork(id, disableOthers);
        mWifiManager.saveConfiguration();
    }

    public void removeWifi(int id) {
        mWifiManager.removeNetwork(id);
        mWifiManager.saveConfiguration();
    }

}
