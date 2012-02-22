package com.backgammon;

import java.util.Iterator;
import java.util.Vector;

import android.graphics.Point;

public class ForvardMove extends Move {

	public ForvardMove(Stack stack, Stack opponent, float chipSize,
			MoveFinishListener lis, Journal j) {
		super(stack, opponent, chipSize, lis, j);
	}

	@Override
	public Vector<MoveBind> getAviableMoves() {
		Vector<MoveBind> res = new Vector<MoveBind>();
		
		if(stack.getFocusedChipPosition() == BoardPositions.POSITIONS_COUNT)
			return res;
		
		if(stack.isAllInLastQuoter()){
			return this.gedFinishMove();
		}
		
		if (stack.getFocusedChipPosition() == 0 && !firstMoveByRound)
			return res;
		
		if (!double_mode) {
			if (first != Integer.MAX_VALUE
					&& stack.getFocusedChipPosition() + first < BoardPositions.POSITIONS_COUNT
					&& opponent.chipsAtPos(BoardPositions.getInstance()
							.ConvertPosition(
									stack.getFocusedChipPosition() + first)) == 0 && canSetToPos(stack.getFocusedChipPosition() + first)) {
				int offset = stack.chipsAtPos(stack.getFocusedChipPosition()
						+ first)
						* (int) chipSize;
				if (stack.getFocusedChipPosition() + first < BoardPositions.POSITIONS_COUNT / 2)
					offset = -offset;
				res.add(new MoveBind(new Point(
						BoardPositions.getInstance().getClockwiseAt(
								stack.getFocusedChipPosition() + first).x,
						BoardPositions.getInstance().getClockwiseAt(
								stack.getFocusedChipPosition() + first).y
								+ offset), stack.getFocusedChipPosition()
						+ first,CompleteMove.FIRST));
			}
			if (second != Integer.MAX_VALUE
					&& stack.getFocusedChipPosition() + second < BoardPositions.POSITIONS_COUNT
					&& opponent.chipsAtPos(BoardPositions.getInstance()
							.ConvertPosition(
									stack.getFocusedChipPosition() + second)) == 0  && canSetToPos(stack.getFocusedChipPosition() + second)) {
				int offset = stack.chipsAtPos(stack.getFocusedChipPosition()
						+ second)
						* (int) chipSize;
				if (stack.getFocusedChipPosition() + second < BoardPositions.POSITIONS_COUNT / 2)
					offset = -offset;
				res.add(new MoveBind(new Point(
						BoardPositions.getInstance().getClockwiseAt(
								stack.getFocusedChipPosition() + second).x,
						BoardPositions.getInstance().getClockwiseAt(
								stack.getFocusedChipPosition() + second).y
								+ offset), stack.getFocusedChipPosition()
						+ second,CompleteMove.SECOND));
			}
			if ((first != Integer.MAX_VALUE && second != Integer.MAX_VALUE)
					&& stack.getFocusedChipPosition() + first + second < BoardPositions.POSITIONS_COUNT
					&& (opponent.chipsAtPos(BoardPositions.getInstance()
							.ConvertPosition(
									stack.getFocusedChipPosition() + first)) == 0 || opponent
							.chipsAtPos(BoardPositions.getInstance()
									.ConvertPosition(
											stack.getFocusedChipPosition()
													+ second)) == 0)
					&& opponent.chipsAtPos(BoardPositions.getInstance()
							.ConvertPosition(
									stack.getFocusedChipPosition() + first
											+ second)) == 0  && canSetToPos(stack.getFocusedChipPosition() + first + second)) {
				int offset = stack.chipsAtPos(stack.getFocusedChipPosition()
						+ first + second)
						* (int) chipSize;
				if (stack.getFocusedChipPosition() + first + second < BoardPositions.POSITIONS_COUNT / 2)
					offset = -offset;
				res.add(new MoveBind(new Point(
						BoardPositions.getInstance()
								.getClockwiseAt(
										stack.getFocusedChipPosition() + first
												+ second).x, BoardPositions
								.getInstance().getClockwiseAt(
										stack.getFocusedChipPosition() + first
												+ second).y
								+ offset), stack.getFocusedChipPosition()
						+ first + second, CompleteMove.BOTH));
			}
		} else {
			for (int i = 0; i < movesLeft; i++) {
				if (first != Integer.MAX_VALUE
						&& stack.getFocusedChipPosition() + first * (i + 1) < BoardPositions.POSITIONS_COUNT
						&& opponent.chipsAtPos(BoardPositions.getInstance()
								.ConvertPosition(
										stack.getFocusedChipPosition() + first
												* (i + 1))) == 0  && canSetToPos(stack.getFocusedChipPosition() + first * (i + 1))) {
					int offset = stack.chipsAtPos(stack
							.getFocusedChipPosition() + first * (i + 1))
							* (int) chipSize;

					if (stack.getFocusedChipPosition() + first * (i + 1) < BoardPositions.POSITIONS_COUNT / 2)
						offset = -offset;
					res.add(new MoveBind(new Point(BoardPositions.getInstance()
							.getClockwiseAt(
									stack.getFocusedChipPosition() + first
											* (i + 1)).x, BoardPositions
							.getInstance().getClockwiseAt(
									stack.getFocusedChipPosition() + first
											* (i + 1)).y
							+ offset), stack.getFocusedChipPosition() + first
							* (i + 1),i+1));
				} else
					return res;
			}
		}
		return res;
	}

	@Override
	protected Vector<MoveBind> gedFinishMove() {
		Vector<MoveBind> res = new Vector<MoveBind>();
		if (!double_mode) {
			if (first != Integer.MAX_VALUE) {
				int targetPos = stack.getFocusedChipPosition() + first;
				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance().ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(stack.focusedChip))) {
					if(targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;
					int offset = (int) (stack.chipsAtPos(targetPos) * ((targetPos < BoardPositions.POSITIONS_COUNT) ? GameBoard.chipSize
							: -GameBoard.chipSize));
					res.add(new MoveBind(
							new Point(BoardPositions.getInstance()
									.getClockwiseAt(targetPos).x,
									BoardPositions.getInstance()
											.getClockwiseAt(targetPos).y
											+ offset), targetPos,CompleteMove.FIRST));
				}
			}
			if (second != Integer.MAX_VALUE) {
				
				int targetPos = stack.getFocusedChipPosition() + second;
				
				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance().ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(stack.focusedChip))) {
					if(targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;
					if(targetPos == BoardPositions.POSITIONS_COUNT){
						for (Iterator<MoveBind> iterator = res.iterator(); iterator.hasNext();) {
							MoveBind moveBind = (MoveBind) iterator.next();
							if(moveBind.getTargetPosition() == targetPos)
								return res;
						}
					}
					int offset = (int) (stack.chipsAtPos(targetPos) * ((targetPos < BoardPositions.POSITIONS_COUNT) ? GameBoard.chipSize
							: -GameBoard.chipSize));
					res.add(new MoveBind(
							new Point(BoardPositions.getInstance()
									.getClockwiseAt(targetPos).x,
									BoardPositions.getInstance()
											.getClockwiseAt(targetPos).y
											+ offset), targetPos, CompleteMove.SECOND));
				}
			}
			if (first != Integer.MAX_VALUE && second != Integer.MAX_VALUE) {
				
				int targetPos = stack.getFocusedChipPosition() + first + second;
				
				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance().ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(stack.focusedChip))) {
					if(targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;
					
					if(targetPos == BoardPositions.POSITIONS_COUNT){
						for (Iterator<MoveBind> iterator = res.iterator(); iterator.hasNext();) {
							MoveBind moveBind = (MoveBind) iterator.next();
							if(moveBind.getTargetPosition() == targetPos)
								return res;
						}
					}
					if(stack.getFocusedChipPosition() + first + second > BoardPositions.POSITIONS_COUNT && stack.ifExistAboveOrHere(stack.focusedChip)){
						return res;
					}
					int offset = (int) (stack.chipsAtPos(targetPos) * ((targetPos < BoardPositions.POSITIONS_COUNT) ? GameBoard.chipSize
							: -GameBoard.chipSize));
					res.add(new MoveBind(
							new Point(BoardPositions.getInstance()
									.getClockwiseAt(targetPos).x,
									BoardPositions.getInstance()
											.getClockwiseAt(targetPos).y
											+ offset), targetPos,CompleteMove.BOTH));
				}
			}
		
		} else {
			for (int i = 1; i <= movesLeft; i++) {
				int targetPos = stack.getFocusedChipPosition() + (first * i);
					if ((targetPos == BoardPositions.POSITIONS_COUNT)
							|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
									.chipsAtPos(BoardPositions.getInstance().ConvertPosition(targetPos)) == 0)
							|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
									.ifExistAbove(stack.focusedChip))) {
						if(targetPos > BoardPositions.POSITIONS_COUNT)
							targetPos = BoardPositions.POSITIONS_COUNT;
						int offset = (int) (stack.chipsAtPos(targetPos) * ((targetPos < BoardPositions.POSITIONS_COUNT) ? GameBoard.chipSize
								: -GameBoard.chipSize));
						res.add(new MoveBind(
								new Point(BoardPositions.getInstance()
										.getClockwiseAt(targetPos).x,
										BoardPositions.getInstance()
												.getClockwiseAt(targetPos).y
												+ offset), targetPos,i));
				}else
					return res;
			}
		}
		return res;
	}
}
