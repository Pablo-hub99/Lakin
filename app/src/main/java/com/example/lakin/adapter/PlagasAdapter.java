package com.example.lakin.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lakin.R;
import com.example.lakin.addplagas;
import com.example.lakin.modelo.PlagasModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class PlagasAdapter extends FirestoreRecyclerAdapter<PlagasModel, PlagasAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    Activity activity;

    // Constructor que recibe FirestoreRecyclerOptions y la Activity asociada
    public PlagasAdapter(@NonNull FirestoreRecyclerOptions<PlagasModel> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    // Método que se llama cuando se enlaza un elemento de datos a una vista en el RecyclerView
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull PlagasModel PlagasModel) {
        // Obtiene la referencia del documento actual en Firestore
        DocumentSnapshot ds = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
        final String id = ds.getId();

        // Establece el nombre de la plaga en el TextView correspondiente
        holder.nombre.setText(PlagasModel.getNombre());

        // Configura el botón de edición para abrir la actividad de edición con el ID de la plaga
        holder.btnEdit.setOnClickListener(view -> {
            Intent i1 = new Intent(activity, addplagas.class);
            i1.putExtra("id_Plagas", id);
            activity.startActivity(i1);
        });

        // Configura el botón de eliminación para eliminar la plaga de Firestore al hacer clic
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(id);
            }

            // Método para eliminar la plaga de Firestore
            private void deleteUser(String id) {
                mFirestore.collection("Plagas").document(id).delete()
                        .addOnSuccessListener(unused ->
                                Toast.makeText(activity, "Plaga eliminada correctamente", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(activity, "Error al eliminar la plaga", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Método para crear nuevas instancias del ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño de un elemento de la lista de plagas
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_plagas_single, parent, false);
        return new ViewHolder(v);
    }

    // ViewHolder que contiene las vistas de cada elemento de la lista de plagas
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        ImageView btnDelete, btnEdit;

        // Constructor que vincula las vistas del diseño
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}


