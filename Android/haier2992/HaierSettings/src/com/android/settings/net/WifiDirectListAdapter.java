package com.android.settings.net;

import java.util.ArrayList;
import java.util.Collection;

import com.android.settings.R;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * [2012-2-24 yanhd]
 * wifi direct device list adapter
 */


class ViewHolder{
	TextView textView;
	ImageView imgView;
	
	ViewHolder(View view){
		this.textView = (TextView) view.findViewById(R.id.device_item);
		this.imgView = (ImageView) view.findViewById(R.id.device_connected_flag);
	}
}



public class WifiDirectListAdapter extends BaseAdapter{

	private Context context;
    private Collection<WifiP2pDevice> itemsList;
    private LayoutInflater factory;
	//ArrayList<WifiP2pDevice> itemsList;
    
	public WifiDirectListAdapter(Context context,Collection<WifiP2pDevice> peers) {
	//public WifiDirectListAdapter(Context context,ArrayList<WifiP2pDevice> peers) {
		this.context = context;
        this.itemsList = peers;
        factory = LayoutInflater.from(context);
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return itemsList.toArray()[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		WifiP2pDevice device = (WifiP2pDevice)itemsList.toArray()[position];
		ViewHolder viewHolder = null;
		View view = null;
		if (null == convertView || null == convertView.getTag()){
			view = (View) factory.inflate(R.layout.wifi_direct_list, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (null != device){
			viewHolder.textView.setText(device.deviceName);
			if (WifiP2pDevice.CONNECTED == device.status){
				viewHolder.imgView.setVisibility(View.VISIBLE);
			}else{
				viewHolder.imgView.setVisibility(View.GONE);
			}
		}else{
			viewHolder.imgView.setVisibility(View.GONE);
		}
		
		/*
		WifiP2pDevice device = (WifiP2pDevice)itemsList.toArray()[position];
		LayoutInflater factory = LayoutInflater.from(context);
		View view = (View) factory.inflate(R.layout.wifi_direct_list, null);
		TextView textView = (TextView) view.findViewById(R.id.device_item);
		ImageView imgView = (ImageView) view.findViewById(R.id.device_connected_flag);
		
		if (null != device){
			textView.setText(device.deviceName);
			if (WifiP2pDevice.CONNECTED == device.status){
				imgView.setVisibility(View.VISIBLE);
			}else{
				imgView.setVisibility(View.GONE);
			}
		}else{
			imgView.setVisibility(View.GONE);
		}
		*/
		return view;
	}

	
}
