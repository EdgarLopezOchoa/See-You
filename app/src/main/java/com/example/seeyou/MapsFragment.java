package com.example.seeyou;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.adapters.MakersAdapters;
import com.example.seeyou.adapters.Markers;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment {
    public static GoogleMap mMap;
    LocationManager locManager;
    private ImageView ubicacion, location,vermarkers, cambiarmapa;
    private Button cancelar, enviar;
    public static double LatitudDialogo, LongitudDialogo;
    private LinearLayout contenedor;
    private SearchView SVubicacion;
    private TextView titulo;
    List<Markers> markerslist = new ArrayList<>();
    public static String direccion = "";
    int tiempo = 5000;
    int bucleubicacion = 0;
    View view;


    private com.google.android.gms.location.LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */


    private OnMapReadyCallback callback = new OnMapReadyCallback() {



        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {

            try {

                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            mMap = googleMap;

            PuntosRecycler();

            //startLocationUpdates();

            if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                getLastLocation();
            }

            LatLng sydney = new LatLng(31.233544, -110.979941);
            LatLng seguro = new LatLng(31.240626, -110.970661);

            mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicacion de la escuela")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            mMap.addMarker(new MarkerOptions().position(seguro).title("IMMS")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));


            //Habilita el ver la ubicacion actual
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
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

                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(Latitud, logitud, 1);

                    Address address = (Address) addresses.get(0);
                    String addressStr = "";
                    addressStr += address.getAddressLine(0);

                        ubicacion.setText(addressStr);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    coordenada.setText("" + Latitud + " : " + logitud);
                    nombre.setText(marker.getTitle());

                    descripcion.setText("AQUI PONDRE UNA DESCRIPCION CUANDO SE CONECTE A LA BD :D");

                    bottomSheetDialog.findViewById(R.id.BTNviajar).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "ESTE BOTON HARA ALGO ALGUN DIA :D",
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

            }catch (Exception e){
                Toast.makeText(getContext(), "NECESITAS ACTIVAR LA UBICACION Y " +
                        "DAR PERMISOS A LA APP", Toast.LENGTH_SHORT).show();
            }
        }




    };


    //PROBANDO CODIGO DE UBICACION VIA GOOGLE




    private void PuntosRecycler() {

        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/Buscar_marcadores.php?id=1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    markerslist.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);

                        markerslist.add(new Markers(

                                cajas.getInt("IDpunto"),
                                cajas.getString("Nombre"),
                                cajas.getDouble("Longitud"),
                                cajas.getDouble("Latitud"),
                                cajas.getString("direccion"),
                                cajas.getString("descripcion")
                        ));
                        MarkerOptions markerOptions = new MarkerOptions();


                        LatLng puntoubicacion =
                                new LatLng(cajas.getDouble("Latitud"),cajas.getDouble("Longitud"));
                        markerOptions.position(puntoubicacion);
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
        Volley.newRequestQueue(getContext()).add(stringRequest);

    }




    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)


        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            LatLng UbicacionActualo = new LatLng(location.getLatitude(), location.getLongitude());

                            //Mueve la camara al punto proporcionado, osea la ubicacion del usuario
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UbicacionActualo, 14));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "NO SE A PODIDO OBTENER TU ULTIMA UBICACION",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //HASTA AQUI TERMINA EL CODIGO DE UBICACION VIA GOOGLE

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


                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);

                    Address address = (Address) addresses.get(0);
                    direccion += address.getAddressLine(0);



                } catch (IOException e) {
                    e.printStackTrace();
                }


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
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        ubicacion = view.findViewById(R.id.IVubicacion);
        cancelar = view.findViewById(R.id.BTNcancelar);
        enviar = view.findViewById(R.id.BTNenviarmensaje);
        location = view.findViewById(R.id.IVlocation);
        contenedor = view.findViewById(R.id.Contenedormarker);
        vermarkers = view.findViewById(R.id.IVvermarkers);
        SVubicacion = view.findViewById(R.id.SVubicacion);
        titulo = view.findViewById(R.id.TVtituloseeyou);
        cambiarmapa = view.findViewById(R.id.IVcambiarmapa);




        cambiarmapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }else if (mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });



        SVubicacion.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = SVubicacion.getQuery().toString();
                List<Address> addressList = null;
                if (location != null & !location.equals("")){
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }catch (Exception e){
                        Toast.makeText(getContext(), "UBICACION NO ENCONTRADA....", Toast.LENGTH_SHORT).show();
                    }
                    }else{
                    Toast.makeText(getContext(), "FAVOR DE INTRODUCIR UN NOMBRE VALIDO.", Toast.LENGTH_SHORT).show();


                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        SVubicacion.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titulo.setVisibility(View.INVISIBLE);
                SVubicacion.setBackgroundResource(R.drawable.searchbar);

            }
        });

        SVubicacion.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                titulo.setVisibility(View.VISIBLE);
                SVubicacion.setBackgroundResource(R.color.Trasparente);
                return false;
            }
        });





        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLastLocation();

            }
        });


        vermarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog
                        (getContext(), R.style.BottomSheetDialog);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                        R.layout.markersbottomshet, (LinearLayout) view.findViewById(R.id.ContenedorTarjeta)
                );
                bottomSheetDialog.setContentView(bottomSheetView);

                bottomSheetDialog.show();
                bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                RecyclerView recyclerView = bottomSheetDialog.findViewById(R.id.RVmarkersbottomsheet);


                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


                PuntosRecycler();
                MakersAdapters adapter = new MakersAdapters(markerslist, getContext());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);

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


