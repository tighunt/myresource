
package com.android.settings.city;

import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.settings.R;

/**
 * 城市选择界面的初始化
 * 
 * @author 曹美娟
 * @date 2012-1-7 上午10:21:10
 * @since 1.0
 */
public class CityViewHolder {

    private CitySettings mCitySettingActivity;
    
    // 城市列表
    public RelativeLayout mCity_list;
  
    //城市选择
    public Button select_city;
    
    // 当前城市
    public RelativeLayout mCurrent_city;
    
    // 当前城市
    public Button current_city;
    
    public CityViewHolder(CitySettings citySettingActivity) {
        this.mCitySettingActivity = citySettingActivity;
        findViews();
    }

    private void findViews() {   
    	mCity_list = (RelativeLayout) mCitySettingActivity.findViewById(R.id.city_list);
    	select_city = (Button) mCitySettingActivity.findViewById(R.id.city_list_set);
    	mCurrent_city = (RelativeLayout) mCitySettingActivity.findViewById(R.id.city);
    	current_city = (Button) mCitySettingActivity.findViewById(R.id.current_city);
    }

}
