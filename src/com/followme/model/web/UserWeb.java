package com.followme.model.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.followme.utils.HttpConnection;

import android.util.Log;

public abstract class UserWeb {

	private static JSONObject jo;
	
	public static String login(String email, String password){
		String url = ServerParams.getApiUrl() + "user/login";
		
		jo = new JSONObject();
		
		try {
			jo.put("apiKey", ServerParams.getEncryptedApiKey());
			jo.put("email", email);
			jo.put("password", password);

		} catch (JSONException e1) {
			Log.e("Script", "erro Json");
		}

		return HttpConnection.getSetDataWeb(url, jo.toString());
	}
	
	public static String atualizaPosicao(int idUser, Double latitude, Double longitude) {
		String url = ServerParams.getApiUrl() + "user/" + idUser + "/set-position";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		
		jo = new JSONObject();
		try {
			jo.put("apiKey", ServerParams.getEncryptedApiKey());
			jo.put("latitude", latitude);
			jo.put("longitude", longitude);
			jo.put("date", currentDateandTime);

		} catch (JSONException e1) {
			Log.e("Script", "erro atualizaPosicao");
		}

		return HttpConnection.getSetDataWeb(url, jo.toString());
	}
	
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
