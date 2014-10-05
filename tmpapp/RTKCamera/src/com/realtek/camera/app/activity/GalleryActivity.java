package com.realtek.camera.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.realtek.camera.MainActivity;
import com.realtek.camera.app.fragment.GalleryFragment;
import com.realtek.camera.app.fragment.GalleryFragment.GalleryFragmentListener;

import java.io.File;

public class GalleryActivity extends Activity implements GalleryFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            GalleryFragment fragment = new GalleryFragment();
            Bundle args = new Bundle();
            args.putParcelable(GalleryFragment.EXTRA_URI, getIntent().getData());
            fragment.setArguments(args);
            
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment).commit();
        }
        
        // ActionBar
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onImageLoaded(File path, int index, int count) {
        ActionBar ab = getActionBar();
        ab.setTitle(String.format("%s (%d/%d)", path.getParentFile().getName(), index + 1, count));
        ab.setSubtitle(path.getName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
