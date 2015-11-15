package br.com.uern.les.sosmovel.controladores;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Erick on 25/03/2015.
 */
public class InformacaoDeTempo {

    public String getDateTime() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getHora(){

        Calendar calendar = new GregorianCalendar();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        String horario;

        horario = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        horario += ":" + calendar.get(Calendar.MINUTE);
        horario += ":" + calendar.get(Calendar.SECOND);

        //System.out.println("Hora: " + calendar.get(Calendar.HOUR_OF_DAY));
        //System.out.println("Minuto: " + calendar.get(Calendar.MINUTE));
        //System.out.println("Segundo: " + calendar.get(Calendar.SECOND));

        return horario;

    }
}
