package com.example.lakin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lakin.modelo.PlanillaDataModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class grafica extends AppCompatActivity {

    ImageView btnAtras;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grafica_view);

        // Inicializar la gráfica de barras desde el layout XML
        barChart = findViewById(R.id.bar_chart);

        // Configuración del botón para volver atrás
        btnAtras = findViewById(R.id.btnAtrasgrafica);
        btnAtras.setOnClickListener(v -> {
            Intent intent = new Intent(grafica.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Configurar y mostrar la gráfica de barras
        setData();
    }

    private void setData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Diccionario para almacenar el recuento de plagas por tipo
        Map<String, Integer> plagasCountMap = new HashMap<>();

        // Obtener los datos de la colección 'Planilla' de Firestore
        db.collection("Planilla")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<PlanillaDataModel> planillasData = new ArrayList<>();

                        // Recorrer los documentos recuperados y agregar los datos a la lista
                        for (DocumentSnapshot document : task.getResult()) {
                            PlanillaDataModel planilla = document.toObject(PlanillaDataModel.class);
                            planillasData.add(planilla);
                        }

                        // Recorrer las planillas y contar las plagas seleccionadas en cada una
                        for (PlanillaDataModel planilla : planillasData) {
                            List<String> plagasSeleccionadas = planilla.getPlagasSeleccionadas();
                            for (String plaga : plagasSeleccionadas) {
                                if (plagasCountMap.containsKey(plaga)) {
                                    // Incrementar el contador si la plaga ya está en el mapa
                                    plagasCountMap.put(plaga, plagasCountMap.get(plaga) + 1);
                                } else {
                                    // Agregar la plaga al mapa si es la primera vez que se encuentra
                                    plagasCountMap.put(plaga, 1);
                                }
                            }
                        }

                        // Crear una lista de BarEntries para almacenar los datos de la gráfica de barras
                        List<BarEntry> entries = new ArrayList<>();
                        List<String> labels = new ArrayList<>(); // Lista para almacenar las etiquetas

                        // Recorrer el mapa de recuento de plagas y agregar las entradas a la gráfica
                        int index = 0;
                        for (Map.Entry<String, Integer> entry : plagasCountMap.entrySet()) {
                            entries.add(new BarEntry(index++, entry.getValue()));
                            labels.add(entry.getKey()); // Agregar el nombre de la plaga a las etiquetas
                        }

                        // Crear un conjunto de datos para la gráfica de barras
                        BarDataSet dataSet = new BarDataSet(entries, "Plagas");

                        // Configurar los colores para las barras de la gráfica
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                        // Crear un objeto BarData con el conjunto de datos
                        BarData data = new BarData(dataSet);

                        // Configurar el tamaño y color del texto de los valores en la gráfica
                        data.setValueTextSize(16f);
                        data.setValueTextColor(Color.BLACK);

                        // Establecer el formateador para mostrar los nombres de las plagas dentro de las barras
                        data.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getBarLabel(BarEntry barEntry) {
                                int index = (int) barEntry.getX();
                                return labels.get(index);
                            }
                        });

                        // Establecer los datos en la gráfica de barras
                        barChart.setData(data);

                        // Configurar las etiquetas del eje X
                        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

                        // Actualizar la gráfica de barras
                        barChart.invalidate();
                    } else {
                        // Manejar errores de recuperación de datos
                        Exception exception = task.getException();
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                        Toast.makeText(grafica.this, "Error al recuperar los datos de Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
