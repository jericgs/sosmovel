package com.example.erick.myapplicationsos;

import android.telephony.SmsManager;

import java.util.ArrayList;

/**
 * Created by Erick on 26/07/2015.
 */
public class SMS {

    private ArrayList<String> listaNumeros = new ArrayList<>();
    private String nonoDigito = "9";

    public String enviar(ArrayList<String> numero, String mensagem){

        for(int i = 0; i < 3; i++){
            StringBuilder stringBuilder = new StringBuilder(numero.get(i).toString());
            stringBuilder.insert(numero.get(i).toString().length() - 8, nonoDigito);
            listaNumeros.add(stringBuilder.toString());
        }

        for(int i = 0; i < 3; i++){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(listaNumeros.get(i).toString(), null, mensagem, null, null);

        }

        return "Enviada com Sucesso";

    }
}
