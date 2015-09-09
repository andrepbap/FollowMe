package com.followme;

import com.followme.location.SendPositionSingleton;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingActivity extends Activity {
	
	private CheckBox refreshCB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		refreshCB = (CheckBox) findViewById(R.id.settingRefreshCheckBox);
		
		if(SendPositionSingleton.getInstance(getApplicationContext()).getTimerStatus()){
			refreshCB.setChecked(true);
		}
		else{
			refreshCB.setChecked(false);
		}
	}
	
	public void salvar(View v){
		if(refreshCB.isChecked()){
			SendPositionSingleton.getInstance(getApplicationContext()).start();
			Toast.makeText(getBaseContext(), "Atualização de posição ligada =)", Toast.LENGTH_SHORT).show();
		}
		else{
			SendPositionSingleton.getInstance(getApplicationContext()).stop();
			Toast.makeText(getBaseContext(), "Atualização de posição desligada =(", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void cancelar(View v){
		finish();
	}

}
