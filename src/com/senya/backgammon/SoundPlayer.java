package com.senya.backgammon;

import java.util.HashMap;
import java.util.Map;

import com.senya.backgammon.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPlayer {
	public final int erace = R.raw.erace;
	public final int draw = R.raw.draw;
	public final int draw2 = R.raw.draw2;
	public final int eraser = R.raw.eraser;
	private SoundPool soundPool;
	private Map<Integer, Integer> soundPoolMap;
	private Context c;
	
	private static SoundPlayer _instance;
	public static SoundPlayer getInstance(){
		if(_instance == null)
			_instance = new SoundPlayer();
		return _instance;
	}
	
	private void SoundPool(){}
	
	/** Populate the SoundPool */
	public void initSounds(Context context) {
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>(2);
//		soundPoolMap.put(erace, soundPool.load(context, R.raw.erace, 1));
//		soundPoolMap.put(draw, soundPool.load(context, R.raw.draw, 2));
		soundPoolMap.put(draw2, soundPool.load(context, R.raw.draw2, 3));
		soundPoolMap.put(eraser, soundPool.load(context, R.raw.eraser, 4));
		this.c = context;
	}

	public void playSound(int soundID) {
		if(!GameActivity.needSound)
			return;
		if (soundPool == null || soundPoolMap == null) {
			initSounds(c);
		}
		float volume = 10;//
		soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
	}
}
