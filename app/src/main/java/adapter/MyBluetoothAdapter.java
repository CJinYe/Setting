package adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.icox.homebabysetting.R;

import java.util.List;

import Utils.ClsUtils;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-31 18:04
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class MyBluetoothAdapter extends BaseAdapter {

    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private List<BluetoothDevice> mDeviceList;

    public MyBluetoothAdapter(BluetoothAdapter bluetoothAdapter, Context context, List<BluetoothDevice> list) {
        mContext = context;
        mDeviceList = list;
        mBluetoothAdapter = bluetoothAdapter;
    }

    public void updateDevices(List<BluetoothDevice> devices) {
        mDeviceList = devices;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mDeviceList != null)
            return mDeviceList.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth, parent, false);
            holder.mBut = (ImageButton) convertView.findViewById(R.id.item_bluetooth_ib_peidui);
            holder.mTvTitle = (TextView) convertView.findViewById(R.id.item_bluetooth_tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BluetoothDevice device = mDeviceList.get(position);
        holder.mTvTitle.setText(device.getName());

        if (device.getBondState() == BluetoothDevice.BOND_BONDED){
            holder.mBut.setImageResource(R.drawable.sele_bluetooth_duankai);
        }

        holder.mBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClsUtils.createBond(device);
                    Log.i("mytest","配对   = ");
                } catch (Exception e) {
                    Log.i("mytest","配对失败   = "+e);
                    e.printStackTrace();
                }

                mDeviceList.clear();
                if (mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();
                mBluetoothAdapter.startDiscovery();
            }
        });
        return convertView;
    }


    public class ViewHolder {
        public TextView mTvTitle;
        public ImageButton mBut;
    }
}
