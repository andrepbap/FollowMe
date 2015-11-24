package com.followme;

import com.followme.entity.Group;
import com.followme.list.GroupListAdapter;
import com.followme.model.AppSettings;
import com.followme.model.UserDAO;
import com.followme.model.web.UserWeb;
import com.followme.utils.location.SendPositionSingleton;

import java.util.ArrayList;
import java.util.List;
 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
 
    // Group parameters
    private ProgressDialog pDialog;
    private List<Group> grupoList = new ArrayList<Group>();
    private ListView listView;
    private GroupListAdapter adapter;
    
    //Data base
    private UserDAO bd;
    private int id_logado;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // verify login
		bd = new UserDAO(getApplicationContext());
		bd.open();
		if (bd.getUsuario() == null) {
			bd.close();
			Intent itLogin = new Intent(this, LoginActivity.class);
			startActivity(itLogin);
			finish();
		}
		else{ 
			id_logado = bd.getUsuario().getId();
			bd.close();
			
			//start send position singleton
			SendPositionSingleton sps = SendPositionSingleton.getInstance(getApplicationContext());
			sps.setUser(id_logado);
			sps.setPeriod(AppSettings.getAppOffMapSendRate(getApplicationContext()));
			if(AppSettings.isOffMapSending(getApplicationContext())){
				sps.start();
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
	        
	        //click listener
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
    
    private class GetGruposAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			return UserWeb.getGroups(id_logado);
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
