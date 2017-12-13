package fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.icox.homebabysetting.R;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author 陈锦业
 * @time 2017-11-2 17:26
 */
public class BluetoothFileFragment extends Fragment {
    @InjectView(R.id.fragment_bluetooth_file_lv)
    ListView mFileLv;
    @InjectView(R.id.fragment_bluetooth_tv_tishi)
    TextView mTvTishi;
    @InjectView(R.id.fragment_bluetooth_file_but_finish)
    ImageButton mButFinish;
    private Context mContext;
    private BluetoothFileAdapter mAdapter;

    public interface BluetoothFileListener {
        void bluetoothFileClickFinish();
    }

    private BluetoothFileListener mBluetoothFileListener;

    public void setBluetoothFileListener(BluetoothFileListener bluetoothFileListener) {
        mBluetoothFileListener = bluetoothFileListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth_file, container, false);
        ButterKnife.inject(this, view);
        mContext = getActivity();
        updateAdapter();

        mButFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothFileListener != null)
                    mBluetoothFileListener.bluetoothFileClickFinish();
            }
        });
        return view;
    }

    public void updateAdapter() {
        String path = Environment.getExternalStorageDirectory() + "/bluetooth";
        File file = new File(path);
        File[] files = file.listFiles();
        if (!file.exists() || files == null || files.length < 1) {
            if (mFileLv != null && mTvTishi != null) {
                mTvTishi.setVisibility(View.VISIBLE);
                mFileLv.setVisibility(View.GONE);
            }
            return;
        }

        if (mAdapter == null)
            mAdapter = new BluetoothFileAdapter(files);
        else
            mAdapter.updateData(files);

        if (mFileLv != null && mTvTishi != null) {
            mFileLv.setAdapter(mAdapter);
            mTvTishi.setVisibility(View.GONE);
        }
    }

    private class BluetoothFileAdapter extends BaseAdapter {
        private File[] mFiles;

        BluetoothFileAdapter(File[] files) {
            mFiles = files;
        }

        void updateData(File[] files) {
            mFiles = files;
            notifyDataSetChanged();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth_file, parent, false);
                holder.mTextView = (TextView) convertView.findViewById(R.id.item_bluetooth_file_tv_name);
                holder.mIvIcon = (ImageView) convertView.findViewById(R.id.item_bluetooth_file_iv_icon);
                holder.mButCheck = (ImageButton) convertView.findViewById(R.id.item_bluetooth_file_but_check);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            String fileName = mFiles[position].getName();
            holder.mTextView.setText(fileName);

            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                    || fileName.endsWith(".png") || fileName.endsWith(".bmp")
                    || fileName.endsWith(".gif"))
                holder.mIvIcon.setImageResource(R.drawable.fragment_bluetooth_file_photo);
            else if (fileName.endsWith(".mp4") || fileName.endsWith(".3gp")
                    || fileName.endsWith(".avi") || fileName.endsWith(".flv")
                    || fileName.endsWith(".rmvb"))
                holder.mIvIcon.setImageResource(R.drawable.fragment_bluetooth_file_video);
            else if (fileName.endsWith(".mp3") || fileName.endsWith(".wma")
                    || fileName.endsWith(".wmv") || fileName.endsWith(".ape")
                    || fileName.endsWith(".wav") || fileName.endsWith(".ogg")
                    || fileName.endsWith(".midi") || fileName.endsWith(".flac")
                    || fileName.endsWith(".mid") || fileName.endsWith(".amr"))
                holder.mIvIcon.setImageResource(R.drawable.fragment_bluetooth_file_music);


            holder.mButCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(showOpenTypeDialog(mFiles[position]));
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView mTextView;
            ImageView mIvIcon;
            ImageButton mButCheck;
        }
    }

    public Intent showOpenTypeDialog(File param) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(param);
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
