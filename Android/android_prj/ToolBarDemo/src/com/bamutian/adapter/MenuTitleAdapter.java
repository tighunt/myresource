package com.bamutian.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.xijiebamutian.R;

public class MenuTitleAdapter extends BaseAdapter {

	private Context mContext;
	private int fontColor;
	private TextView[] title;

	public MenuTitleAdapter(Context context, String[] titles, int fontSize, int color) {
		this.mContext = context;
		this.fontColor = color;
		this.title = new TextView[titles.length];
		for (int i = 0; i < titles.length; i++) {
			title[i] = new TextView(mContext);
			title[i].setText(titles[i]);
			title[i].setTextSize(fontSize);
			title[i].setTextColor(fontColor);
			title[i].setGravity(Gravity.CENTER);
			title[i].setPadding(10, 10, 10, 10);
			title[i].setBackgroundResource(R.drawable.toolbar_menu_release);
		}
	}

	public int getCount() {

		return title.length;
	}

	public Object getItem(int position) {

		return title[position];
	}

	public long getItemId(int position) {

		return title[position].getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null) {
			v = title[position];
		} else {
			v = convertView;
		}
		return v;
	}

}