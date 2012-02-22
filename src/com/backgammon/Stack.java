package com.backgammon;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

public abstract class Stack{
	Chip[] chips;
	public Chip[] getChips() {
		return chips;
	}
	protected boolean hasFocus = false;
	protected int focusedChip;
	protected Point lastFocusedPosition;
	public Move move;
	
	
	public Stack(int capacity) {
		this.chips = new Chip[capacity];
	}
	public abstract void setMove(Move m);
	
	public void dropFocus() {
		this.hasFocus = false;
		for (int i = 0; i < chips.length; i++) {
			chips[i].setFocused(false);
		}
	}
	
	public void setFocus(int pos){
		dropFocus();
		hasFocus = true;
		chips[pos].setFocused(true);
		lastFocusedPosition = chips[pos].getPhisicalPoint();
		focusedChip = pos;
	}
	
	public boolean setFocus(float x, float y) {
		int closest = getClosestTop(x, y);
		if (closest != -1) {
			dropFocus();
			hasFocus = true;
			chips[closest].setFocused(true);
			lastFocusedPosition = chips[closest].getPhisicalPoint();
			focusedChip = closest;
			return true;
		}
		dropFocus();
		return false;
	}

	protected int getClosestTop(float x, float y) {
		Vector<Integer> tops = new Vector<Integer>();
		for (int i = 0; i < chips.length; i++) {
			if (chips[i].isTop())
				tops.add(new Integer(i));
		}
		double min = Integer.MAX_VALUE;
		int res = -1;

		for (Iterator<Integer> iterator = tops.iterator(); iterator.hasNext();) {
			Integer curTop = (Integer) iterator.next();

			double tmplength = Math.sqrt(Math.pow(
					(chips[curTop].getPhisicalPoint().y - y), 2)
					+ Math.pow((chips[curTop].getPhisicalPoint().x - x), 2));
			if (tmplength < min) {
				min = tmplength;
				res = curTop.intValue();
			}
		}

		if (min < GameBoard.boadrWidth / 4)
			return res;
		else
			return -1;
	}

	public void moveFocused(float x, float y) {
		chips[focusedChip].move(x, y);
	}

	public CompleteMove fastMoveFocused() {
		MoveBind now = new MoveBind(lastFocusedPosition,
				chips[focusedChip].getPosition(),CompleteMove.NONE);
		Vector<MoveBind> moves = move.getAviableMoves();
		moves.add(now);

		MoveBind closest = getClosestPoint(moves,
				chips[focusedChip].getPhisicalPoint());

		/*int diff = closest.getTargetPosition()
				- chips[focusedChip].getPosition();*/
		fastMoveFocused(closest, 0);

		CompleteMove cm = new CompleteMove(now, closest, focusedChip);
		
		return cm;
	}

	public CompleteMove fastMoveFocused(MoveBind closest, int timeDelay) {
		int lastPos = chips[focusedChip].getPosition();
		MoveBind now = new MoveBind(chips[focusedChip].getPhisicalPoint(),chips[focusedChip].getPosition(),CompleteMove.NONE);
		CompleteMove cm = new CompleteMove(now, closest, focusedChip);
		
		chips[focusedChip].fastMove(closest, timeDelay);
		if (lastPos != chips[focusedChip].getPosition()) {
			for (int i = 0; i < chips.length; i++) {
				if (i != focusedChip) {
					if (chips[i].getPosition() == chips[focusedChip]
							.getPosition())
						chips[i].setTop(false);
				}
			}

			int highest = this.getHighest(lastPos);
			if (highest >= 0)
				chips[highest].setTop(true);
		}
		return cm;
	}

	protected abstract int getHighest(int pos); 
	/*	int res = -1;
		int tmpHighs = (pos < BoardPositions.POSITIONS_COUNT / 2) ? Integer.MAX_VALUE
				: Integer.MIN_VALUE;
		;
		for (int i = 0; i < chips.length; i++) {
			if (chips[i].getPosition() == pos) {
				if (pos < BoardPositions.POSITIONS_COUNT / 2) {
					if (chips[i].getPhisicalPoint().y < tmpHighs) {
						tmpHighs = chips[i].getPhisicalPoint().y;
						res = i;
					}
				} else {
					if (chips[i].getPhisicalPoint().y > tmpHighs) {
						tmpHighs = chips[i].getPhisicalPoint().y;
						res = i;
					}
				}
			}
		}
		return res;*/
	

	protected MoveBind getClosestPoint(Vector<MoveBind> variants, Point curPoint) {
		double length = Integer.MAX_VALUE;
		MoveBind res = null;

		for (Iterator iterator = variants.iterator(); iterator.hasNext();) {
			MoveBind moveBind = (MoveBind) iterator.next();
			double tmplength = Math.sqrt(Math.pow(
					(moveBind.getTargetPoint().y - curPoint.y), 2)
					+ Math.pow((moveBind.getTargetPoint().x - curPoint.x), 2));
			if (tmplength < length) {
				length = tmplength;
				res = moveBind;
			}
		}

		return res;
	}

	public void drawStack(Canvas c) {
		for (int i = 0; i < chips.length; i++) {
			//Log.e("senya", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			//Log.e("senya", "trying to draw " + i);
			chips[i].draw(c);
		}
	}

	public boolean hasFocus() {
		return hasFocus;
	}

	public int getFocusedChipPosition() {
		return chips[focusedChip].getPosition();
	}

	public int chipsAtPos(int pos) {
		int res = 0;
		for (int i = 0; i < chips.length; i++) {
			if (chips[i].getPosition() == pos)
				res++;
		}
		return res;
	}

	public int getLength() {
		return chips.length;
	}

	public int getPositionAt(int chipN) {
		return chips[chipN].getPosition();
	}
	public abstract void makeMove();
	
	public boolean isAllInLastQuoter(){
		for (int i = 0; i < GameBoard.CHIPS_COUNT; i++) {
			if(chips[i].getPosition() < BoardPositions.POSITIONS_COUNT - BoardPositions.POSITIONS_COUNT/4)
				return false;
		}
		return true;
	} 
	public boolean isAnyInLastQuoter(){
		for (int i = 0; i < GameBoard.CHIPS_COUNT; i++) {
			if(chips[i].getPosition() >= BoardPositions.POSITIONS_COUNT - BoardPositions.POSITIONS_COUNT/4)
				return true;
		}
		return false;
	}
	protected boolean ifExistAbove(int chipNum){
		int pos = chips[chipNum].getPosition();
		for (int i = 0; i < chips.length; i++) {
			if(chips[i].getPosition() < pos)
				return true;
		}
		return false;
	}
	protected boolean ifExistAboveOrHere(int chipNum){
		int pos = chips[chipNum].getPosition();
		for (int i = 0; i < chips.length; i++) {
			if(chips[i].getPosition() <= pos)
				return true;
		}
		return false;
	}
}