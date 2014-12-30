package com.YueXue.player.desgin;

import io.vov.utils.StringUtils;
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
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.YueXue.player.R;


public class VideoHeleper implements OnClickListener , OnCompletionListener, OnBufferingUpdateListener, OnErrorListener, OnInfoListener, OnPreparedListener{
	private Context mActivity;
	private ViewGroup mToolView;
	private VideoView vedio_view;
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

	/** 构造方法
	 * @param mActivity 需要调用的activity
	 * @param mToolView 填充的布局
	 */
	public VideoHeleper(Context mActivity, ViewGroup mToolView)
	{
		this.mActivity = mActivity;
		this.mToolView = mToolView;
		initView(mToolView);
	}

	private void initView(ViewGroup view) {
		seekBar = (SeekBar) view.findViewById(R.id.seekBar);
		fLayout = (FrameLayout) view.findViewById(R.id.title_fl);
		fLayout.setVisibility(View.GONE);
		layout = (LinearLayout)view. findViewById(R.id.buttom_lin);
		layout.setVisibility(View.GONE);
		videoView = (VideoView)view. findViewById(R.id.surface_view);
		imageView = (ImageView)view. findViewById(R.id.image_lock);
		imageView.setOnClickListener(this);
		imageView.setVisibility(View.GONE);
		sudu = (TextView) view.findViewById(R.id.sudu);
		sudu2 = (TextView) view.findViewById(R.id.sudu2);
		bar = view.findViewById(R.id.pb);
		textViewBF = (TextView) bar.findViewById(R.id.buff);
		play_tiem = (TextView) view.findViewById(R.id.play_time);
		endTime = (TextView) view.findViewById(R.id.play_end_time);
		nametTextView = (TextView)view. findViewById(R.id.movie_name);
		nametTextView.setOnClickListener(this);
		timeTextView = (TextView)view. findViewById(R.id.movie_time);
		timeString = (new Timestamp(System.currentTimeMillis())).toString()
				.substring(11, 16);
		timeTextView.setText(timeString);
		chiceButton = (Button)view. findViewById(R.id.zhiliang);
		chiceButton.setOnClickListener(this);
		fontbButton = (Button) view.findViewById(R.id.font);
		fontbButton.setOnClickListener(this);
		playbButton = (Button)view. findViewById(R.id.play);
		playbButton.setOnClickListener(this);
		nextButton = (Button)view. findViewById(R.id.next);
		nextButton.setOnClickListener(this);

		mVolumeBrightnessLayout = view.findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) view.findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) view.findViewById(R.id.operation_percent);
		mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		setListener();
		update = new upDateSeekBar();
		new Thread(update).start();
	}

	/**
	 * 控制条播放
	 */
	private void setListener() {
		
	}

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

	protected void seekBar(long currentPosition) {
		if (videoView.isPlaying()) {
			long mMax = videoView.getDuration();
			int sMax = seekBar.getMax();
			seekBar.setProgress((int) (size * sMax / mMax));

		}
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		
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
	public  void onPause(){
		videoView.pause();
	}
	public  void onDestroy(){
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
	public  void onResume(){
		if (isPlay) {
			videoView.start();
		}
	}
}
