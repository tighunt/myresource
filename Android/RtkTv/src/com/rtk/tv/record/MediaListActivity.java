package com.rtk.tv.record;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.rtk.tv.BaseActivity;
import com.rtk.tv.R;
import com.rtk.tv.media.MediaEntry;
import com.rtk.tv.utils.CheckedArrayList;
import com.rtk.tv.utils.ImageLoader;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;


public class MediaListActivity extends Activity implements OnItemClickListener, OnItemSelectedListener, OnClickListener {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd", Locale.US);
	
	private static final int REQUEST_DELETE = 1;
	
	private Context context;
	
	@SuppressWarnings("serial")
	private static class MediaStorage extends CheckedArrayList<MediaEntry> {

		public long totalSpace = -1;
		public long usableSpace = -1;

		public void copyFrom(MediaStorage data) {
			clear();
			addAll(data);
			totalSpace = data.totalSpace;
			usableSpace = data.usableSpace;
		}
	}

	// Data
	private final MediaStorage mData = new MediaStorage();
	private boolean mManageMode = false;
	private boolean mIsLoading = false;
	private int mLoaderId = 0;

	// View
	private ImageView mImagePreview;
	private ListView mListView;
	private MediaListAdapter mAdapter;
	// View: Disk space
	private ProgressBar mProgressUsage;
	private TextView mTextDiskUsage;
	private TextView mTextPage;
	private ProgressBar mProgressLoading;
	
	// Hint View
	private View mHintManage;
	private View mHintDelete;
	private View mHintSelect;
	private View mHintLock;
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	
	public void createView() {
		// List
		mListView = (ListView) findViewById(R.id.list);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mAdapter = new MediaListAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnKeyListener(mOnListKey);
		mListView.setOnItemSelectedListener(this);
		
		// Other
		mImagePreview = (ImageView) findViewById(R.id.image_preview);
		mProgressLoading = (ProgressBar) findViewById(R.id.progress);
		// Disk space
		mProgressUsage = (ProgressBar) findViewById(R.id.progress_disk_usage);
		mTextDiskUsage = (TextView) findViewById(R.id.text_disk_usage);
		mTextPage = (TextView) findViewById(R.id.text_page);
		
		// Hint view
		mHintManage = findViewById(R.id.hint_manage);
		mHintSelect = findViewById(R.id.hint_select_all);
		mHintDelete = findViewById(R.id.hint_delete);
		mHintLock = findViewById(R.id.hint_lock_unlock);
		
		mHintManage.setOnClickListener(this);
		mHintSelect.setOnClickListener(this);
		mHintDelete.setOnClickListener(this);
		mHintLock.setOnClickListener(this);

		mListView.requestFocus();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_fragment_media_record_list);
		context = this;
		createView();
	}
	@Override
	public void onStart() {
		super.onStart();
		mIsLoading = true;
		refreshViews();
		getLoaderManager().initLoader(0, null, mLoaderCallback);
	}

	/**
	 * Circular child focusing
	 */
	private final OnKeyListener mOnListKey = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_UP:
						if (mListView.getSelectedItemPosition() == 0) {
							mListView.setSelection(mListView.getCount() - 1);
							return true;
						}
						break;
					case KeyEvent.KEYCODE_DPAD_DOWN:
						if (mListView.getSelectedItemPosition() == mListView.getCount() - 1) {
							mListView.setSelection(0);
							return true;
						}
						break;
					default:
						break;
				}
			}
			return false;
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_DELETE && resultCode == Activity.RESULT_OK) {
			mIsLoading = true;
			refreshViews();
			getLoaderManager().restartLoader(++mLoaderId, data.getExtras(), mLoaderCallback);
		}
	}
	
	/**
	 * Loader callback
	 */
	private final LoaderCallbacks<MediaStorage> mLoaderCallback = new LoaderCallbacks<MediaStorage>() {

		@Override
		public Loader<MediaStorage> onCreateLoader(int id, Bundle args) {
			String[] deleteItems = null;
			if (args != null) {
				deleteItems = args.getStringArray("deleteItems");
			}
			return new MediaLoader(context, deleteItems);
		}

		@Override
		public void onLoadFinished(Loader<MediaStorage> loader, MediaStorage data) {
			mIsLoading = false;
			mData.copyFrom(data);
			refreshViews();
		}

		@Override
		public void onLoaderReset(Loader<MediaStorage> loader) {

		}
	};

	
	/**
	 * Loader
	 * @param context
	 */
	private static class MediaLoader extends AsyncTaskLoader<MediaStorage> {
		
		private MediaStorage mData;
		private String[] mToDelete;

		public MediaLoader(Context context, String[] deleteItems) {
			super(context);
			mToDelete = deleteItems;
		}

		@Override
		public MediaStorage loadInBackground() {
			// Delete files
			/*if (mToDelete != null) {
				MediaManager manager = MediaManager.getInstance(getContext());
				manager.deleteRecordedItems(mToDelete);
			}*/

			mData = new MediaStorage();
			/*MediaManager m = MediaManager.getInstance(getContext());
			m.listRecordFiles(mData);
			
			// Get disk space.
			TvManagerHelper tm = TvManagerHelper.getInstance(getContext());
			File storage = tm.getPvrStorage();
			if (storage != null && storage.exists()) {
				mData.usableSpace = storage.getUsableSpace();
				mData.totalSpace = storage.getTotalSpace();
			}*/
			
			return mData;
		}

		@Override
		protected void onStartLoading() {
			super.onStartLoading();
			if (mData == null) {
				forceLoad();
			} else {
				deliverResult(mData);
			}
		}

		@Override
		protected void onReset() {
			super.onReset();
			mData = null;
		}

	}

	private void refreshViews() {

		// Get disk space.
		if (mData.totalSpace > 0) {
			long usedSpace = mData.totalSpace - mData.usableSpace;
			mProgressUsage.setMax((int) (mData.totalSpace / 1024L));
			mProgressUsage.setProgress((int) (usedSpace / 1024L));
			mTextDiskUsage.setText(getString(R.string.format_disk_usage, 
					humanReadableByteCount(usedSpace, true),
					humanReadableByteCount(mData.totalSpace, true)));
			mProgressUsage.setVisibility(View.VISIBLE);
			mTextDiskUsage.setVisibility(View.VISIBLE);
		} else {
			mProgressUsage.setVisibility(View.INVISIBLE);
			mTextDiskUsage.setVisibility(View.INVISIBLE);
		}

		mProgressLoading.setVisibility(mIsLoading ? View.VISIBLE : View.GONE);

		// Refresh list
		mAdapter.notifyDataSetChanged();
		
		refreshHint();
		refreshPages();
	}
	
	private void refreshPages() {
		int count = mListView.getCount();
		int position = mListView.getSelectedItemPosition();
		
		if (count > 0) {
			int color = getResources().getColor(R.color.theme_focused);
			String str1 = String.format(Locale.US, "<font color='#%6X'>%d</font>", color & 0x00FFFFFF, position + 1);
			String str2 = String.format(Locale.US, "%d", count);
			String txt = getString(R.string.format_item_position, str1, str2);
			Spanned spanned = Html.fromHtml(txt);
			mTextPage.setText(spanned);
			mTextPage.setVisibility(View.VISIBLE);
		} else {
			mTextPage.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_PROG_GREEN:
				setManageMode(!mManageMode);
				return true;
			// Manage: Delete
			case KeyEvent.KEYCODE_PROG_RED:
				clickDelete();
				return true;
			// Manage: Select All
			case KeyEvent.KEYCODE_PROG_YELLOW:
				clickSelect();
				return true;
			// Manage: Lock
			case KeyEvent.KEYCODE_PROG_BLUE:
				clickLock();
				return true;
			default:
				return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.hint_delete:
				clickDelete();
				break;
			case R.id.hint_lock_unlock:
				clickLock();
				break;
			case R.id.hint_manage:
				setManageMode(!mManageMode);
				break;
			case R.id.hint_select_all:
				clickSelect();
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MediaEntry media = mData.get(position);
		if (mManageMode) {
			media.toggle();
			mAdapter.notifyDataSetChanged();
			refreshHint();
			
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(media.getUri(), "video/*");
			intent = Intent.createChooser(intent, getString(R.string.open_media));
			startActivity(intent);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		MediaEntry media = mData.get(position);
		// Load video thumbnail
		ImageLoader.getInstance().loadVideoThumbnail(mImagePreview, media.getUri());
		refreshPages();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		ImageLoader.getInstance().clearImage(mImagePreview);
	}

	private void clickDelete() {
		if (mManageMode) {
			// Get checked file paths
			List<MediaEntry> list = mData.getCheckedItems();
			int count = list.size();
			String[] paths = new String[count];
			for (int i = 0; i < count; i++) {
				paths[i] = list.get(i).getPath();
			}
			
			// Check item locked
			boolean hasItemLocked = false;
			for(MediaEntry m : list) {
				if (m.isLocked()) {
					hasItemLocked = true;
					break;
				}
			}
			if (hasItemLocked) {
				Toast.makeText(context, R.string.msg_item_locked, Toast.LENGTH_SHORT).show();
				return;
			}
			
			//Todo
			/*Bundle args = new Bundle();
			args.putStringArray("deleteItems", paths);			
			// Show confirm dialog
			String title = getString(R.string.delete);
			String msg = getString(R.string.format_msg_delete_items, count);
			DialogFragment dialog = SimpleAlertDialog.newInstance(title, msg, args);
			dialog.setTargetFragment(this, REQUEST_DELETE);
			dialog.show(getFragmentManager(), "delete");*/
		}
	}
	
	private void clickLock() {
		if (mManageMode) {
			List<MediaEntry> list = mData.getCheckedItems();
			for (MediaEntry e: list) {
				e.toggleLock();
			}
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private void clickSelect() {
		if (mManageMode) {
			mData.checkAllItems();
			mAdapter.notifyDataSetChanged();
			refreshHint();
		}
	}

	private void setManageMode(boolean mode) {
		mManageMode = mode;
		
		// Uncheck all items on exiting manage mode
		if (!mManageMode) {
			mData.uncheckAllItems();
		}
		
		mAdapter.notifyDataSetChanged();
		refreshHint();
	}
	
	private void refreshHint() {
		if (!mManageMode) {
			mHintManage.setVisibility(View.VISIBLE);
			mHintDelete.setVisibility(View.GONE);
			mHintSelect.setVisibility(View.GONE);
			mHintLock.setVisibility(View.GONE);
			return;
		}
		
		int selectView = mData.hasCheckedItem()? View.VISIBLE: View.GONE;
		
		mHintManage.setVisibility(View.VISIBLE);
		mHintSelect.setVisibility(View.VISIBLE);
		mHintDelete.setVisibility(selectView);
		mHintLock.setVisibility(selectView);
		
	}

	private static class ViewHolder {
		CheckBox textName;
		TextView textDate;
		TextView textDuration;
		ImageView imageInfo;
	}
	
	private class MediaListAdapter extends BaseAdapter {

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
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recorded_media, parent, false);
				holder = new ViewHolder();
				holder.textName = (CheckBox) convertView.findViewById(R.id.text_name);
				holder.textDate = (TextView) convertView.findViewById(R.id.text_date);
				holder.textDuration = (TextView) convertView.findViewById(R.id.text_duration);
				holder.imageInfo = (ImageView) convertView.findViewById(R.id.ic_file_info);
				convertView.setTag(R.layout.item_recorded_media, holder);
			} else {
				holder = (ViewHolder) convertView.getTag(R.layout.item_recorded_media);
			}
			

			MediaEntry m = mData.get(position);
			holder.textName.setText(m.getName());
			holder.textName.setEnabled(mManageMode);
			holder.textName.setChecked(m.isChecked());
			
			if (m.isLocked()) {
				holder.imageInfo.setImageResource(R.drawable.ic_file_locked);
			} else {
				holder.imageInfo.setImageBitmap(null);
			}
			
			holder.textDate.setText(DATE_FORMAT.format(new Date(m.getFile().lastModified())));
			
			long duration = m.getDuration();
			if (duration > 0) {
				holder.textDuration.setText(formatTimeDuration(parent.getContext(), duration));
			} else {
				holder.textDuration.setText("---");
			}
			return convertView;
		}

	}

	private static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format(Locale.US, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	private static String formatTimeDuration(Context context, long duration) {
		if (duration < DateUtils.MINUTE_IN_MILLIS) {
			// Seconds
			int d = (int) (duration / DateUtils.SECOND_IN_MILLIS);
			return context.getString(R.string.format_time_sec, d);
		} else if (duration < DateUtils.HOUR_IN_MILLIS) {
			// Minutes
			int d = (int) (duration / DateUtils.MINUTE_IN_MILLIS);
			return context.getString(R.string.format_time_min, d);
		} else {
			// Hours + Minutes
			int m = (int) ((duration % DateUtils.HOUR_IN_MILLIS) / DateUtils.MINUTE_IN_MILLIS);
			int h = (int) (duration / DateUtils.HOUR_IN_MILLIS);
			return context.getString(R.string.format_time_hour_min, h, m);
		}
	}

}
