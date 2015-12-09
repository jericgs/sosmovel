package br.com.uern.les.sosmovel.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Erick on 17/07/2015.
 */

public class DBAdapter {

    //Tabela Usuarios
    private static final String NOME_TABELA_USUARIOS = "usuarios";
    public static final String VAR_ID_USUARIO = "idUsuario";
    public static final String VAR_NOME_USUARIO = "nome";
    public static final String VAR_DOENCA_HEREDITARIA = "doencas";

    //Tabela Familiares
    private static final String NOME_TABELA_FAMILIARES = "familiares";
    public static final String VAR_ID_FAMILIAR = "idFamiliar";
    public static final String VAR_ID_USUARIO_FOREIGNKEY = "idTabelaUsuario";
    public static final String VAR_NOME_FAMILIAR = "nome";
    public static final String VAR_NUMERO = "numero";
    public static final String VAR_PARENTESCO = "parentesco";

    //Tabela mensagem button
    private static final String NOME_TABELA_MENSAGENS = "Mensagens";
    public static final String VAR_ID_MENSAGENS = "idMensagem";
    public static final String VAR_MENSAGENS = "mensagem";

    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NOME = "My1"; //10
    private static final int DATA_VERSION = 1;

    private static final String DATABASE_CREATE_TABLE_USUARIOS = "CREATE TABLE IF NOT EXISTS " + NOME_TABELA_USUARIOS + " ( "
            + VAR_ID_USUARIO + " TEXT PRIMARY KEY NOT NULL, "
            + VAR_NOME_USUARIO + " TEXT NOT NULL, "
            + VAR_DOENCA_HEREDITARIA + " TEXT NOT NULL);";

    private static final String DATABASE_CREATE_TABLE_FAMILIARES = "CREATE TABLE IF NOT EXISTS " + NOME_TABELA_FAMILIARES + " ( "
            + VAR_ID_FAMILIAR + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VAR_ID_USUARIO_FOREIGNKEY + " TEXT NOT NULL, "
            + VAR_NOME_FAMILIAR + " TEXT NOT NULL, "
            + VAR_NUMERO + " TEXT NOT NULL, "
            + VAR_PARENTESCO + " TEXT NOT NULL, " +
            "FOREIGN KEY (" + VAR_ID_USUARIO_FOREIGNKEY + ") REFERENCES "
            + NOME_TABELA_USUARIOS + "(" + VAR_ID_USUARIO + ") ON DELETE CASCADE ON UPDATE CASCADE);";

    private static final String DATABASE_CREATE_TABLE_MENSAGEM = "CREATE TABLE IF NOT EXISTS " + NOME_TABELA_MENSAGENS + " ("
            + VAR_ID_MENSAGENS + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VAR_MENSAGENS + " TEXT NOT NULL);";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context){
            super(context, DATABASE_NOME, null, DATA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                db.execSQL(DATABASE_CREATE_TABLE_USUARIOS);
                db.execSQL(DATABASE_CREATE_TABLE_FAMILIARES);
                db.execSQL(DATABASE_CREATE_TABLE_MENSAGEM);

            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.w(TAG, "atualizamdo a base de dados sa versão " + oldVersion + "para a " + newVersion + ", o que destruirá todos os dados antigos");

            db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_USUARIOS);
            db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_FAMILIARES);
            db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_MENSAGENS);
            onCreate(db);

        }
    }

    public DBAdapter abrir()throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void fechar(){
        DBHelper.close();
    }

    public long insertTabelaUsuarios(String idUsuario, String nome, String doenca){ //numero do telefone
        ContentValues iniciarValues = new ContentValues();
        iniciarValues.put(VAR_ID_USUARIO, idUsuario);
        iniciarValues.put(VAR_NOME_USUARIO, nome);
        iniciarValues.put(VAR_DOENCA_HEREDITARIA, doenca);
        return db.insert(NOME_TABELA_USUARIOS, null, iniciarValues);
    }

    public long insertTabelaFamiliares(String idUsuario, String nome, String numero, String parentesco){ //numero do telefone
        ContentValues iniciarValues = new ContentValues();
        iniciarValues.put(VAR_ID_USUARIO_FOREIGNKEY, idUsuario);
        iniciarValues.put(VAR_NOME_FAMILIAR, nome);
        iniciarValues.put(VAR_NUMERO, numero);
        iniciarValues.put(VAR_PARENTESCO, parentesco);
        return  db.insert(NOME_TABELA_FAMILIARES, null, iniciarValues);
    }

    public long insertTabelaMensagens(String mensagem){
        ContentValues iniciarValues = new ContentValues();
        iniciarValues.put(VAR_MENSAGENS, mensagem);
        return  db.insert(NOME_TABELA_MENSAGENS, null, iniciarValues);
    }

    public Cursor getAllUsuarios(){
        return db.query(NOME_TABELA_USUARIOS, new String[]{VAR_ID_USUARIO, VAR_NOME_USUARIO, VAR_DOENCA_HEREDITARIA}, null, null, null, null, null);
    }

    public Cursor getAllFamilares(){
        return db.query(NOME_TABELA_FAMILIARES, new String[]{VAR_ID_FAMILIAR, VAR_ID_USUARIO_FOREIGNKEY, VAR_NOME_FAMILIAR, VAR_NUMERO, VAR_PARENTESCO}, null, null, null, null, null);
    }

    public Cursor getAllMensagens(){
        return db.query(NOME_TABELA_MENSAGENS, new String[]{VAR_ID_MENSAGENS, VAR_MENSAGENS}, null, null, null, null, null);
    }

    public boolean updateMensagem(long idMensagem, String mensagem){
        ContentValues args = new ContentValues();

        if(mensagem.equals("") == false) {
            args.put(VAR_MENSAGENS, mensagem);
            return db.update(NOME_TABELA_MENSAGENS, args, VAR_ID_MENSAGENS + "=" + idMensagem, null) > 0;
        }

        return false;
    }

}
