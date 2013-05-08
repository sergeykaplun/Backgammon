package com.senya.backgammon;

public class GameMode {
	public static final int SINGLE_PLAYER = 0;
	public static final int TWO_PLAYERS = 1;
	
	public static int players = SINGLE_PLAYER;
	
	public static final int MODE_NEW_GAME = 0;
	public static final int MODE_CONTINUE = 1;
	
	public static int mode = MODE_NEW_GAME;
	
	public static GameState state = null;
}
