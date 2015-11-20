package com.followme;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.followme.R;
import com.followme.library.HttpConnection;
import com.followme.library.MarkerList;
import com.followme.library.RoundedImageView;
import com.followme.model.DAO.UsuarioDAO;
import com.followme.model.web.UserWeb;
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

	// my location
	private LocationClient mLocationClient;
	private int flagAtualizacao;
	final int TEMPO_ATUALIZACAO = 1;

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

		//mantem tela
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mLocationClient = new LocationClient(this, this, this);

		//recupera usuario
		bd = new UsuarioDAO(getApplicationContext());
		bd.open();
		id_logado = bd.getUsuario().getId();
		bd.close();

		// tenta setar os dados da MainListCarregarGrupoTrajetoActivity
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

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
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

	// --------------------------------------------funcões de controle de
	// trajeto--------------------------------------------

	private void iniciaTrajeto(int id_grupo) {
		if (Conectado(getApplicationContext()) == true) {

			flagAtualizacao = 0;
			map.setOnMyLocationChangeListener(this);

			// carrega grupo
			listUsuarios = new MarkerList();
			new GetPosicoesAsyncTask().execute();

		} else {
			Toast.makeText(
					getBaseContext(),
					"Erro ao iniciar o grupo. Verifique sua conexão e tente novamente.",
					Toast.LENGTH_SHORT).show();
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

	private String generateSendJSON(double lat, double lng) {
		JSONObject jo = new JSONObject();
		String chave = getResources().getString(R.string.api_key);
		try {
			jo.put("api_key", chave);
			jo.put("usuario", id_logado);
			jo.put("lat", lat);
			jo.put("lng", lng);

			// data
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentDateandTime = sdf.format(new Date());

			jo.put("data", currentDateandTime);

		} catch (JSONException e1) {
			Log.e("Script", "erro Json");
		}
		return jo.toString();
	}

	// --------------------------------------------acesso a web
	// services--------------------------------------------

	private void loadMarker(String patch, Bitmap icone, int userId, double latitude, double longitude) {
		
		final String param = patch;
		final int id = userId;
		final Bitmap icon = icone;
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
					final Bitmap markerIcon = RoundedImageView.getMergedBitmap(icon, roundFoto, getBaseContext());

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

	private class PutPosiAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String api = getResources().getString(R.string.api_url);
			String url = api + "usuario/put-posi";
			Log.e(TAG, url);
			return HttpConnection.getSetDataWeb(url, params[0]);
		}

		protected void onPostExecute(String result) {
			Log.e(TAG, result);
			try {
				JSONArray jArray = new JSONArray(result);
				JSONObject obj = jArray.getJSONObject(0);

				Log.e(TAG, "Envio: " + obj.getString("sucesso"));
			} catch (IndexOutOfBoundsException e1) {
				e1.printStackTrace();

				String erro = getResources().getString(R.string.erro_conexao);

				Toast.makeText(getBaseContext(), erro, Toast.LENGTH_SHORT)
						.show();
			} catch (JSONException e2) {
				e2.printStackTrace();

				String erro = getResources().getString(R.string.erro_conexao);

				Toast.makeText(getBaseContext(), erro, Toast.LENGTH_SHORT)
						.show();

			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				Toast.makeText(getBaseContext(), e3.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();

			}
		}
	}

	/*
	 * Chamada pelo onMyLocationChange faz chamada ao servidor para pegar a
	 * última posição do motorista. Caso motorista não esteja na arrayList
	 * listMotorista, adiciona, se não, atualiza a posição
	 */
	private class GetPosicoesAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			return UserWeb.getPosicoes(Integer.parseInt(id_grupo));
		}

		protected void onPostExecute(String result) {
			try {
				Log.e(TAG, result);

				JSONArray jArray = new JSONArray(result);

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject obj = jArray.getJSONObject(i);

					try {
						// se o motorista já está contido no trajeto, atualiza
						// posição, se não, insere.
						if (listUsuarios.contains(obj.getInt("id_usuario"))) {
							listUsuarios
								.get(obj.getInt("id_usuario"))
								.setPosition( new LatLng(obj.getDouble("latitude"),obj.getDouble("longitude")));
						} else {
							// define cor do marcador
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
							//Bitmap imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.dancingbanana);

							loadMarker(obj.getString("foto_patch"), imgMarker, obj.getInt("id_usuario"), obj.getDouble("latitude"),obj.getDouble("longitude"));
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

	// --------------------------------------------métodos das
	// interfaces--------------------------------------------

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

			// faz envio de coordenadas somente se Accuracy for menor que 100m
			if (location.getAccuracy() < 100) {
				flagAtualizacao++;

				if (flagAtualizacao == TEMPO_ATUALIZACAO) {
					// envia localização
					String sendJson = generateSendJSON(location.getLatitude(), location.getLongitude());
					new PutPosiAsyncTask().execute(sendJson);

					// recebe localização
					new GetPosicoesAsyncTask().execute();
					flagAtualizacao = 0;
				}
			}

		}

	}

}
