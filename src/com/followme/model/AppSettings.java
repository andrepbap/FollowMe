package com.followme.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

public class AppSettings {
	
	private static final String TAG = AppSettings.class.getSimpleName();

	private static JSONObject readSettings(Context cx) {
		String FILENAME = "settings";
		String settings = null;

		FileInputStream fis;
		try {
			ContextWrapper cw = new ContextWrapper(cx);
			fis = cw.openFileInput(FILENAME);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();
			settings = sb.toString();
			fis.close();
			JSONObject jSettings =  new JSONObject(settings);
			Log.e(TAG, jSettings.toString());
			return jSettings;

		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	private static void storeSettings(JSONObject settings, Context cx) {

		try {
			String FILENAME = "settings";

			ContextWrapper cw = new ContextWrapper(cx);
			FileOutputStream fos = cw.openFileOutput(FILENAME,
					Context.MODE_PRIVATE);

			fos.write(settings.toString().getBytes());
			fos.close();
			
			Log.e(TAG, "file saved");

		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public static boolean isOffMapSending(Context cx) {
		try {
			JSONObject jSettings = readSettings(cx);
			return jSettings.getBoolean("OffMapSending");
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		} catch (NullPointerException e) {
			Log.e(TAG, e.getMessage());
		}
		
		//default value
		return true;
	}

	public static void setOffMapSending(boolean offMapSending, Context cx) {
		JSONObject jSettings = readSettings(cx);
		if(jSettings == null){
			jSettings = new JSONObject();
		}
		
		try {
			jSettings.put("OffMapSending", offMapSending);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		storeSettings(jSettings, cx);
	}

	public static long getAppOffMapSendRate(Context cx) {
		try {
			JSONObject jSettings = readSettings(cx);
			return jSettings.getLong("appOffMapSendRate");
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		} catch (NullPointerException e) {
			Log.e(TAG, e.getMessage());
		}
		//default value
		return 600000;
	}

	public static void setAppOffMapSendRate(long appOffMapSendRate, Context cx) {
		JSONObject jSettings = readSettings(cx);
		if(jSettings == null){
			jSettings = new JSONObject();
		}
		
		try {
			jSettings.put("appOffMapSendRate", appOffMapSendRate);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		storeSettings(jSettings, cx);
	}

	public static long getAppMapSendRate(Context cx) {
		try {
			JSONObject jSettings = readSettings(cx);
			return jSettings.getLong("appMapSendRate");
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		} catch (NullPointerException e) {
			Log.e(TAG, e.getMessage());
		}
		//default value
		return 5000;
	}

	public static void setAppMapSendRate(long appMapSendRate, Context cx) {
		JSONObject jSettings = readSettings(cx);
		if(jSettings == null){
			jSettings = new JSONObject();
		}
		
		try {
			jSettings.put("appMapSendRate", appMapSendRate);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		storeSettings(jSettings, cx);
	}

}
