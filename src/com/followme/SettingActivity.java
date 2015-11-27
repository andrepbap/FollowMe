package com.followme;

import java.util.ArrayList;
import java.util.List;

import com.followme.entity.Setting;
import com.followme.model.SettingDAO;
import com.followme.model.SettingsID;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

public class SettingActivity extends Activity {

	private CheckBox refreshCB;
	private Spinner timeSpn;
	private Spinner timeMapSpn;
	private boolean isOffMapSending;
	private Period offMapPeriod;
	private Period onMapPeriod;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		//open database
		SettingDAO bdInstance = new SettingDAO(getApplicationContext());
		bdInstance.open();
		
		// Initialize variables
		isOffMapSending = Boolean.valueOf(bdInstance.getSetting(SettingsID.IS_OFF_MAP_SENDING).getValue());
		offMapPeriod = new Period(Long.valueOf(bdInstance.getSetting(SettingsID.OFF_MAP_SENDING_RATE).getValue()));
		onMapPeriod = new Period(Long.valueOf(bdInstance.getSetting(SettingsID.ON_MAP_SENDING_RATE).getValue()));
		
		// close database
		bdInstance.close();

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);

		refreshCB = (CheckBox) findViewById(R.id.settingRefreshCheckBox);
		timeSpn = (Spinner) findViewById(R.id.settingTimeSpinner);
		timeMapSpn = (Spinner) findViewById(R.id.settingMapTimeSpinner);

		// Build check box
		if (isOffMapSending) {
			refreshCB.setChecked(true);
		} else {
			refreshCB.setChecked(false);
		}
		// refreshCB listener
		refreshCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isOffMapSending = isChecked;
			}
		});

		// Build off map spinner
		List<Period> options = new ArrayList<Period>();
		options.add(offMapPeriod);
		options.add(new Period(3000));
		options.add(new Period(10000));
		options.add(new Period(30000));
		options.add(new Period(60000));
		options.add(new Period(600000));
		final ArrayAdapter<Period> aAdapter = new ArrayAdapter<Period>(this,
				android.R.layout.simple_dropdown_item_1line, options);
		timeSpn.setAdapter(aAdapter);

		// timeSpn listener
		timeSpn.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				offMapPeriod = aAdapter.getItem(position);

				// SendPositionSingleton.getInstance(getApplicationContext()).setPeriod(selected.getValue());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// Build on map spinner
		List<Period> mapOptions = new ArrayList<Period>();
		mapOptions.add(onMapPeriod);
		mapOptions.add(new Period(3000));
		mapOptions.add(new Period(5000));
		mapOptions.add(new Period(10000));
		mapOptions.add(new Period(60000));
		final ArrayAdapter<Period> aMapAdapter = new ArrayAdapter<Period>(this,
				android.R.layout.simple_dropdown_item_1line, mapOptions);
		timeMapSpn.setAdapter(aMapAdapter);

		// timeSpn listener
		timeMapSpn.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				onMapPeriod = aMapAdapter.getItem(position);

				// SendPositionSingleton.getInstance(getApplicationContext()).setPeriod(selected.getValue());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onDestroy() {
		//save settings
		SettingDAO bdInstance = new SettingDAO(getApplicationContext());
		
		Setting isOffMapSending = new Setting(SettingsID.IS_OFF_MAP_SENDING, Boolean.toString(this.isOffMapSending));
		Setting onMapSendingRate = new Setting(SettingsID.ON_MAP_SENDING_RATE, Long.toString(onMapPeriod.value));
		Setting offMapSendingRate = new Setting(SettingsID.OFF_MAP_SENDING_RATE, Long.toString(offMapPeriod.value));
		
		bdInstance.open();
		bdInstance.saveSetting(isOffMapSending);
		bdInstance.saveSetting(onMapSendingRate);
		bdInstance.saveSetting(offMapSendingRate);
		bdInstance.close();
		
		//change settings
		Intent SendLocationIntent = new Intent(this, SendLocationService.class);
		stopService(SendLocationIntent);
		startService(SendLocationIntent);
		
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// application icon in action bar clicked; goto parent activity.
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class Period {
		private long value;

		public Period(long value) {
			this.value = value;
		}

		public String toString() {
			if (this.value < 60000) {
				return Long.toString(this.value / 1000) + " segundos";
			} else if (this.value == 60000) {
				return "1 minuto";
			} else {
				return Long.toString(this.value / 60000) + " minutos";
			}

		}
	}

}
