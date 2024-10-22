package com.example.seeyou.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.MapsFragment;
import com.example.seeyou.R;
import com.example.seeyou.RutasFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterUsuario extends RecyclerView.Adapter<AdapterUsuario.ViewHolder> {

    ArrayList<Usuarios> UserList;
    Activity activity;
    private Context context;
    SharedPreferences preferences;
    LinearLayoutManager horizontallayout;
    SweetAlertDialog Eliminar_Marcador_recycler, pDialog;
    private GoogleMap mMap = RutasFragment.mMap;
    SharedPreferences.Editor editor;
    int alertapuntos = 0, height = RutasFragment.height;
    LocationManager locationManager;
    RecyclerView RVfechas = RutasFragment.RVfechas;
    ArrayList<FechasRutas> FechasList = new ArrayList<>();
    ConnectivityManager locationManagerinternet;
    RecyclerView recyclerViewusers = RutasFragment.recyclerViewusers;

    public AdapterUsuario(ArrayList<Usuarios> UserList, Context context, Activity activity) {

        this.UserList = UserList;
        this.activity = activity;
        this.context = context;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuariosrutas, parent, false);
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);



        preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        holder.username.setText(UserList.get(position).getNombre());
        Picasso.get()
                .load(UserList.get(position).getFoto())
                .placeholder(R.drawable.user)
                .noFade()
                .into(holder.userimage);

        if (Objects.equals(preferences.getInt("idusuarioruta", 0), UserList.get(position).getIdusuario())) {

            if(position == 0) {
                holder.constrainuserrutas.setBackgroundResource(R.drawable.button2_top);
                holder.username.setTextColor(Color.WHITE);
            }else{
                holder.constrainuserrutas.setBackgroundResource(R.drawable.buttonfondo5);
                holder.username.setTextColor(Color.WHITE);
            }

            if(UserList.size() == position+1){
                holder.constrainuserrutas.setBackgroundResource(R.drawable.button2_bottom);
                holder.username.setTextColor(Color.WHITE);
            }
        }

        holder.contenedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbicacionUsuario(UserList.get(position).getIdusuario());
                preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("idusuarioruta", UserList.get(position).getIdusuario());
                editor.putString("nombreusuarioruta", UserList.get(position).getNombre());
                editor.putString("apellidousuarioruta", UserList.get(position).getApellido());
                editor.putString("fecharuta", "hoy");
                editor.commit();
                FechasRutas(UserList.get(position).getIdusuario());
                AdapterUsuario adapter = new AdapterUsuario(UserList, context, activity);

                height = RutasFragment.height;
                recyclerViewusers.setHasFixedSize(true);
                recyclerViewusers.setLayoutManager(new LinearLayoutManager(context));
                recyclerViewusers.setAdapter(adapter);
                if (height == (displaymetrics.heightPixels * 78) / 100) {
                    recyclerViewusers.suppressLayout(true);
                } else {
                    recyclerViewusers.suppressLayout(false);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return UserList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        ImageView userimage;
        LinearLayout contenedor;
        ConstraintLayout constrainuserrutas;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.TVusername);
            userimage = itemView.findViewById(R.id.IVuserrutas);
            contenedor = itemView.findViewById(R.id.Layoutuserrutas);
            constrainuserrutas = itemView.findViewById(R.id.constrainuserrutas);
        }
    }

    public void FechasRutas(int id) {

        try {
            pDialog.show();


            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "https://mifolderdeproyectos.online/SEEYOU/fecha_puntos_recorridos.php?id=" + preferences.getInt("idusuarioruta", preferences.getInt("id", 0))
                            + "&idusuario=" + preferences.getInt("id", 0) + "&idgrupo=" + preferences.getInt("idgrupo", 0), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pDialog.dismiss();

                        JSONArray array = new JSONArray(response);
                        FechasList.clear();


                        horizontallayout = new LinearLayoutManager(context, horizontallayout.HORIZONTAL, false);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);

                            FechasList.add(new FechasRutas(
                                    cajas.getString("fecha"),
                                    cajas.getString("mes"),
                                    cajas.getString("dia")
                            ));
                        }
                        FechasAdapter adapter = new FechasAdapter(FechasList, context);
                        RVfechas.setHasFixedSize(true);
                        RVfechas.setLayoutManager(horizontallayout);
                        RVfechas.setAdapter(adapter);


                    } catch (JSONException e) {
                        pDialog.dismiss();
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
                                pDialog.dismiss();
                                if (locationManagerinternet.getActiveNetworkInfo() != null
                                        && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                        && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                                    if (alertapuntos == 0) {

                                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("No Hemos Podido Obtener A Los Usuarios...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                } else {
                                    if (alertapuntos == 0) {
                                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("Por Favor Habilite Su Internet Para Poder Cargar A Los Usuarios...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                }
                            } catch (Exception e) {
                                pDialog.dismiss();
                                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                        .show();
                            }
                        }
                    });
            Volley.newRequestQueue(context).add(stringRequest);
        } catch (Exception e) {

        }
    }


    public void UbicacionUsuario(int id) {

        try {
            pDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "https://mifolderdeproyectos.online/SEEYOU/ubicacion_usuario_rutas.php?id=" + id, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pDialog.dismiss();

                        JSONArray array = new JSONArray(response);

                        LatLng ubicacionActual;
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);
                            if (!Objects.equals(response, "[]")) {
                                ubicacionActual = new LatLng(cajas.getDouble("latitud"), cajas.getDouble("longitud"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, 13));
                            }
                        }


                    } catch (JSONException e) {
                        pDialog.dismiss();
                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
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
                                pDialog.dismiss();
                                if (locationManagerinternet.getActiveNetworkInfo() != null
                                        && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                        && locationManagerinternet.getActiveNetworkInfo().isConnected()) {


                                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Algo Salio Mal..")
                                            .setContentText("No Hemos Podido Obtener La Ubicacion De Este Usuario...")
                                            .show();


                                } else {

                                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Algo Salio Mal..")
                                            .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                            .show();

                                }

                            } catch (Exception e) {
                                pDialog.dismiss();
                                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(2)")
                                        .show();
                            }
                        }
                    });
            Volley.newRequestQueue(context).add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(context, e + "", Toast.LENGTH_SHORT).show();
        }
    }

}
