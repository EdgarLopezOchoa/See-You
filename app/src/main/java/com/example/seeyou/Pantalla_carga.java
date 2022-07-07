package com.example.seeyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class Pantalla_carga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                if (preferences.getBoolean("sesion_usuario", false) != false){
                    //         AQUI DEBE DE REDIRECCIONAR AL MAPSFRAGMENT :D
                    Intent intent = new Intent(Pantalla_carga.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }else {
                    //         AQUI DEBE DE REDIRECCIONAR AL MAPSFRAGMENT :D
                    Intent intent = new Intent(Pantalla_carga.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3500);

    }
}