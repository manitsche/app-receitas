package com.manitsche.appreceitas.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.manitsche.appreceitas.R;
import com.manitsche.appreceitas.activity.TelaMenu;
import com.manitsche.appreceitas.bdhelper.BancoDeDados;
import com.manitsche.appreceitas.model.Receita;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FormularioReceitaFragment extends Fragment {

    private EditText editTitulo, editModoPreparo;
    private Button buttonSalvarAtualizarReceita, buttonSelecionarImagem;
    private ImageView imagem;
    private String caminhoImagemSelecionada;
    private BancoDeDados bancoDeDados;
    private Receita receitaAntiga = null;
    private int corAzul, corVerdeEscuro;
    private static final int REQUEST_CODE_SELECIONAR_IMAGEM = 1;
    private static final String PREFERENCES_NAME = "ReceitasPrefs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_formulario_receita, container, false);

        try {
            inicializarComponentes(view);

            bancoDeDados = new BancoDeDados(getContext());
            corAzul = ContextCompat.getColor(requireContext(), R.color.dark_blue);
            corVerdeEscuro = ContextCompat.getColor(requireContext(), R.color.dark_green);

            carregarDadosDoSharedPreferences();
            configurarFormularioEdicao();

            buttonSelecionarImagem.setOnClickListener(v -> abrirGaleria());
            buttonSalvarAtualizarReceita.setOnClickListener(v -> salvarReceita());
        } catch (Exception e) {
            Log.e("FormularioReceita", "Erro ao inicializar o formulário", e);
        }

        return view;
    }

    private void inicializarComponentes(View view) {
        editTitulo = view.findViewById(R.id.editTitulo);
        editModoPreparo = view.findViewById(R.id.editModoPreparo);
        buttonSalvarAtualizarReceita = view.findViewById(R.id.buttonSalvarAtualizarReceita);
        buttonSelecionarImagem = view.findViewById(R.id.buttonSelecionarImagem);
        imagem = view.findViewById(R.id.imagem);
    }

    private void configurarFormularioEdicao() {
        try {
            if (getArguments() != null) {
                receitaAntiga = (Receita) getArguments().getSerializable("receita");
                if (receitaAntiga != null) {
                    editTitulo.setText(receitaAntiga.getTitulo());
                    editModoPreparo.setText(receitaAntiga.getModopreparo());
                    buttonSalvarAtualizarReceita.setText("Atualizar receita");
                    buttonSalvarAtualizarReceita.setBackgroundColor(corAzul);

                    // Aqui você pode também configurar a imagem, caso a receita tenha imagem associada
                    if (receitaAntiga.getImagem() != null && !receitaAntiga.getImagem().isEmpty()) {
                        carregarImagemEmVisor(Uri.parse(receitaAntiga.getImagem()));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FormularioReceita", "Erro ao configurar formulário de edição", e);
        }
    }

    private void abrirGaleria() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_SELECIONAR_IMAGEM);
        } catch (Exception e) {
            Log.e("FormularioReceita", "Erro ao abrir galeria", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == REQUEST_CODE_SELECIONAR_IMAGEM && resultCode == Activity.RESULT_OK && data != null) {
                Uri imagemSelecionada = data.getData();
                caminhoImagemSelecionada = imagemSelecionada.toString();
                carregarImagemEmVisor(imagemSelecionada);
            }
        } catch (Exception e) {
            Log.e("FormularioReceita", "Erro ao processar resultado da galeria", e);
        }
    }

    private void carregarImagemEmVisor(Uri imagemUri) {
        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imagemUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imagem.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e("FormularioReceita", "Imagem não encontrada", e);
        } catch (Exception e) {
            Log.e("FormularioReceita", "Erro ao carregar imagem", e);
        }
    }

    private void salvarReceita() {
        try {
            String novoTitulo = editTitulo.getText().toString().trim();
            String novoModoPreparo = editModoPreparo.getText().toString().trim();

            if (!validarCampos(novoTitulo, novoModoPreparo)) return;

            Receita receita = receitaAntiga != null
                    ? new Receita(receitaAntiga.getIdreceita(), novoTitulo, caminhoImagemSelecionada, novoModoPreparo)
                    : new Receita(0, novoTitulo, caminhoImagemSelecionada, novoModoPreparo);

            if (receitaAntiga != null) {
                try {
                    bancoDeDados.atualizarReceita(receita);
                    exibirDialogoSucesso("Receita atualizada com sucesso!");
                } catch (Exception e) {
                    Log.e("FormularioReceita", "Erro ao atualizar receita", e);
                    exibirDialogoErro("Erro ao atualizar a receita. Tente novamente.");
                }
            } else {
                Receita novaReceita = bancoDeDados.inserirReceita(receita);
                if (novaReceita != null) {
                    exibirDialogoSucesso("Receita registrada com sucesso!");
                } else {
                    exibirDialogoErro("Erro ao registrar a receita. Tente novamente.");
                }
            }
        } catch (Exception e) {
            Log.e("FormularioReceita", "Erro ao salvar receita", e);
            exibirDialogoErro("Erro ao salvar a receita. Tente novamente.");
        }
    }

    private boolean validarCampos(String titulo, String modoPreparo) {
        if (titulo.isEmpty() || modoPreparo.isEmpty()) {
            exibirDialogoErro("Por favor, preencha todos os campos.");
            return false;
        }
        return true;
    }

    private void exibirDialogoSucesso(String mensagem) {
        try {
            new AlertDialog.Builder(requireContext())
                    .setMessage(mensagem + " Deseja cadastrar mais uma receita?")
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
            Log.e("FormularioReceita", "Erro ao exibir diálogo de sucesso", e);
        }
    }

    private void reiniciarFormulario() {
        try {
            editTitulo.setText("");
            editModoPreparo.setText("");
            receitaAntiga = null;
            buttonSalvarAtualizarReceita.setText("SALVAR RECEITA");
            buttonSalvarAtualizarReceita.setBackgroundColor(corVerdeEscuro);
            imagem.setImageResource(0);
            caminhoImagemSelecionada = null;
        } catch (Exception e) {
            Log.e("FormularioReceita", "Erro ao reiniciar formulário", e);
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
            Log.e("FormularioReceita", "Erro ao exibir diálogo de erro", e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            salvarDadosNoSharedPreferences();
        } catch (Exception e) {
            Log.e("FormularioReceita", "Erro ao salvar dados no SharedPreferences", e);
        }
    }

    private void salvarDadosNoSharedPreferences() {
        try {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("titulo", editTitulo.getText().toString());
            editor.putString("modoPreparo", editModoPreparo.getText().toString());
            editor.putString("caminhoImagem", caminhoImagemSelecionada);
            editor.apply();
        } catch (Exception e) {
            Log.e("FormularioReceita", "Erro ao salvar dados no SharedPreferences", e);
        }
    }

    private void carregarDadosDoSharedPreferences() {
        try {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

            String titulo = sharedPreferences.getString("titulo", "");
            String modoPreparo = sharedPreferences.getString("modoPreparo", "");
            caminhoImagemSelecionada = sharedPreferences.getString("caminhoImagem", "");

            editTitulo.setText(titulo);
            editModoPreparo.setText(modoPreparo);
            if (caminhoImagemSelecionada != null && !caminhoImagemSelecionada.isEmpty()) {
                carregarImagemEmVisor(Uri.parse(caminhoImagemSelecionada));
            }
        } catch (Exception e) {
            Log.e("FormularioReceita", "Erro ao carregar dados do SharedPreferences", e);
        }
    }
}
