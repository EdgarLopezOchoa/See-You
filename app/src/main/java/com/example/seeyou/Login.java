package com.example.seeyou;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button btnNota, btnIngresar;
    //borrar despues
    Button button;

    EditText etEmail, etContraseña;
    String str_email,str_password;
    String url = "https://wwwutntrabajos.000webhostapp.com/SEEYOU/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail2);
        etContraseña = findViewById(R.id.etContraseña2);

        btnNota = findViewById(R.id.btnNota);

        btnNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(Login.this,Registro.class);
                startActivity(i1);

            }
        });

    }

    public void Validar(View view) {

        if (etEmail.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        } else if (etContraseña.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        } else {


            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Por favor espera...");

            progressDialog.show();

            str_email = etEmail.getText().toString().trim();
            str_password = etContraseña.getText().toString().trim();


            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();

                    if (response.equalsIgnoreCase("ingreso correctamente")) {

                        etEmail.setText("");
                        etContraseña.setText("");
                        startActivity(new Intent(getApplicationContext(), MapsFragment.class));
                        Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
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