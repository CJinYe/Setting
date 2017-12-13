package fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.icox.homebabysetting.Lwx_JNI;
import com.icox.homebabysetting.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import views.seekbar.BubbleSeekBar;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-10-28 15:54
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class SceneFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, BubbleSeekBar.OnProgressChangedListener, View.OnClickListener {
    @InjectView(R.id.fragment_scene_screen_seekBar)
    BubbleSeekBar mScreenSeekBar;
    @InjectView(R.id.fragment_scene_sleep_seekBar)
    BubbleSeekBar mSleepSeekBar;
    @InjectView(R.id.fragment_scene_volume_seekBar)
    BubbleSeekBar mVolumeSeekBar;
    @InjectView(R.id.fragment_scene_but_screen_biaozhun)
    ImageButton mFButScreenBiaozhun;
    @InjectView(R.id.fragment_scene_but_screen_huwai)
    ImageButton mButScreenHuwai;
    @InjectView(R.id.fragment_scene_but_screen_yejian)
    ImageButton mButScreenYejian;
    @InjectView(R.id.fragment_scene_but_screen_huyan)
    ImageButton mButScreenHuyan;
    @InjectView(R.id.scene_but_caideng_open)
    ImageButton mButCaidengOpen;
    @InjectView(R.id.scene_but_caideng_close)
    ImageButton mButCaidengClose;
    @InjectView(R.id.scene_layout_caideng)
    LinearLayout mLayoutCaideng;
    @InjectView(R.id.scene_layout_sleep)
    LinearLayout mSceneLayoutSleep;
    private AudioManager mAudioManager;
    private Lwx_JNI mLwxJni;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scene, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    private void initView() {
        mLwxJni = new Lwx_JNI();

        int open = mLwxJni.openGpioDev();
        int[] udata = {0x12340000, 0x56780000, 0x9abc0000, 0xdeff0000};
        int[] udata1 = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        int[] states = mLwxJni.getGpioState(16, 1, udata, udata1.length);
//        Toast.makeText(getActivity(), "open = " + open + "    chat[0] = " + states[0] + "    chat[1] = " + states[1] + "   chat[2] = " + states[2], Toast.LENGTH_LONG).show();
        mLwxJni.closeGpioDev();

        if (states[1] != 1) {
            mLayoutCaideng.setVisibility(View.GONE);
        } else if (states[2] == 0) {
            mButCaidengClose.setImageResource(R.drawable.scene_but_caideng_close_selector);
        } else if (states[2] == 1) {
            mButCaidengOpen.setImageResource(R.drawable.scene_but_caideng_opent_selector);
        }


        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mVolumeSeekBar.getConfigBuilder()
                .min(1)
                .max(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
                .build();

        mScreenSeekBar.getConfigBuilder()
                .min(1)
                .max(255)
                .build();
        mSleepSeekBar.getConfigBuilder()
                .min(1)
                .max(30)
                .build();

        mVolumeSeekBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        mScreenSeekBar.setProgress(
                getSystemBrightness());

        int sleepProgress = getScreenOffTime();
        if (mSceneLayoutSleep.getVisibility() != View.GONE)
            mSleepSeekBar.setProgress(sleepProgress);

        mScreenSeekBar.setOnProgressChangedListener(this);
        mSleepSeekBar.setOnProgressChangedListener(this);
        mVolumeSeekBar.setOnProgressChangedListener(this);

        mButScreenHuwai.setOnClickListener(this);
        mButScreenHuyan.setOnClickListener(this);
        mButScreenYejian.setOnClickListener(this);
        mFButScreenBiaozhun.setOnClickListener(this);
        mButCaidengClose.setOnClickListener(this);
        mButCaidengOpen.setOnClickListener(this);

    }


    /**
     * 获得系统亮度
     *
     * @return
     */
    private int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(
                    getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return systemBrightness;
    }

    /**
     * 设置系统亮度
     *
     * @param value
     */
    private void saveScreenBrightness(int value) {
        setScrennManualMode();
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, value);
    }

    /**
     * 设置系统调节亮度模式
     * Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC：值为1，自动调节亮度。
     * Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL：值为0，手动模式。
     */
    public void setScrennManualMode() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得休眠时间 毫秒
     */
    private int getScreenOffTime() {
        int screenOffTime = 0;
        try {
            screenOffTime = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Exception localException) {
            Toast.makeText(getActivity(), "localException = " + localException, Toast.LENGTH_LONG).show();
        }

        screenOffTime = screenOffTime / 1000 / 60;
        //        Toast.makeText(getActivity(), "getScreenOffTime() = " + screenOffTime, Toast.LENGTH_LONG).show();

        if (screenOffTime > 300)
            mSceneLayoutSleep.setVisibility(View.GONE);
        if (screenOffTime > 30)
            screenOffTime = 30;
        return screenOffTime;
    }

    /**
     * 设置休眠时间 毫秒
     */
    private void setScreenOffTime(int paramInt) {

        try {
            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,
                    paramInt * 1000 * 60);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 改变App当前Window亮度
     *
     * @param brightness
     */
    public void changeAppBrightness(int brightness) {
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.fragment_scene_screen_seekBar:
                saveScreenBrightness(progress);
                break;
            case R.id.fragment_scene_sleep_seekBar:
                setScreenOffTime(progress);
                break;
            case R.id.fragment_scene_volume_seekBar:
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                break;

            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
        switch (bubbleSeekBar.getId()) {
            case R.id.fragment_scene_screen_seekBar:
                saveScreenBrightness(progress);
                break;
            case R.id.fragment_scene_sleep_seekBar:
                setScreenOffTime(progress);
                break;
            case R.id.fragment_scene_volume_seekBar:
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                break;

            default:
                break;
        }
    }

    @Override
    public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

    }

    @Override
    public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_scene_but_screen_biaozhun:
                mScreenSeekBar.setProgress(125);
                break;
            case R.id.fragment_scene_but_screen_huwai:
                mScreenSeekBar.setProgress(255);
                break;
            case R.id.fragment_scene_but_screen_huyan:
                mScreenSeekBar.setProgress(60);
                break;
            case R.id.fragment_scene_but_screen_yejian:
                mScreenSeekBar.setProgress(1);
                break;
            case R.id.scene_but_caideng_open:
                int open = mLwxJni.openGpioDev();
                int state = mLwxJni.setGpioState(15, 1);
                int opens = mLwxJni.getGpio(15);
                mLwxJni.closeGpioDev();
                mButCaidengOpen.setImageResource(R.drawable.scene_but_caideng_opent_selector);
                mButCaidengClose.setImageResource(R.drawable.scene_but_caideng_close_normal);
                break;
            case R.id.scene_but_caideng_close:
                int close = mLwxJni.openGpioDev();
                int stateClose = mLwxJni.setGpioState(15, 0);
                int closes = mLwxJni.getGpio(15);
                mLwxJni.closeGpioDev();
                mButCaidengClose.setImageResource(R.drawable.scene_but_caideng_close_selector);
                mButCaidengOpen.setImageResource(R.drawable.scene_but_caideng_opent_normal);
                break;
            default:
                break;
        }
    }
}
