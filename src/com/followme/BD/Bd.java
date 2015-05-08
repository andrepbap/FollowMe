package com.followme.BD;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Bd {
	
	// nome da tabela
	public static final String TABELA_USUARIO = "motorista";
	// campos da tabela
	public static final String ID_USUARIO = "id";
	public static final String NOME_USUARIO = "nome";   
	public static final String NASCIMENTO_USUARIO = "nascimento";  	
	public static final String EMAIL_USUARIO = "email";
	public static final String SENHA_USUARIO = "senha";
	public static final String LOGADO_USUARIO = "logado"; 
	
	private static final String USUARIO_CREATE_TABLE = "CREATE TABLE "
			+ TABELA_USUARIO + "  (" +
										ID_USUARIO + " INTEGER NOT NULL PRIMARY KEY," +
										NOME_USUARIO + " TEXT NOT NULL, " +
										NASCIMENTO_USUARIO + " TEXT NOT NULL,"+
										EMAIL_USUARIO + " TEXT NOT NULL,"+
										SENHA_USUARIO + " TEXT NOT NULL,"+
										LOGADO_USUARIO + " BOOLEAN NOT NULL"+
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
			}
			// elimina tabelas
			db.execSQL("DROP TABLE IF EXISTS " + TABELA_USUARIO);
			// cria novas tabelas
			onCreate(db);
			// Copia dados anteriores para tabelas novas SEMPRE ALTERAR QUANDO MUDAR VERSï¿½O
			if (oldVersion == validLast && validLast > 0)
			{
				db.execSQL("INSERT INTO "+ TABELA_USUARIO + " SELECT " +	ID_USUARIO + "," + 
																			NOME_USUARIO + "," +
																			NASCIMENTO_USUARIO + "," +
																			EMAIL_USUARIO + "," +
																			SENHA_USUARIO + "," +
																			LOGADO_USUARIO +
															" FROM " + TABELA_USUARIO +"BK");

			}			
			// Elimina tabelas provisï¿½rias utilizadas para manter os dados
			db.execSQL("DROP TABLE IF EXISTS " + TABELA_USUARIO    + "_BK");
		}
	}
    
}