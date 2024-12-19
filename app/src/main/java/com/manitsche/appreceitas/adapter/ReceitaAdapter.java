package com.manitsche.appreceitas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.manitsche.appreceitas.R;
import com.manitsche.appreceitas.model.Receita;
import java.util.List;

public class ReceitaAdapter extends RecyclerView.Adapter<ReceitaAdapter.ReceitaViewHolder> {

    private final Context context;
    private final List<Receita> receitas;

    // Construtor do adaptador
    public ReceitaAdapter(Context context, List<Receita> receitas) {
        this.context = context;
        this.receitas = receitas;
    }

    @NonNull
    @Override
    public ReceitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_receita, parent, false);
        return new ReceitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceitaViewHolder holder, int position) {
        Receita receita = receitas.get(position);
        holder.idReceita.setText(String.valueOf(receita.getIdreceita()));
        holder.descricaoReceita.setText(receita.getTitulo());
    }

    @Override
    public int getItemCount() {
        return receitas.size();
    }

    // Método para obter o item da lista de receitas na posição desejada
    public Receita getItem(int position) {
        return receitas.get(position);
    }

    public static class ReceitaViewHolder extends RecyclerView.ViewHolder {
        TextView idReceita;
        TextView descricaoReceita;

        public ReceitaViewHolder(View itemView) {
            super(itemView);
            idReceita = itemView.findViewById(R.id.idReceita);
            descricaoReceita = itemView.findViewById(R.id.descricaoReceita);
        }
    }
}