package com.example.seeyou.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.MapsFragment;
import com.example.seeyou.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GruposAdapters extends RecyclerView.Adapter<GruposAdapters.ViewHolder> {

    ArrayList<Grupos> GroupsList;
    ArrayList<UsersGroups> UsersGroupsList = new ArrayList<>();
    private Context context;
    SharedPreferences preferences;
    LocationManager locationManager;
    ConnectivityManager locationManagerinternet;
    SweetAlertDialog Eliminar_Marcador_recycler, pDialog;
    private GoogleMap mMap = MapsFragment.mMap;
    SharedPreferences.Editor editor;
    BottomSheetDialog bottomSheetDialog;
    public static RecyclerView usersgroup, recyclerviewgrupos = MapsFragment.recyclerviewgrupos;
    ArrayList<Grupos> gruposlist = new ArrayList<>();

    public GruposAdapters(ArrayList<Grupos> GroupsList, Context context) {

        this.GroupsList = GroupsList;

        this.context = context;

        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(false);

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
        preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        if (Objects.equals(GroupsList.get(position).getId_admin(),preferences.getInt("id",0))){
            holder.CVeditargrupo.setVisibility(View.VISIBLE);
        }

        holder.contraingrupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PuntosMapa(GroupsList.get(position).getId(),GroupsList.get(position).getNombre());
                preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("idusuarioruta",preferences.getInt("id",0));
                editor.putString("nombreusuarioruta",preferences.getString("Nombre",""));
                editor.putString("apellidousuarioruta",preferences.getString("Apellido",""));
                editor.commit();
            }
        });

        holder.constrainsalirgrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog Grupo = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                Grupo.setTitleText("SALIR DEL GRUPO GRUPO")
                        .setContentText("Estas A Punto De Salir De Este Grupo...Esta Accion Es Irreversible....")
                        .setConfirmText("Salir")
                        .setCancelText("Cancelar")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                                EliminarGrupo(preferences.getInt("id",0),GroupsList.get(position).getId());
                                Grupo.dismiss();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Grupo.dismiss();
                            }
                        })
                        .show();
            }
        });

        holder.CVeditargrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetBehavior<View> bottomSheetBehavior;
                bottomSheetDialog = new BottomSheetDialog
                        (context, R.style.BottomSheetDialog);
                View bottomSheetView = LayoutInflater.from(context).inflate(
                        R.layout.informacion_usuarios, null
                );
                bottomSheetDialog.setContentView(bottomSheetView);
                LinearLayout contenedor1 = bottomSheetDialog.findViewById(R.id.contenedorinfouser);
                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.92);

                assert contenedor1 != null;
                contenedor1.setMinimumHeight(height);
                bottomSheetBehavior.setMaxHeight(height);
                UsuariosGrupo(holder,GroupsList.get(position).getId());

                usersgroup = bottomSheetDialog.findViewById(R.id.RBusuariosgrupo);
            }
        });

    }




    @Override
    public int getItemCount() {
        return GroupsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombregrupo,usuarios,codigo;
        CardView contraingrupos,constrainsalirgrupo, CVeditargrupo;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombregrupo = itemView.findViewById(R.id.TVnombregrupo);
            usuarios = itemView.findViewById(R.id.TVusuariosgrupo);
            codigo = itemView.findViewById(R.id.TVcodigogrupo);
            contraingrupos = itemView.findViewById(R.id.CVgrupos);
            constrainsalirgrupo = itemView.findViewById(R.id.CVeliminargrupo);
            CVeditargrupo = itemView.findViewById(R.id.CVeditGrupo);



        }
    }

    public void UsuariosGrupo(ViewHolder holder, int id) {

        try {
            pDialog.show();
            preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "https://mifolderdeproyectos.online/SEEYOU/usuarios_grupos.php?idgroup=" + id+"&iduser=" + preferences.getInt("id",0), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pDialog.dismiss();

                        JSONArray array = new JSONArray(response);
                        UsersGroupsList.clear();


                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);

                            UsersGroupsList.add(new UsersGroups(
                                    id,
                                    cajas.getInt("idusuarios"),
                                    cajas.getString("foto"),
                                    cajas.getString("nombre"),
                                    cajas.getString("apellido")
                            ));
                        }
                        UsersGroupsAdapter adapter = new UsersGroupsAdapter(UsersGroupsList, context);
                        usersgroup.setHasFixedSize(true);
                        usersgroup.setLayoutManager(new LinearLayoutManager(context));
                        usersgroup.setAdapter(adapter);
                        bottomSheetDialog.show();

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
                                                .setContentText("No Hemos Podido Obtener A Los Usuarios...")
                                                .show();


                                } else {

                                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("Por Favor Habilite Su Internet Para Poder Cargar A Los Usuarios...")
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
        } catch (Exception e) {

        }
    }

    private void PuntosMapa(int id_grupo,String Nombre) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://mifolderdeproyectos.online/SEEYOU/puntos_mapa.php?id=" + id_grupo, new Response.Listener<String>() {
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
                        markerOptions.icon(bitmapDescriptorFromVector(context, R.drawable.markers_round_edit));

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


                        mMap.addMarker(markerOptions);
                    }

                    pDialog.dismiss();

                    /*new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Correcto!!")
                            .setContentText("Hemos Cambiador Exitosamente Al Grupo: '"+Nombre+"', Ahora Puedes Ver A Tus Amigos!!!")
                            .show();*/

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


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        try {


            Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } catch (Exception e) {

        }
        return null;
    }


    private Bitmap getMarkerBitmapFromView(String url) {
        int crash = 0;
        try {


            View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
            ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
            Picasso.get().load(url).placeholder(R.drawable.ic_baseline_arrow_circle_down_24).into(markerImageView);
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

    public void EliminarGrupo(int id_usuario, int id_grupo) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/salir_grupo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    SharedPreferences preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("idgrupo", 0);
                    editor.commit();

                    pDialog.dismiss();
                    if(Objects.equals(response,"")){
                    new SweetAlertDialog(context,
                            SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Eliminado")
                            .setContentText("Haz Salido Del Grupo Exitosamente!! ")
                            .show();
                    }else {
                        new SweetAlertDialog(context,
                                SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("OPS....")
                                .setContentText("Algo A Salido Mal Y No Se A Podido Salir Del Grupo...")
                                .show();
                    }

                    BuscarGrupos();


                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(5)")
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
                                .setContentText("No Hemos Podido Eliminar Su Marcador...")
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
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(6)")
                            .show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("idgrupo", id_grupo + "");
                params.put("idusuario", id_usuario + "");


                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void BuscarGrupos() {
        pDialog.show();

        preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://mifolderdeproyectos.online/SEEYOU/buscar_grupos.php?id=" + preferences.getInt("id",0), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    gruposlist.clear();

                    JSONArray array = new JSONArray(response);


                    int ciclo = 0, primero = 1, id = 0, id_admin = 0;


                    String Nombre = preferences.getString("Nombre", ""), codigo = "", grupo = "", verificar = preferences.getString("Nombre", "");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);


                        if (primero == 1) {
                            ciclo = cajas.getInt("idgrupo");
                            primero = 0;
                        }


                        if (cajas.getInt("idgrupo") == ciclo) {

                            if (!Objects.equals(cajas.getString("nombreusuario"), verificar)) {
                                Nombre += ", " + cajas.getString("nombreusuario");
                            }

                            codigo = cajas.getString("codigo");

                            id = cajas.getInt("idgrupo");
                            grupo = cajas.getString("nombregrupo");
                            id_admin = cajas.getInt("id_admin");

                        } else {
                            if (cajas.getInt("idgrupo") != ciclo) {
                                gruposlist.add(new Grupos(
                                        grupo,
                                        id,
                                        Nombre,
                                        codigo,
                                        id_admin
                                ));
                            }


                            ciclo = cajas.getInt("idgrupo");
                            Nombre = "";
                            Nombre = preferences.getString("Nombre", "");
                            if (!Objects.equals(cajas.getString("nombreusuario"), verificar)) {
                                Nombre += ", " + cajas.getString("nombreusuario");
                            }
                            codigo = cajas.getString("codigo");

                            id = cajas.getInt("idgrupo");
                            grupo = cajas.getString("nombregrupo");
                            id_admin = cajas.getInt("id_admin");


                        }

                    }


                    gruposlist.add(new Grupos(
                            grupo,
                            id,
                            Nombre,
                            codigo,
                            id_admin
                    ));


                    pDialog.dismiss();
                    GruposAdapters adapter = new GruposAdapters(gruposlist, context);
                    recyclerviewgrupos.setHasFixedSize(true);
                    recyclerviewgrupos.setLayoutManager(new LinearLayoutManager(context));
                    recyclerviewgrupos.setAdapter(adapter);




                } catch (JSONException e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(21)")
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
                                        .setContentText("No Hemos Podido Obtener Sus Grupos...")
                                        .show();
                            } else {
                                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Grupos...")
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
