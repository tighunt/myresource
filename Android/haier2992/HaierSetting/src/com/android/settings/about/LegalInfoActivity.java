package com.android.settings.about;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.android.settings.R;

/**
 * 法律信息界面
 * 
 * @author ducj
 * @since 1.0 2011-11-17
 */
public class LegalInfoActivity extends Activity {

	private TextView legalInfor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.legal_infor);

		findViews();

		legalInfor.setText(R.string.disclaimer_infor);
	}

	private void findViews() {
		legalInfor = (TextView) findViewById(R.id.about_legal_info);
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			finish();
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}
