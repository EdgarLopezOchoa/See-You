package com.example.seeyou;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.adapters.MakersAdapters;
import com.example.seeyou.adapters.Markers;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MapsFragment extends Fragment {
    public static GoogleMap mMap;
    LocationManager locManager;
    private ImageView ubicacion, location, vermarkers, cambiarmapa;
    private Button cancelar, enviar;
    public static double LatitudDialogo, LongitudDialogo;
    private LinearLayout contenedor;
    private SearchView SVubicacion, SVpunto;
    private TextView titulo, nombremarcador, TVidmarker;
    public static RecyclerView recyclerViewmarker;
    public static ArrayList<Markers> markerslist = new ArrayList<>();
    public static String direccion = "";
    public static int id_usuario = 0;
    int tiempo = 5000;
    public static BottomSheetDialog bottomSheetDialog, bottomSheetDialogmarker;
    int bucleubicacion = 0, mensaje = 0;
    SweetAlertDialog Eliminar_Marcador;
    View view;
    SweetAlertDialog pDialog;
    LocationManager locationManager;
    MakersAdapters adapter;
    RequestQueue requestQueue;
    ConnectivityManager locationManagerinternet;
    NetworkInfo networkInfo;
    TextView ubicacionmarcador, coordenadamarcador, descripcionmarcador;
    Switch habilitado;
    Toolbar toolbar;
    String version = Build.VERSION.RELEASE;
    SharedPreferences preferences;

    private com.google.android.gms.location.LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */


    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @SuppressLint({"MissingPermission"})
        @Override
        public void onMapReady(GoogleMap googleMap) {
            adapter = new MakersAdapters(markerslist, getContext());
            try {
                pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setTitleText("Cargando ...");
                pDialog.setCancelable(true);


                id_usuario = preferences.getInt("id", 0);

                locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
                locationManagerinternet = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
                networkInfo = locationManagerinternet.getActiveNetworkInfo();

                if (mensaje == 0) {
                    new SweetAlertDialog(getContext())
                            .setTitleText("Bienvenido!!!!")
                            .setContentText("Bienvenido De Vuelta " + preferences.getString("Nombre", "") + " "
                                    + preferences.getString("Apellido", "") +
                                    ", \n Siempre Es Un Gusto Tenerte Aqui :D")
                            .show();
                    mensaje = 1;
                }
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                SVubicacion.setQuery("", false);
                SVubicacion.setIconified(true);
                SVubicacion.clearFocus();
                SVubicacion.setEnabled(false);

                mMap = googleMap;

                PuntosMapa();

                //startLocationUpdates();

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                }


                //Habilita el ver la ubicacion actual
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {


                        //asigna el contenedor y el layout donde se mostrara el cuadro de dialogo
                        bottomSheetDialogmarker = new BottomSheetDialog
                                (getContext(), R.style.BottomSheetDialog);
                        View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                                R.layout.marker_layout, (LinearLayout) contenedor
                        );

                        bottomSheetDialogmarker.setContentView(bottomSheetView);


                        //Asigna los valores a los objetos dentro el bottomsheetdialog
                        nombremarcador = bottomSheetDialogmarker.findViewById(R.id.TVnombreubicacion);
                        ubicacionmarcador = bottomSheetDialogmarker.findViewById(R.id.TVubicacion);
                        coordenadamarcador = bottomSheetDialogmarker.findViewById(R.id.TVmasinformacion);
                        descripcionmarcador = bottomSheetDialogmarker.findViewById(R.id.TVdescripcion);
                        habilitado = bottomSheetDialogmarker.findViewById(R.id.SWhabilitar);
                        TVidmarker = bottomSheetDialogmarker.findViewById(R.id.TVbotonesmarker);
                        Button btneliminar = bottomSheetDialogmarker.findViewById(R.id.BTNeliminar);
                        Button iniciarviaje = bottomSheetDialogmarker.findViewById(R.id.BTNviajar);

                        if (preferences.getBoolean("fondo2", false) == true){
                            iniciarviaje.setBackgroundResource(R.drawable.buttonfondo2);

                        }else if(preferences.getBoolean("fondo", false) == true){
                            iniciarviaje.setBackgroundResource(R.drawable.button2);
                        }else if(preferences.getBoolean("fondo3", false) == true){
                            iniciarviaje.setBackgroundResource(R.drawable.buttonfondo3);
                        }

                        habilitado.setText("habilitado");
                        habilitado.setChecked(true);

                        LatLng latLng = marker.getPosition();
                        double Latitud, logitud;
                        Latitud = latLng.latitude;
                        logitud = latLng.longitude;

                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(Latitud, logitud, 1);

                            Address address = (Address) addresses.get(0);
                            String addressStr = "";
                            addressStr += address.getAddressLine(0);

                            ubicacionmarcador.setText(addressStr);

                        } catch (IOException e) {
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                                    .show();
                        }

                        coordenadamarcador.setText("" + Latitud + " : " + logitud);
                        nombremarcador.setText(marker.getTitle());

                        Ubicacion(marker.getPosition().latitude + ""
                                , marker.getPosition().longitude + ""
                                , nombremarcador.getText().toString()
                                , ubicacionmarcador.getText().toString());


                        iniciarviaje.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(), "ESTE BOTON HARA ALGO ALGUN DIA :D",
                                        Toast.LENGTH_SHORT).show();


                            }
                        });

                        btneliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Eliminar_Marcador = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                                Eliminar_Marcador.setTitleText("¿Estas Seguro?");
                                Eliminar_Marcador.setContentText("Este Marcador Ya No Se Podra Recuperar..");
                                Eliminar_Marcador.setConfirmText("Eliminar");
                                Eliminar_Marcador.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        Eliminar(id_usuario, TVidmarker.getText().toString());
                                        Eliminar_Marcador.dismiss();
                                    }
                                });
                                Eliminar_Marcador.show();


                            }
                        });

                        habilitado.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (habilitado.getText().toString() == "habilitado") {
                                    Habilitar("deshabilitado", TVidmarker.getText().toString());

                                } else {
                                    Habilitar("habilitado", TVidmarker.getText().toString());

                                }
                            }
                        });


                        return true;
                    }
                });

            } catch (Exception e) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Algo Salio Mal..")
                        .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                        .show();
            }
        }


    };


    public void Eliminar(int id_usuario, String id_punto) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/eliminar_punto.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(),
                            SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Eliminado")
                            .setContentText("El Marcado Ha Sido Eliminado Correctamente")
                            .show();
                    bottomSheetDialogmarker.dismiss();


                    PuntosMapa();

                } catch (Exception e) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                            .show();
                }
            }


        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    pDialog.dismiss();
                    if (locationManagerinternet.getActiveNetworkInfo() != null
                            && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                            && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Eliminar Su Marcador...")
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
                            .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                            .show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("idpunto", id_punto + "");
                params.put("idusuario", id_usuario + "");


                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    public void Habilitar(String Habilitado1, String id) {
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/habilitar_punto.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();

                    if (Habilitado1 == "habilitado") {

                        habilitado.setText("habilitado");
                        new SweetAlertDialog(getContext())
                                .setTitleText("Habilitado")
                                .setContentText("Este Marcador Ahora Aparecera En Su Mapa :D")
                                .show();
                        habilitado.setChecked(true);

                    } else {

                        habilitado.setText("deshabilitado");
                        new SweetAlertDialog(getContext())
                                .setTitleText("Deshabilitado")
                                .setContentText("Este Marcador Ya No Aparecera En Su Mapa... D:")
                                .show();
                        habilitado.setChecked(false);
                    }

                    PuntosMapa();

                } catch (Exception e) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                            .show();
                }

            }

        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    pDialog.dismiss();
                    if (Habilitado1 == "habilitado") {
                        habilitado.setChecked(false);

                    } else {
                        habilitado.setChecked(true);
                    }
                    if (locationManagerinternet.getActiveNetworkInfo() != null
                            && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                            && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Cambiar El Estado De Su Marcador...")
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
                            .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                            .show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("idpunto", id);
                params.put("habilitado", Habilitado1);


                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    private void PuntosRecycler() {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/Buscar_marcadores.php?id=" + id_usuario, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                    markerslist.clear();

                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);

                        markerslist.add(new Markers(
                                cajas.getString("habilitado"),
                                cajas.getInt("IDpunto"),
                                cajas.getString("Nombre"),
                                cajas.getDouble("Longitud"),
                                cajas.getDouble("Latitud"),
                                cajas.getString("direccion"),
                                cajas.getString("descripcion")

                        ));


                    }
                    pDialog.dismiss();
                    if (Objects.equals(response, "[]")) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("No Cuentas Con Marcadores..")
                                .setContentText("Registra Marcadores Para Que Puedas Observarlos Aqui :D!!")
                                .show();
                    } else {
                        MakersAdapters adapter = new MakersAdapters(markerslist, getContext());
                        recyclerViewmarker.setHasFixedSize(true);
                        recyclerViewmarker.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerViewmarker.setAdapter(adapter);
                        bottomSheetDialog.show();
                    }

                } catch (JSONException e) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                            .show();
                }
            }
        },
                new Response.ErrorListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            pDialog.dismiss();
                            bottomSheetDialog.dismiss();
                            if (locationManagerinternet.getActiveNetworkInfo() != null
                                    && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                    && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("No Hemos Podido Cargar Los Marcadores...")
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
                                    .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                                    .show();
                        }
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(stringRequest);

    }


    private void PuntosMapa() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/puntos_mapa.php?id=" + id_usuario, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);

                    mMap.clear();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);

                        MarkerOptions markerOptions = new MarkerOptions();


                        LatLng puntoubicacion =
                                new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud"));
                        markerOptions.position(puntoubicacion);
                        markerOptions.title(cajas.getString("Nombre"));
                        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.maps_round));
                        mMap.addMarker(markerOptions);
                    }


                } catch (JSONException e) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
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
                                    .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                                    .show();
                        }
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(stringRequest);

    }


    public void Ubicacion(String latitud1, String longitud1, String nombre, String ubicacion1) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/buscar_datos_de_marcador.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();
                    JSONArray array = new JSONArray(response);

                    JSONObject cajas = array.getJSONObject(0);


                    descripcionmarcador.setText(cajas.getString("descripcion"));
                    TVidmarker.setText(cajas.getString("IDpunto"));
                    bottomSheetDialogmarker.show();


                } catch (JSONException e) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                            .show();
                }

            }

        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    pDialog.dismiss();


                    if (locationManagerinternet.getActiveNetworkInfo() != null
                            && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                            && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Obtener La Informacion Del Marcador...")
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
                            .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                            .show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("nombrePunto", nombre);
                params.put("direccion", ubicacion1);
                params.put("latitud", latitud1);
                params.put("longitud", longitud1);

                return params;
            }

        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        try {

            FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());

            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                LatLng UbicacionActualo = new LatLng(location.getLatitude(), location.getLongitude());

                                //Mueve la camara al punto proporcionado, osea la ubicacion del usuario
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UbicacionActualo, 14));
                            } else {
                                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Algo Salio Mal..")
                                            .setContentText("Por Favor Habilite Su Ubicacion...")
                                            .show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Por Favor Active Su Ubicacion O Permita Que La App Accesada A Ella...")
                                    .show();
                        }
                    });
        } catch (Exception e) {
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Algo Salio Mal..")
                    .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                    .show();
        }
    }

    //HASTA AQUI TERMINA EL CODIGO DE UBICACION VIA GOOGLE

    public void Marcador() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                try {

                    //Guarda los valores de longitud y latitud en variables
                    double latitud = latLng.latitude;
                    double longitud = latLng.longitude;


                    //Le da a las variables globales los valores que necesitan
                    LatitudDialogo = latitud;
                    LongitudDialogo = longitud;


                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);

                        direccion = "";

                        Address address = (Address) addresses.get(0);
                        direccion += address.getAddressLine(0);


                    } catch (IOException e) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                                .show();
                    }


                    //Guarda Todas las opciones necesarias para hacer el marcador
                    MarkerOptions markerOptions = new MarkerOptions();

                    //Crea el punto donde se marcara
                    markerOptions.position(latLng);

                    //le da nombre al punto
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);


                    //Mueve la camara al punto proporcionado, osea la ubicacion del usuario
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


                    //llama al fragmento dialogo
                    Dialogo_MensajeFragment dialogofragment = new Dialogo_MensajeFragment();
                    dialogofragment.show(getFragmentManager(), "MyFragment");

                } catch (Exception e) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                            .show();
                }
            }

        });

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);


        //objetos de la pantalla de inicio de maps
        ubicacion = view.findViewById(R.id.IVubicacion);
        cancelar = view.findViewById(R.id.BTNcancelar);
        enviar = view.findViewById(R.id.BTNenviarmensaje);
        location = view.findViewById(R.id.IVlocation);
        contenedor = view.findViewById(R.id.Contenedormarker);
        vermarkers = view.findViewById(R.id.IVvermarkers);
        SVubicacion = view.findViewById(R.id.SVubicacion);
        titulo = view.findViewById(R.id.TVtituloseeyou);
        cambiarmapa = view.findViewById(R.id.IVcambiarmapa);
        toolbar = view.findViewById(R.id.navegador1);

        preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        if (preferences.getBoolean("fondo2", false) == true){
            toolbar.setBackgroundResource(R.drawable.fondodegradado2);

        }else if(preferences.getBoolean("fondo", false) == true){
            toolbar.setBackgroundResource(R.drawable.fondodegradado);
        }
        else if(preferences.getBoolean("fondo3", false) == true){
            toolbar.setBackgroundResource(R.drawable.fondodegradado3);
        }

        cambiarmapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });


        SVubicacion.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = SVubicacion.getQuery().toString();
                List<Address> addressList = null;
                if (location != null & !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } catch (Exception e) {
                        new SweetAlertDialog(getContext())
                                .setTitleText("Ubicacion No Encontrada...")
                                .setContentText("Asegurece De Seguir Esta Estructura: \n  Ciudad + Lugar/calle/Local/Establecimineto o Viceversa.")
                                .show();
                    }
                } else {
                    new SweetAlertDialog(getContext())
                            .setTitleText("Favor De Introducir Una Ubicaion Valida!!")
                            .show();


                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
/*
                String location = SVubicacion.getQuery().toString();
                List<Address> addressList = null;
                if (location != null & !location.equals("")){
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }catch (Exception e){

                    }
                }else{



                }*/
                return false;
            }
        });

        SVubicacion.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titulo.setVisibility(View.INVISIBLE);
                SVubicacion.setBackgroundResource(R.drawable.searchbar);

            }
        });

        SVubicacion.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                titulo.setVisibility(View.VISIBLE);
                SVubicacion.setBackgroundResource(R.color.Trasparente);
                return false;
            }
        });


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLastLocation();

            }
        });


        vermarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    bottomSheetDialog = new BottomSheetDialog
                            (getContext(), R.style.BottomSheetDialog);
                    View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                            R.layout.markersbottomshet, (LinearLayout) view.findViewById(R.id.ContenedorTarjeta)
                    );
                    bottomSheetDialog.setContentView(bottomSheetView);


                    PuntosRecycler();
                    bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    recyclerViewmarker = bottomSheetDialog.findViewById(R.id.RVmarkersbottomsheet);
                    TextView titulopuntos = bottomSheetDialog.findViewById(R.id.TVtitulomarcadorrecicler);
                    SVpunto = bottomSheetDialog.findViewById(R.id.SVpunto);

                    Toolbar marker = bottomSheetDialog.findViewById(R.id.navegadormarker);


                    if (preferences.getBoolean("fondo2", false) == true){
                        marker.setBackgroundResource(R.drawable.fondodegradado2);

                    }else if(preferences.getBoolean("fondo", false) == true){
                        marker.setBackgroundResource(R.drawable.fondodegradado);

                    }else if(preferences.getBoolean("fondo3", false) == true){
                        marker.setBackgroundResource(R.drawable.fondodegradado3);
                    }


                    SVpunto.setOnSearchClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            titulopuntos.setVisibility(View.INVISIBLE);
                            SVpunto.setBackgroundResource(R.drawable.searchbar);

                        }
                    });

                    SVpunto.setOnCloseListener(new SearchView.OnCloseListener() {
                        @Override
                        public boolean onClose() {
                            titulopuntos.setVisibility(View.VISIBLE);
                            SVpunto.setBackgroundResource(R.color.Trasparente);
                            return false;
                        }
                    });

                    SVpunto.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {


                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {


                            ArrayList<Markers> lista = new ArrayList<>();
                            lista = adapter.filtrado(newText);
                            MakersAdapters adapter = new MakersAdapters(lista, getContext());
                            recyclerViewmarker.setHasFixedSize(true);
                            recyclerViewmarker.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerViewmarker.setAdapter(adapter);
                            return false;
                        }

                    });

                } catch (Exception e) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Espere Un Momento....")
                            .show();
                }

            }

        });


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //desactiva la funcion de marcar puntos al hacer click
                mMap.setOnMapClickListener(null);

                //vuelve invisible el boton de cancelar
                cancelar.setVisibility(View.INVISIBLE);


            }
        });

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //vuelve visible el boton de cancelar
                cancelar.setVisibility(View.VISIBLE);


                    /*hacia una prueba donde bajaba el brillo a la pantalla cuando hacia clic a esta imagen,
                     funciona pero no tiene utilidad de momento
                     */

                    /*WindowManager.LayoutParams brillo = getActivity().getWindow().getAttributes();
                    brillo.screenBrightness= 0.05F;
                    getActivity().getWindow().setAttributes(brillo);
                    */

                Marcador();
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }


}


