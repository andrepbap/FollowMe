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
import android.widget.Toast;

public class AppSettings {

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
			Log.e("read file", jSettings.toString());
			return jSettings;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			
			Log.e("settings store file", "file saved");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean isOffMapSending(Context cx) {
		try {
			JSONObject jSettings = readSettings(cx);
			return jSettings.getBoolean("OffMapSending");
		} catch (JSONException e) {
			//default value
			return true;			
		} catch (NullPointerException e) {
			//default value
			return true;
		}
	}

	public static void setOffMapSending(boolean offMapSending, Context cx) {
		try {
			JSONObject jSettings = readSettings(cx);
			jSettings.put("OffMapSending", offMapSending);
			storeSettings(jSettings, cx);
		} catch (JSONException e) {
			Toast.makeText(
					cx,
					"Erro ao guardar configuração: OffMapSending",
					Toast.LENGTH_SHORT).show();
		} catch (NullPointerException e) {
			
		}
	}

	public static long getAppOffMapSendRate(Context cx) {
		try {
			JSONObject jSettings = readSettings(cx);
			return jSettings.getLong("AppOffMapSendRate");
		} catch (JSONException e) {
			//default value
			return 600000;
		} catch (NullPointerException e) {
			//default value
			return 600000;
		}
	}

	public static void setAppOffMapSendRate(long appOffMapSendRate, Context cx) {
		try {
			JSONObject jSettings = readSettings(cx);
			jSettings.put("appOffMapSendRate", appOffMapSendRate);
			storeSettings(jSettings, cx);
		} catch (JSONException e) {
			Toast.makeText(
					cx,
					"Erro ao guardar configuração: appOffMapSendRate",
					Toast.LENGTH_SHORT).show();
		} 
	}

	public static long getAppMapSendRate(Context cx) {
		try {
			JSONObject jSettings = readSettings(cx);
			return jSettings.getLong("AppMapSendRate");
		} catch (JSONException e) {
			//default value
			return 5000;
		} catch (NullPointerException e) {
			//default value
			return 5000;
		}
	}

	public static void setAppMapSendRate(long appMapSendRate, Context cx) {
		try {
			JSONObject jSettings = readSettings(cx);
			jSettings.put("appMapSendRate", appMapSendRate);
			storeSettings(jSettings, cx);
		} catch (JSONException e) {
			Toast.makeText(
					cx,
					"Erro ao guardar configuração: appMapSendRate",
					Toast.LENGTH_SHORT).show();
		} catch (NullPointerException e) {
			
		}
	}

}
