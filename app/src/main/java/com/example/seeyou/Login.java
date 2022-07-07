package com.example.seeyou;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button btnNota, btnIngresar;
    //borrar despues
    Button button,enviar,cancelar,btnGaleria;
    EditText Nombre,Contraseña,Apellido,Celular,Email;
    private LinearLayout contenedor;
    EditText etEmail, etContraseña;
    ImageView imgUsuario;
    CheckBox sesion;
    int PICK_IMAGE_REQUEST = 1 , id = 0;
    String str_email,str_password;
    TextView irregistro;
    RequestQueue requestQueue;
    Bitmap bitmap;
    String url = "https://wwwutntrabajos.000webhostapp.com/SEEYOU/login.php";
    private boolean esVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail2);
        etContraseña = findViewById(R.id.etContraseña2);
        irregistro = findViewById(R.id.TVirregistro);
        contenedor = findViewById(R.id.Contenedormarker);
        sesion = findViewById(R.id.CBsesion);

        irregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog
                        (Login.this, R.style.BottomSheetDialog);
                View bottomSheetView = LayoutInflater.from(Login.this).inflate(
                        R.layout.activity_registro, (LinearLayout) contenedor
                );

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                enviar = bottomSheetDialog.findViewById(R.id.btnEnviar);
                Nombre = bottomSheetDialog.findViewById(R.id.etNombre);
                Contraseña = bottomSheetDialog.findViewById(R.id.etContraseña);
                Apellido = bottomSheetDialog.findViewById(R.id.etApellido);
                Celular = bottomSheetDialog.findViewById(R.id.etCelular);
                Email = bottomSheetDialog.findViewById(R.id.etEmail);
                cancelar = bottomSheetDialog.findViewById(R.id.btncancelarregistro);
                btnGaleria = bottomSheetDialog.findViewById(R.id.btnGaleria);
                imgUsuario = bottomSheetDialog.findViewById(R.id.imgUsuario);


                btnGaleria.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFileChooser();
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
                        registrarUsuarios(Nombre.getText().toString(),Apellido.getText().toString(),
                                Celular.getText().toString(),Email.getText().toString(),Contraseña.getText().toString()
                                );
                    }
                });
            }
        });




        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == event.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea Salir De See You?").setPositiveButton("si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
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
                e.printStackTrace();
            }
        }
    }

    public void registrarUsuarios(String Nombre,String Apellido,String Celular,String Email, String Contraseña){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, 
                "https://wwwutntrabajos.000webhostapp.com/SEEYOU/agregar_usuarios.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                //limpiar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Algo salio mal", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String > getParams() throws AuthFailureError {
                Map<String, String > parametros= new HashMap<String, String>();
                parametros.put("Nombre",Nombre);
                parametros.put("Apellido",Apellido);
                parametros.put("Celular",Celular);
                parametros.put("Email",Email);
                parametros.put("Contraseña",Contraseña);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void Validar(View view) {

        if (etEmail.getText().toString().equals("")) {
            Toast.makeText(this, "INTRODUZCA EL EMAIL", Toast.LENGTH_SHORT).show();
        } else if (etContraseña.getText().toString().equals("")) {
            Toast.makeText(this, "INTRODUZCA UNA CONTRASEÑA", Toast.LENGTH_SHORT).show();
        } else {


            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Estamos procesando su solicitud...");

            progressDialog.show();

            str_email = etEmail.getText().toString().trim();
            str_password = etContraseña.getText().toString().trim();


            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();

                    try {
                        JSONArray array = new JSONArray(response);


                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cajas = array.getJSONObject(i);

                            if (response.equalsIgnoreCase("[]")) {

                                Toast.makeText(Login.this, "No Se A Podido Iniciar Sesion", Toast.LENGTH_SHORT).show();

                            } else {

                                etEmail.setText("");
                                etContraseña.setText("");


                                if (sesion.isChecked() == true) {
                                    SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                                    boolean sesion = true;
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("sesion_usuario", sesion);
                                    editor.putInt("id", cajas.getInt("idusuario"));
                                    editor.commit();

                                }

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                                Toast.makeText(Login.this, "Se Inicio Sesion Correctamente", Toast.LENGTH_SHORT).show();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", str_email);
                    params.put("password", str_password);
                    return params;

                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
            requestQueue.add(request);


        }
    }
}