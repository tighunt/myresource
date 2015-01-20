package com.rtk.tv.fragment.tvsetup;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.BaseFragment;
import com.rtk.tv.utils.FragmentUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;

public class AtscParentControlChangePasswordFragment extends BaseFragment {

    private static final String TAG = "com.rtk.tv-AtscParentControlChangePasswordFragment";    
    View mainView;    
    int newpin1;
    int newpin2;
    int newpin3;
    int newpin4;
    String newpin;
    int confirmpin1;
    int confirmpin2;
    int confirmpin3;
    int confirmpin4;
    String confirmpin;
    TextView newpin1_text;
    TextView newpin2_text;
    TextView newpin3_text;
    TextView newpin4_text;
    TextView confirmpin1_text;
    TextView confirmpin2_text;
    TextView confirmpin3_text;
    TextView confirmpin4_text;   
    TextView toast_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        newpin="";
        confirmpin="";
    }

    void InitPasswdView()
    {
        if(newpin1_text != null)
            newpin1_text.setText("");
        if(newpin2_text != null)
            newpin2_text.setText("");
        if(newpin3_text != null)
            newpin3_text.setText("");
        if(newpin4_text != null)
            newpin4_text.setText("");

        if(confirmpin1_text != null)
            confirmpin1_text.setText("");
        if(confirmpin2_text != null)
            confirmpin2_text.setText("");
        if(confirmpin3_text != null)
            confirmpin3_text.setText("");
        if(confirmpin4_text != null)
            confirmpin4_text.setText("");

        newpin = "";
        confirmpin = "";
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG,"onCreateView");
        mainView = inflater.inflate(R.layout.layout_fragment_atsc_parent_control_change_password,container, false);

        View PreView = null;

        newpin1_text = (TextView)mainView.findViewById(R.id.NEW_PIN1);
        if(newpin1_text != null)
        {
            newpin1_text.setFocusable(true);
            newpin1_text.setOnKeyListener(ViewKeyListener);

            PreView = newpin1_text;
        }

        newpin2_text = (TextView)mainView.findViewById(R.id.NEW_PIN2);
        if(newpin2_text != null)
        {
            newpin2_text.setFocusable(true);
            newpin2_text.setOnKeyListener(ViewKeyListener);

            PreView.setTag(newpin2_text);
            PreView = newpin2_text;
        }

        newpin3_text = (TextView)mainView.findViewById(R.id.NEW_PIN3);
        if(newpin3_text != null)
        {
            newpin3_text.setFocusable(true);
            newpin3_text.setOnKeyListener(ViewKeyListener);

            PreView.setTag(newpin3_text);
            PreView = newpin3_text;
        }

        newpin4_text = (TextView)mainView.findViewById(R.id.NEW_PIN4);
        if(newpin4_text != null)
        {
            newpin4_text.setFocusable(true);
            newpin4_text.setOnKeyListener(ViewKeyListener);

            PreView.setTag(newpin4_text);
            PreView = newpin4_text;
        }

        confirmpin1_text = (TextView)mainView.findViewById(R.id.CONFIRM_PIN1);
        if(confirmpin1_text != null)
        {
            confirmpin1_text.setFocusable(true);
            confirmpin1_text.setOnKeyListener(ViewKeyListener);

            PreView.setTag(confirmpin1_text);
            PreView = confirmpin1_text;
        }

        confirmpin2_text = (TextView)mainView.findViewById(R.id.CONFIRM_PIN2);
        if(confirmpin2_text != null)
        {
            confirmpin2_text.setFocusable(true);
            confirmpin2_text.setOnKeyListener(ViewKeyListener);

            PreView.setTag(confirmpin2_text);
            PreView = confirmpin2_text;
        }
        confirmpin3_text = (TextView)mainView.findViewById(R.id.CONFIRM_PIN3);
        if(confirmpin3_text != null)
        {
            confirmpin3_text.setFocusable(true);
            confirmpin3_text.setOnKeyListener(ViewKeyListener);

            PreView.setTag(confirmpin3_text);
            PreView = confirmpin3_text;
        }
        confirmpin4_text = (TextView)mainView.findViewById(R.id.CONFIRM_PIN4);
        if(confirmpin4_text != null)
        {
            confirmpin4_text.setFocusable(true);
            confirmpin4_text.setOnKeyListener(ViewKeyListener);

            PreView.setTag(confirmpin4_text);
            PreView = confirmpin4_text;
            PreView.setTag(newpin1_text);
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

    protected void ShowWrongPinToast(){
        if(toast_text != null)
            toast_text.setText(R.string.STRING_WRONG_PIN);
    }

    protected void PasswordConfirm(){
        FragmentUtils.popBackSubFragment(this);
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
                        case R.id.NEW_PIN1:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            if(nextView != null)
                                nextView.requestFocus();
                            newpin1 = mValue;
                            break;
                        case R.id.NEW_PIN2:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            if(nextView != null)
                                nextView.requestFocus();
                            newpin2 = mValue;
                            break;
                        case R.id.NEW_PIN3:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            if(nextView != null)
                                nextView.requestFocus();
                            newpin3 = mValue;
                            break;
                        case R.id.NEW_PIN4:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            if(nextView != null)
                                nextView.requestFocus();
                            newpin4 = mValue;
                            break;
                        case R.id.CONFIRM_PIN1:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            if(nextView != null)
                                nextView.requestFocus();
                            confirmpin1 = mValue;
                            break;
                        case R.id.CONFIRM_PIN2:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            if(nextView != null)
                                nextView.requestFocus();
                            confirmpin2 = mValue;
                            break;
                        case R.id.CONFIRM_PIN3:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");
                            if(nextView != null)
                                nextView.requestFocus();
                            confirmpin3 = mValue;
                            break;
                        case R.id.CONFIRM_PIN4:
                            mValue = keyCode-KeyEvent.KEYCODE_0;
                            if(v instanceof TextView)
                                ((TextView)v).setText("*");

                            confirmpin4 = mValue;
                            newpin = newpin + newpin1 + newpin2 + newpin3 + newpin4;
                            confirmpin = confirmpin + confirmpin1 + confirmpin2 + confirmpin3 + confirmpin4;
                            if(newpin.equals(confirmpin)){
                                final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
                                tm.setPLPassword(Integer.valueOf(newpin));
                                PasswordConfirm();
                            }
                            else{
                                ShowWrongPinToast();

                                InitPasswdView();

                                if(nextView != null)
                                    nextView.requestFocus();
                            }

                            break;
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
