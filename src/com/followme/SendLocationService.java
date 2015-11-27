package com.followme;

import java.util.Timer;
import java.util.TimerTask;

import com.followme.model.SettingDAO;
import com.followme.model.SettingsID;
import com.followme.model.web.UserWeb;
import com.followme.utils.location.AppLocationManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SendLocationService extends Service {

	// Log tag
	private static final String TAG = SendLocationService.class.getSimpleName();
	
	private TimerTask task0;
	private Timer timer;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        startTask();
        return START_NOT_STICKY;
    }

	@Override
	public void onCreate() {
		startTask();
	}
	
	@Override
	public void onDestroy(){
		// if timer was started, finish schedule
		if(timer != null){
			timer.cancel();
			timer = null;
			task0 = null;
		}
	}
	
	private void startTask(){
		// if timer was started, finish schedule
		if(timer != null){
			timer.cancel();
			timer = null;
			task0 = null;
		}
		
		// open database
		SettingDAO bdInstance = new SettingDAO(getApplicationContext());
		bdInstance.open();
		
		if(!Boolean.valueOf(bdInstance.getSetting(SettingsID.IS_OFF_MAP_SENDING).getValue())){
			stopSelf();
		} else { 
			final AppLocationManager appLocationManager = new AppLocationManager(getApplicationContext());
			
			final int user = Integer.valueOf(bdInstance.getSetting(SettingsID.LOGGED_USER_ID).getValue());
			long period = Long.valueOf(bdInstance.getSetting(SettingsID.OFF_MAP_SENDING_RATE).getValue());
			
			timer = new Timer();
			
			task0 = new TimerTask(){
				public void run(){
					String result = UserWeb.atualizaPosicao(user, appLocationManager.getLatitude(), appLocationManager.getLongitude());
					Log.i(TAG, result);
				}
			};
	        timer.schedule(task0, 0, period);
		}
		
		bdInstance.close();
	}

}
