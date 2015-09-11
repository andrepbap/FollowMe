package com.followme;

import java.util.ArrayList;
import java.util.List;

import com.followme.location.SendPositionSingleton;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingActivity extends Activity {
	
	private CheckBox refreshCB;
	private Spinner timeSpn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		refreshCB = (CheckBox) findViewById(R.id.settingRefreshCheckBox);
		timeSpn = (Spinner) findViewById(R.id.settingTimeSpinner);
		
		//constroi check box
		if(SendPositionSingleton.getInstance(getApplicationContext()).getTimerStatus()){
			refreshCB.setChecked(true);
		}
		else{
			refreshCB.setChecked(false);
		}
		
		//constroi spinner
		List<Period> options = new ArrayList<Period>();
		options.add(new Period("", 0));
		options.add(new Period("3 segundos", 3000));
		options.add(new Period("10 segundos", 10000));
		options.add(new Period("30 segundos", 30000));
		options.add(new Period("1 minuto", 60000));
		options.add(new Period("10 minutos", 600000));
		final ArrayAdapter<Period> aAdapter = new ArrayAdapter<Period>(this, android.R.layout.simple_dropdown_item_1line, options);
		timeSpn.setAdapter(aAdapter);
		
		//refreshCB listener
		refreshCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					SendPositionSingleton.getInstance(getApplicationContext()).start();
					Toast.makeText(getBaseContext(), "Atualização de posição ligada =)", Toast.LENGTH_SHORT).show();
				}
				else{
					SendPositionSingleton.getInstance(getApplicationContext()).stop();
					Toast.makeText(getBaseContext(), "Atualização de posição desligada =(", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		//timeSpn listener
		timeSpn.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Period selected = aAdapter.getItem(position);
				
				SendPositionSingleton.getInstance(getApplicationContext()).setPeriod(selected.getValue());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private class Period{
		
		private String periodText;
		private long value;
		
		public Period(String periodText, long value){
			this.periodText = periodText;
			this.value = value;
		}
		
		public long getValue(){
			return this.value;
		}
		
		public String toString(){
			return this.periodText;
		}
	}

}
