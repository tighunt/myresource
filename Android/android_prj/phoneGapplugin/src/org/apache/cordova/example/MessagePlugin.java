package org.apache.cordova.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.telephony.SmsManager;
import android.util.Log;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.Plugin;

/**
 * 
 * @author Royal
 *
 */
public class MessagePlugin extends Plugin{

	private static final String SEND = "send";
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		PluginResult result = null;
		Log.e("","send msm");
		if(SEND.equals(action)){
			try {
				//
				String target = data.getString(0);
				//
				String content = data.getString(1);
				//
				SmsManager sms = SmsManager.getDefault();
				//
				sms.sendTextMessage(target, null, content, null, null);
				
				//
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("target", target);
				jsonObj.put("content", content);
				//
				result = new PluginResult(PluginResult.Status.OK,jsonObj);
			} catch (JSONException e) {
				result = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
			}
		}else{
			//
			result = new PluginResult(PluginResult.Status.INVALID_ACTION);
		}
		return result;
	}

}
