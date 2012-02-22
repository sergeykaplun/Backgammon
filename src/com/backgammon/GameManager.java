package com.backgammon;

import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;

public class GameManager implements MoveFinishListener {
	private int firstBone;
	private int secondBone;
	public static boolean isFirstUsed = false;
	public static boolean isSecondUsed = false;
	private boolean double_mode = false;
	private static int moves_left;

	private Stack first;
	private Stack second;

	private Stack current;

	private boolean diceRolled;
	private boolean donePressed;
	private boolean rolling;

	private static GameManager instance;

	private GameManager() {

	}

	//public void Init(ForvardStack w, BackwardStack b) {
	public void Init(Stack w, BackwardStack b) {
		this.first = w;
		this.second = b;
		this.current = first;
		diceRolled = false;
		rolling = false;
	}

	public static GameManager getInstance() {
		if (instance == null)
			instance = new GameManager();
		return instance;
	}

	public void drawBones(Canvas c) {
		Paint paint = new Paint();
		if (!rolling) {
			if (!double_mode) {
				c.drawBitmap(
						(isFirstUsed) ? GameBoard.RED_BONES[firstBone - 1]
								: GameBoard.GREY_BONES[firstBone - 1],
						GameBoard.boadrWidth
								/ 4
								* 3
								- GameBoard.GREY_BONES[firstBone - 1]
										.getWidth(),
						GameBoard.boardHeight
								/ 2
								- GameBoard.GREY_BONES[firstBone - 1]
										.getHeight() / 2, paint);
				c.drawBitmap(
						(isSecondUsed) ? GameBoard.RED_BONES[secondBone - 1]
								: GameBoard.GREY_BONES[secondBone - 1],
						GameBoard.boadrWidth / 4 * 3/*
													 * + GameBoard.GREY_BONES[
													 * firstBone - 1].getWidth()
													 */,
						GameBoard.boardHeight
								/ 2
								- GameBoard.GREY_BONES[firstBone - 1]
										.getHeight() / 2, paint);
			} else {
				int count = 1;
				for (int i = 0; i < 4; i++) {
					// paint.setColor((count++ > moves_left) ? Color.RED :
					// Color.BLACK);
					switch (i) {
					case 0:
						c.drawBitmap(
								(count++ > moves_left) ? GameBoard.RED_BONES[firstBone - 1]
										: GameBoard.GREY_BONES[firstBone - 1],
								(GameBoard.boadrWidth / 4 * 3 - GameBoard.GREY_BONES[firstBone - 1]
										.getWidth()),
								GameBoard.boardHeight
										/ 2
										- GameBoard.GREY_BONES[firstBone - 1]
												.getHeight(), paint);
						break;
					case 1:
						c.drawBitmap(
								(count++ > moves_left) ? GameBoard.RED_BONES[firstBone - 1]
										: GameBoard.GREY_BONES[firstBone - 1],
								GameBoard.boadrWidth / 4 * 3,
								GameBoard.boardHeight
										/ 2
										- GameBoard.GREY_BONES[firstBone - 1]
												.getHeight(), paint);
						break;
					case 2:
						c.drawBitmap(
								(count++ > moves_left) ? GameBoard.RED_BONES[firstBone - 1]
										: GameBoard.GREY_BONES[firstBone - 1],
								(GameBoard.boadrWidth / 4 * 3 - GameBoard.GREY_BONES[firstBone - 1]
										.getWidth()),
								GameBoard.boardHeight / 2, paint);
						break;
					case 3:
						c.drawBitmap(
								(count++ > moves_left) ? GameBoard.RED_BONES[firstBone - 1]
										: GameBoard.GREY_BONES[firstBone - 1],
								GameBoard.boadrWidth / 4 * 3,
								GameBoard.boardHeight / 2, paint);
						break;
					}
				}
			}
		} else {
			c.drawBitmap(GameBoard.GREY_BONES[firstBone - 1],
					GameBoard.boadrWidth / 4 * 3
							- GameBoard.GREY_BONES[firstBone - 1].getWidth(),
					GameBoard.boardHeight / 2
							- GameBoard.GREY_BONES[firstBone - 1].getHeight()
							/ 2, paint);
			c.drawBitmap(GameBoard.GREY_BONES[secondBone - 1],
					GameBoard.boadrWidth / 4 * 3, GameBoard.boardHeight / 2
							- GameBoard.GREY_BONES[firstBone - 1].getHeight()
							/ 2, paint);
		}

	}

	@Override
	public void boneIsUsed(boolean double_mode, int i) {
		if (!double_mode) {
			switch (i) {
			case 1:
				isFirstUsed = true;
				break;
			case 2:
				isSecondUsed = true;
			default:
				break;
			}
		} else {
			moves_left = i;
		}
	}

	@Override
	public void Continue() {
		Done();
	}

	public boolean Done() {
		if (!current.move.hasMoreMooves() && !donePressed) {
			diceRolled = false;
			donePressed = true;

			switchUser();

			if (current instanceof Bot)
				rollDice();
			return true;
		} else if(!current.move.hasMoreMooves()){
			rollDice();
			return true;
		}else
			return false;
	}

	private void switchUser() {
		current.dropFocus();
		if (current == first)
			current = second;
		else if (current == second)
			current = first;
		Journal.getInstance().clear();
	}

	public boolean rollDice() {
		if (!diceRolled) {
			new Timer().schedule(new Rolling(), 0, 75);
			/*
			 * Random rnd = new Random(); int f = rnd.nextInt(6) + 1; int s =
			 * rnd.nextInt(6) + 1; firstBone = f; isFirstUsed = false;
			 * secondBone = s; isSecondUsed = false; current.move.setBones(f,
			 * s); if (f != s) { double_mode = false; } else { double_mode =
			 * true; moves_left = 4; }
			 * 
			 * diceRolled = true; donePressed = false;
			 */
			return true;
		} else
			return false;
	}
	public void makeMove(){
		current.makeMove();
	}

	public static float lastX;
	public static float lastY;
	public boolean movingMode = false;
	public static boolean fastMoveNeeded = true;

	public void actionDown(float x, float y) {
		MoveBind target = intersectedWithTarget(x, y);
		if (target != null) {
			current.move.useBone(current.fastMoveFocused(target, 0));
			fastMoveNeeded = false;
		} else if (current.setFocus(x, y)) {
			movingMode = true;
			lastX = x;
			lastY = y;
			fastMoveNeeded = true;
		}
	}

	public void actionUp() {
		movingMode = false;
		if (fastMoveNeeded && current.hasFocus()) {
			CompleteMove move = current.fastMoveFocused();
			current.move.useBone(move);
		}
	}

	public void actionMove(float x, float y) {
		if (movingMode) {
			current.moveFocused(x - lastX, y - lastY);
			lastX = x;
			lastY = y;
		}
	}

	private MoveBind intersectedWithTarget(float x, float y) {
		MoveBind res = null;

		Vector<MoveBind> targets = current.move.getAviableMoves();
		for (Iterator<MoveBind> iterator = targets.iterator(); iterator
				.hasNext();) {
			MoveBind moveBind = (MoveBind) iterator.next();
			if (x < moveBind.getTargetPoint().x + GameBoard.chipSize / 2
					&& x > moveBind.getTargetPoint().x - GameBoard.chipSize / 2
					&& y < moveBind.getTargetPoint().y + GameBoard.chipSize / 2
					&& y > moveBind.getTargetPoint().y - GameBoard.chipSize / 2) {
				res = moveBind;
				break;
			}
		}
		return res;
	}

	public Vector<MoveBind> getMoveBinds() {
		return current.move.getAviableMoves();
	}

	public boolean hasFocus() {
		return current.hasFocus();
	}

	public void drawTargets(Canvas c) {
		if (hasFocus()) {
			Vector<MoveBind> targets = GameManager.getInstance().getMoveBinds();
			for (Iterator<MoveBind> iterator = targets.iterator(); iterator
					.hasNext();) {
				MoveBind moveBind = (MoveBind) iterator.next();
				RadialGradient gradient = new RadialGradient(
						moveBind.getTargetPoint().x,
						moveBind.getTargetPoint().y, GameBoard.chipSize * 0.4f,
						(current == first) ? 0x6600FF00 : 0x66FF0000,
						(current == first) ? 0x0000FF00 : 0x00FF0000,
						android.graphics.Shader.TileMode.CLAMP);
				Paint p = new Paint();
				p.setDither(true);
				p.setShader(gradient);

				c.drawCircle(moveBind.getTargetPoint().x,
						moveBind.getTargetPoint().y, GameBoard.chipSize * 0.4f,
						p);
			}
		}
	}

	public void revertLast() {
		CompleteMove last = Journal.getInstance().get();
		if (last != null) {
			current.setFocus(last.getChipNum());
			current.fastMoveFocused(last.getFrom(), 0);
			current.move.revert(last.getPosDiff(), last.getTo().getUsedBone());

			switch (last.getTo().getUsedBone()) {
			case CompleteMove.FIRST:
				isFirstUsed = false;
				break;
			case CompleteMove.SECOND:
				isSecondUsed = false;
				break;
			case CompleteMove.BOTH:
				isFirstUsed = false;
				isSecondUsed = false;
				break;
			case CompleteMove.NONE:
				break;
			default:
				moves_left += last.getTo().getUsedBone();
			}
		}
	}

	public void dropFocus() {
		current.dropFocus();
	}

	public boolean hasMoreMooves() {
		return current.move.hasMoreMooves();
	}

	private class Rolling extends TimerTask {
		int count;
		private Random r;// = new Random();

		public Rolling() {
			count = 0;
			r = new Random();
			rolling = true;
		}

		@Override
		public void run() {
			if (count++ < 15) {
				int f = r.nextInt(6) + 1;
				int s = r.nextInt(6) + 1;
				firstBone = f;
				secondBone = s;
			} else {
				rolling = false;
				int f = r.nextInt(6) + 1;
				int s = r.nextInt(6) + 1;
				firstBone = f;
				isFirstUsed = false;
				secondBone = s;
				isSecondUsed = false;
				current.move.setBones(f, s);
				if (f != s) {
					double_mode = false;
				} else {
					double_mode = true;
					moves_left = 4;
				}

				diceRolled = true;
				donePressed = false;
				if (current instanceof Bot)
					current.makeMove();
				this.cancel();
			}
		}
	}
}