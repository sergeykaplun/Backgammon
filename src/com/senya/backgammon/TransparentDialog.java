package com.senya.backgammon;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

public class TransparentDialog extends Dialog{

	public TransparentDialog(Context context, View view) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view);
		getWindow().getDecorView().setBackgroundResource(
				android.R.color.transparent);
	}
}