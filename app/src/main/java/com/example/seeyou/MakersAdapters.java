package com.example.seeyou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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


        holder.iniciar_viaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "ESTE BOTON HARA ALGO ALGUN DIA :D",
                        Toast.LENGTH_SHORT).show();
            }
        });

        holder.eliminarmarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "ESTE BOTON BORRARA EL MARCADOR ALGUN DIA :D",
                        Toast.LENGTH_SHORT).show();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.TVnombreubicacionmarker);
            ubicacion = itemView.findViewById(R.id.TVubicacionmarker);
            coordenadas = itemView.findViewById(R.id.TVmasinformacionmarker);
            descripcion = itemView.findViewById(R.id.TVdescripcionmarker);
            iniciar_viaje = itemView.findViewById(R.id.BTNviajarmarker);
            eliminarmarker = itemView.findViewById(R.id.BTNeliminarmarker);

        }
    }
}
