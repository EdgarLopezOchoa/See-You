package com.example.seeyou;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.adapters.AdapterUsuario;
import com.example.seeyou.adapters.FechasAdapter;
import com.example.seeyou.adapters.FechasRutas;
import com.example.seeyou.adapters.Grupos;
import com.example.seeyou.adapters.MakersAdapters;
import com.example.seeyou.adapters.Markers;
import com.example.seeyou.adapters.Usuarios;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RutasFragment extends Fragment implements GoogleMap.OnPolylineClickListener {
    public static GoogleMap mMap;
    SweetAlertDialog pDialog;
    ArrayList<Usuarios> UserList = new ArrayList<>();
    ArrayList<FechasRutas> FechasList = new ArrayList<>();
    LocationManager locationManager;
    ConnectivityManager locationManagerinternet;
    public static int id_usuario = 0, id_grupo = 0;
    int alertaubicacionactual = 0;
    SharedPreferences preferences;
    private ImageView ubicacion;
    int alertapuntos = 0, alertaubicacion = 0;
    View view;
    public static RecyclerView recyclerViewusers,RVfechas;
    RequestQueue requestQueuefechas = null,requestQueueusuarios = null,requestQueuefotos = null,requestQueuerutas = null;
    TextView nombreusuario;
    double longitud,latitud;
    Marker marker[] = new Marker[1];
    LinearLayoutManager horizontallayout;


    // Color y tamaño para las lineas
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_BLUE_ARGB = 0x000080 ;
    private static final int COLOR_RED_ARGB = 0xff0000 ;
    private static final int COLOR_GREEN_ARGB = 0x008000 ;
    private static final int COLOR_PINK_ARGB = 0xff00ff ;
    private static final int COLOR_GOLD_ARGB = 0xffd700 ;
    private static final int POLYLINE_STROKE_WIDTH_PX = 10;
    //Cambiar lineas con un clic a otro estilo
    private static final int PATTERN_GAP_LENGTH_PX = 16;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);


    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.setTitleText("Cargando ...");
            pDialog.setCancelable(false);

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            getLastLocation();
            ejecutar();
            RutasUsuariosFotos();
            FechasRutas();

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rutas, container, false);
        preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences.edit();

        editor1.putString("fecharuta","hoy");
        editor1.commit();


        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);

        recyclerViewusers = view.findViewById(R.id.RBusuariosrutas);
        RVfechas = view.findViewById(R.id.RVrutasfechas);


        id_usuario = preferences.getInt("id", 0);

        if (preferences.getInt("idgrupo", 0) == 0) {
            new SweetAlertDialog(getContext())
                    .setTitleText("Aviso!")
                    .setContentText("Seleccione Un Grupo Para Ver Sus Marcadores y A Otros Usuarios Del Mismo.")
                    .show();
        }
        id_grupo = preferences.getInt("idgrupo", 0);

        horizontallayout = new LinearLayoutManager(getContext(),horizontallayout.HORIZONTAL,false);

        return view;
    }


    private void ejecutar() {
        final Handler handler = new Handler();
        final Handler handler2 = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RutasUsuarios();

                handler.postDelayed(this, 5000);
            }
        }, 5000);


        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                usuariosMapa();

                handler.postDelayed(this, 5000);
            }
        }, 5000);

    }


    public void RutasUsuarios() {

        try {

            id_grupo = preferences.getInt("idgrupo", 0);

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "https://mifolderdeproyectos.online/SEEYOU/puntos_mapa_recorrido.php?id=" + preferences.getInt("idusuarioruta", preferences.getInt("id", 0)) + "&fecha=" + preferences.getString("fecharuta","hoy"), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Polyline polyline1 = null;



                        JSONArray array = new JSONArray(response);


                        Iterable<LatLng> Points = new ArrayList<>();


                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);

                            LatLng coordenada = new LatLng(cajas.getDouble("Latitud_ruta"),cajas.getDouble("Longitud_ruta"));

                            ((ArrayList<LatLng>) Points).add(coordenada);





                        }

                        if (!Objects.equals(response, "[]")) {
                            try {
                                mMap.clear();

                                    polyline1 = mMap.addPolyline(new PolylineOptions().addAll(Points));
                                    if (((ArrayList<LatLng>) Points).size()<=10){
                                    polyline1.setColor(0x9900F361);
                                    } else  if (((ArrayList<LatLng>) Points).size()<=20){
                                        polyline1.setColor(0x99DD4819);
                                    }else  if (((ArrayList<LatLng>) Points).size()<=30){
                                        polyline1.setColor(0x99E407A6);
                                    }else  if (((ArrayList<LatLng>) Points).size()<=40){
                                        polyline1.setColor(0x99145FAF);
                                    }else  if (((ArrayList<LatLng>) Points).size()<=50){
                                        polyline1.setColor(0x99000DFF);
                                    }else  if (((ArrayList<LatLng>) Points).size() <=60){
                                        polyline1.setColor(0x99595959);
                                    }else  if (((ArrayList<LatLng>) Points).size()<=70){
                                        polyline1.setColor(0xFF000000);
                                    }else  if (((ArrayList<LatLng>) Points).size()<=80){
                                        polyline1.setColor(0x995b00bc);
                                    }else  if (((ArrayList<LatLng>) Points).size()<=90){
                                        polyline1.setColor(0x99FFD400);
                                    }else  if (((ArrayList<LatLng>) Points).size()<=100){
                                        polyline1.setColor(0x99FF0000);
                                    }





                            } catch (Exception e) {

                            }
                        }else{
                            mMap.clear();

                        }



                        if (!Objects.equals(response, "[]")) {
                            // Indicar un nombre para la linea
                            polyline1.setTag("A");
                            polyline1.setClickable(true);
                            polyline1.setVisible(true);

                            //polyline1.setTag(userName);
                            // Estilo de la linea
                            stylePolyline(polyline1);
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

                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                        .show();
                            }
                        }
                    });

            if (requestQueuerutas == null){
                requestQueuerutas = Volley.newRequestQueue(getContext());
            }
            requestQueuerutas.add(stringRequest);

        } catch (Exception e) {

        }
    }


    public void usuariosMapa() {
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "https://mifolderdeproyectos.online/SEEYOU/fotos_rutas.php?id=" + preferences.getInt("idusuarioruta", 0) + "&idgrupo=" + preferences.getInt("idgrupo", 0), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray array = new JSONArray(response);
                        //mMap.clear();


                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);

                            try {


                                MarkerOptions markerOptions = new MarkerOptions();

                                LatLng puntoubicacion =
                                        new LatLng(cajas.getDouble("latitud"), cajas.getDouble("longitud"));
                                markerOptions.position(puntoubicacion);
                                markerOptions.title("Nombre:").visible(false);
                                markerOptions.snippet(cajas.getString("nombre"));
                                markerOptions.draggable(false);
                                markerOptions.visible(true);

                                if(!Objects.equals(cajas.getString("foto"), "")) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                                            getMarkerBitmapFromView(cajas.getString("foto"))));
                                } else{
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                                            getMarkerBitmapFromView("https://mx.web.img2.acsta.net/c_310_420/pictures/15/06/04/16/19/049773.jpg")));

                                }

                                mMap.addMarker(markerOptions);

                            }catch (Exception e){

                            }
                        }


                    } catch (JSONException e) {
                        pDialog.dismiss();
                        Toast.makeText(getContext(), e+"", Toast.LENGTH_SHORT).show();
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

                                    if (alertaubicacion == 0) {
                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("No Hemos Podido Obtener La Ubicacion De Los Miembros Del Grupo...")
                                                .show();
                                        alertaubicacion = 1;
                                    }
                                } else {
                                    if (alertaubicacion == 0) {
                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                                .show();
                                        alertaubicacion = 1;
                                    }
                                }
                            } catch (Exception e) {
                                pDialog.dismiss();
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                        .show();
                            }
                        }
                    });

            if (requestQueueusuarios == null){


                requestQueueusuarios = Volley.newRequestQueue(getContext());
            }
            requestQueueusuarios.add(stringRequest);


        } catch (Exception e) {

        }
    }

    private Bitmap getMarkerBitmapFromView(String url) {
        try {


            View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
            ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
            Picasso.get().load(url).placeholder(R.drawable.user).into(markerImageView);
            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
            customMarkerView.buildDrawingCache();
            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Drawable drawable = customMarkerView.getBackground();
            if (drawable != null)
                drawable.draw(canvas);
            customMarkerView.draw(canvas);
            return returnedBitmap;
        } catch (Exception e) {

        }
        return null;

    }




    public void RutasUsuariosFotos() {

        try {
            pDialog.show();
            id_grupo = preferences.getInt("idgrupo", 0);

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "https://mifolderdeproyectos.online/SEEYOU/usuarios_rutas.php?id=" + preferences.getInt("id", 0) + "&idgrupo=" + preferences.getInt("idgrupo", 0), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pDialog.dismiss();

                        JSONArray array = new JSONArray(response);
                        UserList.clear();


                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);

                            UserList.add(new Usuarios(
                                    cajas.getInt("idusuarios"),
                                    cajas.getString("nombreusuario"),
                                    cajas.getString("apellidousuario"),
                                    cajas.getString("foto")
                            ));
                        }
                        AdapterUsuario adapter = new AdapterUsuario(UserList, getContext());
                        recyclerViewusers.setHasFixedSize(true);
                        recyclerViewusers.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerViewusers.setAdapter(adapter);


                    } catch (JSONException e) {
                        pDialog.dismiss();
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
                                pDialog.dismiss();
                                if (locationManagerinternet.getActiveNetworkInfo() != null
                                        && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                        && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                                    if (alertapuntos == 0) {

                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("No Hemos Podido Obtener A Los Usuarios...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                } else {
                                    if (alertapuntos == 0) {
                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("Por Favor Habilite Su Internet Para Poder Cargar A Los Usuarios...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                }
                            } catch (Exception e) {
                                pDialog.dismiss();
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                        .show();
                            }
                        }
                    });
            if (requestQueuefotos == null){


                requestQueuefotos = Volley.newRequestQueue(getContext());
            }
            requestQueuefotos.add(stringRequest);

        } catch (Exception e) {
            pDialog.dismiss();
            Toast.makeText(getContext(), "Error de catch: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void FechasRutas() {

        try {
            pDialog.show();


            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "https://mifolderdeproyectos.online/SEEYOU/fecha_puntos_recorridos.php?id=" + preferences.getInt("idusuarioruta", preferences.getInt("id",0))
                            +"&idusuario="+preferences.getInt("id",0)+"&idgrupo="+preferences.getInt("idgrupo",0), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pDialog.dismiss();

                        JSONArray array = new JSONArray(response);
                        FechasList.clear();

                        if (!Objects.equals(response,"[]")){


                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);

                            FechasList.add(new FechasRutas(
                                    cajas.getString("fecha"),
                                    cajas.getString("mes"),
                                    cajas.getString("dia")
                            ));
                        }
                        FechasAdapter adapter = new FechasAdapter(FechasList, getContext());
                        RVfechas.setHasFixedSize(true);
                        RVfechas.setLayoutManager(horizontallayout);
                        RVfechas.setAdapter(adapter);

                        }
                    } catch (JSONException e) {
                        pDialog.dismiss();
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
                                pDialog.dismiss();
                                if (locationManagerinternet.getActiveNetworkInfo() != null
                                        && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                        && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                                    if (alertapuntos == 0) {

                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("No Hemos Podido Obtener A Los Usuarios...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                } else {
                                    if (alertapuntos == 0) {
                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("Por Favor Habilite Su Internet Para Poder Cargar A Los Usuarios...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                }
                            } catch (Exception e) {
                                pDialog.dismiss();
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                        .show();
                            }
                        }
                    });
            if (requestQueuefechas == null){


                requestQueuefechas = Volley.newRequestQueue(getContext());
            }
            requestQueuefechas.add(stringRequest);

        } catch (Exception e) {
            pDialog.dismiss();
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

        mapFragment.getMapAsync(callback);


    }

    //Evento cuando se hace clic en una linea (FALLA)
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
        //polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setJointType(JointType.ROUND);
}



}