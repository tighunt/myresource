
package com.rtk.tv.utils;

import android.widget.Checkable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CheckedArrayList<E extends Checkable> extends ArrayList<E> {

	public CheckedArrayList() {
		super();
	}
	
	public void checkAllItems() {
		for (E e : this) {
			e.setChecked(true);
		}
	}
	
	public void uncheckAllItems() {
		for (E e : this) {
			e.setChecked(false);
		}
	}
	
	public void toggleAllItems() {
		for (E e : this) {
			e.toggle();
		}
	}
	
	public List<E> getCheckedItems() {
		List<E> list = new ArrayList<E>();
		for (E e : this) {
			if (e.isChecked()) {
				list.add(e);
			}
		}
		return list;
	}
	
	public boolean hasCheckedItem() {
		// TODO: Improve me?
		for (E e : this) {
			if (e.isChecked()) {
				return true;
			}
		}
		return false;
	}
}
