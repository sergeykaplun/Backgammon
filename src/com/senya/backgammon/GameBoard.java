package com.senya.backgammon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.senya.backgammon.R;
import com.backgammon.dices.DiceManager;
import com.backgammon.dices.DiceManager.DiceStates;

public class GameBoard extends SurfaceView implements SurfaceHolder.Callback {
	public static Bitmap[] GREY_CHIPS;
	public static Bitmap[] RED_CHIPS;
	public static Bitmap[] GREY_BONES;
	public static Bitmap[] RED_BONES;
	public static Bitmap DONE_BUTTON;
	public static Bitmap UNDO_BUTTON;
	private static Bitmap BOARD_BITMAP;
	public static final int CHIPS_COUNT = 15;
	public static final int GAME_BOARD_PADDING = 3;
	// public static int TOOL_BAR_PADDING;

	public static Bitmap redMark;
	public static Bitmap blackMark;
	
	public static float chipSize;
	public static float boadrWidth;
	public static float boardHeight;

	Stack whites;
	BackwardStack blacks;

	private MoveDrawer movesDrawer;

	// public ForvardStack getWhites() {
	public Stack getWhites() {
		return whites;
	}

	public BackwardStack getBlacks() {
		return blacks;
	}

	private GameThread gThread;

	public GameThread getgThread() {
		return gThread;
	}

	public GameBoard(Context context) {
		super(context);
		getHolder().addCallback(this);
		setFocusable(true);
	}

	private void drawBackground(Canvas c) {
		c.drawBitmap(BOARD_BITMAP, 0, 0, new Paint());
	}

	public void drawBoard(Canvas c) {
		// drawBackground(c);
		DiceManager.getInstance().drawDices(c);
		if(GameActivity.needHints)
			GameManager.getInstance().drawTargets(c);
		if (GameManager.getInstance().isUndoSupport()
				&& !Journal.getInstance().isClear())
			c.drawBitmap(UNDO_BUTTON, boadrWidth / 4 - UNDO_BUTTON.getWidth()
					/ 2, boardHeight / 2 - UNDO_BUTTON.getHeight() / 2,
					new Paint());
		/*
		 * c.drawBitmap(GameBoard.DONE_BUTTON, GameBoard.boadrWidth / 4 *3 -
		 * GameBoard.DONE_BUTTON.getWidth() / 2, GameBoard.boardHeight / 2 -
		 * GameBoard.DONE_BUTTON.getHeight() / 2, new Paint());
		 */
		GameManager.getInstance().drawWhichMove(c);
		whites.drawStack(c);
		blacks.drawStack(c);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			drawBackground(canvas);
			movesDrawer.drawMoves(canvas);
			drawBoard(canvas);
		} catch (NullPointerException e) {
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (DiceManager.getInstance().getCurrentState() == DiceStates.None
					|| DiceManager.getInstance().getCurrentState() == DiceStates.Chellange) {
				DiceManager.getInstance().rollDice(true);
				DiceManager.getInstance().hintString = null;
				return true;
			}

			if (!Journal.getInstance().isClear()
					&& isIntersectedWithUndo(event.getX(), event.getY())) {
				GameManager.getInstance().revertLast();
				GameManager.getInstance().fastMoveNeeded = false;
			} else if (!GameActivity.needAutoroll && isIntersectedWithBones(event.getX(), event.getY())) {
				GameManager.getInstance().Done();
				GameManager.getInstance().fastMoveNeeded = false;
			} else
				GameManager.getInstance()
						.actionDown(event.getX(), event.getY());
			break;

		case MotionEvent.ACTION_UP:
			GameManager.getInstance().actionUp();
			break;
		case MotionEvent.ACTION_MOVE:
			GameManager.getInstance().actionMove(event.getX(), event.getY());
			break;
		}
		return true;
	}

	private boolean isIntersectedWithUndo(float x, float y) {
		if (!GameManager.getInstance().isUndoSupport())
			return false;
		if (x < boadrWidth / 4 + UNDO_BUTTON.getWidth() / 2
				&& x > boadrWidth / 4 - UNDO_BUTTON.getWidth() / 2
				&& y < boardHeight / 2 + UNDO_BUTTON.getHeight() / 2
				&& y > boardHeight / 2 - UNDO_BUTTON.getHeight() / 2)
			return true;
		else
			return false;
	}

	private boolean isIntersectedWithBones(float x, float y) {
		if (x < boadrWidth / 4 * 3 + UNDO_BUTTON.getWidth() / 2
				&& x > boadrWidth / 4 * 3 - UNDO_BUTTON.getWidth() / 2
				&& y < boardHeight / 2 + UNDO_BUTTON.getHeight() / 2
				&& y > boardHeight / 2 - UNDO_BUTTON.getHeight() / 2)
			return true;
		else
			return false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		chipSize = (getWidth() - GAME_BOARD_PADDING * 4)
				/ (BoardPositions.POSITIONS_COUNT * 0.5f);
		BoardPositions.getInstance().init(getWidth(), getHeight(), chipSize);

		boadrWidth = getWidth();
		boardHeight = getHeight();
		int bonesize = getWidth() / 8;

		GREY_BONES = new Bitmap[] {
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.b1bone), bonesize, bonesize,
						false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.b2bone), bonesize, bonesize,
						false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.b3bone), bonesize, bonesize,
						false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.b4bone), bonesize, bonesize,
						false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.b5bone), bonesize, bonesize,
						false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.b6bone), bonesize, bonesize,
						false), };
		RED_BONES = new Bitmap[] {
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.r1bone), bonesize, bonesize,
						false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.r2bone), bonesize, bonesize,
						false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.r3bone), bonesize, bonesize,
						false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.r4bone), bonesize, bonesize,
						false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.r5bone), bonesize, bonesize,
						false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.r6bone), bonesize, bonesize,
						false), };

		RED_CHIPS = new Bitmap[] {
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a0), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a1), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a2), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a3), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a4), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a5), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a6), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a7), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a8), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a9), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a10), (int) chipSize,
						(int) chipSize, false), };
		GREY_CHIPS = new Bitmap[] {
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.a0), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.g1), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.g2), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.g3), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.g4), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.g5), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.g6), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.g7), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.g8), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.g9), (int) chipSize,
						(int) chipSize, false),
				Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.g10), (int) chipSize,
						(int) chipSize, false), };

		int buttonSize = getWidth() / 4;

		DONE_BUTTON = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.check),
				buttonSize, buttonSize, true);
		UNDO_BUTTON = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.undo),
				buttonSize, buttonSize, true);
		BOARD_BITMAP = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.whole_board), getWidth(),
				getHeight(), true);

		redMark = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.red_mark), (int) chipSize*2,
				(int) chipSize*2, false);
		blackMark = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.black_mark), (int) chipSize*2,
				(int) chipSize*2, false);
		
		if (GameMode.mode == GameMode.MODE_NEW_GAME) {
			whites = new ForvardStack(CHIPS_COUNT, chipSize, GREY_CHIPS);
			if (GameMode.players == GameMode.TWO_PLAYERS)
				blacks = new BackwardStack(CHIPS_COUNT, chipSize, RED_CHIPS,
						true);
			else {
				blacks = new Bot(CHIPS_COUNT, chipSize, RED_CHIPS);
				((Bot) blacks).setListener(GameManager.getInstance());// new
																		// BackwardStack(CHIPS_COUNT,
																		// chipSize,
																		// RED_CHIPS);
			}
		} else {
			whites = new ForvardStack(CHIPS_COUNT, chipSize, GREY_CHIPS,
					GameMode.state.wPositions, GameMode.state.wPoints,
					GameMode.state.wIsTop);
			if (GameMode.players == GameMode.TWO_PLAYERS)
				blacks = new BackwardStack(CHIPS_COUNT, chipSize, RED_CHIPS,
						GameMode.state.bPositions, GameMode.state.bPoints,
						GameMode.state.bIsTop, true);
			else {
				blacks = new Bot(CHIPS_COUNT, chipSize, RED_CHIPS,
						GameMode.state.bPositions, GameMode.state.bPoints,
						GameMode.state.bIsTop);
				((Bot) blacks).setListener(GameManager.getInstance());
			}
			DiceManager.getInstance().dicesValues = GameMode.state.diceValues;
			DiceManager.getInstance().usedDices = GameMode.state.diceUsage;
			DiceManager
					.getInstance()
					.setState(
							GameMode.state.diceValues[0] != GameMode.state.diceValues[1] ? DiceStates.Normal
									: DiceStates.Double);
			DiceManager.getInstance().hintString = null;
		}

		Move wMove = new ForvardMove(whites, blacks, chipSize,
				GameManager.getInstance(), Journal.getInstance());
		Move bMove = new BackwardMove(blacks, whites, chipSize,
				GameManager.getInstance(), Journal.getInstance());

		whites.setMove(wMove);
		blacks.setMove(bMove);

		movesDrawer = new MoveDrawer((int) boadrWidth, (int) boardHeight);
		// GameManager.getInstance().setCallBack(movesDrawer.getMoveCallBack());

		GameManager.getInstance().Init(whites, blacks, getContext());
		if (GameMode.mode == GameMode.MODE_CONTINUE)
			GameManager.getInstance().setCurrent(GameMode.state.whichMove);

		Log.d("BACKGAMMON", "trying to start thread");
		Log.d("BACKGAMMON", "trying to create thread");
		gThread = new GameThread(getHolder(), this);
		Log.d("BACKGAMMON", "thread created");
		gThread.setPriority(Thread.MAX_PRIORITY);
		gThread.setRunning(true);
		gThread.start();
		Log.d("BACKGAMMON", "thread started");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		gThread.setRunning(false);
		while (retry) {
			try {
				gThread.join();
				retry = false;
				Log.d("BACKGAMMON", "thread stoped");
			} catch (InterruptedException e) {
			}
		}
	}
}