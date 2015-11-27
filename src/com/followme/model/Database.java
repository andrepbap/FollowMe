package com.followme.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Database {
	
	// table name
	public static final String USER_TABLE = "user";
	public static final String SETTING_TABLE = "setting";
	
	// table fields
	public static final String USER_ID = "id";
	public static final String USER_NAME = "nome";   
	public static final String USER_EMAIL = "email"; 
	
	public static final String ID_SETTING = "id";
	public static final String VALUE = "value"; 
	
	private static final String USER_CREATE_TABLE = "CREATE TABLE "
			+ USER_TABLE + "  (" +
										USER_ID + " INTEGER NOT NULL PRIMARY KEY," +
										USER_NAME + " TEXT NOT NULL, " +
										USER_EMAIL + " TEXT NOT NULL"+
								  "  );";
	
	private static final String SETTINGS_CREATE_TABLE = "CREATE TABLE "
			+ SETTING_TABLE + "  (" +
										ID_SETTING + " STRING NOT NULL PRIMARY KEY," +
										VALUE + " STRING NOT NULL" +
								  "  );";
	
	private static final String TAG = "Db";
	protected DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	 
	private static final String DB_NAME = "sigame";
	private static final int DATABASE_VERSION = 1;
	 
	private final Context mCtx;

	public Database(Context ctx) {
		this.mCtx = ctx;
	}
 
	public Database open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
 
	public void close() {
		mDbHelper.close();
                mDb.close();
	}	
	
	protected static class DatabaseHelper extends SQLiteOpenHelper {
		 
		
		@Override
		  public void onOpen(SQLiteDatabase db)
		  {
		    super.onOpen(db);
		    if (!db.isReadOnly())
		    {
		      db.execSQL("PRAGMA foreign_keys=ON;");
		    }
		  }
	 
	 
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DATABASE_VERSION);
		}
	 
		@Override
		public void onCreate(SQLiteDatabase db) {
	 
			db.execSQL(USER_CREATE_TABLE);
			db.execSQL(SETTINGS_CREATE_TABLE);
			Log.w("DbAdapter","DB criado com sucesso!");
		}
	 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Atualizando o banco de dados da versão " + oldVersion + " para " + newVersion);
			// Renomeia tabelas
			int validLast = newVersion - 1;
			if (oldVersion == validLast && validLast > 0)
			{
				db.execSQL("ALTER TABLE " + USER_TABLE   + " RENAME TO " + USER_TABLE   + "BK");
				db.execSQL("ALTER TABLE " + SETTING_TABLE   + " RENAME TO " + SETTING_TABLE   + "BK");
			}
			// elimina tabelas
			db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + SETTING_TABLE);
			// cria novas tabelas
			onCreate(db);
			// Copia dados anteriores para tabelas novas SEMPRE ALTERAR QUANDO MUDAR VERSÃO
			if (oldVersion == validLast && validLast > 0)
			{
				db.execSQL("INSERT INTO "+ USER_TABLE + " SELECT " +	USER_ID + "," + 
									USER_NAME + "," +
									USER_EMAIL + 
					" FROM " + USER_TABLE +"BK");
				db.execSQL("INSERT INTO "+ SETTING_TABLE + " SELECT " +	ID_SETTING + "," + 
									VALUE +
					" FROM " + SETTING_TABLE +"BK");

			}			
			// Elimina tabelas provisórias utilizadas para manter os dados
			db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE    + "_BK");
			db.execSQL("DROP TABLE IF EXISTS " + SETTING_TABLE   + "_BK");
		}
	}
    
}