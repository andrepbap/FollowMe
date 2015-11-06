package com.followme.model.proxy;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.followme.library.HttpConnection;

import android.util.Log;

public abstract class UsuarioProxy {

	private static JSONObject jo;
	
	public static String atualizaPosicao(int id_usuario, String latitude, String longitude) {
		String url = ServerParams.getApiUrl() + "usuario/put-posi";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		
		jo = new JSONObject();
		try {
			jo.put("api_key", ServerParams.getApiKey());
			jo.put("usuario", id_usuario);
			jo.put("lat", latitude);
			jo.put("lng", longitude);
			jo.put("data", currentDateandTime);

		} catch (JSONException e1) {
			Log.e("Script", "erro atualizaPosicao");
		}

		return HttpConnection.getSetDataWeb(url, "send-json", jo.toString());
	}
	
	public static String getPosicoes(int id_grupo){
		String url = ServerParams.getApiUrl() + "grupo/get-usuarios-posi";
		
		jo = new JSONObject();
		
		try {
			jo.put("id_grupo", id_grupo);
			jo.put("api_key", ServerParams.getApiKey());

		} catch (JSONException e1) {
			Log.e("Script", "erro Json");
		}

		return HttpConnection.getSetDataWeb(url, "send-json", jo.toString());
	}
	
	
}
