package com.example.seeyou;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {
    public GoogleMap mMap;
    LocationManager locManager;
    private ImageView ubicacion;
    private Button cancelar,enviar;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            //locManager.removeUpdates(locationListenerGPS);

           Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();

            LatLng Ubicacion = new LatLng(lat, lon);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Ubicacion,14));



            LatLng sydney = new LatLng(31.233544, -110.979941);
            LatLng seguro = new LatLng(31.240626, -110.970661);


            mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicacion de la escuela"));
            mMap.addMarker(new MarkerOptions().position(seguro).title("IMMS"));

            // Add a marker in Sydney and move the camera
            mMap.setMyLocationEnabled(true);
           // mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setPadding(0, 10, 0, 0);


        }


    };


    public void Marcador(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {




                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);

                markerOptions.title(latLng.latitude+" : " +latLng.longitude);

                mMap.addMarker(markerOptions);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));



                Dialogo_MensajeFragment dialogofragment = new Dialogo_MensajeFragment();
                dialogofragment.show(getFragmentManager(),"MyFragment");


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









            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.setOnMapClickListener(null);
                    mMap.clear();
                    cancelar.setVisibility(View.INVISIBLE);

                }
            });

            ubicacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cancelar.setVisibility(View.VISIBLE);
//                    WindowManager.LayoutParams brillo = getActivity().getWindow().getAttributes();
//                    brillo.screenBrightness= 0.05F;
//                    getActivity().getWindow().setAttributes(brillo);
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