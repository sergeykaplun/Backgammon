package com.backgammon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;

public class GameActivity extends Activity {
	GameBoard board;
	Activity mActivity;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(1);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		board = new GameBoard(this);
		mActivity = this;
		setContentView(board);
	}

	@Override
	public void onBackPressed() {
		final AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setMessage("Save game ?");
		b.setPositiveButton("yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveState();
				mActivity.finish();
			}
		});
		b.setNegativeButton("no", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mActivity.finish();
			}
		});
		b.setCancelable(true);
		b.show();
	}
	private void saveState(){
		File f = new File(Environment.getExternalStorageDirectory() + File.separator + GameMenuActivity.FOLDER_BACKGAMMON + File.separator);
		if(!f.exists()){
			try {
				f.mkdirs();
			} catch (Exception e) {
				Log.e("senya", e.getMessage());
			}
		}
		if(f.exists()){
			String fileName;
			if(GameMode.players == GameMode.SINGLE_PLAYER)
				fileName = GameMenuActivity.FILE_SINGLE;
			else
				fileName = GameMenuActivity.FILE_TWO;
			
			//f = new File(Environment.getExternalStorageDirectory() + File.separator + GameMenuActivity.FOLDER_BACKGAMMON + File.separator, fileName);
			try {
				FileOutputStream fos = getApplication().openFileOutput(fileName, MODE_PRIVATE);
				ObjectOutputStream os;
				os = new ObjectOutputStream(fos);
				os.writeObject(new GameState(board.getWhites(), board.getBlacks()));
				os.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				Log.e("senya", e.getMessage());
			}
		}
	}
	/*
	 * @Override protected void onPause() { super.onPause();
	 * GameManager.getInstance().dropFocus();
	 * board.getgThread().setRunning(false); }
	 * 
	 * @Override protected void onResume() { super.onResume();
	 * board.getgThread().setRunning(true); }
	 */
}