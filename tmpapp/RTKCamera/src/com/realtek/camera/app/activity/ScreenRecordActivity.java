package com.realtek.camera.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.realtek.camera.R;
import com.realtek.camera.ScreenRecordService;
import com.realtek.camera.ScreenRecordService.ScreenRecordListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScreenRecordActivity extends Activity implements ScreenRecordListener, OnClickListener, OnCheckedChangeListener {

    private Button mButtonStart;
    private TextView mTextFile;
    private Chronometer mChronometer;
    private ToggleButton mToggleNotification;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenrecord);
        mButtonStart = (Button) findViewById(R.id.button_start_stop);
        mTextFile = (TextView) findViewById(R.id.text_file);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mToggleNotification = (ToggleButton) findViewById(R.id.toggle_notification);
        
        mButtonStart.setOnClickListener(this);
        mToggleNotification.setOnCheckedChangeListener(this);
        mToggleNotification.setChecked(true);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        ScreenRecordService.getInstance(this).registerCallback(this, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScreenRecordService.getInstance(this).unregisterCallback(this);
    }
    
    @Override
    public void onClick(View v) {
        ScreenRecordService record = ScreenRecordService.getInstance(this);
        if (record.isRecording()) {
            record.stopRecording();
        } else {
            File outFile = getMediaOutputFile("REC_");
            record.startRecording(outFile, 1280, 720, -1);
        }
    }
    
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
         ScreenRecordService.getInstance(this).setNotificationEnabled(isChecked);
    }

    @Override
    public void onStartRecord(File outFile, int width, int height, int timeSec, long startTime, long endTime, boolean hasStopped) {
        mTextFile.setText(outFile.getAbsolutePath());
        mButtonStart.setText(R.string.stop);
        mChronometer.setBase(startTime + (SystemClock.elapsedRealtime() - System.currentTimeMillis()));
        mChronometer.start();
    }

    @Override
    public void onStopRecord(File outFile, int width, int height, int timeSec, long startTime, long endTime) {
        mButtonStart.setText(R.string.start);
        mChronometer.stop();
    }

    private static final SimpleDateFormat FILE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
    
    private static File getMediaOutputFile(String prefix) {
        // Get out directory
        File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        outDir = new File(outDir, "screenrecord");
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        
        // create file name
        String fileName = FILE_FORMAT.format(Calendar.getInstance().getTime());
        
        // Create target file
        File file = new File(outDir, prefix + fileName + ".mp4");
        int copy = 0;
        while(file.exists()) {
            file = new File(outDir, String.format("%s%s (%d).mp4", prefix, fileName, ++copy));
        }
        
        return file;
    }

}
