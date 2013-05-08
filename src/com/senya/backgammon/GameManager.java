package com.senya.backgammon;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;

import com.backgammon.dices.DiceManager;
import com.backgammon.dices.DiceManager.DiceStates;
import com.backgammon.dices.DiceRolledListener;

public class GameManager implements MoveFinishListener, DiceRolledListener {
	public static final int ACTIVE_PLAYER_FIRST = 0;
	public static final int ACTIVE_PLAYER_SECOND = 1;

	private Stack first;
	private Stack second;
	private Stack current;
	private MoveDrawer.mCallBack moveCallBack;
	private String ITS_DRAW;
	private String BLACKS;
	private String REDS;
	private String GO_FIRST;
	private String TAP_TO_ROLL;
	private String HAS_BEEN_WON;

	private static GameManager instance;

	private GameManager() {
	}

	public void setCallBack(MoveDrawer.mCallBack callBack) {
		this.moveCallBack = callBack;
	}

	public void Init(Stack w, BackwardStack b, Context c) {
		this.first = w;
		this.second = b;
		this.current = first;
		DiceManager.getInstance().addRolledListener(this);
		ITS_DRAW = (String) c.getText(R.string.its_draw_try_again);
		BLACKS = (String) c.getText(R.string.blacks);
		REDS = (String) c.getText(R.string.reds);
		GO_FIRST = (String) c.getText(R.string.go_first);
		TAP_TO_ROLL = (String) c.getText(R.string.tap_to_roll);
		HAS_BEEN_WON = (String) c.getText(R.string.has_been_won);
	}

	public static GameManager getInstance() {
		if (instance == null)
			instance = new GameManager();
		return instance;
	}

	@Override
	public void boneIsUsed(int i) {
		DiceManager.getInstance().useDice(i);
		if (GameActivity.needAutoroll && !current.move.hasMoreMoves())
			runAutoRoll();
	}

	@Override
	public void moveComplete() {
	}

	@Override
	public void onDiceRolled(int first, int second) {
		// current.move.setBones(first, second);
		if (DiceManager.getInstance().getCurrentState() == DiceStates.Chellange) {
			if (first == second) {
				DiceManager.getInstance().hintString = ITS_DRAW;
				return;
			}

			if (first > second) {
				current = this.first;
				current.dropFocus();
				DiceManager.getInstance().hintString = BLACKS.concat(" ")
						.concat(GO_FIRST).concat(".").concat(TAP_TO_ROLL);
				// DiceManager.getInstance().setState(DiceStates.Normal);
				needRollDice = true;
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						DiceManager.getInstance().hintString = null;
					}
				}, 5000);
			} else if (second > first) {
				DiceManager.getInstance().hintString = REDS.concat(" ").concat(
						GO_FIRST);
				current = this.second;
				current.dropFocus();
				needRollDice = false;
				if (current instanceof Bot) {
					DiceManager.getInstance().rollDice(false);
				} else {
					DiceManager.getInstance().hintString = REDS.concat(" ")
							.concat(GO_FIRST).concat(".").concat(TAP_TO_ROLL);
					// DiceManager.getInstance().setState(DiceStates.Normal);
					needRollDice = true;
				}
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						DiceManager.getInstance().hintString = null;
					}
				}, 5000);
			}

			return;
		} else if (GameActivity.needAutoroll && !current.move.hasMoreMoves()) {
			runAutoRoll();
		} else {
			needRollDice = false;
		}
		if (current instanceof Bot)
			current.makeMove();
	}

	public void Done() {
		if (!current.move.hasMoreMoves()) {
			switchUser();
			DiceManager.getInstance().rollDice(false);
		}
	}

	private void switchUser() {
		current.dropFocus();
		if (current == first)
			current = second;
		else if (current == second)
			current = first;
		current.move.firstMoveByRound = true;
		Journal.getInstance().clear();
	}

	public void makeMove() {
		current.makeMove();
	}

	public static float lastX;
	public static float lastY;
	public boolean movingMode = false;
	public static boolean fastMoveNeeded = true;

	public void actionDown(float x, float y) {
		if (current instanceof Bot)
			return;
		MoveBind target = intersectedWithTarget(x, y);
		if (target != null) {
			CompleteMove move = current.fastMoveFocused(target, 0);
			postMove(move);
			fastMoveNeeded = false;
		} else if (current.setFocus(x, y)) {
			movingMode = true;
			lastX = x;
			lastY = y;
			fastMoveNeeded = true;
			if (this.moveCallBack != null)
				moveCallBack.ActionDown(x, y,
						current instanceof ForvardStack ? 0 : 255);
		}
	}

	public boolean needRollDice = false;

	public void actionUp() {
		if (current instanceof Bot)
			return;
		else if (needRollDice) {
			DiceManager.getInstance().rollDice(false);
			DiceManager.getInstance().hintString = null;
			return;
		}
		movingMode = false;
		if (fastMoveNeeded && current.hasFocus()) {
			if (this.moveCallBack != null)
				moveCallBack.ActionErace(
						current.getChips()[current.focusedChip]
								.getPhisicalPoint().x,
						current.getChips()[current.focusedChip]
								.getPhisicalPoint().y);
			CompleteMove move = current.fastMoveFocused();
			postMove(move);
		}
	}

	public void postMove(CompleteMove move) {
		if (move.getTo().usedBone != CompleteMove.NONE) {
			Journal.getInstance().put(move);
			DiceManager.getInstance().useDice(move.getTo().getUsedBone());
			current.move.useDice(move);
			if (isWin()) {
				if (gameResultListeners != null) {
					String message = current == first ? BLACKS : REDS;
					message += " ".concat(HAS_BEEN_WON);
					gameResultListeners.onGameResult(message);
				}
			} else if (GameActivity.needAutoroll
					&& !current.move.hasMoreMoves()) {
				runAutoRoll();
			}
		}
	}

	public interface GameResultListener {
		public void onGameResult(String message);
	}

	GameResultListener gameResultListeners;

	public void setGameResultListener(GameResultListener listener) {
		gameResultListeners = listener;
	}

	private boolean isWin() {
		for (int i = 0; i < current.chips.length; i++) {
			if (current.chips[i].getPosition() != BoardPositions.POSITIONS_COUNT)
				return false;
		}
		return true;
	}

	public void actionMove(float x, float y) {
		if (current instanceof Bot)
			return;
		if (movingMode) {
			current.moveFocused(x - lastX, y - lastY);
			if (this.moveCallBack != null)
				moveCallBack.ActionDrag(current.getChips()[current.focusedChip]
						.getPhisicalPoint().x,
						current.getChips()[current.focusedChip]
								.getPhisicalPoint().y);
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

	public void drawTargets(Canvas c) {
		if (current.hasFocus() && !(current instanceof Bot)) {
			Vector<MoveBind> targets = current.move.getAviableMoves();
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

	public boolean isUndoSupport() {
		return !(current instanceof Bot);
	}

	public boolean isCurrentHasMoreMoves() {
		return current.move.hasMoreMoves();
	}

	public int getCurrent() {
		return current instanceof ForvardStack ? ACTIVE_PLAYER_FIRST
				: ACTIVE_PLAYER_SECOND;
	}

	public void setCurrent(int which) {
		if (which == ACTIVE_PLAYER_FIRST)
			current = first;
		else if (which == ACTIVE_PLAYER_SECOND)
			current = second;
	}

	public void revertLast() {
		if (GameActivity.needAutoroll && stpe != null) {
			stpe.shutdownNow();
			stpe = null;
		}
		CompleteMove last = Journal.getInstance().get();
		if (last != null) {
			current.setFocus(last.getChipNum());
			current.fastMoveFocused(last.getFrom(), 0);

			DiceManager.getInstance().revertDices(last);
			current.move.revert();
		}
	}

	public boolean needSave() {
		for (int i = 0; i < first.getLength(); i++) {
			if (first.getPositionAt(i) != 0)
				return true;
		}
		for (int i = 0; i < second.getLength(); i++) {
			if (second.getPositionAt(i) != 0)
				return true;
		}
		return false;
	}

	Paint p = new Paint();

	public void drawWhichMove(Canvas c) {
		if (current != null
				&& DiceManager.getInstance().getCurrentState() != DiceManager.DiceStates.Chellange
				&& DiceManager.getInstance().getCurrentState() != DiceManager.DiceStates.None) {
			
			Bitmap btmp = (current instanceof ForvardStack)?GameBoard.blackMark:GameBoard.redMark;
			c.drawBitmap(btmp, c.getWidth() / 2 - btmp.getWidth()/2,
					c.getHeight() / 2 - btmp.getHeight() / 2, p);
		}
	}

	// Timer timer = new Timer();
	ScheduledThreadPoolExecutor stpe;
	private static final int DEFAULT_AUTOROLL_DELAY = 1000;

	private void runAutoRoll() {
		stpe = new ScheduledThreadPoolExecutor(1);
		stpe.schedule(new Runnable() {
			@Override
			public void run() {
				switchUser();
				DiceManager.getInstance().rollDice(false);
			}
		}, DEFAULT_AUTOROLL_DELAY, TimeUnit.MILLISECONDS);
	}
}