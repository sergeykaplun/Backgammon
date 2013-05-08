package com.senya.backgammon;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager {
	public static final String ANIMATION = "animation";
	public static final String ANIMATION_SPEED = "animation_speed";
	public static final String HINTS = "hints";
	public static final String SOUND = "sound";
	public static final String AUTOROLL = "autoroll";
	public static final String DIFFICULTY = "difficulty";
	
	public static final String ISFIRST = "isfirst";
	private static final String PREFERENCES_NAME = "com.senya.backgammon";
	
	
	private PreferencesManager(){}
	private static PreferencesManager _instance;
	public static PreferencesManager getInstance(){
		if(_instance == null){
			_instance = new PreferencesManager();
		}
		return _instance;
	}
	public boolean setPreference(String name, String value, Context c){
		SharedPreferences sp = c.getSharedPreferences(PREFERENCES_NAME,
				c.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(name, value);
		return editor.commit();
	}
	public String getPreferenceValue(String name, Context c){
		SharedPreferences sp = c.getSharedPreferences(PREFERENCES_NAME,
				c.MODE_PRIVATE);
		return sp.getString(name, "");
	}
}