package br.com.uern.les.sosmovel.activitys;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import java.util.ArrayList;

import br.com.uern.les.sosmovel.R;
import br.com.uern.les.sosmovel.controladores.SMS;
import br.com.uern.les.sosmovel.controladores.Servico;
import br.com.uern.les.sosmovel.controladores.ToastManager;
import br.com.uern.les.sosmovel.controladores.TypefaceSpan;
import br.com.uern.les.sosmovel.controladores.DBAdapter;

public class Menu extends ActionBarActivity implements View.OnClickListener, DialogInterface.OnClickListener, LocationListener {

    private Button btMensagem1,btMensagem2, btMensagem3, btOk1, btOk2;
    private DBAdapter banco;
    private long resistroMensagem;
    private String retornoSMS;
    private ArrayList<String> listaMensagens = new ArrayList<String>();
    private ArrayList<String> listaNumeros = new ArrayList<String>();
    private String chave = "";
    private AlertDialog alerta;
    public Context context;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_menu);
        actionBarSetup();

        context = getApplicationContext();
        locationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.i("Dentro do", "IF");
            ToastManager.show(this, "O App S.O.S precisa acessar seu local. Ative o acesso à localização.", ToastManager.INFORMACOES);
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);

        }else{
            Intent intent = new Intent(this, Servico.class);
            startService(intent);
        }

        chave = this.getIntent().getStringExtra("chave");

        if(chave.toString().equalsIgnoreCase("alertaSucesso")){
            int leyout = R.layout.tela_alerta_confirmado;
            int idButton = R.id.buttonOk1;
            alertaDialogo(leyout, idButton);
        }

        if(chave.toString().equalsIgnoreCase("alertaNaoEnviado")){
            int leyout = R.layout.tela_alerta_nao_confirmado;
            int idButton = R.id.buttonOk2;
            alertaDialogo(leyout, idButton);
        }

        banco = new DBAdapter(this);

        banco.abrir();
        Cursor cursor = banco.getAllMensagens();
        if(cursor.moveToNext() == false){

            for(int i = 0; i < 3; i++){
                resistroMensagem = banco.insertTabelaMensagens("Nova Mensagem");
            }
            banco.fechar();
        }

        banco.abrir();
        Cursor mensagensButtons = banco.getAllMensagens();
        if(mensagensButtons.moveToFirst()){
            do{
                addListaMensagem(mensagensButtons);
            }while (mensagensButtons.moveToNext());
        }
        banco.fechar();

        banco.abrir();
        Cursor familares  = banco.getAllFamilares();
        if(familares.moveToNext()){
            do{
                addListaNumeros(familares);
            }while (familares.moveToNext());
        }
        banco.fechar();

        btMensagem1 = (Button)findViewById(R.id.buttonLabel1);
        btMensagem1.setText(listaMensagens.get(0).toString());
        btMensagem1.setTypeface(mudarFonte());
        btMensagem1.setOnClickListener(this);

        btMensagem2 = (Button)findViewById(R.id.buttonLabel2);
        btMensagem2.setText(listaMensagens.get(1).toString());
        btMensagem2.setTypeface(mudarFonte());
        btMensagem2.setOnClickListener(this);

        btMensagem3 = (Button)findViewById(R.id.buttonLabel3);
        btMensagem3.setText(listaMensagens.get(2).toString());
        btMensagem3.setTypeface(mudarFonte());
        btMensagem3.setOnClickListener(this);

        ImageButton btSocorro = (ImageButton)findViewById(R.id.imageButtonSocorro);
        btSocorro.setOnClickListener(this);

    }

    @Override
    public void onResume(){

        context = getApplicationContext();
        locationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.i("Dentro do", "IF");
            ToastManager.show(this, "O App S.O.S precisa acessar seu local. Ative o acesso à localização.", ToastManager.INFORMACOES);
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);

        }else{
            Intent intent = new Intent(this, Servico.class);
            startService(intent);
        }
        super.onResume();

    }


    public void alertaDialogo(int layout, int idButton){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(layout, null);
        view.findViewById(idButton).setOnClickListener(this);
        builder.setCustomTitle(view);
        alerta = builder.create();
        alerta.setCancelable(false);
        alerta.setCanceledOnTouchOutside(false);
        alerta.show();
    }

    public Typeface mudarFonte(){

        return Typeface.createFromAsset(getAssets(), "fonts/Arlrdbd.ttf");
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_item1){

            Intent telaEdicao = new Intent(this, Edicao.class);
            startActivity(telaEdicao);
            finish();
        }

        return super.onOptionsItemSelected(item);
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
    public void onClick(View v) {

        if(v.getId() == R.id.buttonLabel1){

            if(btMensagem1.getText().toString().equalsIgnoreCase("Nova mensagem")){
                Intent telaEdicao = new Intent(this, Edicao.class);
                startActivity(telaEdicao);
                finish();
            }else{
                SMS enviarSMS = new SMS();
                retornoSMS = enviarSMS.enviar(listaNumeros, listaMensagens.get(0).toString());
                ToastManager.show(this, retornoSMS, ToastManager.CONFIRMACOES);
            }

            //Log.i("SMS: ", listaNumeros.toString() + " " + listaMensagens.get(0).toString());
        }

        if(v.getId() == R.id.buttonLabel2){

            if(btMensagem2.getText().toString().equalsIgnoreCase("Nova mensagem")){
                Intent telaEdicao = new Intent(this, Edicao.class);
                startActivity(telaEdicao);
                finish();
            }else{
                SMS enviarSMS = new SMS();
                retornoSMS = enviarSMS.enviar(listaNumeros, listaMensagens.get(1).toString());
                ToastManager.show(this, retornoSMS, ToastManager.CONFIRMACOES);
            }

        }

        if(v.getId() == R.id.buttonLabel3){

            if(btMensagem3.getText().toString().equalsIgnoreCase("Nova mensagem")){
                Intent telaEdicao = new Intent(this, Edicao.class);
                startActivity(telaEdicao);
                finish();
            }else{
                SMS enviarSMS = new SMS();
                retornoSMS = enviarSMS.enviar(listaNumeros, listaMensagens.get(2).toString());
                ToastManager.show(this, retornoSMS, ToastManager.CONFIRMACOES);
            }

        }

        if(v.getId() == R.id.imageButtonSocorro){

            Intent localizacao = new Intent(this, Solicitacao.class);
            localizacao.putExtra("chave", "socorro");
            startActivity(localizacao);
            finish();

        }

        if(v.getId() == R.id.buttonOk1){
            alerta.dismiss();
        }

        if(v.getId() == R.id.buttonOk2){
            alerta.dismiss();
        }

    }

    public void addListaMensagem(Cursor m){
        listaMensagens.add(m.getString(1));
    }

    public void addListaNumeros(Cursor n){
        listaNumeros.add(n.getString(3));
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if(which == -1){
            alerta.dismiss();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}