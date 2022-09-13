package com.example.seeyou.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
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

public class FechasAdapter extends RecyclerView.Adapter<FechasAdapter.ViewHolder> {

    ArrayList<FechasRutas> UserList;
    private Context context;
    SharedPreferences preferences;
    SweetAlertDialog Eliminar_Marcador_recycler, pDialog;
    private GoogleMap mMap = RutasFragment.mMap;
    int alertapuntos = 0, alertaubicacion = 0;
    SharedPreferences.Editor editor;
    RecyclerView recyclerViewusers = RutasFragment.RVfechas;
    LocationManager locationManager;
    ConnectivityManager locationManagerinternet;

    public FechasAdapter(ArrayList<FechasRutas> UserList, Context context) {

        this.UserList = UserList;

        this.context = context;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fechas_rutas, parent, false);
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fecha.setText(UserList.get(position).getFecha_rutas());

        holder.contenerdor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("fecharuta",UserList.get(position).fecha_rutas);
                editor.commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return UserList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView fecha;
        ConstraintLayout contenerdor;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.TVfecharuta);
            contenerdor = itemView.findViewById(R.id.HorizontalRV);

        }
    }



    public void RutasUsuariosFotos() {

        try {
            pDialog.show();


            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "https://mifolderdeproyectos.online/SEEYOU/usuarios_rutas.php?id=" + preferences.getInt("id", 0), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pDialog.dismiss();

                        JSONArray array = new JSONArray(response);
                        UserList.clear();


                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);

                            UserList.add(new FechasRutas(
                                    cajas.getString("fecha")
                            ));
                        }
                        FechasAdapter adapter = new FechasAdapter(UserList, context);
                        recyclerViewusers.setHasFixedSize(true);
                        recyclerViewusers.setLayoutManager(new LinearLayoutManager(context));
                        recyclerViewusers.setAdapter(adapter);


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
                                    if (alertapuntos == 0) {

                                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("No Hemos Podido Obtener Los Puntos De Su Mapa...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                } else {
                                    if (alertapuntos == 0) {
                                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                                .show();
                                        alertapuntos = 1;
                                    }
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
            pDialog.dismiss();
            Toast.makeText(context, "Error de catch: " + e, Toast.LENGTH_SHORT).show();
        }
    }
}
