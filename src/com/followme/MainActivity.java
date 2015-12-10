package com.followme;

import com.followme.entity.Group;
import com.followme.entity.Setting;
import com.followme.list.GroupListAdapter;
import com.followme.model.SettingDAO;
import com.followme.model.SettingsID;
import com.followme.model.web.UserWeb;
import com.followme.utils.RoundedImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	// Log tag
	private static final String TAG = MainActivity.class.getSimpleName();
	
	// default setting parameters
	private static final boolean IS_OFF_MAP_SENDING = true;
	private static final long ON_MAP_SENDING_RATE = 5000;
	private static final long OFF_MAP_SENDING_RATE = 60000;
	
	// settings
	private boolean isOffMapSending;

	// Group parameters
	private ProgressDialog pDialog;
	private List<Group> grupoList = new ArrayList<Group>();
	private ListView listView;
	private GroupListAdapter adapter;

	// Data base
	private SettingDAO bdInstance;
	private int loggedUserId;
	private String loggedUserPhotoPatch;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// create database instance
		bdInstance = new SettingDAO(getApplicationContext());
		
		// verify login
		if (!verifyLoggedUser()) {
			Intent itLogin = new Intent(this, LoginActivity.class);
			startActivity(itLogin);
			finish();
			return;
		}
		
		// verify if settings are initialized
		if(!verifySettings()){
			startDefaultSettingsDatabase();
		}
		
		loadProfilePicture();

		if (isOffMapSending) {
			startService(new Intent(this, SendLocationService.class));
		}

		listView = (ListView) findViewById(R.id.list);
		adapter = new GroupListAdapter(this, grupoList);
		listView.setAdapter(adapter);

		pDialog = new ProgressDialog(this);
		// Showing progress dialog before making http request
		pDialog.setMessage("Carregando grupos...");
		pDialog.show();

		// changing action bar color
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#1b1b1b")));

		new GetGruposAsyncTask().execute();

		// click listener
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Group group = grupoList.get(position);
				String idGroup = Integer.toString(group.getId());
				String gorupName = group.getNome();

				Intent it = new Intent(MainActivity.this, MapaActivity.class);
				it.putExtra("idGroup", idGroup);
				it.putExtra("nome_grupo", gorupName);
				startActivity(it);
			}
		});

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		hidePDialog();
	}

	private void hidePDialog() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_logoff:
			break;
		case R.id.menu_settings:
			Intent itSetting = new Intent(this, SettingActivity.class);
			startActivity(itSetting);
			break;
		}
		return true;
	}
	
	private void loadProfilePicture() {
		final String param = loggedUserPhotoPatch;
		Log.i(TAG, loggedUserPhotoPatch);

		new Thread() {
			public void run() {
				try {
					URL url = new URL(param);
					HttpURLConnection conexao = (HttpURLConnection) url
							.openConnection();
					InputStream input = conexao.getInputStream();
					Bitmap foto = BitmapFactory.decodeStream(input);

					Bitmap profilePicture = RoundedImageView.getCroppedBitmap(foto,
							100);
					Drawable finalPitcture = new BitmapDrawable(getResources(), profilePicture);
					ActionBar actionBar = getActionBar();
					actionBar.setIcon(finalPitcture);

				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}.start();
	}

	private boolean verifyLoggedUser() {
		bdInstance.open();
		if (bdInstance.getSetting(SettingsID.LOGGED_USER_ID) == null) {
			bdInstance.close();
			return false;
		} else {
			loggedUserId = Integer.valueOf(bdInstance.getSetting(SettingsID.LOGGED_USER_ID).getValue());
			loggedUserPhotoPatch = bdInstance.getSetting(SettingsID.LOGGED_USER_PHOTO_PATCH).getValue();
			bdInstance.close();
			return true;
		}
	}
	
	private boolean verifySettings(){
		bdInstance.open();
		if (bdInstance.getSetting(SettingsID.IS_OFF_MAP_SENDING) == null){
			bdInstance.close();
			return false;
		} else {
			isOffMapSending = Boolean.valueOf(bdInstance.getSetting(SettingsID.IS_OFF_MAP_SENDING).getValue());
			bdInstance.close();
			return true;
		}
	}

	private void startDefaultSettingsDatabase() {
		// Attribute static setting to variables
		isOffMapSending = IS_OFF_MAP_SENDING;
		
		SettingDAO bdInstance = new SettingDAO(getApplicationContext());

		Setting isMapSending = new Setting(SettingsID.IS_OFF_MAP_SENDING,
				Boolean.toString(IS_OFF_MAP_SENDING));
		Setting onMapSendingRate = new Setting(SettingsID.ON_MAP_SENDING_RATE,
				Long.toString(ON_MAP_SENDING_RATE));
		Setting offMapSendingRate = new Setting(SettingsID.OFF_MAP_SENDING_RATE,
				Long.toString(OFF_MAP_SENDING_RATE));

		bdInstance.open();
		bdInstance.saveSetting(isMapSending);
		bdInstance.saveSetting(onMapSendingRate);
		bdInstance.saveSetting(offMapSendingRate);
		bdInstance.close();
	}

	private class GetGruposAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return UserWeb.getGroups(loggedUserId);
		}

		protected void onPostExecute(String result) {
			Log.e(TAG, result);
			hidePDialog();

			try {
				JSONArray jArray = new JSONArray(result);

				// Parsing json
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject obj = jArray.getJSONObject(i);

					Group group = new Group(obj.getInt("idGroup"),
							obj.getString("group_name"),
							obj.getString("description"),
							obj.getString("photo-patch"));

					// adding grupo to grupos array
					grupoList.add(group);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// notifying list adapter about data changes
			// so that it renders the list view with updated data
			adapter.notifyDataSetChanged();
		}
	}
}
