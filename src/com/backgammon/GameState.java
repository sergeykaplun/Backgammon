package com.backgammon;

import java.io.Serializable;

import android.graphics.Point;

public class GameState implements Serializable{
	mPoint[] wPoints;
	int[] wPositions;
	boolean[] wIsTop;
	
	mPoint[] bPoints;
	int[] bPositions;
	boolean[] bIsTop;
	
	public GameState(Stack w, Stack b){
		wPoints = new mPoint[GameBoard.CHIPS_COUNT];
		bPoints = new mPoint[GameBoard.CHIPS_COUNT];
		wPositions = new int[GameBoard.CHIPS_COUNT];
		bPositions = new int[GameBoard.CHIPS_COUNT];
		wIsTop = new boolean[GameBoard.CHIPS_COUNT];
		bIsTop = new boolean[GameBoard.CHIPS_COUNT];
		
		
		for (int i = 0; i < GameBoard.CHIPS_COUNT; i++) {
			wPoints[i] = new mPoint(w.getChips()[i].getPhisicalPoint().x, w.getChips()[i].getPhisicalPoint().y);
			bPoints[i] = new mPoint(b.getChips()[i].getPhisicalPoint().x, b.getChips()[i].getPhisicalPoint().y);;
		
			wPositions[i] = w.getChips()[i].getPosition();
			bPositions[i] = b.getChips()[i].getPosition();
			
			wIsTop[i] = w.getChips()[i].isTop();
			bIsTop[i] = b.getChips()[i].isTop();
		}
	}
}
