package com.manitsche.appreceitas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.manitsche.appreceitas.R;
import com.manitsche.appreceitas.adapter.ReceitaAdapter;
import com.manitsche.appreceitas.adapter.IngredienteAdapter;
import com.manitsche.appreceitas.adapter.ReceitaSpinnerAdapter;
import com.manitsche.appreceitas.bdhelper.BancoDeDados;
import com.manitsche.appreceitas.model.Ingrediente;
import com.manitsche.appreceitas.model.Receita;

import java.util.ArrayList;
import java.util.List;

public class TelaListagemIngredientes extends AppCompatActivity {

    private BancoDeDados bancoDeDados;
    private ListView listViewIngredientes;
    private Spinner spinnerReceitas;
    private Button buttonCadastrarIngrediente;
    String tela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_listagem_ingredientes);

        tela = "TelaListagemIngredientes";

        inicializarComponentes();
        carregarIngredientes();
        carregarReceitasNoSpinner();

        buttonCadastrarIngrediente.setOnClickListener(v -> abrirFormularioCadastro());
    }

    private void inicializarComponentes() {
        listViewIngredientes = findViewById(R.id.listViewIngredientes);
        spinnerReceitas = findViewById(R.id.spinnerReceitas); // Referência ao Spinner de Receitas
        buttonCadastrarIngrediente = findViewById(R.id.buttonCadastrarNovoIngrediente);
        bancoDeDados = new BancoDeDados(this);

        listViewIngredientes.setOnItemClickListener((parent, view, position, id) -> {
            IngredienteAdapter adapter = (IngredienteAdapter) listViewIngredientes.getAdapter();
            if (adapter != null && position < adapter.getCount()) {
                Ingrediente ingredienteSelecionado = adapter.getItem(position);
                exibirDialogoOpcoes(ingredienteSelecionado);
            } else {
                Log.e("TelaListagemIngredientes", "Ingrediente inválido na posição: " + position);
            }
        });
    }

    private void carregarIngredientes() {
        try {
            List<Ingrediente> ingredientes = bancoDeDados.listarIngredientes();
            IngredienteAdapter ingredienteAdapter = new IngredienteAdapter(this, ingredientes);
            listViewIngredientes.setAdapter(ingredienteAdapter);
        } catch (Exception e) {
            Log.e("TelaListagemIngredientes", "Erro ao carregar ingredientes: " + e.getMessage(), e);
            exibirDialogoErro("Erro ao carregar os ingredientes. Tente novamente.");
        }
    }

    private void carregarReceitasNoSpinner() {
        try {
            List<Receita> receitas = bancoDeDados.listarReceitas();
            ReceitaSpinnerAdapter adapter = new ReceitaSpinnerAdapter(this, receitas);
            spinnerReceitas.setAdapter(adapter);

            // Adicionar listener para atualizar os ingredientes ao selecionar uma receita
            spinnerReceitas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Receita receitaSelecionada = (Receita) parent.getItemAtPosition(position);
                    atualizarIngredientesPorReceita(receitaSelecionada.getIdreceita());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Limpa a lista se nenhuma receita for selecionada
                    atualizarIngredientesPorReceita(-1);
                }
            });
        } catch (Exception e) {
            Log.e("TelaListagemIngredientes", "Erro ao carregar receitas no Spinner: " + e.getMessage(), e);
            exibirDialogoErro("Erro ao carregar as receitas.");
        }
    }


    private void abrirFormularioCadastro() {
        Intent intent = new Intent(TelaListagemIngredientes.this, TelaFragment.class);
        intent.putExtra("tela", tela);
        startActivity(intent);
    }

    private void excluirIngrediente(Ingrediente ingredienteSelecionado) {
        try {
            bancoDeDados.excluirIngrediente(ingredienteSelecionado);
            carregarIngredientes();
        } catch (Exception e) {
            Log.e("TelaListagemIngredientes", "Erro ao excluir ingrediente: " + e.getMessage(), e);
            exibirDialogoErro("Erro ao excluir o ingrediente. Tente novamente.");
        }
    }

    private void exibirDialogoOpcoes(Ingrediente ingredienteSelecionado) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaListagemIngredientes.this);
        builder.setTitle("Opções para o ingrediente")
                .setMessage("Deseja alterar ou excluir o ingrediente \"" + ingredienteSelecionado.getNome() + "\"?")
                .setCancelable(false)
                .setPositiveButton("Alterar", (dialog, which) -> abrirFormularioEdicao(ingredienteSelecionado))
                .setNegativeButton("Excluir", (dialog, which) -> {
                    excluirIngrediente(ingredienteSelecionado);
                    voltarParaMenu(); // Redireciona para a tela Menu após a exclusão
                })
                .setNeutralButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void abrirFormularioEdicao(Ingrediente ingredienteSelecionado) {
        Intent intent = new Intent(TelaListagemIngredientes.this, TelaFragment.class);
        intent.putExtra("tela", tela);
        intent.putExtra("ingrediente", ingredienteSelecionado);
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

    private void atualizarIngredientesPorReceita(long idReceita) {
        try {
            List<Ingrediente> ingredientesFiltrados;
            if (idReceita == -1) {
                // Caso nenhuma receita válida seja selecionada, exibe uma lista vazia
                ingredientesFiltrados = new ArrayList<>();
            } else {
                // Busca os ingredientes da receita selecionada
                ingredientesFiltrados = bancoDeDados.listarIngredientesPorReceita(idReceita);
            }

            // Atualiza o adaptador com os ingredientes filtrados
            IngredienteAdapter ingredienteAdapter = new IngredienteAdapter(this, ingredientesFiltrados);
            listViewIngredientes.setAdapter(ingredienteAdapter);
        } catch (Exception e) {
            Log.e("TelaListagemIngredientes", "Erro ao atualizar lista de ingredientes: " + e.getMessage(), e);
            exibirDialogoErro("Erro ao atualizar lista de ingredientes.");
        }
    }

    private void voltarParaMenu() {
        Intent intent = new Intent(TelaListagemIngredientes.this, TelaMenu.class); // Substitua TelaMenu pelo nome da classe do Menu principal
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Limpa o histórico para evitar voltar à tela anterior
        startActivity(intent);
        finish(); // Finaliza a atividade atual
    }
}
