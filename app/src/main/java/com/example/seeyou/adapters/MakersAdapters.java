package com.example.seeyou.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.MapsFragment;
import com.example.seeyou.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MakersAdapters extends RecyclerView.Adapter<MakersAdapters.ViewHolder> {


    List<Markers> MarkerList;
    private Context context;
    private RecyclerView recyclerViewmarker = MapsFragment.recyclerViewmarker;
    private int MY_DEFAULT_TIMEOUT = 15000;
    private List<Markers> markerslist = MapsFragment.markerslist;
    private List<Markers> markerslistpuntos = new ArrayList<>();
    private int id_usuario = MapsFragment.id_usuario;
    private GoogleMap mMap = MapsFragment.mMap;

    public MakersAdapters(List<Markers> markerList, Context context) {
        MarkerList = markerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tarjeta,parent,false);

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

        holder.habilitarmarcador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(MarkerList.get(position).getHabilitado(), "habilitado")) {

                    Habilitar("deshabilitado", holder.id, holder);


                } else if (Objects.equals(MarkerList.get(position).getHabilitado(), "deshabilitado")){
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
                Eliminar(id_usuario,holder.id);
            }
        });


    }

    @Override
    public int getItemCount() {
        return MarkerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titulo,ubicacion,coordenadas,descripcion,header;
        private Button iniciar_viaje,eliminarmarker;
        private Switch habilitarmarcador;
        private int id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.TVnombreubicacionmarker);
            ubicacion = itemView.findViewById(R.id.TVubicacionmarker);
            coordenadas = itemView.findViewById(R.id.TVmasinformacionmarker);
            descripcion = itemView.findViewById(R.id.TVdescripcionmarker);
            iniciar_viaje = itemView.findViewById(R.id.BTNviajarmarker);
            eliminarmarker = itemView.findViewById(R.id.BTNeliminarmarker);
            habilitarmarcador = itemView.findViewById(R.id.SWhabilitarmarker);

        }
    }
    public void Habilitar(String Habilitado1, int id, ViewHolder holder ){

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/habilitar_punto.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                    if (Habilitado1 == "habilitado") {

                        holder.habilitarmarcador.setText("habilitado");
                        Toast.makeText(context, "ESTE MARCADOR AHORA APARECERA EN SU MAPA :D", Toast.LENGTH_SHORT).show();
                        holder.habilitarmarcador.setChecked(true);

                    } else  {

                        holder.habilitarmarcador.setText("deshabilitado");
                        Toast.makeText(context, "ESTE MARCADOR YA NO APARECERA EN SU MAPA... D:", Toast.LENGTH_SHORT).show();
                        holder.habilitarmarcador.setChecked(false);
                    }

                    PuntosRecycler(1);
                PuntosMapa();



            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String > getParams() throws AuthFailureError {
                Map<String, String > params= new HashMap<String, String>();


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

    private void PuntosRecycler(int accion) {

        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/Buscar_marcadores.php?id="+id_usuario, new Response.Listener<String>() {
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

                    if(accion == 2) {
                        MakersAdapters adapter = new MakersAdapters(markerslist, context);
                        recyclerViewmarker.setHasFixedSize(true);
                        recyclerViewmarker.setLayoutManager(new LinearLayoutManager(context));
                        recyclerViewmarker.setAdapter(adapter);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        Volley.newRequestQueue(context).add(stringRequest);

    }


    public void Eliminar(int id_usuario, int id_punto ){

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/eliminar_punto.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(context, "MARCADOR ELIMINADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();

                PuntosRecycler(2);
                PuntosMapa();

            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String > getParams() throws AuthFailureError {
                Map<String, String > params= new HashMap<String, String>();


                params.put("idpunto", id_punto + "");
                params.put("idusuario", id_usuario+"");


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

        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/puntos_mapa.php?id="+id_usuario, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    mMap.clear();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);


                        MarkerOptions markerOptions = new MarkerOptions();


                        LatLng puntoubicacion =
                                new LatLng(cajas.getDouble("Latitud"),cajas.getDouble("Longitud"));
                        markerOptions.position(puntoubicacion);
                        markerOptions.title(cajas.getString("Nombre"));
                       // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.maps_round));
                        mMap.addMarker(markerOptions);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        Volley.newRequestQueue(context).add(stringRequest);

    }
}
