package org.apache.cordova.example;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * This class echoes a string called from JavaScript.
 */
public class Echo extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.e("","dfgsdfgsdfsd");
    	if (action.equals("echo")) {
            String message = args.getString(0); 
            this.echo(message,callbackContext);
            return true;
        }
        return false;
    }

    private void echo(String message, CallbackContext callbackContext) {
    	Log.e("","dfgsdfgsdfsd");
        if (message != null && message.length() > 0) { 
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
