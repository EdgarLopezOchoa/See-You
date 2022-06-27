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
import androidx.recyclerview.widget.RecyclerView;

import com.example.seeyou.R;

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
        holder.titulo.setText(MarkerList.get(position).getNombre());
        holder.descripcion.setText(MarkerList.get(position).getDescripcion());
        holder.coordenadas.setText(MarkerList.get(position).getLatitud() + " , " + MarkerList.get(position).getLongitud());
        holder.ubicacion.setText(MarkerList.get(position).getDireccion());



        if (MarkerList.get(position).getHabilitado() == "habilitado") {
            holder.habilitarmarcador.setChecked(true);
        } else {
            holder.habilitarmarcador.setChecked(false);
        }

        holder.habilitarmarcador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(context, MarkerList.get(position).getHabilitado(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        holder.iniciar_viaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prueba_de_id(MarkerList.get(position).getId());
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
        private Switch habilitarmarcador;

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
    private void prueba_de_id(int id){
        Toast.makeText(context, "ESTE ES EL ID DE ESTE MARCADOR: "+ id,
                Toast.LENGTH_SHORT).show();
    }
}
