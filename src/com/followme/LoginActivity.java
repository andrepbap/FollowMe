package com.followme;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.followme.library.Encrypt;
import com.followme.library.HttpConnection;
import com.followme.model.Usuario;
import com.followme.model.DAO.UsuarioDAO;

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
		
		//selecionarTipoTrafego();
		progress = (ProgressBar) findViewById(R.id.loginProgressBar);
		progress.setVisibility(View.INVISIBLE);
	}
    
    public void cadastrar(View v){
    	Intent it = new Intent(this, CadastroActivity.class);
		startActivity(it);
    }
    
    public void entrar(View v){
    	processaLogin();
    }
    
    //GERA JSON
    private String geraJSON(String email, String senha)
    {
    	JSONObject jo = new JSONObject();
    	try
    	{
    		jo.put("email", email);
    		jo.put("senha",senha);
        		
    	}catch(JSONException e1)
    	{
    		Log.e("Script","erro Json");
    	}
    	return jo.toString();
    }

    
	private void processaLogin() {

		EditText editEmail = (EditText) findViewById(R.id.emailLogin);
		EditText editSenha = (EditText) findViewById(R.id.senhaLogin);

		String email = editEmail.getText().toString();
		String senha = editSenha.getText().toString();
		
		if(email.equals("") || senha.equals("")){
			Toast.makeText(
					getBaseContext(),
					"Digite um email e senha.",
					Toast.LENGTH_SHORT).show();
		}
		else{
			try {
				progress.setVisibility(View.VISIBLE);
				
				String json=geraJSON(email, Encrypt.sha1Hash(senha));
				new ReadJsonAsyncTask().execute(json);
				
			} catch (Exception e) {
			
				e.printStackTrace();
			} 
		}
	}
	
	private class ReadJsonAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String api = getResources().getString(R.string.api_url);
			String url = api + "usuario/login";
			return HttpConnection.getSetDataWeb(url, "send-json", params[0]);//servidor remoto
			
		}

		protected void onPostExecute(String result) {
			Log.e(TAG, result);
			UsuarioDAO bd = new UsuarioDAO(getApplicationContext());
			
			try {
				JSONArray jArray = new JSONArray(result);
				JSONObject obj = jArray.getJSONObject(0);

				try{
					Usuario usuario = new Usuario(Integer.parseInt(obj
							.getString("id_usuario")),
							obj.getString("nome_usuario"),
							obj.getString("nascimento"), 
							obj.getString("email"),
							obj.getString("senha"),
							1);

					bd.open();
					bd.gravaUsuario(usuario);
					bd.close();
				}
				catch(JSONException e)
				{
					if(obj.getString("erro") != null){
						switch (obj.getInt("codigo")) {
						case 1:
							throw new Exception("E-mail ou senha inválidos");
						//
						case 2:
							Toast.makeText(
												getBaseContext(),
												"Seu acesso está bloqueado.",
												Toast.LENGTH_SHORT).show();
							
							
							//
						default:
							throw new Exception("Erro interno da API");
			
						}
					}
				}
				
				Intent it = new Intent(getBaseContext(), MainActivity.class);
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(it);
				finish();
				
			}
			catch(JSONException e1){
				e1.printStackTrace();
				
				String erro = getResources().getString(R.string.erro_conexao);
				
				Toast.makeText(
					getBaseContext(),
					erro,
					Toast.LENGTH_SHORT).show();
				
				progress.setVisibility(View.INVISIBLE);
			}
			catch (Exception e2) {
				
				e2.printStackTrace();
				Toast.makeText(
					getBaseContext(),
					e2.getLocalizedMessage(),
					Toast.LENGTH_SHORT).show();
				
				progress.setVisibility(View.INVISIBLE);
			}
		}
	}
}
