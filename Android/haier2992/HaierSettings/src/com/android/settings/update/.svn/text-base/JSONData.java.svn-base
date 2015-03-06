package com.android.settings.update;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONData {

	/**
	 * 通过URL地址获取关于系统版本信息的字符串，提供给JSON解析
	 * 
	 * @param systemUrl
	 * @return
	 */
	public static String getJsonString(JSONObject json) {
		Log.i("JRM", "send:" + json.toString());
		StringBuffer buffer = new StringBuffer();
		try {

			// String jsonUrl = "http://61.183.248.221:8086/wigAdmin/clientService.jsp";
			String jsonUrl = "http://tvosapp.babao.com/interface/clientService.jsp";

			URL url = new URL(jsonUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "text/json; charset=UTF-8");
			// 厂商号用18（专门给彪骐公版演示定义的厂商号），特征码暂用JRM0TVOS0000TV00。
			// 第一段是终端特征码，第二段是终端软件版本号，第三段是接口协议版本号（对于tvos系统，现在请统一设置为100）。
			// String clientInfor = "JRM0TVOS0000TV00_20_100";
			String clientInfor = "full_mstara3-eng_20_100";
			conn.setRequestProperty("Ttag", clientInfor);
			conn.setDoOutput(true);
			conn.connect();

			Writer writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
			writer.write(json.toString());
			writer.flush();

			System.out.println(conn.getResponseCode());

			InputStream u = conn.getInputStream();// 获取servlet返回值，接收
			BufferedReader in = new BufferedReader(new InputStreamReader(u, "utf-8"));

			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("JRM", "return:" + buffer.toString());
		return buffer.toString();
	}

	/**
	 * 从服务器端获取版本信息
	 */
	public static String getInfor() {

		JSONObject json = new JSONObject();

		try {
			json.put("ifid", "TVOSVerUpdate");
			json.put("pla", "full_mstara3-eng");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return getJsonString(json);
	}

}
