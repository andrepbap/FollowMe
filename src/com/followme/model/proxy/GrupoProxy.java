package com.followme.model.proxy;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.followme.library.HttpConnection;

public class GrupoProxy {
	
	private static JSONObject jo;
	
	public static String getGrupos(int id_usuario){
		String url = ServerParams.getApiUrl() + "grupo/get";
		
		jo = new JSONObject();
		try {
			jo.put("api_key", ServerParams.getApiKey());
			jo.put("id_usuario", id_usuario);

		} catch (JSONException e1) {
			Log.e("Script", "erro Json");
		}
		
		return HttpConnection.getSetDataWeb(url, "send-json", jo.toString());
	}
}
