package com.YueXue.player;


import io.vov.utils.StringUtils;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;

import java.sql.Timestamp;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnCompletionListener, OnBufferingUpdateListener, OnErrorListener, OnInfoListener, OnPreparedListener {
	private String path;
	/**
	 * 加锁
	 */
	private ImageView imageView;
	private TextView nametTextView;
	/**
	 * 电影时间
	 */
	private TextView timeTextView;
	/**
	 * 画面选择
	 */
	private Button chiceButton;
	/**
	 * 快退，播放 快进
	 */
	private Button fontbButton, playbButton, nextButton;
	/**
	 * 视频界面
	 */
	private VideoView videoView;
	/**
	 * 主界面
	 */
	private FrameLayout fLayout;
	/**
	 * 底部栏
	 */
	private LinearLayout layout;
	private boolean isPlay;
	/**
	 * 播放进度
	 */
	private SeekBar seekBar;
	private upDateSeekBar update;
	private boolean isLock;
	private String timeString;
	/**
	 * 开始结束时间
	 */
	private TextView play_tiem, endTime;
	private String name;
	private ImageView mOperationBg;
	/**
	 * 加载的底层
	 */
	private ImageView mOperationPercent;
	/**
	 * 加载中的布局
	 */
	private View mVolumeBrightnessLayout;
	private AudioManager mAudioManager;
	private int mMaxVolume;
	private int mVolume = -1;
	private float mBrightness = -1f;
	private GestureDetector mGestureDetector;
	/**
	 * 加载中
	 */
	private View bar;
	private TextView textViewBF;
	/**
	 * 加载速度
	 */
	private TextView sudu;
	private TextView sudu2;
	private PopupWindow pWindow;
	private Button qingxiButton, liuchangButton, gaoqingButton;
    
	private boolean isFinish;
	private long pauseSize;
	private long size;
	private ArrayList<String> urls;
    private static int flag;
	String url1="http://f0.r.56.com/f0.c198.56.com/flvdownload/5/25/141973886376hd_qqvga.mp4?v=1&t=noN0UrsuiPJLugI3msldFA&r=39048&e=1419905321&tt=15&sz=181528&vid=133548030";
	String url2="http://f0.r.56.com/f0.c197.56.com/flvdownload/3/11/141973886376hd.flv.mp4?v=1&t=WBoyfKehYzJ5YqFREwpdmA&r=39048&e=1419905321&tt=15&sz=589985&vid=133548030";
	String url3="http://f0.r.56.com/f0.c197.56.com/flvdownload/3/11/141973886376hd.flv.m3u8?v=1&t=F1CiCWTFsYPqOjo8CXwVSw&r=39048&e=1419905321&tt=0&sz=0&vid=133548030";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		initUrl();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		initView();
		nextButton.setEnabled(false);
		fontbButton.setEnabled(false);
		play();
	}
	private void play() {

		Log.i("hck", "play flag1:  "+urls.get(flag));
		isPlay = true;
		try {
			videoView.setVideoURI(Uri.parse(urls.get(flag)));
			videoView.setOnCompletionListener(this);
			videoView.setOnBufferingUpdateListener(this);
			videoView.setOnErrorListener(this);
			videoView.setOnInfoListener(this);
		   videoView.setOnPreparedListener(this);
			
		} catch (Exception e) {
			Log.i("hck", "PlayActivity " + e.toString());
		}
	
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		fLayout = (FrameLayout) findViewById(R.id.title_fl);
		fLayout.setVisibility(View.GONE);
		layout = (LinearLayout) findViewById(R.id.buttom_lin);
		layout.setVisibility(View.GONE);
		videoView = (VideoView) findViewById(R.id.surface_view);
		imageView = (ImageView) findViewById(R.id.image_lock);
		imageView.setOnClickListener(this);
		imageView.setVisibility(View.GONE);
		sudu = (TextView) findViewById(R.id.sudu);
		sudu2 = (TextView) findViewById(R.id.sudu2);
		bar = findViewById(R.id.pb);
		textViewBF = (TextView) bar.findViewById(R.id.buff);
		play_tiem = (TextView) findViewById(R.id.play_time);
		endTime = (TextView) findViewById(R.id.play_end_time);
		nametTextView = (TextView) findViewById(R.id.movie_name);
		nametTextView.setOnClickListener(this);
		timeTextView = (TextView) findViewById(R.id.movie_time);
		timeString = (new Timestamp(System.currentTimeMillis())).toString()
				.substring(11, 16);
		timeTextView.setText(timeString);
		chiceButton = (Button) findViewById(R.id.zhiliang);
		chiceButton.setOnClickListener(this);
		fontbButton = (Button) findViewById(R.id.font);
		fontbButton.setOnClickListener(this);
		playbButton = (Button) findViewById(R.id.play);
		playbButton.setOnClickListener(this);
		nextButton = (Button) findViewById(R.id.next);
		nextButton.setOnClickListener(this);

		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setListener();
		update = new upDateSeekBar();
		new Thread(update).start();
	}
	class upDateSeekBar implements Runnable {

		@Override
		public void run() {
			if (!isFinish) {
				mHandler.sendMessage(Message.obtain());
				mHandler.postDelayed(update, 1000);
			}

		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (videoView == null) {
				return;
			}
			timeString = (new Timestamp(System.currentTimeMillis())).toString()
					.substring(11, 16);
			timeTextView.setText(timeString);
			play_tiem.setText(StringUtils.generateTime(videoView
					.getCurrentPosition()));
			if (videoView != null) {
				seekBar(videoView.getCurrentPosition());
			}

		};
	};
	private void seekBar(long size) {
		if (videoView.isPlaying()) {
			long mMax = videoView.getDuration();
			int sMax = seekBar.getMax();
			seekBar.setProgress((int) (size * sMax / mMax));

		}
	}

	/**
	 * 控制条控制
	 */
	private void setListener() {

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				int value = (int) (seekBar.getProgress()
						* videoView.getDuration() / seekBar.getMax());
				videoView.seekTo(value);
				videoView.start();
				isPlay = true;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				isPlay = false;
				videoView.pause();
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});

	
	}





	/**
	 * 组装URL
	 */
	private void initUrl() {
		urls=new ArrayList<String>();
		urls.add(url1);
		urls.add(url2);
		urls.add(url3);
	}
	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (mOldX > windowWidth * 4.0 / 5)
				onVolumeSlide((mOldY - y) / windowHeight);
			else if (mOldX < windowWidth / 5.0)
				onBrightnessSlide((mOldY - y) / windowHeight);

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}
	/**
	 * 减少亮度
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

			mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
		mOperationPercent.setLayoutParams(lp);
	}
	/**
	 * 减少声音
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

				ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width
				* index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}



//**********************************点击事件处理****************************
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play:
			if (isPlay) {
				videoView.pause();
				playbButton
						.setBackgroundResource(R.drawable.player_play_highlight);
				isPlay = false;
				seekBar.setEnabled(false);
			} else {
				playbButton
						.setBackgroundResource(R.drawable.player_pause_highlight);
				videoView.start();
				isPlay = true;
				seekBar.setEnabled(true);
			}
			break;
		case R.id.image_lock:
			if (isLock) {
				isLock = false;
				imageView.setBackgroundResource(R.drawable.lock_off);
			} else {
				isLock = true;
				imageView.setBackgroundResource(R.drawable.lock_on);
			}
			break;
		case R.id.next:
			videoView.pause();
			if (videoView!=null) {
				size=videoView.getCurrentPosition()+videoView.getDuration()/10;
				videoView.seekTo(size);
				videoView.start();
			}
			break;
		case R.id.font:
			videoView.pause();
			if (videoView!=null) {
				size=videoView.getCurrentPosition()-videoView.getDuration()/10;
				videoView.seekTo(size);
				videoView.start();
			}
			break;
		case R.id.zhiliang:
//			showPop(v);
			break;
		
			
		}
	}
	private void showPop(View v) {
	
	}
	//**********************************点击事件处理****************************
	@Override
	public void onPrepared(MediaPlayer arg0) {
		nextButton.setEnabled(true);
		fontbButton.setEnabled(true);
		bar.setVisibility(View.GONE);
		if (urls.get(flag).startsWith("http")) {
			videoView.setBufferSize(1024 * 1000);
		} else {
			videoView.setBufferSize(0);
		}
		endTime.setText(StringUtils.generateTime(videoView.getDuration()));
		bar.setVisibility(View.GONE);
		if (pauseSize > 0) {
			videoView.seekTo(pauseSize);
		}
		pauseSize = 0;
		
	}
	@Override
	public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
		switch (arg1) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			if (isPlay) {
				bar.setVisibility(View.VISIBLE);
				videoView.pause();
				isPlay = false;

			}
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (!isPlay) {
				bar.setVisibility(View.GONE);
				videoView.start();
				isPlay = true;

			}
			break;
		case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
			sudu.setTextColor(Color.CYAN);
			sudu.setText(arg2 + "kb/s");
			sudu2.setText(arg2 + "kb/s");
			break;
		}
		return true;
	}
	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		return false;
	}
	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
		textViewBF.setTextColor(Color.CYAN);
		textViewBF.setText(videoView.getBufferPercentage() + "%");
	}
	@Override
	public void onCompletion(MediaPlayer arg0) {
		videoView.stopPlayback();
		
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		videoView.setVideoLayout(3, 0);
	}
	@Override
	protected void onPause() {
		super.onPause();
		videoView.pause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isPlay) {
			videoView.start();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (videoView != null) {
			videoView.stopPlayback();
			videoView = null;
		}
		if (pWindow != null && pWindow.isShowing()) {
			pWindow.dismiss();
		}
		isPlay = false;
		isFinish = true;
		System.gc();

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!isLock) {
				fLayout.setVisibility(View.VISIBLE);
				layout.setVisibility(View.VISIBLE);

			}
			imageView.setVisibility(View.VISIBLE);
			break;
		case MotionEvent.ACTION_UP:
			endGesture();
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;

		disHandler.removeMessages(0);
		disHandler.sendEmptyMessageDelayed(0, 1000);
		disHandler.removeMessages(1);
		disHandler.sendEmptyMessageDelayed(1, 5000);
	}
	private Handler disHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				mVolumeBrightnessLayout.setVisibility(View.GONE);
			} else {
				fLayout.setVisibility(View.GONE);
				layout.setVisibility(View.GONE);
				imageView.setVisibility(View.GONE);
				if (pWindow != null && pWindow.isShowing()) {
					pWindow.dismiss();
				}
			}

		}
	};
}
