package com.backgammon;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.Point;

public class BackwardStack extends Stack{

	public BackwardStack(int capacity, float chipSize, Bitmap[] res) {
		super(capacity);
		int delay = 0;
		for (int i = 0; i < chips.length; i++) {
			chips[i] = new Chip(0, new Point(BoardPositions.getInstance()
					.getOtherwiseAt(0).x, (int) (BoardPositions.getInstance()
					.getOtherwiseAt(0).y + chipSize * i)),
					(i < capacity - 1) ? false : true, chipSize, res, delay);
			delay += 200;
		}
	}
	public BackwardStack(int capacity, float chipSize, Bitmap[] res, int[] positions, mPoint[] points, boolean[] isTop) {
		super(capacity);
		int delay = 0;
		for (int i = 0; i < chips.length; i++) {
			chips[i] = new Chip(positions[i], new Point(points[i].x,points[i].y),
					isTop[i], chipSize, res, delay);
			delay += 200;
		}
	}

	@Override
	protected int getHighest(int pos) {
		int res = -1;
		int tmpHighs = (pos < BoardPositions.POSITIONS_COUNT / 2) ? Integer.MIN_VALUE
				: Integer.MAX_VALUE;
		;
		for (int i = 0; i < chips.length; i++) {
			if (chips[i].getPosition() == pos) {
				if (pos < BoardPositions.POSITIONS_COUNT / 2) {
					if (chips[i].getPhisicalPoint().y > tmpHighs) {
						tmpHighs = chips[i].getPhisicalPoint().y;
						res = i;
					}
				} else {
					if (chips[i].getPhisicalPoint().y < tmpHighs) {
						tmpHighs = chips[i].getPhisicalPoint().y;
						res = i;
					}
				}
			}
		}
		return res;
	}

	@Override
	public void setMove(Move m) {
		if(!(m instanceof BackwardMove))
			return;
		this.move = m;
	}

	@Override
	public void makeMove() {
	}
}
