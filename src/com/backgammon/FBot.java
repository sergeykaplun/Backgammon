package com.backgammon;

import android.graphics.Bitmap;
import android.graphics.Point;

public class FBot extends Bot{

	public FBot(int capacity, float chipSize, Bitmap[] res) {
		super(capacity, chipSize, res);
		int delay = 0;
		for (int i = 0; i < chips.length; i++) {
			chips[i] = new Chip(0, new Point(BoardPositions.getInstance()
					.getClockwiseAt(0).x, (int) (BoardPositions.getInstance()
					.getClockwiseAt(0).y - chipSize * i)),
					(i < capacity - 1) ? false : true, chipSize, res, delay);
			delay += 200;
		}
	}
	public FBot(int capacity, float chipSize, Bitmap[] res, int[] positions, mPoint[] points, boolean[] isTop){
		super(capacity, chipSize, res, positions, points, isTop);
	}
}
