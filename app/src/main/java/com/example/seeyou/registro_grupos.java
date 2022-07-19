package com.example.seeyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class registro_grupos extends AppCompatActivity {

    TextView unirse;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout contenedor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_grupos);
        unirse = findViewById(R.id.TVunirse);



        unirse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetBehavior<View> bottomSheetBehavior ;


                bottomSheetDialog = new BottomSheetDialog
                        (registro_grupos.this);
                View bottomSheetView = LayoutInflater.from(registro_grupos.this).inflate(
                        R.layout.unirse_grupo, null);


                bottomSheetDialog.setContentView(bottomSheetView);
                contenedor = bottomSheetDialog.findViewById(R.id.ContenedorUnirse);
                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                assert contenedor !=null;
                contenedor.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);


                bottomSheetDialog.show();
            }
        });
    }
}