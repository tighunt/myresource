package com.rtk.tv.fragment.tvsetup;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.BaseFragment;
import com.rtk.tv.utils.FragmentUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnKeyListener;

public class AtscParentControlPasswordFragment extends BaseFragment {
    
    private static final String TAG = "com.rtk.tv-AtscParentControlPasswordFragment";
    
    int pin1;
    int pin2;
    int pin3;
    int pin4;

    String pin;

    TextView pin1_text;
    TextView pin2_text;
    TextView pin3_text;
    TextView pin4_text;
    TextView toast_text;
    
    View mainView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.layout_fragment_atsc_parent_control_enter_password, container, false);

        pin="";
        View PreView = null;

        pin1_text = (TextView)mainView.findViewById(R.id.PIN1);
        if(pin1_text != null)
        {
            pin1_text.setFocusable(true);
            pin1_text.setOnKeyListener(ViewKeyListener);

            PreView = pin1_text;
        }

        pin2_text = (TextView)mainView.findViewById(R.id.PIN2);
        if(pin2_text != null)
        {
            pin2_text.setFocusable(true);
            pin2_text.setOnKeyListener(ViewKeyListener);

            PreView.setTag(pin2_text);
            PreView = pin2_text;
        }

        pin3_text = (TextView)mainView.findViewById(R.id.PIN3);
        if(pin3_text != null)
        {
            pin3_text.setFocusable(true);
            pin3_text.setOnKeyListener(ViewKeyListener);

            PreView.setTag(pin3_text);
            PreView = pin3_text;
        }

        pin4_text = (TextView)mainView.findViewById(R.id.PIN4);
        if(pin4_text != null)
        {
            pin4_text.setFocusable(true);
            pin4_text.setOnKeyListener(ViewKeyListener);

            PreView.setTag(pin4_text);
            PreView = pin4_text;
            PreView.setTag(pin1_text);
        }
        
        toast_text = (TextView)mainView.findViewById(R.id.TOAST);

        mainView.requestFocus();
        
        return mainView;
    }

    @Override
    public void onResume()
    {
        Log.d(TAG,"onResume");
        super.onResume();

        if (mainView.isInTouchMode()) {
            mainView.requestFocusFromTouch();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                return true;
            default:
                return false;
        }
    }

    void InitPasswdView()
    {
        if(pin1_text != null)
            pin1_text.setText("");
        if(pin2_text != null)
            pin2_text.setText("");
        if(pin3_text != null)
            pin3_text.setText("");
        if(pin4_text != null)
            pin4_text.setText("");

        pin = "";
    }

    protected void ShowInvalidToast(){
        if(toast_text != null)
            toast_text.setText(R.string.STRING_WRONG_PIN);
    }

    protected void PasswordConfirm(){
        FragmentUtils.showSubFragment(this, AtscParentControlFragment.class);
    }
    
    protected OnKeyListener ViewKeyListener = new View.OnKeyListener()
    {
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
            boolean ret = false;

            if(event.getAction() == KeyEvent.ACTION_DOWN)
            {
                if(toast_text != null)
                    toast_text.setText("");
                if(keyCode>=KeyEvent.KEYCODE_0 && keyCode<=KeyEvent.KEYCODE_9)
                {
                    int mValue = -1;
                    int ViewID = v.getId();
                    View nextView = (v.getTag() instanceof View)? (View)v.getTag():null;
                    switch(ViewID)
                    {
                        case R.id.PIN1:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            if(nextView != null)
                                nextView.requestFocus();
                            pin1 = mValue;
                            break;
                        case R.id.PIN2:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            if(nextView != null)
                                nextView.requestFocus();
                            pin2 = mValue;
                            break;
                        case R.id.PIN3:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            if(nextView != null)
                                nextView.requestFocus();
                            pin3 = mValue;
                            break;
                        case R.id.PIN4:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            pin4 = mValue;

                            pin = pin + pin1 + pin2 + pin3 + pin4;
                            Log.d(TAG, "keycode PIN4 pin : "+pin);
                            final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
                            int tvServerPasswd = tm.getPLPassword();
                            Log.d(TAG,"tvServerPasswd is "+tvServerPasswd+ ",our pin is "+pin);
                            if(Integer.valueOf(pin) == tvServerPasswd){
                                PasswordConfirm();
                            }
                            else{
                                ShowInvalidToast();
                                InitPasswdView();

                                if(nextView != null)
                                    nextView.requestFocus();
                            }

                            break;
                    }
                    Log.d(TAG, "keycode pin : "+pin);
                    return true;
                }
                else  if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT || keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)
                {
                    int viewID = v.getId();
                    String strText = ((TextView)v).getText().toString();
                    if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT)
                    {
                        Log.d(TAG,"@@@ KEYCODE_DPAD_LEFT");
                        
                        Log.d(TAG,"((TextView)v).getText() : "+strText);
                        switch(viewID)
                        {
                           case R.id.PIN1:
                            Log.d(TAG,"@@@ PIN1");
                                break;
                           case R.id.PIN2:
                            Log.d(TAG,"@@@ PIN2");
                                if(v instanceof TextView)
                                {
                                    if(strText.equals(""))
                                    {
                                        //((TextView)v).setText("-");
                                    
                                        pin1_text.requestFocus();
                                        //pin1_text.setText("");
                                    }
                                    
                                }
                                break;
                           case R.id.PIN3:
                                Log.d(TAG,"@@@ PIN3");
                                if(v instanceof TextView)
                                {
                                    if(strText.equals(""))
                                    {
                                        //((TextView)v).setText("-");
                                    
                                        pin2_text.requestFocus();
                                        //pin2_text.setText("");
                                    }
                                    
                                }
                                break;
                           case R.id.PIN4:
                                Log.d(TAG,"PIN4");
                                if(v instanceof TextView)
                                {
                                    if(strText.equals(""))
                                    {
                                        //((TextView)v).setText("-");
                                    
                                        pin3_text.requestFocus();
                                        //pin3_text.setText("");
                                    }
                                    
                                }
                                break;   
                            }
                            Log.d(TAG, "Left pin : "+pin);
                            //Log.d(TAG,"hundred : "+hundred +" ; ten : " + ten  +" ; unit : " + unit);
                            return true;
                        }
                        else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)
                        {
                            Log.d(TAG,"@@@ KEYCODE_DPAD_RIGHT");
                            //Log.d(TAG,"((TextView)v).getText() : "+strText);                        
                            switch(viewID)
                            {
                            case R.id.PIN1:
                                Log.d(TAG,"@@@ PIN1");
                                if(v instanceof TextView)
                                {
                                    if(((TextView)v).getText().equals("*"))
                                    {
                                        //((TextView)v).setText("0");
                                    
                                        pin2_text.requestFocus();
                                    }
                                    /*else
                                    {
                                        unitView.requestFocus();
                                    }*/
                                }
                               break;
                           case R.id.PIN2:
                                Log.d(TAG,"Right : @@@ PIN2");
                                if(v instanceof TextView)
                                {
                                    if(((TextView)v).getText().equals("*"))
                                    {
                                        //((TextView)v).setText("0");
                                    
                                        pin2_text.requestFocus();
                                    }
                                    /*else
                                    {
                                        unitView.requestFocus();
                                    }*/
                                }
                                /*if(v instanceof TextView)
                                {
                                    if(((TextView)v).getText().equals("*"))
                                    {
                                        ((TextView)v).setText("0");
                                    
                                        unitView.requestFocus();
                                    }
                                    else
                                    {
                                        unitView.requestFocus();
                                    }
                                }*/
                                break;
                           case R.id.PIN3:
                               if(v instanceof TextView)
                                {
                                    if(((TextView)v).getText().equals("*"))
                                    {
                                        //((TextView)v).setText("0");
                                    
                                        pin2_text.requestFocus();
                                    }
                                    /*else
                                    {
                                        unitView.requestFocus();
                                    }*/
                                }
                                Log.d(TAG,"@@@ PIN3");
                                break;   
                           case R.id.PIN4:
                                Log.d(TAG,"@@@ PIN4");
                                break;   
                            }
                            Log.d(TAG, "pin : "+pin);
                            return true;
                        }
                        return true;
                    }
                else
                    return ret;
            }
            else
                return v.onKeyUp(keyCode, event);
        }
    };
}
