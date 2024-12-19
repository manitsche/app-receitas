package com.manitsche.appreceitas.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.manitsche.appreceitas.R;

public class TelaMenu extends AppCompatActivity {

    Button buttonIngrediente, buttonReceita;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_menu);

        buttonIngrediente = findViewById(R.id.buttonIngrediente);
        buttonReceita = findViewById(R.id.buttonReceita);

        buttonReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelaMenu.this, TelaListagemReceitas.class);
                startActivity(intent);
            }
        });

        buttonIngrediente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelaMenu.this, TelaListagemIngredientes.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Inicia o MediaPlayer sempre que a tela for chamada
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.windows); // Substitua "sua_midia" pelo nome do arquivo de áudio
        }
        mediaPlayer.start(); // Inicia a reprodução do áudio
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Para a mídia quando a tela for parada ou o app for minimizado
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // Pause a reprodução
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Libera o MediaPlayer quando a tela for destruída
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Libera o recurso do MediaPlayer
            mediaPlayer = null;
        }
    }
}