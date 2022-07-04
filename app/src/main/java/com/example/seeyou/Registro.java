package com.example.seeyou;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Registro extends AppCompatActivity {

    Button btnNota,btnGaleria,btnFoto,btnEnviar;
    ImageView imgUsuario;
    EditText etNombre,etApellido,etCelular,etEmail,etContraseña;
    //borrar despues
    Button button;

    RequestQueue requestQueue;
    Bitmap bitmap;
    int PICK_IMAGE_REQUEST = 1;
    String UPLOAD_URL = "https://wwwutntrabajos.000webhostapp.com/SEEYOU/agregar_usuarios.php";


    String KEY_IMAGE = "foto";
    String KEY_NOMBRE = "Nombre";
    String KEY_APELLIDO = "Apellido";
    String KEY_CELULAR = "Celular";
    String KEY_EMAIL = "Email";
    String KEY_CONTRASEÑA = "Contraseña";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        btnNota = findViewById(R.id.btnNota);
        btnGaleria = findViewById(R.id.btnGaleria);
        btnFoto = findViewById(R.id.btnFoto);
        btnEnviar = findViewById(R.id.btnEnviar);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCelular = findViewById(R.id.etCelular);
        etEmail = findViewById(R.id.etEmail);
        etContraseña = findViewById(R.id.etContraseña);

        btnNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(Registro.this,Login.class);
                startActivity(i1);
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ESTE PHP NO GUARDA LA FOTO
                registrarUsuarios("https://wwwutntrabajos.000webhostapp.com/SEEYOU/agregar_usuarios_sinfoto.php");
            }
        });

    }

    //TODOS LOS METODOS DE AQUI ABAJO SON PARA CARGAR LA FOTO AL HACER EL REGISTRO Y HACER EL REGISTRO
    //VALOR QUE COMENTE PARA QUE REGISTRARA TODOS LOS DEMAS DATOS CORRECTAMENTE PORQUE AUN NO ENCUENTRO
    //PORQUE FALLA LA VARIABLE DE FOTO/IMAGEN
    public void registrarUsuarios(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Te has registrado correctamente", Toast.LENGTH_SHORT).show();
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
                parametros.put("Nombre",etNombre.getText().toString());
                parametros.put("Apellido",etApellido.getText().toString());
                parametros.put("Celular",etCelular.getText().toString());
                parametros.put("Email",etEmail.getText().toString());
                parametros.put("Contraseña",etContraseña.getText().toString());
                return parametros;
            }
        };

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void uploadImage() {
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(Registro.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(Registro.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //String foto = getStringImagen(bitmap);
                String nombre = etNombre.getText().toString().trim();
                String apellido = etApellido.getText().toString().trim();
                String celular = etCelular.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String contraseña = etContraseña.getText().toString().trim();

                Map<String, String> params = new Hashtable<String, String>();
                //params.put(KEY_IMAGE, foto);
                params.put(KEY_NOMBRE, nombre);
                params.put(KEY_APELLIDO, apellido);
                params.put(KEY_CELULAR, celular);
                params.put(KEY_EMAIL, email);
                params.put(KEY_CONTRASEÑA, contraseña);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
}