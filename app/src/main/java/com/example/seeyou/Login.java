package com.example.seeyou;

import static android.view.View.VISIBLE;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity {

    Button btnNota, btnIngresar;
    //borrar despues
    Button enviar, cancelar, btnGaleria, btningresarsesion;
    TextInputEditText Nombre, Contraseña, Apellido, Celular, Email;
    private LinearLayout contenedor;
    TextInputEditText etEmail, etContraseña;
    ImageView imgUsuario;
    CheckBox sesion;
    int PICK_IMAGE_REQUEST = 1, id = 0, registro = 0;
    String str_email, str_password;
    TextView irregistro;
    RequestQueue requestQueue;
    String key_foto = "foto";
    Bitmap bitmap;
    String url = "https://mifolderdeproyectos.online/SEEYOU/login.php";
    private boolean esVisible = true;
    String Apellido1, Nombre1;
    SweetAlertDialog Eliminar_Marcador, CambiarSesion;
    BottomSheetDialog bottomSheetDialog;
    ConnectivityManager locationManagerinternet;
    SweetAlertDialog pDialog;
    LocationManager locationManager;
    TextView Asterisco1, Asterisco2, Asterisco3, Asterisco4, Asterisco5;
    Double Latitud, Longitud;
    TextView tvterminos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        locationManagerinternet = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(true);

        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);


        if (preferences.getBoolean("terminos",false) != true){
        MostrarTerminos();
        }
        etEmail = findViewById(R.id.etEmail2);
        etContraseña = findViewById(R.id.etContraseña2);
        irregistro = findViewById(R.id.TVirregistro);
        contenedor = findViewById(R.id.Contenedormarker);
        sesion = findViewById(R.id.CBsesion);
        btnIngresar = findViewById(R.id.btnIngresar);



        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validacion();
            }
        });

        irregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetBehavior<View> bottomSheetBehavior ;


                bottomSheetDialog = new BottomSheetDialog
                        (Login.this,R.style.BottomSheetDialog);
                View bottomSheetView = LayoutInflater.from(Login.this).inflate(
                        R.layout.activity_registro, null
                );

                bottomSheetDialog.setContentView(bottomSheetView);
                LinearLayout contenedor1 = bottomSheetDialog.findViewById(R.id.BottomSheetRegister);
                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.92);
                assert contenedor1 !=null;
                contenedor1.setMinimumHeight(height);
                bottomSheetBehavior.setMaxHeight(height);
                bottomSheetDialog.show();


                enviar = bottomSheetDialog.findViewById(R.id.btnEnviar);
                Nombre = bottomSheetDialog.findViewById(R.id.etNombre);
                Contraseña = bottomSheetDialog.findViewById(R.id.etContraseña);
                Apellido = bottomSheetDialog.findViewById(R.id.etApellido);
                Celular = bottomSheetDialog.findViewById(R.id.etCelular);
                Email = bottomSheetDialog.findViewById(R.id.etEmail);
                cancelar = bottomSheetDialog.findViewById(R.id.btncancelarregistro);
                Asterisco1 = bottomSheetDialog.findViewById(R.id.TVasterisco1);
                Asterisco2 = bottomSheetDialog.findViewById(R.id.TVasterisco2);
                Asterisco3 = bottomSheetDialog.findViewById(R.id.TVasterisco3);
                Asterisco4 = bottomSheetDialog.findViewById(R.id.TVasterisco4);
                Asterisco5 = bottomSheetDialog.findViewById(R.id.TVasterisco5);
                imgUsuario = bottomSheetDialog.findViewById(R.id.IVuser);
                bottomSheetBehavior.setDraggable(false);

                btnGaleria = bottomSheetDialog.findViewById(R.id.btngaleria);

                btnGaleria.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFileChooser();
                    }
                });

               ImageView cerrar = bottomSheetDialog.findViewById(R.id.IVcloseregistro);

               cerrar.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       bottomSheetBehavior.setState(bottomSheetBehavior.STATE_HIDDEN);
                   }
               });

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.cancel();
                    }
                });


                enviar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLastLocation();
                    }
                });
            }
        });


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


    }


    public void MostrarTerminos(){
        BottomSheetBehavior<View> bottomSheetBehavior ;


        BottomSheetDialog bottomSheetDialogTerminos = new BottomSheetDialog
                (Login.this,R.style.BottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(Login.this).inflate(
                R.layout.terminos_y_condiciones, null
        );

        bottomSheetDialogTerminos.setContentView(bottomSheetView);
        LinearLayout contenedor1 = bottomSheetDialogTerminos.findViewById(R.id.BottomSheetRegister);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.92);
        assert contenedor1 !=null;
        contenedor1.setMinimumHeight(height);
        bottomSheetBehavior.setMaxHeight(height);
        bottomSheetDialogTerminos.show();
        bottomSheetBehavior.setDraggable(false);
        tvterminos = bottomSheetDialogTerminos.findViewById(R.id.TVterminos1);
        Button btncancelar = bottomSheetDialogTerminos.findViewById(R.id.btncancelarterminos);
        CheckBox CBaceptarterminos = bottomSheetDialogTerminos.findViewById(R.id.CBaceptarterminos);
        Button btnaceptar = bottomSheetDialogTerminos.findViewById(R.id.btnacetarterminos);
        btnaceptar.setBackgroundResource(R.drawable.buttonterminos);
        btnaceptar.setClickable(false);

        bottomSheetDialogTerminos.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == event.KEYCODE_BACK) {
                   finish();
                }
                return false;

            }

        });


        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CBaceptarterminos.isChecked() == true) {
                    bottomSheetDialogTerminos.dismiss();
                    SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("terminos", true);
                    editor.commit();

                }
            }
        });

        CBaceptarterminos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (CBaceptarterminos.isChecked() == true){
                    btnaceptar.setBackgroundResource(R.drawable.button2);
                    btnaceptar.setClickable(true);
                }else{
                    btnaceptar.setBackgroundResource(R.drawable.buttonterminos);
                    btnaceptar.setClickable(false);
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == event.KEYCODE_BACK) {
            Eliminar_Marcador = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            Eliminar_Marcador.setTitleText("¿Estas Seguro?");
            Eliminar_Marcador.setContentText("Estas Apunto De Salir De See You...,\n " +
                    "See You Seguira Trabajando En Segundo Plano.");
            Eliminar_Marcador.setConfirmText("Salir");
            Eliminar_Marcador.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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
        return super.onKeyDown(keyCode, event);
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleciona imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Cómo obtener el mapa de bits de la Galería
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Configuración del mapa de bits en ImageView
                imgUsuario.setImageBitmap(bitmap);
            } catch (IOException e) {
                new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Algo Salio Mal..")
                        .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                        .show();
            }
        }
    }

    public void validacion() {
        if (etEmail.getText().toString().equals("")) {
            SweetAlertDialog error = new SweetAlertDialog(Login.this,
                    SweetAlertDialog.ERROR_TYPE);
            error.setTitleText("Campo Vacio!!...");
            error.setContentText("Por Favor Intruduzca Un Email...");
            error.show();
        } else if (etContraseña.getText().toString().equals("")) {
            SweetAlertDialog error = new SweetAlertDialog(Login.this,
                    SweetAlertDialog.ERROR_TYPE);
            error.setTitleText("Campo Vacio!!...");
            error.setContentText("Por Favor Introduzca Una Contraseña...");
            error.show();
        } else if (sesion.isChecked() == false) {
            CambiarSesion = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            CambiarSesion.setTitleText("La Casilla De Mantener Sesion No Esta Marcada...");
            CambiarSesion.setContentText("¿Desea Mantener Su Sesion Activa?");
            CambiarSesion.setConfirmText("SI");
            CambiarSesion.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sesion.setChecked(true);
                    str_email = etEmail.getText().toString().trim();
                    str_password = etContraseña.getText().toString().trim();

                    Validar(str_email, str_password);
                    CambiarSesion.dismiss();

                }
            });
            CambiarSesion.setCancelText("NO");
            CambiarSesion.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    str_email = etEmail.getText().toString().trim();
                    str_password = etContraseña.getText().toString().trim();

                    Validar(str_email, str_password);
                    CambiarSesion.dismiss();

                }
            });
            CambiarSesion.show();
        } else if (sesion.isChecked() == true) {
            str_email = etEmail.getText().toString().trim();
            str_password = etContraseña.getText().toString().trim();

            Validar(str_email, str_password);
        }
    }

    public void ValidarRegistro() {
        if (Nombre.getText().toString().equals("")) {
            Asterisco1.setVisibility(View.VISIBLE);

        } else {
            Asterisco1.setVisibility(View.INVISIBLE);
        }

        if (Apellido.getText().toString().equals("")) {
            Asterisco2.setVisibility(VISIBLE);
        } else {
            Asterisco2.setVisibility(View.INVISIBLE);
        }

        if (Celular.getText().toString().equals("")) {
            Asterisco3.setVisibility(VISIBLE);
        } else {
            Asterisco3.setVisibility(View.INVISIBLE);
        }
        if (Email.getText().toString().equals("")) {
            Asterisco4.setVisibility(VISIBLE);
        } else {
            Asterisco4.setVisibility(View.INVISIBLE);
        }
        if (Contraseña.getText().toString().equals("")) {
            Asterisco5.setVisibility(VISIBLE);
        } else {
            Asterisco5.setVisibility(View.INVISIBLE);
        }


        if (!Nombre.getText().toString().equals("") && !Apellido.getText().toString().equals("")
                && !Contraseña.getText().toString().equals("") && !Celular.getText().toString().equals("")
                && !Email.getText().toString().equals("") && !Contraseña.getText().toString().equals("")) {

            registrarUsuarios(Nombre.getText().toString(), Apellido.getText().toString(),
                    Celular.getText().toString(), Email.getText().toString(), Contraseña.getText().toString()
            );
            registro = 1;
        } else {
            SweetAlertDialog error = new SweetAlertDialog(Login.this,
                    SweetAlertDialog.ERROR_TYPE);
            error.setTitleText("Campo(s) Vacio(s)!!...");
            error.setContentText("Por Favor Llene Los Campos Marcados...");
            error.show();
        }
    }

//Despues de optener la foto esta la compbierte para poderla mandar a la base de datos
    public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void registrarUsuarios(String Nombre, String Apellido, String Celular, String Email, String Contraseña) {
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/agregar_usuarios.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pDialog.dismiss();

                SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                boolean sesion = true;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("sesion_usuario", sesion);
                editor.commit();

                Validar(Email, Contraseña);
                //limpiar();
            }
        }, new Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                if (locationManagerinternet.getActiveNetworkInfo() != null
                        && locationManagerinternet.getActiveNetworkInfo().isAvailable()
                        && locationManagerinternet.getActiveNetworkInfo().isConnected()) {
                    new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("No Hemos Podido Registrar Al Usuario...")
                            .show();
                } else {
                    new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Por Favor Habilite Su Internet...")
                            .show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
               String IMAGEN = getStringImagen(bitmap);
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("Nombre", Nombre);
                parametros.put("Apellido", Apellido);
                parametros.put("Celular", Celular);
                parametros.put("Email", Email);
                parametros.put("Contraseña", Contraseña);
                parametros.put(key_foto,IMAGEN);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        try {

            FusedLocationProviderClient locationClient = getFusedLocationProviderClient(Login.this);

            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                ValidarRegistro();
                                Longitud = location.getLongitude();
                                Latitud = location.getLatitude();



                            } else {
                                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                                    new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
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
                            new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Algo Salio Mal..")
                                    .setContentText("Por Favor Active Su Ubicacion o Permita Que La App Accesada A Ella...")
                                    .show();
                        }
                    });
        } catch (Exception e) {
            pDialog.dismiss();
            new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Algo Salio Mal..")
                    .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                    .show();
        }
    }


    public void Validar(String Email_usuario, String Contraseña_usuario) {


        SweetAlertDialog pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        str_email = etEmail.getText().toString().trim();
        str_password = etContraseña.getText().toString().trim();


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();


                try {
                    JSONArray array = new JSONArray(response);


                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cajas = array.getJSONObject(i);

                        id = cajas.getInt("idusuario");
                        Nombre1 = cajas.getString("Nombre");
                        Apellido1 = cajas.getString("Apellido");


                    }

                    if (Objects.equals(response, "[]")) {

                        SweetAlertDialog error = new SweetAlertDialog(Login.this,
                                SweetAlertDialog.ERROR_TYPE);
                        error.setTitleText("Op...Algo Salio Mal...");
                        error.setContentText("Revise los datos y vuelva a intentarlo");
                        error.show();


                    } else {

                        etEmail.setText("");
                        etContraseña.setText("");


                        if (sesion.isChecked() == true) {
                            SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                            boolean sesion = true;
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("sesion_usuario", sesion);
                            editor.commit();

                        }
                        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("id", id);
                        editor.putString("Nombre", Nombre1);
                        editor.putString("Apellido", Apellido1);
                        editor.putInt("idgrupo", 0);
                        editor.commit();


                        if (registro != 1) {
                            new SweetAlertDialog(Login.this,
                                    SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Se Ha Iniciado Sesion Correctamente")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            registro = 0;
                                            finish();

                                        }
                                    })
                                    .show();
                        } else {
                            new SweetAlertDialog(Login.this,
                                    SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Se Ha Registrado Correctamente")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            startActivity(new Intent(getApplicationContext(), registro_grupos.class));
                                            registro = 0;
                                            finish();
                                        }
                                    })
                                    .show();
                            preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                            boolean sesion = true;
                            editor = preferences.edit();
                            editor.putBoolean("sesion_usuario", sesion);
                            editor.commit();
                        }
                    }

                } catch (JSONException e) {
                    new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                            .show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                new SweetAlertDialog(Login.this,
                        SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Ops...Algo Salio Mal..")
                        .setContentText("No Hemos Podido Iniciar Sesion...")
                        .show();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", Email_usuario);
                params.put("password", Contraseña_usuario);
                params.put("latitud", Latitud + "");
                params.put("longitud", Longitud + "");
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);


    }
}