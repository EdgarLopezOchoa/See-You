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
    }


    @Override
    public int getItemCount() {
        return UserList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView fotouser;
        EditText nombre;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fotouser = itemView.findViewById(R.id.IVusergroup);
            nombre = itemView.findViewById(R.id.ETnombreusuariogrupo);
        }
    }


}
