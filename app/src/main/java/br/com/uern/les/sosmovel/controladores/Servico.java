package br.com.uern.les.sosmovel.controladores;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import br.com.uern.les.sosmovel.controladores.FuncoesServico;

/**
 * Created by Erick Gomes on 13/10/2014.
 */
public class Servico extends Service implements Runnable, FuncoesServico {

    public static final String CATEGORIA = "servico";
    private boolean estado;
    //private List<EntidadeContatos> ListaContatos;

    @Override
    public void onCreate(){
        Log.i(CATEGORIA, "Exemplo de serviço onCreate();");
        estado = true;
        new Thread(this).start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(CATEGORIA, "Exemplo onStart();");

    }

    @Override
    public void run() {
        while (estado){
            /*Contatos contatos = new Contatos(this);
            if(ListaContatos == null){
                ListaContatos = contatos.getContatos();
                Log.i(CATEGORIA, "entrou..");
            }*/
            Log.i(CATEGORIA, "rodando.....");
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*@Override
    public List<EntidadeContatos> agenda() {
        Log.i(CATEGORIA, "PASSOU..");
        return ListaContatos;
    }*/
}
