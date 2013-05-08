package com.senya.backgammon;

public interface MoveFinishListener {
	public void moveComplete();
	public void boneIsUsed(int num);
	//public void unUseBone(boolean double_mode, int num);
}
