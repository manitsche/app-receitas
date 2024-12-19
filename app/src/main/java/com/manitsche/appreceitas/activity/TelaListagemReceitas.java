package com.manitsche.appreceitas.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manitsche.appreceitas.R;
import com.manitsche.appreceitas.adapter.ReceitaAdapter;
import com.manitsche.appreceitas.bdhelper.BancoDeDados;
import com.manitsche.appreceitas.model.Receita;

import java.util.List;

public class TelaListagemReceitas extends AppCompatActivity {

    private BancoDeDados bancoDeDados;
    private RecyclerView recyclerViewReceitas;
    Button buttonCadastrarReceita;
    String tela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_listagem_receitas);

        tela = "TelaListagemReceitas";

        inicializarComponentes();
        configurarRecyclerView();
        carregarReceitas();

        buttonCadastrarReceita.setOnClickListener(view -> abrirFormularioCadastro());
    }

    private void inicializarComponentes() {
        recyclerViewReceitas = findViewById(R.id.recyclerViewReceitas);
        buttonCadastrarReceita = findViewById(R.id.buttonCadastrarNovaReceita);
        bancoDeDados = new BancoDeDados(this);
    }

    private void configurarRecyclerView() {
        recyclerViewReceitas.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewReceitas.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerViewReceitas, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ReceitaAdapter adapter = (ReceitaAdapter) recyclerViewReceitas.getAdapter();
                        if (adapter != null && position < adapter.getItemCount()) {
                            Receita receitaSelecionada = adapter.getItem(position);
                            exibirDialogoOpcoes(receitaSelecionada);
                        } else {
                            Log.e("TelaListagemReceitas", "Receita inválida na posição: " + position);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Clique longo pode ser implementado aqui, se necessário.
                    }
                })
        );
    }

    private void carregarReceitas() {
        try {
            List<Receita> receitas = bancoDeDados.listarReceitas();
            ReceitaAdapter receitaAdapter = new ReceitaAdapter(this, receitas);
            recyclerViewReceitas.setAdapter(receitaAdapter);
        } catch (Exception e) {
            Log.e("TelaListagemReceitas", "Erro ao carregar receitas: " + e.getMessage(), e);
            exibirDialogoErro("Erro ao carregar receitas. Tente novamente.");
        }
    }

    private void abrirFormularioCadastro() {
        Intent intent = new Intent(TelaListagemReceitas.this, TelaFragment.class);
        intent.putExtra("tela", tela);
        startActivity(intent);
    }

    private void excluirReceita(Receita receitaSelecionada) {
        try {
            bancoDeDados.excluirReceita(receitaSelecionada);
            carregarReceitas();
        } catch (Exception e) {
            Log.e("TelaListagemReceitas", "Erro ao excluir a receita: " + e.getMessage(), e);
            exibirDialogoErro("Erro ao excluir a receita. Tente novamente.");
        }
    }

    private void exibirDialogoOpcoes(Receita receitaSelecionada) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaListagemReceitas.this);
        builder.setTitle("Opções para a receita")
                .setMessage("Deseja alterar, excluir ou visualizar a receita \"" + receitaSelecionada.getTitulo() + "\"?")
                .setCancelable(false)
                .setPositiveButton("Alterar", (dialog, which) -> abrirFormularioEdicao(receitaSelecionada))
                .setNegativeButton("Excluir", (dialog, which) -> excluirReceita(receitaSelecionada))
                .setNeutralButton("Visualizar", (dialog, which) -> visualizarReceita(receitaSelecionada));

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void abrirFormularioEdicao(Receita receitaSelecionada) {
        Intent intent = new Intent(TelaListagemReceitas.this, TelaFragment.class);
        intent.putExtra("receita", receitaSelecionada);
        startActivity(intent);
    }

    private void exibirDialogoErro(String mensagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensagem)
                .setTitle("Erro")
                .setCancelable(false)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void visualizarReceita(Receita receitaSelecionada) {
        Intent intent = new Intent(TelaListagemReceitas.this, TelaVisualizarReceita.class);
        intent.putExtra("receita", receitaSelecionada); // Passa a receita selecionada
        startActivity(intent);
    }

}