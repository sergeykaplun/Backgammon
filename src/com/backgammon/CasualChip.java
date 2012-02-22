package com.backgammon;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class CasualChip extends Chip{

	public CasualChip(int pos, Point phPoint, boolean top, float size,
			Bitmap[] res, int delay) {
		super(pos, phPoint, top, size, res, delay);
	}
	
	@Override
	public void draw(Canvas c) {
	/*	Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		c.drawCircle(getPhisicalPoint().x,
				getPhisicalPoint().y, size / 2, paint);
		paint.setColor(color);
		c.drawCircle(getPhisicalPoint().x,
				getPhisicalPoint().y, size / 2 - 1, paint);
	paint.setColor(Color.BLACK);
		c.drawText(String.valueOf(getPosition()), getPhisicalPoint().x, getPhisicalPoint().y, paint);*/
	}

}
