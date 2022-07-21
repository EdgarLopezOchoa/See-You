package com.example.seeyou.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.MapsFragment;
import com.example.seeyou.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MakersAdapters extends RecyclerView.Adapter<MakersAdapters.ViewHolder> {


    ArrayList<Markers> MarkerList;

    ArrayList<Markers> Modificar;
    SharedPreferences preferences;
    private Context context;
    private RecyclerView recyclerViewmarker = MapsFragment.recyclerViewmarker;
    private int MY_DEFAULT_TIMEOUT = 15000;
    private int id_grupo = MapsFragment.id_grupo;
    private GoogleMap mMap = MapsFragment.mMap;
    SweetAlertDialog Eliminar_Marcador_recycler, pDialog;
    LocationManager locationManager;
    ConnectivityManager locationManagerinternet;
    BottomSheetDialog bottomSheetDialog = MapsFragment.bottomSheetDialog;


    public MakersAdapters(ArrayList<Markers> markerList, Context context) {
        this.MarkerList = markerList;
        this.Modificar = new ArrayList<>();
        Modificar.addAll(markerList);


        this.context = context;
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(true);
        preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tarjeta, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {






        holder.titulo.setText(MarkerList.get(position).getNombre());
        holder.descripcion.setText(MarkerList.get(position).getDescripcion());
        holder.coordenadas.setText(MarkerList.get(position).getLatitud() + " , " + MarkerList.get(position).getLongitud());
        holder.ubicacion.setText(MarkerList.get(position).getDireccion());
        holder.habilitarmarcador.setText(MarkerList.get(position).getHabilitado());

        holder.id = MarkerList.get(position).getId();


        if (Objects.equals(MarkerList.get(position).getHabilitado(), "habilitado")) {

            holder.habilitarmarcador.setChecked(true);
        } else {
            holder.habilitarmarcador.setChecked(false);
        }


        if (preferences.getBoolean("fondo2", false) == true){
            holder.iniciar_viaje.setBackgroundResource(R.drawable.buttonfondo2);
            ColorStateList buttonStates = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    },
                    new int[]{
                            Color.BLUE,
                            Color.MAGENTA,
                            Color.RED
                    }
            );

            holder.habilitarmarcador.getThumbDrawable().setTintList(buttonStates);
            holder.habilitarmarcador.getTrackDrawable().setTintList(buttonStates);

        }else if(preferences.getBoolean("fondo", false) == true){
            holder.iniciar_viaje.setBackgroundResource(R.drawable.button2);
            ColorStateList buttonStates = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    },
                    new int[]{
                            Color.BLUE,
                            Color.BLUE,
                            Color.RED
                    }
            );
            holder.habilitarmarcador.getThumbDrawable().setTintList(buttonStates);
            holder.habilitarmarcador.getTrackDrawable().setTintList(buttonStates);

        }else if(preferences.getBoolean("fondo3", false) == true){
            holder.iniciar_viaje.setBackgroundResource(R.drawable.buttonfondo3);
            ColorStateList buttonStates = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    },
                    new int[]{
                            Color.BLUE,
                            Color.RED,
                            Color.DKGRAY
                    }
            );

            holder.habilitarmarcador.getThumbDrawable().setTintList(buttonStates);
            holder.habilitarmarcador.getTrackDrawable().setTintList(buttonStates);
        }
        else if(preferences.getBoolean("fondo4", false) == true){
            holder.iniciar_viaje.setBackgroundResource(R.drawable.buttonfondo4);
            ColorStateList buttonStates = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    },
                    new int[]{
                            Color.BLUE,
                            Color.GREEN,
                            Color.RED
                    }
            );
            holder.habilitarmarcador.getThumbDrawable().setTintList(buttonStates);
            holder.habilitarmarcador.getTrackDrawable().setTintList(buttonStates);
        }



       /* if (preferences.getBoolean("fondo2", false) == true){
            holder.contenedor.setBackgroundResource(R.drawable.blue_background_edit);

        }else if(preferences.getBoolean("fondo", false) == true){
            holder.contenedor.setBackgroundResource(R.drawable.fondonaranaja2);
        }else if(preferences.getBoolean("fondo3", false) == true){
            holder.contenedor.setBackgroundResource(R.drawable.blue_background_edit);
        }else if(preferences.getBoolean("fondo4", false) == true){
            holder.contenedor.setBackgroundResource(R.drawable.fondonaranaja2);
        }*/

        holder.habilitarmarcador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(MarkerList.get(position).getHabilitado(), "habilitado")) {

                    Habilitar("deshabilitado", holder.id, holder);


                } else if (Objects.equals(MarkerList.get(position).getHabilitado(), "deshabilitado")) {
                    Habilitar("habilitado", holder.id, holder);
                }
            }
        });

        holder.habilitarmarcador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        holder.iniciar_viaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, holder.id + "",
                        Toast.LENGTH_SHORT).show();
            }
        });

        holder.eliminarmarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Eliminar_Marcador_recycler = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                Eliminar_Marcador_recycler.setTitleText("¿Estas Seguro?");
                Eliminar_Marcador_recycler.setContentText("Este Marcador Ya no Se Podra Recuperar..");
                Eliminar_Marcador_recycler.setConfirmText("Eliminar");
                Eliminar_Marcador_recycler.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Eliminar(id_grupo, holder.id);
                        Eliminar_Marcador_recycler.dismiss();
                    }
                });
                Eliminar_Marcador_recycler.show();

            }
        });


    }


    @Override
    public int getItemCount() {
        return MarkerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo, ubicacion, coordenadas, descripcion, header;
        private Button iniciar_viaje, eliminarmarker;
        private Switch habilitarmarcador;
        private int id;
        ConstraintLayout fondo;
        LinearLayout contenedor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.TVnombreubicacionmarker);
            ubicacion = itemView.findViewById(R.id.TVubicacionmarker);
            coordenadas = itemView.findViewById(R.id.TVmasinformacionmarker);
            descripcion = itemView.findViewById(R.id.TVdescripcionmarker);
            iniciar_viaje = itemView.findViewById(R.id.BTNviajarmarker);
            eliminarmarker = itemView.findViewById(R.id.BTNeliminarmarker);
            habilitarmarcador = itemView.findViewById(R.id.SWhabilitarmarker);
             fondo = itemView.findViewById(R.id.ContenedorTarjeta);
             contenedor = itemView.findViewById(R.id.Contenedormarker);

        }
    }

    public void Habilitar(String Habilitado1, int id, ViewHolder holder) {

        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/habilitar_punto.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();

                if (Habilitado1 == "habilitado") {

                    holder.habilitarmarcador.setText("habilitado");
                    new SweetAlertDialog(context)
                            .setTitleText("Habilitado")
                            .setContentText("Este Marcador Ahora Aparecera En Su Mapa :D")
                            .show();
                    holder.habilitarmarcador.setChecked(true);

                } else {

                    holder.habilitarmarcador.setText("deshabilitado");
                    new SweetAlertDialog(context)
                            .setTitleText("Deshabilitado")
                            .setContentText("Este Marcador Ya No Aparecera En Su Mapa... D:")
                            .show();
                    holder.habilitarmarcador.setChecked(false);
                }

                PuntosRecycler(1);
                PuntosMapa();


            }

        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();

                if (Habilitado1 == "habilitado") {
                    holder.habilitarmarcador.setChecked(false);

                } else {
                    holder.habilitarmarcador.setChecked(true);
                }
                if (locationManagerinternet.getActiveNetworkInfo() != null
                        && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                        && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("No Hemos Podido Cambiar El Estado De Su Marcador...")
                            .show();
                } else {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Por Favor Habilite Su Internet...")
                            .show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("idpunto", id + "");
                params.put("habilitado", Habilitado1);


                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public ArrayList<Markers> filtrado(String Buscar) {

        try {
            int longitud = Buscar.length();

            if (longitud == 0 || Buscar == null || Buscar == "" || Buscar.isEmpty()) {

                Modificar.clear();
                Modificar.addAll(MarkerList);

            } else {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    List<Markers> collection = MarkerList.stream().filter(i -> i.getNombre().toLowerCase().
                                    contains(Buscar.toLowerCase()))
                            .collect(Collectors.toList());
                    Modificar.clear();

                    if (Objects.equals(Modificar, "[]")) {
                        Modificar.addAll(MarkerList);
                    } else {
                        Modificar.addAll(collection);
                    }

                    ArrayList<Markers> vacio = new ArrayList<Markers>();
                    vacio.clear();

                }

            }
        } catch (Exception e) {
            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Algo Salio Mal..")
                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                    .show();
        }
        return Modificar;


    }

    private void PuntosRecycler(int accion) {

        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/Buscar_marcadores.php?id=" + id_grupo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    MarkerList.clear();

                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);

                        MarkerList.add(new Markers(
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
                    if (accion == 2) {
                        MakersAdapters adapter = new MakersAdapters(MarkerList, context);
                        recyclerViewmarker.setHasFixedSize(true);
                        recyclerViewmarker.setLayoutManager(new LinearLayoutManager(context));
                        recyclerViewmarker.setAdapter(adapter);
                    }


                } catch (JSONException e) {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
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
                        pDialog.dismiss();
                        bottomSheetDialog.dismiss();
                        if (locationManagerinternet.getActiveNetworkInfo() != null
                                && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("No Hemos Podido Cargar Los Marcadores...")
                                    .show();
                        } else {
                            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Por Favor Habilite Su Internet...")
                                    .show();
                        }

                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(stringRequest);

    }


    public void Eliminar(int id_grupo, int id_punto) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/eliminar_punto.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();

                new SweetAlertDialog(context,
                        SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Eliminado")
                        .setContentText("El Marcador Ha Sido Eliminado Correctamente")
                        .show();

                PuntosRecycler(2);
                PuntosMapa();

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
                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Eliminar Su Marcador...")
                                .show();
                    } else {
                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Por Favor Habilite Su Internet...")
                                .show();
                    }
                } catch (Exception e) {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                            .show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("idpunto", id_punto + "");
                params.put("idusuario", id_grupo + "");


                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void PuntosMapa() {

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
                       /* BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.mipmap.markers_round);
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);*/

                        MarkerOptions markerOptions = new MarkerOptions();


                        LatLng puntoubicacion =
                                new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud"));
                        markerOptions.position(puntoubicacion);
                        markerOptions.title(cajas.getString("IDpunto"));


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



                        //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                        mMap.addMarker(markerOptions);
                    }


                } catch (JSONException e) {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
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
                                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("No Hemos Podido Actualizar Su Mapa...")
                                        .show();
                            } else {
                                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                        .show();
                            }
                        } catch (Exception e) {
                            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                    .show();
                        }
                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(stringRequest);

    }
}
