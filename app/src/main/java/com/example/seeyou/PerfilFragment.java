package com.example.seeyou;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seeyou.adapters.MakersAdapters;
import com.example.seeyou.adapters.Markers;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_IMAGE_REQUEST = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SweetAlertDialog pDialog;
    int id_usuario;
    ImageView fondo1,fondo2,fondo3,fondo4,usuario;
    ;
    LocationManager locationManager;
    String NombreUsuario, CorreoUsuario, ContraseñaUsuario, TelefonoUsuario, ApellidoUsuario;
    SharedPreferences preferences;
    RequestQueue requestQueue;
    ConnectivityManager locationManagerinternet;
    SharedPreferences.Editor editor;
    TextInputEditText nombre, correo, contraseña, telefono, apellido;
    ConstraintLayout ConstrainMain = MainActivity.constraintLayout;
  //  String key_foto = "foto";

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    //BORRAR DESPUES
    Button btnCambiar, btnLogin,btngaleria;
    Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        btnLogin = view.findViewById(R.id.btnLogin);
        nombre = view.findViewById(R.id.ETnombreusuario);
        correo = view.findViewById(R.id.ETCorreoUsuario);
        contraseña = view.findViewById(R.id.ETContraseñaUsuario);
        telefono = view.findViewById(R.id.ETCelularUsuario);
        apellido = view.findViewById(R.id.ETApellidoUsuario);
        btnCambiar = view.findViewById(R.id.BTNcambiardatos);
        fondo1 = view.findViewById(R.id.IVfondo1);
        fondo2 = view.findViewById(R.id.IVfondo2);
        btngaleria = view.findViewById(R.id.btngaleriausuario);
        fondo3 = view.findViewById(R.id.IVfondo3);
        fondo4 = view.findViewById(R.id.IVfondo4);
        usuario = view.findViewById(R.id.IVusuarioinfo);
        ConstrainMain = view.findViewById(R.id.Fondobottomnavigation);


        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);


        if (preferences.getBoolean("fondo2", false) == true){

            btnCambiar.setBackgroundResource(R.drawable.buttonfondo2);
            btngaleria.setBackgroundResource(R.drawable.buttonfondo2);
            btnLogin.setBackgroundResource(R.drawable.buttonfondo2);


        }else if(preferences.getBoolean("fondo", false) == true){

            btnCambiar.setBackgroundResource(R.drawable.button2);
            btngaleria.setBackgroundResource(R.drawable.button2);
            btnLogin.setBackgroundResource(R.drawable.button2);

        }else if(preferences.getBoolean("fondo3", false) == true){

            btnCambiar.setBackgroundResource(R.drawable.buttonfondo3);
            btngaleria.setBackgroundResource(R.drawable.buttonfondo3);
            btnLogin.setBackgroundResource(R.drawable.buttonfondo3);

        }
        else if(preferences.getBoolean("fondo4", false) == true){

            btnCambiar.setBackgroundResource(R.drawable.buttonfondo4);
            btngaleria.setBackgroundResource(R.drawable.buttonfondo4);
            btnLogin.setBackgroundResource(R.drawable.buttonfondo4);

        }

        fondo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor = preferences.edit();
                editor.putBoolean("fondo", true);
                editor.putBoolean("fondo2", false);
                editor.putBoolean("fondo3", false);
                editor.putBoolean("fondo4", false);
                editor.commit();
                btnCambiar.setBackgroundResource(R.drawable.button2);
                btngaleria.setBackgroundResource(R.drawable.button2);
                btnLogin.setBackgroundResource(R.drawable.button2);
                MainActivity.fondobottom(1);
                system(1);
            }
        });

        fondo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor = preferences.edit();
                editor.putBoolean("fondo", false);
                editor.putBoolean("fondo2", true);
                editor.putBoolean("fondo3", false);
                editor.putBoolean("fondo4", false);
                editor.commit();
                btnCambiar.setBackgroundResource(R.drawable.buttonfondo2);
                btngaleria.setBackgroundResource(R.drawable.buttonfondo2);
                btnLogin.setBackgroundResource(R.drawable.buttonfondo2);
                MainActivity.fondobottom(2);
                system(2);
            }
        });

        fondo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor = preferences.edit();
                editor.putBoolean("fondo", false);
                editor.putBoolean("fondo2", false);
                editor.putBoolean("fondo3", true);
                editor.putBoolean("fondo4", false);
                editor.commit();
                btnCambiar.setBackgroundResource(R.drawable.buttonfondo3);
                btngaleria.setBackgroundResource(R.drawable.buttonfondo3);
                btnLogin.setBackgroundResource(R.drawable.buttonfondo3);
                MainActivity.fondobottom(3);
                system(3);

            }
        });
        fondo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor = preferences.edit();
                editor.putBoolean("fondo", false);
                editor.putBoolean("fondo2", false);
                editor.putBoolean("fondo3", false);
                editor.putBoolean("fondo4", true);
                editor.commit();
                btnCambiar.setBackgroundResource(R.drawable.buttonfondo4);
                btngaleria.setBackgroundResource(R.drawable.buttonfondo4);
                btnLogin.setBackgroundResource(R.drawable.buttonfondo4);
                MainActivity.fondobottom(4);
                system(4);
            }
        });

        Usuario();

        btnCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validacion();
            }
        });

        btngaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });


        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);

        id_usuario = preferences.getInt("id", 0);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("sesion_usuario", false);
                editor.putInt("id", 0);
                editor.putString("Nombre", "");
                editor.putString("Apellido", "");
                editor.putInt("idusuarioruta",0);
                editor.putString("nombreusuarioruta","");
                editor.putString("apellidousuarioruta","");
                editor.putString("fecharuta","");
                editor.commit();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        return view;
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleciona imagen"), PICK_IMAGE_REQUEST);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Cómo obtener el mapa de bits de la Galería
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Configuración del mapa de bits en ImageView
                usuario.setImageBitmap(bitmap);
            } catch (IOException e) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Algo Salio Mal..")
                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                        .show();
            }
        }
    }





    public void system(int action){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            Drawable background = null;
            if (action == 1){
                background = getResources().getDrawable(R.drawable.fondodegradado);
            } else if(action == 2){
                background = getResources().getDrawable(R.drawable.fondonaranaja2);
            }else if(action == 3){
                background = getResources().getDrawable(R.drawable.fondodegradado3);
            } else if(action == 4){
                background = getResources().getDrawable(R.drawable.fondodegradado4);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }


    private void Usuario() {

        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(true);

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/datos_usuario.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();


                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);


                        NombreUsuario = cajas.getString("Nombre");
                        nombre.setText(NombreUsuario);

                        ApellidoUsuario = cajas.getString("Apellido");
                        apellido.setText(ApellidoUsuario);

                        CorreoUsuario = cajas.getString("Email");
                        correo.setText(CorreoUsuario);

                        TelefonoUsuario = cajas.getString("Celular");
                        telefono.setText(TelefonoUsuario);

                        ContraseñaUsuario = cajas.getString("Contraseña");
                        contraseña.setText(ContraseñaUsuario);

                        if (!Objects.equals(cajas.getString("foto"), "")) {
                            Picasso.get()
                                    .load(cajas.getString("foto"))
                                    .placeholder(R.drawable.user)
                                    .into(usuario);
                        }

                    }


                } catch (JSONException e) {
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
                                        .setContentText("No Hemos Podido Cargar Los Datos Del Usuario...")
                                        .show();
                            } else {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Por Favor Habilite Su Internet...")
                                        .show();
                            }
                        } catch (Exception e){
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


                params.put("idusuario", id_usuario + "");


                return params;
            }

        };


        Volley.newRequestQueue(getContext()).add(stringRequest);

    }

    public void validacion() {

        CambiosDatos();

    }


    //Despues de optener la foto esta la compbierte para poderla mandar a la base de datos
        public String getStringImagen(Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void CambiosDatos() {

        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(true);

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/cambiar_usuario.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();


                        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Cambios Realizados Con Exito!!!!!")
                                .show();
                        Usuario();


                } catch (Exception e) {
                    new SweetAlertDialog(fondo1.getContext(), SweetAlertDialog.ERROR_TYPE)
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
                        pDialog.dismiss();

                        if (locationManagerinternet.getActiveNetworkInfo() != null
                                && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                                && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("No Hemos Podido Hacer Los Cambios...")
                                    .show();
                        } else {
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Por Favor Habilite Su Internet...")
                                    .show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String IMAGEN;
                if (bitmap != null) {
                     IMAGEN = getStringImagen(bitmap);
                }else {
                    IMAGEN="";
                }
                Map<String, String> params = new HashMap<String, String>();


                params.put("Nombre", nombre.getText().toString());
                params.put("Apellido", apellido.getText().toString());
                params.put("Email", correo.getText().toString());
                params.put("Celular", telefono.getText().toString());
                params.put("Contraseña", contraseña.getText().toString());
                params.put("IDusuario", id_usuario + "");
                params.put("foto",IMAGEN);

                return params;
            }

        };

        Volley.newRequestQueue(getContext()).add(stringRequest);

    }

}