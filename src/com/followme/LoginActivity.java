package com.followme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.followme.entity.Setting;
import com.followme.model.SettingDAO;
import com.followme.model.SettingsID;
import com.followme.model.web.UserWeb;
import com.followme.utils.encryption.Encrypt;

import android.R.array;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	// Log tag
	private static final String TAG = LoginActivity.class.getSimpleName();

	private ProgressDialog pDialog;

	// Facebook
	LoginButton loginButton;
	CallbackManager callbackManager;
	GraphRequestAsyncTask graphRequest;
	private ProfileTracker profileTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(this.getApplicationContext());
		setContentView(R.layout.activity_login);

		// Facebook login
		loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setReadPermissions(Arrays.asList("public_profile,email"));
		callbackManager = CallbackManager.Factory.create();

		loginButton.registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						// App code
						GraphRequest request = GraphRequest.newMeRequest(
								loginResult.getAccessToken(),
								new GraphRequest.GraphJSONObjectCallback() {
									@Override
									public void onCompleted(JSONObject object,
											GraphResponse response) {
										// Application code
										Log.i(TAG,
												response.toString());
									}
								});
						Bundle parameters = new Bundle();
						parameters.putString("fields",
								"id,name,email");
						request.setParameters(parameters);
						request.executeAsync();

					}

					@Override
					public void onCancel() {
						// App code
					}

					@Override
					public void onError(FacebookException exception) {
						// App code
					}
				});

		// profileTracker = new ProfileTracker() {
		// @Override
		// protected void onCurrentProfileChanged(
		// Profile oldProfile,
		// Profile currentProfile) {
		// // App code
		// String id = currentProfile.getId();
		// Log.i(TAG, id);
		// }
		// };

		pDialog = new ProgressDialog(this);
	}

	// Facebook
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	public void register(View v) {
		Intent it = new Intent(this, RegisterActivity.class);
		startActivity(it);
	}

	public void entrar(View v) {
		processLogin();
	}

	private void processLogin() {

		EditText editEmail = (EditText) findViewById(R.id.emailLogin);
		EditText editSenha = (EditText) findViewById(R.id.senhaLogin);

		String email = editEmail.getText().toString();
		String senha = editSenha.getText().toString();

		if (email.equals("") || senha.equals("")) {
			Toast.makeText(getBaseContext(), "Digite um email e senha.",
					Toast.LENGTH_SHORT).show();
		} else {
			try {
				// Showing progress dialog before making http request
				pDialog.setMessage("Efetuando login...");
				pDialog.show();

				new ReadJsonAsyncTask().execute(email, Encrypt.sha1Hash(senha));

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	private class ReadJsonAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return UserWeb.login(params[0], params[1]);
		}

		protected void onPostExecute(String result) {
			Log.e(TAG, result);
			SettingDAO bdInstance = new SettingDAO(getApplicationContext());

			try {
				JSONObject json = new JSONObject(result);

				try {
					Setting loggedUserId = new Setting(
							SettingsID.LOGGED_USER_ID, json.getString("idUser"));
					Setting loggedUserPassword = new Setting(
							SettingsID.LOGGED_USER_PASSWORD,
							json.getString("password"));

					bdInstance.open();
					bdInstance.saveSetting(loggedUserPassword);
					bdInstance.saveSetting(loggedUserId);
					bdInstance.close();

					Intent it = new Intent(getBaseContext(), MainActivity.class);
					it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(it);
					finish();

				} catch (Exception e0) {
					Toast.makeText(getBaseContext(), json.getString("erro"),
							Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e1) {
				e1.printStackTrace();

				String erro = getResources().getString(R.string.erro_conexao);

				Toast.makeText(getBaseContext(), erro, Toast.LENGTH_SHORT)
						.show();

				pDialog.dismiss();
			} catch (Exception e2) {

				e2.printStackTrace();
				Toast.makeText(getBaseContext(), e2.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();

				pDialog.dismiss();
			}
		}
	}
}
