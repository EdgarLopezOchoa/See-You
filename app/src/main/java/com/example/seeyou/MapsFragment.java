package com.example.seeyou;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seeyou.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LocationManager locManager;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
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


            //UBICACIONES
            /* loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();

            LatLng sydney = new LatLng(lat, lon);


            mMap.addMarker(new MarkerOptions().position(sydney).title("Esta Es Tu Ubicacion"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14));*/


            LatLng sydney = new LatLng(31.233544, -110.979941);
            LatLng seguro = new LatLng(31.240626, -110.970661);


            mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicacion de la escuela"));
            mMap.addMarker(new MarkerOptions().position(seguro).title("IMMS"));

            // Add a marker in Sydney and move the camera
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setPadding(0, 10, 0, 0);
        }


    };

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_maps, container, false);
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