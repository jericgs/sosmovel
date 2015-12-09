package br.com.uern.les.sosmovel.controllers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.uern.les.sosmovel.R;

public class ToastManager {

    public static final int INFORMACOES = 0;
    public static final int CONFIRMACOES = 1;
    public static final int ERROS = 2;


    public static void show(Context context, String text,int toastType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.tela_toast, null);

        TextView tv = (TextView) layout.findViewById(R.id.tvTexto);
        tv.setText(text);

        LinearLayout llRoot = (LinearLayout) layout.findViewById(R.id.llRoot);

        Drawable img = null;
        int bg = 0;

        switch (toastType) {
            case CONFIRMACOES:
                img = context.getResources().getDrawable(R.drawable.ic_toast_confirmacao);
                bg  = R.drawable.background_toast;
                break;
            case ERROS:
                img = context.getResources().getDrawable(R.drawable.ic_toast_erro);
                bg  = R.drawable.background_toast;
                break;
            default:
                img = context.getResources().getDrawable(R.drawable.ic_toast_informacao);
                bg  = R.drawable.background_toast;
                break;
        }

        tv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        llRoot.setBackgroundResource(bg);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 320);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
