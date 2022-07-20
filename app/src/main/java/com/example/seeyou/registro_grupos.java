package com.example.seeyou;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.engine.Resource;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class registro_grupos extends AppCompatActivity {

    TextView unirse;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout contenedor;
    ImageView cerrarregustro;
    EditText NombreGrupo;
    Button registrar;
    SweetAlertDialog pDialog;
    LocationManager locationManager;
    ConnectivityManager locationManagerinternet;
    SharedPreferences preferences;
    String Codigogrupo = "",NombreGrupo1="";
    int id_grupo=0,id_usuario=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_grupos);
        unirse = findViewById(R.id.TVunirse);
        cerrarregustro = findViewById(R.id.IVcerrarcreargrupo);
        cerrarregustro.setVisibility(View.INVISIBLE);
        NombreGrupo = findViewById(R.id.ETnombregrupo);
        registrar = findViewById(R.id.BTNregistrargrupo);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(true);

        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);

        NombreGrupo.setText("Familia "
                + preferences.getString("Apellido",""));

        id_usuario = preferences.getInt("id",0);


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NombreGrupo1 = NombreGrupo.getText().toString();
                CrearGrupo();
            }
        });


        unirse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetBehavior<View> bottomSheetBehavior ;


                bottomSheetDialog = new BottomSheetDialog
                        (registro_grupos.this,R.style.BottomSheetDialog);
                View bottomSheetView = LayoutInflater.from(registro_grupos.this).inflate(
                        R.layout.unirse_grupo, null);


                bottomSheetDialog.setContentView(bottomSheetView);
                contenedor = bottomSheetDialog.findViewById(R.id.ContenedorUnirse);
                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.92);
                assert contenedor !=null;
                contenedor.setMinimumHeight(height);

                bottomSheetDialog.show();

                EditText numero1,numero2,numero3,numero4,numero5,numero6;
                Button unirse;

                numero1 = bottomSheetDialog.findViewById(R.id.ETdigito1);
                numero2 = bottomSheetDialog.findViewById(R.id.ETdigito2);
                numero3 = bottomSheetDialog.findViewById(R.id.ETdigito3);
                numero4 = bottomSheetDialog.findViewById(R.id.ETdigito4);
                numero5 = bottomSheetDialog.findViewById(R.id.ETdigito5);
                numero6 = bottomSheetDialog.findViewById(R.id.ETdigito6);
                unirse = bottomSheetDialog.findViewById(R.id.BTNunirse);
                ImageView cerrarunir = bottomSheetDialog.findViewById(R.id.IVcerrarunir);

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

                        if (codigo.length()==1){

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

                        if (codigo.length()==1){
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

                        if (codigo.length()==1){
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

                        if (codigo.length()==1){
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

                        if (codigo.length()==1){
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

                        if (codigo.length()==1){
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





            }
        });
    }

    private void CrearGrupo() {
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/agregar_grupo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();


                    if (Objects.equals(response, "Error Grupo")) {
                        new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Crear Tu Grupo....")
                                .show();
                    } else if (Objects.equals(response, "")) {


                        new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("No Hemos Podido Crear Tu Grupo....")
                                .show();

                    }else {
                        new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Correcto!!!!")
                                .setContentText("Tu Grupo Se Ha Creado Exitosamente!!!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                })

                                .show();
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putInt("idgrupo",Integer.parseInt(response));
                        editor.commit();





                    }


                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
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
                                new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("No Hemos Podido Obtener Los Puntos De Su Mapa...")
                                        .show();
                            } else {
                                new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                        .show();
                            }
                        } catch (Exception e) {
                            pDialog.dismiss();
                            new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                    .show();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("Nombre", NombreGrupo1);
                params.put("idusuario", id_usuario+"");


                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }


    private void UnirseGrupo() {
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/unirse_grupo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();



                    if (Objects.equals(response, "Error Grupo")) {
                        new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("EL Grupo No Existe... Introduzca Un Codigo Valido....")
                                .show();
                    } else if (Objects.equals(response, "Error Usuario")) {
                        new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Algo Salio Mal..")
                                .setContentText("Ya Formas Parte De Este Grupo....")
                                .show();
                    } else {
                        new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Correcto!!!!")
                                .setContentText("Ahora Formas Parte De Este Grupo!!!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                })
                                .show();

                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putInt("idgrupo",Integer.parseInt(response));
                        editor.commit();

                        id_grupo = preferences.getInt("idgrupo",0);
                        bottomSheetDialog.dismiss();





                    }


                } catch (Exception e) {
                    pDialog.dismiss();
                    new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
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
                                new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("No Hemos Podido Obtener Los Puntos De Su Mapa...")
                                        .show();
                            } else {
                                new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Algo Salio Mal..")
                                        .setContentText("Por Favor Habilite Su Internet Para Poder Cargar Sus Puntos...")
                                        .show();
                            }
                        } catch (Exception e) {
                            pDialog.dismiss();
                            new SweetAlertDialog(registro_grupos.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                                    .show();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("codigo", Codigogrupo);
                params.put("idusuario", id_usuario+"");


                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }


}