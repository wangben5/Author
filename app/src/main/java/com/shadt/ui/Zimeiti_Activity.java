package com.shadt.ui;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureExternalPreviewActivity;
import com.shadt.caibian_news.R;
import com.shadt.ui.db.MediaInfo;
import com.shadt.ui.download.DownloadListener;
import com.shadt.ui.download.DownloadManager;
import com.shadt.ui.download.DownloadTask;
import com.shadt.util.MyLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.jzvd.JZVideoPlayerStandard;
import io.rong.imageloader.core.ImageLoader;

import static com.shadt.ui.download.DownloadState.STATE_FAILED;
import static com.shadt.ui.download.DownloadState.STATE_FINISHED;
import static com.shadt.ui.download.DownloadState.STATE_PAUSED;
import static com.shadt.ui.download.DownloadState.STATE_PREPARED;
import static com.shadt.ui.download.DownloadState.STATE_WAITING;

public class Zimeiti_Activity extends BaseActivity implements Chronometer.OnChronometerTickListener, SeekBar.OnSeekBarChangeListener {
    TextView t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11;
    ImageView img;
    MediaInfo mMediaInfo;
    Context mContext;
    int position;
    TextView txt_changge;
    private SeekBar sb;
    private ImageView btn;

    JZVideoPlayerStandard video_player;

    private long subtime = 0, beginTime = 0, falgTime = 0, pauseTime = 0;
    RelativeLayout rela_aud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_zimeiti);
        initPages();
    }

    LinearLayout line_back;
    TextView title;
    private MediaPlayer mediaPlayer;
    private TelephonyManager manager;
    boolean is_play = false;
    private List<DownloadTask> tasks;
    DownloadManager controller;
    DownloadTask task;
    ScrollView mscroll;
    public String txt_content;
    public void initPages() {

        mContext = Zimeiti_Activity.this;
        tasks = new ArrayList<>();
        controller = DownloadManager.getInstance();
        txt_changge = findViewById(R.id.txt_changge);
        txt_changge.setVisibility(View.VISIBLE);
        txt_changge.setText("下载");

        txt_changge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(7))) {
                    if (mMediaInfo.getDATA().get(position).get(8).equals("txt")) {
                        Intent it=new Intent(mContext,Edite_txt_Activity.class);
                        it.putExtra("name",mMediaInfo.getDATA().get(position).get(1));

                        it.putExtra("new",false);
                        it.putExtra("context",txt_content);
                        startActivity(it);
                    }else{
                        task.start();
                    }
                }else{
                    task.start();
                }

            }
        });
        sb = (SeekBar) findViewById(R.id.seek);
        btn = (ImageView) findViewById(R.id.btn);
        manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        manager.listen(new MyListener(), PhoneStateListener.LISTEN_CALL_STATE);
        sb.setEnabled(false);
        sb.setOnSeekBarChangeListener(this);
        mscroll = findViewById(R.id.mscroll);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_play == true) {

                    pause();
                } else {
                    try {
                        play(mMediaInfo.getDATA().get(position).get(22));
                        pauseTime = 0;
                        btn.setImageResource(R.drawable.video_pauseer);
                        is_play = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "文件播放出现异常", 0).show();
                    }
                }
            }
        });
        img = (ImageView) findViewById(R.id.img);
        t0 = (TextView) findViewById(R.id.t0);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        t5 = (TextView) findViewById(R.id.t5);
        t6 = (TextView) findViewById(R.id.t6);
        t7 = (TextView) findViewById(R.id.t7);
        t8 = (TextView) findViewById(R.id.t8);
        t9 = (TextView) findViewById(R.id.t9);
        t10 = (TextView) findViewById(R.id.t10);
        t11 = (TextView) findViewById(R.id.t11);
        video_player = (JZVideoPlayerStandard) findViewById(R.id.video_player);
        line_back = (LinearLayout) findViewById(R.id.line_back);
        title = (TextView) findViewById(R.id.title);
        title.setText("详情");
        rela_aud = (RelativeLayout) findViewById(R.id.rela_aud);
        line_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(23))) {
                    Intent intent = new Intent(mContext, PictureExternalPreviewActivity.class);
                    intent.putExtra("img_path", mMediaInfo.getDATA().get(position).get(23));
                    startActivity(intent);
                }
            }
        });
        initDatas();
    }

    @Override
    public void onClickListener(View v) {

    }

    public void initDatas() {


        mMediaInfo = (MediaInfo) getIntent().getSerializableExtra("content");
        position = getIntent().getIntExtra("position", 0);
        MyLog.i("id" + mMediaInfo.getDATA().get(position).get(30));
        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(1))) {
            t0.setText("名称:" + mMediaInfo.getDATA().get(position).get(1));
        }

        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(33))) {
            t2.setText("时间:" + mMediaInfo.getDATA().get(position).get(33));
        }
        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(18))) {
            t3.setText("关键字:" + mMediaInfo.getDATA().get(position).get(18));
        }
        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(17))) {
            t4.setText("文件描述:" + mMediaInfo.getDATA().get(position).get(17));
        }
        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(0))) {
            t5.setText("文件编号:" + mMediaInfo.getDATA().get(position).get(0));
        }
        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(12))) {

            t6.setText("文件大小:" + getFileSize(mMediaInfo.getDATA().get(position).get(12)));
        }
        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(3))) {
            t7.setText("文件夹名称:" + mMediaInfo.getDATA().get(position).get(3));
        }
        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(8))) {
            t8.setText("扩展文件格式:" + mMediaInfo.getDATA().get(position).get(8));
        }
        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(14))) {
            t9.setText("文件宽:" + mMediaInfo.getDATA().get(position).get(14));
        }
        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(15))) {
            t10.setText("文件高:" + mMediaInfo.getDATA().get(position).get(15));
        }
       /* if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(12))) {
            t11.setText("文件大小:" + getFileSize(mMediaInfo.getDATA().get(position).get(12)));
        }*/

        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(23))) {
            ImageLoader.getInstance().displayImage(mMediaInfo.getDATA().get(position).get(23), img);
        }
        MyLog.i(">"+ mMediaInfo.getDATA().get(position).get(22));
        MyLog.i(">"+ mMediaInfo.getDATA().get(position).get(23));
        tasks.add(controller.newTask(mMediaInfo.getDATA().get(position).get(0), mMediaInfo.getDATA().get(position).get(22), mMediaInfo.getDATA().get(position).get(1)).extras(mMediaInfo.getDATA().get(position).get(23)).create());
        task = tasks.get(0);
        task.setListener(new DownloadListener() {
            @Override
            public void onStateChanged(String key, int state) {
                switch (state) {
                    case STATE_FAILED:
                        //重试
//                download.setText(R.string.download_retry);
                        Toast.makeText(mContext, "下载失败", 0).show();
                        break;
                    case STATE_PREPARED:

                        txt_changge.setText("下载");
                        //下载
//                download.setText(R.string.label_download);
                        break;
                    case STATE_PAUSED:
                        //继续
//                download.setText(R.string.download_resume);
                        break;
                    case STATE_WAITING:
                        //等待下载
//                download.setText(R.string.download_wait);
                        break;
                    case STATE_FINISHED:
                        //下载完成


                        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(7))) {
                            if (mMediaInfo.getDATA().get(position).get(8).equals("txt")) {
                                t11.setText("" + getString(task.path));
                                txt_changge.setText("编辑");
                            }else{
                                txt_changge.setText("已经下载");
                            }
                        }else{
                            txt_changge.setText("已经下载");
                        }
                        break;

                }
            }

            @Override
            public void onProgressChanged(String key, long finishedLength, long contentLength) {
                MyLog.i("下载进度" + String.format(Locale.US, "%.1fMB", contentLength / 1048576.0f));
                txt_changge.setText(String.format(Locale.US, "%.1f%%", finishedLength * 100.f / Math.max(contentLength, 1)));
                if (contentLength == 0) {
                } else {
                    MyLog.i("文件大小" + String.format(Locale.US, "%.1fMB", contentLength / 1048576.0f));
                }
            }

        });
        if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(position).get(7))) {
            if (mMediaInfo.getDATA().get(position).get(7).equals("img")) {
                t1.setText("文件类型:图片");
                img.setVisibility(View.VISIBLE);
            } else if (mMediaInfo.getDATA().get(position).get(7).equals("vid")) {
                t1.setText("文件类型:视频");
                video_player.setVisibility(View.VISIBLE);

                //JC 视频播放的URl 地址 的方法是用到了setUP( URL , )
                video_player.setUp(mMediaInfo.getDATA().get(position).get(22)
                        , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
            } else if (mMediaInfo.getDATA().get(position).get(7).equals("aud")) {
                t1.setText("文件类型:音频");
                rela_aud.setVisibility(View.VISIBLE);
            } else {
                mscroll.setVisibility(View.VISIBLE);
                txt_changge.setText("编辑");
                t1.setText("文件类型:文稿");
                task.start();
            }
        }
    }

    //读取文本
    public String getString(String name) {


        StringBuffer sb = new StringBuffer("");
        try {

            File filename = new File(name); // 要读取以上路径的input。txt文件
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "GBK"));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        MyLog.i("sb:" + sb.toString());
        txt_content=sb.toString();
        return sb.toString();
    }

    private String getFileSize(String s) {

        long size = Long.valueOf(s).longValue();


        return new DecimalFormat("0.00").format((double) (size / 1024f) / 1024f) + "MB";

    }

    @Override
    public void onBackPressed() {
        if (video_player.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        video_player.releaseAllVideos();
    }


    Handler handler = new Handler();
    Runnable updateThread = new Runnable() {
        public void run() {
            // 获得歌曲现在播放位置并设置成播放进度条的值
            if (mediaPlayer != null) {
                sb.setProgress(mediaPlayer.getCurrentPosition());
                // 每次延迟100毫秒再启动线程
                handler.postDelayed(updateThread, 100);
            }
        }
    };

    private void pause() {
        // 判断音乐是否在播放
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            // 暂停音乐播放器
            mediaPlayer.pause();

            btn.setImageResource(R.drawable.video_player);

            is_play = true;
            sb.setEnabled(false);
            pauseTime = SystemClock.elapsedRealtime();
            // System.out.println("1 pauseTime" + pauseTime);
        } else if (mediaPlayer != null
                && is_play == true) {

            subtime += SystemClock.elapsedRealtime() - pauseTime;
            // System.out.println("2 subtime:" + subtime);
            btn.setImageResource(R.drawable.video_pauseer);
            mediaPlayer.start();

            sb.setEnabled(true);
            beginTime = falgTime + subtime;
            // System.out.println("3 beginTime" + beginTime);

        }
    }

    /**
     * 播放指定地址的音乐文件 .mp3 .wav .amr
     *
     * @param path
     */
    private void play(String path) throws Exception {
        if ("".equals(path)) {

            return;
        }


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(path);
        // mediaPlayer.prepare(); // c/c++ 播放器引擎的初始化
        // 同步方法
        // 采用异步的方式
        mediaPlayer.prepareAsync();
        // 为播放器注册
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mediaPlayer.start();
                sb.setMax(mediaPlayer.getDuration());
                handler.post(updateThread);
                sb.setEnabled(true);
            }
        });

        // 注册播放完毕后的监听事件
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                mediaPlayer = null;
                btn.setImageResource(R.drawable.video_player);
                sb.setProgress(0);
                sb.setEnabled(false);
                is_play = false;
            }
        });
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                switch (i) {

                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        // if(Utils.getOSVersionSDKINT(VideoPlayer.this)>=9){
                        btn.setImageResource(R.drawable.video_player);
                        // }

                        break;

                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        btn.setImageResource(R.drawable.video_pauseer);
                        // }

                        break;


                }
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            handler.removeCallbacks(updateThread);
        }
    }


    private class MyListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // 音乐播放器暂停
                    pause();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // 重新播放音乐
                    pause();
                    break;
            }
        }
    }

    public void onChronometerTick(Chronometer chronometer) {

    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO 自动生成的方法存根
        if (fromUser == true && mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
            falgTime = SystemClock.elapsedRealtime() - sb.getProgress();
            subtime = 0;
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO 自动生成的方法存根
    }


}
