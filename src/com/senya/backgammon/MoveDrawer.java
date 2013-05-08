package com.senya.backgammon;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class MoveDrawer{
	public interface mCallBack{
		public void ActionErace(float lastContactX, float lastContactY);
		public void ActionDrag(float lastContactX, float lastContactY);
		public void ActionDown(float lastContactX, float lastContactY, int color);
	}
	
	private static final int DEFAULT_STROKE_WIDTH = 30;
	private Paint painter;
	private Bitmap _image;
	private Canvas _canvas;
	private PointF lastPoint;
	private mCallBack moveCallBack;
	private int currentAlfa;
	private int currentStrokeWidth;
	private int currentRed;
	
	public mCallBack getMoveCallBack(){
		return moveCallBack;
	}
	
	public MoveDrawer(int width, int height) {

		painter = new Paint();
		painter.setColor(Color.argb(100, 200, 0, 0));
		painter.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		painter.setAntiAlias(true);

		_image = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		_canvas = new Canvas(_image);

		lastPoint = new PointF(0, 0);
		moveCallBack = new mCallBack() {
			
			@Override
			public void ActionErace(float lastContactX, float lastContactY) {
				MoveDrawer.this.Erace(lastContactX, lastContactY);
			}
			
			@Override
			public void ActionDrag(float lastContactX, float lastContactY) {
				MoveDrawer.this.Drag(lastContactX, lastContactY);
			}
			@Override
			public void ActionDown(float lastContactX, float lastContactY, int red) {
				MoveDrawer.this.currentAlfa = 255;
				MoveDrawer.this.lastPoint.x = lastContactX;
				MoveDrawer.this.lastPoint.y = lastContactY;
				MoveDrawer.this.currentStrokeWidth = DEFAULT_STROKE_WIDTH;
				MoveDrawer.this.currentRed = red;
			}
		};
	}

	public void Erace(float lastContactX, float lastContactY) {
//		{
//			lastPoint.x = lastContactX;
//			lastPoint.y = lastContactY;
//		}
//		_canvas.drawCircle(lastPoint.x, lastPoint.y,
//				DEFAULT_STROKE_WIDTH + 20, painter);
	}
	
	private static final int DECREASING_COEF = 10;
	public void Drag(float lastContactX, float lastContactY) {
		currentAlfa -= DECREASING_COEF;
		if(currentAlfa < 25)
			currentAlfa = 25;
		currentStrokeWidth -= 1;
		if(currentStrokeWidth < 0)
			currentStrokeWidth = 0;
		
		Paint painter2 = new Paint();
		painter2.setStrokeWidth(currentStrokeWidth);
		painter2.setAntiAlias(true);
		painter2.setColor(Color.argb(currentAlfa, currentRed, 0, 0));
		_canvas.drawLine(lastPoint.x, lastPoint.y, lastContactX,
				lastContactY, painter2);
//		_canvas.drawCircle(lastContactX, lastContactY, DEFAULT_STROKE_WIDTH, painter2);
		{
			lastPoint.x = lastContactX;
			lastPoint.y = lastContactY;
		}
	}
	public void drawMoves(Canvas c){
		c.drawBitmap(_image, 0, 0, painter);
	}
}