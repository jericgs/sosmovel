package br.com.uern.les.sosmovel.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.uern.les.sosmovel.R;
import br.com.uern.les.sosmovel.controladores.ConexaoHttpClient;
import br.com.uern.les.sosmovel.controladores.DBAdapter;
import br.com.uern.les.sosmovel.controladores.InformacaoDeTempo;
import br.com.uern.les.sosmovel.controladores.ToastManager;

public class Solicitacao extends ActionBarActivity implements LocationListener{

    private SupportMapFragment mapFrag;
    private GoogleMap map;
    private LocationManager locationManager;
    private boolean permitirNetwork;
    private int camPosition = 0;
    private List<String> geoLocalizacao = new ArrayList<>();
    private AlertDialog alerta;
    private String chave;
    private String respostaRetornada = null;
    private DBAdapter banco;
    private String idUsuario;
    private int cont = 0;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_gps);

        //Tela sempre ativa
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //actionBarSetup();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        pd = new ProgressDialog(this);
        pd.setMessage("Localizando...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        pd.show();

        banco = new DBAdapter(this);

        banco.abrir();
        Cursor usuarios = banco.getAllUsuarios();
        if(usuarios.moveToFirst()){
            do{
                addUsuario(usuarios);
            }while (usuarios.moveToNext());
        }
        banco.fechar();

        chave = this.getIntent().getStringExtra("chave");

        GoogleMapOptions options = new GoogleMapOptions();
        options.zOrderOnTop(true);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment1);
        map = mapFrag.getMap();

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
    public void onResume(){
        super.onResume();

        permitirNetwork = true;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent it = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(it);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        locationManager.removeUpdates(this);
    }


    public void congifLocalizacao(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).bearing(0).tilt(90).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);

        Log.i("Informação", "Passou...1");

        map.setMyLocationEnabled(true);
        if(camPosition <= 1){
            map.moveCamera(update);
            camPosition++;
        }

        MinhaLocalizacao minhaLocalizacao = new MinhaLocalizacao();
        map.setLocationSource(minhaLocalizacao);
        minhaLocalizacao.setLocalizacao(latLng);
        getLocalizacao(latLng);
        Log.i("Latitude", String.valueOf(latLng.latitude));
        Log.i("Longitude", String.valueOf(latLng.longitude));

    }

    public void getLocalizacao(LatLng latLng){
        Geocoder gc = new Geocoder(Solicitacao.this);

        List<Address> addressList;

        try {

            addressList = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
            geoLocalizacao.add(addressList.get(0).getThoroughfare());
            geoLocalizacao.add(addressList.get(0).getLocality());
            geoLocalizacao.add(addressList.get(0).getAdminArea());
            geoLocalizacao.add(addressList.get(0).getCountryCode());
            geoLocalizacao.add(Double.toString(latLng.latitude));
            geoLocalizacao.add(Double.toString(latLng.longitude));

            cont++;

            Log.i("Informação", "Passou...2");

            if((geoLocalizacao != null) && (cont == 2)){

                enviarSolicitacao();
            }

        } catch (IOException e) {

            Log.i("Informação", e.getMessage());
            ToastManager.show(this, "Sem conexão!", ToastManager.INFORMACOES);
            finish();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location.getProvider().equals(LocationManager.GPS_PROVIDER)){
            permitirNetwork = false;
        }

        if(permitirNetwork || location.getProvider().equals(LocationManager.GPS_PROVIDER)){
            congifLocalizacao(new LatLng(location.getLatitude(), location.getLongitude()));
        }

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

    public void enviarSolicitacao() {

        if(chave.equalsIgnoreCase("socorro")){

            try {
                InformacaoDeTempo tempo = new InformacaoDeTempo();

                //Post
                ArrayList<NameValuePair> parametrosPost = new ArrayList<NameValuePair>();
                parametrosPost.add(new BasicNameValuePair("idSolicitacao", "null"));
                parametrosPost.add(new BasicNameValuePair("idUsuario", idUsuario));
                parametrosPost.add(new BasicNameValuePair("logradouro", geoLocalizacao.get(0).toString()));
                parametrosPost.add(new BasicNameValuePair("cidade", geoLocalizacao.get(1).toString()));
                parametrosPost.add(new BasicNameValuePair("estado", geoLocalizacao.get(2).toString()));
                parametrosPost.add(new BasicNameValuePair("pais", geoLocalizacao.get(3).toString()));
                parametrosPost.add(new BasicNameValuePair("dataSolicitacao", tempo.getDateTime()));
                parametrosPost.add(new BasicNameValuePair("hora", tempo.getHora()));
                parametrosPost.add(new BasicNameValuePair("latitude", geoLocalizacao.get(4).toString()));
                parametrosPost.add(new BasicNameValuePair("longitude", geoLocalizacao.get(5).toString()));



                respostaRetornada = ConexaoHttpClient.execultaHttpPost(ConexaoHttpClient.enviarSolicitacaoDeSocorro, parametrosPost);
                String resposta = respostaRetornada.toString();
                Log.i("Informação 1: ", resposta);
                resposta = resposta.replaceAll("\\s+", "");
                Log.i("Informação 2: ", resposta);

                if (resposta.contains("1")){
                    Intent telaMenu = new Intent(this, Menu.class);
                    telaMenu.putExtra("chave", "alertaSucesso");
                    startActivity(telaMenu);
                    pd.dismiss();
                    finish();
                    //ToastManager.show(this, "Solicitação Enviada", ToastManager.CONFIRMACOES);
                    //pd.dismiss();
                    //finish();
                } else if (resposta.contains("0")) {
                    Intent telaMenu = new Intent(this, Menu.class);
                    telaMenu.putExtra("chave", "alertaNaoEnviado");
                    startActivity(telaMenu);
                    pd.dismiss();
                    finish();
                    //ToastManager.show(this, "Solicitação não foi enviada", ToastManager.INFORMACOES);
                    //pd.dismiss();
                    //finish();
                }

            }catch(Exception erro){
                pd.dismiss();
                Log.i("erro", "erro = " + erro);
                ToastManager.show(this, "Erro: " + erro.getMessage(), ToastManager.ERROS);
                Intent telaMenu = new Intent(this, Menu.class);
                telaMenu.putExtra("chave", "Iniciar");
                startActivity(telaMenu);
            }

        }

    }

    public class MinhaLocalizacao implements LocationSource {
        private OnLocationChangedListener PontoListener = null;

        @Override
        public void activate(OnLocationChangedListener PontoListener) {
            this.PontoListener = PontoListener;
        }

        @Override
        public void deactivate() {

        }

        public void setLocalizacao(LatLng latLng){
            Location localizacao = new Location(LocationManager.GPS_PROVIDER);
            localizacao.setLatitude(latLng.latitude);
            localizacao.setLongitude(latLng.longitude);

            if(PontoListener != null){
                PontoListener.onLocationChanged(localizacao);
            }
        }
    }

    public void addUsuario(Cursor u){
        idUsuario = u.getString(0);
    }

}