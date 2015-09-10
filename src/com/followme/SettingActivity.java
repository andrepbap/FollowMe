package com.followme;

import java.util.ArrayList;
import java.util.List;

import com.followme.location.SendPositionSingleton;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
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
		List<String> options = new ArrayList<String>();
		options.add("10 segundos");
		options.add("30 segundos");
		options.add("1 minuto");
		options.add("10 minutos");
		final ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, options);
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
				//String selected = ((TextView) arg1).getText().toString();
				String selected = aAdapter.getItem(position);
				Toast.makeText(getBaseContext(), selected, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
