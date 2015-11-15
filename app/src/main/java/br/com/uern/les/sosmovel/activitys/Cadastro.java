package br.com.uern.les.sosmovel.activitys;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import br.com.uern.les.sosmovel.controladores.ConexaoHttpClient;
import br.com.uern.les.sosmovel.controladores.DBAdapter;
import br.com.uern.les.sosmovel.R;
import br.com.uern.les.sosmovel.controladores.ToastManager;
import br.com.uern.les.sosmovel.controladores.TypefaceSpan;

/**
 * Created by Erick on 17/07/2015.
 */
public class Cadastro extends ActionBarActivity implements View.OnClickListener{

    private EditText nomeUsuario, numeroUsuario, doencasUsuario;
    private EditText nomeFamiliar1, numeroFamiliar1, parentescoFamiliar1;
    private EditText nomeFamiliar2, numeroFamiliar2, parentescoFamiliar2;
    private EditText nomeFamiliar3, numeroFamiliar3, parentescoFamiliar3;
    private Button btCadastro;

    private long resistrosUsuarios,resistrosFamiliares;
    private String respostaRetornadaUsuario = null, respostaRetornadaFamiliar1 = null, respostaRetornadaFamiliar2 = null, respostaRetornadaFamiliar3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_cadastro);

        actionBarSetup();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        nomeUsuario = (EditText)findViewById(R.id.editTextNomeUsuario);
        numeroUsuario = (EditText)findViewById(R.id.editTextNumeroUsuario);
        doencasUsuario = (EditText)findViewById(R.id.editTextDoencasUsuario);

        nomeFamiliar1 = (EditText)findViewById(R.id.editTextNomeFamiliar1);
        numeroFamiliar1 = (EditText)findViewById(R.id.editTextNumeroFamiliar1);
        parentescoFamiliar1 = (EditText)findViewById(R.id.editTextParentescoFamiliar1);

        nomeFamiliar2 = (EditText)findViewById(R.id.editTextNomeFamiliar2);
        numeroFamiliar2 = (EditText)findViewById(R.id.editTextNumeroFamiliar2);
        parentescoFamiliar2 = (EditText)findViewById(R.id.editTextParentescoFamiliar2);

        nomeFamiliar3 = (EditText)findViewById(R.id.editTextNomeFamiliar3);
        numeroFamiliar3 = (EditText)findViewById(R.id.editTextNumeroFamiliar3);
        parentescoFamiliar3 = (EditText)findViewById(R.id.editTextParentescoFamiliar3);

        btCadastro = (Button)findViewById(R.id.buttonCadastro);
        btCadastro.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void actionBarSetup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SpannableString s = new SpannableString("  S.O.S - Móvel");
            s.setSpan(new TypefaceSpan(this, "Aero.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            android.support.v7.app.ActionBar ab = getSupportActionBar();
            ab.setTitle(s);


        }
    }

    @Override
    public void onBackPressed() {
        Intent telaLogin = new Intent(this, Login.class);
        startActivity(telaLogin);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.buttonCadastro){

            if((numeroUsuario.getText().toString().equals("")) || (nomeUsuario.getText().toString().equals("")) || (doencasUsuario.getText().toString().equals("")) ||
               (nomeFamiliar1.getText().toString().equals("")) || (numeroFamiliar1.getText().toString().equals("")) || (parentescoFamiliar1.getText().toString().equals("")) ||
               (nomeFamiliar2.getText().toString().equals("")) || (nomeFamiliar2.getText().toString().equals("")) || (parentescoFamiliar2.getText().toString().equals("")) ||
               (nomeFamiliar3.getText().toString().equals("")) || (numeroFamiliar3.getText().toString().equals("")) || (parentescoFamiliar3.getText().toString().equals(""))){

                ToastManager.show(this, "Preencha todos os campos", ToastManager.INFORMACOES);

            }else{

                List<String> listaCadastro = new ArrayList<String>();

                listaCadastro.add(numeroUsuario.getText().toString());
                listaCadastro.add(nomeUsuario.getText().toString());
                listaCadastro.add(doencasUsuario.getText().toString());

                listaCadastro.add(nomeFamiliar1.getText().toString());
                listaCadastro.add(numeroFamiliar1.getText().toString());
                listaCadastro.add(parentescoFamiliar1.getText().toString());

                listaCadastro.add(nomeFamiliar2.getText().toString());
                listaCadastro.add(numeroFamiliar2.getText().toString());
                listaCadastro.add(parentescoFamiliar2.getText().toString());

                listaCadastro.add(nomeFamiliar3.getText().toString());
                listaCadastro.add(numeroFamiliar3.getText().toString());
                listaCadastro.add(parentescoFamiliar3.getText().toString());

                //Realizar cadastro
                ArrayList<NameValuePair> parametrosPostUsuario = new ArrayList<NameValuePair>();
                parametrosPostUsuario.add(new BasicNameValuePair("idUsuario", listaCadastro.get(0).toString()));
                parametrosPostUsuario.add(new BasicNameValuePair("nome", listaCadastro.get(1).toString()));
                parametrosPostUsuario.add(new BasicNameValuePair("doencas_hereditarias", listaCadastro.get(2).toString()));

                ArrayList<NameValuePair> parametrosPostFamiliar1 = new ArrayList<NameValuePair>();
                parametrosPostFamiliar1.add(new BasicNameValuePair("idFamiliar", "null"));
                parametrosPostFamiliar1.add(new BasicNameValuePair("idUsuario", listaCadastro.get(0).toString()));
                parametrosPostFamiliar1.add(new BasicNameValuePair("nome", listaCadastro.get(3).toString()));
                parametrosPostFamiliar1.add(new BasicNameValuePair("numero", listaCadastro.get(4).toString()));
                parametrosPostFamiliar1.add(new BasicNameValuePair("parentesco", listaCadastro.get(5).toString()));

                ArrayList<NameValuePair> parametrosPostFamiliar2 = new ArrayList<NameValuePair>();
                parametrosPostFamiliar2.add(new BasicNameValuePair("idFamiliar", "null"));
                parametrosPostFamiliar2.add(new BasicNameValuePair("idUsuario", listaCadastro.get(0).toString()));
                parametrosPostFamiliar2.add(new BasicNameValuePair("nome", listaCadastro.get(6).toString()));
                parametrosPostFamiliar2.add(new BasicNameValuePair("numero", listaCadastro.get(7).toString()));
                parametrosPostFamiliar2.add(new BasicNameValuePair("parentesco", listaCadastro.get(8).toString()));

                ArrayList<NameValuePair> parametrosPostFamiliar3 = new ArrayList<NameValuePair>();
                parametrosPostFamiliar3.add(new BasicNameValuePair("idFamiliar", "null"));
                parametrosPostFamiliar3.add(new BasicNameValuePair("idUsuario", listaCadastro.get(0).toString()));
                parametrosPostFamiliar3.add(new BasicNameValuePair("nome", listaCadastro.get(9).toString()));
                parametrosPostFamiliar3.add(new BasicNameValuePair("numero", listaCadastro.get(10).toString()));
                parametrosPostFamiliar3.add(new BasicNameValuePair("parentesco", listaCadastro.get(11).toString()));


                try {
                    respostaRetornadaUsuario = ConexaoHttpClient.execultaHttpPost(ConexaoHttpClient.enviarUsuario, parametrosPostUsuario);
                    respostaRetornadaFamiliar1 = ConexaoHttpClient.execultaHttpPost(ConexaoHttpClient.enviarFamiliar, parametrosPostFamiliar1);
                    respostaRetornadaFamiliar2 = ConexaoHttpClient.execultaHttpPost(ConexaoHttpClient.enviarFamiliar, parametrosPostFamiliar2);
                    respostaRetornadaFamiliar3 = ConexaoHttpClient.execultaHttpPost(ConexaoHttpClient.enviarFamiliar, parametrosPostFamiliar3);

                    String respostaUsuario = respostaRetornadaUsuario.toString();
                    String respostaFamiliar1 = respostaRetornadaFamiliar1.toString();
                    String respostaFamiliar2 = respostaRetornadaFamiliar2.toString();
                    String respostaFamiliar3 = respostaRetornadaFamiliar3.toString();

                    Log.i("Informação Usuario 1: ", respostaUsuario);
                    Log.i("Informação Familiar1 1: ", respostaFamiliar1);
                    Log.i("Informação Familiar2 1: ", respostaFamiliar2);
                    Log.i("Informação Familiar3 1: ", respostaFamiliar3);

                    respostaUsuario = respostaUsuario.replaceAll("\\s+", "");
                    respostaFamiliar1 = respostaFamiliar1.replaceAll("\\s+", "");
                    respostaFamiliar2 = respostaFamiliar2.replaceAll("\\s+", "");
                    respostaFamiliar3 = respostaFamiliar3.replaceAll("\\s+", "");

                    Log.i("Informação Usuario 2: ", respostaUsuario);
                    Log.i("Informação Familiar1 2: ", respostaFamiliar1);
                    Log.i("Informação Familiar2 2: ", respostaFamiliar2);
                    Log.i("Informação Familiar3 2: ", respostaFamiliar3);

                    DBAdapter db = new DBAdapter(this);
                    db.abrir();
                    resistrosUsuarios = db.insertTabelaUsuarios(listaCadastro.get(0).toString(), listaCadastro.get(1).toString(), listaCadastro.get(2).toString());
                    Log.i("Informação", "Número de Resistros USUÁRIOS: " + resistrosUsuarios);
                    resistrosFamiliares = db.insertTabelaFamiliares(listaCadastro.get(0).toString(), listaCadastro.get(3).toString(), listaCadastro.get(4).toString(), listaCadastro.get(5).toString());
                    resistrosFamiliares += db.insertTabelaFamiliares(listaCadastro.get(0).toString(), listaCadastro.get(6).toString(), listaCadastro.get(7).toString(), listaCadastro.get(8).toString());
                    resistrosFamiliares += db.insertTabelaFamiliares(listaCadastro.get(0).toString(), listaCadastro.get(9).toString(), listaCadastro.get(10).toString(), listaCadastro.get(11).toString());
                    Log.i("Informação", "Número de Resistros FAMILIARES: " + resistrosFamiliares);
                    db.fechar();

                    if (respostaUsuario.contains("1") && respostaFamiliar1.contains("1") && respostaFamiliar2.contains("1") && respostaFamiliar3.contains("1")){
                        ToastManager.show(this, "Cadastrado com sucesso", ToastManager.CONFIRMACOES);
                        Intent telaLogin = new Intent(this, Login.class);
                        startActivity(telaLogin);
                        finish();
                    } else if (respostaUsuario.contains("0") && respostaFamiliar1.contains("0") && respostaFamiliar2.contains("0") && respostaFamiliar3.contains("0")) {
                        ToastManager.show(this, "Erro ao gravar", ToastManager.INFORMACOES);
                    }

                }catch(Exception erro){
                    Log.i("erro","erro = "+erro);
                    ToastManager.show(this, "Erro: Servido não encontrado, verifique sua conexão " + erro.toString(), ToastManager.ERROS);
                }
            }
        }
    }
}
