
package com.android.settings.applications;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;

/**
 * 自定义Adapter,提供给listView的自定义view
 * 
 * @author ducj
 * @date 2011-11-10 下午02:47:47
 * @since 1.0
 */
public class ApplicationInforAdapter extends BaseAdapter {

    private List<AppInfor> mListAppInfor = null;

    private LayoutInflater mLayoutInflater = null;

    private Context mContext;

    public ApplicationInforAdapter(Context context, List<AppInfor> appsInfor) {
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        this.mListAppInfor = appsInfor;
        Collections.sort(this.mListAppInfor, ApplicationInforAdapter.APP_INSTALL_TIME_COMPARATOR);
        
    }

    @Override
    public int getCount() {
        return mListAppInfor.size();
    }

    @Override
    public Object getItem(int position) {
        return mListAppInfor.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        ViewHolder viewHolder = null;

        if (convertView == null || convertView.getTag() == null) {
            view = mLayoutInflater.inflate(R.layout.list_item_apps, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AppInfor appInfor = (AppInfor) getItem(position);
        Bitmap bm = appInfor.getIconAppDrawable();
        BitmapDrawable bd = new BitmapDrawable(bm);
        viewHolder.appIcon.setBackgroundDrawable(bd);
        viewHolder.appName.setText(appInfor.getAppName());
        viewHolder.appSize.setText(formateFileSize(appInfor.getAppSize()));
        return view;
    }

    /**
     * 组件的初始化
     */
    class ViewHolder {
        ImageView appIcon;

        TextView appName;

        TextView appSize;

        public ViewHolder(View view) {
            this.appIcon = (ImageView) view.findViewById(R.id.imageItem);
            this.appName = (TextView) view.findViewById(R.id.itemTitle);
            this.appSize = (TextView) view.findViewById(R.id.itemSize);
        }
    }

    /**
     * 系统函数，字符串转换 long -String (kb)
     * 
     * @param size
     * @return
     */
    private String formateFileSize(long size) {
        if (size > 0) {
            return Formatter.formatFileSize(mContext, size);
        }
        return null;
    }
    
    public static final Comparator<AppInfor> APP_INSTALL_TIME_COMPARATOR
    	= new Comparator<AppInfor>() {
    		public final int compare(AppInfor a, AppInfor b) {
    			if (a.getFirstInstallTime() > b.getFirstInstallTime()) return 1;
    				if (a.getFirstInstallTime() < b.getFirstInstallTime()) return -1;
    				return 0;
    		}
    };
}
