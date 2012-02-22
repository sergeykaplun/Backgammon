package com.backgammon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class GameMenuActivity extends Activity {
	public static final String FOLDER_BACKGAMMON = "backgammon";
	public static final String FILE_SINGLE = "single";
	public static final String FILE_TWO = "two";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(1);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.single:
			GameMode.mode = GameMode.MODE_NEW_GAME;
			GameMode.players = GameMode.SINGLE_PLAYER;

			if (isSaveExist(FILE_SINGLE)) {
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setMessage("Continue previous game ?");
				b.setPositiveButton("yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								GameState st = null;
								try {
									FileInputStream fis = getApplicationContext()
											.openFileInput(FILE_SINGLE);

									ObjectInputStream is;
									is = new ObjectInputStream(fis);
									st = (GameState) is.readObject();
									is.close();
								} catch (FileNotFoundException e) {
									Log.e("senya", e.getMessage());
								} catch (StreamCorruptedException e) {
									Log.e("senya", e.getMessage());
								} catch (IOException e) {
									Log.e("senya", e.getMessage());
								} catch (ClassNotFoundException e) {
									Log.e("senya", e.getMessage());
								} catch (IllegalArgumentException e) {
									Log.e("senya", e.getMessage());
								}
								if (st != null) {
									GameMode.mode = GameMode.MODE_CONTINUE;
									GameMode.state = st;
								}
								startGame();
							}
						});
				b.setNegativeButton("no",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								startGame();
							}
						});
				b.show();
			} else {
				startGame();
			}
			break;
		case R.id.two:
			GameMode.mode = GameMode.MODE_NEW_GAME;
			GameMode.players = GameMode.TWO_PLAYERS;

			if (isSaveExist(FILE_TWO)) {
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setMessage("Continue previous game ?");
				b.setPositiveButton("yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								GameState st = null;
								try {
									FileInputStream fis = getApplicationContext()
											.openFileInput(FILE_TWO);

									ObjectInputStream is;
									is = new ObjectInputStream(fis);
									st = (GameState) is.readObject();
									is.close();
								} catch (FileNotFoundException e) {
									Log.e("senya", e.getMessage());
								} catch (StreamCorruptedException e) {
									Log.e("senya", e.getMessage());
								} catch (IOException e) {
									Log.e("senya", e.getMessage());
								} catch (ClassNotFoundException e) {
									Log.e("senya", e.getMessage());
								} catch (IllegalArgumentException e) {
									Log.e("senya", e.getMessage());
								}
								if (st != null) {
									GameMode.mode = GameMode.MODE_CONTINUE;
									GameMode.state = st;
								}
								startGame();
							}
						});
				b.setNegativeButton("no",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								startGame();
							}
						});
				b.show();
			} else {
				startGame();
			}
			break;
		case R.id.exit:this.finish();
					break;
		default:
			Toast.makeText(this, "Not available yet...", Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	private boolean isSaveExist(String fileName) {
		try {
			FileInputStream fis = getApplicationContext().openFileInput(
					fileName);
			if (fis != null)
				fis.close();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	private void startGame() {
		Intent intent = new Intent(this, GameActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}
}