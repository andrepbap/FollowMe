package com.followme;

import android.util.Log;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import com.followme.entity.Setting;
import com.followme.entity.User;
import com.followme.model.SettingDAO;
import com.followme.model.SettingsID;
import com.followme.model.web.UserWeb;
import com.followme.utils.encryption.Encrypt;

public class RegisterActivity extends Activity {

	// Log tag
	private static final String TAG = RegisterActivity.class.getSimpleName();
	
	private AlertDialog alerta;

	private User user;

	private EditText txtName, txtEmail, txtConfirmacaoEmail, txtPassword,
			txtConfirmacaoPassword;
	
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		// back button
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);

		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtConfirmacaoEmail = (EditText) findViewById(R.id.txtEmailConfirmation);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtConfirmacaoPassword = (EditText) findViewById(R.id.txtPasswordConfirmation);
		
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

	public void save(View v) {
		user = new User();

		if (txtName.getText().toString().equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					RegisterActivity.this);
			builder.setTitle("aviso");
			builder.setMessage("Preencher o campo nome");
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface daylog, int which) {
							
						}
					});
			alerta = builder.create();
			alerta.show();
			return;
		}

		if (txtEmail.getText().toString().equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					RegisterActivity.this);
			builder.setTitle("aviso");
			builder.setMessage("Preencher o campo email");
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface daylog, int which) {
						

						}
					});
			alerta = builder.create();
			alerta.show();
			return;
		}
		if (txtConfirmacaoEmail.getText().toString().equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					RegisterActivity.this);
			builder.setTitle("aviso");
			builder.setMessage("Preencher o campo confirme seu e-mail");
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface daylog, int which) {
							

						}
					});
			alerta = builder.create();
			alerta.show();
			return;
		}

		if (!(txtEmail.getText().toString().equals(txtConfirmacaoEmail
				.getText().toString()))) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					RegisterActivity.this);
			builder.setTitle("Titulo");
			builder.setMessage("Emails n�o correspondentes");
			txtConfirmacaoEmail.requestFocus();
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface daylog, int which) {
							

						}
					});
			alerta = builder.create();
			alerta.show();
			return;

		}

		if (txtPassword.getText().toString().equals("")) {
			message("aviso",
					"Preencher o campo senha com no m�nimo 4 caracteres e no m�ximo 16 caracteres");
			return;
		}

		if (txtConfirmacaoPassword.getText().toString().equals("")) {
			message(
					"aviso",
					"Preencher o campo confirme sua senha com no m�nimo 4 caracteres e no m�ximo 16 caracteres");
			return;
		}

		if (!(txtPassword.getText().toString().equals(txtConfirmacaoPassword
				.getText().toString()))) {
			message("aviso", "Passwords n�o correspondentes");
			return;
		}
		
		String senha=txtPassword.getText().toString();
		
		Integer quantidadeCaracteres = senha.length();
		
		if(quantidadeCaracteres<4)
		{
			message("aviso",
					"Password muito curta. A senha deve possuir no m�nimo 4 caracteres");
			return;
		}
		
		
		// set user attributes
		user.setName(txtName.getText().toString());
		user.setEmail(txtEmail.getText().toString());
		user.setPassword(Encrypt.sha1Hash(txtPassword.getText().toString()));

        // Showing progress dialog before making http request
        pDialog.setMessage("Estamos efetuando seu cadastro!");
        pDialog.show();
		
		// call web service
		new ReadJsonAsyncTask().execute();
		
	}

	

	public void message(String titulo, String texto) {
		AlertDialog.Builder caixaAlerta = new AlertDialog.Builder(
				RegisterActivity.this).setMessage(texto).setTitle(titulo)
				.setNeutralButton("ok", null);

		caixaAlerta.show();
	}

	private class ReadJsonAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return UserWeb.register(user.getName(), user.getEmail(), user.getPassword());
		}

		protected void onPostExecute(String result) {
			Log.e(TAG, result);
			SettingDAO bdInstance = new SettingDAO(getApplicationContext());

			try {
				JSONObject json = new JSONObject(result);
				
				Setting loggedUserId = new Setting(SettingsID.LOGGED_USER_ID, json.getString("idUser"));
				Setting loggedUserPassword = new Setting(SettingsID.LOGGED_USER_PASSWORD, json.getString("password"));

				bdInstance.open();
				bdInstance.saveSetting(loggedUserId);
				bdInstance.saveSetting(loggedUserPassword);
				bdInstance.close();

				Intent it = new Intent(getBaseContext(), MainActivity.class);
				startActivity(it);
				finish();
				
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
