package com.followme;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.followme.utils.location.SendPositionSingleton;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
	private JSONObject settings;
	private boolean refresh;
	private Period period;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		try {
			JSONObject settings = readSettings();
			Log.e("settings", settings.toString());
		} catch(Exception e){

		}

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);

		refreshCB = (CheckBox) findViewById(R.id.settingRefreshCheckBox);
		timeSpn = (Spinner) findViewById(R.id.settingTimeSpinner);

		// constroi check box
		if (SendPositionSingleton.getInstance(getApplicationContext())
				.getTimerStatus()) {
			refreshCB.setChecked(true);
		} else {
			refreshCB.setChecked(false);
		}

		// constroi spinner
		List<Period> options = new ArrayList<Period>();
		options.add(new Period("", 0));
		options.add(new Period("3 segundos", 3000));
		options.add(new Period("10 segundos", 10000));
		options.add(new Period("30 segundos", 30000));
		options.add(new Period("1 minuto", 60000));
		options.add(new Period("10 minutos", 600000));
		final ArrayAdapter<Period> aAdapter = new ArrayAdapter<Period>(this,
				android.R.layout.simple_dropdown_item_1line, options);
		timeSpn.setAdapter(aAdapter);

		// refreshCB listener
		refreshCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				refresh = isChecked;
				// if (isChecked) {
				// SendPositionSingleton.getInstance(getApplicationContext())
				// .start();
				// Toast.makeText(getBaseContext(),
				// "Atualização de posição ligada =)",
				// Toast.LENGTH_SHORT).show();
				// } else {
				// SendPositionSingleton.getInstance(getApplicationContext())
				// .stop();
				// Toast.makeText(getBaseContext(),
				// "Atualização de posição desligada =(",
				// Toast.LENGTH_SHORT).show();
				// }
			}
		});

		// timeSpn listener
		timeSpn.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				period = aAdapter.getItem(position);

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
		storeSettings();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; goto parent activity.
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private JSONObject readSettings() {
		String FILENAME = "settings";
		String settings = null;

		getApplicationContext();
		FileInputStream fis;
		try {
			fis = openFileInput(FILENAME);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();
			settings = sb.toString();

			fis.close();
			
			return new JSONObject(settings);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void storeSettings() {
		settings = new JSONObject();

		try {
			settings.put("period", Boolean.toString(refresh));
			settings.put("rate", period.getValue());

			String FILENAME = "settings";

			getApplicationContext();
			FileOutputStream fos = openFileOutput(FILENAME,
					Context.MODE_PRIVATE);

			fos.write(settings.toString().getBytes());
			fos.close();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class Period {

		private String periodText;
		private long value;

		public Period(String periodText, long value) {
			this.periodText = periodText;
			this.value = value;
		}

		public long getValue() {
			return this.value;
		}

		public String toString() {
			return this.periodText;
		}
	}

}
