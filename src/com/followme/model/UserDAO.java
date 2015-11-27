package com.followme.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import com.followme.entity.User;

public class UserDAO extends Database{
	
	public UserDAO(Context ctx)
	{
		super(ctx);
	}
	
	// Getting
    public User getUsuario(int id) {
 	    SQLiteDatabase db = mDbHelper.getWritableDatabase();
 	    Cursor cursor = db.query(USER_TABLE, null, USER_ID + "=?", new String[] { String.valueOf(id) }, null, null, null);

	    try{
	 	    cursor.moveToFirst();
		    User usuario = new User(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
		    
		    db.close();
	        return usuario;
	        
	    }catch(CursorIndexOutOfBoundsException e){
	    	return null;
	    }
     }
    
    public void gravaUsuario(User usuario) {
 		User usuarioAux;
 		usuarioAux = getUsuario(usuario.getId()); 
		
    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
       	ContentValues values = new ContentValues();
       	
       	values.put(USER_ID, usuario.getId());	
       	values.put(USER_NAME, usuario.getName());	
		values.put(USER_EMAIL, usuario.getEmail());
		
		// Inserting Row
        if (usuarioAux == null)
        {
        	db.insert(USER_TABLE, null, values);
        } else
        {
        	db.update(USER_TABLE, values, USER_ID + " = ?", new String[] { String.valueOf(usuario.getId()) });
        }
        usuarioAux = null;
    	db.close(); // Closing database connection
    }

    // delete user
    public void delUsuario(String id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(USER_TABLE, USER_ID + " = ?",
                new String[] { id });
        db.close();
    }

}
