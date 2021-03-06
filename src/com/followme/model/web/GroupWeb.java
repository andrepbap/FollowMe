package com.followme.model.web;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.followme.utils.HttpConnection;

public class GroupWeb {
	
	private static JSONObject jo;
	
	public static String getUsersLocation(int idGroup){
		String url = ServerParams.getApiUrl() + "group/" + idGroup + "/get-users-location";
		
		jo = new JSONObject();
		
		try {
			jo.put("apiKey", ServerParams.getEncryptedApiKey());
		} catch (JSONException e1) {
			Log.e("Script", "erro Json");
		}

		return HttpConnection.getSetDataWeb(url, jo.toString());
	}
	
	public static String createGroup(int idUser, String name, String description){
		String url = ServerParams.getApiUrl() + "group/save";
		
		jo = new JSONObject();
		
		try {
			jo.put("apiKey", ServerParams.getEncryptedApiKey());
			jo.put("idUser", idUser);
			jo.put("name", name);
			jo.put("description", description);
		} catch (JSONException e1) {
			Log.e("Script", "erro Json");
		}

		return HttpConnection.getSetDataWeb(url, jo.toString());
	}
	
}
