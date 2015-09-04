package com.followme.location;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;

import com.followme.proxy.WebServiceProxy;

public class SendPositionSingleton {
	
	private static SendPositionSingleton mInstance = null;
	private TimerTask task0;
	private Timer timer;
	private AppLocationManager appLocationManager;
	private final int user;
	private long period = 5000; //Default
	
	private SendPositionSingleton(int id_logado, Context cx){
		user = id_logado;
		appLocationManager = new AppLocationManager(cx);
		timer = null;
	}
	
	public static SendPositionSingleton getInstance(int id_logado, Context cx){
		if(mInstance == null){
			mInstance = new SendPositionSingleton(id_logado, cx);
		}

		return mInstance;
		
	}
	
	public Boolean startStop(){
		if(timer == null){
			timer = new Timer();
			
			task0 = new TimerTask(){
				public void run(){
					String result = WebServiceProxy.atualizaPosicao(user, appLocationManager.getLatitude(), appLocationManager.getLongitude());
					Log.e("result", result);
				}
			};
	        timer.schedule(task0, 0, period);
	        
	        return true;
		}
		else{
			timer.cancel();
			timer = null;
			return false;
		}	
	}
	
	public void setPeriod(long period){
		this.period = period;
	}
	
	public boolean getTimerStatus(){
		if(timer == null){
			return false;
		}
		return true;
	}

}
