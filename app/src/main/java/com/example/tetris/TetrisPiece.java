package com.example.tetris;

import java.util.Random;
import java.util.Vector;

enum TetrisShape {
	NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape, MiniSquareShape, MiniLineShape
};

public class TetrisPiece {
	public TetrisPiece() {
		setShape(TetrisShape.NoShape);
	}

	public void setRandomShape() {
		Random random = new Random();
		setShape(TetrisShape.values()[(random.nextInt(9) + 1)]);
	}

	public void setShape(TetrisShape shape) {
		if (shape == TetrisShape.MiniSquareShape) {
			coords = new Vector<Vector<Integer>>(1);
			Vector<Integer> vector = new Vector<Integer>();
			vector.add(0);
			vector.add(0);
			vector.add(0);
			coords.add(vector);
		} else if (shape == TetrisShape.MiniLineShape) {
			coords = new Vector<Vector<Integer>>(2);
			Vector<Integer> vector = new Vector<Integer>();
			vector.add(0);
			vector.add(0);
			vector.add(0);
			coords.add(vector);
			vector = new Vector<Integer>();
			vector.add(0);
			vector.add(1);
			vector.add(0);
			coords.add(vector);
		} else {

			int[][][] coordsTable = new int[][][] {
					{ { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } },
					{ { 0, -1, 0 }, { 0, 0, 0 }, { -1, 0, 0 }, { -1, 1, 0 } },
					{ { 0, -1, 0 }, { 0, 0, 0 }, { 1, 0, 0 }, { 1, 1, 0 } },
					{ { 0, -1, 0 }, { 0, 0, 0 }, { 0, 1, 0 }, { 0, 2, 0 } },
					{ { -1, 0, 0 }, { 0, 0, 0 }, { 1, 0, 0 }, { 0, 1, 0 } },
					{ { 0, 0, 0 }, { 1, 0, 0 }, { 0, 1, 0 }, { 1, 1, 0 } },
					{ { -1, -1, 0 }, { 0, -1, 0 }, { 0, 0, 0 }, { 0, 1, 0 } },
					{ { 1, -1, 0 }, { 0, -1, 0 }, { 0, 0, 0 }, { 0, 1, 0 } } };

			coords = new Vector<Vector<Integer>>(4);

			for (int i = 0; i < 4; i++) {
				Vector<Integer> vector = new Vector<Integer>();
				for (int j = 0; j < 2; ++j)
					vector.add(coordsTable[shape.ordinal()][i][j]);
				coords.add(vector);
			}
		}

		pieceShape = shape;
	}

	public TetrisShape shape() {
		return pieceShape;
	}

	public int x(int index) {
		return coords.get(index).get(0);
		// return coords[index][0];
	}

	public int y(int index) {
		return coords.get(index).get(1);
		// return coords[index][1];
	}

	public int minX() {
		int min = coords.get(0).get(0);
		for (int i = 1; i < numberOfSquares(); ++i)
			min = Math.min(min, coords.get(i).get(0));
		return min;
	}

	public int maxX() {
		int max = coords.get(0).get(0);
		for (int i = 1; i < numberOfSquares(); ++i)
			max = Math.max(max, coords.get(i).get(0));
		return max;
	}

	public int minY() {
		int min = coords.get(0).get(1);
		for (int i = 1; i < numberOfSquares(); ++i)
			min = Math.min(min, coords.get(i).get(1));
		return min;
	}

	public int maxY() {
		int max = coords.get(0).get(1);
		for (int i = 1; i < numberOfSquares(); ++i)
			max = Math.max(max, coords.get(i).get(1));
		return max;
	}

	public TetrisPiece rotatedRight() {
		if (pieceShape == TetrisShape.SquareShape)
			return this;

		TetrisPiece result = new TetrisPiece();
		result.pieceShape = pieceShape;
		for (int i = 0; i < numberOfSquares(); ++i) {
			result.setX(i, -y(i));
			result.setY(i, x(i));
		}
		return result;
	}

	private void setX(int index, int x) {
		coords.get(index).set(0, x);
		// coords[index][0] = x;
	}

	private void setY(int index, int y) {
		coords.get(index).set(1, y);
		// coords[index][1] = y;
	}

	public int numberOfSquares() {
		return coords.size();
	}

	private TetrisShape pieceShape;
	public Vector<Vector<Integer>> coords;
}