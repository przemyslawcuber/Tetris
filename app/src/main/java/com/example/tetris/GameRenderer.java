package com.example.tetris;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class GameRenderer implements Renderer {

	TetrisBoard tetrisBoard;
	Context _context;
	Activity _activity;
	private int _height;
	private int _width;

	public GameRenderer(Context context, Activity activity) {
		super();
		_context = context;
		_activity = activity;

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		_width = size.x;
		_height = size.y;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		try {
			Thread.sleep(GameEngine.GAME_THREAD_FPS_SLEEP);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// clear Screen and Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Set to ModelView mode
		gl.glMatrixMode(GL10.GL_MODELVIEW); // Activate Model View Matrix
		// Reset the Modelview Matrix
		gl.glLoadIdentity();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ZERO);

		tetrisBoard.drawBoard();

		tetrisBoard.drawShapes();

		tetrisBoard.drawButtons();

		tetrisBoard.drawStatistics();

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		gl.glViewport(0, 0, width, height);

		// Setup orthographic projection
		gl.glMatrixMode(GL10.GL_PROJECTION); // Activate Projection Matrix
		gl.glLoadIdentity(); // Load Identity Matrix
		gl.glOrthof( // Set Ortho Projection (Left,Right,Bottom,Top,Front,Back)
				0, width, 0, height, 1.0f, -1.0f);

		tetrisBoard._width = width;
		tetrisBoard._height = height;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// gl.glClearColor( (float)(11 / 255.0f), (float)(177 / 255.0f),
		// (float)(246 / 255.0f), 1.0f );
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		tetrisBoard = new TetrisBoard(gl, _context, _activity);
		tetrisBoard.init(_width, _height);
		
	}

	public void handleTouchPress(float x, float y) {
		tetrisBoard.handleTouchPress(x, y);
	}
}
