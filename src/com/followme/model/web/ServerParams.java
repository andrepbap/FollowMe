package com.followme.model.web;

import android.util.Log;

import com.followme.utils.encryption.ApiCrypter;

public class ServerParams {

	private final static String API_KEY = "7e35364adb5f775ac67215bd417b5945ceeeec8b";
	private final static String API_URL = "http://192.168.2.112:8080/api/";
	
	public static String getEncryptedApiKey() {
		long unixTime = System.currentTimeMillis() / 1000L;
		
		ApiCrypter apiCrypter = new ApiCrypter();
		String apiKey = null;
		try {
			apiKey = ApiCrypter.bytesToHex(apiCrypter.encrypt(API_KEY + "-" + unixTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("crypter error", e.getMessage());
		}
		return apiKey;
	}
	
	public static String getApiUrl() {
		return API_URL;
	}
	
	
}
