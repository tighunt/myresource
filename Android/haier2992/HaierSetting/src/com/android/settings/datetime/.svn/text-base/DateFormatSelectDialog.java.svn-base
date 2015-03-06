
package com.android.settings.datetime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.settings.DateTimeSettings;
import com.android.settings.R;

/**
 * 自定义Dialog用于用户对日期格式的选择
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-5 上午09:51:36
 * @since 1.0
 */
public class DateFormatSelectDialog extends Dialog {

    private final static String NAME = "share_pres";

    private final static String DATEFORMAT_INDEX = "date_format_index";

    private DateTimeSettings mDateTimeSettingActivity;

    // 存储日期格式的数据
    private List<Map<String, Object>> dateFormatDatas = new ArrayList<Map<String, Object>>();

    private SimpleAdapter dateFormatAdapter;

    // 日期格式
    private String dateFormatString;

    // 日期格式目前选择项
    private int mDateFormatIndex;

    private Handler onItemClickHandler;

    public DateFormatSelectDialog(DateTimeSettings dateTimeSettingActivity, Handler handler) {
        super(dateTimeSettingActivity);
        this.mDateTimeSettingActivity = dateTimeSettingActivity;
        this.onItemClickHandler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_format_list);

        // 实例化新的窗口
        Window w = getWindow();

        // 获取默认显示数据
        Display display = w.getWindowManager().getDefaultDisplay();

        // 获取窗口的背景图片
        Resources resources = mDateTimeSettingActivity.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.dialog_bg);
        // // 设置窗口的背景图片
        w.setBackgroundDrawable(drawable);

        // 窗口的标题为空
        w.setTitle("                                               "
        		+mDateTimeSettingActivity.getResources().getString(R.string.choose_date_format));
        w.setTitleColor(Color.argb(204, 204, 204,204));
		
        
        // 定义窗口的宽和高
        int width = (int) (display.getWidth() * 0.5);
        int height = (int) (display.getHeight() * 0.6);///[2011-1-13yanhd] change dialog height

        // 设置窗口的大小
        w.setLayout(width, height);

        // 设置窗口的显示位置
        w.setGravity(Gravity.CENTER);

        // 设置窗口的属性
        WindowManager.LayoutParams wl = w.getAttributes();
        w.setAttributes(wl);

        getDateFormat();

        findViews();
    }

    private void findViews() {
        ListView dateFormat = (ListView) findViewById(R.id.date_format_list);
        dateFormat.setDivider(null);

        final String items[] = mDateTimeSettingActivity.getResources().getStringArray(
                R.array.date_format_values);

        // 添加第一项数据
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("txtItem", items[0]);
        if (mDateFormatIndex == 0) {
            map1.put("imgItem", R.drawable.selected);
        } else {
            map1.put("imgItem", R.drawable.unselected);
        }
        dateFormatDatas.add(map1);

        // 添加第二项数据
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("txtItem", items[1]);
        if (mDateFormatIndex == 1) {
            map2.put("imgItem", R.drawable.selected);
        } else {
            map2.put("imgItem", R.drawable.unselected);
        }
        dateFormatDatas.add(map2);

        // 添加第三项数据
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("txtItem", items[2]);
        if (mDateFormatIndex == 2) {
            map3.put("imgItem", R.drawable.selected);
        } else {
            map3.put("imgItem", R.drawable.unselected);
        }
        dateFormatDatas.add(map3);

        // 实例化适配器
        dateFormatAdapter = new SimpleAdapter(mDateTimeSettingActivity, dateFormatDatas,
                R.layout.date_format_item_list, new String[] {
                        "txtItem", "imgItem"
                }, new int[] {
                        R.id.date_format_item, R.id.date_format_item_iv
                });

        // 添加适配器
        dateFormat.setAdapter(dateFormatAdapter);
        dateFormat.requestFocus();
        dateFormat.setSelection(mDateFormatIndex);

        dateFormat.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeRadioImg(mDateFormatIndex, false);
                changeRadioImg(position, true);
                mDateFormatIndex = position;
                commitDateFormat();
                dateFormatString = mDateTimeSettingActivity.dateFormatStrings[position];
                Message msg = new Message();
                Bundle mBundle = new Bundle();
                mBundle.putString("dateFormat", dateFormatString);
                msg.setData(mBundle);
                onItemClickHandler.sendMessage(msg);
                dismiss();
            }
        });
    }

    /**
     * 实时改变单选项
     * 
     * @param selectedItem 当前选中项
     * @param b 是否点击此项
     */
    @SuppressWarnings("unchecked")
	private void changeRadioImg(int selectedItem, boolean b) {
        SimpleAdapter la = dateFormatAdapter;
        HashMap<String, Object> map = (HashMap<String, Object>) la.getItem(selectedItem);
        if (b) {
            map.put("imgItem", R.drawable.selected);
        } else {
            map.put("imgItem", R.drawable.unselected);
        }
        la.notifyDataSetChanged();
    }

    /**
     * 把日期格式的索引保存到本地
     */
    private void commitDateFormat() {

        SharedPreferences preference = mDateTimeSettingActivity.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);

        Editor edit = preference.edit();

        edit.putInt(DATEFORMAT_INDEX, mDateFormatIndex);

        edit.commit();
    }

    /**
     * 获取日期格式的索引
     */
    private void getDateFormat() {
        SharedPreferences preference = mDateTimeSettingActivity.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);

        mDateFormatIndex = preference.getInt(DATEFORMAT_INDEX, 0);
    }
}
