package fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.storage.StorageManager;
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
import com.icox.homebabysetting.Constants;
import com.icox.homebabysetting.R;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import butterknife.ButterKnife;
import butterknife.InjectView;
import views.StoreSeekBar;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-28 15:54
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class StoreFragment extends Fragment implements View.OnClickListener {
    @InjectView(R.id.fragment_store_tv_bendi)
    TextView mTvBendi;
    @InjectView(R.id.fragment_store_tv_sd_card)
    TextView mTvSdCard;
    @InjectView(R.id.fragment_store_tv_u_pan)
    TextView mTvUPan;
    @InjectView(R.id.fragment_store_iv_Local_no_device)
    ImageView mIvLocalNoDevice;
    @InjectView(R.id.fragment_store_iv_SDCard_no_device)
    ImageView mIvSDCardNoDevice;
    @InjectView(R.id.fragment_store_iv_u_no_device)
    ImageView mIvUNoDevice;

    @InjectView(R.id.fragment_store_seekBar_local)
    StoreSeekBar mSeekBarLocal;
    @InjectView(R.id.fragment_store_seekBar_SD_card)
    StoreSeekBar mSeekBarSDCard;
    @InjectView(R.id.fragment_store_seekBar_U)
    StoreSeekBar mSeekBarU;
    @InjectView(R.id.fragment_store_but_Local_save)
    ImageButton mButLocalSave;
    @InjectView(R.id.fragment_store_but_SDCard_save)
    ImageButton mButSDCardSave;
    @InjectView(R.id.fragment_store_but_U_save)
    ImageButton mButUSave;
    @InjectView(R.id.fragment_store_layout_local)
    LinearLayout mLayoutLocal;
    @InjectView(R.id.fragment_store_layout_SDCard)
    LinearLayout mLayoutSDCard;
    @InjectView(R.id.fragment_store_layout_UPan)
    LinearLayout mLayoutUPan;
    @InjectView(R.id.test_tv)
    TextView mTestTv;

    private String[] mResult;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_store, container, false);
        ButterKnife.inject(this, mView);
        registraBroadcastReceiver();

        float tvSize = BaseActivity.mSizeRatio[1] * 16;
        mTvBendi.setTextSize(tvSize);
        mTvSdCard.setTextSize(tvSize);
        mTvUPan.setTextSize(tvSize);

        mHandler.sendEmptyMessage(1);
        mButLocalSave.setOnClickListener(this);
        mButSDCardSave.setOnClickListener(this);
        mButUSave.setOnClickListener(this);


        return mView;
    }

    private void initView() {
        mResult = null;
        mResult = initVolumePaths();

        //        String paths = "";
        //        for (int i = 0; i < mResult.length; i++) {
        //            paths += "i  = " + i + "   path = " + mResult[i] + "\n";
        //        }
        //        mTestTv.setText(paths);
        //        Log.e("mytest", "paths " + paths);
        //
        //        File file = new File(mResult[2], "a.text");
        //        try {
        //            file.createNewFile();
        //        } catch (IOException e) {
        //            Log.e("mytest", "IOException " + e+"\n"+file.getPath());
        //            e.printStackTrace();
        //        }

        double lacalToatl = getLocalTotalMemorySize();
        double lacalFree = getLocalFreeMemorySize();
        if (lacalToatl > 0d) {
            int ratio = (int) ((lacalToatl - lacalFree) / lacalToatl * 100);
            mIvLocalNoDevice.setVisibility(View.GONE);
            mLayoutLocal.setVisibility(View.VISIBLE);
            mSeekBarLocal.setProgress(ratio);
            mSeekBarLocal.setText(ratio);
            mTvBendi.setText("总容量：" + getLocalTotalMemorySize() + "GB\n可用：" + getLocalFreeMemorySize() + "GB");
        } else {
            mIvLocalNoDevice.setVisibility(View.VISIBLE);
            mLayoutLocal.setVisibility(View.GONE);
        }

        double SDCardToatl = getSDCardTotalMemorySize();
        double SDCardFree = getSDCardFreeMemorySize();
        if (SDCardToatl > 0d) {
            int ratio = (int) ((SDCardToatl - SDCardFree) / SDCardToatl * 100);
            mSeekBarSDCard.setProgress(ratio);
            mSeekBarSDCard.setText(ratio);
            mTvSdCard.setText("总容量：" + SDCardToatl + "GB\n可用：" + SDCardFree + "GB");
            mLayoutSDCard.setVisibility(View.VISIBLE);
            mIvSDCardNoDevice.setVisibility(View.GONE);
        } else {
            mLayoutSDCard.setVisibility(View.GONE);
            mIvSDCardNoDevice.setVisibility(View.VISIBLE);
        }


        double UToatl = getUTotalMemorySize();
        double UFree = getUFreeMemorySize();
        if (UToatl > 0d) {
            int ratio = (int) ((UToatl - UFree) / UToatl * 100);
            mSeekBarU.setProgress(ratio);
            mSeekBarU.setText(ratio);
            mTvUPan.setText("总容量：" + UToatl + "GB\n可用：" + UFree + "GB");
            mIvUNoDevice.setVisibility(View.GONE);
            mLayoutUPan.setVisibility(View.VISIBLE);
        } else {
            mIvUNoDevice.setVisibility(View.VISIBLE);
            mLayoutUPan.setVisibility(View.GONE);
        }

        SharedPreferences sp = getActivity().getSharedPreferences(Constants.SHARE_NAME, Context.MODE_PRIVATE);
        String selectedPath = sp.getString(Constants.SHARE_01, Constants.INTERNAL_MEMORY);
        for (int j = 0; j < 3; j++) {
            if (mResult.length < (j + 1) || mResult[j] == null) {
                ImageButton imageButton = (ImageButton) mView.findViewById(R.id.fragment_store_but_Local_save + j * 5);
                imageButton.setImageResource(R.drawable.fragment_store_default_save_normal);
            } else if (mResult[j].equalsIgnoreCase(selectedPath)) {
                ImageButton imageButton = (ImageButton) mView.findViewById(R.id.fragment_store_but_Local_save + j * 5);
                imageButton.setImageResource(R.drawable.fragment_store_default_save_check);
            }
        }


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            post(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
        }
    };

    private BroadcastReceiver mBroadcastReceiver;

    private void registraBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mHandler.sendEmptyMessageDelayed(1, 1000);
            }
        };

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.setPriority(1000);// 设置最高优先级
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);// sd卡存在，但还没有挂载
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);// sd卡被移除
        //        intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);// sd卡作为 USB大容量存储被共享，挂载被解除
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);// sd卡已经从sd卡插槽拔出，但是挂载点还没解除
        //        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);// 开始扫描
        //        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);// 扫描完成
        intentFilter.addDataScheme("file");
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);

    }

    private String[] initVolumePaths() {
        StorageManager storageManager = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);
        String[] paths = null;
        try {
            Method methodGetPaths = storageManager.getClass().getMethod("getVolumePaths");
            methodGetPaths.setAccessible(true);
            paths = (String[]) methodGetPaths.invoke(storageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (paths != null && paths.length > 3) {
            String[] resultPaths = new String[3];
            resultPaths[0] = paths[0];
            resultPaths[1] = paths[1];
            for (int i = 2; i < paths.length; i++) {
                File file = new File(paths[i]);
                Log.e("mytest", "filepaths " + file.getPath());
                if (file.canRead() && file.canWrite()) {
                    resultPaths[2] = paths[i];
                    return resultPaths;
                }
            }
        }

        //        B63E-68EE     U盘
        //        964E-D2C8     SD卡

        if (paths != null && paths.length == 2) {
            if (getStoragePath(getActivity(), paths[1])) {
                String[] resultPaths = new String[3];
                resultPaths[0] = paths[0];
                resultPaths[1] = "";
                resultPaths[2] = paths[1];
                return resultPaths;
            }
        }

        return paths;
    }

    /**
     * 6.0获取外置sdcard和U盘路径，并区分
     *
     * @param mContext
     * @param keyword  SD = "内部存储"; EXT = "SD卡"; USB = "U盘"
     * @return
     */
    public boolean getStoragePath(Context mContext, String keyword) {
        String targetpath = "";
        boolean isUpan = false;
        try {
            StorageManager mStorageManager = (StorageManager) mContext
                    .getSystemService(Context.STORAGE_SERVICE);
            Class<?> storageVolumeClazz = null;
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");

            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");

            Method getPath = storageVolumeClazz.getMethod("getPath");

            Object result = getVolumeList.invoke(mStorageManager);

            final int length = Array.getLength(result);

            Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");


            for (int i = 0; i < length; i++) {

                Object storageVolumeElement = Array.get(result, i);

                String userLabel = (String) getUserLabel.invoke(storageVolumeElement);

                String path = (String) getPath.invoke(storageVolumeElement);

                //                Toast.makeText(this, "path循环 = " + path + " \n userLabel = " + userLabel, Toast.LENGTH_LONG).show();

                if (path.equals(keyword) && (userLabel.contains("U") || userLabel.contains("盘"))) {
                    isUpan = true;
                    targetpath = path;
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return isUpan;
    }


    public Double getLocalFreeMemorySize() {
        if (mResult == null || mResult.length < 1)
            return 0d;

        File file = new File(mResult[0]);
        StatFs statFs;
        try {
            statFs = new StatFs(file.getPath());
        } catch (Exception e) {
            return 0d;
        }
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getBlockCount();
        Double size = (double) (blockSize * availableBlocks);
        size = div(size, 1073741824, 2);
        return size;
    }

    public Double getLocalTotalMemorySize() {
        if (mResult == null || mResult.length < 1)
            return 0d;

        File file = new File(mResult[0]);
        StatFs statFs;
        try {
            statFs = new StatFs(file.getPath());
        } catch (Exception e) {
            return 0d;
        }
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getBlockCount();
        Double size = (double) (blockSize * availableBlocks);
        size = div(size, 1073741824, 2);
        return size;
    }

    public Double getSDCardTotalMemorySize() {
        if (mResult == null || mResult.length < 2 || TextUtils.isEmpty(mResult[1]))
            return 0d;
        File file = new File(mResult[1]);
        StatFs statFs;
        try {
            statFs = new StatFs(file.getPath());
        } catch (Exception e) {
            return 0d;
        }
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getBlockCount();
        Double size = (double) (blockSize * availableBlocks);
        size = div(size, 1073741824, 2);
        return size;
    }

    public Double getSDCardFreeMemorySize() {
        if (mResult == null || mResult.length < 2 || TextUtils.isEmpty(mResult[1]))
            return 0d;
        File file = new File(mResult[1]);
        StatFs statFs;
        try {
            statFs = new StatFs(file.getPath());
        } catch (Exception e) {
            return 0d;
        }
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        Double size = (double) (blockSize * availableBlocks);
        size = div(size, 1073741824, 2);
        return size;
    }

    public Double getUTotalMemorySize() {
        if (mResult == null || mResult.length < 3 || TextUtils.isEmpty(mResult[2]))
            return 0d;
        File file = new File(mResult[2]);
        StatFs statFs;
        try {
            statFs = new StatFs(file.getPath());
        } catch (Exception e) {
            return 0d;
        }
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getBlockCount();
        Double size = (double) (blockSize * availableBlocks);
        size = div(size, 1073741824, 2);
        return size;
    }

    public Double getUFreeMemorySize() {
        if (mResult == null || mResult.length < 3 || TextUtils.isEmpty(mResult[2]))
            return 0d;
        File file = new File(mResult[2]);
        StatFs statFs;
        try {
            statFs = new StatFs(file.getPath());
        } catch (Exception e) {
            return 0d;
        }
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        Double size = (double) (blockSize * availableBlocks);
        size = div(size, 1073741824, 2);
        return size;
    }

    public double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_store_but_Local_save:
                selectorSavePath(0, mButLocalSave);
                break;
            case R.id.fragment_store_but_SDCard_save:
                selectorSavePath(1, mButSDCardSave);
                break;
            case R.id.fragment_store_but_U_save:
                selectorSavePath(2, mButUSave);
                break;

            default:
                break;
        }
    }

    private void selectorSavePath(int index, ImageButton butSave) {
        String path = mResult[index];
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(getActivity(), "该存储路径不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!file.canRead() && !file.canWrite()) {
            Toast.makeText(getActivity(), "该存储路径不存在或无读写权限", Toast.LENGTH_SHORT).show();
            return;
        }

        // 将选中的路径写到内部存储中，以便其它下载应用读取使用
        String mntFilePath = Constants.INTERNAL_MEMORY + Constants.SHARE_DIR;
        File mmtFile = new File(mntFilePath);
        if (!mmtFile.exists()) {
            mmtFile.mkdir();
        }
        mntFilePath = mntFilePath + "/" + Constants.FILE_MNT;
        writeFileSdcardFile(mntFilePath, path);

        // 保存配置数据
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.SHARE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.SHARE_01, path);

        if (editor.commit()) {

            mButLocalSave.setImageResource(R.drawable.fragment_store_default_save_normal);
            mButSDCardSave.setImageResource(R.drawable.fragment_store_default_save_normal);
            mButUSave.setImageResource(R.drawable.fragment_store_default_save_normal);
            butSave.setImageResource(R.drawable.fragment_store_default_save_check);

        } else {
            Toast.makeText(getActivity(), "配置数据保存失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeFileSdcardFile(String fileName, String write_str) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = write_str.getBytes();
            fout.write(bytes);
            fout.close();
            //            Toast.makeText(getActivity(),"写入成功 path = "+fileName,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            //            Toast.makeText(getActivity(),"失败 path = "+fileName,Toast.LENGTH_LONG).show();
        }
    }
}
