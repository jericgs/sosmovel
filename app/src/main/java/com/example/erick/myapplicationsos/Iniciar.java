package com.example.erick.myapplicationsos;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by Erick on 22/07/2015.
 */

public class Iniciar extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBAdapter banco = new DBAdapter(this);

        banco.abrir();
        Cursor cursor = banco.getAllUsuarios();

        if(cursor.moveToNext()){ //verifica se tem usuario no sqlite

            banco.fechar();
            Intent telaMenu = new Intent(this, Menu.class);
            telaMenu.putExtra("chave", "Iniciar");
            startActivity(telaMenu);
            finish();
        }else{
            Intent telaLogin = new Intent(this, Login.class);
            startActivity(telaLogin);
            finish();
        }
    }
}
