package com.example.seeyou.adapters;

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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seeyou.MapsFragment;
import com.example.seeyou.R;
import com.google.android.gms.maps.GoogleMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterUsuario extends RecyclerView.Adapter<AdapterUsuario.ViewHolder> {

    ArrayList<Usuarios> UserList;
    private Context context;
    SharedPreferences preferences;
    SweetAlertDialog Eliminar_Marcador_recycler, pDialog;
    private GoogleMap mMap = MapsFragment.mMap;
    SharedPreferences.Editor editor;

    public AdapterUsuario(ArrayList<Usuarios> UserList, Context context) {

        this.UserList = UserList;

        this.context = context;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuariosrutas, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText(UserList.get(position).getNombre());
        Picasso.get()
                .load(UserList.get(position).getFoto())
                .placeholder(R.drawable.ic_baseline_arrow_circle_down_24)
                .into(holder.userimage);

        holder.contenedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("idusuarioruta",UserList.get(position).getIdusuario());
                editor.putString("nombreusuarioruta",UserList.get(position).getNombre());
                editor.commit();
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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.TVusername);
            userimage = itemView.findViewById(R.id.IVuserrutas);
            contenedor = itemView.findViewById(R.id.Layoutuserrutas);
        }
    }
}
