package br.com.uern.les.sosmovel.activitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;

import br.com.uern.les.sosmovel.R;
import br.com.uern.les.sosmovel.controladores.ToastManager;
import br.com.uern.les.sosmovel.controladores.ConexaoHttpClient;
import br.com.uern.les.sosmovel.controladores.DBAdapter;

/**
 * Created by Erick on 23/07/2015.
 */
public class Login extends Activity implements View.OnClickListener{

    private EditText editTextNumero;
    private Button btEntra, btCadastro;
    private String respostaRetornadaLogin;
    private long resistrosUsuarios,resistrosFamiliares;
    private ProgressDialog pd;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_login);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        editTextNumero = (EditText)findViewById(R.id.editTextNumero);

        btEntra = (Button)findViewById(R.id.btEntra);
        btEntra.setOnClickListener(this);

        btCadastro = (Button) findViewById(R.id.btCadastro);
        btCadastro.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btEntra){

            pd = new ProgressDialog(this);
            pd.setMessage("Localizando...");
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);

            if(editTextNumero.getText().toString().equals("")){

                ToastManager.show(this, "O campo numero está em branco", ToastManager.INFORMACOES);

            }else{

                pd.show();

                ArrayList<NameValuePair> parametrosPostLonginUsuario = new ArrayList<NameValuePair>();
                parametrosPostLonginUsuario.add(new BasicNameValuePair("idUsuario", editTextNumero.getText().toString()));

                try{

                    respostaRetornadaLogin = ConexaoHttpClient.execultaHttpPost(ConexaoHttpClient.enviarSolicitacaoLogin, parametrosPostLonginUsuario);
                    String respostaUsuarioLogin = respostaRetornadaLogin.toString();
                    Log.i("Informação Usuario 1: ", respostaUsuarioLogin);

                    if (respostaUsuarioLogin.contains("#") ){

                        Log.i("Retorno: ", respostaUsuarioLogin);

                        String retornoLogin [] = new String[13];
                        retornoLogin = respostaUsuarioLogin.split("#");

                        DBAdapter db = new DBAdapter(this);
                        db.abrir();
                        resistrosUsuarios = db.insertTabelaUsuarios(retornoLogin[0], retornoLogin[1], retornoLogin[2]);
                        Log.i("Informação", "Número de Resistros USUÁRIOS: " + resistrosUsuarios);
                        resistrosFamiliares = db.insertTabelaFamiliares(retornoLogin[0],retornoLogin[3], retornoLogin[4], retornoLogin[5]);
                        resistrosFamiliares += db.insertTabelaFamiliares(retornoLogin[0],retornoLogin[6], retornoLogin[7], retornoLogin[8]);
                        resistrosFamiliares += db.insertTabelaFamiliares(retornoLogin[0],retornoLogin[9], retornoLogin[10], retornoLogin[11]);
                        Log.i("Informação", "Número de Resistros FAMILIARES: " + resistrosFamiliares);
                        db.fechar();

                        for(int i = 0; i < retornoLogin.length; i++){
                            Log.i("Retorno" + i + " : ", retornoLogin[i]);
                        }


                        final Intent telaMenu = new Intent(this, Menu.class);
                        telaMenu.putExtra("chave", "Login");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {


                                try {
                                    Thread.sleep(1000);
                                    pd.dismiss();
                                    finish();
                                    startActivity(telaMenu);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }


                            }
                        }).start();

                    } else{
                        ToastManager.show(this, "Usuário não está cadastrado", ToastManager.INFORMACOES);
                        pd.dismiss();
                    }

                }catch (Exception erro){
                    Log.i("erro","erro = "+erro);
                    ToastManager.show(this, "Erro: Servido não encontrado, verifique sua conexão " + erro.toString(), ToastManager.ERROS);
                    pd.dismiss();

                }
            }
        }

        if(v.getId() == R.id.btCadastro){
            Intent telaCadastro = new Intent(this, Cadastro.class);
            startActivity(telaCadastro);
            finish();
        }

    }
}
