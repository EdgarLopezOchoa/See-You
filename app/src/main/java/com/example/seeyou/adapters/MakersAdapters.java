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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
    public static GoogleMap mapa;
    BottomSheetDialog bottomSheetDialog = MapsFragment.bottomSheetDialog, bottomSheetDialogeditmarker;


    public MakersAdapters(ArrayList<Markers> markerList, Context context) {
        this.MarkerList = markerList;
        this.Modificar = new ArrayList<>();
        Modificar.addAll(markerList);


        this.context = context;
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(false);
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

        holder.id = MarkerList.get(position).getId();


        if (Objects.equals(MarkerList.get(position).getHabilitado(), "habilitado")) {

            holder.habilitarmarcador.setChecked(true);
        } else {
            holder.habilitarmarcador.setChecked(false);
        }


        if (preferences.getBoolean("fondo2", false) == true){

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

        holder.contenedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetBehavior<View> bottomSheetBehavior;
                bottomSheetDialogeditmarker = new BottomSheetDialog
                        (context,R.style.BottomSheetDialog);
                View bottomSheetView = LayoutInflater.from(context).inflate(
                        R.layout.detalles_marker, null
                );
                bottomSheetDialogeditmarker.setContentView(bottomSheetView);
                LinearLayout contenedor1 = bottomSheetDialogeditmarker.findViewById(R.id.editarmarker);
                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.92);

                assert contenedor1 !=null;
                contenedor1.setMinimumHeight(height);
                bottomSheetBehavior.setMaxHeight(height);

                holder.Nombregrupo = bottomSheetDialogeditmarker.findViewById(R.id.etNombreMarker);
                holder.DescripcionGrupo = bottomSheetDialogeditmarker.findViewById(R.id.etDescripcionMarker);
                holder.idmarker = bottomSheetDialogeditmarker.findViewById(R.id.TVidmarker);
                holder.eliminarmarkerbutton = bottomSheetDialogeditmarker.findViewById(R.id.BTNeliminarmarker);
                holder.DirreccionGrupo = bottomSheetDialogeditmarker.findViewById(R.id.etDireccionMarker);
                holder.guardarcambios = bottomSheetDialogeditmarker.findViewById(R.id.TVguardarcambios);
                holder.DirreccionGrupo.setEnabled(false);
                holder.cerrareditarmarker = bottomSheetDialogeditmarker.findViewById(R.id.TVcerrareditarmarcador);

                Ubicacion(holder.id,holder);

                holder.eliminarmarkerbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Eliminar_Marcador_recycler = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                        Eliminar_Marcador_recycler.setTitleText("¿Estas Seguro?");
                        Eliminar_Marcador_recycler.setContentText("Este Marcador Ya no Se Podra Recuperar..");
                        Eliminar_Marcador_recycler.setConfirmText("Eliminar");
                        Eliminar_Marcador_recycler.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Eliminar(id_grupo, holder.id,1);
                                Eliminar_Marcador_recycler.dismiss();
                            }
                        });
                        Eliminar_Marcador_recycler.setCancelText("Cancelar");
                        Eliminar_Marcador_recycler.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Eliminar_Marcador_recycler.dismiss();
                            }
                        });
                        Eliminar_Marcador_recycler.show();
                    }
                });

                holder.guardarcambios.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CambiarPunto(holder.DescripcionGrupo.getText().toString(),
                                holder.idmarker.getText().toString(),holder.Nombregrupo.getText().toString());
                    }
                });

                holder.cerrareditarmarker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialogeditmarker.dismiss();
                    }
                });



            }
        });




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
                        Eliminar(id_grupo, holder.id,0);
                        Eliminar_Marcador_recycler.dismiss();
                    }
                });
                Eliminar_Marcador_recycler.setCancelText("Cancelar");
                Eliminar_Marcador_recycler.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
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
        private TextView titulo, idmarker,guardarcambios,cerrareditarmarker;
        private Button iniciar_viaje,eliminarmarkerbutton;
        private Switch habilitarmarcador;
        private ImageView eliminarmarker;
        private int id;
        ConstraintLayout contenedor;

        EditText Nombregrupo,DescripcionGrupo,DirreccionGrupo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.TVnombreubicacionmarker);
            eliminarmarker = itemView.findViewById(R.id.IVeliminarmaker);
            habilitarmarcador = itemView.findViewById(R.id.SWhabilitarmarker);
            contenedor = itemView.findViewById(R.id.ContenedorTarjeta);


        }
    }

    public void Habilitar(String Habilitado1, int id, ViewHolder holder) {

        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/habilitar_punto.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();

                if (Habilitado1 == "habilitado") {

                    new SweetAlertDialog(context)
                            .setTitleText("Habilitado")
                            .setContentText("Este Marcador Ahora Aparecera En Su Mapa :D")
                            .show();
                    holder.habilitarmarcador.setChecked(true);

                } else {

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


    public void CambiarPunto(String descripcion1, String idpunto, String nombre1) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/cambiar_datos_punto.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();


                    if (Objects.equals(response, "Cambios Realizados")) {


                        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Correcto!!")
                                .setContentText("Los Datos Han Sido Actualizados Correctamente!!")
                                .show();

                        bottomSheetDialogeditmarker.dismiss();


                    } else {
                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Actualizar Los Datos De Su Punto....")
                                .show();
                    }

                    PuntosMapa();

                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
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
                    pDialog.dismiss();


                    if (locationManagerinternet.getActiveNetworkInfo() != null
                            && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                            && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Obtener La Informacion Del Marcador...")
                                .show();
                    } else {
                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Por Favor Habilite Su Internet...")
                                .show();
                    }
                } catch (Exception e) {
                    pDialog.dismiss();
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


                params.put("idpunto", idpunto);
                params.put("nombre", nombre1);
                params.put("descripcion", descripcion1 + "");

                return params;
            }

        };


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
                "https://mifolderdeproyectos.online/SEEYOU/Buscar_marcadores.php?id=" + id_grupo, new Response.Listener<String>() {
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


    public void Ubicacion(int id, ViewHolder holder) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/buscar_datos_de_marcador.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();
                    JSONArray array = new JSONArray(response);

                    JSONObject cajas = array.getJSONObject(0);


                    holder.DescripcionGrupo.setText(cajas.getString("descripcion"));
                    holder.idmarker.setText(cajas.getString("IDpunto"));
                    holder.Nombregrupo.setText(cajas.getString("Nombre"));
                    holder.DirreccionGrupo.setText(cajas.getString("direccion"));
                    bottomSheetDialogeditmarker.show();


                } catch (JSONException e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("No Hemos Podido Obtener Los Datos De Su Marcador....")
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
                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Obtener La Informacion Del Marcador...")
                                .show();
                    } else {
                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Por Favor Habilite Su Internet...")
                                .show();
                    }
                } catch (Exception e) {
                    pDialog.dismiss();
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


                params.put("id", id+"");


                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }





    public void Eliminar(int id_grupo, int id_punto, int accion) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/eliminar_punto.php", new Response.Listener<String>() {
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

                if(accion == 1) {
                    bottomSheetDialogeditmarker.dismiss();
                }
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
                "https://mifolderdeproyectos.online/SEEYOU/puntos_mapa.php?id=" + id_grupo, new Response.Listener<String>() {
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
                                    .radius(50)
                                    .strokeWidth(3)
                                    .strokeColor(Color.TRANSPARENT)
                                    .fillColor(0x30DD4819));

                        }else if(preferences.getBoolean("fondo", false) == true){
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                    .radius(50)
                                    .strokeWidth(3)
                                    .strokeColor(Color.TRANSPARENT)
                                    .fillColor(0x30391B6F));
                        }
                        else if(preferences.getBoolean("fondo3", false) == true){
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                    .radius(50)
                                    .strokeWidth(3)
                                    .strokeColor(Color.TRANSPARENT)
                                    .fillColor(0x30FF0000));
                        }
                        else if(preferences.getBoolean("fondo4", false) == true){
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                    .radius(50)
                                    .strokeWidth(3)
                                    .strokeColor(Color.TRANSPARENT)
                                    .fillColor(0x3000F361));
                        } else{
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                    .radius(50)
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
