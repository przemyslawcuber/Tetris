package com.example.tetris;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;

public class TetrisBoard {

	public TetrisBoard(GL10 gl, Context context, Activity activity) {
		_gl = gl;
		_context = context;
		_activity = activity;
	}

	public void init(float width, float height) {
		_width = width;
		_height = height;
		boardWidth = 0.63f * _width;
		boardHeight = 0.79f * _height;

		board = new TetrisShape[GameEngine.PLAYFIELD_COLS
				* GameEngine.PLAYFIELD_ROWS];
		colorTable = new RGB[] { new RGB(0, 0, 0), new RGB(204, 102, 102),
				new RGB(102, 204, 102), new RGB(102, 102, 204),
				new RGB(204, 204, 102), new RGB(204, 102, 204),
				new RGB(102, 204, 204), new RGB(218, 170, 0),
				new RGB(204, 102, 204), new RGB(102, 102, 204) };

		// Create the GLText
		glText = new GLText(_gl, _context.getAssets());

		// Load the font from file (set size + padding), creates the texture
		// NOTE: after a successful call to this the font is ready for
		// rendering!
		glText.load("Roboto-Regular.ttf", 22, 2, 2); // Create Font (Height: 14
														// Pixels / X+Y Padding
														// 2 Pixels)

		setTimer();

		isStarted = false;
		isPaused = false;
		clearBoard();
		shapePadding = 1;
		scoreManager = new ScoreManager(_context);
		alertManager = new AlertManager(_context, _activity, scoreManager);

		nextPiece = new TetrisPiece();
		nextPiece.setRandomShape();
		curPiece = new TetrisPiece();

		button = new Button[] {
				new Button(new float[] { 0f, 0f, 0f, // V1 - bottom left
						0f, 0.31f * _width, 0f, // V2 - top left
						0.31f * _width, 0f, 0f, // V3 - bottom right
						0.31f * _width, 0.31f * _width, 0f // V4 - top right
						}, R.drawable.arrow3, R.drawable.arrow4),
				new Button(new float[] { (float) (_width - 0.31f * _width), 0f, 0f, // V1 -
																			// bottom
																			// left
						(float) (_width - 0.31f * _width), 0.31f * _width, 0f, // V2 - top left
						(float) (_width), 0f, 0f, // V3 - bottom right
						(float) (_width), 0.31f * _width, 0f // V4 - top right
						}, R.drawable.arrow2, R.drawable.arrow1),
				new Button(new float[] { 0.31f * _width, 0.31f * _width / 2, 0f, // V1 - bottom left
						0.31f * _width, 0.31f * _width, 0f, // V2 - top left
						(float) (_width - 0.31f * _width), 0.31f * _width / 2, 0f, // V3 - bottom right
						(float) (_width - 0.31f * _width), 0.31f * _width, 0f // V4 - top right
						}, R.drawable.rotate, R.drawable.rotate2),
				new Button(new float[] { 0.31f * _width, 0f, 0f, // V1 - bottom left
						0.31f * _width, 0.31f * _width / 2, 0f, // V2 - top left
						(float) (_width - 0.31f * _width), 0f, 0f, // V3 - bottom right
						(float) (_width - 0.31f * _width), 0.31f * _width / 2, 0f // V4 - top right
						}, R.drawable.down, R.drawable.down2),
				new Button(new float[] { (float) (_width - 0.31f * _width / 2),
						(float) (_height - 0.31f * _width / 2), 0f, // V1 - bottom left
						(float) (_width - 0.31f * _width / 2), (float) _height, 0f, // V2 - top
																	// left
						(float) (_width), (float) (_height - 0.31f * _width / 2), 0f, // V3 -
																		// bottom
																		// right
						(float) (_width), (float) (_height), 0f // V4 - top
																// right
						}, R.drawable.play, R.drawable.pause), };

		for (int i = 0; i < button.length; ++i) {
			button[i].loadGLTexture(_gl, _context);
		}

	}

	private void start() {
		if (isPaused)
			return;

		isStarted = true;
		isWaitingAfterLine = false;
		numLinesRemoved = 0;
		numPiecesDropped = 0;
		score = 0;
		level = 1;
		clearBoard();

		newPiece();
		timer.schedule(myTimerTask, 0, timeoutTime());
	}

	private TetrisShape shapeAt(int x, int y) {
		return board[(y * GameEngine.PLAYFIELD_COLS) + x];
	}

	private void setShapeAt(int x, int y, TetrisShape shape) {
		board[(y * GameEngine.PLAYFIELD_COLS) + x] = shape;
	}

	private int timeoutTime() {
		return 1000 / (1 + level);
	}

	private float squareWidth() {
		return boardWidth / GameEngine.PLAYFIELD_COLS;
	}

	private float squareHeight() {
		return boardHeight / GameEngine.PLAYFIELD_ROWS;
	}

	private void clearBoard() {
		for (int i = 0; i < GameEngine.PLAYFIELD_COLS
				* GameEngine.PLAYFIELD_ROWS; ++i)
			board[i] = TetrisShape.NoShape;
	}

	private void showWhereShapeFalls() {
		float newY = curY;
		while (newY > 0) {
			if (!tryMove2(curPiece, curX, newY - squareHeight()))
				break;
			newY -= squareHeight();
		}
		// draw shape
		for (int i = 0; i < curPiece.numberOfSquares(); ++i) {
			drawSquare2(curX + curPiece.x(i) * squareWidth() - squareWidth(), newY - curPiece.y(i)
					* squareHeight(), curPiece.shape());
		}
	}

	public void drawSquare2(float x, float y, TetrisShape shape) {
		RGB color = colorTable[shape.ordinal()];

		float line_vertex[] = {
				// TOP LINE
				(float) x + shapePadding,
				(float) (y + squareHeight() - shapePadding), // top left
				(float) (x + squareWidth() - shapePadding),
				(float) (y + squareHeight() - shapePadding), // top
																		// right
				// LEFT LINE
				(float) x + shapePadding,
				(float) y + shapePadding, // bottom left
				(float) x + shapePadding,
				(float) (y + squareHeight() - shapePadding), // top left
				// BOTTOM LINE
				(float) x + shapePadding,
				(float) y + shapePadding, // bottom left
				(float) (x + squareWidth() - shapePadding),
				(float) y + shapePadding, // bottom right
				// RIGHT LINE
				(float) (x + squareWidth() - shapePadding),
				(float) y + shapePadding, // bottom right
				(float) (x + squareWidth() - shapePadding),
				(float) (y + squareHeight() - shapePadding), // top
																		// right
		};

		ByteBuffer byteBuffer = ByteBuffer
				.allocateDirect(line_vertex.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder()); // Use native byte order
		FloatBuffer vertexBuffer2;

		vertexBuffer2 = byteBuffer.asFloatBuffer(); // Convert byte buffer to
													// float
		vertexBuffer2.put(line_vertex); // Copy data into buffer
		vertexBuffer2.position(0);

		_gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer2);

		_gl.glPushMatrix();

		_gl.glColor4f((float) color.getR() / 255.0f,
				(float) color.getG() / 255.0f, (float) color.getB() / 255.0f,
				1.0f);

		_gl.glDrawArrays(GL10.GL_LINES, 0, 8);

		_gl.glPopMatrix();
	}

	private void oneLineDown() {
		if (!tryMove(curPiece, curX, curY - squareHeight()))
			pieceDropped(0);
	}

	private void pieceDropped(int dropHeight) {
		for (int i = 0; i < curPiece.numberOfSquares(); ++i) {
			float x = (curX / squareWidth()) + curPiece.x(i);
			float y = (curY - (int)(_height - boardHeight)) / squareHeight() - curPiece.y(i);
			setShapeAt((int)x, (int)y, curPiece.shape());
		}

		++numPiecesDropped;
		if (numPiecesDropped % 25 == 0) {
			++level;
			// timer.start(timeoutTime(), this);
			// emit levelChanged(level);
		}

		score += dropHeight + 7;
		// emit scoreChanged(score);
		removeFullLines();

		if (!isWaitingAfterLine)
			newPiece();
	}

	private void removeFullLines() {

		int numFullLines = 0;

		for (int i = GameEngine.PLAYFIELD_ROWS - 1; i >= 0; --i) {
			boolean lineIsFull = true;

			for (int j = 0; j < GameEngine.PLAYFIELD_COLS; ++j) {
				if (shapeAt(j, i) == TetrisShape.NoShape) {
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) {
				++numFullLines;
				for (int k = i; k < GameEngine.PLAYFIELD_ROWS - 1; ++k) {
					for (int j = 0; j < GameEngine.PLAYFIELD_COLS; ++j)
						setShapeAt(j, k, shapeAt(j, k + 1));
				}

				for (int j = 0; j < GameEngine.PLAYFIELD_COLS; ++j)
					setShapeAt(j, GameEngine.PLAYFIELD_ROWS - 1,
							TetrisShape.NoShape);
			}

			if (numFullLines > 0) {
				numLinesRemoved += numFullLines;
				score += 10 * numFullLines;
				// emit linesRemovedChanged(numLinesRemoved);
				// emit scoreChanged(score);

				timer.cancel();
				setTimer();
				timer.schedule(myTimerTask, 0, 500);

				isWaitingAfterLine = true;
				curPiece.setShape(TetrisShape.NoShape);
			}
		}
	}

	private void newPiece() {
		curPiece.setShape(nextPiece.shape());
		nextPiece.setRandomShape();
		curX = (int)(boardWidth / 2);
		curY = _height - squareHeight()
				+ (curPiece.minY() * squareHeight());
		
		if (!tryMove(curPiece, curX, curY)) {
			// KONIEC GRY - ZAPIS WYNIKU
			curPiece.setShape(TetrisShape.NoShape);
			timer.cancel();
			isStarted = false;

			scoreManager.isTopScore(score);

			if (scoreManager.isTopScore(score)) {
				// jest w top
				_activity.runOnUiThread(new Runnable() {
					public void run() {
						alertManager.PushAlert(1, score);
					}
				});
			} else {
				// nie jest w top
				_activity.runOnUiThread(new Runnable() {
					public void run() {
						alertManager.PushAlert(0, score);
					}
				});
			}
		}
	}

	private void showNextPiece() {
		for (int i = 0; i < nextPiece.numberOfSquares(); ++i) {
			int x = (int)(0.729f * _width);
			int y = (int)(0.688f * _height);
			drawSquare(x + nextPiece.x(i) * squareWidth(), y - nextPiece.y(i)
					* squareHeight(), nextPiece.shape());
		}
	}

	private boolean tryMove(final TetrisPiece newPiece, float newX, float newY) {
		for (int i = 0; i < newPiece.numberOfSquares(); ++i) {
			float x = (newX /  squareWidth()) + newPiece.x(i);
			float y = ((newY - (_height - boardHeight)) / squareHeight()) - newPiece.y(i);
			
			if (x < 0 || x >= 10 || y < 0 || y >= 18 || newY < 150) {
				return false;
			}
			if (shapeAt((int)x, (int)y) != TetrisShape.NoShape) {
				return false;
			}
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		return true;
	}

	private boolean tryMove2(final TetrisPiece newPiece, float newX, float newY) {
		for (int i = 0; i < newPiece.numberOfSquares(); ++i) {
			float x = (newX / squareWidth()) + newPiece.x(i);
			float y = ((newY - (_height - boardHeight)) / squareHeight()) - newPiece.y(i);

			if (x < 0 || x >= 10 || y < 0 || y >= 18 || newY < _height - boardHeight)
				return false;
			if (shapeAt((int)x, (int)y) != TetrisShape.NoShape) {
				return false;
			}
		}

		return true;
	}

	public void drawButtons() {
		_gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )
		_gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
		// gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); //Black Background
		_gl.glClearDepthf(1.0f); // Depth Buffer Setup
		_gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
		_gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do

		// Really Nice Perspective Calculations
		_gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		// gl.glScalef(0.4f, 0.4f, 1f);

		for (int i = 0; i < button.length; ++i) {
			button[i].draw(_gl);
		}

	}

	public void drawBoard() {
		// drawing table ---------------------------------------------------

		_gl.glDisable(GL10.GL_TEXTURE_2D); // works same like the next line
		// gl.glBindTexture(GL10.GL_TEXTURE_2D, 0); // switches to the default
		// texture

		_gl.glColor4f(1.0f, 0.5f, 0.0f, 1.0f);

		// -------------------------------------------------------------------------------------------

		float line_vertex[] = { 0f, (float) _height, boardWidth, (float) _height, // top
				0f, _height - boardHeight, 0f, (float) _height, // left
				0f, _height - boardHeight, boardWidth, _height - boardHeight, // bottom
				boardWidth, _height - boardHeight, boardWidth, (float) _height // right
		};

		ByteBuffer byteBuffer = ByteBuffer
				.allocateDirect(line_vertex.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder()); // Use native byte order
		FloatBuffer vertexBuffer2;

		vertexBuffer2 = byteBuffer.asFloatBuffer(); // Convert byte buffer to
													// float
		vertexBuffer2.put(line_vertex); // Copy data into buffer
		vertexBuffer2.position(0);

		_gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer2);
		_gl.glLineWidth(2);

		_gl.glPushMatrix();

		_gl.glDrawArrays(GL10.GL_LINES, 0, 8);

		_gl.glPopMatrix();
	}

	public void drawShapes() {
		// DRAW SHAPE

		if (curPiece.shape() != TetrisShape.NoShape) {
			for (int i = 0; i < curPiece.numberOfSquares(); ++i) {
				drawSquare(curX + curPiece.x(i) * squareWidth() - squareWidth(), curY
						- curPiece.y(i) * squareHeight(),
						curPiece.shape());
			}
			showWhereShapeFalls();
		}

		showNextPiece();

		for (int i = 0; i < GameEngine.PLAYFIELD_ROWS; ++i) {
			for (int j = 0; j < GameEngine.PLAYFIELD_COLS; ++j) {
				TetrisShape shape = shapeAt(j, GameEngine.PLAYFIELD_ROWS - i
						- 1);
				if (shape != TetrisShape.NoShape)
					drawSquare(j * squareWidth(),
							(_height - (i * squareHeight()) - squareHeight()),
							shape);
			}
		}
	}

	public void drawStatistics() {
		// TEXT
		// ---------------------------------------------------------------------------------------

		// gl.glMatrixMode( GL10.GL_MODELVIEW ); // Activate Model View Matrix
		// gl.glLoadIdentity();

		// enable texture + alpha blending
		// NOTE: this is required for text rendering! we could incorporate it
		// into
		// the GLText class, but then it would be called multiple times (which
		// impacts performance).
		_gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping
		_gl.glEnable(GL10.GL_BLEND); // Enable Alpha Blend
		_gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA); // Set
																			// Alpha
																			// Blend
																			// Function

		// TEST: render the entire font texture
		// gl.glColor4f( 1.0f, 1.0f, 1.0f, 1.0f ); // Set Color to Use
		// glText.drawTexture( 100, 100 ); // Draw the Entire Texture

		// TEST: render some strings with the font
		//glText.begin(1.0f, 1.0f, 1.0f, 1.0f); // Begin Text Rendering (Set Color
												// WHITE)
		//glText.draw("Time:", 350, 450); // Draw Test String
		//glText.end(); // End Text Rendering

		glText.begin(0.0f, 0.0f, 1.0f, 1.0f); // Begin Text Rendering (Set Color
												// BLUE)
		glText.draw("Score:", (int)(0.729 * _width), (int)(0.435 * _height)); // Draw Test String
		glText.draw(Integer.toString(score), (int)(0.729 * _width), (int)(0.435 * _height) - 25);
		glText.end(); // End Text Rendering

		// disable texture + alpha
		_gl.glDisable(GL10.GL_BLEND); // Disable Alpha Blend
		_gl.glDisable(GL10.GL_TEXTURE_2D); // Disable Texture Mapping
	}

	public void drawSquare(float x, float y, TetrisShape shape) {
		RGB color = colorTable[shape.ordinal()];

		float ShapeVertices[] = {
				// Triangle 1
				(float) x + shapePadding,
				(float) y + shapePadding, // bottom left
				(float) x + shapePadding,
				(float) (y + squareHeight() - shapePadding), // top left
				(float) (x + squareWidth() - shapePadding),
				(float) (y + squareHeight() - shapePadding), // top
																		// right

				// Triangle 2
				(float) x + shapePadding,
				(float) y + shapePadding, // bottom left
				(float) (x + squareWidth() - shapePadding),
				(float) (y + squareHeight() - shapePadding), // top
																		// right
				(float) (x + squareWidth() - shapePadding),
				(float) y + shapePadding, // bottom right
		};

		ByteBuffer byteBuffer = ByteBuffer
				.allocateDirect(ShapeVertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer vertexBuffer;

		vertexBuffer = byteBuffer.asFloatBuffer();
		vertexBuffer.put(ShapeVertices);
		vertexBuffer.position(0);

		_gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);

		_gl.glPushMatrix();

		_gl.glColor4f((float) color.getR() / 255.0f,
				(float) color.getG() / 255.0f, (float) color.getB() / 255.0f,
				1.0f);
		_gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);

		_gl.glPopMatrix();
	}

	public void handleTouchPress(float x, float y) {

		// PLAY/PAUSE
		if (x > button[4].vertices[0] && x < button[4].vertices[9]
				&& (_height - y) > button[4].vertices[1]
				&& (_height - y) < button[4].vertices[10]) {
			// PIERWSZE WYWOŁANIE CZYLI PLAY
			if (!isStarted && !isPaused) {
				isStarted = true;
				isWaitingAfterLine = false;
				numLinesRemoved = 0;
				numPiecesDropped = 0;
				score = 0;
				level = 1;
				clearBoard();

				// linesRemovedChanged(numLinesRemoved);
				// scoreChanged(score);
				// levelChanged(level);
				button[4].setTexture(1);
				newPiece();
				timer.schedule(myTimerTask, 0, timeoutTime());
			} else if (isStarted && !isPaused) {
				pause();
			} else if (isStarted && isPaused) {
				isPaused = !isPaused;
				button[4].setTexture(1);
				setTimer();
				timer.schedule(myTimerTask, 0, timeoutTime());
			}

			return;
		}

		if (!isStarted || isPaused || curPiece.shape() == TetrisShape.NoShape) {
			return;
		}

		if (x > button[0].vertices[0] && x < button[0].vertices[9]
				&& (_height - y) > button[0].vertices[1]
				&& (_height - y) < button[0].vertices[10]) {
			tryMove(curPiece, curX - squareWidth(), curY);

			new Thread() {
				public void run() {
					// animuj przycisk
					button[0].setTexture(1);
					try {
						Thread.sleep(100);
						button[0].setTexture(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();

			return;
		}
		if (x > button[1].vertices[0] && x < button[1].vertices[9]
				&& (_height - y) > button[1].vertices[1]
				&& (_height - y) < button[1].vertices[10]) {
			tryMove(curPiece, curX + squareWidth(), curY);

			new Thread() {
				public void run() {
					// animuj przycisk
					button[1].setTexture(1);
					try {
						Thread.sleep(100);
						button[1].setTexture(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			return;
		}
		if (x > button[2].vertices[0] && x < button[2].vertices[9]
				&& (_height - y) > button[2].vertices[1]
				&& (_height - y) < button[2].vertices[10]) {
			tryMove(curPiece.rotatedRight(), curX, curY);

			new Thread() {
				public void run() {
					// animuj przycisk
					button[2].setTexture(1);
					try {
						Thread.sleep(100);
						button[2].setTexture(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			return;
		}
		if (x > button[3].vertices[0] && x < button[3].vertices[9]
				&& (_height - y) > button[3].vertices[1]
				&& (_height - y) < button[3].vertices[10]) {
			oneLineDown();
			new Thread() {
				public void run() {
					// animuj przycisk
					button[3].setTexture(1);
					try {
						Thread.sleep(100);
						button[3].setTexture(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			return;
		}
	}

	private void setTimer() {
		timer = new Timer();
		myTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (isWaitingAfterLine) {
					isWaitingAfterLine = false;
					newPiece();
					timer.cancel();
					setTimer();
					timer.schedule(myTimerTask, 0, timeoutTime());
				} else {
					oneLineDown();
				}
			}
		};
	}

	public void pause() {
		isPaused = !isPaused;
		if (isPaused)
			timer.cancel();
		button[4].setTexture(0);
	}

	private boolean isStarted;
	private boolean isPaused;
	private boolean isWaitingAfterLine;
	private TetrisPiece curPiece;
	private TetrisPiece nextPiece;
	private float curX;
	private float curY;
	private int numLinesRemoved;
	private int numPiecesDropped;
	private int score;
	private int level;
	private TetrisShape[] board;
	private RGB[] colorTable;
	private Button[] button;
	private float boardWidth;
	private float boardHeight;
	
	
	TimerTask myTimerTask;
	Timer timer;
	public float _width;
	public float _height;
	GL10 _gl;
	Context _context;
	private float shapePadding;
	GLText glText;

	private ScoreManager scoreManager;
	private AlertManager alertManager;
	private Activity _activity;
}
