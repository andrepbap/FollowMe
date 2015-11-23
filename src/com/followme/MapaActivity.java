package com.followme;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.followme.R;
import com.followme.model.AppSettings;
import com.followme.model.UsuarioDAO;
import com.followme.model.web.GroupWeb;
import com.followme.model.web.UserWeb;
import com.followme.utils.HttpConnection;
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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
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
    
	// imagens
	private Handler handler = new Handler();

	// location
	private LocationClient mLocationClient;
	final int TEMPO_ATUALIZACAO = 1;
	private TimerTask task0;
	private Timer timer;

	// bd
	private UsuarioDAO bd;

	// mapa
	private GoogleMap map;

	// trajeto
	private MarkerList listUsuarios;
	private int id_logado;

	//parametros
	private String id_grupo, nome_grupo = null;
	private Boolean atualizaTela = true;

	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mLocationClient = new LocationClient(this, this, this);

		//find user
		bd = new UsuarioDAO(getApplicationContext());
		bd.open();
		id_logado = bd.getUsuario().getId();
		bd.close();

		try {
			Intent it = getIntent();

			String id_grupo = it.getStringExtra("id_grupo");
			String nome_grupo = it.getStringExtra("nome_grupo");
			this.id_grupo = id_grupo;
			this.nome_grupo = nome_grupo;
			getActionBar().setTitle(this.nome_grupo); 

		} catch (Exception erro) {
			Log.e("Script", "erro");
		}

		// inicialização do mapa
		map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		map.setMyLocationEnabled(true);
		map.setTrafficEnabled(true);

		if (id_grupo != null) {
			iniciaTrajeto(Integer.parseInt(id_grupo));
			Toast.makeText(getBaseContext(), nome_grupo + " carregado!", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * Called when the Activity becomes visible.
	 */
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
			.setPeriod(AppSettings.getAppOffMapSendRate(getApplicationContext()));
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
	
	private boolean Conectado(Context context) {
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
	
	private void getUsersLocationTask(){
		timer = new Timer();
		
		task0 = new TimerTask(){
			public void run(){
				new GetUsersLocationAsyncTask().execute();
			}
		};
        timer.schedule(task0, 0, AppSettings.getAppMapSendRate(getApplicationContext()));
	}

	private void iniciaTrajeto(int id_grupo) {
		if (Conectado(getApplicationContext()) == true) {
			map.setOnMyLocationChangeListener(this);

			listUsuarios = new MarkerList();
			getUsersLocationTask();
			SendPositionSingleton.getInstance(getApplicationContext())
				.setPeriod(AppSettings.getAppMapSendRate(getApplicationContext()));

		} else {
			Toast.makeText(
					getBaseContext(),
					"Erro ao iniciar o grupo. Verifique sua conexão e tente novamente.",
					Toast.LENGTH_SHORT).show();
		}
	}

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
							
							listUsuarios.add(id,marker);
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
			// TODO Auto-generated method stub
			return GroupWeb.getUsersLocation(Integer.parseInt(id_grupo));
		}

		protected void onPostExecute(String result) {
			try {
				Log.e(TAG, result);

				JSONArray jArray = new JSONArray(result);

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject obj = jArray.getJSONObject(i);

					try {
						if (listUsuarios.contains(obj.getInt("idUser"))) {
							listUsuarios
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

						Log.e("recebimento", "ok!");
					} catch (Exception e2) {
						Log.e("position", e2.getMessage());
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
		Log.e("location", "conectado");
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
