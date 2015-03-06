
package com.android.settings.desktop;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.settings.R;

/**
 * 桌面设置中组件的初始化
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-9 上午11:21:25
 * @since 1.0
 */
public class DesktopSettingViewHolder {

    private DesktopSettingActivity mDesktopSettingActivity;

    // 桌面设置中背景设置左箭头
    protected ImageView background_left_arrowHead;

    // 桌面设置中背景设置右箭头
    protected ImageView background_right_arrowHead;

    // 桌面设置中风格设置左箭头
    protected ImageView style_left_arrowHead;

    // 桌面设置中风格设置右箭头
    protected ImageView style_right_arrowHead;

    // 背景布局
    protected RelativeLayout mBackgroundRelativeLayout;

    // 风格布局
    protected RelativeLayout mStyleRelativeLayout;

    // 修改背景图片
    protected Button change_background_btn;

    // 修改风格
    protected Button change_style_btn;

    public DesktopSettingViewHolder(DesktopSettingActivity desktopSettingActivity) {
        super();
        this.mDesktopSettingActivity = desktopSettingActivity;
        findViews();
    }

    private void findViews() {
        background_left_arrowHead = (ImageView) mDesktopSettingActivity
                .findViewById(R.id.desk_left_arrowhead);
        background_right_arrowHead = (ImageView) mDesktopSettingActivity
                .findViewById(R.id.desk_right_arrowhead);
        style_left_arrowHead = (ImageView) mDesktopSettingActivity
                .findViewById(R.id.desk_style_left_arrowhead);
        style_right_arrowHead = (ImageView) mDesktopSettingActivity
                .findViewById(R.id.desk_style_right_arrowhead);

        mBackgroundRelativeLayout = (RelativeLayout) mDesktopSettingActivity
                .findViewById(R.id.desktop_background_rl);
        mStyleRelativeLayout = (RelativeLayout) mDesktopSettingActivity
                .findViewById(R.id.desktop_style_rl);
        change_background_btn = (Button) mDesktopSettingActivity
                .findViewById(R.id.change_image_btn);
        change_style_btn = (Button) mDesktopSettingActivity.findViewById(R.id.change_style_btn);
    }

}
