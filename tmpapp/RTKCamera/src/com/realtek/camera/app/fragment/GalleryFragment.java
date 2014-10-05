
package com.realtek.camera.app.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.realtek.camera.R;
import com.realtek.camera.util.ImageLoader;
import com.realtek.camera.util.MediaSaver;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("deprecation")
public class GalleryFragment extends Fragment {

    public static final String EXTRA_URI = "uri";

    public interface GalleryFragmentListener {
        public void onImageLoaded(File path, int index, int count);
    }

    private static class MediaEntry {

        public boolean isImage;
        public boolean isVideo;
        public final File file;
        
        public MediaEntry(File f) {
            file = f;
        }

        public Uri uri() {
            return Uri.fromFile(file);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MediaEntry) {
                return file.equals(((MediaEntry) o).file);
            }
            return super.equals(o);
        }

        private static Comparator<MediaEntry> COMP_DATE = new Comparator<GalleryFragment.MediaEntry>() {
            
            @Override
            public int compare(MediaEntry lhs, MediaEntry rhs) {
                return (int) (lhs.file.lastModified() - rhs.file.lastModified());
            }
        };
    }
    
    // View
    private ImageView mSwitcher;
    private Gallery mGallery;
    private GalleryAdapter mAdapter;
    
    private GalleryFragmentListener mListener;
    
    private boolean mFirstLoaded = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_gallery, container, false);
        
        mSwitcher = (ImageView) layout.findViewById(R.id.image_switcher);
        
        mAdapter = new GalleryAdapter(getActivity());
        mGallery = (Gallery) layout.findViewById(R.id.gallery);
        mGallery.setFocusable(false);
        mGallery.setOnItemSelectedListener(mOnGalleryItemSelected);
        mGallery.setOnFocusChangeListener(mOnGalleryItemFocused);
        mGallery.setOnItemClickListener(mOnGalleryItemClicked);
        mGallery.setAdapter(mAdapter);
        return layout;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(0, getArguments(), mLoaderCallback);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof GalleryFragmentListener) {
            mListener = (GalleryFragmentListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void hidePreview() {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        mSwitcher.setVisibility(View.INVISIBLE);
        mSwitcher.startAnimation(anim);
    }
    
    private OnFocusChangeListener mOnGalleryItemFocused = new OnFocusChangeListener() {
        
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
        }
    };
    
    private OnItemClickListener mOnGalleryItemClicked = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaEntry media = (MediaEntry) mAdapter.getItem(position);
            
            // Launch external gallery
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = media.uri();
            String mime = media.isImage ? "image/*" : "video/*";
            intent.setDataAndType(uri, mime);
            startActivity(Intent.createChooser(intent, getString(R.string.title_open_media)));
        }
    };
    
    private OnItemSelectedListener mOnGalleryItemSelected = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            MediaEntry media = (MediaEntry) mAdapter.getItem(position);
            if (media.isVideo) {
                ImageLoader.getInstance().loadVideoThumbnail(mSwitcher, media.uri());
            } else if (media.isImage) {
                ImageLoader.getInstance().loadImage(mSwitcher, media.uri());
            }
            if (mListener != null) {
                mListener.onImageLoaded(media.file, position, mAdapter.getCount());
            }
            view.requestFocus();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mSwitcher.setVisibility(View.INVISIBLE);
        }
    };
    
    private class GalleryAdapter extends BaseAdapter {
        
        protected final List<MediaEntry> mData = new ArrayList<GalleryFragment.MediaEntry>();
        
        protected final Context mContext;
        
        public GalleryAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gallery, parent, false);
            }
            
            ImageView iv = (ImageView) convertView.findViewById(R.id.image_thumb);
            MediaEntry media = mData.get(position);
            if (media.isVideo) {
                ImageLoader.getInstance().loadVideoThumbnail(iv, media.uri(), ImageLoader.CALLBACK_FADE_IN);
                convertView.findViewById(R.id.indicator_play).setVisibility(View.VISIBLE);
            } else if (media.isImage) {
                ImageLoader.getInstance().loadImage(iv, media.uri(), ImageLoader.CALLBACK_FADE_IN);
                convertView.findViewById(R.id.indicator_play).setVisibility(View.INVISIBLE);
            }
            
            iv.setOnFocusChangeListener(mOnGalleryItemFocused);
            return convertView;
        }

        public void setData(List<MediaEntry> data) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
        
    }

    private LoaderCallbacks<List<MediaEntry>> mLoaderCallback = new LoaderCallbacks<List<MediaEntry>>() {

        @Override
        public Loader<List<MediaEntry>> onCreateLoader(int id, Bundle args) {
            Uri uri = null;
            if (args != null) {
                uri = args.getParcelable(EXTRA_URI);
            }
            return new MediaLoader(getActivity(), uri);
        }

        @Override
        public void onLoadFinished(Loader<List<MediaEntry>> loader, List<MediaEntry> data) {
            mAdapter.setData(data);
            if (mFirstLoaded) {
                Bundle args = getArguments();
                if (args == null) {
                    return;
                }
                Uri uri = args.getParcelable(EXTRA_URI);
                if (uri == null) {
                    return;
                }
                
                File f = new File(uri.getPath());
                if (!f.isDirectory()) {
                    int idx = data.indexOf(new MediaEntry(f));
                    if (idx >= 0) {
                        mGallery.setSelection(idx);
                    }
                }
            }
            mFirstLoaded = false;
        }

        @Override
        public void onLoaderReset(Loader<List<MediaEntry>> loader) {
            
        }
    };

    private static class MediaLoader extends AsyncTaskLoader<List<MediaEntry>> {

        private Uri mUri;
        
        public MediaLoader(Context context, Uri uri) {
            super(context);
            mUri = uri;
        }

        @Override
        public List<MediaEntry> loadInBackground() {
            File dir;
            if (mUri == null) {
                dir = MediaSaver.getMediaOutputDirectory();
            } else {
                dir = new File(mUri.getPath());
                if (!dir.isDirectory()) {
                    dir = dir.getParentFile();
                }
            }
            
            File[] files = dir.listFiles();
            List<MediaEntry> list = new ArrayList<GalleryFragment.MediaEntry>();
            if (files != null) {
                for (File f : files) {
                    String name = f.getName();
                    if (name.endsWith("jpg")) {
                        MediaEntry media = new MediaEntry(f);
                        media.isImage = true;
                        list.add(media);
                    } else if (name.endsWith("mp4")) {
                        MediaEntry media = new MediaEntry(f);
                        media.isVideo = true;
                        list.add(media);
                    }
                }
                
                Collections.sort(list, MediaEntry.COMP_DATE);
            }
            return list;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }
        
        
    }
}
