package com.followme;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.followme.R;
import com.followme.model.SettingDAO;
import com.followme.model.web.GroupWeb;
import com.followme.utils.MarkerList;
import com.followme.utils.RoundedImageView;
import com.followme.utils.location.SendPositionSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

public class MapaActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		OnMyLocationChangeListener {

	// Log tag
    private static final String TAG = MapaActivity.class.getSimpleName();
    
	// images
	private Handler handler = new Handler();

	// location
	private LocationClient mLocationClient;
	final int TEMPO_ATUALIZACAO = 1;
	private TimerTask task0;
	private Timer timer;

	// map
	private GoogleMap map;

	// utilities
	private MarkerList usersList;

	// parameters
	private String idGroup, nome_grupo = null;
	private Boolean atualizaTela = true;
	
	//setting
	private long onMapSendingRate;
	private long offMapSendingRate;

	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);

		// set screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mLocationClient = new LocationClient(this, this, this);
		
		// start setting database
		SettingDAO bdInstance = new SettingDAO(getApplicationContext());
		bdInstance.open();
		onMapSendingRate = Long.valueOf(bdInstance.getSetting("onMapSendingRate").getValue());
		offMapSendingRate = Long.valueOf(bdInstance.getSetting("onMapSendingRate").getValue());
		bdInstance.close();

		// get activity parameters
		try {
			Intent it = getIntent();

			String idGroup = it.getStringExtra("idGroup");
			String nome_grupo = it.getStringExtra("nome_grupo");
			this.idGroup = idGroup;
			this.nome_grupo = nome_grupo;
			getActionBar().setTitle(this.nome_grupo); 

		} catch (Exception erro) {
			Log.e("Script", "erro");
		}

		// Initialize map 
		map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.setTrafficEnabled(true);

		// initialize engine
		initializeEngine();
	}
	
	@Override
	public void onResume(){
		//restart timer
		if(timer != null){
			timer.cancel();
			timer = null;
		} 
		getUsersLocationTask();
		
		SendPositionSingleton.getInstance(getApplicationContext())
			.setPeriod(onMapSendingRate);
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		if (isGooglePlayServicesAvailable()) {
			mLocationClient.connect();
		}

	}
	
	@Override
	public void onDestroy() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		if(timer != null){
			timer.cancel();
			timer = null;
		}
		SendPositionSingleton.getInstance(getApplicationContext())
			.setPeriod(offMapSendingRate);
		super.onStop();
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
		case R.id.menu_settings:
			Intent itSetting = new Intent(this, SettingActivity.class);
			startActivity(itSetting);
		default:
			break;
		}
		return true;

	}

	/**
	 * 
	 * @return
	 */
	private boolean isGooglePlayServicesAvailable() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			return true;
		} else {
			Toast.makeText(getBaseContext(),
					"Google Play services is not available.",
					Toast.LENGTH_SHORT).show();
			return false;
		}

	}
	
	/**
	 * 
	 */
	private void initializeEngine() {
		if (isConnected(getApplicationContext()) == true) {
			
			map.setOnMyLocationChangeListener(this);
			usersList = new MarkerList();
			getUsersLocationTask();
			SendPositionSingleton.getInstance(getApplicationContext())
				.setPeriod(onMapSendingRate);

		} else {
			Toast.makeText(
					getBaseContext(),
					"Verifique sua conexão com a internet.",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	private boolean isConnected(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.isConnected()) {
				return true;
			} else if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.isConnected()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Initialize thread to get users position.
	 */
	private void getUsersLocationTask(){
		timer = new Timer();
		task0 = new TimerTask(){
			public void run(){
				new GetUsersLocationAsyncTask().execute();
			}
		};
        timer.schedule(task0, 0, onMapSendingRate);
	}

	/**
	 * Assemble users markers with pin icon and their photos.
	 * @param patch
	 * @param icon
	 * @param userId
	 * @param latitude
	 * @param longitude
	 */
	private void loadMarker(String patch, Bitmap icon, int userId, double latitude, double longitude) {
		final String param = patch;
		final int id = userId;
		final Bitmap newIcon = icon;
		final double lat = latitude, lng = longitude;

		new Thread() {
			public void run() {
				try {
					URL url = new URL(param);
					HttpURLConnection conexao = (HttpURLConnection) url
							.openConnection();
					InputStream input = conexao.getInputStream();
					Bitmap foto = BitmapFactory.decodeStream(input);

					Bitmap roundFoto = RoundedImageView.getCroppedBitmap(foto,
							100);
					final Bitmap markerIcon = RoundedImageView.getMergedBitmap(newIcon, roundFoto, getBaseContext());

					handler.post(new Runnable() {
						public void run() {

							Marker marker = map.addMarker(new MarkerOptions()
									.position(new LatLng(lat, lng))
									.icon(BitmapDescriptorFactory
											.fromBitmap(markerIcon)));						
							
							usersList.add(id,marker);
						}
					});
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}.start();
	}

	private class GetUsersLocationAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return GroupWeb.getUsersLocation(Integer.parseInt(idGroup));
		}

		protected void onPostExecute(String result) {
			try {
				Log.e(TAG, result);

				JSONArray jArray = new JSONArray(result);

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject obj = jArray.getJSONObject(i);

					try {
						if (usersList.contains(obj.getInt("idUser"))) {
							usersList
								.get(obj.getInt("idUser"))
								.setPosition( new LatLng(obj.getDouble("latitude"),obj.getDouble("longitude")));
						} else {
							int color;
							switch (i) {
							case 0:
								color = R.drawable.azul;
								break;
							case 1:
								color = R.drawable.amarelo;
								break;
							case 2:
								color = R.drawable.preto;
								break;
							case 3:
								color = R.drawable.verde;
								break;
							case 4:
								color = R.drawable.vermelho;
								break;
							case 5:
								color = R.drawable.roxo;
								break;
							default:
								color = R.drawable.laranja;
								break;
							}

							Bitmap imgMarker = BitmapFactory.decodeResource(getResources(), color);
							loadMarker(obj.getString("photo_patch"), imgMarker, obj.getInt("idUser"), obj.getDouble("latitude"),obj.getDouble("longitude"));
							Log.e(TAG, "marcador criado");
						}

						Log.e(TAG, "user data received");
					} catch (Exception e2) {
						Log.e(TAG, e2.getMessage());
					}
				}

			} catch (IndexOutOfBoundsException e1) {
				e1.printStackTrace();

				String erro = getResources().getString(R.string.erro_conexao);

				Toast.makeText(getBaseContext(), erro, Toast.LENGTH_SHORT)
						.show();
				
				Log.e(TAG, "Exception: e1");
			} catch (JSONException e2) {
				e2.printStackTrace();

				String erro = getResources().getString(R.string.erro_conexao);

				Toast.makeText(getBaseContext(), erro, Toast.LENGTH_SHORT)
						.show();

				Log.e(TAG, "Exception: e2");
				
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				Toast.makeText(getBaseContext(), e3.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();

				Log.e(TAG, "Exception: e3");
			}
		}

	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Log.e(TAG, "location services connected");
		Location location = mLocationClient.getLastLocation();
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());

		CameraPosition currentPlace = new CameraPosition.Builder()
				.target(latLng).bearing(location.getBearing()).tilt(65.5f)
				.zoom(17).build();

		map.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Sorry. Location services not available to you",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onMyLocationChange(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			if(atualizaTela){
				float zoom = map.getCameraPosition().zoom;
				
				CameraPosition currentPlace = new CameraPosition.Builder()
						.target(new LatLng(location.getLatitude(), location
								.getLongitude())).bearing(location.getBearing())
						.tilt(65.5f).zoom(zoom).build();
	
				map.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
			}
		}

	}

}
