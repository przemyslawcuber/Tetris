package com.example.tetris;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint({ "ClickableViewAccessibility", "InlinedApi" })
public class MainActivity extends Activity {
	
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

        // set our renderer to be the main renderer with
        // the current activity context

		//setContentView(gameView);
		setContentView(R.layout.activity_main);
		
		Button startButton = (Button) findViewById(R.id.startbutton);
		startButton.setOnClickListener( new OnClickListener() {
		            public void onClick(View v) {
		            	Intent intent = new Intent(MainActivity.this, GameActivity.class);

//		                intent.putExtra(EXTRA_MESSAGE, message);
		                startActivity(intent);

		            }
		        });
		Button scoreButton = (Button) findViewById(R.id.score);
		scoreButton.setOnClickListener( new OnClickListener() {
		            public void onClick(View v) {
		            	Intent intent = new Intent(MainActivity.this, ScoreActivity.class);

//		                intent.putExtra(EXTRA_MESSAGE, message);
		                startActivity(intent);

		            }
		        });

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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}