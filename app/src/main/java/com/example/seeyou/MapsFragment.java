package com.example.seeyou;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.adapters.Grupos;
import com.example.seeyou.adapters.GruposAdapters;
import com.example.seeyou.adapters.MakersAdapters;
import com.example.seeyou.adapters.Markers;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

//IMPLEMENT PARA GRAFICAR LAS LINEAS
public class MapsFragment extends Fragment implements GoogleMap.OnPolylineClickListener {
    public static GoogleMap mMap, mapubicacion;
    private static ObjectAnimator animacionDesvanecido;
    private static ObjectAnimator animacionbuttons;
    private static ObjectAnimator animacionRotation;

    public static ImageView closerecycler, cerrarunir, IVcerrarcreargrupo;
    ImageView NotificacionAlerta,IVusermarker, IVsettings;
    private ImageView ubicacion, location, vermarkers, cambiarmapa, grupos;
    private Button cancelar, enviar, cancelarviaje;
    public static Button agregargrupo, creargrupo;
    public static double LatitudDialogo, LongitudDialogo;
    private LinearLayout contenedor;
    private SearchView SVubicacion, SVpunto;
    private TextView nombremarcador, TVidmarker, TValertamarcador;
    public static RecyclerView recyclerViewmarker, recyclerviewgrupos;
    public static ArrayList<Markers> markerslist = new ArrayList<>();
    public static ArrayList<Grupos> gruposlist = new ArrayList<>();
    public static String direccion = "", Codigogrupo = "";
    public static int id_usuario = 0, id_grupo = 0, cerrarbucle = 0;
    String addressStr, NombreGrupo1;
    int alertapuntos = 0, alertaubicacion = 0, nomasviaje = 0, settings = 0;
    public static BottomSheetDialog bottomSheetDialog, bottomSheetDialogmarker, bottomSheetDialogunirse, bottomSheetDialogcreargrupo;
    public static Toolbar TBgrupos;
    SweetAlertDialog Eliminar_Marcador;
    View view;
    SweetAlertDialog pDialog;
    LocationManager locationManager;
    MakersAdapters adapter;
    RequestQueue requestQueue,requestQueueUsers = null,requestQueuePuntos = null;
    ConnectivityManager locationManagerinternet;
    NetworkInfo networkInfo;
    TextView ubicacionmarcador, coordenadamarcador, descripcionmarcador,TVusermarker;
    Switch habilitado;
    SharedPreferences preferences;
    EditText Nombregrupo;
    Marker marker[] = new Marker[20];
    Double Latitudruta, Longitudruta;
    Polyline polylineruta = null;



    private com.google.android.gms.location.LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 sec */

    // VARIABLES DEL MAU
    // Color y tamaño para las lineas
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    //Cambiar lineas con un clic a otro estilo
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    // FIN

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @SuppressLint({"MissingPermission"})
        @Override
        public void onMapReady(GoogleMap googleMap) {

            adapter = new MakersAdapters(markerslist, getContext());
            try {
                pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setTitleText("Cargando ...");
                pDialog.setCancelable(false);

                pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        pDialog.dismiss();
                    }
                });


                locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
                locationManagerinternet = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
                networkInfo = locationManagerinternet.getActiveNetworkInfo();


                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                SVubicacion.setQuery("", false);
                SVubicacion.setIconified(true);
                SVubicacion.clearFocus();
                SVubicacion.setEnabled(false);

                mMap = googleMap;

                ejecutar();
                PuntosMapa();


                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation(1);
                }


                //Habilita el ver la ubicacion actual
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);


                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDrag(@NonNull Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(@NonNull Marker marker) {

                        try {

                            String id = marker.getTitle();
                            double Latitud, logitud;
                            Latitud = marker.getPosition().latitude;
                            logitud = marker.getPosition().longitude;


                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                            List<Address> addresses = null;

                            addresses = geocoder.getFromLocation(Latitud, logitud, 1);


                            Address address = (Address) addresses.get(0);
                            addressStr = "";
                            addressStr += address.getAddressLine(0);

                            Eliminar_Marcador = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                            Eliminar_Marcador.setTitleText("¿Estas Seguro?");
                            Eliminar_Marcador.setContentText("Estas Apunto De Cambiar La Ubicacion De Este Marcador..");
                            Eliminar_Marcador.setConfirmText("Cambiar");
                            Eliminar_Marcador.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    UbicacionPunto(Latitud, logitud, id, addressStr);
                                    Eliminar_Marcador.dismiss();
                                }
                            });
                            Eliminar_Marcador.setCancelText("Cancelar");
                            Eliminar_Marcador.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Eliminar_Marcador.dismiss();
                                    PuntosMapa();
                                }
                            });
                            Eliminar_Marcador.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    PuntosMapa();

                                }
                            });
                            Eliminar_Marcador.show();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMarkerDragStart(@NonNull Marker marker) {

                    }
                });


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {


                        //asigna el contenedor y el layout donde se mostrara el cuadro de dialogo
                        bottomSheetDialogmarker = new BottomSheetDialog
                                (getContext(), R.style.BottomSheetDialog);
                        View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                                R.layout.marker_layout, (LinearLayout) contenedor
                        );

                        bottomSheetDialogmarker.setContentView(bottomSheetView);


                        //Asigna los valores a los objetos dentro el bottomsheetdialog
                        nombremarcador = bottomSheetDialogmarker.findViewById(R.id.TVnombreubicacion);
                        ubicacionmarcador = bottomSheetDialogmarker.findViewById(R.id.TVubicacion);
                        descripcionmarcador = bottomSheetDialogmarker.findViewById(R.id.TVdescripcion);
                        habilitado = bottomSheetDialogmarker.findViewById(R.id.SWhabilitar);
                        TVidmarker = bottomSheetDialogmarker.findViewById(R.id.TVbotonesmarker);
                        Button btneliminar = bottomSheetDialogmarker.findViewById(R.id.BTNeliminar);
                        Button iniciarviaje = bottomSheetDialogmarker.findViewById(R.id.BTNviajar);
                        IVusermarker = bottomSheetDialogmarker.findViewById(R.id.IVusermarker);
                        TVusermarker = bottomSheetDialogmarker.findViewById(R.id.TVusermarker);


                        if (preferences.getBoolean("fondo2", false) == true) {
                            iniciarviaje.setBackgroundResource(R.drawable.buttonfondo2);

                            ColorStateList buttonStates = new ColorStateList(
                                    new int[][]{
                                            new int[]{-android.R.attr.state_enabled},
                                            new int[]{android.R.attr.state_checked},
                                            new int[]{}
                                    },
                                    new int[]{
                                            Color.BLUE,
                                            Color.MAGENTA,
                                            Color.RED
                                    }
                            );

                            habilitado.getThumbDrawable().setTintList(buttonStates);
                            habilitado.getTrackDrawable().setTintList(buttonStates);

                        } else if (preferences.getBoolean("fondo", false) == true) {
                            iniciarviaje.setBackgroundResource(R.drawable.button2);
                            ColorStateList buttonStates = new ColorStateList(
                                    new int[][]{
                                            new int[]{-android.R.attr.state_enabled},
                                            new int[]{android.R.attr.state_checked},
                                            new int[]{}
                                    },
                                    new int[]{
                                            Color.BLUE,
                                            Color.BLUE,
                                            Color.RED
                                    }
                            );

                            habilitado.getThumbDrawable().setTintList(buttonStates);
                            habilitado.getTrackDrawable().setTintList(buttonStates);
                        } else if (preferences.getBoolean("fondo3", false) == true) {
                            iniciarviaje.setBackgroundResource(R.drawable.buttonfondo3);
                            ColorStateList buttonStates = new ColorStateList(
                                    new int[][]{
                                            new int[]{-android.R.attr.state_enabled},
                                            new int[]{android.R.attr.state_checked},
                                            new int[]{}
                                    },
                                    new int[]{
                                            Color.BLUE,
                                            Color.RED,
                                            Color.DKGRAY
                                    }
                            );

                            habilitado.getThumbDrawable().setTintList(buttonStates);
                            habilitado.getTrackDrawable().setTintList(buttonStates);
                        } else if (preferences.getBoolean("fondo4", false) == true) {
                            iniciarviaje.setBackgroundResource(R.drawable.buttonfondo4);
                            ColorStateList buttonStates = new ColorStateList(
                                    new int[][]{
                                            new int[]{-android.R.attr.state_enabled},
                                            new int[]{android.R.attr.state_checked},
                                            new int[]{}
                                    },
                                    new int[]{
                                            Color.BLUE,
                                            Color.GREEN,
                                            Color.RED
                                    }
                            );

                            habilitado.getThumbDrawable().setTintList(buttonStates);
                            habilitado.getTrackDrawable().setTintList(buttonStates);
                        }


                        habilitado.setText("habilitado");
                        habilitado.setChecked(true);

                        LatLng latLng = marker.getPosition();
                        double Latitud, logitud;
                        Latitud = latLng.latitude;
                        logitud = latLng.longitude;

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(Latitud, logitud, 1);

                            Address address = (Address) addresses.get(0);
                            addressStr = "";
                            addressStr += address.getAddressLine(0);

                            ubicacionmarcador.setText(addressStr);

                        } catch (IOException e) {
                            pDialog.dismiss();
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(3)")
                                    .show();
                        }

                        nombremarcador.setText(marker.getTitle());

                        if (!Objects.equals(marker.getTitle(), "Nombre:")) {
                            DatosMarker(marker.getTitle());
                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                        }

                        iniciarviaje.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomSheetDialogmarker.dismiss();
                                cancelarviaje.setVisibility(View.VISIBLE);
                                mMap.clear();
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("viaje", true);
                                editor.commit();
                                nomasviaje = 0;
                                PuntosMapa();
                                cerrarbucle = 1;
                                double latitud = marker.getPosition().latitude,
                                        longitud = marker.getPosition().longitude;
                                LatLng ubicacion = new LatLng(Latitudruta, Longitudruta);

                                if (polylineruta != null) {
                                    polylineruta.remove();
                                }


                                rutastrazo(latitud, longitud);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 18));

                            }
                        });

                        btneliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Eliminar_Marcador = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                                Eliminar_Marcador.setTitleText("¿Estas Seguro?");
                                Eliminar_Marcador.setContentText("Este Marcador Ya No Se Podra Recuperar..");
                                Eliminar_Marcador.setConfirmText("Eliminar");
                                Eliminar_Marcador.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        Eliminar(preferences.getInt("idgrupo", 0), marker.getTitle());
                                        Eliminar_Marcador.dismiss();

                                    }
                                });
                                Eliminar_Marcador.setCancelText("Cancelar");
                                Eliminar_Marcador.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Eliminar_Marcador.dismiss();
                                    }
                                });
                                Eliminar_Marcador.show();


                            }
                        });

                        habilitado.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (habilitado.getText().toString() == "habilitado") {
                                    Habilitar("deshabilitado", marker.getTitle());

                                } else {
                                    Habilitar("habilitado", marker.getTitle());

                                }
                            }
                        });


                        return true;
                    }
                });

            } catch (Exception e) {
                pDialog.dismiss();
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Algo Salio Mal..")
                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(4)")
                        .show();
            }
        }


    };


    public void Notificar(int notID) {
        NotificationCompat.Builder creador;
        String canalID = "MiCanal01";
        NotificationManager notificador = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
        // Si nuestro dispositivo tiene Android 8 (API 26, Oreo) o superior
        creador = new NotificationCompat.Builder(getContext(), canalID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String canalNombre = "ALERTA";
            String canalDescribe = "ALGUIEN NECESITA TU AYUDA!!!!";
            int importancia = NotificationManager.IMPORTANCE_MAX;
            @SuppressLint("WrongConstant")
            NotificationChannel miCanal = new NotificationChannel(canalID, canalNombre, importancia);
            miCanal.setDescription(canalDescribe);
            miCanal.enableLights(true);
            miCanal.setLightColor(Color.BLUE); // Esto no lo soportan todos los dispositivos
            miCanal.enableVibration(true);
            notificador.createNotificationChannel(miCanal);

        }
        Bitmap iconoNotifica = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.emergency);
        int iconoSmall = R.mipmap.icon_foreground;
        creador.setSmallIcon(iconoSmall);
        creador.setLargeIcon(iconoNotifica);
        creador.setContentTitle("ALERTA!!");
        creador.setContentText("ALGUIEN NECESITA TU AYUDA!!");
        creador.setStyle(new NotificationCompat.BigTextStyle().bigText("ALGUIEN NECESITA TU AYUDA!!"));
        creador.setChannelId(canalID);
        notificador.notify(notID, creador.build());
    }


    public void rutastrazo(double latitud, double longitud) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    if (nomasviaje == 0) {
                        if (polylineruta != null) {
                            polylineruta.remove();
                        }
                        getLastLocation(0);
                        polylineruta = mMap.addPolyline(new PolylineOptions().add(
                                new LatLng(Latitudruta, Longitudruta),
                                new LatLng(latitud, longitud)
                        ));

                        polylineruta.setTag("A");
                        stylePolyline(polylineruta);

                        handler.postDelayed(this, 1000);
                    }

                } catch (Exception e) {

                }


            }
        }, 1000);


    }


    // EVENTOS DEL MAU
    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }


    }

    // Estilo para las lineas
    private void stylePolyline(Polyline polyline) {

        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(android.R.drawable.presence_online), 10));
                polyline.setEndCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(android.R.drawable.presence_busy), 10));
                break;
        }

        //polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);

    }
    // FIN

    private void ejecutar() {
        final Handler handler = new Handler();
        final Handler handler2 = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (cerrarbucle == 0) {
                    usuariosMapa();

                    handler.postDelayed(this, 3000);
                }
            }
        }, 3000);

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                PuntosMapa();


                handler.postDelayed(this, 9000);
            }
        }, 9000);
    }


    public void Eliminar(int id_usuario, String id_punto) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/eliminar_punto.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(),
                            SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Eliminado")
                            .setContentText("El Marcado Ha Sido Eliminado Correctamente")
                            .show();
                    bottomSheetDialogmarker.dismiss();


                    PuntosMapa();


                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(5)")
                            .show();
                }
            }


        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    pDialog.dismiss();
                    if (locationManagerinternet.getActiveNetworkInfo() != null
                            && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                            && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Eliminar Su Marcador...")
                                .show();
                    } else {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Por Favor Habilite Su Internet...")
                                .show();
                    }
                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(6)")
                            .show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("idpunto", id_punto + "");
                params.put("idusuario", id_usuario + "");


                return params;
            }

        };


        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    public void Habilitar(String Habilitado1, String id) {
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/habilitar_punto.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();

                    if (Habilitado1 == "habilitado") {

                        habilitado.setText("habilitado");
                        new SweetAlertDialog(getContext())
                                .setTitleText("Habilitado")
                                .setContentText("Este Marcador Ahora Aparecera En Su Mapa :D")
                                .show();
                        habilitado.setChecked(true);

                    } else {

                        habilitado.setText("deshabilitado");
                        new SweetAlertDialog(getContext())
                                .setTitleText("Deshabilitado")
                                .setContentText("Este Marcador Ya No Aparecera En Su Mapa... D:")
                                .show();
                        habilitado.setChecked(false);
                    }

                    PuntosMapa();


                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(7)")
                            .show();
                }

            }

        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    pDialog.dismiss();
                    if (Habilitado1 == "habilitado") {
                        habilitado.setChecked(false);

                    } else {
                        habilitado.setChecked(true);
                    }
                    if (locationManagerinternet.getActiveNetworkInfo() != null
                            && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                            && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Cambiar El Estado De Su Marcador...")
                                .show();
                    } else {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Por Favor Habilite Su Internet...")
                                .show();
                    }
                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(8)")
                            .show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("idpunto", id);
                params.put("habilitado", Habilitado1);


                return params;
            }

        };


        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    private void PuntosRecycler() {

        pDialog.show();

        id_grupo = preferences.getInt("idgrupo", 0);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://mifolderdeproyectos.online/SEEYOU/Buscar_marcadores.php?id=" + id_grupo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                    markerslist.clear();

                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);

                        markerslist.add(new Markers(
                                cajas.getString("habilitado"),
                                cajas.getInt("IDpunto"),
                                cajas.getString("Nombre"),
                                cajas.getDouble("Longitud"),
                                cajas.getDouble("Latitud"),
                                cajas.getString("direccion"),
                                cajas.getString("descripcion")

                        ));


                    }
                    pDialog.dismiss();
                    if (Objects.equals(response, "[]")) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Cuentas Con Marcadores..")
                                .setContentText("Registra Marcadores Para Que Puedas Observarlos Aqui :D!!")
                                .show();
                    } else {
                        MakersAdapters adapter = new MakersAdapters(markerslist, getContext());
                        recyclerViewmarker.setHasFixedSize(true);
                        recyclerViewmarker.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerViewmarker.setAdapter(adapter);
                        bottomSheetDialog.show();
                    }

                } catch (JSONException e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(9)")
                            .show();
                }
            }
        },
                new Response.ErrorListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            pDialog.dismiss();
                            bottomSheetDialog.dismiss();
                            if (locationManagerinternet.getActiveNetworkInfo() != null
                                    && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                    && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("No Hemos Podido Cargar Los Marcadores...")
                                        .show();
                            } else {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Por Favor Habilite Su Internet...")
                                        .show();
                            }
                        } catch (Exception e) {
                            pDialog.dismiss();
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(10)")
                                    .show();
                        }
                    }
                });

        Volley.newRequestQueue(getContext()).add(stringRequest);

    }


    public void PuntosMapa() {
        try {


            id_grupo = preferences.getInt("idgrupo", 0);

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "https://mifolderdeproyectos.online/SEEYOU/puntos_mapa.php?id=" + id_grupo, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray array = new JSONArray(response);
                        Circle circle = null;
                        mMap.clear();

                        if (circle != null) {
                            circle.remove();
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);
                            try {


                                MarkerOptions markerOptions = new MarkerOptions();


                                LatLng puntoubicacion =
                                        new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud"));
                                markerOptions.position(puntoubicacion);
                                markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.markers_round_edit));

                                markerOptions.title(cajas.getString("IDpunto"));
                                markerOptions.draggable(true);

                                if (preferences.getBoolean("fondo2", false) == true) {

                                    circle = mMap.addCircle(new CircleOptions()
                                            .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                            .radius(50)
                                            .strokeWidth(3)
                                            .strokeColor(Color.TRANSPARENT)
                                            .fillColor(0x30DD4819));

                                } else if (preferences.getBoolean("fondo", false) == true) {
                                    circle = mMap.addCircle(new CircleOptions()
                                            .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                            .radius(50)
                                            .strokeWidth(3)
                                            .strokeColor(Color.TRANSPARENT)
                                            .fillColor(0x30391B6F));
                                } else if (preferences.getBoolean("fondo3", false) == true) {
                                    circle = mMap.addCircle(new CircleOptions()
                                            .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                            .radius(50)
                                            .strokeWidth(3)
                                            .strokeColor(Color.TRANSPARENT)
                                            .fillColor(0x30FF0000));
                                } else if (preferences.getBoolean("fondo4", false) == true) {
                                    circle = mMap.addCircle(new CircleOptions()
                                            .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                            .radius(50)
                                            .strokeWidth(3)
                                            .strokeColor(Color.TRANSPARENT)
                                            .fillColor(0x3000F361));
                                } else {
                                    circle = mMap.addCircle(new CircleOptions()
                                            .center(new LatLng(cajas.getDouble("Latitud"), cajas.getDouble("Longitud")))
                                            .radius(50)
                                            .strokeWidth(3)
                                            .strokeColor(Color.TRANSPARENT)
                                            .fillColor(0x30391B6F));
                                }

                                mMap.addMarker(markerOptions);
                                usuariosMapa();
                            } catch (Exception e) {

                            }
                        }


                    } catch (JSONException e) {
                        pDialog.dismiss();
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(13)")
                                .show();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {

                                if (locationManagerinternet.getActiveNetworkInfo() != null
                                        && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                        && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                                    if (alertapuntos == 0) {

                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("No Hemos Podido Obtener Los Puntos De Su Mapa...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                } else {
                                    if (alertapuntos == 0) {
                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Algo Salio Mal..")
                                                .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                                .show();
                                        alertapuntos = 1;
                                    }
                                }
                            } catch (Exception e) {
                                pDialog.dismiss();
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(14)")
                                        .show();
                            }
                        }
                    });

            if (requestQueuePuntos == null) {
                requestQueuePuntos = Volley.newRequestQueue(getContext());
            }
            requestQueuePuntos.add(stringRequest);
        } catch (Exception e) {

        }

    }


    public void usuariosMapa() {
        try {


            id_grupo = preferences.getInt("idgrupo", 0);

            if (id_grupo != 0) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        "https://mifolderdeproyectos.online/SEEYOU/usuarios_mapa.php?id=" + id_grupo, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            int verificar = 0;
                            if (marker != null) {

                                for (int p = 0; p < array.length(); p++) {
                                    try {

                                        marker[p].remove();

                                    } catch (Exception e) {

                                    }
                                }
                            }

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject cajas = array.getJSONObject(i);
                                try {
                                    MarkerOptions markerOptions = new MarkerOptions();

                                    LatLng puntoubicacion =
                                            new LatLng(cajas.getDouble("latitud"), cajas.getDouble("longitud"));
                                    markerOptions.position(puntoubicacion);
                                    markerOptions.title("Nombre:").visible(false);
                                    markerOptions.snippet(cajas.getString("nombre"));
                                    markerOptions.draggable(false);
                                    markerOptions.visible(true);

                                    if (!Objects.equals(cajas.getString("foto"), "")) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                                                getMarkerBitmapFromView(cajas.getString("foto"))));
                                    } else {
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                                                getMarkerBitmapFromView("https://mx.web.img2.acsta.net/c_310_420/pictures/15/06/04/16/19/049773.jpg")));

                                    }

                                    marker[i] = mMap.addMarker(markerOptions);

                                    if (Objects.equals(cajas.getInt("idusuario"), preferences.getInt("id", 0))) {
                                        verificar = 1;
                                    }


                                } catch (Exception e) {

                                }
                            }

                            if (verificar == 0) {
                                try {
                                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Ya No Perteneces A Este Grupo")
                                            .show();
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putInt("idgrupo", 0);
                                    editor.commit();
                                } catch (Exception e) {

                                }

                            }
                        } catch (JSONException e) {
                            pDialog.dismiss();

                        }
                    }
                },
                        new Response.ErrorListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {

                                    if (locationManagerinternet.getActiveNetworkInfo() != null
                                            && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                            && locationManagerinternet.getActiveNetworkInfo().isConnected()) {

                                        if (alertaubicacion == 0) {
                                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Algo Salio Mal..")
                                                    .setContentText("No Hemos Podido Obtener La Ubicacion De Los Miembros Del Grupo...")
                                                    .show();
                                            alertaubicacion = 1;
                                        }
                                    } else {
                                        if (alertaubicacion == 0) {
                                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Algo Salio Mal..")
                                                    .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                                    .show();
                                            alertaubicacion = 1;
                                        }
                                    }
                                } catch (Exception e) {
                                    pDialog.dismiss();

                                }
                            }
                        });
                if (requestQueueUsers == null){
                requestQueueUsers = Volley.newRequestQueue(getContext());

                }
                requestQueueUsers.add(stringRequest);
            }
        } catch (Exception e) {

        }
    }


    private Bitmap getMarkerBitmapFromView(String url) {
        try {


            View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
            ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
            Picasso.get().load(url).placeholder(R.drawable.user).into(markerImageView);
            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
            customMarkerView.buildDrawingCache();
            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Drawable drawable = customMarkerView.getBackground();
            if (drawable != null)
                drawable.draw(canvas);
            customMarkerView.draw(canvas);
            return returnedBitmap;
        } catch (Exception e) {

        }
        return null;

    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        try {


            Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } catch (Exception e) {

        }
        return null;
    }


    private void UnirseGrupo() {
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/unirse_grupo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();


                    if (Objects.equals(response, "Error Grupo")) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("EL Grupo No Existe... Introduzca Un Codigo Valido....")
                                .show();
                    } else if (Objects.equals(response, "Error Usuario")) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Ya Formas Parte De Este Grupo....")
                                .show();
                    } else {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Correcto!!!!")
                                .setContentText("Ahora Formas Parte De Este Grupo!!!")
                                .show();
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putInt("idgrupo", Integer.parseInt(response));
                        editor.commit();

                        id_grupo = preferences.getInt("idgrupo", 0);
                        PuntosMapa();
                        BuscarGrupos();
                        bottomSheetDialogunirse.dismiss();


                    }


                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(
                            getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                            .show();
                }
            }
        },
                new Response.ErrorListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            pDialog.dismiss();
                            if (locationManagerinternet.getActiveNetworkInfo() != null
                                    && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                    && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("No Hemos Podido Obtener Los Puntos De Su Mapa...")
                                        .show();
                            } else {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                        .show();
                            }
                        } catch (Exception e) {
                            pDialog.dismiss();
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                    .show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("codigo", Codigogrupo);
                params.put("idusuario", id_usuario + "");


                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(stringRequest);

    }


    private void CrearGrupo() {
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/agregar_grupo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();


                    if (Objects.equals(response, "Error Grupo")) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Crear Tu Grupo....")
                                .show();
                    } else if (Objects.equals(response, "")) {


                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Crear Tu Grupo....")
                                .show();

                    } else {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Correcto!!!!")
                                .setContentText("Tu Grupo Se Ha Creado Exitosamente!!!")
                                .show();
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putInt("idgrupo", Integer.parseInt(response));
                        editor.commit();

                        id_grupo = preferences.getInt("idgrupo", 0);
                        PuntosMapa();
                        BuscarGrupos();
                        bottomSheetDialogcreargrupo.dismiss();

                    }


                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                            .show();
                }
            }
        },
                new Response.ErrorListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            pDialog.dismiss();
                            if (locationManagerinternet.getActiveNetworkInfo() != null
                                    && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                    && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("No Hemos Podido Obtener Los Puntos De Su Mapa...")
                                        .show();
                            } else {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                        .show();
                            }
                        } catch (Exception e) {
                            pDialog.dismiss();
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(20)")
                                    .show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("Nombre", NombreGrupo1);
                params.put("idusuario", id_usuario + "");


                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(stringRequest);

    }


    private void BuscarGrupos() {
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://mifolderdeproyectos.online/SEEYOU/buscar_grupos.php?id=" + id_usuario, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    gruposlist.clear();

                    JSONArray array = new JSONArray(response);


                    int ciclo = 0, primero = 1, id = 0, id_admin = 0;


                    String Nombre = preferences.getString("Nombre", ""), codigo = "", grupo = "", verificar = preferences.getString("Nombre", "");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);


                        if (primero == 1) {
                            ciclo = cajas.getInt("idgrupo");
                            primero = 0;
                        }


                        if (cajas.getInt("idgrupo") == ciclo) {

                            if (!Objects.equals(cajas.getString("nombreusuario"), verificar)) {
                                Nombre += ", " + cajas.getString("nombreusuario");
                            }

                            codigo = cajas.getString("codigo");

                            id = cajas.getInt("idgrupo");
                            grupo = cajas.getString("nombregrupo");
                            id_admin = cajas.getInt("id_admin");

                        } else {
                            if (cajas.getInt("idgrupo") != ciclo) {
                                gruposlist.add(new Grupos(
                                        grupo,
                                        id,
                                        Nombre,
                                        codigo,
                                        id_admin
                                ));
                            }


                            ciclo = cajas.getInt("idgrupo");
                            Nombre = "";
                            Nombre = preferences.getString("Nombre", "");
                            if (!Objects.equals(cajas.getString("nombreusuario"), verificar)) {
                                Nombre += ", " + cajas.getString("nombreusuario");
                            }
                            codigo = cajas.getString("codigo");

                            id = cajas.getInt("idgrupo");
                            grupo = cajas.getString("nombregrupo");
                            id_admin = cajas.getInt("id_admin");


                        }

                    }


                    gruposlist.add(new Grupos(
                            grupo,
                            id,
                            Nombre,
                            codigo,
                            id_admin
                    ));


                    pDialog.dismiss();
                    GruposAdapters adapter = new GruposAdapters(gruposlist, getContext());
                    recyclerviewgrupos.setHasFixedSize(true);
                    recyclerviewgrupos.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerviewgrupos.setAdapter(adapter);

                    Animaciongrupos();


                } catch (JSONException e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(21)")
                            .show();
                }
            }
        },
                new Response.ErrorListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            pDialog.dismiss();
                            if (locationManagerinternet.getActiveNetworkInfo() != null
                                    && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                    && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("No Hemos Podido Obtener Sus Grupos...")
                                        .show();
                            } else {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Grupos...")
                                        .show();
                            }
                        } catch (Exception e) {
                            pDialog.dismiss();
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                    .show();
                        }
                    }
                });

        Volley.newRequestQueue(getContext()).add(stringRequest);

    }


    private void Animaciongrupos() {
        recyclerviewgrupos.setVisibility(View.VISIBLE);
        animacionDesvanecido = ObjectAnimator.ofFloat(recyclerviewgrupos, View.ALPHA, 0.0f, 1.0f);
        animacionDesvanecido.setDuration(750);
        animacionRotation = ObjectAnimator.ofFloat(recyclerviewgrupos, "rotation", 0f, 360f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionDesvanecido, animacionRotation);
        animatorSet.start();


        agregargrupo.setVisibility(View.VISIBLE);
        animacionDesvanecido = ObjectAnimator.ofFloat(agregargrupo, View.ALPHA, 0.0f, 1.0f);
        animacionDesvanecido.setDuration(750);

        animacionRotation = ObjectAnimator.ofFloat(agregargrupo, "rotation", 0f, 360f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionDesvanecido, animacionRotation);
        animatorSet.start();


        creargrupo.setVisibility(View.VISIBLE);
        animacionDesvanecido = ObjectAnimator.ofFloat(creargrupo, View.ALPHA, 0.0f, 1.0f);
        animacionDesvanecido.setDuration(750);

        animacionRotation = ObjectAnimator.ofFloat(creargrupo, "rotation", 0f, 360f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionDesvanecido, animacionRotation);
        animatorSet.start();


        TBgrupos.setVisibility(View.VISIBLE);
        animacionDesvanecido = ObjectAnimator.ofFloat(TBgrupos, View.ALPHA, 0.0f, 1.0f);
        animacionDesvanecido.setDuration(750);
        animatorSet = new AnimatorSet();
        animatorSet.play(animacionDesvanecido);
        animatorSet.start();


        closerecycler.setVisibility(View.VISIBLE);
        animacionDesvanecido = ObjectAnimator.ofFloat(closerecycler, View.ALPHA, 0.0f, 1.0f);
        animacionDesvanecido.setDuration(750);

        animacionRotation = ObjectAnimator.ofFloat(closerecycler, "rotation", 0f, 360f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionDesvanecido, animacionRotation);
        animatorSet.start();
    }


    public void UbicacionPunto(double latitud1, double longitud1, String idpunto, String direccion) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/cambiar_ubicacion.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();


                    if (Objects.equals(response, "Cambios Realizados")) {


                        new SweetAlertDialog(ubicacion.getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Correcto!!")
                                .setContentText("La Ubicacion Ha Sido Actualizada Correctamente!!")
                                .show();


                    } else {
                        new SweetAlertDialog(ubicacion.getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Actualizar La Ubicacion De Su Punto....")
                                .show();
                    }

                    PuntosMapa();

                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(ubicacion.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(23)")
                            .show();
                }

            }

        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    pDialog.dismiss();


                    if (locationManagerinternet.getActiveNetworkInfo() != null
                            && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                            && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Obtener La Informacion Del Marcador...")
                                .show();
                    } else {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Por Favor Habilite Su Internet...")
                                .show();
                    }
                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(ubicacion.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(24)")
                            .show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("idpunto", idpunto);
                params.put("direccion", direccion);
                params.put("latitud", latitud1 + "");
                params.put("longitud", longitud1 + "");

                return params;
            }

        };


        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }


    public void DatosMarker(String id) {

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/buscar_datos_de_marcador.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();
                    JSONArray array = new JSONArray(response);

                    JSONObject cajas = array.getJSONObject(0);


                    descripcionmarcador.setText(cajas.getString("descripcion"));
                    TVidmarker.setText(cajas.getString("IDpunto"));
                    nombremarcador.setText(cajas.getString("Nombre"));
                    TVusermarker.setText(cajas.getString("usuario")+" "+cajas.getString("apellido"));
                    Picasso.get()
                            .load(cajas.getString("foto"))
                            .placeholder(R.drawable.user)
                            .into(IVusermarker);
                    bottomSheetDialogmarker.show();


                } catch (JSONException e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(ubicacion.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("No Hemos Podido Obtener Los Datos De Su Marcador....")
                            .show();
                }

            }

        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    pDialog.dismiss();


                    if (locationManagerinternet.getActiveNetworkInfo() != null
                            && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                            && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Obtener La Informacion Del Marcador...")
                                .show();
                    } else {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Por Favor Habilite Su Internet...")
                                .show();
                    }
                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(ubicacion.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(25)")
                            .show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("id", id);


                return params;
            }

        };


        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }


    @SuppressLint("MissingPermission")
    public void getLastLocation(int accion) {
        // Get last known recent location using new Google Play Services SDK (v11+)
        try {

            FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());

            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                LatLng UbicacionActualo = new LatLng(location.getLatitude(), location.getLongitude());

                                Latitudruta = location.getLatitude();
                                Longitudruta = location.getLongitude();
                                if (accion == 1) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UbicacionActualo, 13));
                                }
                            } else {
                                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Algo Salio Mal..")
                                            .setContentText("Por Favor Habilite Su Ubicacion...")
                                            .show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Por Favor Active Su Ubicacion O Permita Que La App Accesada A Ella...")
                                    .show();
                        }
                    });
        } catch (Exception e) {

        }
    }

    //HASTA AQUI TERMINA EL CODIGO DE UBICACION VIA GOOGLE

    public void Marcador() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                try {


                    //Guarda los valores de longitud y latitud en variables
                    double latitud = latLng.latitude;
                    double longitud = latLng.longitude;


                    //Le da a las variables globales los valores que necesitan
                    LatitudDialogo = latitud;
                    LongitudDialogo = longitud;


                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);

                        direccion = "";

                        Address address = (Address) addresses.get(0);
                        direccion += address.getAddressLine(0);


                    } catch (IOException e) {
                        pDialog.dismiss();
                        new SweetAlertDialog(ubicacion.getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(27)")
                                .show();
                    }


                    //Guarda Todas las opciones necesarias para hacer el marcador
                    MarkerOptions markerOptions = new MarkerOptions();

                    //Crea el punto donde se marcara
                    markerOptions.position(latLng);

                    //le da nombre al punto
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);


                    //Mueve la camara al punto proporcionado, osea la ubicacion del usuario
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


                    //llama al fragmento dialogo
                    Dialogo_MensajeFragment dialogofragment = new Dialogo_MensajeFragment();
                    dialogofragment.show(getFragmentManager(), "MyFragment");

                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(ubicacion.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(28)")
                            .show();
                }
            }

        });

    }


    public static void cerrargrupo() {

        animacionDesvanecido = ObjectAnimator.ofFloat(closerecycler, View.ALPHA, 1.0f, 0.0f);
        animacionDesvanecido.setDuration(750);
        animacionRotation = ObjectAnimator.ofFloat(closerecycler, "rotation", 0f, 360f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionDesvanecido, animacionRotation);
        animatorSet.start();


        animacionDesvanecido = ObjectAnimator.ofFloat(agregargrupo, View.ALPHA, 1.0f, 0.0f);
        animacionDesvanecido.setDuration(750);
        animacionRotation = ObjectAnimator.ofFloat(agregargrupo, "rotation", 0f, 360f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionDesvanecido, animacionRotation);


        animatorSet.start();
        animacionDesvanecido = ObjectAnimator.ofFloat(creargrupo, View.ALPHA, 1.0f, 0.0f);
        animacionDesvanecido.setDuration(750);
        animacionRotation = ObjectAnimator.ofFloat(creargrupo, "rotation", 0f, 360f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionDesvanecido, animacionRotation);
        animatorSet.start();


        animacionDesvanecido = ObjectAnimator.ofFloat(TBgrupos, View.ALPHA, 1.0f, 0.0f);
        animacionDesvanecido.setDuration(750);
        animatorSet = new AnimatorSet();
        animatorSet.play(animacionDesvanecido);
        animatorSet.start();


        animacionDesvanecido = ObjectAnimator.ofFloat(recyclerviewgrupos, View.ALPHA, 1.0f, 0.0f);
        animacionDesvanecido.setDuration(750);
        animatorSet = new AnimatorSet();

        animacionRotation = ObjectAnimator.ofFloat(recyclerviewgrupos, "rotation", 0f, 360f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionDesvanecido, animacionRotation);
        animatorSet.start();


        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                recyclerviewgrupos.setVisibility(View.INVISIBLE);
                TBgrupos.setVisibility(View.INVISIBLE);
                agregargrupo.setVisibility(View.INVISIBLE);
                closerecycler.setVisibility(View.INVISIBLE);
                creargrupo.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    public void cambiarbotones(){

        animacionbuttons = ObjectAnimator.ofFloat(ubicacion, "y", 65);
        animacionbuttons.setDuration(0);
        animacionDesvanecido = ObjectAnimator.ofFloat(ubicacion, View.ALPHA, 1.0f, 0.0f);
        animacionDesvanecido.setDuration(0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionbuttons,animacionDesvanecido);
        animatorSet.start();


        animacionbuttons = ObjectAnimator.ofFloat(cambiarmapa, "y", 71);
        animacionDesvanecido = ObjectAnimator.ofFloat(cambiarmapa, View.ALPHA, 1.0f, 0.0f);
        animacionDesvanecido.setDuration(0);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionbuttons,animacionDesvanecido);
        animatorSet.start();


        animacionbuttons = ObjectAnimator.ofFloat(vermarkers, "y", 73);
        animacionbuttons.setDuration(0);
        animacionDesvanecido = ObjectAnimator.ofFloat(vermarkers, View.ALPHA, 1.0f, 0.0f);
        animacionDesvanecido.setDuration(0);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animacionbuttons,animacionDesvanecido);
        animatorSet.start();


        ubicacion.setVisibility(View.GONE);
        vermarkers.setVisibility(View.GONE);
        cambiarmapa.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);


        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        //objetos de la pantalla de inicio de maps
        ubicacion = view.findViewById(R.id.IVsetmarker);
        cancelar = view.findViewById(R.id.BTNcancelar);
        enviar = view.findViewById(R.id.BTNenviarmensaje);
        location = view.findViewById(R.id.IVlocation);
        contenedor = view.findViewById(R.id.Contenedormarker);
        vermarkers = view.findViewById(R.id.IVvermarkers);
        SVubicacion = view.findViewById(R.id.SVubicacion);
        cancelarviaje = view.findViewById(R.id.BTNcancelarviaje);
        cambiarmapa = view.findViewById(R.id.IVcambiarmapa);
        grupos = view.findViewById(R.id.IVgrupos);
        recyclerviewgrupos = view.findViewById(R.id.RBgrupos);
        TValertamarcador = view.findViewById(R.id.TValertamarcador);
        NotificacionAlerta = view.findViewById(R.id.IValerta);
        IVsettings = view.findViewById(R.id.IVsettingsmap);

        //Grupos Dialog
        closerecycler = view.findViewById(R.id.IVclose);

        TBgrupos = view.findViewById(R.id.TBgrupos);
        agregargrupo = view.findViewById(R.id.BTNunirsegrupo);
        creargrupo = view.findViewById(R.id.BTNcreargrupo);


        cambiarbotones();





        NotificacionAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notificar(0);
            }
        });


        IVsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (settings == 1) {
                    animacionbuttons = ObjectAnimator.ofFloat(ubicacion, "y", 65);
                    animacionbuttons.setDuration(500);
                    animacionDesvanecido = ObjectAnimator.ofFloat(ubicacion, View.ALPHA, 1.0f, 0.0f);
                    animacionDesvanecido.setDuration(500);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animacionbuttons,animacionDesvanecido);
                    animatorSet.start();




                    animacionbuttons = ObjectAnimator.ofFloat(cambiarmapa, "y", 71);
                    animacionbuttons.setDuration(500);
                    animacionDesvanecido = ObjectAnimator.ofFloat(cambiarmapa, View.ALPHA, 1.0f, 0.0f);
                    animacionDesvanecido.setDuration(500);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animacionbuttons,animacionDesvanecido);
                    animatorSet.start();


                    animacionbuttons = ObjectAnimator.ofFloat(vermarkers, "y", 73);
                    animacionbuttons.setDuration(500);
                    animacionDesvanecido = ObjectAnimator.ofFloat(vermarkers, View.ALPHA, 1.0f, 0.0f);
                    animacionDesvanecido.setDuration(500);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animacionbuttons,animacionDesvanecido);
                    animatorSet.start();

                    ubicacion.setClickable(false);
                    vermarkers.setClickable(false);
                    cambiarmapa.setClickable(false);



                    settings = 0;
                } else{

                    ubicacion.setVisibility(View.VISIBLE);
                    vermarkers.setVisibility(View.VISIBLE);
                    cambiarmapa.setVisibility(View.VISIBLE);

                    animacionbuttons = ObjectAnimator.ofFloat(ubicacion, "y", 165);
                    animacionbuttons.setDuration(500);
                    animacionDesvanecido = ObjectAnimator.ofFloat(ubicacion, View.ALPHA, 0.0f, 1.0f);
                    animacionDesvanecido.setDuration(500);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animacionbuttons,animacionDesvanecido);
                    animatorSet.start();


                    animacionbuttons = ObjectAnimator.ofFloat(cambiarmapa, "y", 261);
                    animacionbuttons.setDuration(500);
                    animacionDesvanecido = ObjectAnimator.ofFloat(cambiarmapa, View.ALPHA, 0.0f, 1.0f);
                    animacionDesvanecido.setDuration(500);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animacionbuttons,animacionDesvanecido);
                    animatorSet.start();


                    animacionbuttons = ObjectAnimator.ofFloat(vermarkers, "y", 358);
                    animacionbuttons.setDuration(500);
                    animacionDesvanecido = ObjectAnimator.ofFloat(vermarkers, View.ALPHA, 0.0f, 1.0f);
                    animacionDesvanecido.setDuration(500);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animacionbuttons,animacionDesvanecido);
                    animatorSet.start();
                    animatorSet = new AnimatorSet();
                    animatorSet.play(animacionbuttons);
                    animatorSet.start();

                    ubicacion.setClickable(true);
                    vermarkers.setClickable(true);
                    cambiarmapa.setClickable(true);


                    settings = 1;
                }
            }
        });



        SVubicacion.setIconifiedByDefault(false);
        SVubicacion.setBackgroundResource(R.drawable.searchbar);
        SVubicacion.setQueryHint("Buscar Ubicacion");
        SVubicacion.setFocusable(false);

        preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        id_usuario = preferences.getInt("id", 0);

        cancelarviaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (polylineruta != null) {
                    polylineruta.remove();
                    cancelarviaje.setVisibility(View.INVISIBLE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("viaje", false);
                    editor.commit();
                    nomasviaje = 1;
                    getLastLocation(1);
                }
            }
        });


        /*if(preferences.getBoolean("viaje",false) == true){
            cancelarviaje.setVisibility(View.VISIBLE);
        }*/

        if (preferences.getInt("idgrupo", 0) == 0) {
            new SweetAlertDialog(getContext())
                    .setTitleText("Aviso!")
                    .setContentText("Seleccione Un Grupo Para Ver Sus Marcadores y A Otros Usuarios Del Mismo.")
                    .show();
        }
        id_grupo = preferences.getInt("idgrupo", 0);


        if (preferences.getBoolean("fondo2", false) == true) {


            TBgrupos.setBackgroundResource(R.drawable.fondodegradado2);
            creargrupo.setBackgroundResource(R.drawable.buttonfondo2);
            agregargrupo.setBackgroundResource(R.drawable.buttonfondo2);

        } else if (preferences.getBoolean("fondo", false) == true) {

            TBgrupos.setBackgroundResource(R.drawable.fondodegradado);
            creargrupo.setBackgroundResource(R.drawable.button2);
            agregargrupo.setBackgroundResource(R.drawable.button2);
        } else if (preferences.getBoolean("fondo3", false) == true) {

            TBgrupos.setBackgroundResource(R.drawable.fondodegradado3);
            creargrupo.setBackgroundResource(R.drawable.buttonfondo3);
            agregargrupo.setBackgroundResource(R.drawable.buttonfondo3);

        } else if (preferences.getBoolean("fondo4", false) == true) {

            TBgrupos.setBackgroundResource(R.drawable.fondodegradado4);
            creargrupo.setBackgroundResource(R.drawable.buttonfondo4);
            agregargrupo.setBackgroundResource(R.drawable.buttonfondo4);

        } else {
            TBgrupos.setBackgroundResource(R.drawable.fondodegradado);
            creargrupo.setBackgroundResource(R.drawable.button2);
            agregargrupo.setBackgroundResource(R.drawable.button2);
        }


        creargrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetBehavior<View> bottomSheetBehavior;
                bottomSheetDialogcreargrupo = new BottomSheetDialog
                        (getContext(), R.style.BottomSheetDialog);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                        R.layout.activity_registro_grupos, null
                );
                bottomSheetDialogcreargrupo.setContentView(bottomSheetView);
                LinearLayout contenedor1 = bottomSheetDialogcreargrupo.findViewById(R.id.Contenedorgrupos);
                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.92);
                assert contenedor1 != null;
                contenedor1.setMinimumHeight(height);
                bottomSheetBehavior.setMaxHeight(height);
                bottomSheetDialogcreargrupo.show();

                TextView unirsegrupo, tienesgrupo;
                unirsegrupo = bottomSheetDialogcreargrupo.findViewById(R.id.TVcuentacogrupo);
                unirsegrupo.setVisibility(View.INVISIBLE);
                tienesgrupo = bottomSheetDialogcreargrupo.findViewById(R.id.TVunirse);
                tienesgrupo.setVisibility(View.INVISIBLE);
                IVcerrarcreargrupo = bottomSheetDialogcreargrupo.findViewById(R.id.IVcerrarcreargrupo);
                Button crear = bottomSheetDialogcreargrupo.findViewById(R.id.BTNregistrargrupo);
                Nombregrupo = bottomSheetDialogcreargrupo.findViewById(R.id.ETnombregrupo);

                crear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NombreGrupo1 = Nombregrupo.getText().toString();
                        CrearGrupo();
                    }
                });


                IVcerrarcreargrupo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                });

            }
        });


        agregargrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    BottomSheetBehavior<View> bottomSheetBehavior;
                    bottomSheetDialogunirse = new BottomSheetDialog
                            (getContext(), R.style.BottomSheetDialog);
                    View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                            R.layout.unirse_grupo, null
                    );
                    bottomSheetDialogunirse.setContentView(bottomSheetView);
                    LinearLayout contenedor1 = bottomSheetDialogunirse.findViewById(R.id.ContenedorUnirse);
                    bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.92);
                    assert contenedor1 != null;
                    contenedor1.setMinimumHeight(height);
                    bottomSheetBehavior.setMaxHeight(height);
                    bottomSheetDialogunirse.show();


                    EditText numero1, numero2, numero3, numero4, numero5, numero6;
                    Button unirse;

                    numero1 = bottomSheetDialogunirse.findViewById(R.id.ETdigito1);
                    numero2 = bottomSheetDialogunirse.findViewById(R.id.ETdigito2);
                    numero3 = bottomSheetDialogunirse.findViewById(R.id.ETdigito3);
                    numero4 = bottomSheetDialogunirse.findViewById(R.id.ETdigito4);
                    numero5 = bottomSheetDialogunirse.findViewById(R.id.ETdigito5);
                    numero6 = bottomSheetDialogunirse.findViewById(R.id.ETdigito6);
                    unirse = bottomSheetDialogunirse.findViewById(R.id.BTNunirse);
                    cerrarunir = bottomSheetDialogunirse.findViewById(R.id.IVcerrarunir);


                    cerrarunir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    });


                    numero1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            numero1.setBackgroundResource(R.drawable.codigo_background2);
                            numero2.setBackgroundResource(R.drawable.codigo_background);
                            numero3.setBackgroundResource(R.drawable.codigo_background);
                            numero4.setBackgroundResource(R.drawable.codigo_background);
                            numero5.setBackgroundResource(R.drawable.codigo_background);
                            numero6.setBackgroundResource(R.drawable.codigo_background);

                        }
                    });

                    numero2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            numero1.setBackgroundResource(R.drawable.codigo_background);
                            numero2.setBackgroundResource(R.drawable.codigo_background2);
                            numero3.setBackgroundResource(R.drawable.codigo_background);
                            numero4.setBackgroundResource(R.drawable.codigo_background);
                            numero5.setBackgroundResource(R.drawable.codigo_background);
                            numero6.setBackgroundResource(R.drawable.codigo_background);
                        }
                    });

                    numero3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            numero1.setBackgroundResource(R.drawable.codigo_background);
                            numero2.setBackgroundResource(R.drawable.codigo_background);
                            numero3.setBackgroundResource(R.drawable.codigo_background2);
                            numero4.setBackgroundResource(R.drawable.codigo_background);
                            numero5.setBackgroundResource(R.drawable.codigo_background);
                            numero6.setBackgroundResource(R.drawable.codigo_background);
                        }
                    });
                    numero4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            numero1.setBackgroundResource(R.drawable.codigo_background);
                            numero2.setBackgroundResource(R.drawable.codigo_background);
                            numero3.setBackgroundResource(R.drawable.codigo_background);
                            numero4.setBackgroundResource(R.drawable.codigo_background2);
                            numero5.setBackgroundResource(R.drawable.codigo_background);
                            numero6.setBackgroundResource(R.drawable.codigo_background);
                        }
                    });
                    numero5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            numero1.setBackgroundResource(R.drawable.codigo_background);
                            numero2.setBackgroundResource(R.drawable.codigo_background);
                            numero3.setBackgroundResource(R.drawable.codigo_background);
                            numero4.setBackgroundResource(R.drawable.codigo_background);
                            numero5.setBackgroundResource(R.drawable.codigo_background2);
                            numero6.setBackgroundResource(R.drawable.codigo_background);
                        }
                    });
                    numero6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            numero1.setBackgroundResource(R.drawable.codigo_background);
                            numero2.setBackgroundResource(R.drawable.codigo_background);
                            numero3.setBackgroundResource(R.drawable.codigo_background);
                            numero4.setBackgroundResource(R.drawable.codigo_background);
                            numero5.setBackgroundResource(R.drawable.codigo_background);
                            numero6.setBackgroundResource(R.drawable.codigo_background2);
                        }
                    });


                    numero1.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            String codigo = s.toString();

                            if (codigo.length() == 1) {

                                numero2.requestFocus();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {


                        }
                    });

                    numero2.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            String codigo = s.toString();

                            if (codigo.length() == 1) {
                                numero3.requestFocus();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    numero3.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            String codigo = s.toString();

                            if (codigo.length() == 1) {
                                numero4.requestFocus();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });


                    numero4.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            String codigo = s.toString();

                            if (codigo.length() == 1) {
                                numero5.requestFocus();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });


                    numero5.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            String codigo = s.toString();

                            if (codigo.length() == 1) {
                                numero6.requestFocus();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });


                    numero6.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            String codigo = s.toString();

                            if (codigo.length() == 1) {
                                numero1.requestFocus();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });


                    unirse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Codigogrupo = numero1.getText().toString() + numero2.getText().toString() +
                                    numero3.getText().toString() + numero4.getText().toString() +
                                    numero5.getText().toString() + numero6.getText().toString();
                            UnirseGrupo();
                        }
                    });


                } catch (Exception e) {

                }
            }
        });


        closerecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cerrargrupo();


            }
        });


        grupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BuscarGrupos();
                cancelar.setVisibility(View.INVISIBLE);
                TValertamarcador.setVisibility(View.INVISIBLE);
            }
        });


        cambiarmapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });


        SVubicacion.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = SVubicacion.getQuery().toString();
                List<Address> addressList = null;
                if (location != null & !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } catch (Exception e) {
                        pDialog.dismiss();
                        new SweetAlertDialog(getContext())
                                .setTitleText("Ubicacion No Encontrada...")
                                .setContentText("Asegurece De Seguir Esta Estructura: \n  Ciudad + Lugar/calle/Local/Establecimineto o Viceversa.")
                                .show();
                    }
                } else {
                    new SweetAlertDialog(getContext())
                            .setTitleText("Favor De Introducir Una Ubicaion Valida!!")
                            .show();


                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
/*
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

                    }
                }else{



                }*/
                return false;
            }
        });

        SVubicacion.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SVubicacion.setBackgroundResource(R.drawable.fondo_de_edittext);

            }
        });

        SVubicacion.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                SVubicacion.setBackgroundResource(R.color.Trasparente);
                return false;
            }
        });


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLastLocation(1);

            }
        });


        vermarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    BottomSheetBehavior<View> bottomSheetBehavior;
                    bottomSheetDialog = new BottomSheetDialog
                            (getContext(), R.style.BottomSheetDialog);
                    View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                            R.layout.markersbottomshet, null
                    );
                    bottomSheetDialog.setContentView(bottomSheetView);
                    LinearLayout contenedor1 = bottomSheetDialog.findViewById(R.id.Contenedormarker);
                    bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.92);

                    assert contenedor1 != null;
                    contenedor1.setMinimumHeight(height);
                    bottomSheetBehavior.setMaxHeight(height);

                    recyclerViewmarker = bottomSheetDialog.findViewById(R.id.RVmarkersbottomsheet);
                    TextView titulopuntos = bottomSheetDialog.findViewById(R.id.TVtitulomarcadorrecicler);
                    SVpunto = bottomSheetDialog.findViewById(R.id.SVpunto);

                    PuntosRecycler();


                    SVpunto.setOnSearchClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            titulopuntos.setVisibility(View.INVISIBLE);


                        }
                    });

                    SVpunto.setOnCloseListener(new SearchView.OnCloseListener() {
                        @Override
                        public boolean onClose() {
                            titulopuntos.setVisibility(View.VISIBLE);
                            SVpunto.setBackgroundResource(R.color.Trasparente);
                            return false;
                        }
                    });

                    SVpunto.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {


                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {


                            ArrayList<Markers> lista = new ArrayList<>();
                            lista = adapter.filtrado(newText);
                            MakersAdapters adapter = new MakersAdapters(lista, getContext());
                            recyclerViewmarker.setHasFixedSize(true);
                            recyclerViewmarker.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerViewmarker.setAdapter(adapter);
                            return false;
                        }

                    });

                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....(29)")
                            .show();
                }

            }

        });


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //desactiva la funcion de marcar puntos al hacer click
                mMap.setOnMapClickListener(null);

                //vuelve invisible el boton de cancelar
                cancelar.setVisibility(View.INVISIBLE);
                TValertamarcador.setVisibility(View.INVISIBLE);


            }
        });

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //vuelve visible el boton de cancelar
                cancelar.setVisibility(View.VISIBLE);
                TValertamarcador.setVisibility(View.VISIBLE);
                cerrargrupo();

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


