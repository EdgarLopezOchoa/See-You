package com.example.seeyou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SweetAlertDialog pDialog;
    int id_usuario, validacion = 0;
    ImageView fondo1,fondo2,fondo3,fondo4;
    ;
    LocationManager locationManager;
    String NombreUsuario, CorreoUsuario, ContraseñaUsuario, TelefonoUsuario, ApellidoUsuario;
    Toolbar navegadorperfil;
    SharedPreferences preferences;
    RequestQueue requestQueue;
    ConnectivityManager locationManagerinternet;
    SharedPreferences.Editor editor;
    TextInputEditText nombre, correo, contraseña, telefono, apellido;

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
    Button btnCambiar, btnLogin;

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
        navegadorperfil = view.findViewById(R.id.navegador2);
        fondo3 = view.findViewById(R.id.IVfondo3);
        fondo4 = view.findViewById(R.id.IVfondo4);

        preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);


        if (preferences.getBoolean("fondo2", false) == true){
            navegadorperfil.setBackgroundResource(R.drawable.fondodegradado2);
            btnCambiar.setBackgroundResource(R.drawable.buttonfondo2);
            btnLogin.setBackgroundResource(R.drawable.buttonfondo2);

        }else if(preferences.getBoolean("fondo", false) == true){
            navegadorperfil.setBackgroundResource(R.drawable.fondodegradado);
            btnCambiar.setBackgroundResource(R.drawable.button2);
            btnLogin.setBackgroundResource(R.drawable.button2);
        }else if(preferences.getBoolean("fondo3", false) == true){
            navegadorperfil.setBackgroundResource(R.drawable.fondodegradado3);
            btnCambiar.setBackgroundResource(R.drawable.buttonfondo3);
            btnLogin.setBackgroundResource(R.drawable.buttonfondo3);
        }
        else if(preferences.getBoolean("fondo4", false) == true){
            navegadorperfil.setBackgroundResource(R.drawable.fondodegradado4);
            btnCambiar.setBackgroundResource(R.drawable.buttonfondo4);
            btnLogin.setBackgroundResource(R.drawable.buttonfondo4);
        }

        fondo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegadorperfil.setBackgroundResource(R.drawable.fondodegradado);
                editor = preferences.edit();
                editor.putBoolean("fondo", true);
                editor.putBoolean("fondo2", false);
                editor.putBoolean("fondo3", false);
                editor.putBoolean("fondo4", false);
                editor.commit();
                btnCambiar.setBackgroundResource(R.drawable.button2);
                btnLogin.setBackgroundResource(R.drawable.button2);
            }
        });

        fondo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegadorperfil.setBackgroundResource(R.drawable.fondodegradado2);
                editor = preferences.edit();
                editor.putBoolean("fondo", false);
                editor.putBoolean("fondo2", true);
                editor.putBoolean("fondo3", false);
                editor.putBoolean("fondo4", false);
                editor.commit();
                btnCambiar.setBackgroundResource(R.drawable.buttonfondo2);
                btnLogin.setBackgroundResource(R.drawable.buttonfondo2);
            }
        });

        fondo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegadorperfil.setBackgroundResource(R.drawable.fondodegradado3);
                editor = preferences.edit();
                editor.putBoolean("fondo", false);
                editor.putBoolean("fondo2", false);
                editor.putBoolean("fondo3", true);
                editor.putBoolean("fondo4", false);
                editor.commit();
                btnCambiar.setBackgroundResource(R.drawable.buttonfondo3);
                btnLogin.setBackgroundResource(R.drawable.buttonfondo3);
            }
        });
        fondo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegadorperfil.setBackgroundResource(R.drawable.fondodegradado4);
                editor = preferences.edit();
                editor.putBoolean("fondo", false);
                editor.putBoolean("fondo2", false);
                editor.putBoolean("fondo3", false);
                editor.putBoolean("fondo4", true);
                editor.commit();
                btnCambiar.setBackgroundResource(R.drawable.buttonfondo4);
                btnLogin.setBackgroundResource(R.drawable.buttonfondo4);
            }
        });

        Usuario();

        btnCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validacion();
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
                editor.commit();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        return view;
    }

    private void Usuario() {

        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(true);

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/datos_usuario.php", new Response.Listener<String>() {
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


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
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


        if (!Objects.equals(nombre.getText().toString(), NombreUsuario)) {
            validacion++;
        }
        if (!Objects.equals(apellido.getText().toString(), ApellidoUsuario)) {
            validacion++;
        }
        if (!Objects.equals(correo.getText().toString(), CorreoUsuario)) {
            validacion++;
        }
        if (!Objects.equals(telefono.getText().toString(), TelefonoUsuario)) {
            validacion++;
        }
        if (!Objects.equals(contraseña.getText().toString(), ContraseñaUsuario)) {
            validacion++;
        }
        if (validacion == 0) {
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("No Hay Cambios..")
                    .setContentText("Ningun Campo Ha Cambiado...")
                    .show();
        } else {
            CambiosDatos();

        }

    }

    private void CambiosDatos() {

        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(true);

        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/cambiar_usuario.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();

                    validacion = 0;
                    if (Objects.equals(response, "Cambios Realizados")) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Cambios Realizados Con Exito!!!!!")
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                Map<String, String> params = new HashMap<String, String>();


                params.put("Nombre", nombre.getText().toString());
                params.put("Apellido", apellido.getText().toString());
                params.put("Email", correo.getText().toString());
                params.put("Telefono", telefono.getText().toString());
                params.put("Contraseña", contraseña.getText().toString());
                params.put("idusuario", id_usuario + "");


                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(stringRequest);

    }

}