package com.example.seeyou.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.KotlinServices.LocationService;
import com.example.seeyou.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class UpdateLocation extends LocationService {
    RequestQueue requestQueue = null;



    public UpdateLocation(Double Latitud,Double Longitud,Context context){


        ActualizarUbicacion(Latitud,Longitud,context);
    }

    public void ActualizarUbicacion(double latitud, double longitud, Context context) {
        try{


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/ubicacion_usuario.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }

        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences preferences;
                int id_usuario;
                preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                id_usuario = preferences.getInt("id", 0);

                params.put("id", id_usuario + "");
                params.put("latitud", latitud + "");
                params.put("longitud", longitud + "");

                return params;
            }

        };

        if(requestQueue == null) {

            requestQueue = Volley.newRequestQueue(context);
        }
        requestQueue.add(stringRequest);

        }catch (Exception e){

        }
    }

}
