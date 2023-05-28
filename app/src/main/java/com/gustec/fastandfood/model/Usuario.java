package com.gustec.fastandfood.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.gustec.fastandfood.helper.ConfiguracaoFirebase;
import com.gustec.fastandfood.helper.UsuarioFirebase;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    private String idUsuario;
    private String nome;

    private String email;

    private String endereco;
    private String foto;

    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef
                .child("Usuarios")
                .child( getIdUsuario() );
        usuarioRef.setValue(this);

    }


    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
