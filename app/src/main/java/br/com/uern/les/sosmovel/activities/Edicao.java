package br.com.uern.les.sosmovel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import br.com.uern.les.sosmovel.R;
import br.com.uern.les.sosmovel.controladores.DBAdapter;
import br.com.uern.les.sosmovel.controladores.ToastManager;


public class Edicao extends ActionBarActivity implements View.OnClickListener {

    private EditText editText_mensagem1, editText_mensagem2, editText_mensagem3;
    private Button btSalvar, btCancelar;
    private DBAdapter banco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_edicao_mensagens);
        //actionBarSetup();

        //Tela sempre ativa
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        banco = new DBAdapter(this);

        editText_mensagem1 = (EditText)findViewById(R.id.editTextMensagem1);
        editText_mensagem2 = (EditText)findViewById(R.id.editTextMensagem2);
        editText_mensagem3 = (EditText)findViewById(R.id.editTextMensagem3);

        btSalvar = (Button)findViewById(R.id.buttonSalvar);
        btSalvar.setOnClickListener(this);
        btCancelar = (Button)findViewById(R.id.buttonCancelar);
        btCancelar.setOnClickListener(this);

    }

    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void actionBarSetup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SpannableString s = new SpannableString("  S.O.S - Móvel");
            s.setSpan(new TypefaceSpan(this, "Aero.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            android.support.v7.app.ActionBar ab = getSupportActionBar();
            ab.setTitle(s);
        }
    }*/

    @Override
    public void onBackPressed() {
        Intent telaMenu = new Intent(this, Menu.class);
        telaMenu.putExtra("chave", "edicao");
        startActivity(telaMenu);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.buttonSalvar){
            int cont = 0;

            banco.abrir();
            if(banco.updateMensagem(1, editText_mensagem1.getText().toString())){
                cont++;
            }

            if(banco.updateMensagem(2, editText_mensagem2.getText().toString())){
                cont++;
            }

            if(banco.updateMensagem(3, editText_mensagem3.getText().toString())){
                cont++;
            }

            if(cont > 0){
                ToastManager.show(this, "Atualizado com sucesso", ToastManager.CONFIRMACOES);
                Intent telaMenu = new Intent(this, Menu.class);
                telaMenu.putExtra("chave", "edicao");
                startActivity(telaMenu);
                finish();

            }

            if(cont == 0){
                ToastManager.show(this, "A atualização não foi realizada", ToastManager.INFORMACOES);
                Intent telaMenu = new Intent(this, Menu.class);
                telaMenu.putExtra("chave", "edicao");
                startActivity(telaMenu);
                finish();
            }

            banco.fechar();
        }

        if(v.getId() == R.id.buttonCancelar){
            Intent telaMenu = new Intent(this, Menu.class);
            telaMenu.putExtra("chave", "edicao");
            startActivity(telaMenu);
            finish();
        }

    }
}
