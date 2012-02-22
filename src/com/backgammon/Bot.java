package com.backgammon;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import android.graphics.Bitmap;

public class Bot extends BackwardStack{
	MoveFinishListener listener; 
	
	public Bot(int capacity, float chipSize, Bitmap[] res) {
		super(capacity, chipSize, res);
	}
	public Bot(int capacity, float chipSize, Bitmap[] res, int[] positions, mPoint[] points, boolean[] isTop){
		super(capacity, chipSize, res, positions, points, isTop);
	}
	public void setListener(MoveFinishListener m){
		this.listener = m;
	}

	@Override
	public void makeMove() {
		int delay = 1;
		while (move.hasMoreMooves()) {
			Hashtable<Integer, Vector<MoveBind>> allMoves = getAllAvailableMoves();
			Enumeration<Integer> keys = allMoves.keys();
			Vector<MoveRatingBind> ratingBinds = new Vector<Bot.MoveRatingBind>();
			while (keys.hasMoreElements()) {
				Integer chipNum	 = (Integer) keys.nextElement();
				Vector<MoveBind> moves = allMoves.get(chipNum);
				float bestMoveRate = Float.MIN_VALUE;
				int bestMoveNum = 0;
				for (int i = 0; i < moves.size(); i++) {
					float moveRate = rateMove(chipNum.intValue(), moves.elementAt(i).getTargetPosition());
					if(moveRate > bestMoveRate){
						bestMoveRate = moveRate;
						bestMoveNum = i;
					}
				}
				ratingBinds.add(new MoveRatingBind(chipNum.intValue(), bestMoveRate, moves.elementAt(bestMoveNum)));
			}
			
			MoveRatingBind best = getBestRatingBind(ratingBinds);
			setFocus(best.chipNum);
			move.useBone(fastMoveFocused(best.moveBind, ++delay * 500), delay * 500);
		}
		listener.Continue();
	}
	private MoveRatingBind getBestRatingBind(Vector<MoveRatingBind>  bind){
		float tmpRate = Float.MIN_VALUE;
		int best = 0;
		for (int i = 0; i < bind.size(); i++) {
			if(bind.elementAt(i).moveRate > tmpRate){
				best = i;
				tmpRate = bind.elementAt(i).moveRate;
			}
		}
		return bind.elementAt(best);
	}
	
	private void makeRandomMove(){
		Random rnd = new Random();
		int delay = 1;

		while (move.hasMoreMooves()) {
			int pos = rnd.nextInt(GameBoard.CHIPS_COUNT);
			while (!chips[pos].isTop()) {
				pos = rnd.nextInt(GameBoard.CHIPS_COUNT);
			}
			if (setFocus(chips[pos].getPhisicalPoint().x,
					chips[pos].getPhisicalPoint().y)) {
				Vector<MoveBind> vmb = move.getAviableMoves();
				if (vmb.size() < 1)
					continue;
				MoveBind mb = vmb.elementAt(rnd.nextInt(vmb.size()));
				move.useBone(fastMoveFocused(mb, delay++ * 500));
			}
		}
		listener.Continue();
	}
	private Hashtable<Integer, Vector<MoveBind>> getAllAvailableMoves(){
		Hashtable<Integer, Vector<MoveBind>> res = new Hashtable<Integer, Vector<MoveBind>>();
		for (int i = 0; i < chips.length; i++) {
			if(chips[i].isTop()){
				setFocus(i);
				Vector<MoveBind> vmb = move.getAviableMoves();
				if (vmb.size() < 1)
					continue;
				res.put(Integer.valueOf(i), vmb);
			}
		}
		return res;
	}
	private float rateMove(int chipNum, int target){
		return targetRating(chipNum, target) + leavingRating(chipNum) + ((chips[chipNum].getPosition() == 0)?0.3f:0);
	}
	private static final float COEF_LEAVING_1 = -0.2f;
	private static final float COEF_LEAVING_2 = -1f;
	private float leavingRating(int chipNum){
		float res = 0f;
		
		//Должно препядствывать открыванию перекрытий
		if(!isExistUnder(chipNum)){									
			int neigbor = 0;
			for (int i = 1; i < 4; i++) {
				neigbor += chipsAtPos(chips[chipNum].getPosition() + i)>0?1:0;
				neigbor += chipsAtPos(chips[chipNum].getPosition() - i)>0?1:0;
			}
			res += neigbor * neigbor * COEF_LEAVING_1;
		}
		//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		
		//TODO смотреть количество фишек соперника вокруг покидаемой позиции	1.0 
		//TODO смотреть как много фишек соперника должно пройти через освобожденную ячейку 0.3
		
		//должно минимизировать количество больших пробелов		//требуеться улучшение
		if(ifExistAbove(chipNum)){
			int distanse = 0;
			while(true){
				if(chipsAtPos(chips[chipNum].getPosition() - (++distanse)) < 1){
					//noting
				}else{
					break;
				}
			}
			if(distanse > 3){
				res += distanse * COEF_LEAVING_2;
			}
		}
		//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		
		if(!isExistUnder(chipNum))
			res -= 10f;
		
		return res;
	}
	
	private static final float COEF_MOVING_1 = 0.2f;
	//private static final float COEF_MOVING_2 = -1f;
	private float targetRating(int chipNum, int target){
		float res = 0f;
	
		//Должно созжавать перекрытия
			int neigbor = 0;
			for (int i = 1; i < 4; i++) {
				neigbor += chipsAtPos(target + i)>0?1:0;
				neigbor += chipsAtPos(target - i)>0?1:0;
			}
			res += neigbor * COEF_MOVING_1;
		//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		
		//	Должно препядствывать собиранию пирамиды
			if(chipsAtPos(target) == 0){
				res += 10f;
			}else{
				res += chipsAtPos(target) * (-0.5f);
			}
		//<<<<<<<<<<<<<<<<<<<<<	
			
		//Должно способствовать разгребанию пирамиды
			if(isExistUnder(chipNum) && chipsAtPos(target) < 1)
				res += 1.0f;
		//<<<<<<<<<<<<<<<<<<<<<	
		
		//Должно помочь занимать пустые пространства
			neigbor = 0;
			for (int i = 1; i < 4; i++) {
				neigbor += chipsAtPos(target + i)>0?0:1;
				neigbor += chipsAtPos(target - i)>0?0:1;
			}
			res += neigbor * neigbor * 0.5f;
		//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<	
		
		//Стараться	удерживать четвертую и пятую позиции
			if(target - chips[chipNum].getPosition() == 5 || target - chips[chipNum].getPosition() == 4){
				res += 1.5f;
			}
		//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			
		//TODO	Смореть сколько фишек противника вокруг занятой позиции, препятствывать перекрыванию	1,0
		//TODO	Считать общую удаленность фишек от цели	??????????????
		//TODO	Препядствовать преждевременному заниманию ячеек в доме	0,8
		//TODO 	Учитывать перемещения непоследней из покинутой позиции в пустую целевую позицию	0,5
		return res;	
	}
	
	private boolean isExistUnder(int chipNum){
		for (int i = 0; i < chips.length; i++) {
			if(i == chipNum)
				continue;
			if(chips[i].getPosition() == chips[chipNum].getPosition())
				return true;
		}
		return false;
	}
	
	private class MoveRatingBind{		//Need to implement iComparable
		public int chipNum;
		public float moveRate;
		public MoveBind moveBind;
		public MoveRatingBind(int num, float rate, MoveBind bind){
			this.chipNum = num;
			this.moveRate = rate;
			this.moveBind = bind;
		}
	}
}
