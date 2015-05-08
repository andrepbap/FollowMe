package com.followme.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import com.followme.model.Usuario;

public class UsuarioDA extends Bd{
	
	public UsuarioDA(Context ctx)
	{
		super(ctx);
	}
	
	// Getting
    public Usuario getUsuario(int id) {
 	    SQLiteDatabase db = mDbHelper.getWritableDatabase();
 	    Cursor cursor = db.query(TABELA_USUARIO, null, ID_USUARIO + "=?", new String[] { String.valueOf(id) }, null, null, null);

	    try{
	 	    cursor.moveToFirst();
		    Usuario usuario = new Usuario(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4), cursor.getInt(5));
		    
		    db.close();
	        return usuario;
	        
	    }catch(CursorIndexOutOfBoundsException e){
	    	return null;
	    }
     }
    
    public Usuario getUsuario() {
 	    SQLiteDatabase db = mDbHelper.getWritableDatabase();
 	    Cursor cursor = db.query(TABELA_USUARIO, null, LOGADO_USUARIO + "=?", new String[] { "1" }, null, null, null);

	    try{
	 	    cursor.moveToFirst();
		    Usuario usuario = new Usuario(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4), cursor.getInt(5));
		    
		    db.close();
	        return usuario;
	        
	    }catch(CursorIndexOutOfBoundsException e){
	    	return null;
	    }
     }
    
    public void gravaUsuario(Usuario usuario) {
		// Verifica se descricao existe no cadastro
    	
 		Usuario usuarioAux;
 		usuarioAux = getUsuario(usuario.getId()); 
		
		// processa dados
    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
       	ContentValues values = new ContentValues();
       	
       	values.put(ID_USUARIO, usuario.getId());	
       	values.put(NOME_USUARIO, usuario.getNome());	
		values.put(NASCIMENTO_USUARIO, usuario.getNascimento());
		values.put(EMAIL_USUARIO, usuario.getEmail());
		values.put(SENHA_USUARIO, usuario.getSenha());
		values.put(LOGADO_USUARIO, usuario.getLogado());
		// Inserting Row
        if (usuarioAux == null)
        {
        	db.insert(TABELA_USUARIO, null, values);
        } else
        {
        	db.update(TABELA_USUARIO, values, ID_USUARIO + " = ?", new String[] { String.valueOf(usuario.getId()) });
        }
        usuarioAux = null;
    	db.close(); // Closing database connection
    }
    
    public void logoffUsuario(){
    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
       	ContentValues values = new ContentValues();
       	
       	values.put(LOGADO_USUARIO, 0);
       	
       	db.update(TABELA_USUARIO, values, LOGADO_USUARIO + " = ?", new String[] { "1" });
    }

    // exclui usuario
    public void delUsuario(String id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABELA_USUARIO, ID_USUARIO + " = ?",
                new String[] { id });
        db.close();
    }

}
