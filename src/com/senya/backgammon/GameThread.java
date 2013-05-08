package com.senya.backgammon;

import android.graphics.Canvas;
import android.util.Log;
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
		//super.run();
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
	}
}