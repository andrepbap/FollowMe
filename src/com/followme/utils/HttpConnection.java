package com.followme.utils;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.util.Log;

public class HttpConnection {

    public static  String getSetDataWeb(String url, String data){

		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost(url);
		String answer = "";
		
		try {
			ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
			valores.add(new BasicNameValuePair("json", data));
			httpPost.setEntity(new UrlEncodedFormEntity(valores));
			HttpResponse resposta = httpClient.execute(httpPost);
			answer = EntityUtils.toString(resposta.getEntity());
		}
		catch(NullPointerException e){ 
			e.printStackTrace(); 
			Log.e("Script", "e>>"+e);
			
		}
		catch(ClientProtocolException e){ 
			e.printStackTrace(); 
			Log.e("Script", "e>>"+e);
			
		}
		catch(IOException e){ 
			e.printStackTrace();
			Log.e("Script", "e>>"+e);
			return "3";
		}
		
		return answer;
	}


}
