package com.example.tetris;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;

public class AlertManager {

	public static final int TYPE_GAME_OVER = 0;
	public static final int TYPE_TOP_SCORE = 1;

	private static AlertDialog.Builder alert;
	private static Context _context;
	private static Activity _activity; 
	
	private static int _score;
	private static ScoreManager _scoreManager;

	AlertManager(Context context, Activity activity, ScoreManager scoreManager) {
		alert = new AlertDialog.Builder(context);
		alert.setCancelable(false);
		_context = context;
		_activity = activity;
		_scoreManager = scoreManager;
	}

	public static void PushAlert(final int type, int score) {
		_score = score;
		String title = "", msg = "", button_name = "";
		final EditText input = new EditText(_context);
		switch (type) {
		case TYPE_GAME_OVER:
			alert.setTitle("Game Over!");
			alert.setMessage("There is no more room for the new piece, you have lost.");
			button_name = "Back to menu";
			break;
		case TYPE_TOP_SCORE:
			alert.setTitle("Congratulations!");
			alert.setMessage("Your score has made it to the top score");
			alert.setView(input);
			button_name = "Zapisz";
			break;
		}
		
		alert.setPositiveButton(button_name,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (type == TYPE_GAME_OVER) {
							// przejdz do menu
							Intent intent = new Intent(_context, MainActivity.class);

//			                intent.putExtra(EXTRA_MESSAGE, message);
			                _context.startActivity(intent);
							
						} else if (type == TYPE_TOP_SCORE) {
							// zapisz do bazy i przejdz do aktywności score
							String player = input.getText().toString();
							if (player.equals("")) {
								AlertManager.PushAlert(TYPE_TOP_SCORE, _score);
								return;
							}
							// Do something with value!
							_scoreManager.saveScore(player, _score);
							Intent intent = new Intent(_context, ScoreActivity.class);

//			                intent.putExtra(EXTRA_MESSAGE, message);
			                _context.startActivity(intent);
						}
					}
				});

		alert.show();
	}
}
