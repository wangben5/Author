package com.shadt.ui.widget;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.os.IBinder;

public class MediaPlayerService extends Service {

	/**
	 * ���岥�ŷ���Ķ���action
	 */
	public static final String PLAY_CMD_ACTION = "play_cmd_action";
	public static final String PAUSE_CMD_ACTION = "pause_cmd_action";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * �������ŷ���MediaPlayer
	 */
	private MediaPlayer mMediaPlayer;

	@Override
	public void onCreate() {

		super.onCreate();

		// mMediaPlayer = MediaPlayer.create(this,
		// Uri.parse("file:///" + SD_ROOT + "/music.mp3"));

		mMediaPlayer = new MediaPlayer();

		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

			}
		});
		// ����reset�������ò���ʵ��

		mMediaPlayer.reset();

		// ����setDataSource�������ò��Ÿ�����·��
		try {
			mMediaPlayer.setDataSource(SD_ROOT + "/music.mp3");
			// ����prepare�����ò��Ŷ���ʵ��Ԥ����
			mMediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ����һ��������ȡ��ǰSD���ĸ�Ŀ¼
	 */
	public static final String SD_ROOT = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	@Override
	public void onStart(Intent intent, int startId) {

		String cmd = intent.getStringExtra("CMD");
		if (PLAY_CMD_ACTION.equals(cmd)) {
			play();
		} else if (PAUSE_CMD_ACTION.equals(cmd)) {
			pause();
		}

		super.onStart(intent, startId);
	}

	private void play() {
		mMediaPlayer.start();// ����start��������

	}

	private void pause() {

		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();// ����pause������ͣ
		}

	}
}
