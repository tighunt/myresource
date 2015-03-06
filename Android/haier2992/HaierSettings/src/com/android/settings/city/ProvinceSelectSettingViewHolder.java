
package com.android.settings.city;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RelativeLayout;
import android.widget.Button;

import com.android.settings.R;

/**
 * 省份选择中组件的初始化
 * 
 * @author 曹美娟
 * @date 2012-1-18 下午02:46:06
 * @since 1.0
 */
public class ProvinceSelectSettingViewHolder {

    private ProvinceSelectSettingActivity mProvinceSelectSettingActivity;
    // add by Zhanghs at 2012-02-11 begin
    protected SharedPreferences sf;
    private static final String CITY_NAME = "cityName";
    private static final String SELECTED_CITY_ID= "selectedCityId";
    private static final String SELECTED_CITY_NAME = "selectedCityName";
    // add by Zhanghs at 2012-02-11 end

    public Button mBeijing;
    public Button mShanghai;
    public Button mChongqing;   
    public Button mTianjin;
    public Button mXianggang;
    public Button mAomen;   
    public Button mAnhui;
    public Button mNeimenggu;
    public Button mNingxia;
    public Button mFujian;
    public Button mQinghai;
    public Button mGuangxi;   
    public Button mGuangdong;
    public Button mGuizhou;
    public Button mGansu;   
    public Button mShandong;
    public Button mShanxi;
    public Button mShaanxi;
    public Button mSichuan;
    public Button mHainan;
    public Button mHenan;   
    public Button mHebei;
    public Button mHubei;
    public Button mHunan;   
    public Button mHeilongjiang;
    public Button mTaiwan;
    public Button mXizang;
    public Button mXinjiang;
    public Button mJilin;
    public Button mJiangsu;
    public Button mJiangxi;
    public Button mYunnan;
    public Button mLiaoning;
    public Button mZhejiang;
    public ProvinceSelectSettingViewHolder(ProvinceSelectSettingActivity provinceSelectSettingActivity) {
        this.mProvinceSelectSettingActivity = provinceSelectSettingActivity;
        // add by Zhanghs at 2012-02-11 begin
        this.sf = provinceSelectSettingActivity.getSharedPreferences(CITY_NAME, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        // add by Zhanghs at 2012-02-11 end
        findViews();
    }

    private void findViews() {
    	mBeijing = (Button) mProvinceSelectSettingActivity
                .findViewById(R.id.province_list_beijing);
    	mShanghai = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_shanghai);
    	mChongqing = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_chongqing);
        mTianjin = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_tianjin);
        mXianggang = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_xianggang);
        mAomen = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_aomen);   
        mAnhui = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_anhui);
        mNeimenggu = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_neimenggu);
        mNingxia = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_ningxia);
        mFujian = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_fujian);
        mQinghai = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_qinghai);
        mGuangxi = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_guangxi);  
        mGuangdong = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_guangdong);
        mGuizhou = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_guizhou);
        mGansu = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_gansu);  
        mShandong = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_shandong);
        mShanxi = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_shanxi);
	    mShaanxi = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_shaanxi);
	    mSichuan = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_sichuan);
	    mHainan = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_hainan);
	    mHenan = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_henan);  
	    mHebei = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_hebei);
	    mHubei = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_hubei);
	    mHunan = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_hunan);   
	    mHeilongjiang = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_heilongjiang);
	    mTaiwan = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_taiwan);
	    mXizang = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_xizang);
	    mXinjiang = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_xinjiang);
	    mJilin = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_jilin);
	    mJiangsu = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_jiangsu);
	    mJiangxi = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_jiangxi);
	    mYunnan = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_yunnan);
	    mLiaoning = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_liaoning);
	    mZhejiang = (Button) mProvinceSelectSettingActivity.findViewById(R.id.province_list_zhejiang);
    }
    
	/**
	 * add by Zhanghs at 2012-02-11
	 * 
	 * @param cityName
	 */
	public void putString(String cityId,String cityName) {
		sf.edit().putString(SELECTED_CITY_ID, cityId).commit();
		sf.edit().putString(SELECTED_CITY_NAME, cityName).commit();
	}

	/**
	 * add by Zhanghs at 2012-02-11
	 * 
	 * @param cityName
	 * @return
	 */
	public String getString(String cityName) {
		return sf.getString(SELECTED_CITY_NAME, cityName);
	}

}
