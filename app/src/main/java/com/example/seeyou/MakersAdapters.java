package com.example.seeyou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MakersAdapters extends RecyclerView.Adapter<MakersAdapters.ViewHolder> {


    List<Markers> MarkerList;
    private Context context;

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
        holder.titulo.setText(MarkerList.get(position).getName());
        holder.descripcion.setText(MarkerList.get(position).getDescripction());
        holder.coordenadas.setText(MarkerList.get(position).getCoordenadas());
        holder.ubicacion.setText(MarkerList.get(position).getUbicacion());
        holder.header.setText(MarkerList.get(position).getTitulo());
    }

    @Override
    public int getItemCount() {
        return MarkerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titulo,ubicacion,coordenadas,descripcion,header;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.TVnombreubicacion);
            ubicacion = itemView.findViewById(R.id.TVubicacion);
            coordenadas = itemView.findViewById(R.id.TVmasinformacion);
            descripcion = itemView.findViewById(R.id.TVdescripcion);
            header = itemView.findViewById(R.id.TVmarcador);

        }
    }
}
