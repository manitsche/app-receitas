package com.manitsche.appreceitas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;
import com.manitsche.appreceitas.R;
import com.manitsche.appreceitas.model.Receita;
import java.util.List;

public class ReceitaSpinnerAdapter extends BaseAdapter {

    private final Context context;
    private final List<Receita> receitas;

    public ReceitaSpinnerAdapter(Context context, List<Receita> receitas) {
        this.context = context;
        this.receitas = receitas;
    }

    @Override
    public int getCount() {
        return receitas.size();
    }

    @Override
    public Object getItem(int position) {
        return receitas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return receitas.get(position).getIdreceita(); // Use o ID da receita
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_receita_spinner, parent, false);
        }

        Receita receita = receitas.get(position);
        TextView descricaoReceita = convertView.findViewById(R.id.descricaoReceita);

        descricaoReceita.setText(receita.getTitulo());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent); // Reutiliza o método `getView`
    }
}
