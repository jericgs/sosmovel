package br.com.uern.les.sosmovel.controladores;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Erick Gomes on 17/10/2014.
 */
public class ServicoComConexao extends Servico implements FuncoesServico {

    private final IBinder conexao = new LocalBinder();
    private static final String CATEGORIA = "servico";


    // Implementação de IBinder, retorna a interface para interagir com o serviço

    public class LocalBinder extends Binder {
        public FuncoesServico getContador() {
            // Retorna o serviço SimplesBindService para a Activity
            //Chama o metodo do serviço, ex: coun()
            return ServicoComConexao.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(CATEGORIA, "ServicoComConexao.onBind() conexao");
        // Retorna o Binder para a Activity utilizar
        return conexao;
    }
}
