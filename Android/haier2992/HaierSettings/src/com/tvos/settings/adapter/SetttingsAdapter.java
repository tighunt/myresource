
package com.tvos.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;

public class SetttingsAdapter extends BaseAdapter {

    private Context context;
    private String[] _items;
    private int[] _icons;

    public SetttingsAdapter(Context context, String[] _items, int[] _icons) {
        this.context = context;
        this._items = _items;
        this._icons = _icons;
    }

    @Override
    public int getCount() {
        return _icons.length;
    }

    @Override
    public Object getItem(int position) {
        return _icons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater factory = LayoutInflater.from(context);
        /* 使用list_item_settings.xml为每几个item的Layout */
        View v = (View) factory.inflate(R.layout.list_item_settings, null);
        /* 取得View */
        ImageView iv = (ImageView) v.findViewById(R.id.imageIcon);
        TextView tv = (TextView) v.findViewById(R.id.titleItem);
        ImageView ivDrop = (ImageView) v.findViewById(R.id.imageDropItem);
        ivDrop.setBackgroundResource(R.drawable.set_drop);
        /* 设定显示的Image与文字 */
        iv.setImageResource(_icons[position]);
        tv.setText(_items[position]);
        return v;
    }
}
