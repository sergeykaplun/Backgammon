package com.backgammon.dices;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.R.bool;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.senya.backgammon.CompleteMove;
import com.senya.backgammon.GameActivity;
import com.senya.backgammon.GameBoard;
import com.senya.backgammon.GameManager;

public class DiceManager {
	public enum DiceStates {
		Rolling, Normal, Double, Chellange, None;
	}

	private DiceStates curState;
	public int[] dicesValues;
	public boolean[] usedDices;
	private List<DiceRolledListener> listeners;
	private static DiceManager _instance;
	public boolean autoroll;

	public static DiceManager getInstance() {
		if (_instance == null)
			_instance = new DiceManager();
		return _instance;
	}

	public void addRolledListener(DiceRolledListener listener) {
		if (listeners == null)
			listeners = new LinkedList<DiceRolledListener>();
		listeners.add(listener);
	}

	private void notifyListeners() {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onDiceRolled(dicesValues[0], dicesValues[1]);
		}
	}

	private DiceManager() {
		dicesValues = new int[4];
		usedDices = new boolean[4];
		curState = DiceStates.None;
	}

	private void resetDices(int first, int second) {
		autoroll = false;
		if (first == second) {
			curState = DiceStates.Double;
			for (int i = 0; i < dicesValues.length; i++) {
				dicesValues[i] = first;
			}
		} else {
			dicesValues[0] = first;
			dicesValues[1] = second;
			curState = DiceStates.Normal;
		}
		for (int i = 0; i < usedDices.length; i++) {
			usedDices[i] = false;
		}
		notifyListeners();
	}

	public void rollDice(boolean isChallenge) {
		new Timer().schedule(new Rolling(isChallenge), 0, 75);
	}

	private class Rolling extends TimerTask {
		int count;
		private Random r;// = new Random();

		public Rolling(boolean isChallenge) {
			count = 0;
			r = new Random();
			curState = isChallenge?DiceStates.Chellange:DiceStates.Rolling;
		}

		@Override
		public void run() {
			if (count++ < 15) {
				int f = r.nextInt(6) + 1;
				int s = r.nextInt(6) + 1;
				dicesValues[0] = f;
				dicesValues[1] = s;
			} else {
				int f = r.nextInt(6) + 1;
				int s = r.nextInt(6) + 1;
				if(curState == DiceStates.Chellange){
					dicesValues[0] = f;
					dicesValues[1] = s;
					notifyListeners();
				}
				else	
					resetDices(f, s);
				this.cancel();
			}
		}
	}

	public void revertDices(CompleteMove last) {
		if (curState == DiceStates.Normal)
			switch (last.getTo().getUsedBone()) {
			case CompleteMove.FIRST:
				usedDices[0] = false;
				break;
			case CompleteMove.SECOND:
				usedDices[1] = false;
				break;
			case CompleteMove.BOTH:
				usedDices[0] = false;
				usedDices[1] = false;
				break;
			case CompleteMove.NONE:
				break;
			}
		else
			addMovesLeft(last.getTo().getUsedBone());
	}

	private void addMovesLeft(int count) {
		if (curState != DiceStates.Double)
			throw new IllegalStateException("Not in double mod");

		int usedCount = 0;
		for (int i = 0; i < usedDices.length; i++) {
			if (usedDices[i])
				usedCount++;
		}
		usedCount -= count;
		for (int i = 0; i < usedDices.length; i++) {
			usedDices[i] = false;
			if (i < usedCount)
				usedDices[i] = true;
		}
	}

	private void addMovesUsed(int count) {
		if (curState != DiceStates.Double)
			throw new IllegalStateException("Not in double mod");

		int used = 0;
		for (int i = 0; i < usedDices.length; i++) {
			if (usedDices[i])
				used++;
			usedDices[i] = false;
		}
		used += count;
		for (int i = 0; i < used; i++) {
			usedDices[i] = true;
		}
		Log.i("SetMovesLeft", "" + count);
	}

	public int getAvailableMovesCount() {
		int count = 0;
		switch (curState) {
		case Double:
			for (int i = 0; i < usedDices.length; i++) {
				if (!usedDices[i])
					count++;
			}
			return count;
		case Normal:
			for (int i = 0; i < usedDices.length / 2; i++) {
				if (!usedDices[i])
					count++;
			}
			return count;
		default:
			return count;
		}
	}
	
	public void reInit(String message){
		this.curState = DiceStates.None;
		this.dicesValues = new int[4];
		this.usedDices = new boolean[]{false,false,false,false};
		this.hintString = message; 
	}

	public void drawDices(Canvas c) {
		Paint paint = new Paint();
		switch (curState) {
		case Normal:
			c.drawBitmap(
					(usedDices[0]) ? GameBoard.RED_BONES[dicesValues[0] - 1]
							: GameBoard.GREY_BONES[dicesValues[0] - 1],
					GameBoard.boadrWidth
							/ 4
							* 3
							- GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getWidth(),
					GameBoard.boardHeight
							/ 2
							- GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getHeight() / 2, paint);
			c.drawBitmap(
					(usedDices[1]) ? GameBoard.RED_BONES[dicesValues[1] - 1]
							: GameBoard.GREY_BONES[dicesValues[1] - 1],
					GameBoard.boadrWidth / 4 * 3/*
												 * + GameBoard.GREY_BONES[
												 * firstBone - 1].getWidth()
												 */,
					GameBoard.boardHeight
							/ 2
							- GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getHeight() / 2, paint);
			break;
		case Double:
			int moves_left = getAvailableMovesCount();
			int count = 1;
			for (int i = 0; i < 4; i++) {
				switch (i) {
				case 0:
					c.drawBitmap(
							(count++ > moves_left) ? GameBoard.RED_BONES[dicesValues[0] - 1]
									: GameBoard.GREY_BONES[dicesValues[0] - 1],
							(GameBoard.boadrWidth / 4 * 3 - GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getWidth()),
							GameBoard.boardHeight
									/ 2
									- GameBoard.GREY_BONES[dicesValues[0] - 1]
											.getHeight(), paint);
					break;
				case 1:
					c.drawBitmap(
							(count++ > moves_left) ? GameBoard.RED_BONES[dicesValues[0] - 1]
									: GameBoard.GREY_BONES[dicesValues[0] - 1],
							GameBoard.boadrWidth / 4 * 3,
							GameBoard.boardHeight
									/ 2
									- GameBoard.GREY_BONES[dicesValues[0] - 1]
											.getHeight(), paint);
					break;
				case 2:
					c.drawBitmap(
							(count++ > moves_left) ? GameBoard.RED_BONES[dicesValues[0] - 1]
									: GameBoard.GREY_BONES[dicesValues[0] - 1],
							(GameBoard.boadrWidth / 4 * 3 - GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getWidth()), GameBoard.boardHeight / 2,
							paint);
					break;
				case 3:
					c.drawBitmap(
							(count++ > moves_left) ? GameBoard.RED_BONES[dicesValues[0] - 1]
									: GameBoard.GREY_BONES[dicesValues[0] - 1],
							GameBoard.boadrWidth / 4 * 3,
							GameBoard.boardHeight / 2, paint);
					break;
				}
			}
			break;
		case Rolling:
			c.drawBitmap(
					GameBoard.GREY_BONES[dicesValues[0] - 1],
					GameBoard.boadrWidth
							/ 4
							* 3
							- GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getWidth(),
					GameBoard.boardHeight
							/ 2
							- GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getHeight() / 2, paint);
			c.drawBitmap(
					GameBoard.GREY_BONES[dicesValues[1] - 1],
					GameBoard.boadrWidth / 4 * 3,
					GameBoard.boardHeight
							/ 2
							- GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getHeight() / 2, paint);
			break;
		case Chellange:
			c.drawBitmap(
					GameBoard.GREY_BONES[dicesValues[0] - 1],
					GameBoard.boadrWidth
							/ 4
							- GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getWidth()/2,
					GameBoard.boardHeight
							/ 2
							- GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getHeight() / 2, paint);
			c.drawBitmap(
					GameBoard.GREY_BONES[dicesValues[1] - 1],
					GameBoard.boadrWidth / 4 * 3 - GameBoard.GREY_BONES[dicesValues[0] - 1]
							.getWidth()/2,
					GameBoard.boardHeight
							/ 2
							- GameBoard.GREY_BONES[dicesValues[0] - 1]
									.getHeight() / 2, paint);
			break;
		default:
			break;
		}
		if (curState != DiceStates.Rolling && curState != DiceStates.None && curState != DiceStates.Chellange 
				&& (getAvailableMovesCount() == 0 || !GameManager.getInstance().isCurrentHasMoreMoves())) {
			c.drawBitmap(
					GameBoard.DONE_BUTTON,
					GameBoard.boadrWidth / 4 * 3
							- GameBoard.DONE_BUTTON.getWidth() / 2,
					GameBoard.boardHeight / 2
							- GameBoard.DONE_BUTTON.getHeight() / 2, paint);
		}
		drawHint(c);
	}
	
		private Paint hintPainter;
		private int fontSize = 0;
		private static final int DEFAULT_FONT_SIZE = 100;
	{
		hintPainter = new Paint();
		hintPainter.setColor(Color.BLACK);
	}
	private void calculateFontSize(){
		float width = GameBoard.boadrWidth;
		width -= width/5;
		hintPainter.setTextSize(fontSize = DEFAULT_FONT_SIZE);
		try{
			while (hintPainter.measureText(hintString) > width) {
				hintPainter.setTextSize(--fontSize);
			}
		}catch(IllegalArgumentException ex){
			Log.e("BACKGAMMON", ex.getMessage());
		}
	}
	public String hintString;
	private void drawHint(Canvas c){
		synchronized (_instance) {
			if(hintString == null)
				return;
			float width = hintPainter.measureText(hintString);
			Typeface t = GameActivity.lazyTypeface;
			hintPainter.setTypeface(t);
			hintPainter.setTextSkewX(-0.3f);
			calculateFontSize();
			c.drawText(hintString, GameBoard.boadrWidth/2 - width/2, 100, hintPainter);
		}
	}
	
	public void useDice(int which) {
		switch (curState) {
		case Normal:
			switch (which) {
				case CompleteMove.FIRST:
					usedDices[0] = true;
					break;
				case CompleteMove.SECOND:
					usedDices[1] = true;
					break;
				case CompleteMove.BOTH:
					usedDices[0] = true;
					usedDices[1] = true;
					break;
				default:
					break;
				}
			break;
		case Double:
			if (which < 0)
				return;
			addMovesUsed(which);
			break;
		default:
			break;
		}
	}

	public boolean isDoubleMode() {
		return curState == DiceStates.Double;
	}

	public int getFirstDice() {
		if (curState == DiceStates.Normal && usedDices[0])
			return Integer.MAX_VALUE;
		else
			return dicesValues[0];
	}

	public int getSecondDice() {
		if (curState == DiceStates.Normal && usedDices[1])
			return Integer.MAX_VALUE;
		else
			return dicesValues[1];
	}
	
	public DiceStates getCurrentState(){
		return curState;
	}
	public void setState(DiceStates state){
		this.curState = state;
	}
}