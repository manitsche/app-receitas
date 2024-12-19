package com.manitsche.appreceitas.model;

import java.io.Serializable;

public class Ingrediente implements Serializable {

    private long idingrediente;
    private String nome;
    private long idreceita;

    public Ingrediente(long idingrediente, String nome, long idreceita) {
        this.idingrediente = idingrediente;
        this.nome = nome;
        this.idreceita = idreceita;
    }

    public Ingrediente() {}

    public long getIdingrediente() {
        return idingrediente;
    }

    public void setIdingrediente(long idingrediente) {
        this.idingrediente = idingrediente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getIdreceita() {
        return idreceita;
    }

    public void setIdreceita(long idreceita) {
        this.idreceita = idreceita;
    }
}