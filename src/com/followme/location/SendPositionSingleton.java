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
	private int user;
	private long period = 5000; //Default
	
	private SendPositionSingleton(Context cx){
		appLocationManager = new AppLocationManager(cx);
		timer = null;
		user = -1;
	}
	
	public static SendPositionSingleton getInstance(Context cx){
		if(mInstance == null){
			mInstance = new SendPositionSingleton(cx);
		}

		return mInstance;
		
	}
	
	public void setUser(int id_logado){
		user = id_logado;
	}
	
	public Boolean start(){
		if(timer == null && user != -1){
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
		return false;
		
	}
	
	public Boolean stop(){
		if(timer != null){
			timer.cancel();
			timer = null;
			return true;
		}
		return false;
	}
	
	public void setPeriod(long period){
		if(period > 0){
			this.stop();
			this.period = period;
			this.start();
		}
	}
	
	public boolean getTimerStatus(){
		if(timer == null){
			return false;
		}
		return true;
	}

}
