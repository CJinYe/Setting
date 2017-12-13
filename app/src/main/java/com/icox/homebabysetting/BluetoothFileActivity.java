package com.icox.homebabysetting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-1 15:20
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class BluetoothFileActivity extends Activity {
    @InjectView(R.id.bluetooth_file_lv)
    ListView mLv;
    @InjectView(R.id.bluetooth_file_tv_no)
    TextView mTvNo;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_file);
        ButterKnife.inject(this);
        mContext = this;
//        initView();
    }

    private void initView() {
        String path = Environment.getExternalStorageDirectory() + "/bluetooth";
        File file = new File(path);
        File[] files = file.listFiles();
        if (!file.exists() || files == null || files.length < 1) {
            mLv.setVisibility(View.GONE);
            return;
        }

        BluetoothFileAdapter adapter = new BluetoothFileAdapter(files);
        mLv.setAdapter(adapter);
        mTvNo.setVisibility(View.GONE);


    }

    private class BluetoothFileAdapter extends BaseAdapter {
        private File[] mFiles;

        public BluetoothFileAdapter(File[] files) {
            mFiles = files;
        }

        @Override
        public int getCount() {
            return mFiles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth_file,parent,false);
                holder.mTextView = (TextView) convertView.findViewById(R.id.item_bluetooth_file_tv_name);
                convertView.setTag(holder);
            }else
                holder = (ViewHolder) convertView.getTag();

            holder.mTextView.setText(mFiles[position].getName());
            return convertView;
        }

        private class ViewHolder{
            TextView mTextView;
        }
    }
}
