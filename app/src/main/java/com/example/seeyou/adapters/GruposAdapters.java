package com.example.seeyou.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.MapsFragment;
import com.example.seeyou.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GruposAdapters extends RecyclerView.Adapter<GruposAdapters.ViewHolder> {

    ArrayList<Grupos> GroupsList;
    private Context context;
    SharedPreferences preferences;
    LocationManager locationManager;
    ConnectivityManager locationManagerinternet;
    SweetAlertDialog Eliminar_Marcador_recycler, pDialog;
    private GoogleMap mMap = MapsFragment.mMap;

    public GruposAdapters(ArrayList<Grupos> GroupsList, Context context) {

        this.GroupsList = GroupsList;

        this.context = context;

        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(true);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_grupos, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombregrupo.setText(GroupsList.get(position).getNombre());
        holder.usuarios.setText(GroupsList.get(position).getUsuarios());
        holder.codigo.setText(GroupsList.get(position).getCodigo());

        holder.contraingrupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PuntosMapa(GroupsList.get(position).getId(),GroupsList.get(position).getNombre());

            }
        });

    }




    @Override
    public int getItemCount() {
        return GroupsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombregrupo,usuarios,codigo;
        ConstraintLayout contraingrupos;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombregrupo = itemView.findViewById(R.id.TVnombregrupo);
            usuarios = itemView.findViewById(R.id.TVusuariosgrupo);
            codigo = itemView.findViewById(R.id.TVcodigogrupo);
            contraingrupos = itemView.findViewById(R.id.ConstrainGrupos);



        }
    }

    private void PuntosMapa(int id_grupo,String Nombre) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/puntos_mapa.php?id=" + id_grupo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);

                    mMap.clear();
                    preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putInt("idgrupo",id_grupo);
                    editor.commit();
                    MapsFragment.cerrargrupo();


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

                    pDialog.dismiss();

                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Correcto!!")
                            .setContentText("Hemos Cambiador Exitosamente Al Grupo: '"+Nombre+"', Ahora Puedes Ver A Tus Amigos!!!")
                            .show();

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
                                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("No Hemos Podido Obtener Los Puntos De Su Mapa...")
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
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                    .show();
                        }
                    }
                });

        Volley.newRequestQueue(context).add(stringRequest);

    }
}
