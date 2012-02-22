package com.backgammon;

import android.graphics.Point;

public class MoveBind {
	Point targetPoint;
	public Point getTargetPoint() {
		return targetPoint;
	}
	public int getTargetPosition() {
		return targetPosition;
	}
	int targetPosition;
	int usedBone;
	public int getUsedBone() {
		return usedBone;
	}
	public MoveBind(Point point, int pos,int usedBone){
		this.targetPoint = point;
		this.targetPosition = pos;
		this.usedBone = usedBone;
	}
}
