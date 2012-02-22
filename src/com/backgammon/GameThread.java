package com.backgammon;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
	private SurfaceHolder surfaceHolder;
	private GameBoard board;
	private boolean running = false;

	public GameThread(SurfaceHolder sh, GameBoard gb) {
		surfaceHolder = sh;
		board = gb;
	}

	public void setRunning(boolean r) {
		running = r;
	}

	@Override
	public void run() {
		Canvas c;
		while (running) {
			c = null;
			try {
				c = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					board.onDraw(c);
				}
			} finally {
				if (c != null)
					surfaceHolder.unlockCanvasAndPost(c);
			}
		}
		super.run();
	}
}