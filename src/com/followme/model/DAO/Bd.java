package com.followme.model.DAO;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Bd {
	
	// nome da tabela
	public static final String TABELA_USUARIO = "motorista";
	public static final String TABELA_SETTINGS = "settings";
	// campos da tabela
	public static final String ID_USUARIO = "id";
	public static final String NOME_USUARIO = "nome";   
	public static final String NASCIMENTO_USUARIO = "nascimento";  	
	public static final String EMAIL_USUARIO = "email";
	public static final String SENHA_USUARIO = "senha";
	public static final String LOGADO_USUARIO = "logado"; 
	
	public static final String ID_SETTING = "id";
	public static final String ATUALIZA = "atualiza"; 
	public static final String TEXT_PERIOD = "text_period";
	public static final String VALUE_PERIOD = "value_period"; 
	
	private static final String USUARIO_CREATE_TABLE = "CREATE TABLE "
			+ TABELA_USUARIO + "  (" +
										ID_USUARIO + " INTEGER NOT NULL PRIMARY KEY," +
										NOME_USUARIO + " TEXT NOT NULL, " +
										NASCIMENTO_USUARIO + " TEXT NOT NULL,"+
										EMAIL_USUARIO + " TEXT NOT NULL,"+
										SENHA_USUARIO + " TEXT NOT NULL,"+
										LOGADO_USUARIO + " BOOLEAN NOT NULL"+
								  "  );";
	
	private static final String SETTINGS_CREATE_TABLE = "CREATE TABLE "
			+ TABELA_SETTINGS + "  (" +
										ID_SETTING + " INTEGER NOT NULL PRIMARY KEY," +
										ATUALIZA + " BOOLEAN NOT NULL, " +
										TEXT_PERIOD + " TEXT NOT NULL,"+
										VALUE_PERIOD + " LONG NOT NULL"+
								  "  );";
	
	private static final String TAG = "Db";
	protected DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	 
	private static final String DB_NAME = "sigame";
	private static final int DATABASE_VERSION = 1;
	 
	private final Context mCtx;

	public Bd(Context ctx) {
		this.mCtx = ctx;
	}
 
	public Bd open() throws SQLException {
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
	 
			db.execSQL(USUARIO_CREATE_TABLE);
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
				db.execSQL("ALTER TABLE " + TABELA_USUARIO   + " RENAME TO " + TABELA_USUARIO   + "BK");
				db.execSQL("ALTER TABLE " + TABELA_SETTINGS   + " RENAME TO " + TABELA_SETTINGS   + "BK");
			}
			// elimina tabelas
			db.execSQL("DROP TABLE IF EXISTS " + TABELA_USUARIO);
			db.execSQL("DROP TABLE IF EXISTS " + TABELA_SETTINGS);
			// cria novas tabelas
			onCreate(db);
			// Copia dados anteriores para tabelas novas SEMPRE ALTERAR QUANDO MUDAR VERSÇÃO
			if (oldVersion == validLast && validLast > 0)
			{
				db.execSQL("INSERT INTO "+ TABELA_USUARIO + " SELECT " +	ID_USUARIO + "," + 
									NOME_USUARIO + "," +
									NASCIMENTO_USUARIO + "," +
									EMAIL_USUARIO + "," +
									SENHA_USUARIO + "," +
									LOGADO_USUARIO +
					" FROM " + TABELA_USUARIO +"BK");
				db.execSQL("INSERT INTO "+ TABELA_SETTINGS + " SELECT " +	ID_SETTING + "," + 
									ATUALIZA + "," +
									TEXT_PERIOD + "," +
									VALUE_PERIOD + 
					" FROM " + TABELA_SETTINGS +"BK");

			}			
			// Elimina tabelas provisórias utilizadas para manter os dados
			db.execSQL("DROP TABLE IF EXISTS " + TABELA_USUARIO    + "_BK");
			db.execSQL("DROP TABLE IF EXISTS " + TABELA_SETTINGS   + "_BK");
		}
	}
    
}