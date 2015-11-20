package com.followme;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.followme.entity.Usuario;
import com.followme.model.UsuarioDAO;
import com.followme.model.web.UserWeb;
import com.followme.utils.HttpConnection;
import com.followme.utils.Json;
import com.followme.utils.encryption.Encrypt;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends Activity {

	// Log tag
	private static final String TAG = LoginActivity.class.getSimpleName();
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// selecionarTipoTrafego();
		progress = (ProgressBar) findViewById(R.id.loginProgressBar);
		progress.setVisibility(View.INVISIBLE);
	}

	public void cadastrar(View v) {
		Intent it = new Intent(this, CadastroActivity.class);
		startActivity(it);
	}

	public void entrar(View v) {
		processaLogin();
	}

	private void processaLogin() {

		EditText editEmail = (EditText) findViewById(R.id.emailLogin);
		EditText editSenha = (EditText) findViewById(R.id.senhaLogin);

		String email = editEmail.getText().toString();
		String senha = editSenha.getText().toString();

		if (email.equals("") || senha.equals("")) {
			Toast.makeText(getBaseContext(), "Digite um email e senha.",
					Toast.LENGTH_SHORT).show();
		} else {
			try {
				progress.setVisibility(View.VISIBLE);
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
			UsuarioDAO bd = new UsuarioDAO(getApplicationContext());

			try {
				JSONObject json = new JSONObject(result);

				try {

					Usuario usuario = new Usuario(Integer.parseInt(json
							.getString("idUser")), 
							json.getString("userName"),
							json.getString("birth"),
							json.getString("email"),
							json.getString("password"), 1);

					bd.open();
					bd.gravaUsuario(usuario);
					bd.close();

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

				progress.setVisibility(View.INVISIBLE);
			} catch (Exception e2) {

				e2.printStackTrace();
				Toast.makeText(getBaseContext(), e2.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();

				progress.setVisibility(View.INVISIBLE);
			}
		}
	}
}
