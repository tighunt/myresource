
package com.rtk.tv.fragment.tvsetup;

import com.rtk.tv.R;
import com.rtk.tv.fragment.BaseFragment;
import com.rtk.tv.utils.FragmentUtils;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConfirmDialogFragment extends BaseFragment implements OnKeyListener {
    
    private static final String TAG = "com.rtk.tv-ConfirmDialogFragment";
    
    View mainView;
    View button_ok;
    View button_cancel;

	public static void show(Fragment parent, Class<? extends Fragment> targetFragment, Bundle targetArgs, int title, int message) {
		Bundle arg = new Bundle();
		arg.putInt("title", title);
		arg.putInt("message", message);
		arg.putString("targetClass", targetFragment.getName());
		arg.putBundle("targetArgs", targetArgs);
		FragmentUtils.showSubFragment(parent, ConfirmDialogFragment.class, arg);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = getArguments();
		int title = args.getInt("title");
		int message = args.getInt("message");

		View v = inflater.inflate(R.layout.dialog_alert_generic, container, false);
		mainView = v;
		v.findViewById(R.id.button_neutral).setVisibility(View.GONE);	
		v.findViewById(R.id.button_ok).setOnKeyListener(this);
		v.findViewById(R.id.button_cancel).setOnKeyListener(this);
		
		v.findViewById(R.id.button_ok).setNextFocusLeftId(R.id.button_cancel);
		v.findViewById(R.id.button_cancel).setNextFocusRightId(R.id.button_ok);
		
		TextView textTitle = (TextView) v.findViewById(R.id.text_title);
		TextView textMessage = (TextView) v.findViewById(R.id.text_message);

		textTitle.setText(title);
		textMessage.setText(message);
		return v;
	}
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        boolean ret = false;
        Log.d(TAG, "onKey, KeyEvent========" + event + "=========");
        switch(keyCode){
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(v.getId() == R.id.button_cancel)
                    mainView.findViewById(v.getNextFocusRightId()).requestFocus();       
                ret = true;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(v.getId() == R.id.button_ok)
                    mainView.findViewById(v.getNextFocusLeftId()).requestFocus();
                ret = true;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if(v.getId() == R.id.button_ok)
                {
                    Bundle args = getArguments();
                    String clazz = args.getString("targetClass");
                    Bundle ta = args.getBundle("targetArgs");
                    FragmentUtils.showSubFragment(this, clazz, ta);
                }
                else if(v.getId() == R.id.button_cancel)
                {
                    FragmentUtils.popBackSubFragment(this);
                }
                ret = true;
                break;
                    
            default:
                break;
            
        }        
        
        return ret;
    }

}
