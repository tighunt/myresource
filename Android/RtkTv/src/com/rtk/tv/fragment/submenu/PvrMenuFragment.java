package com.rtk.tv.fragment.submenu;


//import android.os.storage.StorageEventListener;
//import android.os.storage.StorageVolume;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.rtk.tv.R;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.fragment.submenu.item.SpinnerMenuItem;
import com.rtk.tv.utils.Constants;
import java.util.List;

public class PvrMenuFragment extends BaseMenuFragment {
	
	private static final int[] VALUE_TIME_SHIFT_SIZE = {
		Constants.PVR_TIME_SHIFT_SIZE_512M,
		Constants.PVR_TIME_SHIFT_SIZE_1G,
		Constants.PVR_TIME_SHIFT_SIZE_2G,
		Constants.PVR_TIME_SHIFT_SIZE_4G,
	};
	protected static final int REQUEST_FORMAT = 0;
/*	
	private static class FileDelegate {
		
		public final StorageVolume file;
		
		public FileDelegate(StorageVolume file) {
			this.file = file;
		}

		@Override
		public String toString() {
			CharSequence label = file.getUserLabel();
			if (TextUtils.isEmpty(label)) {
				return file.getPathFile().getName();
			}
			return label.toString();
		}
		
	}*/
	
	private SpinnerMenuItem mItemDisk;
	private SpinnerMenuItem mItemSize;
	private MenuItem mItemFormat;
	private SpinnerMenuItem mItemTimeshift;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*if (requestCode == REQUEST_FORMAT && resultCode == Activity.RESULT_OK) {
			if (ActivityManager.isUserAMonkey()) {
				// Do nothing if user is a monkey.
				return;
			}
			
			StorageVolume storageVolume = data.getParcelableExtra(StorageVolume.EXTRA_STORAGE_VOLUME);
			
			Intent intent = new Intent(ExternalStorageFormatter.FORMAT_ONLY);
			intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
			intent.putExtra(StorageVolume.EXTRA_STORAGE_VOLUME, storageVolume);
			getActivity().startService(intent);
		}*/
	}

	@Override
	public void onStart() {
		super.onStart();
		//StorageManager.from(getActivity()).registerListener(mStorageListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		//StorageManager.from(getActivity()).unregisterListener(mStorageListener);
	}
/*	
	private StorageEventListener mStorageListener = new StorageEventListener() {

		@Override
		public void onUsbMassStorageConnectionChanged(boolean connected) {
			super.onUsbMassStorageConnectionChanged(connected);
			refreshDiskSetting();
		}

		@Override
		public void onStorageStateChanged(String path, String oldState, String newState) {
			super.onStorageStateChanged(path, oldState, newState);
			refreshDiskSetting();
		}

	};*/

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		
		// Select Disk
		mItemDisk = MenuItem.createSpinnerItem(R.string.STRING_SELECT_DISK);
		mItemDisk.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				/*FileDelegate d = (FileDelegate) mItemDisk.getSelectedOption();
				StorageVolume v = d.file;
				File f = v.getPathFile();
				tm.setPvrStorage(f.getAbsolutePath());*/
			}
		});
		items.add(mItemDisk);
		
		// Time Shift Size
		String[] shiftSizes = getResources().getStringArray(R.array.time_shift_size);
		mItemSize = MenuItem.createSpinnerItem(R.string.STRING_TIMESHIFT_SIZE);
		mItemSize
			.setSpinnerOptionsByArray(shiftSizes)
			.setCurrentPosition(0/*indexOf(VALUE_TIME_SHIFT_SIZE, tm.getPvrTimeShiftSize())*/)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					//tm.setPvrTimeShiftSize(VALUE_TIME_SHIFT_SIZE[value]);
				}
			});
		items.add(mItemSize);

		// Format Start 
		mItemFormat = MenuItem.createTextItem(R.string.STRING_FORMAT_DISK)
		.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*FileDelegate d = (FileDelegate) mItemDisk.getSelectedOption();
				StorageVolume storageVolume = d.file;
				
				Bundle args = new Bundle();
				args.putParcelable(StorageVolume.EXTRA_STORAGE_VOLUME, storageVolume);
				DialogFragment ad = SimpleAlertDialog.newInstance(R.string.STRING_FORMAT_DISK, R.string.msg_ask_erase_storage, args, true);
				ad.setTargetFragment(PvrMenuFragment.this, REQUEST_FORMAT);
				ad.show(getFragmentManager(), "dialog");*/
			}
		});
		items.add(mItemFormat);
		
		
		// Time Shift On/Off
		mItemTimeshift = MenuItem.createSpinnerItem(R.string.STRING_TIMESHIFT_ONOFF);
		mItemTimeshift
			.addSpinnerOption(R.string.STRING_ON)
			.addSpinnerOption(R.string.STRING_OFF)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					if(!enableItemTimeshift)
					{
						mItemTimeshift.setCurrentValue(itemTimeshiftValue);
						return;
					}
					boolean enable = value == R.string.STRING_ON;
					itemTimeshiftValue = value;
					/*SetPvrTimeShiftEnableTask setPvrTimeShiftEnableTask = 
							new SetPvrTimeShiftEnableTask(getActivity(),tm,mItemTimeshift);  
					setPvrTimeShiftEnableTask.execute(Boolean.valueOf(enable));*/  
				}
			});
		items.add(mItemTimeshift);
		refreshDiskSetting();
	}
private boolean enableItemTimeshift = true;
private int itemTimeshiftValue;
/*	   class SetPvrTimeShiftEnableTask extends AsyncTask<Boolean,Integer,Boolean>{  
	        private Context context; 
	        private TvManagerHelper tm;
	        private boolean enable;
	        private SpinnerMenuItem mItemTimeshift;
	        SetPvrTimeShiftEnableTask(Context context,TvManagerHelper tm,SpinnerMenuItem mItemTimeshift) {  
	            this.context = context;
	            this.tm = tm;
	            this.mItemTimeshift = mItemTimeshift;
	        } 
	        @Override  
	        protected void onPreExecute() {
	        	enableItemTimeshift = false;
	        }   
	        @Override  
	        protected Boolean doInBackground(Boolean... params) { 
	        	enable = ((Boolean)params[0]).booleanValue();
	        	Boolean re = tm.setPvrTimeShiftEnable(enable);
	            return re;  
	        }    
	        @Override  
	        protected void onPostExecute(Boolean re) {
	            if(!(re.booleanValue())){
					if (enable) {
						Toast.makeText(context, R.string.msg_failed_to_enable_timeshift, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(context, R.string.msg_failed_to_disable_timeshift, Toast.LENGTH_LONG).show();
					}
	            }
	            refreshDiskSetting();
	            enableItemTimeshift = true;
	        } 
	    }  */
	   
	private void refreshDiskSetting() {
		/*mItemDisk.clearOptions();
		
		// Get volumes
		StorageManager sm = StorageManager.from(getActivity());
		StorageVolume[] volumes = sm.getVolumeList();
		
		// Get current settings
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		final int source = tm.getInputSource();
		final boolean pvrTimeShiftEnabled = tm.isPvrTimeShiftEnabled();
		final File pvr = tm.getPvrStorage();
		
		// Setup options
		int position = -1;
		int i = 0;
		for (StorageVolume v : volumes) {
			if (TvManagerHelper.isUsablePvrStorageVolume(v)) {
				mItemDisk.addSpinnerOption(new FileDelegate(v), i);
				if (v.getPathFile().equals(pvr)) {
					position = i;
				}
				i++;
			}
		}
		
		boolean hasStorages = mItemDisk.getOptionCount() > 0;
		
		// Init selection
		if (position >= 0) {
			mItemDisk.setCurrentPosition(position);
			
			// Select first storage if not set before. 
		} else if (hasStorages) {
			mItemDisk.setCurrentPosition(0, true);
		}
		
		mItemTimeshift.setCurrentPosition(pvrTimeShiftEnabled ? 0 : 1);
		
		final boolean optionEnable = hasStorages && !pvrTimeShiftEnabled;
		//mItemTimeshift.setEnable(hasStorages && TvManagerHelper.isPvrSupported(source));
		mItemDisk.setEnable(optionEnable);
		mItemSize.setEnable(optionEnable);
		mItemFormat.setEnable(optionEnable);
		
		// Update ListView
		notifyDataSetChanged();*/
	}

	@Override
	public int getTitle() {
		return R.string.STRING_PVR;
	}

}
