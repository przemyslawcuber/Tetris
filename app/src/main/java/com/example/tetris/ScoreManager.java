package com.example.tetris;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Comment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoreManager {

	private static final String DATABASE_TABLE_SCORES = "scores";
	private static final String DATABASE_TABLE_SCORES_ID = "id";
	public static final String DATABASE_TABLE_SCORES_NAME = "name";
	public static final String DATABASE_TABLE_SCORES_SCORE = "score";
	public static final int TOP_SCORE_NB = 10;

	public int currentScore;
	private Context _context;
	private ScoreDBHelper helper;
	private SQLiteDatabase database;
	public boolean scoreWasSaved;

	public ScoreManager(Context context) {
		_context = context;
		helper = new ScoreDBHelper(_context);
		//database = helper.getWritableDatabase();
		database = helper.getReadableDatabase();
	}

	public Cursor getTopScores() {
		return database.query(DATABASE_TABLE_SCORES, new String[] {
				DATABASE_TABLE_SCORES_NAME, DATABASE_TABLE_SCORES_SCORE },
				null, null, null, null, DATABASE_TABLE_SCORES_SCORE+" DESC");
	}

	public boolean isTopScore(int score) {
		database = helper.getReadableDatabase();
		boolean ret = false;
		Cursor cursor = getTopScores();

		if (cursor.getCount() >= TOP_SCORE_NB) {
			cursor.moveToLast();
			if (score > cursor.getInt(cursor.getColumnIndex(DATABASE_TABLE_SCORES_SCORE))){
				ret = true;
			}
		}
		else
			ret = true;
		cursor.close();
		return ret;
	}

	public long saveScore(String player, int score) {
		database = helper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_TABLE_SCORES_NAME, player);
		initialValues.put(DATABASE_TABLE_SCORES_SCORE, score);
		Cursor cursor = getTopScores();
		if (cursor.getCount() >= TOP_SCORE_NB)
			removeLastRow();
		return database.insert(DATABASE_TABLE_SCORES, null, initialValues);
	}
	
	public void removeLastRow() {
		database = helper.getWritableDatabase();
		Cursor cursor = database.query(DATABASE_TABLE_SCORES, allColumns,
				null, null, null, null, DATABASE_TABLE_SCORES_SCORE+" DESC");
		cursor.moveToLast();
		int id = cursor.getColumnIndex(DATABASE_TABLE_SCORES_ID);
		
		database.delete(DATABASE_TABLE_SCORES, DATABASE_TABLE_SCORES_ID + "=" + cursor.getLong(id), null);
	}
	
	private String[] allColumns = { DATABASE_TABLE_SCORES_ID, DATABASE_TABLE_SCORES_NAME, DATABASE_TABLE_SCORES_SCORE };
	
	public List<ScoreRow> getAllScores() {
	    List<ScoreRow> scoreRows = new ArrayList<ScoreRow>();

	    Cursor cursor = database.query(DATABASE_TABLE_SCORES,
	        allColumns, null, null, null, null, DATABASE_TABLE_SCORES_SCORE+" DESC"); 

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      ScoreRow scoreRow = cursorToScoreRow(cursor);
	      scoreRows.add(scoreRow);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return scoreRows;
	  }
	
	  private ScoreRow cursorToScoreRow(Cursor cursor) {
		    ScoreRow scoreRow = new ScoreRow();
		    scoreRow.setName(cursor.getString(cursor.getColumnIndex(DATABASE_TABLE_SCORES_NAME)));
		    scoreRow.setScore(cursor.getInt(cursor.getColumnIndex(DATABASE_TABLE_SCORES_SCORE)));
		    return scoreRow;
		  }
	
	public class ScoreDBHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "data";
		private static final int DATABASE_VERSION = 1;

		public ScoreDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table " + DATABASE_TABLE_SCORES + " ("
					+ DATABASE_TABLE_SCORES_ID
					+ " integer primary key autoincrement, "
					+ DATABASE_TABLE_SCORES_NAME + " TEXT, "
					+ DATABASE_TABLE_SCORES_SCORE + " integer);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SCORES);
			onCreate(db);
		}
	}
}


