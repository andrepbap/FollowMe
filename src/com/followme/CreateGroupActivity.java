package com.followme;

import org.json.JSONException;
import org.json.JSONObject;

import com.followme.model.SettingDAO;
import com.followme.model.SettingsID;
import com.followme.model.web.GroupWeb;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateGroupActivity extends Activity {
	
	// Log tag
	private static final String TAG = CreateGroupActivity.class.getSimpleName();
	
	private ProgressDialog pDialog;
	
	private EditText groupName;
	private EditText groupDescription;
	
	private int loggedUserId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		
		//get logged user
		// create database instance
		SettingDAO bdInstance = new SettingDAO(getApplicationContext());
		bdInstance.open();
		loggedUserId = Integer.valueOf(bdInstance.getSetting(SettingsID.LOGGED_USER_ID).getValue());
		bdInstance.close();
		
		// back button
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		
		groupName = (EditText) findViewById(R.id.editTextAddGroupName);
		groupDescription = (EditText) findViewById(R.id.editTextAddGroupDescription);
		pDialog = new ProgressDialog(this);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	public void createGroup(View v){
		// Showing progress dialog before making http request
        pDialog.setMessage("Criando grupo...");
        pDialog.show();
        new ReadJsonAsyncTask().execute();
	}
	
	private class ReadJsonAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return GroupWeb.createGroup(loggedUserId, groupName.getText().toString(), groupDescription.getText().toString());
		}

		protected void onPostExecute(String result) {
			Log.e(TAG, result);

			try {
				JSONObject json = new JSONObject(result);
				
				Intent it = new Intent(CreateGroupActivity.this, MapaActivity.class);
				it.putExtra("idGroup", json.getString("idGroup"));
				startActivity(it);
				
			} catch (JSONException e1) {
				pDialog.dismiss();
				
				e1.printStackTrace();

				String erro = getResources().getString(R.string.erro_conexao);

				Toast.makeText(getBaseContext(), erro, Toast.LENGTH_SHORT)
						.show();
			} catch (Exception e2) {
			
				pDialog.dismiss();
				
				e2.printStackTrace();
				Toast.makeText(getBaseContext(), e2.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
