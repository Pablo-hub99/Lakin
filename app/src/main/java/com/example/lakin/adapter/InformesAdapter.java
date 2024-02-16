package com.example.lakin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lakin.R;
import com.example.lakin.modelo.InformeModel;

import java.util.List;

public class InformesAdapter extends RecyclerView.Adapter<InformesAdapter.InformeViewHolder> {

    private List<InformeModel> informesList;
    private Context context;
    private OnInformeClickListener listener;

    public InformesAdapter(List<InformeModel> informesList, Context context, OnInformeClickListener listener) {
        this.informesList = informesList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InformeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_informe_single, parent, false);
        return new InformeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InformeViewHolder holder, int position) {
        InformeModel informe = informesList.get(position);
        holder.bind(informe);
    }

    @Override
    public int getItemCount() {
        return informesList.size();
    }

    public class InformeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView usuarioTextView;
        private TextView fechaHoraTextView;
        private InformeModel informe;

        public InformeViewHolder(@NonNull View itemView) {
            super(itemView);
            usuarioTextView = itemView.findViewById(R.id.usuarioTextView);
            fechaHoraTextView = itemView.findViewById(R.id.fechaHoraTextView);
            itemView.setOnClickListener(this);
        }

        public void bind(InformeModel informe) {
            this.informe = informe;
            usuarioTextView.setText(informe.getUsuario());
            fechaHoraTextView.setText(informe.getFechaHora());
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onInformeClick(informe);
            }
        }
    }

    public interface OnInformeClickListener {
        void onInformeClick(InformeModel informe);
    }
}
