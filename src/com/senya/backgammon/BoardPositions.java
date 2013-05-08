package com.senya.backgammon;

import java.util.Vector;

import android.graphics.Point;

public class BoardPositions {
	public static final int POSITIONS_COUNT = 24;
	
	private static BoardPositions instance;
	Vector<Point> clockwise;
	Vector<Point> otherwise;
	private Point clockwiseEndPoint;
	private Point otherwiceEndPoint;
	
	private BoardPositions(){
		clockwise = new Vector<Point>();
		otherwise = new Vector<Point>();
	}
	
	public static BoardPositions getInstance(){
		if(instance==null)
			instance = new BoardPositions();
		return instance;
	}
	public void init(int width, int height,float chipSize){
		for(int i = 0; i < POSITIONS_COUNT/2;i++){
			int x = (int) (chipSize * i + chipSize/2 + ((i<6)?GameBoard.GAME_BOARD_PADDING:GameBoard.GAME_BOARD_PADDING*3));
			int y = (int) (height - GameBoard.GAME_BOARD_PADDING - chipSize/2);	
			clockwise.add(new Point(x,y));
		}
		for(int i = POSITIONS_COUNT/2; i < POSITIONS_COUNT;i++){
			int x = (int) (width - ((i<18)?GameBoard.GAME_BOARD_PADDING:GameBoard.GAME_BOARD_PADDING*3) - chipSize/2 - (chipSize * (i - POSITIONS_COUNT/2)));// chipSize * i + chipSize/2 + ((i<6)?GameBoard.GAME_BOARD_PADDING:GameBoard.GAME_BOARD_PADDING*3);
			int y = (int) (GameBoard.GAME_BOARD_PADDING + chipSize/2);	
			clockwise.add(new Point(x,y));
		}
		
		
		for(int i = 0; i < POSITIONS_COUNT/2;i++){
			int x = (int) (width - ((i<6)?GameBoard.GAME_BOARD_PADDING:GameBoard.GAME_BOARD_PADDING*3) - chipSize/2 - (chipSize * i));
			int y = (int) (GameBoard.GAME_BOARD_PADDING + chipSize/2);	
			otherwise.add(new Point(x,y));
		}
		for(int i = POSITIONS_COUNT/2; i < POSITIONS_COUNT;i++){
			int x = (int) (chipSize * (i - POSITIONS_COUNT/2) + chipSize/2 + ((i<18)?GameBoard.GAME_BOARD_PADDING:GameBoard.GAME_BOARD_PADDING*3));
			int y = (int) (height - GameBoard.GAME_BOARD_PADDING - chipSize/2);	
			otherwise.add(new Point(x,y));
		}
//		clockwiseEndPoint = new Point((int)(width/2 - chipSize), height/3);
//		otherwiceEndPoint = new Point((int)width/2, height/3);
	}
	public Vector<Point> getClockwise(){
		return clockwise;
	}
	public Vector<Point> getOtherwise(){
		return otherwise;
	}
	public Point getClockwiseAt(int i){
		return (i > POSITIONS_COUNT - 1)?clockwise.elementAt(0):clockwise.elementAt(i);
	}
	public Point getOtherwiseAt(int i){
		return (i > POSITIONS_COUNT - 1)?otherwise.elementAt(0):otherwise.elementAt(i);
	}
	public int ConvertPosition(int pos){
		if(pos < POSITIONS_COUNT/2)
			return pos + POSITIONS_COUNT/2;
		else
			return pos - POSITIONS_COUNT/2;
	}
}