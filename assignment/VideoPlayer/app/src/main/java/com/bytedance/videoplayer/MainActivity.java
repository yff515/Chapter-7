package com.bytedance.videoplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bytedance.videoplayer.player.VideoPlayerIJK;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bytedance.videoplayer.player.VideoPlayerIJK;
import com.bytedance.videoplayer.player.VideoPlayerListener;

import java.lang.ref.WeakReference;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String TAG2 = "MainActivity2";
    public VideoPlayerIJK ijkPlayer;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private int state=0;
    TextView totalTime;
    SeekBar seekBar;
    private TimerHandler mHandler;
    public VideoPlayerIJK ijkPlayer2;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {//要权限
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 当前为横屏
            Log.d(TAG2, "onConfigurationChanged() called with: newConfig = [" + newConfig + "]");
            //LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ijkPlayer.getLayoutParams();
            // 取控件aaa当前的布局参数
            //linearParams.height = 150;        // 当控件的高强制设成150象素
            //linearParams.weight = 300;
            //ijkPlayer.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG2, "onConfigurationChanged()2 called with: newConfig = [" + newConfig + "]");
            //LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ijkPlayer.getLayoutParams();
            // 取控件aaa当前的布局参数
            //linearParams.height = 150;        // 当控件的高强制设成150象素
            //linearParams.weight = 300;
            //ijkPlayer.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
        }

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = findViewById(R.id.imageView);
        TextView totalTime = findViewById(R.id.totalTime);
        String url = "https://s3.pstatp.com/toutiao/static/img/logo.271e845.png";//今日头条logo
        Glide.with(this).load(url).into(imageView);

        ijkPlayer=findViewById(R.id.ijkPlayer);
        ijkPlayer2=findViewById(R.id.ijkPlayer2);



        //加载native库 就默认是固定搭配吧
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        ijkPlayer.setListener(new VideoPlayerListener());//设置监听
        ijkPlayer.setVideoResource(R.raw.bytedance);
//        ijkPlayer.setVideoPath(getVideoPath());

        findViewById(R.id.buttonPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state==0)
                {
                    ijkPlayer.start();
                    state=1;
                }
                else
                {
                    ijkPlayer.pause();
                    state=0;
                }
            }
        });

        findViewById(R.id.buttonSeek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ijkPlayer.seekTo(20 * 1000);
            }
        });

        long duration = ijkPlayer.getDuration();
        System.out.println(duration);

        seekBar = findViewById(R.id.seekBar);
        //seekBar.setProgress();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            long newposition;
            boolean mDragging=true;

            TextView totalTime = findViewById(R.id.totalTime);
            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {

                long duration=ijkPlayer.getDuration();
                totalTime.setText(String.valueOf(Math.round(progress*1.0/100.0*duration/100.0)/10.0)+"/"+String.valueOf(Math.round(duration/100.0)/10.0));

                newposition = (duration*progress)/100L;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ijkPlayer.seekTo(newposition);
            }
        });

        mHandler = new TimerHandler(this.seekBar);
        //getTime();
        mHandler.sendEmptyMessageDelayed(1, 1000);

    }

    private final class TimerHandler extends Handler {
        private WeakReference<SeekBar> seekBarWeakReference;
        private TimerHandler(SeekBar seekBar){
            seekBarWeakReference = new WeakReference<>(seekBar);
        }

        @Override
        public void handleMessage(Message msg){
            Log.d(TAG, "handleMessage() called with: msg = [" + msg + "]");
            SeekBar seekBar = seekBarWeakReference.get();
            if(seekBar!=null){
                seekBar.setProgress((int)(ijkPlayer.getCurrentPosition()*1.0/ijkPlayer.getDuration()*100.0));//小心前面结果为零
                //totalTime.setText(String.valueOf((ijkPlayer.getCurrentPosition()/ijkPlayer.getDuration()*100)));
                sendEmptyMessageDelayed(1,1000);
            }
        }
    }
}
