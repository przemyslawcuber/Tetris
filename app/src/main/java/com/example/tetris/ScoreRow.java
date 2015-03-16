package com.example.tetris;

public class ScoreRow {
	public ScoreRow() {
		name = "";
		score = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return getScore() + " " + getName();
	}

	String name;
	int score;
}