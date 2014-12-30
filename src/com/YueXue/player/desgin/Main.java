package com.YueXue.player.desgin;

import com.YueXue.player.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

public class Main extends Activity {
	private ViewGroup custvideo;
	private VideoHeleper heleper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		custvideo = (ViewGroup) findViewById(R.id.custvideo);
		heleper = new VideoHeleper(Main.this, custvideo);
	}
	@Override
	protected void onPause() {
		super.onPause();
		heleper.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		heleper.onDestroy();
	}
	@Override
	protected void onResume() {
		super.onResume();
		heleper.onResume();
	}
}
