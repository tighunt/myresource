package com.realtek.camera.app.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.realtek.camera.util.MediaSaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * TODO:
 * Fix screen orientation
 * @author shangcheih@realtek.com
 *
 */
public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    protected static final boolean DEBUG = true;

    public static final int MODE_CAMERA = 0;
    public static final int MODE_RECORDER = 1;
    
    // Note that opening camera and start/stop record are put in background thread.
    private static final int STAT_NO_CAMERA = 0;
    private static final int STAT_CAMERA_OPENING = 1;
    private static final int STAT_CAMERA_OPENED = 2;
    
    private static final int STAT_RECORD_STARTING = 4;
    private static final int STAT_RECORDING = 5;
    private static final int STAT_RECORD_STOPPING = 6;
    
    // Message for background handler
    private static final int MSG_OPEN_CAMERA = 0;
    private static final int MSG_START_RECORD = 1;
    private static final int MSG_STOP_RECORD = 2;

    // Message for main thread handler
    private static final int MSG_STAT_CHANGED = 0;
    private static final int MSG_RECORD_FINISHED = 1;

    // TODO: Refine the listener interface
    public interface CameraFragmentListener {
        public void onCameraOpened(CameraFragment cameraFragment);

        public void onCameraOpenFailed(CameraFragment cameraFragment);

        public void onCameraRelease();

        public void onCameraStartPreview();

        public void onCameraStartRecord();

        public void onRecordFinished(File outputFile, Exception exception);

        public void onImageFileSaved(String uri, byte[] data, Exception exception);

        public void onModeChanged(int mode);
    }

    public static class Size {
        public final int width;
        public final int height;
        
        private Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public String toString() {
            return String.format(Locale.US, "width = %d, height = %d",  width, height);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Size) {
                Size s = (Size) o;
                return s.width == width && s.height == height;
            }
            return false;
        }
        
    }
    
    private static class RecordResult {
        public final File file;
        public final Exception exception;
        
        public RecordResult(File f, Exception e) {
            file = f;
            exception = e;
        }
    }

    private int mMode = MODE_CAMERA;
    
    // View
    private SurfaceView mSurface;
    private SurfaceHolder mSurfaceHolder;
    
    // Camera
    private Camera mCamera;
    private int mCameraId = -1;
    private List<Camera.Size> mSupportedPreviewSizes;
    private List<Size> mSupportedPictureSizes;
    private List<Size> mSupportedRecordSizes;
    private Size mDesiredPictureSize;
    private Size mDesiredRecordSize;
    
    // Status
    private volatile boolean mIsDisplayPreview = false;
    private Size mCurrentPreviewSize;
    private final Object[] mStatLock = new Object[0];
    private int mStat;
    private volatile boolean mReleaseCalled;
    
    // Video
    private MediaRecorder mMediaRecorder;
    private File mVideOutput;
    
    // Callback
    private CameraFragmentListener mListener;
    
    // Thread
    private Looper mLooper;
    private Handler mHandler;
    
    private Handler mMainHandler;

    private class MainHandler extends Handler {

        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STAT_CHANGED:
                    onStateChanged(msg.arg1, msg.arg2);
                    break;
                case MSG_RECORD_FINISHED:
                    RecordResult result = (RecordResult) msg.obj;
                    if (mListener != null) {
                        mListener.onRecordFinished(result.file, result.exception);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_OPEN_CAMERA:
                    if(openCameraInBackground()) {
                        setStat(STAT_CAMERA_OPENED);
                    } else {
                        setStat(STAT_NO_CAMERA);
                    }
                    break;
                case MSG_START_RECORD:
                    startRecordInBackground();
                    if (mReleaseCalled) {
                        releaseCamera();
                    }
                    break;
                case MSG_STOP_RECORD:
                    stopRecordInBackground();
                    if (mReleaseCalled) {
                        releaseCamera();
                    }
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CameraFragmentListener) {
            mListener = (CameraFragmentListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        HandlerThread t = new HandlerThread("CameraThread");
        t.start();
        mLooper = t.getLooper();
        mHandler = new MyHandler(mLooper);
        mMainHandler = new MainHandler(Looper.getMainLooper());
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mLooper.quit();
        mLooper = null;
        mHandler = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = mSurface = new CameraSurface(inflater.getContext());
        mSurfaceHolder = mSurface.getHolder();
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    // ======== State operations
    private void setStat(int nextStat) {
        synchronized(mStatLock) {
            if (mStat != nextStat) {
                mMainHandler.obtainMessage(MSG_STAT_CHANGED, mStat, nextStat).sendToTarget();
                mStat = nextStat;
            }
        }
    }
    
    private boolean setStatIf(int nextStat, int... currentStat) {
        boolean set = false;
        synchronized(mStatLock) {
            for (int s : currentStat) {
                if (mStat == s) {
                    set = true;
                    if (mStat != nextStat) {
                        mMainHandler.obtainMessage(MSG_STAT_CHANGED, mStat, nextStat).sendToTarget();
                        mStat = nextStat;
                    }
                    break;
                }
            }
        }
        return set;
    }
    
    private boolean checkStat(int... currentStat) {
        synchronized(mStatLock) {
            for (int s : currentStat) {
                if (mStat == s) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void assertStat(int stat) {
        synchronized(mStatLock) {
            assert(mStat == stat);
        }
    }
    
    /**
     * This is executed in main thread.
     * @param prev
     * @param curr
     */
    private void onStateChanged(int prev, int curr) {
        // Camera opened
        if (prev == STAT_CAMERA_OPENING && curr == STAT_CAMERA_OPENED) {
            // Fragment Callback
            if (mListener != null) {
                mListener.onCameraOpened(this);
            }
            startCameraPreview();

        // Camera open failed
        } else if (prev == STAT_CAMERA_OPENING && curr == STAT_NO_CAMERA) {
            if (mListener != null) {
                mListener.onCameraOpenFailed(this);
            }

        // Start record
        } else if (prev == STAT_RECORD_STARTING && curr == STAT_RECORDING) {
            if (mListener != null) {
                mListener.onCameraStartRecord();
            }

        // Start record failed
        } else if (prev == STAT_RECORD_STARTING && curr == STAT_CAMERA_OPENED) {
            // Do nothing. It's handled in stopRecordInBackground.

        // Stop record
        } else if (prev == STAT_RECORD_STOPPING && curr == STAT_CAMERA_OPENED) {
            // Do nothing. It's handled in stopRecordInBackground.

        // Released
        } else if (curr == STAT_NO_CAMERA) {
            if (mListener != null) {
                mListener.onCameraRelease();
            }
        }
    }
    // ========

    /**
     * Open camera asynchronously.
     */
    private void openCamera() {
        if (!setStatIf(STAT_CAMERA_OPENING, STAT_NO_CAMERA)) {
            return;
        }
        
        mHandler.sendEmptyMessage(MSG_OPEN_CAMERA);
    }

    private boolean openCameraInBackground() {
        assertStat(STAT_CAMERA_OPENING);

        if (DEBUG) {
            Log.v(TAG, "Opening camera");
        }
        
        int cameraCount = Camera.getNumberOfCameras();
        if (cameraCount < 0) {
            return false;
        }
        
        // Open
        try {
            mCameraId = 0;
            mCamera = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
            mCamera = null;
            mCameraId = -1;
            return false;
        }
        
        // Set parameter & Callback
        Parameters param = mCamera.getParameters();
        param.setPictureFormat(ImageFormat.JPEG);
        param.setJpegQuality(90);
        mSupportedPreviewSizes = param.getSupportedPreviewSizes();
        
        // Picture Size
        Camera.Size pictureSize = param.getPictureSize();
        mSupportedPictureSizes = new ArrayList<CameraFragment.Size>();
        for (Camera.Size s : param.getSupportedPictureSizes()) {
            mSupportedPictureSizes.add(new Size(s.width, s.height));
        }
        mDesiredPictureSize = new Size(pictureSize.width, pictureSize.height);
        
        // Video Size
        mSupportedRecordSizes = getAvailableVideoSize(mCamera);
        mDesiredRecordSize = mSupportedRecordSizes.get(0);
        
        return true;
    }

    private void startCameraPreview() {
        if (!isCameraOpened() || mIsDisplayPreview) {
            return;
        }

        // Get Preview Size
        mCurrentPreviewSize = getSuggestedPreviewSize();
        
        mCamera.getParameters().setPreviewSize(mCurrentPreviewSize.width, mCurrentPreviewSize.height);
        Log.v(TAG, "setPreviewSize:" + mCurrentPreviewSize.toString());

        // Set Preview Display
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (DEBUG) {
            Log.v(TAG, "Starting preview");
        }
        // Start Preview
        mCamera.setOneShotPreviewCallback(mOnPreviewStart);
        mCamera.startPreview();
    }

    /**
     * Get preview size based on current mode and resolution settings.
     * @return
     */
    private Size getSuggestedPreviewSize() {
        // TODO: Refine the selection policy.
        if (mMode == MODE_CAMERA) {
            return getOptimalPreviewSize(mSupportedPreviewSizes, mDesiredPictureSize.width, mDesiredPictureSize.height);
        } else {
            return getOptimalPreviewSize(mSupportedPreviewSizes, mDesiredRecordSize.width, mDesiredRecordSize.height);
        }
    }
    
    private void stopCameraPreview() {
        if (isRecordingVideo()) {
            throw new IllegalStateException("Cannot stop preview during recording.");
        }
        
        if (!isCameraOpened()) {
            return;
        }
        
        mCamera.stopPreview();
        mIsDisplayPreview = false;
        mCurrentPreviewSize = null;
    }
    
    private void restartCameraPreview() {
        if (!isCameraOpened()) {
            return;
        }
        
        if (mIsDisplayPreview) {
            Size newSize = getSuggestedPreviewSize();
            if (!newSize.equals(mCurrentPreviewSize)) {
                stopCameraPreview();
            }
        }
        
        startCameraPreview();
    }
    
    private void releaseCamera() {
        synchronized (mStatLock) {
            // Busy: Release later
            if (mStat == STAT_CAMERA_OPENING ||
                mStat == STAT_RECORD_STARTING ||
                mStat == STAT_RECORD_STOPPING) {
                mReleaseCalled = true;
                if (DEBUG) {
                    Log.w(TAG, "releaseCamera: Camera busy; release later.");
                }
                return;
            } else if (mStat == STAT_RECORDING) {
                if (DEBUG) {
                    Log.w(TAG, "releaseCamera: Camera recording; release later.");
                }
                stopRecordVideo();
                mReleaseCalled = true;
                return;
                
            // Not opened
            } else if (mStat == STAT_NO_CAMERA) {
                return;
            } 

            if (DEBUG) {
                Log.v(TAG, "releaseCamera");
            }

            // Stop Preview
            stopCameraPreview();

            // Release camera
            mCamera.release();
            mCamera = null;
            mCameraId = -1;
            mSupportedPreviewSizes = null;
            mSupportedPictureSizes = null;
            mSupportedRecordSizes = null;
            mDesiredPictureSize = null;
            mDesiredRecordSize = null;

            // Notify callback
            mMainHandler.obtainMessage(MSG_STAT_CHANGED, mStat, STAT_NO_CAMERA).sendToTarget();
            
            mStat = STAT_NO_CAMERA;
            mReleaseCalled = false;
        }

    }

    // ====== public operations ======
    public void setCurrentMode(int mode) {
        if (mMode == mode) {
            return;
        }
        
        mMode = mode;
        mSurface.requestLayout();
        restartCameraPreview();
        
        // Callback
        if (mListener != null) {
            mListener.onModeChanged(mode);
        }
    }
    
    public int getCurrentMode() {
        return mMode;
    }
    
    public void takePicture() {
        if (!checkStat(STAT_CAMERA_OPENED, STAT_RECORDING) && mIsDisplayPreview) {
            return;
        }

        mIsDisplayPreview = false;
        mCamera.takePicture(mShutterCallback, mRawCallback, mPostCallback, mJpegCallback);
    }

    public void startRecordVideo() {
        if (!setStatIf(STAT_RECORD_STARTING, STAT_CAMERA_OPENED)) {
            return;
        }
        
        mHandler.sendEmptyMessage(MSG_START_RECORD);
    }
        
    private void startRecordInBackground() {

        try {
            // Unlock camera
            // Note: Starting with Android 4.0 (API level 14),
            // the Camera.lock() and Camera.unlock() calls are managed for you automatically
            mCamera.unlock();

            // Initialize MediaRecorder
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            CamcorderProfile profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_HIGH);
            profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
            profile.videoCodec = MediaRecorder.VideoEncoder.MPEG_4_SP;
            profile.videoFrameWidth = mDesiredRecordSize.width;
            profile.videoFrameHeight = mDesiredRecordSize.height;
            if (DEBUG) {
                Log.d(TAG, String.format("StartRecord: frameRate=%d, width=%d, height=%d", profile.videoFrameRate, profile.videoFrameWidth, profile.videoFrameHeight));
            }
            mMediaRecorder.setProfile(profile);

            mVideOutput = MediaSaver.getVideoOutputFile();
            mMediaRecorder.setOutputFile(mVideOutput.getAbsolutePath());

            mMediaRecorder.prepare();

            mMediaRecorder.start();

            setStat(STAT_RECORDING);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start recording: " + e);
            e.printStackTrace();
            mMediaRecorder = null;
            mVideOutput = null;
            setStat(STAT_CAMERA_OPENED);
        }

    }

    public void stopRecordVideo() {
        if (!setStatIf(STAT_RECORD_STOPPING, STAT_RECORDING)) {
            return;
        }
        
        mHandler.sendEmptyMessage(MSG_STOP_RECORD);
    }

    private void stopRecordInBackground() {
        assertStat(STAT_RECORD_STOPPING);
        
        // Stop recorder
        // Note that a RuntimeException is intentionally thrown to the
        // application, if no valid audio/video data has been received when stop() is called.
        Exception exception = null;
        try {
            mMediaRecorder.stop();
        } catch (Exception e) {
            // Record failed.
            if (mVideOutput.exists()) {
                mVideOutput.delete();
            }
            
            //Prepare arguments for callback.
            exception = e;
            mVideOutput = null;
            Log.e(TAG, "Faild to stop recording: " + e);
            
        } finally {
            mMediaRecorder.release();
        }

        // Insert video file if succeed
        if (exception == null) {
            MediaSaver.insertVideoFile(getActivity(), mVideOutput);
        }

        // Callback
        RecordResult result = new RecordResult(mVideOutput, exception);
        mMainHandler.obtainMessage(MSG_RECORD_FINISHED, result).sendToTarget();
        
        mMediaRecorder = null;
        mVideOutput = null;
        mCamera.startPreview();
        setStat(STAT_CAMERA_OPENED);
    }

    public boolean isRecordingVideo() {
        synchronized(mStatLock) {
            return mStat == STAT_RECORDING;
        }
    }

    public boolean isCameraOpened() {
        synchronized (mStatLock) {
            return mStat != STAT_NO_CAMERA && mStat != STAT_CAMERA_OPENING;
        }
    }

    public boolean isDisplayPreview() {
        return mIsDisplayPreview;
    }
    
    public void setPictureSize(int width, int height) {
        mDesiredPictureSize = new Size(width, height);
        
        Parameters param = mCamera.getParameters();
        param.setPictureSize(width, height);
        mCamera.setParameters(param);
        
        mSurface.requestLayout();
        restartCameraPreview();
    }
    
    public void setVideoSize(int width, int height) {
        mDesiredRecordSize = new Size(width, height);
        
        mSurface.requestLayout();
        restartCameraPreview();
    }

    public List<Size> getSupportedPictureSizes() {
        return mSupportedPictureSizes;
    }
    
    public List<Size> getSupportedRecordSizes() {
        return mSupportedRecordSizes;
    }
    
    public int getPictureSizeIndex() {
        if (!isCameraOpened()) {
            return -1;
        }
        
        return mSupportedPictureSizes.indexOf(mDesiredPictureSize);
    }
    
    public int getRecordSizeIndex() {
        if (!isCameraOpened()) {
            return -1;
        }
        
        return mSupportedRecordSizes.indexOf(mDesiredRecordSize);
    }
    
    // ==============================
    private Camera.PreviewCallback mOnPreviewStart = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (!isCameraOpened()) {
                return;
            }
            
            mIsDisplayPreview = true;
            if (mListener != null) {
                mListener.onCameraStartPreview();
            }
            if (DEBUG) {
                Camera.Size size = camera.getParameters().getPreviewSize();
                Log.v(TAG, String.format("onPreviewStart: w=%d, h=%d" , size.width, size.height));
            }
        }
    };

    /**
     * The shutter callback occurs after the image is captured.
     * This can be used to trigger a sound to let the user know that image has been captured. 
     */
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        
        @Override
        public void onShutter() {
        }
    };
    
    /**
     * The raw callback occurs when the raw image data is available.
     * (NOTE: the data will be null if there is no raw image callback buffer available
     * or the raw image callback buffer is not large enough to hold the raw image).
     */
    private Camera.PictureCallback mRawCallback = new Camera.PictureCallback() {
        
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };
    
    /**
     * The postview callback occurs when a scaled, fully processed postview image is available
     * (NOTE: not all hardware supports this). 
     */
    private Camera.PictureCallback mPostCallback = new Camera.PictureCallback() {
        
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };
    
    /**
     * The jpeg callback occurs when the compressed image is available.
     * If the application does not need a particular callback, a null can be passed instead of a callback method.
     */
    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // Start Preview
            camera.setOneShotPreviewCallback(mOnPreviewStart);
            camera.startPreview();
            mIsDisplayPreview = true;
            
            // Create title/filename
            String title = "TV Camera";
            String description = "TV captured image";
            
            // Save image
            MediaSaver.getInstance(getActivity()).saveImage(data, title, description, mOnImageSaved);
            
        }
    };
    
    private MediaSaver.OnImageSavedListener mOnImageSaved = new MediaSaver.OnImageSavedListener() {

        @Override
        public void onImageSaved(byte[] data, String uri, Exception exception) {
            if (mListener != null) {
                mListener.onImageFileSaved(uri, data, exception);
            }
        }
    };

    private class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {

        public CameraSurface(Context context) {
            super(context);
            getHolder().addCallback(this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (isCameraOpened()) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                
                Size size = getSuggestedPreviewSize();
                float ratio = (float) size.width / (float) size.height;
                
                int targetWidth = (int) ((float) height * ratio);
                if (targetWidth >= width) {
                    height = (int) ((float )width / ratio);
                } else {
                    width = targetWidth;
                }
                setMeasuredDimension(width, height);
                if (DEBUG) {
                    Log.v(TAG, "onMeasureSurface: previewSize: " + size.toString());
                }
            } else {
                if (DEBUG) {
                    Log.v(TAG, "onMeasureSurface");
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (DEBUG) {
                Log.v(TAG, "surfaceDestroyed");
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (DEBUG) {
                Log.v(TAG, "surfaceCreated");
            }
            startCameraPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (DEBUG) {
                Log.v(TAG, String.format("onSurfaceChanged: w=%d, h=%d", width, height));
            }
            
            restartCameraPreview();
        }
    }

    private static Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        double targetRatio = (double) w / h;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        boolean swap = false;
        double minDiff = Double.MAX_VALUE;
        double minDiffRatio = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            double diffRatio;
            if ((diffRatio = Math.abs(ratio - targetRatio)) <= minDiffRatio) {
                if (diffRatio != minDiffRatio || Math.abs(size.height - targetHeight) < minDiff) {
                    minDiffRatio = diffRatio;
                    swap = false;
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            } else if ((diffRatio = Math.abs(1/ratio - targetRatio)) <= minDiffRatio) {
                if (diffRatio != minDiffRatio || Math.abs(size.width - targetHeight) < minDiff) {
                    minDiffRatio = diffRatio;
                    swap = true;
                    optimalSize = size;
                    minDiff = Math.abs(size.width - targetHeight);
                }
            }
        }
        
        if (swap) {
            return new Size(optimalSize.height, optimalSize.width);
        }
        return new Size(optimalSize.width, optimalSize.height);
    }

    private static List<Size> getAvailableVideoSize(Camera camera) {
        List<Size> list = new ArrayList<Size>();
        List<Camera.Size> listVideoSizes = camera.getParameters().getSupportedVideoSizes();
        
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        for (Camera.Size s : listVideoSizes) {
            if (s.width <= profile.videoFrameWidth && s.height <= profile.videoFrameHeight) {
                list.add(new Size(s.width, s.height));
            }
        }
        
        if (list.isEmpty()) {
            list.add(new Size(profile.videoFrameWidth, profile.videoFrameHeight));
        }
        return list;
    }
}
