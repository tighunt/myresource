
package com.realtek.camera.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MediaSaver {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static MediaSaver sInstance;

    private static final int MAX_THUMB_SIZE = 128 * 128;/* Pixels */

    private static final int MSG_SAVE_PIC = 0;

    private static final String TAG = "ImageSaver";

    private static final String PREF_NAME = "image_saver";

    private static final SimpleDateFormat FILE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss",
            Locale.US);

    public static MediaSaver getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MediaSaver(context);
        }
        return sInstance;
    }

    public interface OnImageSavedListener {
        public void onImageSaved(byte[] data, String uri, Exception exception);
    }

    private static class SaveTask {
        public byte[] jpeg;
        public String title;
        public String description;
        public OnImageSavedListener listener;
        public Exception exception;

        public String uri;
    }

    private final Context mContext;
    private final HandlerThread mThread;
    private final Handler mHandler;

    private MediaSaver(Context context) {
        // Use application to avoid memory leaks
        mContext = context.getApplicationContext();

        // Initialize a looper thread.
        mThread = new HandlerThread("PictureSaver");
        mThread.start();

        // Create Handler
        mHandler = new MyHandler(mThread.getLooper());

    }

    private class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SAVE_PIC:
                    SaveTask task = (SaveTask) msg.obj;
                    // Save image
                    try {
                        task.uri = saveImage(mContext, task.jpeg, task.title, task.description);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to save image: " + e);
                        task.exception = e;
                    }

                    // Notify callback
                    Message m = mCallbackHandler.obtainMessage();
                    m.copyFrom(msg);
                    mCallbackHandler.sendMessage(m);
                    break;
                default:
                    break;
            }
        }

    }

    private Handler mCallbackHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SAVE_PIC:
                    SaveTask task = (SaveTask) msg.obj;
                    if (task.listener != null) {
                        task.listener.onImageSaved(task.jpeg, task.uri, task.exception);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private SaveTask obtainTaskInstance() {
        return new SaveTask();
    }

    // ========

    public void saveImage(byte[] jpeg, String title, String description,
            OnImageSavedListener listener) {
        SaveTask task = obtainTaskInstance();
        task.jpeg = jpeg;
        task.title = title;
        task.description = description;
        task.listener = listener;
        mHandler.obtainMessage(MSG_SAVE_PIC, task).sendToTarget();
    }

    // ======== Saved media properties.

    public static String getLastSavedUri(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString("last_saved_uri", null);
    }

    public static String getLastSavedMime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString("last_saved_mime", null);
    }

    private static String createFileNameByTime() {
        return FILE_FORMAT.format(Calendar.getInstance().getTime());
    }

    private static File getMediaOutputFile(String prefix, String extName) {
        // Get out directory
        File outDir = getMediaOutputDirectory();

        // Create target file
        String fileName = createFileNameByTime();
        File file = new File(outDir, prefix + fileName + "." + extName);
        int copy = 0;
        while (file.exists()) {
            file = new File(outDir,
                    String.format("%s%s (%d).%s", prefix, fileName, ++copy, extName));
        }

        return file;
    }

    public static File getMediaOutputDirectory() {
        File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        return outDir;
    }

    public static File getVideoOutputFile() {
        return getMediaOutputFile("VID_", "mp4");
    }

    // ======== Media save/insert

    private static String saveImage(Context context, byte[] jpeg, String title, String description)
            throws Exception {
        String stringUrl = null;
        // Create target file
        File file = getMediaOutputFile("IMG_", "jpg");

        // Write to file
        FileOutputStream fos = new FileOutputStream(file);
        try {
            fos.write(jpeg);
        } finally {
            fos.close();
        }

        // Added photo to the gallery
        // stringUrl =
        // MediaStore.Images.Media.insertImage(context.getContentResolver(),
        // file.getAbsolutePath(), null, null);
        // Don't know why this doesn't work. Send broadcast instead.
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcastAsUser(mediaScanIntent, UserHandle.CURRENT);
        stringUrl = contentUri.toString();

        // Save URI
        if (stringUrl != null) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                    .putString("last_saved_uri", stringUrl).putString("last_saved_mime", "image/*")
                    .commit();
        }
        return stringUrl;
    }

    public static void insertVideoFile(Context context, File videoFile) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                .putString("last_saved_uri", Uri.fromFile(videoFile).toString())
                .putString("last_saved_mime", "video/*").commit();

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(videoFile);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcastAsUser(mediaScanIntent, UserHandle.CURRENT);
    }

    // ======== Image Decode

    public static Bitmap decodeThumbBitmap(byte[] data, int offset, int length) {
        // Decode bitmap: Get bounds
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);
        int size = options.outWidth * options.outHeight;
        int sample = 1;
        while (size > MAX_THUMB_SIZE) {
            sample *= 2;
            size /= 4;
        }

        // Decode bitmap
        options.inJustDecodeBounds = false;
        options.inSampleSize = sample;
        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return bm;
    }

    public static Bitmap decodeThumbBitmap(String stringUri) {
        if (stringUri == null) {
            return null;
        }

        Uri uri = Uri.parse(stringUri);
        if (uri == null) {
            return null;
        }
        String path = uri.getPath();

        // Decode bitmap: Get bounds
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int size = options.outWidth * options.outHeight;
        int sample = 1;
        while (size > MAX_THUMB_SIZE) {
            sample *= 2;
            size /= 4;
        }

        // Decode bitmap
        options.inJustDecodeBounds = false;
        options.inSampleSize = sample;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        return bm;
    }

    public static Bitmap decodeVideoThumbBitmap(Context context, String stringUri) {
        if (stringUri == null) {
            return null;
        }

        Uri uri = Uri.parse(stringUri);
        if (uri == null) {
            return null;
        }

        return ThumbnailUtils.createVideoThumbnail(uri.getPath(), Thumbnails.MICRO_KIND);

//		ContentResolver cr = context.getContentResolver();
//		Cursor c = MediaStore.Video.query(cr, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,new String[]{MediaStore.Video.Media._ID});
//		if (c == null) {
//			return null;
//		}
//		// TODO: select the item
//		int id = c.getInt(0);
//		
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inSampleSize = 1;
//		
//		Bitmap bm = MediaStore.Video.Thumbnails.getThumbnail(cr, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
//		return bm;
    }
}
