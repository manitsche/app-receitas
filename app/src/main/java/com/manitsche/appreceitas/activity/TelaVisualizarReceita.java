package com.manitsche.appreceitas.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.manitsche.appreceitas.R;
import com.manitsche.appreceitas.model.Receita;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class TelaVisualizarReceita extends AppCompatActivity {

    private TextView textViewTitulo;
    private PhotoView photoView;
    private TextView textViewModoPreparo;
    private Button buttonVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_visualizar_receita);

        inicializarComponentes();
        carregarInformacoesReceita();
        configurarBotaoVoltar();
    }

    private void inicializarComponentes() {
        textViewTitulo = findViewById(R.id.textViewTitulo);
        photoView = findViewById(R.id.photoview);
        textViewModoPreparo = findViewById(R.id.textViewModoPreparo);
        buttonVoltar = findViewById(R.id.buttonVoltar);
    }

    private void carregarInformacoesReceita() {
        Intent intent = getIntent();
        Receita receitaSelecionada = (Receita) intent.getSerializableExtra("receita");

        if (receitaSelecionada != null) {
            textViewTitulo.setText(receitaSelecionada.getTitulo());

            // Sempre define a imagem como placeholder
            photoView.setImageResource(R.drawable.placeholder);

            textViewModoPreparo.setText(receitaSelecionada.getModopreparo());
        }
    }

    private void configurarBotaoVoltar() {
        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelaVisualizarReceita.this, TelaMenu.class);
                startActivity(intent);
            }
        });
    }
}