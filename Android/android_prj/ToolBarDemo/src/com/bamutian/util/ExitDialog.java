package com.bamutian.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bamutian.navigation.ConvertActivityGroup;
import com.xijiebamutian.R;

/**
 *  This dialog will be showed to alert users when it will exit.
 *
 * @author XijieChen
 */
public class ExitDialog extends AlertDialog {

	public ExitDialog(Context context) {
		super(context);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_dialog);

		TextView message_exit = (TextView) findViewById(R.id.message_exit);
		message_exit.setText(R.string.hint_exit);
		setTitle(null);
		Window w = getWindow();
		w.setBackgroundDrawableResource(R.color.white);
		Button okButton = (Button) findViewById(R.id.ok_button);
		okButton.setText(R.string.ok_timeout);
		okButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				ConvertActivityGroup.group.getLocalActivityManager()
						.getCurrentActivity().finish();
				System.exit(0);
			}
		});
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setText(R.string.cancel);
		cancelButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}