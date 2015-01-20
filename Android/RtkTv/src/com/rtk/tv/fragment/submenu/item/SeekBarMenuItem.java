package com.rtk.tv.fragment.submenu.item;

import com.rtk.tv.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarMenuItem extends MenuItem implements OnSeekBarChangeListener {
	
	// Data and status
	private int progress;
	private int max = 100;
	private int min = 0;

	// View
	private SeekBar mSeekBar;
	
	SeekBarMenuItem(int title) {
		super(title, ITEM_TYPE_SEEK_BAR);
	}
	public int getCurrentProgress() {
		return progress;
	}
	
	public SeekBarMenuItem setCurrentProgress(int progress) {
		return setCurrentProgress(progress, false);
	}
	
	public SeekBarMenuItem setCurrentProgress(int progress, boolean notify) {
		this.progress = progress;
		if (notify) {
			notifyValueChange(progress);
		}
		updateContent();
		return this;
	}
	
	public SeekBarMenuItem setBoundary(int min, int max) {
		this.min = min;
		this.max = max;
		return this;
	}
	
	@Override
	public void selectNext() {
		increaseProgress();
	}

	@Override
	public void selectPrev() {
		decreaseProgress();
	}

	public int increaseProgress() {
		progress++;
		if (progress > max) {
			progress = max;
		}
		notifyValueChange(progress);
		updateContent();
		return progress;
	}
	
	public int decreaseProgress() {
		progress--;
		if (progress < min) {
			progress = min;
		}
		notifyValueChange(progress);
		updateContent();
		return progress;
	}
	
	@Override
	protected void onBindView(LayoutInflater inflater, ViewGroup container, boolean inflate) {
		if (inflate) {
			inflater.inflate(R.layout.layout_item_setting_progress, container);
		}
		mSeekBar = (SeekBar) container.findViewById(R.id.seek);
		updateContent();
	}
	
	@Override
	protected void onUnbindView() {
		super.onUnbindView();
		mSeekBar = null;
	}
	
	private void updateContent() {
		if (mSeekBar == null) {
			return;
		}
		mSeekBar.setOnSeekBarChangeListener(null);
		mSeekBar.setMax(max - min);
		mSeekBar.setProgress(progress - min);
		mSeekBar.setOnSeekBarChangeListener(this);
		setTextValue(String.valueOf(progress));
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		this.progress = progress + min;
		updateContent();
		if (seekBar != mSeekBar) {
			// Not sure why this happen
			// New view for this item has been created, yet hasn't been attached to the list view!?
			// Workaround to update the TextView.
			ViewGroup parent =(ViewGroup) seekBar.getParent().getParent(); 
			TextView tv = (TextView) parent.findViewById(R.id.text_value); 
			tv.setText(String.valueOf(progress));
			tv.setVisibility(View.VISIBLE);
		}
		notifyValueChange(this.progress);
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
}
