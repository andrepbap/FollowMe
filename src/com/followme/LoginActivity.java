package com.followme;

import org.json.JSONException;
import org.json.JSONObject;

import com.followme.entity.Setting;
import com.followme.model.SettingDAO;
import com.followme.model.SettingsID;
import com.followme.model.web.UserWeb;
import com.followme.utils.encryption.Encrypt;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		pDialog = new ProgressDialog(this);
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
					Setting loggedUserId = new Setting(SettingsID.LOGGED_USER_ID, json.getString("idUser"));
					Setting loggedUserPassword = new Setting(SettingsID.LOGGED_USER_PASSWORD, json.getString("password"));

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
