package com.example.lakin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Establece la vista desde activity_main.xml

        // Vinculación de elementos de la interfaz con las variables correspondientes
        ImageView btnUsuario = findViewById(R.id.btnUsuario); // Botón para acceder a la sección de usuarios
        ImageView btnPlagas = findViewById(R.id.btnplagas); // Botón para acceder a la sección de plagas
        Button btnCerrar = findViewById(R.id.btnCerrarSesion); // Botón para cerrar sesión

        // Configuración de listeners para los botones
        btnUsuario.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de usuarios
            Intent intent = new Intent(MainActivity.this, usuarios.class);
            startActivity(intent); // Inicia la actividad de usuarios
        });

        btnPlagas.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de plagas
            Intent intent = new Intent(MainActivity.this, plagas.class);
            startActivity(intent); // Inicia la actividad de plagas
        });

        btnCerrar.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de cerrar sesión
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent); // Inicia la actividad de inicio de sesión (Login)
        });
    }
}
