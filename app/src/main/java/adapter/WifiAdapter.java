package adapter;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.icox.homebabysetting.R;

import java.util.List;


/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-30 11:14
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class WifiAdapter extends BaseAdapter {

    private Context mContext;
    private List<ScanResult> mScanResults;
    private WifiInfo mCurrentWifi;
    private NetworkInfo.State mInfoState;

    public WifiAdapter(Context context, List<ScanResult> scanResults, WifiInfo currentWifi, NetworkInfo.State infoState) {
        mContext = context;
        mScanResults = scanResults;
        mCurrentWifi = currentWifi;
        mInfoState = infoState;
    }

    public void updateDate(List<ScanResult> results, WifiInfo currentWifi, NetworkInfo.State infoState) {
        mCurrentWifi = currentWifi;
        mScanResults = results;
        mInfoState = infoState;
        notifyDataSetChanged();
    }

    public void updateDate(List<ScanResult> results, WifiInfo wifiInfo) {
        mCurrentWifi = wifiInfo;
        mScanResults = results;
        notifyDataSetChanged();
    }

    public void updateCurrentWifi(WifiInfo wifiInfo) {
        notifyDataSetChanged();
    }


    public ScanResult getScanResut(int position) {
        return mScanResults.get(position);
    }

    @Override
    public int getCount() {
        if (mScanResults != null)
            return mScanResults.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wifi, parent, false);
            holder.ivLevel = (ImageView) convertView.findViewById(R.id.item_wifi_iv_level);
            holder.tvName = (TextView) convertView.findViewById(R.id.item_wifi_tv_name);
            holder.tvLink = (TextView) convertView.findViewById(R.id.item_wifi_tv_link);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScanResult scanResult = mScanResults.get(position);
        holder.tvName.setText(scanResult.SSID);
        if (scanResult.capabilities != null &&
                (scanResult.capabilities.contains("WPA")
                        || scanResult.capabilities.contains("WPA2")
                        || scanResult.capabilities.contains("WPA/WPA2"))) {
            if (scanResult.level <= -80)
                holder.ivLevel.setImageResource(R.drawable.wifi_level_0_lock);
            else if (scanResult.level > -80 && scanResult.level <= -70)
                holder.ivLevel.setImageResource(R.drawable.wifi_level_1_lock);
            else if (scanResult.level > -70 && scanResult.level <= -60)
                holder.ivLevel.setImageResource(R.drawable.wifi_level_2_lock);
            else if (scanResult.level > -60)
                holder.ivLevel.setImageResource(R.drawable.wifi_level_3_lock);
        } else {
            if (scanResult.level <= -80)
                holder.ivLevel.setImageResource(R.drawable.wifi_level_0);
            else if (scanResult.level > -80 && scanResult.level <= -70)
                holder.ivLevel.setImageResource(R.drawable.wifi_level_1);
            else if (scanResult.level > -70 && scanResult.level <= -60)
                holder.ivLevel.setImageResource(R.drawable.wifi_level_2);
            else if (scanResult.level > -60)
                holder.ivLevel.setImageResource(R.drawable.wifi_level_3);
        }

        if (scanResult.BSSID.equals(mCurrentWifi.getBSSID())) {
            holder.tvLink.setVisibility(View.VISIBLE);
            switch (mInfoState) {
                case CONNECTING:
                    holder.tvLink.setText("正在连接");
                    break;
                case DISCONNECTED:
                    holder.tvLink.setText("连接失败");
                    break;
                case CONNECTED:
                    holder.tvLink.setText("已连接");
                    break;

                default:
                    break;
            }
        } else {
            holder.tvLink.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class ViewHolder {
        public TextView tvName;
        public TextView tvLink;
        public ImageView ivLevel;
    }
}
