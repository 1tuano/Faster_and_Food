package com.gustec.fastandfood.model;

import com.google.firebase.database.DatabaseReference;
import com.gustec.fastandfood.helper.ConfiguracaoFirebase;

import java.io.Serializable;

public class Empresa implements Serializable {

    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String email;
    private String tempo;
    private String categoria;
    private Double precoEntrega;

    public Empresa() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("Empresas")
                .child( getIdUsuario() );
        empresaRef.setValue(this);

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecoEntrega() {
        return precoEntrega;
    }

    public void setPrecoEntrega(Double precoEntrega) {
        this.precoEntrega = precoEntrega;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
