package com.example.seeyou.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.example.seeyou.RutasFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UsersGroupsAdapter extends RecyclerView.Adapter<UsersGroupsAdapter.ViewHolder> {

    ArrayList<UsersGroups> UserList;
    private Context context;
    SharedPreferences preferences;
    SweetAlertDialog Eliminar_Marcador_recycler, pDialog;
    private GoogleMap mMap = RutasFragment.mMap;
    int alertapuntos = 0, alertaubicacion = 0;
    SharedPreferences.Editor editor;
    RecyclerView recyclerViewusers = RutasFragment.RVfechas;
    LocationManager locationManager;
    ConnectivityManager locationManagerinternet;

    public UsersGroupsAdapter(ArrayList<UsersGroups> UserList, Context context) {

        this.UserList = UserList;

        this.context = context;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_group, parent, false);
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombre.setText(UserList.get(position).getNombre() +" " + UserList.get(position).getApellido());
        Picasso.get()
                .load(UserList.get(position).getFoto())
                .placeholder(R.drawable.user)
                .into(holder.fotouser);

        holder.deleteuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog Grupo = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                Grupo.setTitleText("ELIMINAR USUARI0")
                        .setContentText("Estas A Punto De Eliminar A Este Usuario DEl Grupo...Esta Accion Es Irreversible....")
                        .setConfirmText("Eliminar")
                        .setCancelText("Cancelar")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                                EliminarGrupo(UserList.get(position).getId_usuario(),UserList.get(position).getId_grupo());
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
    }



    @Override
    public int getItemCount() {
        return UserList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView fotouser,deleteuser;
        EditText nombre;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fotouser = itemView.findViewById(R.id.IVusergroup);
            nombre = itemView.findViewById(R.id.ETnombreusuariogrupo);
            deleteuser = itemView.findViewById(R.id.IVeliminarusuario);
        }
    }

    public void EliminarGrupo(int id_usuario, int id_grupo) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/salir_grupo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    pDialog.dismiss();
                    if(Objects.equals(response,"")){
                        new SweetAlertDialog(context,
                                SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Eliminado")
                                .setContentText("Haz Eliminado Al Usuario Del Grupo Exitosamente!! ")
                                .show();
                    }else {
                        new SweetAlertDialog(context,
                                SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("OPS....")
                                .setContentText("Algo A Salido Mal Y No Se A Podido Salir Del Grupo...")
                                .show();
                    }



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

}
