package com.example.lakin.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lakin.R;
import com.example.lakin.modelo.PlagasModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

public class PlanillaAdapter extends FirestoreRecyclerAdapter<PlagasModel, PlanillaAdapter.ViewHolder> {
    private Activity activity;
    private List<PlagasModel> plagasSeleccionadas;

    public PlanillaAdapter(@NonNull FirestoreRecyclerOptions<PlagasModel> options, Activity activity) {
        super(options);
        this.activity = activity;
        plagasSeleccionadas = new ArrayList<>();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull PlagasModel plagasModel) {
        holder.checkBox.setText(plagasModel.getNombre());
        holder.checkBox.setChecked(plagasSeleccionadas.contains(plagasModel)); // Marcar checkbox si está en la lista de plagas seleccionadas

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Si se marca el checkbox, agregar la plaga a la lista de plagas seleccionadas
                plagasSeleccionadas.add(plagasModel);
            } else {
                // Si se desmarca el checkbox, quitar la plaga de la lista de plagas seleccionadas
                plagasSeleccionadas.remove(plagasModel);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_plagas_planilla_single, parent, false);
        return new ViewHolder(v);
    }

    public List<PlagasModel> getPlagasSeleccionadas() {
        return plagasSeleccionadas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.Plaga);
        }
    }
}
