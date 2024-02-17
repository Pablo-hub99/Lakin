package com.example.lakin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class grafica extends AppCompatActivity {

    ImageView btnAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grafica_view); // Establece la vista desde plagas_view.xml

        // Configuración del botón para volver atrás
        btnAtras = findViewById(R.id.btnAtrasgrafica);
        btnAtras.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de volver atrás
            Intent intent = new Intent(grafica.this, MainActivity.class);
            startActivity(intent); // Inicia la actividad principal (MainActivity)
            finish(); // Finaliza la actividad actual
        });

    }
}
