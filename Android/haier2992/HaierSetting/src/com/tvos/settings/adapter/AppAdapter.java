
package com.tvos.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;

public class AppAdapter extends BaseAdapter {

    private Context context;

    private String[] _items;

    private String[] _itemSize;

    private int[] icons;

    public AppAdapter(Context context, int[] icons, String[] _items, String[] _itemSize) {
        this.context = context;
        this.icons = icons;
        this._items = _items;
        this._itemSize = _itemSize;
    }

    @Override
    public int getCount() {
        return _itemSize.length;
    }

    @Override
    public Object getItem(int position) {
        return _itemSize[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater factory = LayoutInflater.from(context);
        /* 使用list_item_settings.xml为每几个item的Layout */
        View v = (View) factory.inflate(R.layout.list_item_apps, null);
        /* 取得View */
        ImageView iv = (ImageView) v.findViewById(R.id.imageItem);
        TextView title = (TextView) v.findViewById(R.id.itemTitle);
        TextView size = (TextView) v.findViewById(R.id.itemSize);
        /* 设定显示的Image与文字 */
        iv.setImageResource(icons[position]);
        title.setText(_items[position]);
        size.setText(_itemSize[position]);
        return v;
    }
}
