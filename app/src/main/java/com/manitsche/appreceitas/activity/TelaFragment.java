package com.manitsche.appreceitas.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.manitsche.appreceitas.R;
import com.manitsche.appreceitas.fragment.FormularioIngredienteFragment;
import com.manitsche.appreceitas.fragment.FormularioReceitaFragment;
import com.manitsche.appreceitas.model.Receita;

public class TelaFragment extends AppCompatActivity {

    Button buttonIngrediente, buttonReceita;
    FormularioIngredienteFragment formularioIngredienteFragment;
    FormularioReceitaFragment formularioReceitaFragment;
    String tela;
    Receita receitaAntiga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_fragment);

        buttonIngrediente = findViewById(R.id.buttonIngrediente);
        buttonReceita = findViewById(R.id.buttonReceita);

        formularioIngredienteFragment = new FormularioIngredienteFragment();
        formularioReceitaFragment = new FormularioReceitaFragment();

        tela = getIntent().getStringExtra("tela");

        Bundle dados = getIntent().getExtras();

        if (dados != null) {
            receitaAntiga = (Receita) dados.getSerializable("receita");
        }
        if ("TelaListagemIngredientes".equals(tela)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameConteudo, formularioIngredienteFragment)
                    .commit();
        }
        if ("TelaListagemReceitas".equals(tela)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameConteudo, formularioReceitaFragment)
                    .commit();
        }
        buttonIngrediente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameConteudo, formularioIngredienteFragment)
                        .commit();
            }
        });

        buttonReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameConteudo, formularioReceitaFragment)
                        .commit();
            }
        });
    }
}