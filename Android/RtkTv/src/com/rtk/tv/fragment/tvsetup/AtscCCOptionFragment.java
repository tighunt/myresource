
package com.rtk.tv.fragment.tvsetup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.BaseMenuFragment;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.fragment.submenu.item.SpinnerMenuItem;

public class AtscCCOptionFragment extends BaseMenuFragment{
    
    private static final String TAG = "com.rtk.tv-AtscCCOptionFragment";
    
    private static final int[] VALUES_CC_OPTION_MODE = {
        TvManagerHelper.CC_option_Custom, 
        TvManagerHelper.CC_option_Default
    };
    private static final int[] VALUES_CC_FONT_STYLE = {
        TvManagerHelper.CC_char_Font0, 
        TvManagerHelper.CC_char_Font1,
        TvManagerHelper.CC_char_Font2,
        TvManagerHelper.CC_char_Font3,
        TvManagerHelper.CC_char_Font4,
        TvManagerHelper.CC_char_Font5,
        TvManagerHelper.CC_char_Font6,
        TvManagerHelper.CC_char_Font7,
        TvManagerHelper.CC_char_Font_Default
    };
    private static final int[] VALUES_CC_FONT_SIZE = {
        TvManagerHelper.CC_char_Size_Small, 
        TvManagerHelper.CC_char_Size_Std,
        TvManagerHelper.CC_char_Size_Large,
        TvManagerHelper.CC_char_Size_Default
    };
    private static final int[] VALUES_CC_FONT_EDGE_STYLE = {
        TvManagerHelper.CC_charEdge_None, 
        TvManagerHelper.CC_charEdge_Raised,
        TvManagerHelper.CC_charEdge_Depressed,
        TvManagerHelper.CC_charEdge_Uniform,
        TvManagerHelper.CC_charEdge_LeftDropShadow,
        TvManagerHelper.CC_charEdge_RightDropShadow,
        TvManagerHelper.CC_charEdge_Default
    };
    private static final int[] VALUES_CC_COLOR = {
        TvManagerHelper.CC_color_Black, 
        TvManagerHelper.CC_color_White,
        TvManagerHelper.CC_color_Red,
        TvManagerHelper.CC_color_Green,
        TvManagerHelper.CC_color_Blue,
        TvManagerHelper.CC_color_Yellow,
        TvManagerHelper.CC_color_Manenta,
        TvManagerHelper.CC_color_Cyan,
        TvManagerHelper.CC_color_Default,
    };
    private static final int[] VALUES_CC_OPACITY_STYLE = {
        TvManagerHelper.CC_opacity_Solid, 
        TvManagerHelper.CC_opacity_Flashing,
        TvManagerHelper.CC_opacity_Translucent,
        TvManagerHelper.CC_opacity_Transparent,
        TvManagerHelper.CC_opacity_low,
        TvManagerHelper.CC_opacity_high,
        TvManagerHelper.CC_opacity_Default
    };
    
    // Items
    private SpinnerMenuItem mItemOptionMode;
    private SpinnerMenuItem mItemFont;
    private SpinnerMenuItem mItemSize;
    private SpinnerMenuItem mItemEdgeEffect;
    private SpinnerMenuItem mItemEdgeColor;
    private SpinnerMenuItem mItemTextColor;
    private SpinnerMenuItem mItemBackgroundColor;
    private SpinnerMenuItem mItemTextOpacity;
    private SpinnerMenuItem mItemBackgroundOpacity;
    
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_list, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	    Log.d(TAG, "onViewCreated");
		super.onViewCreated(view, savedInstanceState);
		Button buttonOk = (Button) view.findViewById(R.id.button_ok);
		buttonOk.setVisibility(View.GONE);
		view.findViewById(R.id.button_neutral).setVisibility(View.GONE);
		view.findViewById(R.id.button_cancel).setVisibility(View.GONE);		
	}

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
	    Log.d(TAG, "onCreateMenuItems");
	    final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
	    final String[] cc_option_mode = getResources().getStringArray(R.array.atsc_cc_option_mode);
	    final String[] cc_font_style = getResources().getStringArray(R.array.atsc_cc_font_style);
	    final String[] cc_font_size = getResources().getStringArray(R.array.atsc_cc_font_size);
	    final String[] cc_font_edge_style = getResources().getStringArray(R.array.atsc_cc_font_edge_style);
	    final String[] cc_color = getResources().getStringArray(R.array.atsc_cc_color);
	    final String[] cc_opacity_style = getResources().getStringArray(R.array.atsc_cc_opacity_style);
	    
	    //option mode
	    mItemOptionMode = MenuItem.createSpinnerItem(R.string.string_cc_option_mode);
	    mItemOptionMode.setSpinnerOptionsByArray(cc_option_mode);
	    mItemOptionMode.setCurrentValue(indexOf(VALUES_CC_OPTION_MODE, tm.getAtscCCOptionMode()));
	    mItemOptionMode.setOnValueChangeListener(new OnValueChangeListener() {
            
            @Override
            public void onValueChanged(MenuItem item, int index) {
                tm.setAtscCCOptionMode(VALUES_CC_OPTION_MODE[index]);
                updateCCOption();
            }
        });
	    items.add(mItemOptionMode);	    
	    
	    //font
	    mItemFont = MenuItem.createSpinnerItem(R.string.string_cc_option_font);
	    mItemFont.setSpinnerOptionsByArray(cc_font_style);
	    mItemFont.setCurrentValue(indexOf(VALUES_CC_FONT_STYLE, tm.getAtscCCOptionFont()));
	    mItemFont.setOnValueChangeListener(new OnValueChangeListener() {
            
            @Override
            public void onValueChanged(MenuItem item, int index) {
                tm.setAtscCCOptionFont(VALUES_CC_FONT_STYLE[index]);
                //mItemFont.setEnable(isCustomMode());
            }
        });
	    mItemFont.setEnable(isCustomMode());
        items.add(mItemFont);
        
        //size
        mItemSize = MenuItem.createSpinnerItem(R.string.string_cc_option_size);
        mItemSize.setSpinnerOptionsByArray(cc_font_size);
        mItemSize.setCurrentValue(indexOf(VALUES_CC_FONT_SIZE, tm.getAtscCCOptionFontSize()));        
        mItemSize.setOnValueChangeListener(new OnValueChangeListener() {
            
            @Override
            public void onValueChanged(MenuItem item, int index) {
                tm.setAtscCCOptionFontSize(VALUES_CC_FONT_SIZE[index]);
                //mItemSize.setEnable(isCustomMode());
            }
        });
        mItemSize.setEnable(isCustomMode());
        items.add(mItemSize);
        
        //edge effect
        mItemEdgeEffect = MenuItem.createSpinnerItem(R.string.string_cc_option_edge_effect);
        mItemEdgeEffect.setSpinnerOptionsByArray(cc_font_edge_style);
        mItemEdgeEffect.setCurrentValue(indexOf(VALUES_CC_FONT_EDGE_STYLE, tm.getAtscCCOptionEdgeStyle()));        
        mItemEdgeEffect.setOnValueChangeListener(new OnValueChangeListener() {
            
            @Override
            public void onValueChanged(MenuItem item, int index) {
                tm.setAtscCCOptionEdgeStyle(VALUES_CC_FONT_EDGE_STYLE[index]);
                //mItemEdgeEffect.setEnable(isCustomMode());
            }
        });
        mItemEdgeEffect.setEnable(isCustomMode());
        items.add(mItemEdgeEffect);   
        
        //edge color
        mItemEdgeColor = MenuItem.createSpinnerItem(R.string.string_cc_option_edge_color);
        mItemEdgeColor.setSpinnerOptionsByArray(cc_color);
        mItemEdgeColor.setCurrentValue(indexOf(VALUES_CC_COLOR, tm.getAtscCCOptionEdgeColor()));        
        mItemEdgeColor.setOnValueChangeListener(new OnValueChangeListener() {
            
            @Override
            public void onValueChanged(MenuItem item, int index) {
                tm.setAtscCCOptionEdgeColor(VALUES_CC_COLOR[index]);
                //mItemEdgeColor.setEnable(isCustomMode());
            }
        });
        mItemEdgeColor.setEnable(isCustomMode());
        items.add(mItemEdgeColor);	
        
        //text color
        mItemTextColor = MenuItem.createSpinnerItem(R.string.string_cc_option_text_color);
        mItemTextColor.setSpinnerOptionsByArray(cc_color);
        mItemTextColor.setCurrentValue(indexOf(VALUES_CC_COLOR, tm.getAtscCCOptionTextColor()));        
        mItemTextColor.setOnValueChangeListener(new OnValueChangeListener() {
            
            @Override
            public void onValueChanged(MenuItem item, int index) {
                tm.setAtscCCOptionTextColor(VALUES_CC_COLOR[index]);
                //mItemTextColor.setEnable(isCustomMode());
            }
        });
        mItemTextColor.setEnable(isCustomMode());
        items.add(mItemTextColor);
        
        //bg color
        mItemBackgroundColor = MenuItem.createSpinnerItem(R.string.string_cc_option_background_color);
        mItemBackgroundColor.setSpinnerOptionsByArray(cc_color);
        mItemBackgroundColor.setCurrentValue(indexOf(VALUES_CC_COLOR, tm.getAtscCCOptionBgColor()));        
        mItemBackgroundColor.setOnValueChangeListener(new OnValueChangeListener() {
            
            @Override
            public void onValueChanged(MenuItem item, int index) {
                tm.setAtscCCOptionBgColor(VALUES_CC_COLOR[index]);
                //mItemBackgroundColor.setEnable(isCustomMode());
            }
        });
        mItemBackgroundColor.setEnable(isCustomMode());
        items.add(mItemBackgroundColor);
        
        //text opacity
        mItemTextOpacity = MenuItem.createSpinnerItem(R.string.string_cc_option_text_opacity);
        mItemTextOpacity.setSpinnerOptionsByArray(cc_opacity_style);
        mItemTextOpacity.setCurrentValue(indexOf(VALUES_CC_OPACITY_STYLE, tm.getAtscCCOptionTextOpacity()));        
        mItemTextOpacity.setOnValueChangeListener(new OnValueChangeListener() {
            
            @Override
            public void onValueChanged(MenuItem item, int index) {
                tm.setAtscCCOptionTextOpacity(VALUES_CC_OPACITY_STYLE[index]);
                //mItemTextOpacity.setEnable(isCustomMode());
            }
        });
        mItemTextOpacity.setEnable(isCustomMode());
        items.add(mItemTextOpacity);
        
        //bg opacity
        mItemBackgroundOpacity = MenuItem.createSpinnerItem(R.string.string_cc_option_background_opacity);
        mItemBackgroundOpacity.setSpinnerOptionsByArray(cc_opacity_style);
        mItemBackgroundOpacity.setCurrentValue(indexOf(VALUES_CC_OPACITY_STYLE, tm.getAtscCCOptionBgOpacity()));        
        mItemBackgroundOpacity.setOnValueChangeListener(new OnValueChangeListener() {
            
            @Override
            public void onValueChanged(MenuItem item, int index) {
                tm.setAtscCCOptionBgOpacity(VALUES_CC_OPACITY_STYLE[index]);
                //mItemBackgroundOpacity.setEnable(isCustomMode());
            }
        });
        mItemBackgroundOpacity.setEnable(isCustomMode());
        items.add(mItemBackgroundOpacity);
        
        tm.enter_captionPreview();
	}

	@Override
	public int getTitle() {
		return R.string.string_cc_option;
	}
	
	private boolean isCustomMode()
	{
	    boolean ret = mItemOptionMode.getSelectedValue()==0? true:false;
	    Log.d(TAG, "isCustomMode: " + ret);
	    return ret;
	}
	
	private void updateCCOption()
	{
	    Log.d(TAG, "updateCCOption");
	    final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
	    if(!isCustomMode()) //show all options to default(but no need set), and disabled
	    {
	        mItemFont.setCurrentValue(mItemFont.getOptionCount()-1).setEnable(false);
	        mItemSize.setCurrentValue(mItemSize.getOptionCount()-1).setEnable(false);
	        mItemEdgeEffect.setCurrentValue(mItemEdgeEffect.getOptionCount()-1).setEnable(false);
	        mItemEdgeColor.setCurrentValue(mItemEdgeColor.getOptionCount()-1).setEnable(false);
	        mItemTextColor.setCurrentValue(mItemTextColor.getOptionCount()-1).setEnable(false);
	        mItemBackgroundColor.setCurrentValue(mItemBackgroundColor.getOptionCount()-1).setEnable(false);
	        mItemTextOpacity.setCurrentValue(mItemTextOpacity.getOptionCount()-1).setEnable(false);
	        mItemBackgroundOpacity.setCurrentValue(mItemBackgroundOpacity.getOptionCount()-1).setEnable(false);
	    }
	    else
	    {
	        mItemFont.setCurrentValue(indexOf(VALUES_CC_FONT_STYLE, tm.getAtscCCOptionFont())).setEnable(true);
	        mItemSize.setCurrentValue(indexOf(VALUES_CC_FONT_SIZE, tm.getAtscCCOptionFontSize())).setEnable(true);
	        mItemEdgeEffect.setCurrentValue(indexOf(VALUES_CC_FONT_EDGE_STYLE, tm.getAtscCCOptionEdgeStyle())).setEnable(true);
	        mItemEdgeColor.setCurrentValue(indexOf(VALUES_CC_COLOR, tm.getAtscCCOptionEdgeColor())).setEnable(true);
	        mItemTextColor.setCurrentValue(indexOf(VALUES_CC_COLOR, tm.getAtscCCOptionTextColor())).setEnable(true);
	        mItemBackgroundColor.setCurrentValue(indexOf(VALUES_CC_COLOR, tm.getAtscCCOptionBgColor())).setEnable(true);
	        mItemTextOpacity.setCurrentValue(indexOf(VALUES_CC_OPACITY_STYLE, tm.getAtscCCOptionTextOpacity())).setEnable(true); 
	        mItemBackgroundOpacity.setCurrentValue(indexOf(VALUES_CC_OPACITY_STYLE, tm.getAtscCCOptionBgOpacity())).setEnable(true);	        
	    }
	}
	
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
        tm.exit_captionPreview();
    }

}
