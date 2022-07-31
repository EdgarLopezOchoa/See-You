package com.example.seeyou;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RutasFragment extends Fragment implements GoogleMap.OnPolylineClickListener{
    GoogleMap mMap;
    SweetAlertDialog pDialog;
    LocationManager locationManager;
    ConnectivityManager locationManagerinternet;
    public static int id_usuario = 0, id_grupo = 0;
    SharedPreferences preferences;
    private ImageView ubicacion;
    int alertapuntos = 0, alertaubicacion = 0;
    View view;




    // VARIABLES DEL MAU
    // Color y tamaño para las lineas
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    //Cambiar lineas con un clic a otro estilo
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    // FIN

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap=googleMap;

            pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.setTitleText("Cargando ...");
            pDialog.setCancelable(false);

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            getLastLocation();
            RutasUsuarios();


            ////////////////////////////////////////////////////////////////////////////////////////
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rutas, container, false);
        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);


        preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        id_usuario = preferences.getInt("id", 0);

        if (preferences.getInt("idgrupo", 0) == 0) {
            new SweetAlertDialog(ubicacion.getContext())
                    .setTitleText("Aviso!")
                    .setContentText("Seleccione Un Grupo Para Ver Sus Marcadores y A Otros Usuarios Del Mismo.")
                    .show();
        }
        id_grupo = preferences.getInt("idgrupo", 0);



    return view;
    }


    public void RutasUsuarios(){

        try {
            pDialog.show();
            id_grupo = preferences.getInt("idgrupo", 0);

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "https://mifolderdeproyectos.online/SEEYOU/puntos_mapa_recorrido.php?id="+preferences.getInt("id",0), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pDialog.dismiss();

                        JSONArray array = new JSONArray(response);

                        ArrayList<Double> ArrayLongitud = new ArrayList<Double>();
                        ArrayList<Double> ArrayLatitud = new ArrayList<Double>();


                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);

                                ArrayLatitud.add(cajas.getDouble("Latitud_ruta"));
                                ArrayLongitud.add(cajas.getDouble("Longitud_ruta"));


                                Toast.makeText(getContext(), "Posicion: " + (i + 1) + "\n"
                                                + "Latitud: " + ArrayLatitud.get(i) + "\n"
                                                + "Longitud: " + ArrayLongitud.get(i) + "\n"
                                        , Toast.LENGTH_SHORT).show();
                            }



                        if(!Objects.equals(response,"[]")) {

                            Polyline polyline1 = mMap.addPolyline(new PolylineOptions().clickable(true)
                                    .add(

                                            new LatLng(ArrayLatitud.get(0), ArrayLongitud.get(0)),
                                            new LatLng(ArrayLatitud.get(1), ArrayLongitud.get(1)),
                                            new LatLng(ArrayLatitud.get(2), ArrayLongitud.get(2)),
                                            new LatLng(ArrayLatitud.get(3), ArrayLongitud.get(3)),
                                            new LatLng(ArrayLatitud.get(4), ArrayLongitud.get(4)),
                                            new LatLng(ArrayLatitud.get(5), ArrayLongitud.get(5)),
                                            new LatLng(ArrayLatitud.get(6), ArrayLongitud.get(6)),
                                            new LatLng(ArrayLatitud.get(7), ArrayLongitud.get(7)),
                                            new LatLng(ArrayLatitud.get(8), ArrayLongitud.get(8)),
                                            new LatLng(ArrayLatitud.get(9), ArrayLongitud.get(9))

                                    ));
                            // Indicar un nombre para la linea
                            polyline1.setTag("A");
                            // Estilo de la linea
                            stylePolyline(polyline1);
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        new SweetAlertDialog(ubicacion.getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(1)")
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
                                    if (alertapuntos == 0) {

                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("No Hemos Podido Obtener Los Puntos De Su Mapa...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                } else {
                                    if (alertapuntos == 0) {
                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                }
                            } catch (Exception e) {
                                pDialog.dismiss();
                                new SweetAlertDialog(ubicacion.getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(2)")
                                        .show();
                            }
                        }
                    });
            Volley.newRequestQueue(getContext()).add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error de catch: " + e, Toast.LENGTH_SHORT).show();
        }
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
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UbicacionActualo, 13));
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
            pDialog.dismiss();
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Algo Salio Mal..")
                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                    .show();
        }
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

    // EVENTOS DEL MAU
    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }


    }

    // Estilo para las lineas
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(android.R.drawable.presence_online), 10));
                polyline.setEndCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(android.R.drawable.presence_busy), 10));
                break;
        }

        //polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }
    // FIN
}