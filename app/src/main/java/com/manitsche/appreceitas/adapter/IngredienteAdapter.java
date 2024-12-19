package com.manitsche.appreceitas.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.manitsche.appreceitas.R;
import com.manitsche.appreceitas.model.Ingrediente;

import java.util.ArrayList;
import java.util.List;

public class IngredienteAdapter extends ArrayAdapter<Ingrediente> {

    private Context context;
    private List<Ingrediente> ingredientes;

    public IngredienteAdapter(@NonNull Context context, List<Ingrediente> ingredientes) {
        super(context, 0, ingredientes);
        this.context = context;
        this.ingredientes = ingredientes != null ? ingredientes : new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ingrediente, parent, false);
        }

        // Referências aos TextViews
        TextView idIngrediente = convertView.findViewById(R.id.idIngrediente);
        TextView informacaoIngrediente = convertView.findViewById(R.id.descricaoIngrediente);

        // Verifica se a posição é válida
        if (position < ingredientes.size()) {
            // Recupera o ingrediente da posição atual
            Ingrediente ingrediente = ingredientes.get(position);

            idIngrediente.setText(String.valueOf(ingrediente.getIdingrediente()));
            informacaoIngrediente.setText(ingrediente.getNome());

            // Define a cor do texto e o estilo de negrito
            idIngrediente.setTextColor(ContextCompat.getColor(context, R.color.black));
            informacaoIngrediente.setTextColor(ContextCompat.getColor(context, R.color.black));
            informacaoIngrediente.setTypeface(null, Typeface.BOLD);
        } else {
            // Lida com uma possível posição inválida ou ausência de dados
            idIngrediente.setText("");  // Deixa o campo vazio
            informacaoIngrediente.setText("");  // Deixa o campo vazio
        }

        return convertView;
    }

    public void atualizarIngredientes(List<Ingrediente> novosIngredientes) {
        this.ingredientes = novosIngredientes != null ? novosIngredientes : new ArrayList<>();
        notifyDataSetChanged();  // Atualiza a exibição da lista
    }
}