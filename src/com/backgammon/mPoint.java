package com.backgammon;

import java.io.Serializable;

import android.graphics.Point;

public class mPoint implements Serializable{
	public int x;
	public int y;
	public mPoint(int x, int y){
		this.x = x;
		this.y = y;
	}
}

