package com.rtk.tv.fragment.tvsetup;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.BaseFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AtscAutoTuningFragment extends BaseFragment {
    
    private static final String TAG = "com.rtk.tv-AtscAutoTuningFragment";

    private TvManagerHelper tm;

    private TextView mTextProgress;
    private TextView mTextFound;
    private TextView mTextFrequency;
    private ProgressBar mProgress;
    private ProgressBar mProgressSpin;

    private boolean mScanFinished = false;
/*
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE.equals(action)) {
                int msg = intent.getIntExtra(TvBroadcastDefs.EXTRA_TV_MEDIA_MESSAGE, 0);
                switch (msg) {
                    case TvManager.TV_MEDIA_MSG_SCAN_AUTO_COMPLETE:
                        mScanFinished = true;
                    case TvManager.TV_MEDIA_MSG_SCAN_FREQ_UPDATE:
                        updateView(
                                intent.getIntExtra(TvManager.EXTRA_PROGRESS, 0),
                                intent.getIntExtra(TvManager.EXTRA_CHANNEL_COUNT, 0),
                                intent.getIntExtra(TvManager.EXTRA_CHANNEL_FREQUENCY, 0));
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private IntentFilter mIntentFilter = new IntentFilter(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        tm = TvManagerHelper.getInstance(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_auto_tuning, container, false);
        mTextFound = (TextView) view.findViewById(R.id.text_found);
        mTextProgress = (TextView) view.findViewById(R.id.text_progress);
        mTextFrequency = (TextView) view.findViewById(R.id.text_frequency);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        mProgressSpin = (ProgressBar) view.findViewById(R.id.progress_spin);
        updateView(0, 0, 0);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*if (mTvManager.isTvScanning()) {
            mTvManager.tvAutoScanStop();
        }
        mTvManager.tvAutoScanStart(false);*/
        updateView(0, 0, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        //mTvManager.tvAutoScanComplete();
    }

    @Override
    public void onResume() {
        super.onResume();
        //getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private void updateView(int progress, int found, int freq) {
        final boolean isScanning = false;/*mTvManager.isTvScanning()*/;

        mProgressSpin.setVisibility(isScanning ? View.VISIBLE : View.INVISIBLE);
        mTextFound.setText(String.valueOf(found));
        mTextProgress.setText(progress + "%");
        mTextFrequency.setText(getString(R.string.format_frequency_mhz, freq / 1000000F));
        mProgress.setProgress(progress);

        if (mScanFinished) {
            finishScan();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishScan();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void finishScan() {
        /*FragmentManager fm = getFragmentManager();
        FragmentUtils.popAllBackStacks(fm);
        fm.beginTransaction().add(getId(), new AtscAutoTuningResultFragment(), Tv_strategy.STACK_MENU)
        .addToBackStack(Tv_strategy.STACK_MENU)
        .commit();*/
    }
}
