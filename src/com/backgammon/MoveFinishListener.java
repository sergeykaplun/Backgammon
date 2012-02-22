package com.backgammon;

public interface MoveFinishListener {
	public void Continue();
	public void boneIsUsed(boolean double_mode, int num);
	//public void unUseBone(boolean double_mode, int num);
}
