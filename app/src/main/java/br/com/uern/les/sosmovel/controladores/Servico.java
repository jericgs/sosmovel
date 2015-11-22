package br.com.uern.les.sosmovel.controladores;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Erick on 22/11/2015.
 */
public class Servico extends Service implements Runnable, LocationListener {

    public static final String CATEGORIA = "servico";
    private boolean estado;
    private int cont = 0;
    public Context context;
    private LocationManager locationManager;

    @Override
    public void onCreate(){
        super.onCreate();

        context = getApplicationContext();
        locationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);

        estado = true;
        new Thread(this).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "Rodando em segundo plano...", Toast.LENGTH_LONG).show();
        return START_STICKY;

    }

    @Override
    public void onDestroy(){
        Toast.makeText(this, "Serviço destruido...", Toast.LENGTH_LONG).show();
        estado = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {

        while (estado){

            try {
                Thread.sleep(5000);
                cont++;
                Log.i(CATEGORIA, "rodando.....: " + cont);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
