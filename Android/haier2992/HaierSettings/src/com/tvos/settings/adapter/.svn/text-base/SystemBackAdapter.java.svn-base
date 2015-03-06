
package com.tvos.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.settings.R;

public class SystemBackAdapter extends BaseAdapter {

    private Context context;

    private String[] _items;

    public SystemBackAdapter(Context context, String[] _items) {
        this.context = context;
        this._items = _items;
    }

    @Override
    public int getCount() {
        return _items.length;
    }

    @Override
    public Object getItem(int position) {
        return _items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater factory = LayoutInflater.from(context);
        /* 使用list_item_settings.xml为每几个item的Layout */
        View v = (View) factory.inflate(R.layout.list_item_system_update, null);
        /* 取得View */
        TextView tv = (TextView) v.findViewById(R.id.updateItem);
        /* 设定显示的Image与文字 */
        tv.setText(_items[position]);
        return v;
    }

}
