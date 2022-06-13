package com.example.seeyou;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MapsFragment extends Fragment {
    public static GoogleMap mMap;
    LocationManager locManager;
    private ImageView ubicacion, location;
    private Button cancelar, enviar;
    public static double LatitudDialogo, LongitudDialogo;
    private LinearLayout contenedor;
    int tiempo = 5000;

    Handler handler = new Handler();

    private OnMapReadyCallback callback = new OnMapReadyCallback() {



        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            //UbicacionEnBucle();

            obtenerubicacion();



            LatLng sydney = new LatLng(31.233544, -110.979941);
            LatLng seguro = new LatLng(31.240626, -110.970661);


            mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicacion de la escuela"));
            mMap.addMarker(new MarkerOptions().position(seguro).title("IMMS"));


            //Habilita el ver la ubicacion actual
            mMap.setMyLocationEnabled(true);
            // mMap.getUiSettings().setZoomControlsEnabled(true);

            //Habilida la funcion del compas, cuando gires la pantalla podras volver a orientarla
            mMap.getUiSettings().setCompassEnabled(true);

            //Desabilita el boton que te envia a la ubicacion tuya (porque cree un boton mas estetico para eso)
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {


                    //asigna el contenedor y el layout donde se mostrara el cuadro de dialogo
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog
                            (getContext(), R.style.BottomSheetDialog);
                    View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                            R.layout.marker_layout, (LinearLayout) contenedor
                    );
                    bottomSheetDialog.setContentView(bottomSheetView);

                    bottomSheetDialog.show();


                    //Asigna los valores a los objetos dentro el bottomsheetdialog
                    TextView nombre = bottomSheetDialog.findViewById(R.id.TVnombreubicacion);
                    TextView ubicacion = bottomSheetDialog.findViewById(R.id.TVubicacion);
                    TextView coordenada = bottomSheetDialog.findViewById(R.id.TVmasinformacion);
                    TextView descripcion = bottomSheetDialog.findViewById(R.id.TVdescripcion);
                    LatLng latLng = marker.getPosition();
                    double Latitud,logitud;
                    Latitud = latLng.latitude;
                    logitud = latLng.longitude;

                    coordenada.setText("" + Latitud + " : " + logitud);
                    nombre.setText(marker.getTitle());
                    ubicacion.setText("Aun Nose Como le pondre esta info xd");
                    descripcion.setText("AQUI PONDRE UNA DESCRIPCION CUANDO SE CONECTE A LA BD :D");

                    bottomSheetDialog.findViewById(R.id.BTNviajar).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "ESTE BOTON HARA ALGO LUEGO :D",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    bottomSheetDialog.findViewById(R.id.BTNeliminar).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "ESTE BOTON BORRARA EL MARCADOR ALGUN DIA :D",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


                    return true;
                }
            });


        }


    };

    /*public void UbicacionEnBucle() {



        handler.postDelayed(new Runnable() {
            public void run() {


                if (tiempo == 5000) {
                    // función a ejecutar
                    Toast.makeText(getContext(), "SE REPITIO :D",
                            Toast.LENGTH_SHORT).show();

                    handler.postDelayed(this, tiempo);
                }
            }

        }, tiempo);

    }*/


    public void obtenerubicacion(){

        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        //Optiene la ultima localizacion conocida del usuario
        @SuppressLint("MissingPermission")
        Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc != null) {
            //Guarda los valores de longitud y latitud en variables
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();

            //Crea el punto donde se marcara
            LatLng UbicacionActualo = new LatLng(lat, lon);

            //Mueve la camara al punto proporcionado, osea la ubicacion del usuario
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UbicacionActualo, 14));
        }else{
            Toast.makeText(getContext(), "NO SE A PODIDO OBTENER LA UBICACION", Toast.LENGTH_SHORT).show();
        }
    }

    public void Marcador() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {


                //Guarda los valores de longitud y latitud en variables
                double latitud = latLng.latitude;
                double longitud = latLng.longitude;


                //Le da a las variables globales los valores que necesitan
                LatitudDialogo = latitud;
                LongitudDialogo = longitud;


                //Guarda Todas las opciones necesarias para hacer el marcador
                MarkerOptions markerOptions = new MarkerOptions();

                //Crea el punto donde se marcara
                markerOptions.position(latLng);

                //le da nombre al punto
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);


                //Mueve la camara al punto proporcionado, osea la ubicacion del usuario
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));


                //llama al fragmento dialogo
                Dialogo_MensajeFragment dialogofragment = new Dialogo_MensajeFragment();
                dialogofragment.show(getFragmentManager(), "MyFragment");


            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        ubicacion = view.findViewById(R.id.IVubicacion);
        cancelar = view.findViewById(R.id.BTNcancelar);
        enviar = view.findViewById(R.id.BTNenviarmensaje);
        location = view.findViewById(R.id.IVlocation);
        contenedor = view.findViewById(R.id.ContenedorTarjeta);


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obtenerubicacion();

            }
        });


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //desactiva la funcion de marcar puntos al hacer click
                mMap.setOnMapClickListener(null);

                //vuelve invisible el boton de cancelar
                cancelar.setVisibility(View.INVISIBLE);

            }
        });

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //vuelve visible el boton de cancelar
                cancelar.setVisibility(View.VISIBLE);

                    /*hacia una prueba donde bajaba el brillo a la pantalla cuando hacia clic a esta imagen,
                     funciona pero no tiene utilidad de momento
                     */

                    /*WindowManager.LayoutParams brillo = getActivity().getWindow().getAttributes();
                    brillo.screenBrightness= 0.05F;
                    getActivity().getWindow().setAttributes(brillo);
                    */

                Marcador();
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }
}


