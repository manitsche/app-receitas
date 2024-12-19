package com.manitsche.appreceitas.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.manitsche.appreceitas.R;

public class TelaInicial extends AppCompatActivity {

    Button buttonIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_inicial);

        buttonIniciar = findViewById(R.id.buttonIniciar);

        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelaInicial.this, TelaMenu.class);
                startActivity(intent);
            }
        });
    }
}