package com.senya.backgammon;

import java.util.Vector;

import com.backgammon.dices.DiceManager;
import com.backgammon.dices.DiceManager.DiceStates;

public abstract class Move implements Cloneable{
	public boolean firstMoveByRound;
	protected Stack stack;
	protected Stack opponent;
	protected float chipSize;
	protected MoveFinishListener listener;
	protected Journal journal;

	private boolean isFirstMove = true;
	
	public Move(Stack stack, Stack opponent, float chipSize,
			MoveFinishListener lis, Journal j) {
		this.stack = stack;
		this.opponent = opponent;
		this.chipSize = chipSize;
		listener = lis;
		this.journal = j;
		this.firstMoveByRound = true;
	}

	public abstract Vector<MoveBind> getAviableMoves();
	public abstract Vector<MoveBind> getAviableMoves(int which);

	public void useDice(CompleteMove move) {
		if (move.getPosDiff() > 0 && move.getFrom().getTargetPosition() == 0) {
			if(!canTakeTwoChips()){
				firstMoveByRound = false;
			}
			if(isFirstMove)
				isFirstMove = false;
		}
	}

	private boolean canTakeTwoChips(){
		if(isFirstMove){
			if(DiceManager.getInstance().getCurrentState() == DiceStates.Double){
				int diceValue = DiceManager.getInstance().getFirstDice(); 
				if(diceValue == 3 || diceValue == 4 || diceValue == 6){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hasMoreMoves() {
		int first = DiceManager.getInstance().getFirstDice();
		int second = DiceManager.getInstance().getSecondDice();
		boolean double_mode = DiceManager.getInstance().isDoubleMode();
		int movesLeft = DiceManager.getInstance().getAvailableMovesCount();

		if (!moveExist())
			return false;
		if (!double_mode)
			return !(first == Integer.MAX_VALUE && second == Integer.MAX_VALUE);
		else
			return (movesLeft == 0) ? false : true;
	}

	protected boolean moveExist() {
		boolean answer = false;
		synchronized (journal) {
			//int focus = stack.focusedChip;
			for (int i = 0; i < stack.getLength(); i++) {
				if (stack.getPositionAt(i) == 0 && !firstMoveByRound)
					continue;
				//stack.setFocus(i);
				if(!stack.isAllInLastQuoter()){
					if (getAviableMoves(i).size() > 0)
						answer = true;
				}else{
					if (getFinishMove(i).size() > 0)
						answer = true;
				}
			}
			//stack.setFocus(focus);
		}
		return answer;
	}

	public void revert(/* int diff, int used */) {
		int first = DiceManager.getInstance().getFirstDice();
		int second = DiceManager.getInstance().getSecondDice();
		boolean double_mode = DiceManager.getInstance().isDoubleMode();
		if (!double_mode) {
			if (first != Integer.MAX_VALUE && second != Integer.MAX_VALUE)
				firstMoveByRound = true;
		} else {
			int movesLeft = DiceManager.getInstance().getAvailableMovesCount();
			if (movesLeft == 4)
				firstMoveByRound = true;
		}
		if(stack.chipsAtPos(0) == 15)
			isFirstMove = true;
	}

	protected boolean canSetToPos(int pos) {
		if (!opponent.isAnyInLastQuoter()) {
			int tmpPos = pos;
			int chips_around = 0;
			while (--tmpPos != pos) {
				if (tmpPos < 0)
					tmpPos = BoardPositions.POSITIONS_COUNT - 1;
				if (stack.chipsAtPos(tmpPos) == 0)
					break;
				chips_around++;
			}
			tmpPos = pos;
			while (++tmpPos != pos) {
				if (tmpPos >= BoardPositions.POSITIONS_COUNT)
					tmpPos = 0;
				if (stack.chipsAtPos(tmpPos) == 0)
					break;
				chips_around++;
			}
			if (chips_around + 1 > 5)
				return false;
			else
				return true;

		} else {
			return true;
		}
	}

	protected abstract Vector<MoveBind> gedFinishMove();
	protected abstract Vector<MoveBind> getFinishMove(int which);
}
