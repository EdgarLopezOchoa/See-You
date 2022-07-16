package com.example.seeyou.adapters;

import android.content.Context;
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

import com.example.seeyou.R;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GruposAdapters extends RecyclerView.Adapter<GruposAdapters.ViewHolder>{

    ArrayList<Grupos> GroupsList;
    private Context context;

    public GruposAdapters(ArrayList<Grupos> GroupsList, Context context) {

        this.GroupsList = GroupsList;

        this.context = context;

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

    }




    @Override
    public int getItemCount() {
        return GroupsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombregrupo,usuarios,codigo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombregrupo = itemView.findViewById(R.id.TVnombregrupo);
            usuarios = itemView.findViewById(R.id.TVusuariosgrupo);
            codigo = itemView.findViewById(R.id.TVcodigogrupo);



        }
    }
}
