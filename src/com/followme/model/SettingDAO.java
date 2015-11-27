package com.followme.model;

import com.followme.entity.Setting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

public class SettingDAO extends Database {
	
	public SettingDAO(Context cx){
		super(cx);
	}
	
	public Setting getSetting(String idSetting){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
 	    Cursor cursor = db.query(SETTING_TABLE, null, ID_SETTING + "=?", new String[] { idSetting }, null, null, null);

	    try{
	 	    cursor.moveToFirst();
	 	   Setting setting = new Setting(cursor.getString(0),cursor.getString(1));
		    
		    db.close();
	        return setting;
	        
	    }catch(CursorIndexOutOfBoundsException e){
	    	return null;
	    }
	}
	
	public void saveSetting(Setting setting){
		Setting settingAux = getSetting(setting.getIdSetting()); 
		
    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
       	ContentValues values = new ContentValues();
       	
       	values.put(ID_SETTING, setting.getIdSetting());	
       	values.put(VALUE, setting.getValue());	
		// Inserting Row
        if (settingAux == null)
        {
        	db.insert(SETTING_TABLE, null, values);
        } else
        {
        	db.update(SETTING_TABLE, values, ID_SETTING + " = ?", new String[] { setting.getIdSetting() });
        }
        settingAux = null;
    	db.close(); // Closing database connection
	}

}
