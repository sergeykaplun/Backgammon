package com.senya.backgammon;

import java.util.Iterator;
import java.util.Vector;

import com.backgammon.dices.DiceManager;

import android.graphics.Point;
import android.util.Log;

public class ForvardMove extends Move {

	public ForvardMove(Stack stack, Stack opponent, float chipSize,
			MoveFinishListener lis, Journal j) {
		super(stack, opponent, chipSize, lis, j);
	}

	@Override
	public Vector<MoveBind> getAviableMoves() {
		int first = DiceManager.getInstance().getFirstDice();
		int second = DiceManager.getInstance().getSecondDice();
		boolean double_mode = DiceManager.getInstance().isDoubleMode();
		Vector<MoveBind> res = new Vector<MoveBind>();

		if (stack.getFocusedChipPosition() == BoardPositions.POSITIONS_COUNT)
			return res;

		if (stack.isAllInLastQuoter()) {
			return this.gedFinishMove();
		}

		if (stack.getFocusedChipPosition() == 0 && !firstMoveByRound)
			return res;

		if (!double_mode) {
			if (first != Integer.MAX_VALUE
					&& stack.getFocusedChipPosition() + first < BoardPositions.POSITIONS_COUNT
					&& opponent.chipsAtPos(BoardPositions.getInstance()
							.ConvertPosition(
									stack.getFocusedChipPosition() + first)) == 0
					&& canSetToPos(stack.getFocusedChipPosition() + first)) {
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
						+ first, CompleteMove.FIRST));
			}
			if (second != Integer.MAX_VALUE
					&& stack.getFocusedChipPosition() + second < BoardPositions.POSITIONS_COUNT
					&& opponent.chipsAtPos(BoardPositions.getInstance()
							.ConvertPosition(
									stack.getFocusedChipPosition() + second)) == 0
					&& canSetToPos(stack.getFocusedChipPosition() + second)) {
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
						+ second, CompleteMove.SECOND));
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
											+ second)) == 0
					&& canSetToPos(stack.getFocusedChipPosition() + first
							+ second)) {
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
			int movesLeft = DiceManager.getInstance().getAvailableMovesCount();

			for (int i = 0; i < movesLeft; i++) {
				if (first != Integer.MAX_VALUE
						&& stack.getFocusedChipPosition() + first * (i + 1) < BoardPositions.POSITIONS_COUNT
						&& opponent.chipsAtPos(BoardPositions.getInstance()
								.ConvertPosition(
										stack.getFocusedChipPosition() + first
												* (i + 1))) == 0
						&& canSetToPos(stack.getFocusedChipPosition() + first
								* (i + 1))) {
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
							* (i + 1), i + 1));
				} else
					return res;
			}
		}
		return res;
	}

	@Override
	public Vector<MoveBind> getAviableMoves(int which) {
		int first = DiceManager.getInstance().getFirstDice();
		int second = DiceManager.getInstance().getSecondDice();
		boolean double_mode = DiceManager.getInstance().isDoubleMode();
		Vector<MoveBind> res = new Vector<MoveBind>();

		if (stack.getPositionAt(which) == BoardPositions.POSITIONS_COUNT)
			return res;

		/*if (stack.isAllInLastQuoter()) {
			return this.gedFinishMove();
		}*/

		if (stack.getPositionAt(which) == 0 && !firstMoveByRound)
			return res;

		if (!double_mode) {
			if (first != Integer.MAX_VALUE
					&& stack.getPositionAt(which) + first < BoardPositions.POSITIONS_COUNT
					&& opponent.chipsAtPos(BoardPositions.getInstance()
							.ConvertPosition(
									stack.getPositionAt(which) + first)) == 0
					&& canSetToPos(stack.getPositionAt(which) + first)) {
				int offset = stack.chipsAtPos(stack.getFocusedChipPosition()
						+ first)
						* (int) chipSize;
				if (stack.getPositionAt(which) + first < BoardPositions.POSITIONS_COUNT / 2)
					offset = -offset;
				res.add(new MoveBind(new Point(
						BoardPositions.getInstance().getClockwiseAt(
								stack.getPositionAt(which) + first).x,
						BoardPositions.getInstance().getClockwiseAt(
								stack.getPositionAt(which) + first).y
								+ offset), stack.getPositionAt(which)
						+ first, CompleteMove.FIRST));
			}
			if (second != Integer.MAX_VALUE
					&& stack.getPositionAt(which) + second < BoardPositions.POSITIONS_COUNT
					&& opponent.chipsAtPos(BoardPositions.getInstance()
							.ConvertPosition(
									stack.getPositionAt(which) + second)) == 0
					&& canSetToPos(stack.getPositionAt(which) + second)) {
				int offset = stack.chipsAtPos(stack.getPositionAt(which)
						+ second)
						* (int) chipSize;
				if (stack.getPositionAt(which) + second < BoardPositions.POSITIONS_COUNT / 2)
					offset = -offset;
				res.add(new MoveBind(new Point(
						BoardPositions.getInstance().getClockwiseAt(
								stack.getPositionAt(which) + second).x,
						BoardPositions.getInstance().getClockwiseAt(
								stack.getPositionAt(which) + second).y
								+ offset), stack.getPositionAt(which)
						+ second, CompleteMove.SECOND));
			}
			if ((first != Integer.MAX_VALUE && second != Integer.MAX_VALUE)
					&& stack.getPositionAt(which) + first + second < BoardPositions.POSITIONS_COUNT
					&& (opponent.chipsAtPos(BoardPositions.getInstance()
							.ConvertPosition(
									stack.getPositionAt(which) + first)) == 0 || opponent
							.chipsAtPos(BoardPositions.getInstance()
									.ConvertPosition(
											stack.getPositionAt(which)
													+ second)) == 0)
					&& opponent.chipsAtPos(BoardPositions.getInstance()
							.ConvertPosition(
									stack.getPositionAt(which) + first
											+ second)) == 0
					&& canSetToPos(stack.getPositionAt(which) + first
							+ second)) {
				int offset = stack.chipsAtPos(stack.getPositionAt(which)
						+ first + second)
						* (int) chipSize;
				if (stack.getPositionAt(which) + first + second < BoardPositions.POSITIONS_COUNT / 2)
					offset = -offset;
				res.add(new MoveBind(new Point(
						BoardPositions.getInstance()
								.getClockwiseAt(
										stack.getPositionAt(which) + first
												+ second).x, BoardPositions
								.getInstance().getClockwiseAt(
										stack.getPositionAt(which) + first
												+ second).y
								+ offset), stack.getPositionAt(which)
						+ first + second, CompleteMove.BOTH));
			}
		} else {
			int movesLeft = DiceManager.getInstance().getAvailableMovesCount();

			for (int i = 0; i < movesLeft; i++) {
				if (first != Integer.MAX_VALUE
						&& stack.getPositionAt(which) + first * (i + 1) < BoardPositions.POSITIONS_COUNT
						&& opponent.chipsAtPos(BoardPositions.getInstance()
								.ConvertPosition(
										stack.getPositionAt(which) + first
												* (i + 1))) == 0
						&& canSetToPos(stack.getPositionAt(which) + first
								* (i + 1))) {
					int offset = stack.chipsAtPos(stack
							.getPositionAt(which) + first * (i + 1))
							* (int) chipSize;

					if (stack.getPositionAt(which) + first * (i + 1) < BoardPositions.POSITIONS_COUNT / 2)
						offset = -offset;
					res.add(new MoveBind(new Point(BoardPositions.getInstance()
							.getClockwiseAt(
									stack.getPositionAt(which) + first
											* (i + 1)).x, BoardPositions
							.getInstance().getClockwiseAt(
									stack.getPositionAt(which) + first
											* (i + 1)).y
							+ offset), stack.getPositionAt(which) + first
							* (i + 1), i + 1));
				} else
					return res;
			}
		}
		return res;
	}

	@Override
	protected Vector<MoveBind> gedFinishMove() {
		int first = DiceManager.getInstance().getFirstDice();
		int second = DiceManager.getInstance().getSecondDice();
		boolean double_mode = DiceManager.getInstance().isDoubleMode();
		Vector<MoveBind> res = new Vector<MoveBind>();
		if (!double_mode) {
			if (first != Integer.MAX_VALUE) {
				int targetPos = stack.getFocusedChipPosition() + first;
				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance()
										.ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(stack.focusedChip))) {
					if (targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;
					int offset = (int) (stack.chipsAtPos(targetPos) * ((targetPos < BoardPositions.POSITIONS_COUNT) ? GameBoard.chipSize
							: -GameBoard.chipSize));
					res.add(new MoveBind(
							new Point(BoardPositions.getInstance()
									.getClockwiseAt(targetPos).x,
									BoardPositions.getInstance()
											.getClockwiseAt(targetPos).y
											+ offset), targetPos,
							CompleteMove.FIRST));
				}
			}
			if (second != Integer.MAX_VALUE) {

				int targetPos = stack.getFocusedChipPosition() + second;

				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance()
										.ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(stack.focusedChip))) {
					if (targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;
					if (targetPos == BoardPositions.POSITIONS_COUNT) {
						for (Iterator<MoveBind> iterator = res.iterator(); iterator
								.hasNext();) {
							MoveBind moveBind = (MoveBind) iterator.next();
							if (moveBind.getTargetPosition() == targetPos)
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
											+ offset), targetPos,
							CompleteMove.SECOND));
				}
			}
			if (first != Integer.MAX_VALUE && second != Integer.MAX_VALUE) {

				int targetPos = stack.getFocusedChipPosition() + first + second;

				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance()
										.ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(stack.focusedChip))) {
					if (targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;

					if (targetPos == BoardPositions.POSITIONS_COUNT) {
						for (Iterator<MoveBind> iterator = res.iterator(); iterator
								.hasNext();) {
							MoveBind moveBind = (MoveBind) iterator.next();
							if (moveBind.getTargetPosition() == targetPos)
								return res;
						}
					}
					if (stack.getFocusedChipPosition() + first + second > BoardPositions.POSITIONS_COUNT
							&& stack.ifExistAboveOrHere(stack.focusedChip)) {
						return res;
					}
					int offset = (int) (stack.chipsAtPos(targetPos) * ((targetPos < BoardPositions.POSITIONS_COUNT) ? GameBoard.chipSize
							: -GameBoard.chipSize));
					res.add(new MoveBind(
							new Point(BoardPositions.getInstance()
									.getClockwiseAt(targetPos).x,
									BoardPositions.getInstance()
											.getClockwiseAt(targetPos).y
											+ offset), targetPos,
							CompleteMove.BOTH));
				}
			}

		} else {
			int movesLeft = DiceManager.getInstance().getAvailableMovesCount();
			for (int i = 1; i <= movesLeft; i++) {
				int targetPos = stack.getFocusedChipPosition() + (first * i);
				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance()
										.ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(stack.focusedChip))) {
					if (targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;
					int offset = (int) (stack.chipsAtPos(targetPos) * ((targetPos < BoardPositions.POSITIONS_COUNT) ? GameBoard.chipSize
							: -GameBoard.chipSize));
					res.add(new MoveBind(
							new Point(BoardPositions.getInstance()
									.getClockwiseAt(targetPos).x,
									BoardPositions.getInstance()
											.getClockwiseAt(targetPos).y
											+ offset), targetPos, i));
				} else
					return res;
			}
		}
		return res;
	}
	
	
	@Override
	protected Vector<MoveBind> getFinishMove(int which) {
		int first = DiceManager.getInstance().getFirstDice();
		int second = DiceManager.getInstance().getSecondDice();
		boolean double_mode = DiceManager.getInstance().isDoubleMode();
		Vector<MoveBind> res = new Vector<MoveBind>();
		if (!double_mode) {
			if (first != Integer.MAX_VALUE) {
				int targetPos = stack.getPositionAt(which) + first;
				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance()
										.ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(which))) {
					if (targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;
					int offset = (int) (stack.chipsAtPos(targetPos) * ((targetPos < BoardPositions.POSITIONS_COUNT) ? GameBoard.chipSize
							: -GameBoard.chipSize));
					res.add(new MoveBind(
							new Point(BoardPositions.getInstance()
									.getClockwiseAt(targetPos).x,
									BoardPositions.getInstance()
											.getClockwiseAt(targetPos).y
											+ offset), targetPos,
							CompleteMove.FIRST));
				}
			}
			if (second != Integer.MAX_VALUE) {

				int targetPos = stack.getPositionAt(which) + second;

				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance()
										.ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(which))) {
					if (targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;
					if (targetPos == BoardPositions.POSITIONS_COUNT) {
						for (Iterator<MoveBind> iterator = res.iterator(); iterator
								.hasNext();) {
							MoveBind moveBind = (MoveBind) iterator.next();
							if (moveBind.getTargetPosition() == targetPos)
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
											+ offset), targetPos,
							CompleteMove.SECOND));
				}
			}
			if (first != Integer.MAX_VALUE && second != Integer.MAX_VALUE) {

				int targetPos = stack.getPositionAt(which) + first + second;

				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance()
										.ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(which))) {
					if (targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;

					if (targetPos == BoardPositions.POSITIONS_COUNT) {
						for (Iterator<MoveBind> iterator = res.iterator(); iterator
								.hasNext();) {
							MoveBind moveBind = (MoveBind) iterator.next();
							if (moveBind.getTargetPosition() == targetPos)
								return res;
						}
					}
					if (stack.getPositionAt(which) + first + second > BoardPositions.POSITIONS_COUNT
							&& stack.ifExistAboveOrHere(which)) {
						return res;
					}
					int offset = (int) (stack.chipsAtPos(targetPos) * ((targetPos < BoardPositions.POSITIONS_COUNT) ? GameBoard.chipSize
							: -GameBoard.chipSize));
					res.add(new MoveBind(
							new Point(BoardPositions.getInstance()
									.getClockwiseAt(targetPos).x,
									BoardPositions.getInstance()
											.getClockwiseAt(targetPos).y
											+ offset), targetPos,
							CompleteMove.BOTH));
				}
			}

		} else {
			int movesLeft = DiceManager.getInstance().getAvailableMovesCount();
			for (int i = 1; i <= movesLeft; i++) {
				int targetPos = stack.getPositionAt(which) + (first * i);
				if ((targetPos == BoardPositions.POSITIONS_COUNT)
						|| (targetPos < BoardPositions.POSITIONS_COUNT && opponent
								.chipsAtPos(BoardPositions.getInstance()
										.ConvertPosition(targetPos)) == 0)
						|| (targetPos > BoardPositions.POSITIONS_COUNT && !stack
								.ifExistAbove(which))) {
					if (targetPos > BoardPositions.POSITIONS_COUNT)
						targetPos = BoardPositions.POSITIONS_COUNT;
					int offset = (int) (stack.chipsAtPos(targetPos) * ((targetPos < BoardPositions.POSITIONS_COUNT) ? GameBoard.chipSize
							: -GameBoard.chipSize));
					res.add(new MoveBind(
							new Point(BoardPositions.getInstance()
									.getClockwiseAt(targetPos).x,
									BoardPositions.getInstance()
											.getClockwiseAt(targetPos).y
											+ offset), targetPos, i));
				} else
					return res;
			}
		}
		return res;
	}
}