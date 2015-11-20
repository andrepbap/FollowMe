package com.followme.model.web;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.followme.library.ApiCrypter;
import com.followme.library.HttpConnection;

public class GroupWeb {
	
	private static JSONObject jo;
	
	public static String getGroups(int idUser){
		String url = ServerParams.getApiUrl() + "user/"+ idUser +"/get-groups";
		
		jo = new JSONObject();
		try {
			jo.put("apiKey", ServerParams.getEncryptedApiKey());

		} catch (JSONException e1) {
			Log.e("Script", "erro Json");
		}
		
		return HttpConnection.getSetDataWeb(url, jo.toString());
	}
}
