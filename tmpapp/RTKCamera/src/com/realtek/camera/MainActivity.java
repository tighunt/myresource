
package com.realtek.camera;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.realtek.camera.app.activity.GalleryActivity;
import com.realtek.camera.app.activity.ScreenRecordActivity;
import com.realtek.camera.app.fragment.CameraFragment;
import com.realtek.camera.app.fragment.CameraFragment.CameraFragmentListener;
import com.realtek.camera.app.fragment.CameraFragment.Size;
import com.realtek.camera.util.ImageLoader;
import com.realtek.camera.util.MediaSaver;
import com.realtek.camera.widget.ImageThumbButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements OnClickListener, CameraFragmentListener {

    private static class SizeDeligate {
        public final Size size;

        public SizeDeligate(Size size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "%dx%d", size.width, size.height);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof SizeDeligate) {
                return ((SizeDeligate) o).size.equals(size);
            }
            return false;
        }

    }
	
	private CameraFragment mFragmentCamera;
	private final List<SizeDeligate> mListPictureSizes = new ArrayList<MainActivity.SizeDeligate>();
	private final List<SizeDeligate> mListVideoSizes = new ArrayList<MainActivity.SizeDeligate>();
	
	// View
	private View mButtonShutter;
	private ImageView mButtonRecord;
	private ImageView mButtonSwitch;
	private View mIndicatorRecording;
	private Spinner mSpinnerSize;
	private ArrayAdapter<SizeDeligate> mAdapterSize;
	private ImageThumbButton mImageThumb;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Button shot
        mButtonShutter = findViewById(R.id.button_shutter);
        mButtonShutter.setOnClickListener(this);
        mButtonRecord = (ImageView) findViewById(R.id.button_record);
        mButtonRecord.setOnClickListener(this);
        
        // Button Switch
        mButtonSwitch = (ImageView) findViewById(R.id.button_switch);
        mButtonSwitch.setOnClickListener(this);
        
        // Thumb image
        mImageThumb = (ImageThumbButton) findViewById(R.id.image_thumb);
        mImageThumb.setOnClickListener(this);
        
        // Indicator
        mIndicatorRecording = findViewById(R.id.indicator_recording);
        
        // Size spinner
        mSpinnerSize = (Spinner) findViewById(R.id.spinner_size);
        mAdapterSize = new ArrayAdapter<SizeDeligate>(this, android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSize.setAdapter(mAdapterSize);
        mSpinnerSize.setOnItemSelectedListener(mOnSizeSelected);
        
        // Fragments
        FragmentManager fm = getFragmentManager();
        mFragmentCamera = (CameraFragment) fm.findFragmentById(R.id.fragment_camera);

        // 
        findViewById(R.id.button_screen_record).setOnClickListener(this);
        refreshUI();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Update thumb image.
        // This may be invoked when user is returned from gallery.
        String stringUri = MediaSaver.getLastSavedUri(this);
        String mime = MediaSaver.getLastSavedMime(this);
        if (stringUri != null && mime != null) {
            Uri uri = Uri.parse(stringUri);
            if (mime.startsWith("image")) {
                ImageLoader.getInstance().loadImage(mImageThumb, uri);
            } else if (mime.startsWith("video")) {
                ImageLoader.getInstance().loadVideoThumbnail(mImageThumb, uri);
            }
        }
    }

    /**
     * Refresh UI components by the status of camera.
     */
    private void refreshUI() {
        boolean isCameraOpened = mFragmentCamera.isCameraOpened();
        boolean isRecording = mFragmentCamera.isRecordingVideo();

        mButtonShutter.setEnabled(isCameraOpened);
        mSpinnerSize.setEnabled(isCameraOpened);
        mButtonRecord.setEnabled(isCameraOpened);

        mIndicatorRecording.setVisibility(isRecording ? View.VISIBLE : View.INVISIBLE);
        mButtonRecord.setImageResource(isRecording ? R.drawable.btn_shutter_video_recording
                : R.drawable.btn_shutter_video);
        
        // Refresh Size List & Switch
        mButtonSwitch.setVisibility(isRecording ? View.INVISIBLE : View.VISIBLE);
        mSpinnerSize.setEnabled(!isRecording);
        
        int mode = mFragmentCamera.getCurrentMode();
        mAdapterSize.clear();
        int idx = -1;
        switch (mode) {
            case CameraFragment.MODE_RECORDER:
                mButtonRecord.setVisibility(View.VISIBLE);
                mButtonSwitch.setImageResource(R.drawable.ic_switch_video);
                mAdapterSize.addAll(mListVideoSizes);
                idx = mFragmentCamera.getRecordSizeIndex();
                break;
            default:
            case CameraFragment.MODE_CAMERA:
                mButtonRecord.setVisibility(View.INVISIBLE);
                mButtonSwitch.setImageResource(R.drawable.ic_switch_camera);
                mAdapterSize.addAll(mListPictureSizes);
                idx = mFragmentCamera.getPictureSizeIndex();
                break;
        }
        mAdapterSize.notifyDataSetChanged();
        mSpinnerSize.setSelection(idx);
    }
    
    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.button_shutter:
                // Take picture!
                mFragmentCamera.takePicture();
                break;
            case R.id.button_record:
                // Start/Stop capture video
                if (mFragmentCamera.isRecordingVideo()) {
                    mFragmentCamera.stopRecordVideo();
                } else {
                    mFragmentCamera.startRecordVideo();
                }
                break;
            case R.id.button_switch:
                int mode = mFragmentCamera.getCurrentMode();
                if (mode == CameraFragment.MODE_CAMERA) {
                    mFragmentCamera.setCurrentMode(CameraFragment.MODE_RECORDER);
                } else {
                    mFragmentCamera.setCurrentMode(CameraFragment.MODE_CAMERA);
                }
                break;
            case R.id.image_thumb: {
                // Launch gallery
                Intent intent = new Intent(this, GalleryActivity.class);
                String uri = MediaSaver.getLastSavedUri(this);
                if (uri != null) {
                    intent.setData(Uri.parse(uri));
                }
                startActivity(intent);
            }
                break;
            case R.id.button_screen_record: {
                Intent intent = new Intent(this, ScreenRecordActivity.class);
                startActivity(intent);
            }
                break;
            default:
                break;
        }
    }

    private OnItemSelectedListener mOnSizeSelected = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        	Size size = mAdapterSize.getItem(position).size;
        	int mode = mFragmentCamera.getCurrentMode();
        	if (mode == CameraFragment.MODE_RECORDER) {
        	    mFragmentCamera.setVideoSize(size.width, size.height);
        	} else {
        	    mFragmentCamera.setPictureSize(size.width, size.height);
        	}
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

    // ========== Camera callbacks ==========
    @Override
    public void onCameraOpened(CameraFragment cameraFragment) {
        // Get Picture Sizes
        List<Size> supportSizes = cameraFragment.getSupportedPictureSizes();
        mListPictureSizes.clear();
        for (Size s : supportSizes) {
            mListPictureSizes.add(new SizeDeligate(s));
        }

        // Get Video sizes
        supportSizes = cameraFragment.getSupportedRecordSizes();
        mListVideoSizes.clear();
        for (Size s : supportSizes) {
            mListVideoSizes.add(new SizeDeligate(s));
        }
        
        refreshUI();
    }

    @Override
    public void onCameraStartPreview() {

    }

    @Override
    public void onCameraRelease() {
        refreshUI();
    }

    @Override
    public void onCameraStartRecord() {
        refreshUI();
    }

    @Override
    public void onRecordFinished(File videoOutput, Exception exception) {
        refreshUI();
        if (exception == null) {
            ImageLoader.getInstance().loadVideoThumbnail(
                    mImageThumb, Uri.fromFile(videoOutput), mImageLoaderCallback);
        } else {
//            Toast.makeText(this, R.string.msg_video_recording_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onImageFileSaved(String uri, byte[] data, Exception exception) {
        if (exception == null) {
            ImageLoader.getInstance().loadImage(mImageThumb, Uri.parse(uri), mImageLoaderCallback);
            refreshUI();
        } else {
            exception.printStackTrace();
            String msg = getString(R.string.msg_failed_to_capture_image);
            msg += ": " + exception.getLocalizedMessage();
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private ImageLoader.Callback mImageLoaderCallback = new ImageLoader.Callback() {

        @Override
        public void setImageViewBitmap(ImageView imageView, Bitmap bitmap, boolean immediate) {
            ((ImageThumbButton) imageView).updateImageBitmap(bitmap);
        }
    };

    @Override
    public void onModeChanged(int mode) {
        refreshUI();
    }

    @Override
    public void onCameraOpenFailed(CameraFragment cameraFragment) {
        Toast.makeText(this, R.string.msg_cannot_open_camera, Toast.LENGTH_SHORT).show();
    }

}
