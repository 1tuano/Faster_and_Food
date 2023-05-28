package com.gustec.fastandfood.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import com.gustec.fastandfood.model.Usuario;


public class UsuarioFirebase {

    public static String getIdUsuario(){

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();

    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static boolean atualizarTipoUsuario(String tipo){

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(tipo)
                    .build();
            user.updateProfile(profile);
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
