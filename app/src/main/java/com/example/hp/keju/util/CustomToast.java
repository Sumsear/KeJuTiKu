package com.example.hp.keju.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.keju.R;

public class CustomToast {

    public static void show(Context context, String msg){
        Toast toast = new Toast(context);
        View v = LayoutInflater.from(context).inflate(R.layout.toast_custom,null);
        TextView tvMsg = v.findViewById(R.id.toast_msg);
        tvMsg.setText(msg);
        toast.setView(v);
        toast.show();
    }

}
