package com.example.tetris;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint({ "ClickableViewAccessibility", "InlinedApi" })
public class GameActivity extends Activity {

	private GameView gameView;
	private GameRenderer gameRenderer;
	
	@SuppressLint({ "ClickableViewAccessibility", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initiate the Open GL view and
        // create an instance with this activity
		gameView = new GameView(this);
		gameRenderer = new GameRenderer(this, new GameActivity());

        // set our renderer to be the main renderer with
        // the current activity context
		gameView.setRenderer(gameRenderer);
		gameView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, final MotionEvent event) {
				if (event != null) {					
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						gameView.queueEvent(new Runnable() {
							@Override
							public void run() {
								gameRenderer.handleTouchPress(event.getX(), event.getY());
							}
						});
					}
					return true;
				} else {
					return false;
				}
			}
		});
		
		setContentView(gameView);

		//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//int id = item.getItemId();
		//if (id == R.id.action_settings) {
		//	return true;
		//}
		//return super.onOptionsItemSelected(item);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		gameView.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		gameView.onPause();
	}
/*
	@Override
	protected void onStop() {
		super.onStop();
		//gameRenderer.pause();
	}
	*/
}