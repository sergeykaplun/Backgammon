package com.senya.backgammon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.Properties;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GameMenuActivity extends Activity {
	public static final String FOLDER_BACKGAMMON = "backgammon";
	public static final String FILE_SINGLE = "single";
	public static final String FILE_TWO = "two";
	private Typeface myTypeface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SoundPlayer.getInstance().initSounds(this);
		setRequestedOrientation(1);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		myTypeface = Typeface.createFromAsset(getAssets(), "OlgaCTT.ttf");
		((TextView) findViewById(R.id.single_player_textview))
				.setTypeface(myTypeface);
		((TextView) findViewById(R.id.two_players_textview))
				.setTypeface(myTypeface);
		((TextView) findViewById(R.id.exit_textview)).setTypeface(myTypeface);
		((TextView) findViewById(R.id.help_textview)).setTypeface(myTypeface);
		((TextView) findViewById(R.id.options_textview))
				.setTypeface(myTypeface);
		((TextView) findViewById(R.id.app_name)).setTypeface(myTypeface);

		String isFirstLaunchString = PreferencesManager.getInstance()
				.getPreferenceValue(PreferencesManager.ISFIRST, this);

		if (isFirstLaunchString.equals("")) {
			Properties properties = new Properties();
			try {
				properties.load(getAssets().open("default.properties"));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if (properties != null && properties.size() > 0) {
				PreferencesManager.getInstance().setPreference(
						PreferencesManager.ANIMATION,
						properties.getProperty(PreferencesManager.ANIMATION),
						this);
				PreferencesManager
						.getInstance()
						.setPreference(
								PreferencesManager.ANIMATION_SPEED,
								properties
										.getProperty(PreferencesManager.ANIMATION_SPEED),
								this);
				PreferencesManager.getInstance().setPreference(
						PreferencesManager.HINTS,
						properties.getProperty(PreferencesManager.HINTS), this);
				PreferencesManager.getInstance().setPreference(
						PreferencesManager.SOUND,
						properties.getProperty(PreferencesManager.SOUND), this);
				PreferencesManager.getInstance().setPreference(
						PreferencesManager.AUTOROLL,
						properties.getProperty(PreferencesManager.AUTOROLL),
						this);
				PreferencesManager.getInstance().setPreference(
						PreferencesManager.DIFFICULTY,
						properties.getProperty(PreferencesManager.DIFFICULTY),
						this);
				PreferencesManager.getInstance().setPreference(
						PreferencesManager.ISFIRST, "false", this);
			} else {
				throw new IllegalStateException(
						"Cant find default.properties or its empty");
			}
		}
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.single:
			GameMode.mode = GameMode.MODE_NEW_GAME;
			GameMode.players = GameMode.SINGLE_PLAYER;

			if (isSaveExist(FILE_SINGLE)) {
				View forDialog = LayoutInflater.from(this).inflate(
						R.layout.yes_no_dialog_layout, null);
				final Dialog dialog = new TransparentDialog(this, forDialog);
				((AnimatedText) forDialog.findViewById(R.id.dialog_title))
						.setTypeface(myTypeface);
				((AnimatedText) forDialog.findViewById(R.id.dialog_title))
						.animateText(getText(R.string.continue_game));
				((ImageButton) forDialog.findViewById(R.id.yes_button))
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
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
				((ImageButton) forDialog.findViewById(R.id.no_button))
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								startGame();
							}
						});
				dialog.show();
			} else {
				startGame();
			}
			break;
		case R.id.two:
			GameMode.mode = GameMode.MODE_NEW_GAME;
			GameMode.players = GameMode.TWO_PLAYERS;

			if (isSaveExist(FILE_TWO)) {
				View forDialog = LayoutInflater.from(this).inflate(
						R.layout.yes_no_dialog_layout, null);
				final Dialog dialog = new TransparentDialog(this, forDialog);
				((AnimatedText) forDialog.findViewById(R.id.dialog_title))
						.setTypeface(myTypeface);
				((AnimatedText) forDialog.findViewById(R.id.dialog_title))
						.animateText(getText(R.string.continue_game));
				((ImageButton) forDialog.findViewById(R.id.yes_button))
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
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
				((ImageButton) forDialog.findViewById(R.id.no_button))
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								startGame();
							}
						});
				dialog.show();
			} else {
				startGame();
			}
			break;
		case R.id.exit:
			this.finish();
			break;
		case R.id.prefs:
			final View prefsRoot = LayoutInflater.from(this).inflate(
					R.layout.prefs_layout, null);
			setProperties(prefsRoot);
			TransparentDialog td = new TransparentDialog(this, prefsRoot) {
				@Override
				protected void onStop() {
					super.onStop();
					saveProperties(prefsRoot);
				}
			};
			td.setCanceledOnTouchOutside(false);
			td.show();
			break;
		case R.id.help_tour:
			ScrollView sv = new ScrollView(this);
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.setBackgroundResource(R.drawable.cross_multilpy);
			ll.setPadding(5, 2, 5, 2);
			sv.addView(ll);
			TextView helpTextView = new TextView(this);
			helpTextView.setTypeface(myTypeface);
			helpTextView.setText(getText(R.string.help_in));
			helpTextView.setTextColor(Color.BLACK);
			helpTextView.setTextSize(25);
			ll.addView(helpTextView);
			TransparentDialog helpDialog = new TransparentDialog(this, sv);
			helpDialog.setCanceledOnTouchOutside(false);
			helpDialog.show();
			break;
		default:
			Toast.makeText(this, "Not available yet...", Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	private void setProperties(View prefsRoot) {
		// boolean animation = Boolean.valueOf(PreferencesManager.getInstance()
		// .getPreferenceValue(PreferencesManager.ANIMATION, this));
		boolean autoroll = Boolean.valueOf(PreferencesManager.getInstance()
				.getPreferenceValue(PreferencesManager.AUTOROLL, this));
		boolean sound = Boolean.valueOf(PreferencesManager.getInstance()
				.getPreferenceValue(PreferencesManager.SOUND, this));
		boolean hints = Boolean.valueOf(PreferencesManager.getInstance()
				.getPreferenceValue(PreferencesManager.HINTS, this));

		((CheckBox) prefsRoot.findViewById(R.id.sound_checkbox))
				.setTypeface(myTypeface);
		((CheckBox) prefsRoot.findViewById(R.id.sound_checkbox))
				.setTextSize(25);
		((CheckBox) prefsRoot.findViewById(R.id.sound_checkbox))
				.setChecked(sound);
		((CheckBox) prefsRoot.findViewById(R.id.sound_checkbox))
				.setButtonDrawable(R.drawable.checkbox_selector);

		((CheckBox) prefsRoot.findViewById(R.id.autoroll_checkbox))
				.setTypeface(myTypeface);

		((CheckBox) prefsRoot.findViewById(R.id.autoroll_checkbox))
				.setTextSize(25);
		((CheckBox) prefsRoot.findViewById(R.id.autoroll_checkbox))
				.setChecked(autoroll);
		((CheckBox) prefsRoot.findViewById(R.id.autoroll_checkbox))
				.setButtonDrawable(R.drawable.checkbox_selector);
		((CheckBox) prefsRoot.findViewById(R.id.hint_checkbox))
				.setTypeface(myTypeface);
		((CheckBox) prefsRoot.findViewById(R.id.hint_checkbox)).setTextSize(25);
		((CheckBox) prefsRoot.findViewById(R.id.hint_checkbox))
				.setChecked(hints);
		((CheckBox) prefsRoot.findViewById(R.id.hint_checkbox))
				.setButtonDrawable(R.drawable.checkbox_selector);

		// ((TextView)
		// prefsRoot.findViewById(R.id.animation_speed_textview)).setTypeface(myTypeface);
		// ((TextView)
		// prefsRoot.findViewById(R.id.difficulty_textview)).setTypeface(myTypeface);
		//
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_spinner_item, data);
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//
		// Spinner spinner = (Spinner)
		// prefsRoot.findViewById(R.id.animation_speed_spinner);
		// spinner.setAdapter(adapter);
	}

	private void saveProperties(View prefsRoot) {
		// boolean animation = ((CheckBox)
		// prefsRoot.findViewById(R.id.animation_checkbox)).isChecked();
		boolean autoroll = ((CheckBox) prefsRoot
				.findViewById(R.id.autoroll_checkbox)).isChecked();
		boolean sound = ((CheckBox) prefsRoot.findViewById(R.id.sound_checkbox))
				.isChecked();
		boolean hints = ((CheckBox) prefsRoot.findViewById(R.id.hint_checkbox))
				.isChecked();

		PreferencesManager.getInstance().setPreference(
				PreferencesManager.AUTOROLL, String.valueOf(autoroll), this);
		PreferencesManager.getInstance().setPreference(
				PreferencesManager.SOUND, String.valueOf(sound), this);
		PreferencesManager.getInstance().setPreference(
				PreferencesManager.HINTS, String.valueOf(hints), this);
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