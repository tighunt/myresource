package com.rtk.tv.fragment.tvsetup;

import com.rtk.tv.R;
import com.rtk.tv.fragment.BaseFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class AtscParentControlUSATVFragment extends BaseFragment {
    
    private static final String TAG = "com.rtk.tv-AtscParentControlUSATVFragment";
    
    // TV age and content table field custom setting
    private final int blockTvY_All = R.id.pl_usa_tv_table_row2_col2;
    private final int blockTvY7_All = R.id.pl_usa_tv_table_row3_col2;
    private final int blockTvG_All = R.id.pl_usa_tv_table_row4_col2;
    private final int blockTvPG_All = R.id.pl_usa_tv_table_row5_col2;
    private final int blockTv14_All = R.id.pl_usa_tv_table_row6_col2;
    private final int blockTvMA_All = R.id.pl_usa_tv_table_row7_col2;

    // For TV-Y7(all childen)
    private final int blockRatingTVY7_FV = R.id.pl_usa_tv_table_row3_col3;
    // For TV-PG(Parental Guidances suggested)
    private final int blockRatingTVPG_V = R.id.pl_usa_tv_table_row5_col4;
    private final int blockRatingTVPG_S = R.id.pl_usa_tv_table_row5_col5;
    private final int blockRatingTVPG_L = R.id.pl_usa_tv_table_row5_col6;
    private final int blockRatingTVPG_D = R.id.pl_usa_tv_table_row5_col7;
    // For TV-14(Parental strongly cautioned)
    private final int blockRatingTV14_V = R.id.pl_usa_tv_table_row6_col4;
    private final int blockRatingTV14_S = R.id.pl_usa_tv_table_row6_col5;
    private final int blockRatingTV14_L = R.id.pl_usa_tv_table_row6_col6;
    private final int blockRatingTV14_D = R.id.pl_usa_tv_table_row6_col7;
    // For TV-MA(Mature audience only)
    private final int blockRatingTVMA_V = R.id.pl_usa_tv_table_row7_col4;
    private final int blockRatingTVMA_S = R.id.pl_usa_tv_table_row7_col5;
    private final int blockRatingTVMA_L = R.id.pl_usa_tv_table_row7_col6;
    
    private int[] blockIDs = {blockTvY_All, blockTvY7_All, blockTvG_All, blockTvPG_All, 
            blockTv14_All, blockTvMA_All, blockRatingTVY7_FV, blockRatingTVPG_V, 
            blockRatingTVPG_S, blockRatingTVPG_L, blockRatingTVPG_D, blockRatingTV14_V, 
            blockRatingTV14_S, blockRatingTV14_L, blockRatingTV14_D, blockRatingTVMA_V, 
            blockRatingTVMA_S, blockRatingTVMA_L};
    private boolean[][] matrix = {{false,false,false,false,false,false},
            {false,false,false,false,false,false},{false,false,false,false,false,false},
            {false,false,false,false,false,false},{false,false,false,false,false,false},
            {false,false,false,false,false,false}};

    View mainView;
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable(){
        public void run (){
            updateMatrixToTvServer();
            updateMatrixFromTvServer();
            updateUI();
        }
    };
    
    OnClickListener clickListener = new OnClickListener() {
        
        @Override
        public void onClick(View arg0) {
            handler.removeCallbacks(runnable);
            if(((TextView)arg0).getText() == "LOCK")
                setLockEnable(arg0.getId(), false);
            else 
                setLockEnable(arg0.getId(), true);
            handler.postDelayed(runnable,1000);
        }
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        mainView = inflater.inflate(R.layout.layout_fragment_atsc_parent_control_usa_tv,container, false);

        mainView.findViewById(blockTvY_All).setOnClickListener(clickListener);
        mainView.findViewById(blockTvY7_All).setOnClickListener(clickListener);
        mainView.findViewById(blockTvG_All).setOnClickListener(clickListener);
        mainView.findViewById(blockTvPG_All).setOnClickListener(clickListener);
        mainView.findViewById(blockTv14_All).setOnClickListener(clickListener);
        mainView.findViewById(blockTvMA_All).setOnClickListener(clickListener);
        
        mainView.findViewById(blockRatingTVY7_FV).setOnClickListener(clickListener);
        
        mainView.findViewById(blockRatingTVPG_V).setOnClickListener(clickListener);
        mainView.findViewById(blockRatingTVPG_S).setOnClickListener(clickListener);
        mainView.findViewById(blockRatingTVPG_L).setOnClickListener(clickListener);
        mainView.findViewById(blockRatingTVPG_D).setOnClickListener(clickListener);
        
        mainView.findViewById(blockRatingTV14_V).setOnClickListener(clickListener);
        mainView.findViewById(blockRatingTV14_S).setOnClickListener(clickListener);
        mainView.findViewById(blockRatingTV14_L).setOnClickListener(clickListener);
        mainView.findViewById(blockRatingTV14_D).setOnClickListener(clickListener);
        
        mainView.findViewById(blockRatingTVMA_V).setOnClickListener(clickListener);
        mainView.findViewById(blockRatingTVMA_S).setOnClickListener(clickListener);
        mainView.findViewById(blockRatingTVMA_L).setOnClickListener(clickListener);
        
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
        
        updateMatrixFromTvServer();
        updateUI();
    }
    
    @Override
    public void onPause() {
        super.onPause();

        handler.removeCallbacks(runnable);
        updateMatrixToTvServer();
    }
    
    private void setLockEnable(int type, boolean enable)
    {
        if(getMatrixEnable(type) == enable)
            return;
        Log.d(TAG, "setLockEnable: "+type);
        ((TextView)mainView.findViewById(type)).setText(enable ? "LOCK" : "");
        setMatrix(type, enable);
        
        if (enable) {
            switch (type) {
            case blockTvY_All:
                setLockEnable(blockTvY7_All, enable);
                break;
            case blockTvY7_All:
                setLockEnable(blockRatingTVY7_FV, enable);
                break;
            case blockTvG_All:
                setLockEnable(blockTvPG_All, enable);
                break;
            case blockTvPG_All:
                setLockEnable(blockRatingTVPG_V, enable);
                setLockEnable(blockRatingTVPG_S, enable);
                setLockEnable(blockRatingTVPG_L, enable);
                setLockEnable(blockRatingTVPG_D, enable);
                setLockEnable(blockTv14_All, enable);
                break;
            case blockTv14_All:
                setLockEnable(blockRatingTV14_V, enable);
                setLockEnable(blockRatingTV14_S, enable);
                setLockEnable(blockRatingTV14_L, enable);
                setLockEnable(blockRatingTV14_D, enable);
                setLockEnable(blockTvMA_All, enable);
                break;
            case blockTvMA_All:
                setLockEnable(blockRatingTVMA_V, enable);
                setLockEnable(blockRatingTVMA_S, enable);
                setLockEnable(blockRatingTVMA_L, enable);
                break;
            case blockRatingTVY7_FV:
                setLockEnable(blockTvY7_All, enable);
                break;
            case blockRatingTVPG_V:
                if(getMatrixEnable(blockRatingTVPG_S) &&
                        getMatrixEnable(blockRatingTVPG_L) &&
                        getMatrixEnable(blockRatingTVPG_D))
                    setLockEnable(blockTvPG_All, enable);
                setLockEnable(blockRatingTV14_V, enable);
                break;
            case blockRatingTVPG_S:
                if(getMatrixEnable(blockRatingTVPG_V) &&
                        getMatrixEnable(blockRatingTVPG_L) &&
                        getMatrixEnable(blockRatingTVPG_D))
                    setLockEnable(blockTvPG_All, enable);
                setLockEnable(blockRatingTV14_S, enable);
                break;
            case blockRatingTVPG_L:
                if(getMatrixEnable(blockRatingTVPG_V) &&
                        getMatrixEnable(blockRatingTVPG_S) &&
                        getMatrixEnable(blockRatingTVPG_D))
                    setLockEnable(blockTvPG_All, enable);
                setLockEnable(blockRatingTV14_L, enable);
                break;
            case blockRatingTVPG_D:
                if(getMatrixEnable(blockRatingTVPG_V) &&
                        getMatrixEnable(blockRatingTVPG_S) &&
                        getMatrixEnable(blockRatingTVPG_L))
                    setLockEnable(blockTvPG_All, enable);
                setLockEnable(blockRatingTV14_D, enable);
                break;
            case blockRatingTV14_V:
                if(getMatrixEnable(blockRatingTV14_S) &&
                        getMatrixEnable(blockRatingTV14_L) &&
                        getMatrixEnable(blockRatingTV14_D))
                    setLockEnable(blockTv14_All, enable);
                setLockEnable(blockRatingTVMA_V, enable);
                break;
            case blockRatingTV14_S:
                if(getMatrixEnable(blockRatingTV14_V) &&
                        getMatrixEnable(blockRatingTV14_L) &&
                        getMatrixEnable(blockRatingTV14_D))
                    setLockEnable(blockTv14_All, enable);
                setLockEnable(blockRatingTVMA_S, enable);
                break;
            case blockRatingTV14_L:
                if(getMatrixEnable(blockRatingTV14_V) &&
                        getMatrixEnable(blockRatingTV14_S) &&
                        getMatrixEnable(blockRatingTV14_D))
                    setLockEnable(blockTv14_All, enable);
                setLockEnable(blockRatingTVMA_L, enable);
                break;
            case blockRatingTV14_D:
                if(getMatrixEnable(blockRatingTV14_V) &&
                        getMatrixEnable(blockRatingTV14_S) &&
                        getMatrixEnable(blockRatingTV14_L))
                    setLockEnable(blockTv14_All, enable);
                break;
            case blockRatingTVMA_V:
                if(getMatrixEnable(blockRatingTVMA_S) &&
                        getMatrixEnable(blockRatingTVMA_L))
                    setLockEnable(blockTvMA_All, enable);
                break;
            case blockRatingTVMA_S:
                if(getMatrixEnable(blockRatingTVMA_V) &&
                        getMatrixEnable(blockRatingTVMA_L))
                    setLockEnable(blockTvMA_All, enable);
                break;
            case blockRatingTVMA_L:
                if(getMatrixEnable(blockRatingTVMA_V) &&
                        getMatrixEnable(blockRatingTVMA_S))
                    setLockEnable(blockTvMA_All, enable);
                break;

            default:
                break;
            }
        }
        else {
            switch (type) {
            case blockTvY_All:
                break;
            case blockTvY7_All:
                setLockEnable(blockTvY_All, enable);
                setLockEnable(blockRatingTVY7_FV, enable);
                break;
            case blockTvG_All:
                break;
            case blockTvPG_All:
                setLockEnable(blockTvG_All, enable);
                if(!(getMatrixEnable(blockRatingTVPG_V) &&
                        getMatrixEnable(blockRatingTVPG_S) &&
                        getMatrixEnable(blockRatingTVPG_L) &&
                        getMatrixEnable(blockRatingTVPG_D)))
                    break;
                setLockEnable(blockRatingTVPG_V, enable);
                setLockEnable(blockRatingTVPG_S, enable);
                setLockEnable(blockRatingTVPG_L, enable);
                setLockEnable(blockRatingTVPG_D, enable);
                break;
            case blockTv14_All:
                if(!(getMatrixEnable(blockRatingTV14_V) &&
                        getMatrixEnable(blockRatingTV14_S) &&
                        getMatrixEnable(blockRatingTV14_L) &&
                        getMatrixEnable(blockRatingTV14_D)))
                    break;
                setLockEnable(blockTvPG_All, enable);
                setLockEnable(blockRatingTV14_V, enable);
                setLockEnable(blockRatingTV14_S, enable);
                setLockEnable(blockRatingTV14_L, enable);
                setLockEnable(blockRatingTV14_D, enable);
                break;
            case blockTvMA_All:
                if(!(getMatrixEnable(blockRatingTVMA_V) &&
                        getMatrixEnable(blockRatingTVMA_S) &&
                        getMatrixEnable(blockRatingTVMA_L)))
                    break;
                setLockEnable(blockTv14_All, enable);
                setLockEnable(blockRatingTVMA_V, enable);
                setLockEnable(blockRatingTVMA_S, enable);
                setLockEnable(blockRatingTVMA_L, enable);
                setLockEnable(blockRatingTV14_D, enable);
                break;
            case blockRatingTVY7_FV:
                setLockEnable(blockTvY7_All, enable);
                break;
            case blockRatingTVPG_V:
                setLockEnable(blockTvPG_All, enable);
                break;
            case blockRatingTVPG_S:
                setLockEnable(blockTvPG_All, enable);
                break;
            case blockRatingTVPG_L:
                setLockEnable(blockTvPG_All, enable);
                break;
            case blockRatingTVPG_D:
                setLockEnable(blockTvPG_All, enable);
                break;
            case blockRatingTV14_V:
                setLockEnable(blockRatingTVPG_V, enable);
                setLockEnable(blockTv14_All, enable);
                break;
            case blockRatingTV14_S:
                setLockEnable(blockRatingTVPG_S, enable);
                setLockEnable(blockTv14_All, enable);
                break;
            case blockRatingTV14_L:
                setLockEnable(blockRatingTVPG_L, enable);
                setLockEnable(blockTv14_All, enable);
                break;
            case blockRatingTV14_D:
                setLockEnable(blockRatingTVPG_D, enable);
                setLockEnable(blockTv14_All, enable);
                break;
            case blockRatingTVMA_V:
                setLockEnable(blockRatingTV14_V, enable);
                setLockEnable(blockTvMA_All, enable);
                break;
            case blockRatingTVMA_S:
                setLockEnable(blockRatingTV14_S, enable);
                setLockEnable(blockTvMA_All, enable);
                break;
            case blockRatingTVMA_L:
                setLockEnable(blockRatingTV14_L, enable);
                setLockEnable(blockTvMA_All, enable);
                break;

            default:
                break;
            }
        }
    }
    
    private void setMatrix(int type, boolean enable){
        switch (type) {
            case blockTvY_All:
                matrix[0][0] = enable;
                break;
            case blockTvY7_All:
                matrix[1][0] = enable;
                break;
            case blockTvG_All:
                matrix[2][0] = enable;
                break;
            case blockTvPG_All:
                matrix[3][0] = enable;
                break;
            case blockTv14_All:
                matrix[4][0] = enable;
                break;
            case blockTvMA_All:
                matrix[5][0] = enable;
                break;

            // For TV-Y7(all childen)
            case blockRatingTVY7_FV:
                matrix[1][1] = enable;
                break;
            // For TV-PG(Parental Guidances suggested)
            case blockRatingTVPG_V:
                matrix[3][2] = enable;
                break;
            case blockRatingTVPG_S:
                matrix[3][3] = enable;
                break;
            case blockRatingTVPG_L:
                matrix[3][4] = enable;
                break;
            case blockRatingTVPG_D:
                matrix[3][5] = enable;
                break;
            // For TV-14(Parental strongly cautioned)
            case blockRatingTV14_V:
                matrix[4][2] = enable;
                break;
            case blockRatingTV14_S:
                matrix[4][3] = enable;
                break;
            case blockRatingTV14_L:
                matrix[4][4] = enable;
                break;
            case blockRatingTV14_D:
                matrix[4][5] = enable;
                break;
            // For TV-MA(Mature audience only)
            case blockRatingTVMA_V:
                matrix[5][2] = enable;
                break;
            case blockRatingTVMA_S:
                matrix[5][3] = enable;
                break;
            case blockRatingTVMA_L:
                matrix[5][4] = enable;
                break;
            default:
                break;
        }
    }
    
    private boolean getMatrixEnable(int type){
        switch (type) {
            case blockTvY_All:
                return matrix[0][0];
            case blockTvY7_All:
                return matrix[1][0];
            case blockTvG_All:
                return matrix[2][0];
            case blockTvPG_All:
                return matrix[3][0];
            case blockTv14_All:
                return matrix[4][0];
            case blockTvMA_All:
                return matrix[5][0];
            // For TV-Y7(all childen)
            case blockRatingTVY7_FV:
                return matrix[1][1];
            // For TV-PG(Parental Guidances suggested)
            case blockRatingTVPG_V:
                return matrix[3][2];
            case blockRatingTVPG_S:
                return matrix[3][3];
            case blockRatingTVPG_L:
                return matrix[3][4];
            case blockRatingTVPG_D:
                return matrix[3][5];
            // For TV-14(Parental strongly cautioned)
            case blockRatingTV14_V:
                return matrix[4][2];
            case blockRatingTV14_S:
                return matrix[4][3];
            case blockRatingTV14_L:
                return matrix[4][4];
            case blockRatingTV14_D:
                return matrix[4][5];
            // For TV-MA(Mature audience only)
            case blockRatingTVMA_V:
                return matrix[5][2];
            case blockRatingTVMA_S:
                return matrix[5][3];
            case blockRatingTVMA_L:
                return matrix[5][4];
            default:
                return false;
        }
    }
    
    private void updateUI()
    {
        int count = blockIDs.length;
        for (int i = 0; i < count; i++) {
            ((TextView)mainView.findViewById(blockIDs[i])).setText(getMatrixEnable(blockIDs[i]) ? "LOCK" : "");
        }
    }
    
    private void updateMatrixFromTvServer()
    {
        /*final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
        VChipRatingConfig config = tm.getVChipRatingConfig();
        
        setMatrix(blockTvY_All, config.blockTvY_All > 0 ? true : false);
        setMatrix(blockTvY7_All, config.blockTvY7_All > 0 ? true : false);
        setMatrix(blockTvG_All, config.blockTvG_All > 0 ? true : false);
        setMatrix(blockTvPG_All, config.blockTvPG_All > 0 ? true : false);
        setMatrix(blockTv14_All, config.blockTv14_All > 0 ? true : false);
        setMatrix(blockTvMA_All, config.blockTvMA_All > 0 ? true : false);
        
        setMatrix(blockRatingTVY7_FV, config.blockRatingTVY7_FV > 0 ? true : false);
        
        setMatrix(blockRatingTVPG_V, config.blockRatingTVPG_V > 0 ? true : false);
        setMatrix(blockRatingTVPG_S, config.blockRatingTVPG_S > 0 ? true : false);
        setMatrix(blockRatingTVPG_L, config.blockRatingTVPG_L > 0 ? true : false);
        setMatrix(blockRatingTVPG_D, config.blockRatingTVPG_D > 0 ? true : false);
        
        setMatrix(blockRatingTV14_V, config.blockRatingTV14_V > 0 ? true : false);
        setMatrix(blockRatingTV14_S, config.blockRatingTV14_S > 0 ? true : false);
        setMatrix(blockRatingTV14_L, config.blockRatingTV14_L > 0 ? true : false);
        setMatrix(blockRatingTV14_D, config.blockRatingTV14_D > 0 ? true : false);
        
        setMatrix(blockRatingTVMA_V, config.blockRatingTVMA_V > 0 ? true : false);
        setMatrix(blockRatingTVMA_S, config.blockRatingTVMA_S > 0 ? true : false);
        setMatrix(blockRatingTVMA_L, config.blockRatingTVMA_L > 0 ? true : false);*/
    }
    
    private void updateMatrixToTvServer()
    {
        /*final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
        VChipRatingConfig config = tm.getVChipRatingConfig();
        
        config.blockTvY_All = getMatrixEnable(blockTvY_All) ? 1 : 0;
        config.blockTvY7_All = getMatrixEnable(blockTvY7_All) ? 1 : 0;
        config.blockTvG_All = getMatrixEnable(blockTvG_All) ? 1 : 0;
        config.blockTvPG_All = getMatrixEnable(blockTvPG_All) ? 1 : 0;
        config.blockTv14_All = getMatrixEnable(blockTv14_All) ? 1 : 0;
        config.blockTvMA_All = getMatrixEnable(blockTvMA_All) ? 1 : 0;
        
        config.blockRatingTVY7_FV = getMatrixEnable(blockRatingTVY7_FV) ? 1 : 0;
        
        config.blockRatingTVPG_V = getMatrixEnable(blockRatingTVPG_V) ? 1 : 0;
        config.blockRatingTVPG_S = getMatrixEnable(blockRatingTVPG_S) ? 1 : 0;
        config.blockRatingTVPG_L = getMatrixEnable(blockRatingTVPG_L) ? 1 : 0;
        config.blockRatingTVPG_D = getMatrixEnable(blockRatingTVPG_D) ? 1 : 0;
        
        config.blockRatingTV14_V = getMatrixEnable(blockRatingTV14_V) ? 1 : 0;
        config.blockRatingTV14_S = getMatrixEnable(blockRatingTV14_S) ? 1 : 0;
        config.blockRatingTV14_L = getMatrixEnable(blockRatingTV14_L) ? 1 : 0;
        config.blockRatingTV14_D = getMatrixEnable(blockRatingTV14_D) ? 1 : 0;
        
        config.blockRatingTVMA_V = getMatrixEnable(blockRatingTVMA_V) ? 1 : 0;
        config.blockRatingTVMA_S = getMatrixEnable(blockRatingTVMA_S) ? 1 : 0;
        config.blockRatingTVMA_L = getMatrixEnable(blockRatingTVMA_L) ? 1 : 0;
        
        tm.setVChipRatingConfig(config);*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
