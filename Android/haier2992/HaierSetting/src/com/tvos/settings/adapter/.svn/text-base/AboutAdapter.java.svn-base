
package com.tvos.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.settings.R;

/**
 * 自定义的adapter,用于关于子菜单的显示
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-3 上午10:00:10
 * @since 1.0
 */
public class AboutAdapter extends BaseAdapter {

    // 上下文
    private Context context;

    // 每个子菜单的标题
    private String[] _items;

    // 每个子菜单的内容
    private String[] _content;

    /**
     * 构造函数
     * 
     * @param context 上下文
     * @param _items 每个子菜单的标题
     * @param content 每个子菜单的内容
     */
    public AboutAdapter(Context context, String[] _items, String[] content) {
        this.context = context;
        this._items = _items;
        this._content = content;
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
        View v = null;
        // 法律信息子菜单和其他子菜单显示有区别，因此在此处需要进行判断，应该加载哪个Layout
//        if (position != 3) {
            // 取得View
            v = (View) factory.inflate(R.layout.about_item_text, null);
            TextView title = (TextView) v.findViewById(R.id.aboutItem);
            TextView content = (TextView) v.findViewById(R.id.content);
            // 设定显示的文 字
            title.setText(_items[position]);
            content.setText(_content[position]);
            
//        }
//        } else {
//            // 法律信息子菜单
//            v = (View) factory.inflate(R.layout.about_item_image, null);
//            TextView title = (TextView) v.findViewById(R.id.item_name_tv);
//            ImageView image = (ImageView) v.findViewById(R.id.item_image);
//            title.setText(_items[3]);
//            image.setBackgroundResource(R.drawable.desktop_right);
//        }
        return v;
    }
}
