package com.example.seeyou;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.KotlinServices.LocationService;
import com.example.seeyou.KotlinServices.NotificationService;
import com.example.seeyou.KotlinServices.RoutesService;
import com.example.seeyou.adapters.Markers;
import com.example.seeyou.services.ServiceLocation;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    RutasFragment RutasFragment = new RutasFragment();
    MapsFragment mapsFragment = new MapsFragment();
    PerfilFragment perfilFragment = new PerfilFragment();
    int alerta = 0;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static ConstraintLayout constraintLayout;
    int id_usuario = 0, alertaubicacionactual = 0;
    double longitud, latitud;
    long tiempoEnMS = 0;
    SweetAlertDialog pDialog;
    public static int bucle = 0;
    ArrayList<Integer> pressedkeys = new ArrayList<>();

    private com.google.android.gms.location.LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 sec */
    private long FASTEST_INTERVAL = 3000; /* 3 sec */
    private float distance_interval = 15.0F;


    //private com.google.android.gms.location.LocationRequest RUTA_mLocationRequest;
    //private long RUTA_INTERVAL = 10 * 6000; /* 60 sec */
    //private long RUTA_FASTEST_INTERVAL = 3000; /* 3 sec */


    SweetAlertDialog Eliminar_Marcador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,ServiceLocation.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                2*2000,pendingIntent);*/

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ServiceLocation.class));
        }*/
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(false);

        Intent intent = new Intent(this, LocationService.class);
        Intent intent2 = new Intent(this, RoutesService.class);
        Intent intent3 = new Intent(this, NotificationService.class);
        if (LocationService.Companion.isServiceStarted() == false) {
            startService(intent);
        }
        if (RoutesService.Companion.isServiceStarted() == false) {
            startService(intent2);
        }
        if (NotificationService.isServiceEnable == false) {
            startService(intent3);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        constraintLayout = findViewById(R.id.Fondobottomnavigation);

        preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        id_usuario = preferences.getInt("id", 0);

        if (preferences.getBoolean("fondo2", false) == true) {


            constraintLayout.setBackgroundResource(R.drawable.fondonaranaja2);
            system(2);

        } else if (preferences.getBoolean("fondo", false) == true) {


            constraintLayout.setBackgroundResource(R.drawable.fondodegradado);
            system(1);
        } else if (preferences.getBoolean("fondo3", false) == true) {

            constraintLayout.setBackgroundResource(R.drawable.fondodegradado3);
            system(3);
        } else if (preferences.getBoolean("fondo4", false) == true) {

            constraintLayout.setBackgroundResource(R.drawable.fondodegradado4);
            system(4);
        }


        //ejecutarRUTA();
        startLocationUpdates();
        //llama al fragmento de mapa y lo pone en el FrameLayout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.FrameLayout, mapsFragment);
        transaction.commit();

        BottomNavigationView navigation = findViewById(R.id.navegador);
        navigation.setOnNavigationItemSelectedListener(mOnNavigacionItemSelectedListener);

        //BuscarAlertas();
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigacionItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //la navegacion entre fragmentos de la barra inferior

            switch (item.getItemId()) {
                case R.id.RutasFragment:
                    loadFragment(RutasFragment);
                    bucle = 1;
                    return true;
                case R.id.MapsFragment:
                    loadFragment(mapsFragment);
                    bucle = 0;
                    return true;
                case R.id.PerfilFragment:
                    loadFragment(perfilFragment);
                    bucle = 1;
                    return true;

            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment) {

        //remplaza el fragmentLayour por los fragmentos
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.FrameLayout, fragment);
        transaction.commit();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == event.KEYCODE_BACK) {

            Eliminar_Marcador = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            Eliminar_Marcador.setTitleText("¿Estas Seguro?");
            Eliminar_Marcador.setContentText("Estas Apunto De Salir De See You...,\n " +
                    "See You Seguira Trabajando En Segundo Plano.");
            Eliminar_Marcador.setConfirmText("Salir");
            Eliminar_Marcador.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Eliminar_Marcador.dismiss();
                }
            });
            Eliminar_Marcador.setCancelText("Cancelar");
            Eliminar_Marcador.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Eliminar_Marcador.dismiss();
                }
            });
            Eliminar_Marcador.show();


        }

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
    }


    public static void fondobottom(int accion) {

        if (accion == 1) {
            constraintLayout.setBackgroundResource(R.drawable.fondodegradado);
        } else if (accion == 2) {
            constraintLayout.setBackgroundResource(R.drawable.fondonaranaja2);
        } else if (accion == 3) {
            constraintLayout.setBackgroundResource(R.drawable.fondodegradado3);
        } else if (accion == 4) {
            constraintLayout.setBackgroundResource(R.drawable.fondodegradado4);
        }

    }

    public void system(int action) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            Drawable background = null;
            if (action == 1) {
                background = getResources().getDrawable(R.drawable.fondodegradado);
            } else if (action == 2) {
                background = getResources().getDrawable(R.drawable.fondonaranaja2);
            } else if (action == 3) {
                background = getResources().getDrawable(R.drawable.fondodegradado3);
            } else if (action == 4) {
                background = getResources().getDrawable(R.drawable.fondodegradado4);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }


    /*private void ejecutarRUTA() {
        final Handler handler = new Handler();
        final Handler handler2 = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                guardarruta();

                handler.postDelayed(this, 60 * 2000);
            }
        }, 20 * 6000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                alerta = 0;
                handler.postDelayed(this, 3000);
            }
        }, 3000);

    }*/


    public void Notificar(int notID, String Nombre, int id_notifi) {
        NotificationCompat.Builder creador;
        String canalID = "MiCanal01";
        NotificationManager notificador = (NotificationManager) getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
        // Si nuestro dispositivo tiene Android 8 (API 26, Oreo) o superior
        creador = new NotificationCompat.Builder(MainActivity.this, canalID);
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


   /*private void BuscarAlertas() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://mifolderdeproyectos.online/SEEYOU/Notificaciones_Alertas.php?iduser=" + preferences.getInt("id", 0)
                +"&idnotifi=" + preferences.getInt("idNotificacion", 0), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                    JSONArray array = new JSONArray(response);

                    int notifi = 2;

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);

                       Notificar(notifi,cajas.getString("nombre")+" "
                               +cajas.getString("apellido"),cajas.getInt("notificacion"));

                       notifi = notifi + 1;


                       if(i+1 == array.length()){
                           preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putInt("idNotificacion",cajas.getInt("notificacion"));
                           editor.commit();

                           new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                   .setTitleText("Algo Salio Mal..")
                                   .setContentText(preferences.getInt("idNotificacion",0)+"")
                                   .show();

                       }
                    }


                } catch (JSONException e) {

                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                            .show();
                }
            }
        },
                new Response.ErrorListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {


                        } catch (Exception e) {

                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                    .show();
                        }
                    }
                });

        Volley.newRequestQueue(MainActivity.this).add(stringRequest);

    }*/

    private void EnviarAlerta() {
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/Notificacion_registro.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();

                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("ALERTA!!")
                            .setContentText("AHORA TUS AMIGOS SABEN QUE LOS NECESITAS!!")
                            .show();
                } catch (Exception e) {
                    pDialog.dismiss();

                }
            }
        },
                new Response.ErrorListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onErrorResponse(VolleyError error) {

                            pDialog.dismiss();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("idgrupo", preferences.getInt("idgrupo", 0)+"");
                params.put("idusuario", id_usuario + "");


                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new com.google.android.gms.location.LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(distance_interval);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());


                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        latitud = location.getLatitude();
        longitud = location.getLongitude();

    }

    /*public void guardarruta() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/puntos_recorridos.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                } catch (Exception e) {

                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                            .show();
                }

            }

        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                try {

                    if (alertaubicacionactual == 0) {
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Almacenar La Ruta...")
                                .show();
                        alertaubicacionactual = 1;
                    }

                } catch (Exception e) {

                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                            .show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("id", id_usuario + "");
                params.put("latitud", latitud + "");
                params.put("longitud", longitud + "");

                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }*/

}