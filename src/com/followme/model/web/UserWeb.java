package com.followme.model.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.followme.library.HttpConnection;

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
	
	public static String atualizaPosicao(int id_usuario, String latitude, String longitude) {
		String url = ServerParams.getApiUrl() + "usuario/put-posi";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		
		jo = new JSONObject();
		try {
			jo.put("api_key", ServerParams.getEncryptedApiKey());
			jo.put("usuario", id_usuario);
			jo.put("lat", latitude);
			jo.put("lng", longitude);
			jo.put("data", currentDateandTime);

		} catch (JSONException e1) {
			Log.e("Script", "erro atualizaPosicao");
		}

		return HttpConnection.getSetDataWeb(url, jo.toString());
	}
	
	public static String getPosicoes(int id_grupo){
		String url = ServerParams.getApiUrl() + "grupo/get-usuarios-posi";
		
		jo = new JSONObject();
		
		try {
			jo.put("id_grupo", id_grupo);
			jo.put("api_key", ServerParams.getEncryptedApiKey());

		} catch (JSONException e1) {
			Log.e("Script", "erro Json");
		}

		return HttpConnection.getSetDataWeb(url, jo.toString());
	}
	
	
}
