package com.backgammon;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public abstract class Move {
	protected boolean firstMove;
	protected boolean firstMoveByRound;
	protected boolean double_mode;
	protected int movesLeft;
	protected int reFirst;
	protected int reSecond;
	protected int first;
	protected int second;
	protected Stack stack;
	protected Stack opponent;
	protected float chipSize;
	protected MoveFinishListener listener;
	protected Journal journal;

	public Move(Stack stack, Stack opponent, float chipSize,
			MoveFinishListener lis, Journal j) {
		this.first = Integer.MAX_VALUE;
		this.second = Integer.MAX_VALUE;
		double_mode = false;
		this.stack = stack;
		this.opponent = opponent;
		this.chipSize = chipSize;
		listener = lis;
		this.journal = j;
		this.firstMove = true;
		this.firstMoveByRound = true;
	}

	public void setBones(int first, int second) {
		if (first != second) {
			this.first = first;
			this.second = second;
			reFirst = first;
			reSecond = second;
			double_mode = false;
		} else{
			setBones(first);
			movesLeft = 4;
		}
		firstMoveByRound = true;
	}

	protected void setBones(int first) {
		this.first = this.second = reFirst = first;
		double_mode = true;
		movesLeft = 4;
	}

	public abstract Vector<MoveBind> getAviableMoves();

	public void useBone(CompleteMove move) {
		if (firstMove)
			firstMove = false;
		if (move.getPosDiff() > 0 && move.getFrom().getTargetPosition() == 0) {// stack.getFocusedChipPosition()
																				// ==
																				// 0){
			firstMoveByRound = false;
		}
		if (!double_mode) {
			switch (move.getTo().getUsedBone()) {
			case CompleteMove.FIRST:
				first = Integer.MAX_VALUE;
				listener.boneIsUsed(false, 1);
				if (journal != null)
					journal.put(move);
				break;
			case CompleteMove.SECOND:
				second = Integer.MAX_VALUE;
				listener.boneIsUsed(false, 2);
				if (journal != null)
					journal.put(move);
				break;
			case CompleteMove.BOTH:
				first = Integer.MAX_VALUE;
				second = Integer.MAX_VALUE;
				listener.boneIsUsed(false, 1);
				listener.boneIsUsed(false, 2);
				if (journal != null)
					journal.put(move);
				break;
			}
			/*
			 * if (move.getUsebBone() == CompleteMove.FIRST) {
			 * 
			 * } else if (move.getPosDiff() == second) {
			 * 
			 * } else if (move.getPosDiff() == first + second) {
			 * 
			 * }
			 */
		} else {
			if(move.getTo().getUsedBone() > 0){
				movesLeft -= move.getTo().getUsedBone();
				listener.boneIsUsed(true, movesLeft);
				if (journal != null)
					journal.put(move);
			}
		}
	}
	public void useBone(CompleteMove move,int delay) {
		if (firstMove)
			firstMove = false;
		if (move.getPosDiff() > 0 && move.getFrom().getTargetPosition() == 0) {// stack.getFocusedChipPosition()
																				// ==
																				// 0){
			firstMoveByRound = false;
		}
		if (!double_mode) {
			switch (move.getTo().getUsedBone()) {
			case CompleteMove.FIRST:
				first = Integer.MAX_VALUE;
				new Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						listener.boneIsUsed(false, 1);
					}
				}, delay);
				if (journal != null)
					journal.put(move);
				break;
			case CompleteMove.SECOND:
				second = Integer.MAX_VALUE;
				new Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						listener.boneIsUsed(false, 2);
					}
				}, delay);
				if (journal != null)
					journal.put(move);
				break;
			case CompleteMove.BOTH:
				first = Integer.MAX_VALUE;
				second = Integer.MAX_VALUE;
				new Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						listener.boneIsUsed(false, 1);
						listener.boneIsUsed(false, 2);
					}
				}, delay);
				if (journal != null)
					journal.put(move);
				break;
			}
			/*
			 * if (move.getUsebBone() == CompleteMove.FIRST) {
			 * 
			 * } else if (move.getPosDiff() == second) {
			 * 
			 * } else if (move.getPosDiff() == first + second) {
			 * 
			 * }
			 */
		} else {
			if(move.getTo().getUsedBone() > 0){
				movesLeft -= move.getTo().getUsedBone();
				new Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						listener.boneIsUsed(true, movesLeft);
					}
				}, delay);
				if (journal != null)
					journal.put(move);
			}
		}
	}

	public boolean hasMoreMooves() {
		if (!moveExist())
			return false;
		if (!double_mode)
			return (first == Integer.MAX_VALUE && second == Integer.MAX_VALUE) ? false
					: true;
		else
			return (movesLeft == 0) ? false : true;
	}

	public int movesLeft() {
		return movesLeft;
	}

	protected boolean moveExist() {
		boolean answer = false;
		int focus = stack.focusedChip;
		for (int i = 0; i < stack.getLength(); i++) {
			if (stack.getPositionAt(i) == 0 && !firstMoveByRound)
				continue;
			stack.setFocus(i);
			if (getAviableMoves().size() > 0)
				answer = true;
		}
		stack.setFocus(focus);
		return answer;
	}

	public int revert(int diff, int used) {
		if (!double_mode) {
			switch (used) {
			case CompleteMove.FIRST:
				this.first = reFirst;
				break;
			case CompleteMove.SECOND:
				this.second = reSecond;
				break;
			case CompleteMove.BOTH:
				this.first = reFirst;
				this.second = reSecond;
				break;
			}
			if (first != Integer.MAX_VALUE && second != Integer.MAX_VALUE)
				firstMoveByRound = true;
			return -1;
		} else {
			movesLeft += used;
			if (movesLeft == 4)
				firstMoveByRound = true;
			return movesLeft;
		}
	}
	protected boolean canSetToPos(int pos){
		
		if(!opponent.isAnyInLastQuoter()){
			int tmpPos = pos;
			int chips_around = 0;
			while(true){
				if(--tmpPos < 0)
					break;
				if(stack.chipsAtPos(tmpPos) == 0)
					break;
				chips_around++;
			}
			tmpPos = pos;
			while(true){
				if(++tmpPos >= GameBoard.CHIPS_COUNT)
					break;
				if(stack.chipsAtPos(tmpPos) == 0)
					break;
				chips_around++;
			}
			if(chips_around + 1 >5)
				return false;
			else 
				return true;
			
		}else{
			return true;
		}
	}
	protected abstract Vector<MoveBind> gedFinishMove();
}
