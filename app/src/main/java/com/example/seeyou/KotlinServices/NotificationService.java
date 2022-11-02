package com.example.seeyou.KotlinServices;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.MainActivity;
import com.example.seeyou.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NotificationService extends Service {
    SharedPreferences preferences;
    public static boolean isServiceEnable = false;
    SharedPreferences.Editor editor;
    ArrayList<Integer> pressedkeys = new ArrayList<>();
    RequestQueue requestQueue = null;

    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        isServiceEnable = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int Flags, int startId) {
        isServiceEnable = true;
        ejecutar();
        return START_STICKY;
    }

    public void onDestroy() {
        Intent startservice1 = new Intent(NotificationService.this, NotificationService.class);

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

           startService(startservice1);
        //}

    }

    public void onResume() {
        ejecutar();
    }

    public void onPause() {
        onResume();
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

       pressedkeys.add(keyCode);

        if(event.isLongPress())
            if(pressedkeys.contains(24) && pressedkeys.contains(25)) {

                EnviarAlerta();

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        pressedkeys.removeAll(Collections.singleton(keyCode));
        return false;
    }*/


    private void ejecutar() {

        final Handler handler2 = new Handler();

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {

                BuscarAlertas();
                handler2.postDelayed(this, 10000);
            }
        }, 10000);

    }

    private void BuscarAlertas() {
        isServiceEnable = true;
        preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://mifolderdeproyectos.online/SEEYOU/Notificaciones_Alertas.php?iduser=" + preferences.getInt("id", 0)
                        + "&idnotifi=" + preferences.getInt("idNotificacion", 0), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                    JSONArray array = new JSONArray(response);

                    int notifi = 2;

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);

                        Notificar(notifi, cajas.getString("nombre") + " "
                                + cajas.getString("apellido"), cajas.getInt("notificacion"));

                        notifi = notifi + 1;


                        if (i + 1 == array.length()) {
                            preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("idNotificacion", cajas.getInt("notificacion"));
                            editor.commit();

                        }
                    }


                } catch (JSONException e) {


                }
            }
        },
                new Response.ErrorListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(NotificationService.this);

        } else {
            requestQueue.add(stringRequest);
            ;
        }
    }


    public void Notificar(int notID, String Nombre, int id_notifi) {
        NotificationCompat.Builder creador;
        String canalID = "MiCanal01";
        NotificationManager notificador = (NotificationManager) getSystemService(NotificationService.this.NOTIFICATION_SERVICE);
        // Si nuestro dispositivo tiene Android 8 (API 26, Oreo) o superior
        creador = new NotificationCompat.Builder(NotificationService.this, canalID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String canalNombre = "ALERTA";
            String canalDescribe = Nombre + " NECESITA TU AYUDA!!!!";
            int importancia = NotificationManager.IMPORTANCE_MAX;
            @SuppressLint("WrongConstant")
            NotificationChannel miCanal = new NotificationChannel(canalID, canalNombre, importancia);
            miCanal.setDescription(canalDescribe);
            miCanal.enableLights(true);
            miCanal.setLightColor(Color.BLUE); // Esto no lo soportan todos los dispositivos
            miCanal.enableVibration(true);
            notificador.createNotificationChannel(miCanal);

        }
        Bitmap iconoNotifica = BitmapFactory.decodeResource(getResources(), R.mipmap.emergency);
        int iconoSmall = R.mipmap.icon_round;
        creador.setSmallIcon(iconoSmall);
        creador.setLargeIcon(iconoNotifica);
        creador.setContentTitle("ALERTA!!");
        creador.setContentText(Nombre + " NECESITA TU AYUDA!!!!");
        creador.setStyle(new NotificationCompat.BigTextStyle().bigText(Nombre + " NECESITA TU AYUDA!!!!"));
        creador.setChannelId(canalID);
        notificador.notify(notID, creador.build());
    }

}

class BootDeviceReceiversNotification extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
