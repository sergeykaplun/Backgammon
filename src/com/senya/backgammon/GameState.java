package com.senya.backgammon;

import java.io.Serializable;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class GameState implements Serializable, Parcelable{
	mPoint[] wPoints;
	int[] wPositions;
	boolean[] wIsTop;
	
	mPoint[] bPoints;
	int[] bPositions;
	boolean[] bIsTop;
	
	int whichMove;
	int[] diceValues;
	boolean[] diceUsage;
	
	public GameState(Stack w, Stack b, int whichMove, int[] dices, boolean[] dicesUsage){
		wPoints = new mPoint[GameBoard.CHIPS_COUNT];
		bPoints = new mPoint[GameBoard.CHIPS_COUNT];
		wPositions = new int[GameBoard.CHIPS_COUNT];
		bPositions = new int[GameBoard.CHIPS_COUNT];
		wIsTop = new boolean[GameBoard.CHIPS_COUNT];
		bIsTop = new boolean[GameBoard.CHIPS_COUNT];
		
		
		for (int i = 0; i < GameBoard.CHIPS_COUNT; i++) {
			wPoints[i] = new mPoint(w.getChips()[i].getPhisicalPoint().x, w.getChips()[i].getPhisicalPoint().y);
			bPoints[i] = new mPoint(b.getChips()[i].getPhisicalPoint().x, b.getChips()[i].getPhisicalPoint().y);;
		
			wPositions[i] = w.getChips()[i].getPosition();
			bPositions[i] = b.getChips()[i].getPosition();
			
			wIsTop[i] = w.getChips()[i].isTop();
			bIsTop[i] = b.getChips()[i].isTop();
		}
		this.whichMove = whichMove;
		this.diceValues = dices;
		this.diceUsage = dicesUsage;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		//write whites 
		dest.writeArray(wPoints);
		dest.writeIntArray(wPositions);
		dest.writeBooleanArray(wIsTop);
		
		//write blacks 
		dest.writeArray(bPoints);
		dest.writeIntArray(bPositions);
		dest.writeBooleanArray(bIsTop);
		
		//write dices
		dest.writeInt(whichMove);
		dest.writeIntArray(diceValues);
		dest.writeBooleanArray(diceUsage);
	}
	
	public GameState(Parcel in) {
		//read first
		Object[] objects = in.readArray(mPoint.class.getClassLoader());
		this.wPoints = (mPoint[]) objects;
		this.wPositions = new int[BoardPositions.POSITIONS_COUNT];
		in.readIntArray(wPositions);
		this.wIsTop = new boolean[BoardPositions.POSITIONS_COUNT];
		in.readBooleanArray(wIsTop);
		
		//read first
		this.bPoints = (mPoint[]) in.readArray(mPoint.class.getClassLoader());
		this.bPositions = new int[BoardPositions.POSITIONS_COUNT];
		in.readIntArray(bPositions);
		this.bIsTop = new boolean[BoardPositions.POSITIONS_COUNT];
		in.readBooleanArray(bIsTop);
		
		//read dices
		this.whichMove = in.readInt();
		this.diceValues = new int[4];
		in.readIntArray(diceValues);
		this.diceUsage = new boolean[4];
		in.readBooleanArray(diceUsage);
	}
	
//	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
//        public GameState createFromParcel(Parcel in) {
//            return new GameState(in); 
//        }
//
//        public GameState[] newArray(int size) {
//            return new GameState[size];
//        }
//    };
}
