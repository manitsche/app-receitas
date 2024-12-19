package com.manitsche.appreceitas.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.manitsche.appreceitas.R;
import com.manitsche.appreceitas.activity.TelaMenu;
import com.manitsche.appreceitas.adapter.ReceitaAdapter;
import com.manitsche.appreceitas.adapter.ReceitaSpinnerAdapter;
import com.manitsche.appreceitas.bdhelper.BancoDeDados;
import com.manitsche.appreceitas.model.Ingrediente;
import com.manitsche.appreceitas.model.Receita;

import java.util.List;

public class FormularioIngredienteFragment extends Fragment {

    private Spinner spinnerReceitas;
    private EditText editNome;
    private Button buttonSalvarAtualizarIngrediente;
    private BancoDeDados bancoDeDados;
    private Ingrediente ingredienteAntigo = null;
    private int corVerdeEscuro;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_formulario_ingrediente, container, false);

        try {
            inicializarComponentes(view);

            bancoDeDados = new BancoDeDados(getContext());
            corVerdeEscuro = ContextCompat.getColor(requireContext(), R.color.dark_green);

            carregarReceitasNoSpinner();
            configurarFormularioEdicao();

            buttonSalvarAtualizarIngrediente.setOnClickListener(v -> salvarIngrediente());
        } catch (Exception e) {
            Log.e("FormularioIngrediente", "Erro ao inicializar o formulário", e);
        }

        return view;
    }

    private void inicializarComponentes(View view) {
        spinnerReceitas = view.findViewById(R.id.spinnerReceitas);
        editNome = view.findViewById(R.id.editNome);
        buttonSalvarAtualizarIngrediente = view.findViewById(R.id.buttonSalvarAtualizarIngrediente);
    }

    private void configurarFormularioEdicao() {
        try {
            if (getArguments() != null) {
                ingredienteAntigo = (Ingrediente) getArguments().getSerializable("ingrediente");
                if (ingredienteAntigo != null) {
                    editNome.setText(ingredienteAntigo.getNome());
                    buttonSalvarAtualizarIngrediente.setText("Atualizar Ingrediente");
                    buttonSalvarAtualizarIngrediente.setBackgroundColor(corVerdeEscuro);

                    // Seleciona a receita associada ao ingrediente no Spinner
                    List<Receita> receitas = bancoDeDados.listarReceitas();
                    for (int i = 0; i < receitas.size(); i++) {
                        if (receitas.get(i).getIdreceita() == ingredienteAntigo.getIdreceita()) {
                            spinnerReceitas.setSelection(i);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FormularioIngrediente", "Erro ao configurar formulário de edição", e);
        }
    }

    private void salvarIngrediente() {
        try {
            // Captura o nome do ingrediente
            String nomeIngrediente = editNome.getText().toString().trim();

            // Captura a receita selecionada no Spinner
            Receita receitaSelecionada = (Receita) spinnerReceitas.getSelectedItem();

            // Valida os campos
            if (!validarCampos(nomeIngrediente, receitaSelecionada)) return;

            // Obtém o título da receita
            String tituloReceita = receitaSelecionada.getTitulo();

            // Obtém o idReceita utilizando o método getIdReceitaPorTitulo
            long idReceita = bancoDeDados.getIdReceitaPorTitulo(tituloReceita);
            Log.i("FormularioIngrediente", "ID da receita selecionada: " + idReceita);

            // Verifica se a receita foi encontrada
            if (idReceita == -1) {
                exibirDialogoErro("Receita não encontrada. Tente novamente.");
                return;
            }

            // Cria o objeto Ingrediente
            Ingrediente ingrediente = new Ingrediente(0, nomeIngrediente, idReceita); // Inicializa com id=0 e associando o idReceita

            if (ingredienteAntigo != null) {
                // Atualização de Ingrediente Existente
                ingrediente.setIdingrediente(ingredienteAntigo.getIdingrediente());
                try {
                    bancoDeDados.atualizarIngrediente(ingrediente, ingredienteAntigo);
                    exibirDialogoSucesso("Ingrediente atualizado com sucesso!");
                } catch (Exception e) {
                    // Log detalhado da exceção e exibe a mensagem ao usuário
                    Log.e("FormularioIngrediente", "Erro ao atualizar ingrediente: " + e.getMessage(), e);
                    exibirDialogoErro("Erro ao atualizar o ingrediente. Detalhes: " + e.getMessage());
                }
            } else {
                // Inserção de Novo Ingrediente
                try {
                    Ingrediente ingredienteInserido = bancoDeDados.inserirIngrediente(ingrediente);

                    if (ingredienteInserido != null) {
                        exibirDialogoSucesso("Ingrediente registrado com sucesso!");
                    } else {
                        Log.e("FormularioIngrediente", "Erro: inserirIngrediente retornou null. Verifique os logs.");
                        exibirDialogoErro("Erro ao registrar o ingrediente. Tente novamente.");
                    }
                } catch (Exception e) {
                    // Log detalhado da exceção e exibe a mensagem ao usuário
                    Log.e("FormularioIngrediente", "Erro ao inserir ingrediente: " + e.getMessage(), e);
                    exibirDialogoErro("Erro ao inserir o ingrediente. Detalhes: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            // Log detalhado da exceção e exibe a mensagem ao usuário
            Log.e("FormularioIngrediente", "Erro ao salvar ingrediente: " + e.getMessage(), e);
            exibirDialogoErro("Erro ao salvar o ingrediente. Tente novamente. Detalhes: " + e.getMessage());
        }
    }

    private boolean validarCampos(String nome, Receita receita) {
        if (nome.isEmpty() || receita == null) {
            exibirDialogoErro("Por favor, preencha todos os campos.");
            return false;
        }
        return true;
    }

    private void exibirDialogoSucesso(String mensagem) {
        try {
            new AlertDialog.Builder(requireContext())
                    .setMessage(mensagem + " Deseja cadastrar mais um ingrediente?")
                    .setTitle("Mensagem")
                    .setPositiveButton("Sim", (dialog, which) -> reiniciarFormulario())
                    .setNegativeButton("Não", (dialog, which) -> {
                        Intent intent = new Intent(requireContext(), TelaMenu.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        // Finalizar a atividade atual para que ela não volte quando pressionar o botão de voltar
                        requireActivity().finish();
                    })
                    .show();
        } catch (Exception e) {
            Log.e("FormularioIngrediente", "Erro ao exibir diálogo de sucesso", e);
        }
    }

    private void reiniciarFormulario() {
        try {
            editNome.setText("");
            spinnerReceitas.setSelection(0);
            ingredienteAntigo = null;
        } catch (Exception e) {
            Log.e("FormularioIngrediente", "Erro ao reiniciar formulário", e);
        }
    }

    private void exibirDialogoErro(String mensagem) {
        try {
            new AlertDialog.Builder(requireContext())
                    .setMessage(mensagem)
                    .setTitle("Erro")
                    .setCancelable(false)
                    .setPositiveButton("OK", null)
                    .show();
        } catch (Exception e) {
            Log.e("FormularioIngrediente", "Erro ao exibir diálogo de erro", e);
        }
    }

    private void carregarReceitasNoSpinner() {
        try {
            List<Receita> receitas = bancoDeDados.listarReceitas();
            ReceitaSpinnerAdapter adapter = new ReceitaSpinnerAdapter(requireContext(), receitas);
            spinnerReceitas.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("FormularioIngrediente", "Erro ao carregar receitas no Spinner", e);
            exibirDialogoErro("Erro ao carregar as receitas.");
        }
    }
}