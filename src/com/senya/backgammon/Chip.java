package com.senya.backgammon;

import java.io.Serializable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.senya.backgammon.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.util.Log;

public class Chip implements Serializable{
	protected boolean isNeedHighlight;
	protected float size;
	protected Point phisicalPoint;
	protected boolean isFocused = false;
	protected Bitmap[] images;
	protected int currentImage;
	protected boolean drawFocus;
	
	public boolean isFocused() {
		return isFocused;
	}

	public void setFocused(boolean isFocused) {
		this.isFocused = isFocused;
	}

	public Point getPhisicalPoint() {
		return phisicalPoint;
	}

	public void setPhisicalPoint(Point phisicalPoint) {
		this.phisicalPoint = phisicalPoint;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	private int position;
	private boolean isTop;
	
	public Chip(int pos, Point phPoint, boolean top,float size,Bitmap[] res, int delay, boolean highlight){
		this.phisicalPoint = phPoint;
		this.position = pos;
		this.isTop = top;
		this.size = size;
		this.images = res; 
//		this.images = new Bitmap[res.length];
//		for (int i = 0; i < res.length; i++) {
//			int scaleX = randomizer.nextInt(25);
//			int scaleY = randomizer.nextInt(25);
//			
//			this.images[i] = Bitmap.createScaledBitmap(res[i], calculateDifference(res[i].getWidth(), scaleX), calculateDifference(res[i].getHeight(), scaleY), false);
//		}
		currentImage = 0;
		drawFocus = true;
		isNeedHighlight = highlight;
		
		new Timer().schedule(new InitAnimation(), delay, 20);
	}
	public boolean isIntersected(float x,float y){
		return (x>this.phisicalPoint.x - size/2 && x<this.phisicalPoint.x+size/2 && y>this.phisicalPoint.y - size/2 && y<this.phisicalPoint.y+size/2)?true:false;
	}
	public void move(float x,float y){
		this.phisicalPoint = new Point(this.phisicalPoint.x + (int)x,this.phisicalPoint.y + (int)y);
	}
	public void fastMove(MoveBind newPoint, int moveDelay){
		//float Xdiff = (newPoint.targetPoint.x - getPhisicalPoint().x)/5;
		//float Ydiff = (newPoint.targetPoint.y - getPhisicalPoint().y)/5;
		
		new Timer().schedule(new ReDrawing(newPoint.getTargetPoint()), moveDelay, 30);
		//new Timer().schedule(new Motion(Xdiff, Ydiff, newPoint.getTargetPoint()), moveDelay, 20);
		//this.phisicalPoint = newPoint.getTargetPoint();
		this.position = newPoint.getTargetPosition();
	}
	Random randomizer = new Random();
	public void draw(Canvas c){
		Paint paint = new Paint();
		//paint.setColor(this.isTop?topColor:color);
		if(isNeedHighlight && drawFocus && isFocused()){
			RadialGradient gradient = new RadialGradient(getPhisicalPoint().x, getPhisicalPoint().y, size/3*2, 0xFF000000, 0x0000FF00, android.graphics.Shader.TileMode.CLAMP);
		    Paint p = new Paint();
		    p.setDither(true);
		    p.setShader(gradient);

		    c.drawCircle(getPhisicalPoint().x, getPhisicalPoint().y, size/3*2, p);
		}
		/*paint.setColor(Color.argb(128, 255, 255, 255));
		c.drawCircle(getPhisicalPoint().x,
				getPhisicalPoint().y, size / 2 + 1, paint);*/
		c.drawBitmap(images[currentImage], phisicalPoint.x - size/2, phisicalPoint.y - size/2, paint);
		
		/*paint.setColor(this.isTop?topColor:color);
		c.drawCircle(getPhisicalPoint().x,
				getPhisicalPoint().y, size / 2 - 1, paint);*/
		
	/*	paint.setColor(Color.BLACK);
		c.drawText(String.valueOf(getPosition()), getPhisicalPoint().x, getPhisicalPoint().y, paint);*/
	}
	
	private int calculateDifference(int total, int persentage){
		int diff = total * persentage / 100;
		return total - diff;
	} 
	
	private class Motion extends TimerTask{
		private float incrementX;
		private float incrementY;
		private int couner = 0;
		private Point last;
		
		public Motion(float x,float y, Point last){
			this.incrementX = x;
			this.incrementY = y;
			this.last = last;
		}
		
		@Override
		public void run() {
			if(++couner<5){
				synchronized (this) {
					move(incrementX, incrementY);
				}
			}
			else{
				phisicalPoint = last;
				this.cancel();
			}
		}
	} 
	private class ReDrawing extends TimerTask{
		private int couner = 0;
		private Point last;
		private boolean isEraceSoundPlayed;
		private boolean isDrawSoundPlayed;
		
		
		public ReDrawing(Point last){
			this.last = last;
			drawFocus = false;
			isDrawSoundPlayed = false;
			isEraceSoundPlayed = false;
		}
		
		@Override
		public void run() {
			if(couner >= images.length * 2){
				drawFocus = true;
				this.cancel();
			}
			
			if(couner == images.length)
				phisicalPoint = last;
			
			if(couner < images.length){
				if(!isEraceSoundPlayed){
					isEraceSoundPlayed = true;
					SoundPlayer.getInstance().playSound(R.raw.eraser);
				}
				if(currentImage - 1 > 0)
					currentImage--;
			}
			else{
				if(currentImage + 1< images.length){
					if(!isDrawSoundPlayed){
						isDrawSoundPlayed = true;
						SoundPlayer.getInstance().playSound(R.raw.draw2);
					}
					currentImage++;
				}
			}
			couner++;
		}
	} 
	private class InitAnimation extends TimerTask{
		private int coutner = 0;
		private boolean isSoundPlayed = false;
		
		@Override
		public void run() {
			if(coutner++<10){
//				if(!isSoundPlayed){
//					isSoundPlayed = true;
//					SoundPlayer.getInstance().playSound(R.raw.draw2);
//				}
				if(currentImage + 1 < images.length)
					currentImage++;
			}else
				this.cancel();
		}
	}
}