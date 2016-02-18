package br.com.uern.les.sosmovel.controllers;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by Erick on 22/11/2015.
 */
public class Servico extends IntentService implements LocationListener, SensorEventListener {

    // valor usado como limite de aceleração/impacto para detectar acidentes. Valores acima disto
    // irão disparar o alerta de acidente
    public final static float ACELERAÇÃO_LIMITE = 5.0f;

    // objeto que gerencia o acesso aos dados do GPS
    private LocationManager locationManager;
    // objeto que armazena a latitude (GPS)
    private double latitude;
    // objeto que armazena a longitude (GPS)
    private double longitude;
    // objeto que gerencia o acesso aos sensores
    private SensorManager mSensorManager;
    // objetos que gerenciam o acesso aos dados de sensores específicos
    private Sensor acelerometro, magnometro, aceleraçãoLinear;

    /**
     * Construtor da classe.
     * @param name nome do serviço.
     */
    public Servico(String name) {
        super(name);
    }

    /**
     * Construtor da classe.
     */
    public Servico(){
        super("ActivityRecognitionIntentService");
//        Log.i("Oi", "Eu sou Goku");
    }

    /**
     * Método chamado pelo Android ao criar o serviço. Aqui são instanciados os objetos que
     * aessarão os sensores.
     */
    @Override
    public void onCreate(){
        // chamando o construtor da superclasse (necessário, por padrão)
        super.onCreate();
        // obtendo uma instância do gerenciador do GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // solicitando ao regenciador que esta classe obtenha atualizações constantes do GPS
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this);

        // obtendo acesso ao gerenciador dos sensores
        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        // verificando se o acelerômetro está presente
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            // obtendo acesso ao acelerômetro
            acelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // registrando esta classe como Listener do acelerômetro, recebendo atualizações constantes
            mSensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
            // imprimindo no log que achei um acelerômetro
            Log.d("Acelerômetro", "Acelerômetro presente.");
        }
        else{
            // Sorry, there are no accelerometers on your device.
            Log.e("Acelerômetro", "Acelerômetro não encontrado.");
        }
        // verifncando se o magnetômetro está presente
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            // obtendo acesso ao magnetômetro
            magnometro = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            // registrando a classe como listener das atualizações do magnetômetro
            mSensorManager.registerListener(this, magnometro, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("Magnetômetro", "Magnetômetro presente.");
        }
        else{
            // Sorry, there are no magnetometers on your device.
            Log.e("Magnetômetro", "Magnetômetro não encontrado.");
        }
        // verificando se há sensor de aceleração linear (acelerômetro MENOS gravidade) presente
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            // obtendo acesso ao sensor
            aceleraçãoLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            // registrando a classe como listener das atualizações
            mSensorManager.registerListener(this, aceleraçãoLinear, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("Aceleração linear", "Aceleração linear presente.");
        }
        else{
            // Sorry, there are no linear accelerometers on your device.
            Log.e("Aceleração linear", "Aceleração linear não encontrado.");
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "Rodando em segundo plano...", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        Toast.makeText(this, "Serviço destruido...", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
//        Log.d("Informação", "Latitude: " + latitude + "\n Longitude: " + longitude);
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



    // ################################# DETECÇÃO DE IMPACTO ####################################

    /* O que falta fazer:
    todo: enviar o alerta com detalhes (queda, batida, capotamento, etc) para a base web
     */

    // objeto que armazena dados do acelerômetro (vetor [X, Y, Z])
    float[] mGravity;
    // objeto que armazena dados do magnetômetro (vetor [X, Y, Z])
    float[] mGeomagnetic;
    // objeto que armazena dados da aceleração linear (vetor [X, Y, Z])
    float[] linearAcceleration;
    // objetos usados para calcular a rotação do celular
    float azimut, pitch, roll; // eixos Z, X, Y

    /**
     * Método chamado pelo Android em intervalos quase fixos para informar ao app
     * sobre alterações nos valores dos sensores observados.
     * @param sensorEvent contém os dados alterados e a descrição do sensor que gerou o evento.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // verificando qual sensor disparou o método de atualização
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){ // se foi o acelerômetro
            mGravity = sensorEvent.values.clone(); // salvo os valores
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){ // se foi o magnetômetro
            mGeomagnetic = sensorEvent.values.clone(); // salvo os valores
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){ // se foi a aceleração linear
            linearAcceleration = sensorEvent.values.clone(); // salvo os valores
        }
        // tentando determinar a rotação do aparelho nos três eixos com base nos dados do
        // acelerômetro e magnetômetro.
        if (mGravity != null && mGeomagnetic != null) { // os dados do acelerômetro e magnetômetro não podem estar nulos para este cálculo
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // eixo Z
                pitch = orientation[1]; // eixo X
                roll = orientation[2]; // eixo Y
                // adicionando a posição do celular na janela de dados
                janelaGiroscopio.add(new Ponto3D(azimut, pitch, roll));
                if (janelaGiroscopio.size() > 200){
                    janelaGiroscopio.remove(0); // removendo o valor excedente mais antigo
                }
            }
        }

        // verifico se houve acidente
        checarStatus();
    }

    /**
     * Método chamado pelo Android sempre que houver mudança na resolução (precisão) dos dados dos sensores
     * @param sensor Descrição do sensor
     * @param i nova resolução
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i){

    }

    ArrayList<Ponto3D> janelaGiroscopio = new ArrayList<>();
    /**
     * Verifica o array onde o histórico do giroscópio está para avaliar se houve rotação
     * ou não no aparelho (analizando apenas o eixo Y).
     * @return
     */
    private boolean houveRotação(){
        if (janelaGiroscopio.get(janelaGiroscopio.size() - 1).getY() < 0.){
            for (Ponto3D ponto : janelaGiroscopio){
                if (ponto.getY() > 0.){
                    return true;
                }
            }
        } else {
            for (Ponto3D ponto : janelaGiroscopio){
                if (ponto.getY() < 0.){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Avalia os dados obtidos dos sensores para determinar se houve acidentes.
     */
    private void checarStatus(){
        // só posso prosseguir na avaliação se eu já possuir dados de todos os sensores
        if (linearAcceleration == null || magnometro == null || mGeomagnetic == null){
            return;
        }
        // verificando a aceleração linear. Valores maiores que 5ms
        if (linearAcceleration[0] > ACELERAÇÃO_LIMITE || linearAcceleration[1] > ACELERAÇÃO_LIMITE || linearAcceleration[2] > ACELERAÇÃO_LIMITE){
            Log.i("INFO", "Colisão detectada");
            // zerando os valores de aceleração, pra evitar detecção dupla
            linearAcceleration[0] = 0.f;
            linearAcceleration[1] = 0.f;
            linearAcceleration[2] = 0.f;
            Toast.makeText(Servico.this, "Impacto detectado.", Toast.LENGTH_LONG).show();
            if (atividadeAtual == DetectedActivity.STILL){
                Log.i("Alerta lançado", "Provável desmaio");
            } else if (atividadeAtual == DetectedActivity.WALKING){
                Log.i("Alerta lançado", "Provável queda");
            } else if (atividadeAtual == DetectedActivity.RUNNING){
                Log.i("Alerta lançado", "Provável queda");
            } else if (atividadeAtual == DetectedActivity.ON_BICYCLE){
                Log.i("Alerta lançado", "Provável queda");
            } else if (atividadeAtual == DetectedActivity.IN_VEHICLE){
                if (houveRotação()){
                    Log.i("Alerta lançado", "Provável capotagem");
                } else {
                    Log.i("Alerta lançado", "Provável batida");
                }
            } else{
                Log.i("Alerta lançado", "impacto não identificado");
            }
        }
    }

    private static final String TAG = Servico.class.getSimpleName();

    // ########################## MÉTODOS DE DETECÇÃO DE ATIVIDADE #############################

    private int atividadeAtual = DetectedActivity.STILL; // atividade default
    /**
     * Traduz os códigos de descrição da atividade para nomes legíveis.
     * @param type código da atividade
     * @return nome da atividade
     */
    private String getActivityName(int type) {
        switch (type) {
            case DetectedActivity.IN_VEHICLE: // batida ou capotamento (rotação)
                return "In Vehicle";
            case DetectedActivity.ON_BICYCLE: // queda
                return "On Bicycle";
            case DetectedActivity.ON_FOOT: // tropeçou e caiu
                return "On Foot";
            case DetectedActivity.WALKING: // tropeçou e caiu
                return "Walking";
            case DetectedActivity.STILL: // desmaio
                return "Still";
            case DetectedActivity.TILTING:
                return "Tilting";
            case DetectedActivity.RUNNING: // tropeçou e caiu
                return "Running";
            case DetectedActivity.UNKNOWN:
                return "Unknown";
        }
        return "N/A";
    }

    /**
     * Método que trata os intents recebidos pelo serviço. Neste caso, é utilizado para receber os
     * dados da detecção de atividade, que são lançadas pela API como intents.
     * @param intent intent recebido.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // se o intent é da API de reconhecimento de atividades...
        if(ActivityRecognitionResult.hasResult(intent)) {
            //Extract the result from the Response
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity detectedActivity = result.getMostProbableActivity();

            //Get the Confidence and Name of Activity
            int confidence = detectedActivity.getConfidence();
            String mostProbableName = getActivityName(detectedActivity.getType());

            // salvando a atividade atual na minha variável local
            atividadeAtual = detectedActivity.getType();

            //Fire the intent with activity name & confidence
            Intent i = new Intent("ImActive");
            i.putExtra("activity", mostProbableName);
            i.putExtra("confidence", confidence);

            Log.d(TAG, "Most Probable Name : " + mostProbableName);
            Log.d(TAG, "Confidence : " + confidence);

            //Send Broadcast to be listen in MainActivity
            this.sendBroadcast(i);
        }else {
            Log.d(TAG, "Intent had no data returned");
        }
    }

}
