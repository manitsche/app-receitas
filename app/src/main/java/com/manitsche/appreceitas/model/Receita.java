package com.manitsche.appreceitas.model;

import java.io.Serializable;

public class Receita implements Serializable {

    private long idreceita;
    private String titulo;
    private String imagem;
    private String modopreparo;

    public Receita(long idreceita, String titulo, String imagem, String modopreparo) {
        this.idreceita = idreceita;
        this.titulo = titulo;
        this.imagem = imagem;
        this.modopreparo = modopreparo;
    }

    public Receita() {}

    public long getIdreceita() {
        return idreceita;
    }

    public void setIdreceita(long idreceita) {
        this.idreceita = idreceita;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getModopreparo() {
        return modopreparo;
    }

    public void setModopreparo(String modopreparo) {
        this.modopreparo = modopreparo;
    }

    @Override
    public String toString() {
        return titulo;
    }
}