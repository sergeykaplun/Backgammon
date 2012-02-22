package com.backgammon;

public class CompleteMove{
	public static final int FIRST = -1;
	public static final int SECOND = -2;
	public static final int BOTH = -3;
	public static final int NONE = -4;
	//public static final int DOUBLE = 4;
	private MoveBind from;
	private MoveBind to;
	private int chipNum;
	public int getChipNum() {
		return chipNum;
	}
	public void setChipNum(int chipNum) {
		this.chipNum = chipNum;
	}
	//private int usedBone; 
	public CompleteMove(int chip) {
		this.chipNum = chip;
	}
	public CompleteMove(MoveBind from, MoveBind to, int chip){
		this.from = from;
		this.to = to;
		this.chipNum = chip;
	}
	public MoveBind getFrom() {
		return from;
	}
	public void setFrom(MoveBind from) {
		this.from = from;
	}
	public MoveBind getTo() {
		return to;
	}
	public void setTo(MoveBind to) {
		this.to = to;
	}
	public int getPosDiff(){
		return to.getTargetPosition() - from.getTargetPosition();
	}
	/*public int getUsebBone() {
		return usedBone;
	}
	public void setUsebBone(int usebBone) {
		this.usedBone = usebBone;
	}*/
}