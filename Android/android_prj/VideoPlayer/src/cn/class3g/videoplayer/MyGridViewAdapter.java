package cn.class3g.videoplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGridViewAdapter extends BaseAdapter{

	private Context context;
	private String[] items;
	private int[] icons;
	
	public MyGridViewAdapter(Context context, String[] items, int[] icons){
		this.context = context;
		this.items = items;
		this.icons = icons;
	}
	
	public int getCount() {
		return items.length;
	}

	public Object getItem(int arg0) {
		return items[arg0];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = (View)layoutInflater .inflate(R.layout.grid, null);
		ImageView imageView = (ImageView)view.findViewById(R.id.image_view);
		TextView textview = (TextView)view.findViewById(R.id.text_view);
		imageView.setImageResource(icons[position]);
		textview.setText(items[position]);
		return view;
	}

}
