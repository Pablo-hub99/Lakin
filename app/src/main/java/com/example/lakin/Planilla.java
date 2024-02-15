package com.example.lakin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lakin.adapter.PlanillaAdapter;
import com.example.lakin.modelo.PlagasModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class Planilla extends AppCompatActivity {

    private FirebaseFirestore db;
    private PlanillaAdapter planillaAdapter;
    private RecyclerView recyclerView;
    ImageView btnAtrasPlanilla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planilla_view); // Reemplaza tu_layout con el nombre de tu layout XML

        // Configuración del botón para volver atrás
        btnAtrasPlanilla = findViewById(R.id.btnAtrasPlanilla);
        btnAtrasPlanilla.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de volver atrás
            Intent intent = new Intent(Planilla.this, MainActivity.class);
            startActivity(intent); // Inicia la actividad principal (MainActivity)
            finish(); // Finaliza la actividad actual
        });

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        // Configura el RecyclerView
        recyclerView = findViewById(R.id.PlanillaList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crea las opciones de FirestoreRecyclerOptions para las plagas
        FirestoreRecyclerOptions<PlagasModel> options = new FirestoreRecyclerOptions.Builder<PlagasModel>()
                .setQuery(db.collection("Plagas"), PlagasModel.class)
                .build();

        // Crea una instancia del adaptador PlagasAdapter
        planillaAdapter = new PlanillaAdapter(options, this);

        // Configura el adaptador en el RecyclerView
        recyclerView.setAdapter(planillaAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Inicia la escucha del adaptador
        planillaAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Detiene la escucha del adaptador
        planillaAdapter.stopListening();
    }
}