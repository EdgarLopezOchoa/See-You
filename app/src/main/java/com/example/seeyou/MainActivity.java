package com.example.seeyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    RutasFragment RutasFragment = new RutasFragment();
    MapsFragment mapsFragment = new MapsFragment();
    PerfilFragment perfilFragment = new PerfilFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();

        window.setBackgroundDrawableResource(R.drawable.statusbar);

        Toolbar toolbar = findViewById(R.id.navegador1);

        setSupportActionBar(toolbar);

        //llama al fragmento de mapa y lo pone en el FrameLayout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.FrameLayout,mapsFragment);
        transaction.commit();

        BottomNavigationView navigation = findViewById(R.id.navegador);
        navigation.setOnNavigationItemSelectedListener(mOnNavigacionItemSelectedListener);
    }
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigacionItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //la navegacion entre fragmentos de la barra inferior

            switch (item.getItemId()){
                case R.id.RutasFragment:
                    loadFragment(RutasFragment);
                    return true;
                case R.id.MapsFragment:
                    loadFragment(mapsFragment);
                    return true;
                case R.id.PerfilFragment:
                    loadFragment(perfilFragment);
                    return true;
            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment){

        //remplaza el fragmentLayour por los fragmentos
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.FrameLayout,fragment);
        transaction.commit();
    }
}