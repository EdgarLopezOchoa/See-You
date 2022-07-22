package com.example.seeyou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.androidgamesdk.gametextinput.Listener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Dialogo_MensajeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dialogo_MensajeFragment<listener> extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String version = Build.VERSION.RELEASE;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Toma las variables de otro fragmento
    int a = 1;
    int id_grupo = 0;
    public GoogleMap mMap = MapsFragment.mMap;
    double LatitudDialogo = MapsFragment.LatitudDialogo;
    double LongitudDialogo = MapsFragment.LongitudDialogo;
    String direccion = MapsFragment.direccion;
    String latitud = LatitudDialogo + "", Longitud = LongitudDialogo + "";
    LocationManager locationManager;
    NetworkInfo networkInfo;
    SharedPreferences preferences;
    RequestQueue requestQueue;
    ConnectivityManager locationManagerinternet;
    //


    //variables normales
    private NotificationManagerCompat notificationManagerCompat;
    private EditText descripcion, titulo1;
    private Button Registrar, cancelar;
    public String idpunto;

    public Dialogo_MensajeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dialogo_MensajeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Dialogo_MensajeFragment newInstance(String param1, String param2) {
        Dialogo_MensajeFragment fragment = new Dialogo_MensajeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }

    public void validar() {
        if (Objects.equals(titulo1.getText().toString(), "")) {
            new SweetAlertDialog(Registrar.getContext(),
                    SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Campo(s) Vacio(s)!")
                    .setContentText("Rellene Todos Los Campos Por Favor")
                    .show();
        } else if (Objects.equals(descripcion.getText().toString(), "")) {
            new SweetAlertDialog(Registrar.getContext(),
                    SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Campo(s) Vacio(s)!")
                    .setContentText("Rellene Todos Los Campos Por Favor")
                    .show();

        } else {
            Ubicacion("https://wwwutntrabajos.000webhostapp.com/SEEYOU/agregar_marcador.php");
        }
    }

    public void Ubicacion(String URL) {

        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                new SweetAlertDialog(Registrar.getContext(),
                        SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Buen Trabajo!")
                        .setContentText("El Marcador Ha Sido Registrado Correctamente")
                        .show();
                /*int height = 80;
                int width = 80;
                Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.markers_round);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);*/
                PuntosMapa();


                //cierra el fragmento
                getFragmentManager().beginTransaction().remove(Dialogo_MensajeFragment.this).commit();
            }

        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();

                try {
                    if (locationManagerinternet.getActiveNetworkInfo() != null
                            && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                            && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Registrar El Marcador...")
                                .show();
                    } else {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Por Favor Habilite Su Internet...")
                                .show();
                    }

                } catch (Exception e) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                            .show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("nombrePunto", titulo1.getText().toString());
                params.put("direccion", direccion);
                params.put("descripcion", descripcion.getText().toString());
                params.put("Longitud", Longitud);
                params.put("Latitud", latitud);
                params.put("idusuario", a + "");
                params.put("idgrupo", id_grupo + "");
                return params;
            }

        };


        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    public void PuntosMapa() {

        id_grupo = preferences.getInt("idgrupo",0);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/puntos_mapa.php?id=" + id_grupo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);

                    mMap.clear();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);
                        int height = 85;
                        int width = 85;
                        /*BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.mipmap.markers_round);
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);*/



                        MarkerOptions markerOptions = new MarkerOptions();


                        LatLng puntoubicacion =
                                new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud"));
                        markerOptions.position(puntoubicacion);
                        markerOptions.title(cajas.getString("IDpunto"));
                        markerOptions.draggable(true);

                        if (preferences.getBoolean("fondo2", false) == true){

                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                    .radius(90)
                                    .strokeWidth(3)
                                    .strokeColor(Color.TRANSPARENT)
                                    .fillColor(0x30DD4819));

                        }else if(preferences.getBoolean("fondo", false) == true){
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                    .radius(90)
                                    .strokeWidth(3)
                                    .strokeColor(Color.TRANSPARENT)
                                    .fillColor(0x30391B6F));
                        }
                        else if(preferences.getBoolean("fondo3", false) == true){
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                    .radius(90)
                                    .strokeWidth(3)
                                    .strokeColor(Color.TRANSPARENT)
                                    .fillColor(0x30FF0000));
                        }
                        else if(preferences.getBoolean("fondo4", false) == true){
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                    .radius(90)
                                    .strokeWidth(3)
                                    .strokeColor(Color.TRANSPARENT)
                                    .fillColor(0x3000F361));
                        } else{
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                    .radius(90)
                                    .strokeWidth(3)
                                    .strokeColor(Color.TRANSPARENT)
                                    .fillColor(0x30391B6F));
                        }


                        // markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.maps_round));
                        mMap.addMarker(markerOptions);
                    }


                } catch (JSONException e) {

                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
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

                            if (locationManagerinternet.getActiveNetworkInfo() != null
                                    && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                    && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("No Hemos Podido Obtener Los Puntos De Su Mapa...")
                                        .show();
                            } else {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                        .show();
                            }
                        } catch (Exception e) {

                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                    .show();
                        }
                    }
                });

        Volley.newRequestQueue(getContext()).add(stringRequest);

    }



    @SuppressLint("MissingPermission")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManagerCompat = NotificationManagerCompat.from(getContext());
        SharedPreferences preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        a = preferences.getInt("id", 0);
        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        networkInfo = locationManagerinternet.getActiveNetworkInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialogo__mensaje, container, false);
        Registrar = view.findViewById(R.id.BTNenviarmensaje);
        cancelar = view.findViewById(R.id.BTNenviarcancelar);
        titulo1 = view.findViewById(R.id.ETtituloubicacion);
        descripcion = view.findViewById(R.id.ETdescripcionubicacion);


        preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        id_grupo = preferences.getInt("idgrupo",0);



        if (preferences.getBoolean("fondo2", false) == true){
           Registrar.setBackgroundResource(R.drawable.buttonfondo2);

        }else if(preferences.getBoolean("fondo", false) == true){
            Registrar.setBackgroundResource(R.drawable.button2);
        }else if(preferences.getBoolean("fondo3", false) == true){
            Registrar.setBackgroundResource(R.drawable.buttonfondo3);
        }


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //cierra el fragmento
                getFragmentManager().beginTransaction().remove(Dialogo_MensajeFragment.this).commit();

            }
        });

        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validar();

                //Crea la ubicacion con los valores de longitud y latitud que se le proporciona


                //Codigo de notificaciones por si lo necesitamos

               /* String message = mensaje.getText().toString();
                String titulo = titulo1.getText().toString();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                        .setContentTitle(titulo)
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL);
                Intent intent = new Intent(getContext(),NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("message",message);

                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),
                        0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService
                        (Context.NOTIFICATION_SERVICE
                        );
                notificationManager.notify(3,builder.build());
                getFragmentManager().beginTransaction().remove(Dialogo_MensajeFragment.this).commit();*/
            }
        });
        return view;

    }
}