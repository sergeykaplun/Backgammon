package com.senya.backgammon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.backgammon.dices.DiceManager;
import com.senya.backgammon.GameManager.GameResultListener;

public class GameActivity extends Activity implements GameResultListener {
	GameBoard board;
	public static Typeface lazyTypeface;
	private Handler h = new Handler();
	
	public static boolean needHints;
	//public static boolean needAnimation;
	public static boolean needAutoroll;
	public static boolean needSound;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(1);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (savedInstanceState != null) {
//			GameMode.mode = savedInstanceState.getInt(KEY_SAVE_MODE);
//			GameMode.players = savedInstanceState.getInt(KEY_SAVE_PLAYERS);
//			GameMode.state = savedInstanceState.getParcelable(KEY_SAVE_STATE);
			this.finish();
		}
		board = new GameBoard(this);
		setContentView(board);
		DiceManager.getInstance().reInit((String) getText(R.string.tap_to_chellenge));
		lazyTypeface = Typeface.createFromAsset(getAssets(), "OlgaCTT.ttf");
		GameManager.getInstance().setGameResultListener(this);
		SoundPlayer.getInstance().initSounds(this);
	}
	
	@Override
	public void onBackPressed() {
		if(GameManager.getInstance().needSave()){
			h.post(new Runnable() {
				
				@Override
				public void run() {
					View forDialog = LayoutInflater.from(GameActivity.this).inflate(
							R.layout.yes_no_dialog_layout, null);
					final Dialog dialog = new TransparentDialog(GameActivity.this, forDialog);
					dialog.show();
					
					((AnimatedText) forDialog.findViewById(R.id.dialog_title))
							.setTypeface(lazyTypeface);
					((AnimatedText) forDialog.findViewById(R.id.dialog_title))
							.animateText(getText(R.string.save_game));
					((ImageButton) forDialog.findViewById(R.id.yes_button))
							.setOnClickListener(new View.OnClickListener() {
			
								@Override
								public void onClick(View v) {
									saveState();
									GameActivity.this.finish();
									dialog.dismiss();
									Journal.getInstance().clear();
								}
							});
					((ImageButton) forDialog.findViewById(R.id.no_button))
							.setOnClickListener(new View.OnClickListener() {
			
								@Override
								public void onClick(View v) {
									GameActivity.this.finish();
									dialog.dismiss();
									Journal.getInstance().clear();
								}
							});
				}
			});
		}else{
			this.finish();
		}
	}

	private void saveState() {
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + GameMenuActivity.FOLDER_BACKGAMMON
				+ File.separator);
		if (!f.exists()) {
			try {
				f.mkdirs();
			} catch (Exception e) {
				Log.e("senya", e.getMessage());
			}
		}
		if (f.exists()) {
			String fileName;
			if (GameMode.players == GameMode.SINGLE_PLAYER)
				fileName = GameMenuActivity.FILE_SINGLE;
			else
				fileName = GameMenuActivity.FILE_TWO;

			try {
				FileOutputStream fos = getApplication().openFileOutput(
						fileName, MODE_PRIVATE);
				ObjectOutputStream os;
				os = new ObjectOutputStream(fos);
				os.writeObject(new GameState(board.getWhites(), board
						.getBlacks(), GameManager.getInstance().getCurrent(),
						DiceManager.getInstance().dicesValues, DiceManager
								.getInstance().usedDices));
				os.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				Log.e("senya", e.getMessage());
			}
		}
	}

	private static final String KEY_SAVE_STATE = "state_save";
	private static final String KEY_SAVE_MODE = "mode_save";
	private static final String KEY_SAVE_PLAYERS = "players_save";

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putParcelable(
		// KEY_SAVE_STATE,
		// new GameState(board.getWhites(), board.getBlacks(), GameManager
		// .getInstance().getCurrent(),
		// DiceManager.getInstance().dicesValues, DiceManager
		// .getInstance().usedDices));
		// outState.putInt(KEY_SAVE_MODE, GameMode.mode);
		// outState.putInt(KEY_SAVE_PLAYERS, GameMode.players);
	}

	@Override
	public void onGameResult(String message) {
		View forDialog = LayoutInflater.from(this).inflate(
				R.layout.yes_no_dialog_layout, null);
		final Dialog dialog = new TransparentDialog(GameActivity.this, forDialog);

		((AnimatedText) forDialog.findViewById(R.id.dialog_title))
				.setTypeface(lazyTypeface);
		((AnimatedText) forDialog.findViewById(R.id.dialog_title))
				.animateText(message);
		((ImageButton) forDialog.findViewById(R.id.yes_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Journal.getInstance().clear();
						dialog.dismiss();
						GameActivity.this.finish();
					}
				});
		forDialog.findViewById(R.id.no_button).setVisibility(View.GONE);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private boolean isContinue = false;
	private int playersCount;
	private GameState state;
	
	@Override
	protected void onStop() {
		super.onPause();
		isContinue = true;
		state = new GameState(board.getWhites(), board.getBlacks(), GameManager
				.getInstance().getCurrent(),
				DiceManager.getInstance().dicesValues,
				DiceManager.getInstance().usedDices);

		playersCount = GameMode.players;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isContinue) {
			isContinue = false;
			GameMode.mode = GameMode.MODE_CONTINUE;
			GameMode.state = state;
			GameMode.players = playersCount;
		}
		
		//needAnimation = Boolean.valueOf(PreferencesManager.getInstance().getPreferenceValue(PreferencesManager.ANIMATION, this));
		needAutoroll = Boolean.valueOf(PreferencesManager.getInstance().getPreferenceValue(PreferencesManager.AUTOROLL, this));
		needHints = Boolean.valueOf(PreferencesManager.getInstance().getPreferenceValue(PreferencesManager.HINTS, this));
		needSound = Boolean.valueOf(PreferencesManager.getInstance().getPreferenceValue(PreferencesManager.SOUND, this));
	}
}