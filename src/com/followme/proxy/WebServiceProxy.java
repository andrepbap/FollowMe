package com.followme.proxy;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.followme.library.HttpConnection;

import android.util.Log;

public abstract class WebServiceProxy {

	private final static String API_KEY = "afae92e4fb8ca1258431f1a709910ca2dbd2f0e0";
	private final static String API_URL = "http://php-sigame.rhcloud.com/app/api/";
	private static JSONObject jo;
	
	public static String atualizaPosicao(int id_usuario, String latitude, String longitude) {
		String url = API_URL + "usuario/put-posi";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		
		jo = new JSONObject();
		try {
			jo.put("api_key", API_KEY);
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
		String url = API_URL + "grupo/get-usuarios-posi";
		
		jo = new JSONObject();
		
		try {
			jo.put("id_grupo", id_grupo);
			jo.put("api_key", API_KEY);

		} catch (JSONException e1) {
			Log.e("Script", "erro Json");
		}

		return HttpConnection.getSetDataWeb(url, "send-json", jo.toString());
	}
	
	public static String getGrupos(int id_usuario){
		String url = API_URL + "grupo/get";
		
		jo = new JSONObject();
		try {
			jo.put("api_key", API_KEY);
			jo.put("id_usuario", id_usuario);

		} catch (JSONException e1) {
			Log.e("Script", "erro Json");
		}
		
		return HttpConnection.getSetDataWeb(url, "send-json", jo.toString());
	}
}
